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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SettingsController implements Initializable {
    @FXML
    private TextField settingsHost;
    @FXML
    private TextField settingsPort;

    Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default values from user preferences
        settingsHost.setText(userPrefs.get("settingsHost", "localhost"));
        settingsPort.setText(Integer.toString(userPrefs.getInt("settingsPort", 8899)));
    }

    public void savePreferencesAndCloseSettings() {
        userPrefs.put("settingsHost", settingsHost.getText());
        userPrefs.putInt("settingsPort", Integer.parseInt(settingsPort.getText()));
        closeSettings();
    }

    public void closeSettings() {
        ((Stage)settingsHost.getScene().getWindow()).close();
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

}
