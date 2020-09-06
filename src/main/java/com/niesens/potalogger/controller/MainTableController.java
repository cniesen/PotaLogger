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

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalTime;

public class MainTableController {
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

    public void initialize() {
        contactSequence.setCellFactory(indexCellFactory());
        contactDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        contactTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        contactCallsign.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        contactRstSent.setCellValueFactory(new PropertyValueFactory<>("rstSent"));
        contactRstReceived.setCellValueFactory(new PropertyValueFactory<>("rstReceived"));
        contactParkToPark.setCellValueFactory(new PropertyValueFactory<>("parkToPark"));
    }

    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> indexCellFactory() {
        return t -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0) {
                    setText(null);
                } else {
                    setText(Integer.toString(index + 1));
                }
            }
        };
    }

 }
