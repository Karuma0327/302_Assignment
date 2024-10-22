package com.socslingo.controllers;

import com.socslingo.models.User;
import com.socslingo.services.UserService;
import com.socslingo.dataAccess.UserDataAccess;
import com.socslingo.managers.DatabaseManager;
import com.socslingo.managers.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private Label avatarLabel;



    @FXML
    private TextField actualNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    private UserService userService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseManager dbManager = new DatabaseManager();
        UserDataAccess userDataAccess = new UserDataAccess(dbManager);
        userService = new UserService(userDataAccess);

        // Fetch current user from SessionManager
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            populateUserDetails();
        } else {
            showAlert(Alert.AlertType.ERROR, "No User Logged In", "Please log in to access settings.");
            // Redirect to login screen or close settings
        }
    }

    private void populateUserDetails() {
        avatarLabel.setText(currentUser.getUsername().substring(0, 1).toUpperCase());
        if (currentUser.getProfileBannerPath() != null) {
            File avatarFile = new File(currentUser.getProfileBannerPath());
            if (avatarFile.exists()) {
                Image avatarImage = new Image(avatarFile.toURI().toString());
                
            }
        }
        actualNameField.setText(currentUser.getActualName()); // Set Actual Name
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail()); // Set Email
    }
    

    @FXML
    private void handleSaveChanges() {
        String newActualName = actualNameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();

        // Input Validation
        if (newActualName.isEmpty() || newEmail.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Actual Name and Email cannot be empty.");
            return;
        }

        // Update actual name
        currentUser.setActualName(newActualName);

        // Update email
        currentUser.setEmail(newEmail);

        // Update password if provided
        if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
            // Validate current password
            User validatedUser = userService.validateUser(currentUser.getUsername(), hashPassword(currentPassword));
            if (validatedUser == null) {
                showAlert(Alert.AlertType.ERROR, "Authentication Failed", "Current password is incorrect.");
                return;
            }
            currentUser.setPassword(hashPassword(newPassword));
        }

        // Update user in the database
        boolean updateSuccess = userService.updateUser(currentUser);

        if (updateSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your settings have been updated.");
            // Clear password fields
            currentPasswordField.clear();
            newPasswordField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "An error occurred while updating your settings.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Account");
        confirmationAlert.setHeaderText("Are you sure you want to delete your account?");
        confirmationAlert.setContentText("This action cannot be undone.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleteSuccess = userService.deleteUser(currentUser.getId());
                if (deleteSuccess) {
                    showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been successfully deleted.");
                    // Redirect to login screen after deletion
                    redirectToLogin();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "An error occurred while deleting your account.");
                }
            }
        });
    }

    @FXML
    private void handleUpdateAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        // File selectedFile = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());

        // if (selectedFile != null) {
        //     // Update avatar in the UI
        //     Image avatarImage = new Image(selectedFile.toURI().toString());
        //     avatarImageView.setImage(avatarImage);
        //     avatarLabel.setText(""); // Clear the initial label

        //     // Update user's avatar path
        //     currentUser.setProfileBannerPath(selectedFile.getPath());

        //     // Persist the avatar path in the database
        //     boolean updateSuccess = userService.updateUserProfileBanner(currentUser.getId(), selectedFile.getPath());

        //     if (updateSuccess) {
        //         showAlert(Alert.AlertType.INFORMATION, "Avatar Updated", "Your avatar has been updated successfully.");
        //     } else {
        //         showAlert(Alert.AlertType.ERROR, "Update Failed", "An error occurred while updating your avatar.");
        //     }
        // }
    }

    @FXML
    private void handleLogout() {
        // Clear the current user session
        SessionManager.getInstance().setCurrentUser(null);
        showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out.");

        // Redirect to the login screen
        redirectToLogin();
    }

    /**
     * Utility method to hash passwords.
     * Implement your preferred hashing mechanism here.
     */
    private String hashPassword(String password) {
        // Example using simple hashing (You should use a stronger hashing mechanism like BCrypt)
        return Integer.toHexString(password.hashCode());
    }

    /**
     * Utility method to show alerts.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Redirects the user to the login screen.
     */
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage primaryStage = (Stage) ((Node) usernameField).getScene().getWindow();
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading login.fxml: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Unable to load the login screen.");
        }
    }
}