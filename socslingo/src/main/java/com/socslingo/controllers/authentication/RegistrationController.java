package com.socslingo.controllers.authentication;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
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

/**
 * Controller class responsible for handling user registration interactions.
 * <p>
 * This class manages the registration process, including input validation,
 * user creation, and navigation between login and registration scenes.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class RegistrationController {
    private UserService userService;

    /**
     * Constructs a new {@code RegistrationController} with the specified {@code UserService}.
     * 
     * @param userService the service responsible for user-related operations
     */
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

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

    private boolean canSwitch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    /**
     * Initializes the controller class. This method is automatically called after the FXML
     * file has been loaded. It sets up event handlers and bindings for UI components.
     */
    @FXML
    private void initialize() {
        // Add event handler for Enter key press
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);
        emailField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
        confirmPasswordField.setOnKeyPressed(this::handleEnterKeyPress);

        // Add scene change listeners to handle Tab key presses globally within the scene
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

        // Configure cooldown to prevent rapid scene switching
        cooldown.setOnFinished(event -> canSwitch = true);

        // Bind the visibility and managed properties of the error labels to their text properties
        usernameErrorLabel.visibleProperty().bind(usernameErrorLabel.textProperty().isNotEmpty());
        usernameErrorLabel.managedProperty().bind(usernameErrorLabel.textProperty().isNotEmpty());
        usernameErrorIcon.visibleProperty().bind(usernameErrorLabel.textProperty().isNotEmpty());
        usernameErrorIcon.managedProperty().bind(usernameErrorLabel.textProperty().isNotEmpty());

        emailErrorLabel.visibleProperty().bind(emailErrorLabel.textProperty().isNotEmpty());
        emailErrorLabel.managedProperty().bind(emailErrorLabel.textProperty().isNotEmpty());
        emailErrorIcon.visibleProperty().bind(emailErrorLabel.textProperty().isNotEmpty());
        emailErrorIcon.managedProperty().bind(emailErrorLabel.textProperty().isNotEmpty());

        passwordErrorLabel.visibleProperty().bind(passwordErrorLabel.textProperty().isNotEmpty());
        passwordErrorLabel.managedProperty().bind(passwordErrorLabel.textProperty().isNotEmpty());
        passwordErrorIcon.visibleProperty().bind(passwordErrorLabel.textProperty().isNotEmpty());
        passwordErrorIcon.managedProperty().bind(passwordErrorLabel.textProperty().isNotEmpty());

        confirmPasswordErrorLabel.visibleProperty().bind(confirmPasswordErrorLabel.textProperty().isNotEmpty());
        confirmPasswordErrorLabel.managedProperty().bind(confirmPasswordErrorLabel.textProperty().isNotEmpty());
        confirmPasswordErrorIcon.visibleProperty().bind(confirmPasswordErrorLabel.textProperty().isNotEmpty());
        confirmPasswordErrorIcon.managedProperty().bind(confirmPasswordErrorLabel.textProperty().isNotEmpty());
    }

    /**
     * Handles the registration action when the register button is clicked or Enter key is pressed.
     * <p>
     * This method validates user input, creates a new user if validation passes,
     * and navigates to the login scene upon successful registration.
     * </p>
     */
    @FXML
    private void handleRegistration() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Reset error messages and styles
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
        usernameField.getStyleClass().remove("invalid-field");
        passwordField.getStyleClass().remove("invalid-field");
        confirmPasswordField.getStyleClass().remove("invalid-field");

        boolean hasError = false;

        // Validate username
        if (username.isEmpty()) {
            usernameErrorLabel.setText("Username is required.");
            usernameField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate email
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email is required.");
            emailField.getStyleClass().add("invalid-field");
            hasError = true;
        } else if (!isValidEmail(email)) {
            emailErrorLabel.setText("Invalid email address");
            emailField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate password
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password is required");
            passwordField.getStyleClass().add("invalid-field");
            hasError = true;
        } else if (password.length() < 8) {
            passwordErrorLabel.setText("Password must be at least 8 characters long");
            passwordField.getStyleClass().add("invalid-field");
            hasError = true;
        } else if (password.contains(" ")) {
            passwordErrorLabel.setText("Password cannot contain spaces");
            passwordField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordErrorLabel.setText("Confirm Password is required");
            confirmPasswordField.getStyleClass().add("invalid-field");
            hasError = true;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match");
            confirmPasswordField.getStyleClass().add("invalid-field");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Hash password and register user
        String hashedPassword = hashPassword(password);
        boolean isRegistered = userService.registerUser(username, email, hashedPassword);
		
        if (isRegistered) {
            // Use SceneManager to switch to the login scene
            SceneManager.getInstance().switchTo(SceneManager.LOGIN);
        } else {
            usernameErrorLabel.setText("Registration failed. Username may already exist.");
        }
    }

    /**
     * Hashes the provided password using SHA-256 algorithm.
     * 
     * @param password the plain-text password to be hashed
     * @return the hashed password as a hexadecimal string
     * @throws RuntimeException if the hashing algorithm is not available or encoding fails
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the Enter key press event on input fields.
     * <p>
     * If the Enter key is pressed, this method triggers the registration process.
     * </p>
     * 
     * @param event the key event representing the Enter key press
     */
    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleRegistration();
        }
    }

    /**
     * Handles the Tab key press event to switch to the login scene.
     * <p>
     * When the Tab key is pressed and no input field is focused, this method transitions
     * the application to the login scene, provided that a cooldown period has passed
     * to prevent rapid switching.
     * </p>
     * 
     * @param event the key event representing the Tab key press
     */
    private void handleTabKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB && canSwitch) {
            if (!usernameField.isFocused() && !emailField.isFocused() && !passwordField.isFocused() && !confirmPasswordField.isFocused()) {
                SceneManager.getInstance().switchTo(SceneManager.LOGIN);
                event.consume(); // Consume the event to prevent default Tab behavior
                canSwitch = false;
                cooldown.playFromStart();
            }
        }
    }

    /**
     * Switches the current scene to the login FXML layout.
     * <p>
     * This method is invoked when the user clicks the button to navigate to the login screen.
     * </p>
     */
    @FXML
    private void switchToLoginFXML() {
        SceneManager.getInstance().switchTo(SceneManager.LOGIN);
    }

    /**
     * Validates the format of the provided email address.
     * 
     * @param email the email address to validate
     * @return {@code true} if the email matches the regex pattern, {@code false} otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
