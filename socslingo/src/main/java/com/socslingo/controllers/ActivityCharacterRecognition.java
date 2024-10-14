package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;

public class ActivityCharacterRecognition {

    @FXML
    private TextFlow correct_or_incorrect_text;

    @FXML
    private TextFlow if_incorrect_then_reason;

    @FXML
    private Button someButton;


    @FXML
    private ToggleButton toggleButton;

    @FXML
    private Label label;


    @FXML
    private void initialize() {
        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Button is toggled on; ensure label has active styles
                if (!label.getStyleClass().contains("label-active")) {
                    label.getStyleClass().add("label-active");
                }
            } else {
                // Button is toggled off; remove active styles from label
                label.getStyleClass().removeAll("label-active");
            }
        });


        toggleButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // Apply active styles to the label
            if (!label.getStyleClass().contains("label-active")) {
                label.getStyleClass().add("label-active");
            }
        });

        toggleButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            // If button is not selected after release, remove active styles
            if (!toggleButton.isSelected()) {
                label.getStyleClass().removeAll("label-active");
            }
        });


    }


}
