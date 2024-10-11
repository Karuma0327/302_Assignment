package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class ActivityMainController {

    @FXML
    private HBox activity_content_area;

    @FXML
    private Button change_to_character_recognition_activity;

    @FXML
    private Button check_button_character_recognition_activity;

    @FXML
    private Button exit_button;

    @FXML
    private Button skip_button;

    @FXML
    private Button report_button;

    // Initialize method to set up event handlers
    @FXML
    public void initialize() {
        // Set action for changing to Character Recognition Activity
        change_to_character_recognition_activity.setOnAction(event -> {
            try {
                loadCharacterRecognitionActivity();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        });

        // Example: Set action for exit button
        exit_button.setOnAction(event -> {
            // Implement exit functionality, e.g., Platform.exit();
            System.exit(0);
        });

        // Similarly, set up other button actions as needed
    }

    /**
     * Loads the Character Recognition Activity into the activity_content_area.
     */
    private void loadCharacterRecognitionActivity() throws IOException {
        // Clear existing content
        activity_content_area.getChildren().clear();

        // Load the Character Recognition FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/activity_character_recognition.fxml"));
        
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found at /com/socslingo/views/activity_character_recognition.fxml");
        }

        Node characterRecognitionActivity = loader.load();

        // Optionally, get the controller of the loaded FXML if you need to interact with it
        // ActivityCharacterRecognitionController controller = loader.getController();

        // Add the loaded content to the activity_content_area
        activity_content_area.getChildren().add(characterRecognitionActivity);
    }
}
