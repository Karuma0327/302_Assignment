package com.socslingo.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.socslingo.App;
import com.socslingo.controllers.database.UserDatabaseController;

public class RegistrationController {

    @FXML
    private Button registerButton;

    @FXML
    private Button switchToLoginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;
    


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
        emailField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
        confirmPasswordField.setOnKeyPressed(this::handleEnterKeyPress);
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
    private void handleRegistration() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }

        // Hash the password
        String hashedPassword = hashPassword(password);

        // Insert user data into the database
        boolean isInserted = UserDatabaseController.insertUser(username, email, hashedPassword);

        // If registration is successful, switch to the login FXML
        if (isInserted) {
            try {
                switchToLoginFXML();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String hashPassword(String password) {
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
            handleRegistration();
        }
    }

    @FXML
    private void switchToLoginFXML() throws IOException {
        App.getInstance().switchToLoginScene();
    }
}