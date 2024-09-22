package com.socslingo.controllers.authentication;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import com.socslingo.models.User;
import com.socslingo.managers.SessionManager;

/**
 * Controller class responsible for handling user interactions on the login screen.
 * <p>
 * This class manages the login process, including user authentication, transitioning
 * to the registration screen, and handling UI events such as button clicks and key presses.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class LoginController {

    private UserService userService;

    /**
     * Constructs a new {@code LoginController} with the specified {@code UserService}.
     * 
     * @param userService the service responsible for user-related operations
     */
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

    private boolean canSwitch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    /**
     * Initializes the controller class. This method is automatically called after the FXML
     * file has been loaded. It sets up event handlers and bindings for UI components.
     */
    @FXML
    private void initialize() {
        // Add event handler for Enter key press on username and password fields
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);
        passwordField.setOnKeyPressed(this::handleEnterKeyPress);

        // Add scene change listeners to handle Tab key presses globally within the scene
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

        // Configure cooldown to prevent rapid scene switching
        cooldown.setOnFinished(event -> canSwitch = true);

        // Bind visibility of error messages and icons to the presence of error text
        errorMessageLabel.visibleProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorMessageLabel.managedProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorIcon.visibleProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
        errorIcon.managedProperty().bind(errorMessageLabel.textProperty().isNotEmpty());
    }

    /**
     * Handles the login action when the login button is clicked or Enter key is pressed.
     * <p>
     * This method retrieves the entered username and password, hashes the password,
     * and validates the user credentials using the {@code UserService}. If authentication
     * is successful, it sets the current user in the {@code SessionManager} and transitions
     * to the main home scene. Otherwise, it displays an error message.
     * </p>
     */
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
            // Use SceneManager to switch to the main home scene
            SceneManager.getInstance().switchTo(SceneManager.MAIN_HOME);
        } else {
            System.out.println("Invalid username or password. Please try again.");
            errorMessageLabel.setText("Wrong username or password. Please try again.");
        }
    }

    /**
     * Hashes the provided password using SHA-256 algorithm.
     * 
     * @param password the plain-text password to be hashed
     * @return the hashed password as a hexadecimal string
     * @throws RuntimeException if the hashing algorithm is not available or encoding fails
     */
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

    /**
     * Handles the Enter key press event on the username and password fields.
     * <p>
     * If the Enter key is pressed, this method triggers the login process.
     * </p>
     * 
     * @param event the key event representing the Enter key press
     */
    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    /**
     * Handles the Tab key press event to switch to the registration scene.
     * <p>
     * When the Tab key is pressed and no input field is focused, this method transitions
     * the application to the registration scene, provided that a cooldown period has passed
     * to prevent rapid switching.
     * </p>
     * 
     * @param event the key event representing the Tab key press
     */
    private void handleTabKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB && canSwitch) {
            if (!usernameField.isFocused() && !passwordField.isFocused()) {
                SceneManager.getInstance().switchTo(SceneManager.REGISTRATION);
                event.consume(); // Consume the event to prevent default Tab behavior
                canSwitch = false;
                cooldown.playFromStart();
            }
        }
    }

    /**
     * Switches the current scene to the registration FXML layout.
     * <p>
     * This method is invoked when the user clicks the button to navigate to the registration screen.
     * </p>
     */
    @FXML
    private void switchToRegistrationFXML() {
        SceneManager.getInstance().switchTo(SceneManager.REGISTRATION);
    }
}
