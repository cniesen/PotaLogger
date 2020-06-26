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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Controller implements Initializable {

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

    List<Qso> qsos = new ArrayList<>();
    Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);

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
        myCallsign.setText(userPrefs.get("myCallsign", ""));
        myGrid.setText(userPrefs.get("myGrid", ""));
        myCountry.setText(userPrefs.get("myCountry", ""));
        myState.setText(userPrefs.get("myState", ""));
        myCounty.setText(userPrefs.get("myCounty", ""));
        myIaruRegion.setText(userPrefs.get("myIaruRegion", ""));
        myItu.setText(userPrefs.get("myItu", ""));
        myCq.setText(userPrefs.get("myCq", ""));
    }

    private void addContactOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isAltDown() && !event.isControlDown() && !event.isMetaDown() && !event.isShiftDown()) {
            addContact();
        }
    }

    public void addContact() {
        Qso qso = new Qso(
                formContacts.getItems().size() + 1,
                formDate.getValue(),
                LocalTime.parse(formTimeHH.getText() + ":" + formTimeMM.getText()),
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
        formTimeMM.requestFocus();
    }

    public void saveAdifFile(ActionEvent event) {
        System.out.println("save adif file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save ADIF");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Amateur Data Interchange Format (*.adi)", "*.adi");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) menuBar.getScene().getWindow());
        if (file != null) {
            try {
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsos).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void emailAdifFile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Not yet implemented");
        alert.setHeaderText("Bummer!");
        alert.showAndWait();
    }

    public void udpAdifFile() {
        UnicastSendingMessageHandler handler = new UnicastSendingMessageHandler("localhost", 8899);
        handler.handleMessage(MessageBuilder.withPayload(new Adif().addAllQsos(qsos).toString()).build());
    }

    public void savePreferences() {
        userPrefs.put("myCallsign", myCallsign.getText());
        userPrefs.put("myGrid", myGrid.getText());
        userPrefs.put("myCountry", myCountry.getText());
        userPrefs.put("myState", myState.getText());
        userPrefs.put("myCounty", myCounty.getText());
        userPrefs.put("myIaruRegion", myIaruRegion.getText());
        userPrefs.put("myItu", myItu.getText());
        userPrefs.put("myCq", myCq.getText());
    }

    public void removePreferences() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Preferences");
        alert.setHeaderText("Are you sure you want to remove all saved user preferences, loose entered data, and exit this program?");
        alert.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                userPrefs.clear();
                Platform.exit();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        });
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
