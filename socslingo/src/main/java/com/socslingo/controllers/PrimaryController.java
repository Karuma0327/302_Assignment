package com.socslingo.controllers;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryController {

    private Stage stage;

    @FXML
    private Button closeButton;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button minimizeButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private ImageView mascotImageView;

    @FXML
    private HBox windowControls;

    private double xOffset = 0;
    private double yOffset = 0;

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

    @FXML
    private Button leftSidebarButton;

    @FXML
    void handleLeftSidebarButtonAction() {
        // Add your button action logic here
        System.out.println("Left Sidebar Button Clicked!");
    }

    @FXML
    private Button switchToMainHomeFXMLButton;

    @FXML
    void switchToMainHomeFXML() throws IOException {
        switchScene("/com/socslingo/views/mainHome.fxml", "/com/socslingo/css/mainHome.css");
    }

    @FXML
    private Button switchToInitialFlashcardLandingPageButton;

    @FXML
    void switchToInitialFlashcardLandingPage() throws IOException {
        switchScene("/com/socslingo/views/initialFlashcard.fxml", "/com/socslingo/css/createFlashCard.css");
    }

    @FXML
    private Button switchToCreateFlashCardPageButton;

    @FXML
    void switchToCreateFlashCardPage() throws IOException {
        switchScene("/com/socslingo/views/mainFlashcard.fxml", "/com/socslingo/css/mainFlashcard.css");
    }

    @FXML
    private Button switchToCreateFlashcardFXMLButton;

    @FXML
    void switchToCreateFlashcardFXML() throws IOException {
        switchScene("/com/socslingo/views/createFlashcard.fxml", "/com/socslingo/css/createFlashcard.css");
    }

    @FXML
    private Button switchToMainFlashcardFXMLButton;

    @FXML
    void switchToMainFlashcardFXML() throws IOException {
        switchScene("/com/socslingo/views/mainFlashcard.fxml", "/com/socslingo/css/mainFlashcard.css");
    }

    @FXML
    private Button switchToLoginFXMLButton;

    @FXML
    void switchToLoginFXML() throws IOException {
        switchScene("/com/socslingo/views/login.fxml", "/com/socslingo/css/login.css");
    }

    @FXML
    private Button switchToRegistrationPageButton;

    @FXML
    void switchToRegistrationPage() throws IOException {
        switchScene("/com/socslingo/views/registration.fxml", "/com/socslingo/css/registration.css");
    }

    @FXML
    private Button switchToRegistrationFXMLButton;

    @FXML
    void switchToRegistrationFXML() throws IOException {
        switchScene("/com/socslingo/views/registration.fxml", "/com/socslingo/css/registration.css");
    }

    @FXML
    private Button switchToViewFlashcardFXMLButton;

    @FXML
    void switchToViewFlashcardFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/viewFlashcard.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, getScreenWidth() * 0.95, getScreenHeight() * 0.95);
        String css = getClass().getResource("/com/socslingo/css/viewFlashcard.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Get the controller and set the PrimaryController instance
        FlashcardController flashcardController = loader.getController();
        flashcardController.setPrimaryController(this);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        Stage stage = (Stage) switchToViewFlashcardFXMLButton.getScene().getWindow();
        stage.setScene(scene);
        fadeOut.play();
    }

    private void switchScene(String fxmlPath, String cssPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        VBox root = loader.load();
        Scene scene = new Scene(root, getScreenWidth() * 0.95, getScreenHeight() * 0.95);
        String css = getClass().getResource(cssPath).toExternalForm();
        scene.getStylesheets().add(css);

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

    private double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    private double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
}