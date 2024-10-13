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

    private Map<String, String> button_to_fxml_map;

    @FXML
    private void initialize() {
        button_to_fxml_map = new HashMap<>();
        button_to_fxml_map.put("switch_to_flashcard_editing_button", "/com/socslingo/views/edit_flashcard.fxml");
        button_to_fxml_map.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");
        button_to_fxml_map.put("switch_to_flashcard_viewing_button", "/com/socslingo/views/view_flashcard.fxml");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Button clicked_button = (Button) event.getSource();
        String fxml_path = button_to_fxml_map.get(clicked_button.getId());

        if (fxml_path != null) {
            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContent(fxml_path);
            } else {
                System.out.println("PrimaryController instance is null.");
            }
        } else {
            System.out.println("No FXML mapping found for button ID: " + clicked_button.getId());
        }
    }
}