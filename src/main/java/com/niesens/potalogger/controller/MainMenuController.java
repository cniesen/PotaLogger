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

import com.niesens.potalogger.config.BuildInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainMenuController extends MenuBar {

    @FXML
    private MenuItem menuSaveAdifFile;
    @FXML
    private MenuItem menuSavePotaAdifFile;
    @FXML
    private MenuItem menuUdpAdifFile;
    @FXML
    private CheckMenuItem menuSettingsLocalTime;

    private final List<Listener> listeners = new ArrayList<>();

    public interface Listener {
        void onSaveAdifFile();
        void onSavePotaAdifFile();
        void onSendUdpAdifFile();
        void onLocalTimeSettingChange(boolean localTimeSelected);
    }

    public void addListener(Listener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    public void initialize(boolean localTime, boolean disableAdifActions) {
        // Set default values from user preferences
        menuSettingsLocalTime.setSelected(localTime);

        updateMenuActions(disableAdifActions);
    }

    public void updateMenuActions(boolean disableAdifActions) {
        menuSaveAdifFile.setDisable(disableAdifActions);
        menuSavePotaAdifFile.setDisable(disableAdifActions);
        menuUdpAdifFile.setDisable(disableAdifActions);
    }

    public void saveAdifFile() {
        listeners.forEach(Listener::onSaveAdifFile);
    }

    public void savePotaAdifFile() {
        listeners.forEach(Listener::onSavePotaAdifFile);
    }

    public void sendUdpAdifFile() {
        listeners.forEach(Listener::onSendUdpAdifFile);
    }

    public void settingsLocalTime() {
        listeners.forEach(listener -> listener.onLocalTimeSettingChange(menuSettingsLocalTime.isSelected()));
    }

    public void settings() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settings.fxml"));
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
