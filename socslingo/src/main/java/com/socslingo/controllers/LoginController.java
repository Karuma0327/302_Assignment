package com.socslingo.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import com.socslingo.App;
import com.socslingo.controllers.database.UserDatabaseController;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button switchToRegistrationButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button closeButton;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private HBox windowControls;

    private double xOffset = 0;
    private double yOffset = 0;



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
    private void initialize() {
        // Add event handler for Enter key press
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
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
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Hash the password
        String hashedPassword = hashPassword(password);
        // Validate user credentials
        boolean isValidUser = UserDatabaseController.validateUser(username, hashedPassword);

        // If login is successful, switch to the main application FXML
        if (isValidUser) {
            try {
                switchToMainAppFXML();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void switchToMainAppFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/mainHome.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root, getScreenWidth() * 0.95, getScreenHeight() * 0.95);
        String css = getClass().getResource("/com/socslingo/css/mainHome.css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.play();
    }

    private double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    private double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
    @FXML
    private void switchToRegistrationFXML() throws IOException {
        App.getInstance().switchToRegistrationScene();
    }
}