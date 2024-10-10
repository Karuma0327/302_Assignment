package com.socslingo.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainFlashcardController {

    @FXML
    private Button switch_to_flashcard_editing_button;

    @FXML
    private Button switch_to_flashcard_creation_button;

    @FXML
    private Button switch_to_flashcard_viewing_button;

    private Map<String, String> buttonToFXMLMap;

    @FXML
    private void initialize() {
        buttonToFXMLMap = new HashMap<>();
        buttonToFXMLMap.put("switch_to_flashcard_editing_button", "/com/socslingo/views/edit_flashcard.fxml");
        buttonToFXMLMap.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");
        buttonToFXMLMap.put("switch_to_flashcard_viewing_button", "/com/socslingo/views/view_flashcard.fxml");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String fxmlPath = buttonToFXMLMap.get(clickedButton.getId());

        if (fxmlPath != null) {
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContent(fxmlPath);
            } else {
                System.out.println("PrimaryController instance is null.");
            }
        } else {
            System.out.println("No FXML mapping found for button ID: " + clickedButton.getId());
        }
    }
}
