package com.socslingo.controllers;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PrimaryController {

    @FXML
    private ImageView mascotImageView;

    @FXML
    public void initialize() {
        if (mascotImageView != null) {
            // Create a PauseTransition to keep the mascot still for a bit
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.7));

            // Play the pause transition
            pauseTransition.play();
        }
    }

    @FXML
    private Button leftSidebarButton;

    @FXML
    private void handleLeftSidebarButtonAction() {
        // Add your button action logic here
        System.out.println("Left Sidebar Button Clicked!");
    }

    @FXML
    private Button switchToHomeButton;

    @FXML
    void switchToHomePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/main.fxml"));
        HBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToHomeButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    private Button switchToInitialFlashcardLandingPageButton;

    @FXML
    void switchToInitialFlashcardLandingPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/initialFlashcard.fxml"));
        HBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/flashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToInitialFlashcardLandingPageButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    private Button switchToCreateFlashCardPageButton;

    @FXML
    void switchToCreateFlashCardPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/mainFlashcard.fxml"));
        HBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/flashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToCreateFlashCardPageButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    private Button switchToCreateFlashcardListPageButton;

    @FXML
    void switchToCreateFlashcardListPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/createFlashcardList.fxml"));
        HBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/flashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToCreateFlashcardListPageButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    private Button switchToMainFlashcardPageButton;

    @FXML
    void switchToMainFlashcardPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/mainFlashcard.fxml"));
        HBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/flashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToMainFlashcardPageButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }



    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;

    @FXML
    private Button saveButton2;
    @FXML
    private Button saveButton3;

    @FXML
    private Button retrieveButton3;
    @FXML
    private Button retrieveButton2;

    @FXML
    private void handleSaveButtonAction() {
        int userId = 1; // Assuming a fixed user_id for this example
        String createdDate = "2023-10-01"; // Assuming a fixed created_date for this example

        if (saveButton2.isArmed()) {
            String value = textField2.getText();
            Database.insertData(2, userId, value, "Card Back 2", createdDate);
        } else if (saveButton3.isArmed()) {
            String value = textField3.getText();
            Database.insertData(3, userId, value, "Card Back 3", createdDate);
        }
    }

    @FXML
    private void handleRetrieveButtonAction() {
        if (retrieveButton2.isArmed()) {
            Database.retrieveData(2);
            // Update textField2 with the retrieved data
        } else if (retrieveButton3.isArmed()) {
            Database.retrieveData(3);
            // Update textField3 with the retrieved data
        }
    }
}