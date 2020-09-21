package com.niesens.potalogger;

import com.niesens.potalogger.enumerations.Mode;
import javafx.scene.control.TextFormatter;

public class CustomTextFormatter {
    /**
     * Upper case
     */
    public static TextFormatter.Change upperCase(TextFormatter.Change change) {
        change.setText(change.getText().toUpperCase());
        return change;
    }

    /**
     * Numbers 1 to 3
     */
    public static TextFormatter.Change numbersTo3(TextFormatter.Change change) {
        return change.getControlNewText().matches("[0-3]?") ? change : null;
    }

    /**
     * Numbers 1 to 40
     */
    public static TextFormatter.Change numbersTo40(TextFormatter.Change change) {
        return change.getControlNewText().matches("[1-9]?|[1-3][0-9]|40") ? change : null;
    }

    /**
     * Numbers 1 to 90
     */
    public static TextFormatter.Change numbersTo90(TextFormatter.Change change) {
        return change.getControlNewText().matches("[1-9]?|[1-8][0-9]|90") ? change : null;
    }

    /**
     * Frequency 1 through 999.999
     */
    public static TextFormatter.Change frequency(TextFormatter.Change change) {
        return change.getControlNewText().matches("[1-9]?|[1-9][0-9]{0,2}|[1-9][0-9]{0,2}\\.|[1-9][0-9]{0,2}\\.[0-9]{0,3}") ? change : null;
    }

    /**
     * A through R for position one and two, 00 through 99 for position three and four
     */
    public static TextFormatter.Change gridCoordinate(TextFormatter.Change change) {
        change.setText(change.getText().toUpperCase());
        return change.getControlNewText().matches("[A-R]{0,2}|[A-R]{2}[0-9]{0,2}") ? change : null;
    }

    /**
     * Hours of time (0 to 23)
     */
    public static TextFormatter.Change timeHH(TextFormatter.Change change) {
        return change.getControlNewText().matches("[0-2]?|[0-1][0-9]|[2][0-3]") ? change : null;
    }

    /**
     * Minutes of time (0 to 59)
     */
    public static TextFormatter.Change timeMM(TextFormatter.Change change) {
        return change.getControlNewText().matches("[0-5]?|[0-5][0-9]") ? change : null;
    }

    /**
     * RST based on mode
     */
    public static TextFormatter.Change rst(TextFormatter.Change change, Mode mode) {
        if (mode == Mode.SSB) {
            return change.getControlNewText().matches("[1-5]?|[1-5][1-9]") ? change : null;
        } else {
            return change.getControlNewText().matches("[1-5]?|[1-5][1-9]|[1-5][1-9][1-9]") ? change : null;
        }
    }
}
