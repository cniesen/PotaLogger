/*
	Claus' POTA Logger
	Copyright (C) 2020  Claus Niesen
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.niesens.potalogger;

import com.niesens.potalogger.enumerations.Mode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import java.awt.*;
import javafx.scene.control.MenuItem;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

public class Controller implements Initializable {
    @FXML
    private MenuItem menuSaveAdifFile;
    @FXML
    private MenuItem menuSavePotaAdifFile;
    @FXML
    private MenuItem menuUdpAdifFile;
    @FXML
    private CheckMenuItem menuSettingsLocalTime;

    @FXML
    private TextField myCallsign;
    @FXML
    private TextField myGrid;
    @FXML
    private TextField myCountry;
    @FXML
    private TextField myState;
    @FXML
    private TextField myCounty;
    @FXML
    private TextField myIaruRegion;
    @FXML
    private TextField myItu;
    @FXML
    private TextField myCq;

    @FXML
    private TextField formActivatedPark;
    @FXML
    private TextField formFrequency;
    @FXML
    private ChoiceBox<Mode> formMode;
    @FXML
    private DatePicker formDate;
    @FXML
    private Label formTimeLabel;
    @FXML
    private TextField formTimeHH;
    @FXML
    private TextField formTimeMM;
    @FXML
    private TextField formCallsign;
    @FXML
    private TextField formRstSent;
    @FXML
    private TextField formRstReceived;
    @FXML
    private TextField formParkToPark;


    @FXML
    private TableColumn<Parameter, Integer> contactSequence;
    @FXML
    private TableColumn<Parameter, LocalDate> contactDate;
    @FXML
    private TableColumn<Parameter, LocalTime> contactTime;
    @FXML
    private TableColumn<Parameter, String> contactCallsign;
    @FXML
    private TableColumn<Parameter, String> contactRstSent;
    @FXML
    private TableColumn<Parameter, String> contactRstReceived;
    @FXML
    private TableColumn<Parameter, String> contactParkToPark;

    @FXML
    private TableView<Qso> formContacts;

    @FXML
    private MenuBar menuBar;

    private List<Qso> qsos = new ArrayList<>();
    private Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myCallsign.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        myGrid.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::gridCoordinate));
        myIaruRegion.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo3));
        myItu.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo90));
        myCq.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo40));

        formActivatedPark.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formFrequency.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::frequency));
        formMode.getItems().addAll(Mode.values());
        formMode.addEventFilter(KeyEvent.KEY_PRESSED, CustomEventFilter::handleUpDown);
        formTimeHH.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::timeHH));
        formTimeMM.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::timeMM));
        formCallsign.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formRstSent.setTextFormatter(new TextFormatter<String>(change -> {
            return CustomTextFormatter.rst(change, formMode.getValue());
        }));
        formRstReceived.setTextFormatter(new TextFormatter<String>(change -> {
            return CustomTextFormatter.rst(change, formMode.getValue());
        }));
        formRstReceived.setOnKeyPressed(this::addContactOnEnter);
        formParkToPark.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formParkToPark.setOnKeyPressed(this::addContactOnEnter);

        contactSequence.setCellValueFactory(new PropertyValueFactory<>("sequence"));
        contactDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        contactTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        contactCallsign.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        contactRstSent.setCellValueFactory(new PropertyValueFactory<>("rstSent"));
        contactRstReceived.setCellValueFactory(new PropertyValueFactory<>("rstReceived"));
        contactParkToPark.setCellValueFactory(new PropertyValueFactory<>("parkToPark"));

        // Set default values from user preferences
        menuSettingsLocalTime.setSelected(userPrefs.getBoolean("settingsLocalTime", false));
        setFormTimeLabel(menuSettingsLocalTime.isSelected());
        myCallsign.setText(userPrefs.get("myCallsign", ""));
        myGrid.setText(userPrefs.get("myGrid", ""));
        myCountry.setText(userPrefs.get("myCountry", ""));
        myState.setText(userPrefs.get("myState", ""));
        myCounty.setText(userPrefs.get("myCounty", ""));
        myIaruRegion.setText(userPrefs.get("myIaruRegion", ""));
        myItu.setText(userPrefs.get("myItu", ""));
        myCq.setText(userPrefs.get("myCq", ""));

        updateMenuActions();
    }

    private void updateMenuActions() {
        menuSaveAdifFile.setDisable(qsos.isEmpty());
        menuSavePotaAdifFile.setDisable(qsos.isEmpty());
        menuUdpAdifFile.setDisable(qsos.isEmpty());
    }

    private void addContactOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isAltDown() && !event.isControlDown() && !event.isMetaDown() && !event.isShiftDown()) {
            addContact();
        }
    }

    public void addContact() {
        LocalDateTime localDateTime = LocalDateTime.parse(formDate.getValue() + "T" + formTimeHH.getText() + ":" + formTimeMM.getText());
        if (userPrefs.getBoolean("settingsLocalTime", false)) {
            localDateTime = localDateTime.atZone(Clock.systemDefaultZone().getZone()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        Qso qso = new Qso(
                formContacts.getItems().size() + 1,
                localDateTime.toLocalDate(),
                localDateTime.toLocalTime(),
                formCallsign.getText(),
                formRstSent.getText(),
                formRstReceived.getText(),
                formParkToPark.getText(),
                formActivatedPark.getText(),
                formFrequency.getText(),
                formMode.getValue().toString(),
                myCallsign.getText(),
                myCountry.getText(),
                myState.getText(),
                myCounty.getText(),
                myGrid.getText(),
                myItu.getText(),
                myCq.getText(),
                myIaruRegion.getText()
        );
        qsos.add(qso);
        formContacts.getItems().add(qso);
        formParkToPark.clear();
        updateMenuActions();
        formTimeMM.requestFocus();
    }

    public void saveAdifFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save ADIF");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Amateur Data Interchange Format (*.adi)", "*.adi");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) menuBar.getScene().getWindow());
        if (file != null) {
            try {
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsos, false).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void savePotaAdifFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog((Stage) menuBar.getScene().getWindow());
        if (selectedDirectory != null) {
            try {
                File file = new File(selectedDirectory.getAbsolutePath() + "/" + myCallsign.getText() + "@"
                        + formActivatedPark.getText() + " " + getQsoFirstDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".adi");
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsos, true).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LocalDate getQsoFirstDate() {
        return qsos.stream().min(Comparator.comparing(Qso::getDate)).orElseThrow(NoSuchElementException::new).getDate();
    }

    public void udpAdifFile() {
        UnicastSendingMessageHandler handler = new UnicastSendingMessageHandler(userPrefs.get("settingsHost",""), userPrefs.getInt("settingsPort",0));
        handler.handleMessage(MessageBuilder.withPayload(new Adif().addAllQsos(qsos, false).toString()).build());
    }

    public void savePreferences() {
        userPrefs.putBoolean("settingsLocalTime", menuSettingsLocalTime.isSelected());
        userPrefs.put("myCallsign", myCallsign.getText());
        userPrefs.put("myGrid", myGrid.getText());
        userPrefs.put("myCountry", myCountry.getText());
        userPrefs.put("myState", myState.getText());
        userPrefs.put("myCounty", myCounty.getText());
        userPrefs.put("myIaruRegion", myIaruRegion.getText());
        userPrefs.put("myItu", myItu.getText());
        userPrefs.put("myCq", myCq.getText());
    }

    public void settingsLocalTime() {
        userPrefs.putBoolean("settingsLocalTime", menuSettingsLocalTime.isSelected());
        setFormTimeLabel(menuSettingsLocalTime.isSelected());
    }

    private void setFormTimeLabel(boolean isLocal) {
        if (isLocal) {
            formTimeLabel.setText("Time (local)");
        } else {
            formTimeLabel.setText("Time (UTC)");
        }
    }

    public void settings() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
        try {
            Parent root = loader.load();
            stage.setTitle("Claus' POTA Logger - Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 500, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void appHomepage() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/cniesen/PotaLogger"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void support() {
        try {
            Desktop.getDesktop().browse(new URI("https://groups.io/g/ClausPotaLogger"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void download() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/cniesen/PotaLogger/releases/latest"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Claus' POTA Logger");
        alert.setHeaderText("Claus' POTA Logger is published under the GPL 3.0 license\nCopyright (c) 2020 by Claus Niesen\n\nVersion: " + BuildInfo.getVersion());
        alert.showAndWait();
    }
}
