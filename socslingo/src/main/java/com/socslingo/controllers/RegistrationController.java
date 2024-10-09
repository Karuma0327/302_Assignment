package com.socslingo.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import com.socslingo.managers.SceneManager;
import com.socslingo.services.UserService;



public class RegistrationController {
    private UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // Flag to prevent multiple registrations during animation
    private boolean isAnimating = false;

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
    private Label usernameErrorLabel;

    @FXML
    private ImageView usernameErrorIcon;

    @FXML
    private Label emailErrorLabel;

    
    @FXML
    private ImageView emailErrorIcon;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private ImageView passwordErrorIcon;

    @FXML
    private Label confirmPasswordErrorLabel;

    @FXML
    private ImageView confirmPasswordErrorIcon;

    private boolean isProcessing = false;

    private boolean canSwitch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    @FXML
    private void initialize() {
        // Add event handler for Enter key press
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);
        emailField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
        confirmPasswordField.setOnKeyPressed(this::handleEnterKeyPress);

        // Add scene change listeners to handle Tab key press
        ChangeListener<javafx.scene.Scene> sceneChangeListener = new ChangeListener<javafx.scene.Scene>() {
            @Override
            public void changed(ObservableValue<? extends javafx.scene.Scene> observable, javafx.scene.Scene oldScene, javafx.scene.Scene newScene) {
                if (newScene != null) {
                    newScene.addEventFilter(KeyEvent.KEY_PRESSED, RegistrationController.this::handleTabKeyPress);
                }
            }
        };
        usernameField.sceneProperty().addListener(sceneChangeListener);
        emailField.sceneProperty().addListener(sceneChangeListener);
        passwordField.sceneProperty().addListener(sceneChangeListener);
        confirmPasswordField.sceneProperty().addListener(sceneChangeListener);

        cooldown.setOnFinished(event -> canSwitch = true);

        // Bind the visibility and managed properties of the error labels to their text properties
        bindErrorLabel(usernameErrorLabel, usernameErrorIcon);
        bindErrorLabel(emailErrorLabel, emailErrorIcon);
        bindErrorLabel(passwordErrorLabel, passwordErrorIcon);
        bindErrorLabel(confirmPasswordErrorLabel, confirmPasswordErrorIcon);
    }

    /**
     * Binds the visibility and managed properties of an error label and its icon.
     *
     * @param errorLabel The Label displaying the error message.
     * @param errorIcon  The ImageView displaying the error icon.
     */
    private void bindErrorLabel(Label errorLabel, ImageView errorIcon) {
        errorLabel.visibleProperty().bind(errorLabel.textProperty().isNotEmpty());
        errorLabel.managedProperty().bind(errorLabel.textProperty().isNotEmpty());
        errorIcon.visibleProperty().bind(errorLabel.textProperty().isNotEmpty());
        errorIcon.managedProperty().bind(errorLabel.textProperty().isNotEmpty());
    }

    /**
     * Handles the registration process when the Register button is clicked or Enter key is pressed.
     */
    @FXML
    private void handleRegistration() {
        String username = usernameField.getText().trim(); // Trim to remove leading/trailing spaces
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Clear all error messages and remove invalid styles
        clearErrors();

        boolean hasError = false;

        // Validate Username
        String usernameValidationResult = validateUsername(username);
        if (!usernameValidationResult.isEmpty()) {
            usernameErrorLabel.setText(usernameValidationResult);
            usernameField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate Email
        String emailValidationResult = validateEmail(email);
        if (!emailValidationResult.isEmpty()) {
            emailErrorLabel.setText(emailValidationResult);
            emailField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate Password
        String passwordValidationResult = validatePassword(password);
        if (!passwordValidationResult.isEmpty()) {
            passwordErrorLabel.setText(passwordValidationResult);
            passwordField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate Confirm Password
        String confirmPasswordValidationResult = validateConfirmPassword(password, confirmPassword);
        if (!confirmPasswordValidationResult.isEmpty()) {
            confirmPasswordErrorLabel.setText(confirmPasswordValidationResult);
            confirmPasswordField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        if (hasError) {
            isProcessing = false; // Reset the flag if there's an error
            return; // Stop registration if there are validation errors
        }

        String hashedPassword = hashPassword(password);
        boolean isRegistered = userService.registerUser(username, email, hashedPassword);

        if (isRegistered) {
            // Animate the registration button and then switch to login scene
            isAnimating = true;
            animateDotsAndSwitchScene();
        } else {
            usernameErrorLabel.setText("Registration failed. Username may already exist.");
            usernameField.getStyleClass().add("invalid-field");
        }
    }

    /**
     * Clears all error messages and removes the 'invalid-field' style from input fields.
     */
    private void clearErrors() {
        usernameErrorLabel.setText("");
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");

        usernameField.getStyleClass().remove("invalid-field");
        emailField.getStyleClass().remove("invalid-field");
        passwordField.getStyleClass().remove("invalid-field");
        confirmPasswordField.getStyleClass().remove("invalid-field");
    }

    /**
     * Validates the username based on defined rules.
     *
     * @param username The username to validate.
     * @return An error message if invalid; otherwise, an empty string.
     */
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
        // Add more username validations here if needed
        return "";
    }

    /**
     * Validates the email format using regex.
     *
     * @param email The email address to validate.
     * @return An error message if invalid; otherwise, an empty string.
     */
    private String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email is required.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email address.";
        }
        return "";
    }

    /**
     * Validates the password based on defined rules.
     *
     * @param password The password to validate.
     * @return An error message if invalid; otherwise, an empty string.
     */
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

    /**
     * Validates that the confirmation password matches the original password.
     *
     * @param password        The original password.
     * @param confirmPassword The confirmation password.
     * @return An error message if invalid; otherwise, an empty string.
     */
    private String validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            return "Confirm Password is required.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        return "";
    }

    /**
     * Hashes the password using SHA-256.
     *
     * @param password The plain text password.
     * @return The hashed password as a hexadecimal string.
     */
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
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Handles the Enter key press event to trigger registration.
     *
     * @param event The KeyEvent triggered by key press.
     */
    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleRegistration();
        }
    }

    /**
     * Handles the Tab key press event to switch scenes if applicable.
     *
     * @param event The KeyEvent triggered by key press.
     */
    private void handleTabKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB && canSwitch) {
            Scene currentScene = SceneManager.getInstance().getCurrentScene();
            Scene buttonScene = this.registerButton.getScene();
            
            // Add null checks to prevent NullPointerException
            if (currentScene != null && buttonScene != null && currentScene.getRoot().equals(buttonScene.getRoot())) {
                if (usernameField.isFocused()) {
                    passwordField.requestFocus();
                    event.consume(); // Prevent default Tab behavior
                } else if (passwordField.isFocused()) {
                    confirmPasswordField.requestFocus();
                    event.consume(); // Prevent default Tab behavior
                } else {
                    SceneManager.getInstance().switchToLogin();
                    event.consume(); // Prevent default Tab behavior
                    canSwitch = false;
                    cooldown.playFromStart();
                }
            }
        }
    }


    /**
     * Switches the scene to the login view.
     */
    @FXML
    private void switchToLoginFXML() {
        SceneManager.getInstance().switchToLogin();
    }

    /**
     * Validates the email format using regex.
     *
     * @param email The email address to validate.
     * @return True if valid; otherwise, false.
     */
    private boolean isValidEmail(String email) {
        // Updated regex to ensure top-level domain is present
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Animates the three dots replacing the button's text and then switches to the login scene.
     * The dots' fill colors change sequentially using shades of grey with varying transparency.
     */
    private void animateDotsAndSwitchScene() {
        // Clear the button's text
        registerButton.setText("");

        // Create three dots with initial subtle grey fill
        Circle dot1 = new Circle(5, Color.rgb(128, 128, 128, 0.15)); // Light grey, lower opacity
        Circle dot2 = new Circle(5, Color.rgb(128, 128, 128, 0.15)); // Light grey, lower opacity
        Circle dot3 = new Circle(5, Color.rgb(128, 128, 128, 0.15)); // Light grey, lower opacity

        // Create an HBox to hold the dots
        HBox dotsBox = new HBox(5); // spacing = 5
        dotsBox.setAlignment(Pos.CENTER);
        dotsBox.getChildren().addAll(dot1, dot2, dot3);

        // Set the HBox as the button's graphic
        registerButton.setGraphic(dotsBox);


        // Define the shades of grey with subtle transparency
        Color greyLowOpacity = Color.rgb(128, 128, 128, 0.15);    // Light grey, lower opacity
        Color greyMediumOpacity = Color.rgb(128, 128, 128, 0.35); // Medium grey, medium opacity
        Color greyHighOpacity = Color.rgb(128, 128, 128, 0.55);   // Dark grey, higher opacity

        // Number of animation cycles
        int cycleCount = 2; // Increased to repeat the animation 3 times

        // Duration for each color change
        Duration durationPerCycle = Duration.seconds(0.6); // Decreased duration per cycle

        // Create a Timeline for the animation
        Timeline timeline = new Timeline();

        for (int cycle = 0; cycle < cycleCount; cycle++) {
            double timeOffset = cycle * durationPerCycle.toSeconds();

            // Dot 1 changes opacity at timeOffset + 0.1s
            KeyFrame kf1 = new KeyFrame(Duration.seconds(timeOffset + 0.1),
                new KeyValue(dot1.fillProperty(), greyMediumOpacity));

            // Dot 1 returns to low opacity at timeOffset + 0.3s
            KeyFrame kf1Reset = new KeyFrame(Duration.seconds(timeOffset + 0.3),
                new KeyValue(dot1.fillProperty(), greyLowOpacity));

            // Dot 2 changes opacity at timeOffset + 0.3s
            KeyFrame kf2 = new KeyFrame(Duration.seconds(timeOffset + 0.3),
                new KeyValue(dot2.fillProperty(), greyMediumOpacity));

            // Dot 2 returns to low opacity at timeOffset + 0.5s
            KeyFrame kf2Reset = new KeyFrame(Duration.seconds(timeOffset + 0.5),
                new KeyValue(dot2.fillProperty(), greyLowOpacity));

            // Dot 3 changes opacity at timeOffset + 0.5s
            KeyFrame kf3 = new KeyFrame(Duration.seconds(timeOffset + 0.5),
                new KeyValue(dot3.fillProperty(), greyMediumOpacity));

            // Dot 3 returns to low opacity at timeOffset + 0.7s
            KeyFrame kf3Reset = new KeyFrame(Duration.seconds(timeOffset + 0.7),
                new KeyValue(dot3.fillProperty(), greyLowOpacity));

            // Add all KeyFrames to the Timeline
            timeline.getKeyFrames().addAll(kf1, kf1Reset, kf2, kf2Reset, kf3, kf3Reset);
        }

        // After the animation cycles, switch to the login scene and reset fields/button
        timeline.setOnFinished(event -> {
            // Clear registration fields
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            // Reset the button's text and graphic
            registerButton.setText("CREATE ACCOUNT");
            registerButton.setGraphic(null);

            // Switch to login scene
            SceneManager.getInstance().switchToLogin();

            // Reset the flag
            isAnimating = false;
        });

        // Play the animation
        timeline.play();
    }

}
