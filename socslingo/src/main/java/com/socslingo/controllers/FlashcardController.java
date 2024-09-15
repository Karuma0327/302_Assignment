package com.socslingo.controllers;

import java.io.IOException;
import java.util.List;

import com.socslingo.controllers.database.FlashcardDatabaseController;
import com.socslingo.models.Flashcard;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlashcardController {

    @FXML
    private Button saveFlashcardButton;

    @FXML
    private TextField frontTextField;

    @FXML
    private TextField backTextField;



    @FXML
    private Button switchToHomeFXMLButton;

    @FXML
    private PrimaryController primaryController;



    // !! CURRENT IMPLEMENTATION OF SWITCHING BACK TO ORIGINAL FXML 
    // !! I have no other solutions to solve this problem and will leave it for later

    @FXML
    private HBox windowControls;

    private double xOffset = 0;
    private double yOffset = 0;


    /**
     * 
     */
    @FXML
    public void initialize() {

        windowControls.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        windowControls.setOnMouseDragged(event -> {
            Stage stage = (Stage) windowControls.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) windowControls.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void maximizeWindow() {
        Stage stage = (Stage) windowControls.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) windowControls.getScene().getWindow();
        stage.setIconified(true);
    }




    public FlashcardController() {
        // Initialize primaryController here or ensure it's injected properly
        this.primaryController = new PrimaryController(); // Example initialization
    }

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
        System.out.println("PrimaryController has been set in FlashcardController");
    }
    

    @FXML
    public void handleSaveFlashcardAction() {
        if (primaryController == null) {
            System.out.println("PrimaryController is null in handleSaveFlashcardAction");
            return;
        }
        // Retrieve text from TextFields
        String frontText = frontTextField.getText();
        String backText = backTextField.getText();

        // Assuming user_id is retrieved from somewhere
        int user_id = getUserId();
        String created_date = getCurrentDate();

        // Save flashcard to database
        FlashcardDatabaseController.insertData(user_id, frontText, backText, created_date);

        System.out.println("Save Flashcard Button Clicked!");
    }




    private int getUserId() {
        // Implement logic to retrieve user_id
        return 1; // Placeholder
    }

    private String getCurrentDate() {
        // Implement logic to get the current date
        return "2023-10-01"; // Placeholder
    }


    @FXML
    private Button switchToMainHomeFXMLButton;

    @FXML
    void switchToMainHomeFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/mainHome.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        String css = getClass().getResource("/com/socslingo/css/mainHome.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Get the stage from the button's scene
        Stage stage = (Stage) switchToMainHomeFXMLButton.getScene().getWindow();
        if (stage != null) {
            stage.setScene(scene);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.play();
        } else {
            System.err.println("Stage is null");
        }
    }

    @FXML
    private Button switchToInitialFlashcardLandingPageButton;

    @FXML
    void switchToInitialFlashcardLandingPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/initialFlashcard.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/createFlashCard.css").toExternalForm();
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
    private Button switchToCreateFlashcardFXMLButton;

    @FXML
    void switchToCreateFlashcardFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/createFlashcard.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/createFlashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToCreateFlashcardFXMLButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    private Button switchToMainFlashcardFXMLButton;

    @FXML
    void switchToMainFlashcardFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/mainFlashcard.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, 1600, 900);
        // Add the CSS file to the scene's stylesheets
        String css = getClass().getResource("/com/socslingo/css/mainFlashcard.css").toExternalForm();
        scene.getStylesheets().add(css);
        // Get the current stage from the button's scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToMainFlashcardFXMLButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    @FXML
    public void handleLeftSidebarButtonAction() {
        if (primaryController == null) {
            System.out.println("PrimaryController is null in handleLeftSidebarButtonAction");
            return;
        }
        primaryController.handleLeftSidebarButtonAction();
    }




    @FXML
    private ListView<Flashcard> flashcardListView;
    
    @FXML
    private Label frontLabel;

    @FXML
    private Label backLabel;

    
    @FXML
    private VBox vbox;

    @FXML
    public void handleViewFlashcardsAction() {
        if (primaryController == null) {
            System.out.println("PrimaryController is null in handleViewFlashcardsAction");
            return;
        }
        int userId = getUserId();
        List<Flashcard> flashcards = FlashcardDatabaseController.retrieveAllFlashcards(userId);
        flashcardListView.getItems().clear();
        flashcardListView.getItems().addAll(flashcards);
    }
    
}