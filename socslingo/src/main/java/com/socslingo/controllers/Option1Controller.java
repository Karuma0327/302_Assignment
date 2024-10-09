package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;



// Controller for flashcards that displayed in the right sidebar that will be loaded on the deck preview
// that allows the user to drag and drop the flashcard onto the deck, which wil

public class Option1Controller {

    @FXML
    private Label option1Label;

    @FXML
    private void initialize() {
        option1Label.setText("This is dynamic content for Option 1.");
        // Initialize other components or data as needed
    }


    // Add event handlers and other methods as required
}
