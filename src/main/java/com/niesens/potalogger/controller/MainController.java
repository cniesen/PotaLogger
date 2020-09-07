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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.Preferences;

public class MainController implements Initializable, MainMenuController.Listener, MainFormController.Listener {

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

    private final List<Qso> qsos = new ArrayList<>();
    private final Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SortedList<Qso> qsoSortedList = qsoObservableList.sorted(Comparator.comparing(Qso::getDate).thenComparing(Qso::getTime));
        qsoTableView.setItems(qsoSortedList);

        menuBarController.addListener(this);
        formController.addListener(this);

        boolean localTime = userPrefs.getBoolean("settingsLocalTime", false);
        menuBarController.initialize(localTime, qsos.isEmpty());
    }

    @Override
    public boolean onQsoAdd(Qso qso) {
        if (qsos.contains(qso)) {
            return false;
        } else {
            qsos.add(qso);
            qsoObservableList.add(qso);
            menuBarController.updateMenuActions(qsos.isEmpty());
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
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsos, false).toString(), StandardCharsets.UTF_8);
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
                FileUtils.writeStringToFile(file, new Adif().addAllQsos(qsos, true).toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Qso getFirstQso() {
        return qsos.stream().min(Comparator.comparing(Qso::getDate)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void onSendUdpAdifFile() {
        UnicastSendingMessageHandler handler = new UnicastSendingMessageHandler(userPrefs.get("settingsHost",""), userPrefs.getInt("settingsPort",0));
        handler.handleMessage(MessageBuilder.withPayload(new Adif().addAllQsos(qsos, false).toString()).build());
    }

    @Override
    public void onLocalTimeSettingChange(boolean localTimeSelected) {
        userPrefs.putBoolean("settingsLocalTime", localTimeSelected);
        formController.setFormTimeLabel(localTimeSelected);
    }

    public void savePreferences() {
        formController.savePreferences();
    }
}
