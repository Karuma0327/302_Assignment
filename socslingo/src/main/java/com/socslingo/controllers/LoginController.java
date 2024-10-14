package com.socslingo.controllers;

import java.io.IOException;
import java.security.*;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import com.socslingo.cache.ImageCache;
import com.socslingo.managers.SceneManager;
import com.socslingo.managers.SessionManager;
import com.socslingo.services.UserService;
import com.socslingo.models.User;

public class LoginController {

    private UserService user_service;

    public LoginController(UserService user_service) {
        this.user_service = user_service;
    }

    @FXML
    private Button login_button;

    @FXML
    private Button switch_to_registration_button;

    @FXML
    private TextField username_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button close_button;

    @FXML
    private Label error_message_label;

    @FXML
    private ImageView error_icon;

    @FXML
    private javafx.scene.layout.Pane root_pane;

    private boolean can_switch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    @FXML
    private void initialize() {

        username_field.setOnKeyPressed(this::handleEnterKeyPress);
        password_field.setOnKeyPressed(this::handleEnterKeyPress);

        cooldown.setOnFinished(event -> can_switch = true);

        error_message_label.visibleProperty().bind(error_message_label.textProperty().isNotEmpty());
        error_message_label.managedProperty().bind(error_message_label.textProperty().isNotEmpty());

        error_icon.visibleProperty().bind(error_message_label.textProperty().isNotEmpty());
        error_icon.managedProperty().bind(error_message_label.textProperty().isNotEmpty());
    }

    @FXML
    private void handleLogin() {
        String username = username_field.getText();
        String password = password_field.getText();
        String hashed_password = hashPassword(password);
        User user = user_service.validateUser(username, hashed_password);
        if (user != null) {
            System.out.println("Login successful, User ID: " + user.getId());
            SessionManager.getInstance().setCurrentUser(user);

            // Preload the banner image
            String image_path = user.getProfileBannerPath();
            if (image_path != null && !image_path.isEmpty()) {
                Image cachedImage = ImageCache.getInstance().getBannerImage(user.getId());
                if (cachedImage == null) {
                    System.out.println("Banner image not found in cache for user ID: " + user.getId());
                    // Optionally, handle missing image
                } else {
                    System.out.println("Banner image retrieved from cache for user ID: " + user.getId());
                }
            }

            // Create a fade-out transition for the login screen
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root_pane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                // Switch to the main scene with a fade-in effect
                SceneManager.getInstance().switchScene(
                        "/com/socslingo/views/main.fxml","/com/socslingo/css/main.css",
                        Duration.seconds(1) // Duration for fade-in
                );
                
            });
            
            fadeOut.play();
        } else {
            System.out.println("Invalid username or password. Please try again.");
            error_message_label.setText("Wrong login. Please try again.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest message_digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed_bytes = message_digest.digest(password.getBytes("UTF-8"));
            StringBuilder string_builder = new StringBuilder();
            for (byte b : hashed_bytes) {
                string_builder.append(String.format("%02x", b));
            }
            return string_builder.toString();
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
    private void switchToRegistrationFXML() {
        SceneManager.getInstance().switchToRegistration();
    }

}
