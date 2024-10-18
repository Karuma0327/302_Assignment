package com.socslingo.controllers;

import java.io.IOException;
import java.security.*;
import java.util.regex.Pattern;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import com.socslingo.managers.SceneManager;
import com.socslingo.services.UserService;


public class RegistrationController {
    private UserService user_service;

    public RegistrationController(UserService user_service) {
        this.user_service = user_service;
    }

    @SuppressWarnings("unused")
    private boolean is_animating = false;

    @FXML
    private Button register_button;

    @FXML
    private Button switch_to_login_button;

    @FXML
    private TextField username_field;

    @FXML
    private TextField email_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private PasswordField confirm_password_field;

    @FXML
    private Label username_error_label;

    @FXML
    private ImageView username_error_icon;

    @FXML
    private Label email_error_label;

    
    @FXML
    private ImageView email_error_icon;

    @FXML
    private Label password_error_label;

    @FXML
    private ImageView password_error_icon;

    @FXML
    private Label confirm_password_error_label;

    @FXML
    private ImageView confirm_password_error_icon;


    @SuppressWarnings("unused")
    private boolean can_switch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    @FXML
    private void initialize() {
        username_field.setOnKeyPressed(this::handleEnterKeyPress);
        email_field.setOnKeyPressed(this::handleEnterKeyPress);
        password_field.setOnKeyPressed(this::handleEnterKeyPress);
        confirm_password_field.setOnKeyPressed(this::handleEnterKeyPress);

        cooldown.setOnFinished(event -> can_switch = true);

        bindErrorLabel(username_error_label, username_error_icon);
        bindErrorLabel(email_error_label, email_error_icon);
        bindErrorLabel(password_error_label, password_error_icon);
        bindErrorLabel(confirm_password_error_label, confirm_password_error_icon);
    }

    private void bindErrorLabel(Label error_label, ImageView error_icon) {
        error_label.visibleProperty().bind(error_label.textProperty().isNotEmpty());
        error_label.managedProperty().bind(error_label.textProperty().isNotEmpty());
        error_icon.visibleProperty().bind(error_label.textProperty().isNotEmpty());
        error_icon.managedProperty().bind(error_label.textProperty().isNotEmpty());
    }

    @FXML
    private void handleRegistration() {
        String username = username_field.getText().trim();
        String email = email_field.getText().trim();
        String password = password_field.getText();
        String confirm_password = confirm_password_field.getText();

        clearErrors();

        boolean has_error = false;

        String username_validation_result = validateUsername(username);
        if (!username_validation_result.isEmpty()) {
            username_error_label.setText(username_validation_result);
            username_field.getStyleClass().add("invalid-field");
            has_error = true;
        }

        String email_validation_result = validateEmail(email);
        if (!email_validation_result.isEmpty()) {
            email_error_label.setText(email_validation_result);
            email_field.getStyleClass().add("invalid-field");
            has_error = true;
        }

        String password_validation_result = validatePassword(password);
        if (!password_validation_result.isEmpty()) {
            password_error_label.setText(password_validation_result);
            password_field.getStyleClass().add("invalid-field");
            has_error = true;
        }

        String confirm_password_validation_result = validateConfirmPassword(password, confirm_password);
        if (!confirm_password_validation_result.isEmpty()) {
            confirm_password_error_label.setText(confirm_password_validation_result);
            confirm_password_field.getStyleClass().add("invalid-field");
            has_error = true;
        }

        if (has_error) {
            return;
        }

        String hashed_password = hashPassword(password);
        boolean is_registered = user_service.registerUser(username, email, hashed_password);

        if (is_registered) {
            is_animating = true;
            animateDotsAndSwitchScene();
        } else {
            username_error_label.setText("Registration failed. Username may already exist.");
            username_field.getStyleClass().add("invalid-field");
        }
    }

    private void clearErrors() {
        username_error_label.setText("");
        email_error_label.setText("");
        password_error_label.setText("");
        confirm_password_error_label.setText("");

        username_field.getStyleClass().remove("invalid-field");
        email_field.getStyleClass().remove("invalid-field");
        password_field.getStyleClass().remove("invalid-field");
        confirm_password_field.getStyleClass().remove("invalid-field");
    }

    private String validateUsername(String username) {
        if (username.isEmpty()) {
            return "Username is required.";
        }
        if (username.contains(" ")) {
            return "Username cannot contain spaces.";
        }
        if (username.toLowerCase().contains("hyper")) {
            return "Username cannot contain 'hyper'.";
        }
        return "";
    }

    private String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email is required.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email address.";
        }
        return "";
    }

    private String validatePassword(String password) {
        if (password.isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (password.contains(" ")) {
            return "Password cannot contain spaces.";
        }
        return "";
    }

    private String validateConfirmPassword(String password, String confirm_password) {
        if (confirm_password.isEmpty()) {
            return "Confirm Password is required.";
        }
        if (!password.equals(confirm_password)) {
            return "Passwords do not match.";
        }
        return "";
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest message_digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed_bytes = message_digest.digest(password.getBytes("UTF-8"));
            StringBuilder string_builder = new StringBuilder();
            for (byte b : hashed_bytes) {
                string_builder.append(String.format("%02x", b));
            }
            return string_builder.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleRegistration();
        }
    }



    @FXML
    private void switchToLoginFXML() {
        SceneManager.getInstance().switchToLogin();
    }

    private boolean isValidEmail(String email) {
        String email_regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(email_regex);
        return pattern.matcher(email).matches();
    }

    private void animateDotsAndSwitchScene() {
        register_button.setText("");

        Circle dot_1 = new Circle(5, Color.rgb(128, 128, 128, 0.15));
        Circle dot_2 = new Circle(5, Color.rgb(128, 128, 128, 0.15));
        Circle dot_3 = new Circle(5, Color.rgb(128, 128, 128, 0.15));

        HBox dots_box = new HBox(5);
        dots_box.setAlignment(Pos.CENTER);
        dots_box.getChildren().addAll(dot_1, dot_2, dot_3);

        register_button.setGraphic(dots_box);


        Color grey_low_opacity = Color.rgb(128, 128, 128, 0.15);
        Color grey_medium_opacity = Color.rgb(128, 128, 128, 0.35);
        @SuppressWarnings("unused")
        Color grey_high_opacity = Color.rgb(128, 128, 128, 0.55);

        int cycle_count = 2;

        Duration duration_per_cycle = Duration.seconds(0.6);

        Timeline timeline = new Timeline();

        for (int cycle = 0; cycle < cycle_count; cycle++) {
            double time_offset = cycle * duration_per_cycle.toSeconds();

            KeyFrame keyframe_1 = new KeyFrame(Duration.seconds(time_offset + 0.1),
                new KeyValue(dot_1.fillProperty(), grey_medium_opacity));

            KeyFrame keyframe_1_reset = new KeyFrame(Duration.seconds(time_offset + 0.3),
                new KeyValue(dot_1.fillProperty(), grey_low_opacity));

            KeyFrame keyframe_2 = new KeyFrame(Duration.seconds(time_offset + 0.3),
                new KeyValue(dot_2.fillProperty(), grey_medium_opacity));

            KeyFrame keyframe_2_reset = new KeyFrame(Duration.seconds(time_offset + 0.5),
                new KeyValue(dot_2.fillProperty(), grey_low_opacity));

            KeyFrame keyframe_3 = new KeyFrame(Duration.seconds(time_offset + 0.5),
                new KeyValue(dot_3.fillProperty(), grey_medium_opacity));

            KeyFrame keyframe_3_reset = new KeyFrame(Duration.seconds(time_offset + 0.7),
                new KeyValue(dot_3.fillProperty(), grey_low_opacity));

            timeline.getKeyFrames().addAll(keyframe_1, keyframe_1_reset, keyframe_2, keyframe_2_reset, keyframe_3, keyframe_3_reset);
        }

        timeline.setOnFinished(event -> {
            username_field.clear();
            email_field.clear();
            password_field.clear();
            confirm_password_field.clear();

            register_button.setText("CREATE ACCOUNT");
            register_button.setGraphic(null);

            SceneManager.getInstance().switchToLogin();

            is_animating = false;
        });

        timeline.play();
    }

}
