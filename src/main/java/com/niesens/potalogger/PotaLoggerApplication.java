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

import com.niesens.potalogger.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PotaLoggerApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("Claus' POTA Logger");
		primaryStage.setOnCloseRequest(evt -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirm Exit");
			alert.setHeaderText("Are you sure you want to close the program?\n\n If you did not save the logs beforehand you will loose the data.");
			ButtonType responseButton = alert.showAndWait().orElse(ButtonType.CANCEL);
			if (responseButton == ButtonType.OK) {
				MainController controller = loader.getController();
				controller.savePreferences();
			} else {
				evt.consume();
			}
		});
		primaryStage.setScene(new Scene(root, 800, 500));
		primaryStage.getScene().getStylesheets().add("stylesheet.css");
		primaryStage.show();
	}
}
