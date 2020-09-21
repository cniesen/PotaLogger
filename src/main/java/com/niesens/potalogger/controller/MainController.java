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

package com.niesens.potalogger.controller;

import com.niesens.potalogger.Adif;
import com.niesens.potalogger.PotaLoggerApplication;
import com.niesens.potalogger.Qso;
import com.niesens.potalogger.UdpSendTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainController implements Initializable, MainMenuController.Listener, MainFormController.Listener, EditQsoController.Listener {

    @FXML
    private MenuBar menuBar;
    @FXML
    private MainMenuController menuBarController;

    @FXML
    private VBox form;
    @FXML
    private MainFormController formController;

    @FXML
    private TableView<Qso> qsoTableView;
    private final ObservableList<Qso> qsoObservableList = FXCollections.observableArrayList();

    @FXML
    private MainTableController formContactsController;

    private final Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SortedList<Qso> qsoSortedList = qsoObservableList.sorted(Comparator.comparing(Qso::getDate).thenComparing(Qso::getTime));
        qsoTableView.setItems(qsoSortedList);

        qsoTableView.setOnMouseClicked( event -> { if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-qso.fxml"));
            try {
                Parent root = loader.load();
                stage.setTitle("Claus' POTA Logger - Update QSO");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root, 700, 300));
                stage.getScene().getStylesheets().add("stylesheet.css");
                EditQsoController controller = loader.getController();
                controller.initData(qsoTableView.getSelectionModel().getSelectedIndex(), qsoTableView.getSelectionModel().getSelectedItem(), this);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }});

        qsoTableView.setOnKeyReleased( event -> { if (event.getCode() == KeyCode.ENTER) {
            System.out.println(qsoTableView.getSelectionModel().getSelectedItem());
        }});
        menuBarController.addListener(this);
        formController.addListener(this);

        boolean localTime = userPrefs.getBoolean("settingsLocalTime", false);
        menuBarController.initialize(localTime, qsoObservableList.isEmpty());
    }

    @Override
    public boolean onQsoAdd(Qso qso) {
        // scroll to bottom of QSO list
        qsoTableView.scrollTo(qsoTableView.getItems().size() -1);

        if (qsoObservableList.contains(qso)) {
            return false;
        } else {
            qsoObservableList.add(qso);
            menuBarController.updateMenuActions(qsoObservableList.isEmpty());
            return true;
        }
    }

    @Override
    public void onSaveAdifFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save ADIF");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Amateur Data Interchange Format (*.adi)", "*.adi");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try {
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsoObservableList, false).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSavePotaAdifFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(menuBar.getScene().getWindow());
        Qso firstQso = getFirstQso();
        if (selectedDirectory != null) {
            try {
                File file = new File(selectedDirectory.getAbsolutePath() + "/" + firstQso.getMyCallsign() + "@"
                        + firstQso.getActivatedPark() + " " + firstQso.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".adi");
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsoObservableList, true).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Qso getFirstQso() {
        return qsoObservableList.stream().min(Comparator.comparing(Qso::getDate)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void onSendUdpAdifFile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Claus' POTA Logger");
        alert.setHeaderText("Sending data");
        alert.show();

        Adif adif = new Adif().addAllQsos(qsoObservableList, false);
        String host = userPrefs.get("settingsHost","<unspecified>");
        int port = userPrefs.getInt("settingsPort",0);
        UdpSendTask udpSendTask = new UdpSendTask(adif.toString(), host, port);
        udpSendTask.setOnFailed(event -> {
            alert.setResult(ButtonType.OK);
            alert.close();
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("About Claus' POTA Logger");
            alert2.setHeaderText("ADIF send via UDP to host \"" + host + "\" on port \"" + port + "\" failed.");
            alert2.setContentText(event.getSource().getException().getMessage());
            alert2.showAndWait();
        });
        new Thread(udpSendTask).start();
    }

    @Override
    public void onLocalTimeSettingChange(boolean localTimeSelected) {
        userPrefs.putBoolean("settingsLocalTime", localTimeSelected);
        formController.setFormTimeLabel(localTimeSelected);
    }

    @Override
    public void onDelete(int index) {
        qsoObservableList.remove(index);
    }

    @Override
    public void onUpdate(int index, Qso qso) {
        qsoObservableList.set(index, qso);
    }

    public void savePreferences() {
        formController.savePreferences();
    }
}
