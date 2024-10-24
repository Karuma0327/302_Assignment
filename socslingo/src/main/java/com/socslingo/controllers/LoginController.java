package com.socslingo.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import com.socslingo.managers.SceneManager;
import com.socslingo.services.UserService;
import com.socslingo.models.User;
import com.socslingo.managers.SessionManager;

public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

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
    private Label errorMessageLabel;

    @FXML
    private ImageView errorIcon;

    @FXML
    private javafx.scene.layout.Pane rootPane; // Assuming rootPane is the root container of your login screen

    private boolean canSwitch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    @FXML
    private void initialize() {
        // Add event handler for Enter key press
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
        ChangeListener<javafx.scene.Scene> sceneChangeListener = new ChangeListener<javafx.scene.Scene>() {
            @Override
            public void changed(ObservableValue<? extends javafx.scene.Scene> observable, javafx.scene.Scene oldScene, javafx.scene.Scene newScene) {
                if (newScene != null) {
                    newScene.addEventFilter(KeyEvent.KEY_PRESSED, LoginController.this::handleTabKeyPress);
                }
            }
        };
        usernameField.sceneProperty().addListener(sceneChangeListener);
        passwordField.sceneProperty().addListener(sceneChangeListener);
        cooldown.setOnFinished(event -> canSwitch = true);
        errorMessageLabel.visibleProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorMessageLabel.managedProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorIcon.visibleProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorIcon.managedProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hashedPassword = hashPassword(password);
        User user = userService.validateUser(username, hashedPassword);
        if (user != null) {
            System.out.println("Login successful, User ID: " + user.getId());
            // Set the current user in the SessionManager
            SessionManager.getInstance().setCurrentUser(user);
            // Add fade transition
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), rootPane);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setOnFinished(event -> {
                // Use SceneManager to switch to the main scene
                SceneManager.getInstance().switchScene(
                    "/com/socslingo/views/main.fxml",
                    "/com/socslingo/css/main.css"
                );
                // Fade in the new scene
                FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), rootPane);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            });
            fadeTransition.play();
        } else {
            System.out.println("Invalid username or password. Please try again.");
            errorMessageLabel.setText("Wrong login. Please try again.");
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

    private void handleTabKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB && canSwitch) {
            Scene currentScene = SceneManager.getInstance().getCurrentScene();
            Scene buttonScene = this.loginButton.getScene();
            
            if (currentScene != null && buttonScene != null && currentScene.getRoot().equals(buttonScene.getRoot())) {
                if (usernameField.isFocused()) {
                    passwordField.requestFocus();
                    event.consume(); // Prevent default Tab behavior
                } else {
                    SceneManager.getInstance().switchToRegistration();
                    event.consume(); // Prevent default Tab behavior
                    canSwitch = false;
                    cooldown.playFromStart();
                }
            }
        }
    }

    @FXML
    private void switchToRegistrationFXML() {
        SceneManager.getInstance().switchToRegistration();
    }
}