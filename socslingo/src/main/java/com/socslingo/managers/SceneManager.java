package com.socslingo.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * SceneManager is responsible for handling scene transitions within the application.
 * It follows the Singleton pattern to ensure only one instance manages the scenes.
 */
public class SceneManager {

    private Stage primaryStage;
    private static SceneManager instance;

    // Map to store preloaded Parent nodes keyed by their FXML paths
    private final Map<String, Parent> preloadedRoots = new HashMap<>();

    // Variable to keep track of the current active scene
    private Scene currentScene;

    /**
     * Private constructor to enforce Singleton pattern.
     *
     * @param stage The primary stage of the application.
     */
    private SceneManager(Stage stage) {
        this.primaryStage = stage;
        preloadScenes(); // Preload Login and Registration scenes at startup
    }

    /**
     * Initializes the SceneManager with the primary stage.
     * Should be called once during application startup.
     *
     * @param stage The primary stage of the application.
     */
    public static void initialize(Stage stage) {
        if (instance == null) {
            instance = new SceneManager(stage);
        }
    }

    /**
     * Retrieves the singleton instance of SceneManager.
     *
     * @return The SceneManager instance.
     * @throws IllegalStateException If SceneManager is not initialized.
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SceneManager not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Preloads specific scenes to eliminate lag during scene switching.
     * Currently preloads Login and Registration scenes.
     */
    private void preloadScenes() {
        preloadScene("/com/socslingo/views/login.fxml", "/com/socslingo/css/login.css");
        preloadScene("/com/socslingo/views/registration.fxml", "/com/socslingo/css/registration.css");
    }

    /**
     * Helper method to preload a single scene.
     *
     * @param fxmlPath The path to the FXML file.
     * @param cssPath  The path to the CSS file.
     */
    private void preloadScene(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controllers are managed properly
            Parent root = loader.load();
            if (cssPath != null && !cssPath.isEmpty()) {
                root.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            }
            preloadedRoots.put(fxmlPath, root);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to preload the scene: " + fxmlPath);
        }
    }

    /**
     * Private method to handle the actual scene switching logic.
     *
     * @param fxmlPath       The path to the FXML file.
     * @param cssPath        The path to the CSS file (can be null or empty).
     * @param fadeInDuration The duration of the fade-in transition (can be null for no transition).
     */
    private void switchSceneInternal(String fxmlPath, String cssPath, Duration fadeInDuration) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Calculate 95% of the screen dimensions
            double sceneWidth = screenWidth * 0.95;
            double sceneHeight = screenHeight * 0.95;
            Parent root;
            if (preloadedRoots.containsKey(fxmlPath)) {
                root = preloadedRoots.get(fxmlPath);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controllers are managed properly
                root = loader.load();
                if (cssPath != null && !cssPath.isEmpty()) {
                    root.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                }
            }

            // If the currentScene is null, set it for the first time
            if (currentScene == null) {
                currentScene = new Scene(root, sceneWidth, sceneHeight);
                primaryStage.setScene(currentScene);
                primaryStage.show();

                // Center the stage on the screen
                primaryStage.centerOnScreen();

                // Apply fade-in transition if specified
                if (fadeInDuration != null) {
                    FadeTransition fadeIn = new FadeTransition(fadeInDuration, root);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }
            } else {
                // Use the existing scene
                Parent oldRoot = currentScene.getRoot();

                if (fadeInDuration != null) {
                    // Apply fade-out to the current root
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), oldRoot);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> {
                        currentScene.setRoot(root);
                        currentScene.getStylesheets().clear(); // Clear previous stylesheets
                        if (cssPath != null && !cssPath.isEmpty()) {
                            currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                        }
                        // Apply fade-in to the new root
                        FadeTransition fadeIn = new FadeTransition(fadeInDuration, root);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                        // Update the currentScene variable
                        currentScene = primaryStage.getScene();
                    });
                    fadeOut.play();
                } else {
                    // No transition; simply switch the root
                    currentScene.setRoot(root);
                    currentScene.getStylesheets().clear(); // Clear previous stylesheets
                    if (cssPath != null && !cssPath.isEmpty()) {
                        currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                    }

                    // Update the currentScene variable
                    currentScene = primaryStage.getScene();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the scene: " + fxmlPath);
        }
    }

    /**
     * Switches the current scene to the specified FXML file without any transition.
     *
     * @param fxmlPath The path to the FXML file.
     * @param cssPath  The path to the CSS file (can be null or empty).
     */
    public void switchScene(String fxmlPath, String cssPath) {
        switchSceneInternal(fxmlPath, cssPath, null);
    }

    /**
     * Switches the current scene to the specified FXML file with a fade transition.
     *
     * @param fxmlPath       The path to the FXML file.
     * @param cssPath        The path to the CSS file (can be null or empty).
     * @param fadeInDuration The duration of the fade-in transition.
     */
    public void switchScene(String fxmlPath, String cssPath, Duration fadeInDuration) {
        switchSceneInternal(fxmlPath, cssPath, fadeInDuration);
    }

    /**
     * Shows the startup scene with fade-in and automatic transition to the registration scene.
     */
    public void showStartupScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/startup.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controllers are managed properly
            Parent root = loader.load();

            // Retrieve screen dimensions
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Calculate 95% of the screen dimensions
            double sceneWidth = screenWidth * 0.95;
            double sceneHeight = screenHeight * 0.95;

            // Create the scene with 95% of the screen size
            Scene scene = new Scene(root, sceneWidth, sceneHeight);
            scene.getStylesheets().add(getClass().getResource("/com/socslingo/css/startup.css").toExternalForm());

            primaryStage.setTitle("Socslingo");
            Image icon = new Image(getClass().getResourceAsStream("/com/socslingo/images/mascot.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Center the stage on the screen
            primaryStage.centerOnScreen();

            // Set the currentScene variable
            currentScene = scene;

            // Fade-in and transition logic
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
                delay.setOnFinished(pauseEvent -> transitionToRegistration(root));
                delay.play();
            });
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the startup scene.");
        }
    }

    /**
     * Transitions from the startup scene to the registration scene with fade effects.
     *
     * @param currentRoot The root node of the current (startup) scene.
     */
    public void transitionToRegistration(Parent currentRoot) {
        // Fade out the current (startup) scene
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            // Switch to the registration scene
            switchToRegistration();
            Scene registrationScene = primaryStage.getScene();

            if (registrationScene != null) {
                Parent registrationRoot = registrationScene.getRoot();
                // Ensure the registration scene starts with opacity 0
                registrationRoot.setOpacity(0.0);

                // Fade in the registration scene
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), registrationRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } else {
                System.err.println("Registration scene not found in SceneManager.");
            }
        });
        fadeOut.play();
    }

    /**
     * Retrieves the currently active scene.
     *
     * @return The current Scene.
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Utility method to show error alerts to the user.
     *
     * @param title   The title of the alert dialog.
     * @param message The content message of the alert.
     */
    private void showErrorAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Specific Scene Switching Methods

    /**
     * Switches to the Registration scene with a fade transition.
     */
    public void switchToRegistrationWithFade() {
        switchScene(
            "/com/socslingo/views/registration.fxml",
            "/com/socslingo/css/registration.css",
            Duration.seconds(1) // Fade duration
        );
    }

    /**
     * Switches to the Registration scene without any transition.
     */
    public void switchToRegistration() {
        switchScene(
            "/com/socslingo/views/registration.fxml",
            "/com/socslingo/css/registration.css"
        );
    }

    /**
     * Switches to the Login scene without any transition.
     */
    public void switchToLogin() {
        switchScene(
            "/com/socslingo/views/login.fxml",
            "/com/socslingo/css/login.css"
        );
    }

    /**
     * Switches to the Main scene without any transition.
     *
     * @param fxmlPath The path to the main FXML file.
     * @param cssPath  The path to the main CSS file.
     */
    public void switchToMain(String fxmlPath, String cssPath) {
        switchScene(
            fxmlPath,
            cssPath
        );
    }

    /**
     * Switches to the Main scene with a fade transition.
     *
     * @param fxmlPath       The path to the main FXML file.
     * @param cssPath        The path to the main CSS file.
     * @param fadeInDuration The duration of the fade-in transition.
     */
    public void switchToMain(String fxmlPath, String cssPath, Duration fadeInDuration) {
        switchScene(
            fxmlPath,
            cssPath,
            fadeInDuration
        );
    }

    // ... Add more specific methods for other scenes as needed
}
