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

import com.niesens.potalogger.CustomEventFilter;
import com.niesens.potalogger.CustomTextFormatter;
import com.niesens.potalogger.PotaLoggerApplication;
import com.niesens.potalogger.Qso;
import com.niesens.potalogger.enumerations.Mode;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

public class MainFormController extends VBox {

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
    private Label formMessage;

    private final Preferences userPrefs = Preferences.userNodeForPackage(PotaLoggerApplication.class);
    private final List<Listener> listeners = new ArrayList<>();

    private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    public interface Listener {
        /**
         * Add QSO
         *
         * @param qso the QSO to be added
         * @return true on successful app, false on duplicate qso
         */
        boolean onQsoAdd(Qso qso);
    }

    public void addListener(Listener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    public void initialize() {
        this.myCallsign.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        this.myGrid.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::gridCoordinate));
        this.myIaruRegion.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo3));
        this.myItu.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo90));
        this.myCq.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::numbersTo40));

        formActivatedPark.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formFrequency.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::frequency));
        formMode.getItems().setAll(Mode.values());
        formMode.addEventFilter(KeyEvent.KEY_PRESSED, CustomEventFilter::handleUpDown);
        formTimeHH.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::timeHH));
        formTimeMM.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::timeMM));
        formCallsign.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formRstSent.setTextFormatter(new TextFormatter<String>(change -> CustomTextFormatter.rst(change, formMode.getValue())));
        formRstReceived.setTextFormatter(new TextFormatter<String>(change -> CustomTextFormatter.rst(change, formMode.getValue())));
        formRstReceived.setOnKeyPressed(this::addContactOnEnter);
        formParkToPark.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
        formParkToPark.setOnKeyPressed(this::addContactOnEnter);

        // Set default values from user preferences
        setFormTimeLabel(userPrefs.getBoolean("settingsLocalTime", false));
        myCallsign.setText(userPrefs.get("myCallsign", ""));
        myGrid.setText(userPrefs.get("myGrid", ""));
        myCountry.setText(userPrefs.get("myCountry", ""));
        myState.setText(userPrefs.get("myState", ""));
        myCounty.setText(userPrefs.get("myCounty", ""));
        myIaruRegion.setText(userPrefs.get("myIaruRegion", ""));
        myItu.setText(userPrefs.get("myItu", ""));
        myCq.setText(userPrefs.get("myCq", ""));
    }

    public void setFormTimeLabel(boolean isLocal) {
        if (isLocal) {
            formTimeLabel.setText("Time (local)");
        } else {
            formTimeLabel.setText("Time (UTC)");
        }
    }

    private void addContactOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isAltDown() && !event.isControlDown() && !event.isMetaDown() && !event.isShiftDown()) {
            addContact();
        }
    }
    
    public void addContact() {
        boolean valid = validateTextFieldNotBlank(formCallsign);
        valid &= validateTextFieldNotBlank(formRstSent);
        valid &= validateTextFieldNotBlank(formRstReceived);
        valid &= validateTextFieldNotBlank(formParkToPark);
        valid &= validateTextFieldNotBlank(formActivatedPark);
        valid &= validateTextFieldNotBlank(formFrequency);
        valid &= validateChoiceBoxNotNull(formMode);
        valid &= validateTextFieldNotBlank(myCallsign);
        valid &= validateTextFieldNotBlank(myCountry);
        valid &= validateTextFieldNotBlank(myState);
        valid &= validateTextFieldNotBlank(myCounty);
        valid &= validateTextFieldNotBlank(myGrid);
        valid &= validateTextFieldNotBlank(myItu);
        valid &= validateTextFieldNotBlank(myCq);
        valid &= validateTextFieldNotBlank(myIaruRegion);
        LocalDateTime localDateTime = validateDateTime(formDate, formTimeHH, formTimeMM);
        if (localDateTime == null) {
            valid = false;
        }

        if (!valid) {
            formMessage.setText("Invalid data!");
            return;
        }

        Qso qso = new Qso(
 1 ,//                formContacts.getItems().size() + 1,
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
        AtomicBoolean duplicateQso = new AtomicBoolean(false);
        listeners.forEach(listener -> { if (!listener.onQsoAdd(qso)) duplicateQso.set(true); });
        if (duplicateQso.get()) {
            formMessage.setText("Duplicate QSO!");
        } else {
            formMessage.setText("");
            formParkToPark.clear();
            formTimeMM.requestFocus();
        }
    }

    private static boolean validateTextFieldNotBlank(TextInputControl textInputControl) {
        if (textInputControl.getText().isBlank()) {
            textInputControl.pseudoClassStateChanged(errorClass, true);
            return false;
        } else {
            textInputControl.pseudoClassStateChanged(errorClass, false);
            return true;
        }
    }

    private static boolean validateTextFieldLength(TextInputControl textInputControl, int length) {
        if (textInputControl.getText().length() == length) {
            textInputControl.pseudoClassStateChanged(errorClass, false);
            return true;
        } else {
            textInputControl.pseudoClassStateChanged(errorClass, true);
            return false;
        }
    }

    private static boolean validateChoiceBoxNotNull(ChoiceBox choiceBox) {
        if (choiceBox.getValue() == null) {
            choiceBox.pseudoClassStateChanged(errorClass, true);
            return false;
        } else {
            choiceBox.pseudoClassStateChanged(errorClass, false);
            return true;
        }
    }

     private static boolean validateDatePicker(DatePicker datepicker) {
        if (datepicker.getEditor().getText().isBlank()) {
            datepicker.pseudoClassStateChanged(errorClass, true);
            return false;
        }
        try {
            datepicker.setValue(datepicker.getConverter().fromString(datepicker.getEditor().getText()));
            datepicker.pseudoClassStateChanged(errorClass, false);
            return true;
        } catch (DateTimeParseException e) {
            datepicker.pseudoClassStateChanged(errorClass, true);
            return false;
        }
    }

    private LocalDateTime validateDateTime(DatePicker formDate, TextField formTimeHH, TextField formTimeMM) {
        boolean valid = true;
        valid &= validateDatePicker(formDate);
        valid &= validateTextFieldLength(formTimeHH, 2);
        valid &= validateTextFieldLength(formTimeMM, 2);
        if (!valid) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(formDate.getValue() + "T" + formTimeHH.getText() + ":" + formTimeMM.getText());
            if (userPrefs.getBoolean("settingsLocalTime", false)) {
                localDateTime = localDateTime.atZone(Clock.systemDefaultZone().getZone()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            }
            return localDateTime;
        } catch (DateTimeParseException e) {
            return null;
        }
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
}
