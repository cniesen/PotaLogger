package com.niesens.potalogger.controller;

import com.niesens.potalogger.CustomEventFilter;
import com.niesens.potalogger.CustomTextFormatter;
import com.niesens.potalogger.Qso;
import com.niesens.potalogger.enumerations.Mode;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;


public class EditQsoController implements Initializable {
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

    private int qsoIndex;
    private Listener callbackListener;

    private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    public interface Listener {
        void onDelete(int index);
        void onUpdate(int index, Qso qso);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        formParkToPark.setTextFormatter(new TextFormatter<String>(CustomTextFormatter::upperCase));
    }

    public void initData(int index, Qso qso, Listener listener) {
        qsoIndex = index;
        callbackListener = listener;
        myCallsign.setText(qso.getMyCallsign());
        myGrid.setText(qso.getMyGrid());
        myCountry.setText(qso.getMyCountry());
        myState.setText(qso.getMyState());
        myCounty.setText(qso.getMyCounty());
        myIaruRegion.setText(qso.getMyIaruRegion());
        myItu.setText(qso.getMyItu());
        myCq.setText(qso.getMyCq());

        formActivatedPark.setText(qso.getActivatedPark());
        formFrequency.setText(qso.getFrequency());
        formMode.setValue(qso.getMode());
        formDate.getEditor().setText(formDate.getConverter().toString(qso.getDate()));
        formTimeLabel.setText("Time (UTC)");
        formTimeHH.setText(qso.getTime().format(DateTimeFormatter.ofPattern("kk")));
        formTimeMM.setText(qso.getTime().format(DateTimeFormatter.ofPattern("mm")));
        formCallsign.setText(qso.getCallsign());
        formRstSent.setText(qso.getRstSent());
        formRstReceived.setText(qso.getRstReceived());
        formParkToPark.setText(qso.getParkToPark());
    }


    public void updateContact(ActionEvent actionEvent) {
        boolean valid = validateTextFieldNotBlank(formCallsign);
        valid &= validateTextFieldNotBlank(formRstSent);
        valid &= validateTextFieldNotBlank(formRstReceived);
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
                localDateTime.toLocalDate(),
                localDateTime.toLocalTime(),
                formCallsign.getText(),
                formRstSent.getText(),
                formRstReceived.getText(),
                formParkToPark.getText(),
                formActivatedPark.getText(),
                formFrequency.getText(),
                formMode.getValue(),
                myCallsign.getText(),
                myCountry.getText(),
                myState.getText(),
                myCounty.getText(),
                myGrid.getText(),
                myItu.getText(),
                myCq.getText(),
                myIaruRegion.getText()
        );
        callbackListener.onUpdate(qsoIndex, qso);
        closeWindow();
    }

    public void deleteContact(ActionEvent actionEvent) {
        callbackListener.onDelete(qsoIndex);
        closeWindow();
    }

    public void closeWindow() {
        ((Stage)myCallsign.getScene().getWindow()).close();
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
            return LocalDateTime.parse(formDate.getValue() + "T" + formTimeHH.getText() + ":" + formTimeMM.getText());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
