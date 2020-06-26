package com.niesens.potalogger;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomEventFilter {

    /**
     * prevent move focus on pressing UP/DOWN with ChoiceBox
     */
    public static void handleUpDown(KeyEvent event) {
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            event.consume();
        }
    }

}
