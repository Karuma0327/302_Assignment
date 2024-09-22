package com.socslingo.helpers;

import com.socslingo.managers.SceneManager;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Helper class for managing scene transitions with animations.
 * <p>
 * This class provides methods to perform smooth transitions between different scenes
 * within the application, enhancing the user experience with fade animations.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class TransitionHelper {

    @SuppressWarnings("unused")
    private final Stage primaryStage;
    private final SceneManager sceneManager;

    /**
     * Constructs a new {@code TransitionHelper} with the specified primary stage and scene manager.
     * 
     * @param primaryStage the primary stage of the application
     * @param sceneManager the manager responsible for handling scene transitions
     */
    public TransitionHelper(Stage primaryStage, SceneManager sceneManager) {
        this.primaryStage = primaryStage;
        this.sceneManager = sceneManager;
    }

    /**
     * Performs a smooth fade transition from the current scene to the registration scene.
     *
     * @param currentRoot The root node of the current scene (startup scene).
     */
    public void transitionToRegistration(Parent currentRoot) {
        // Fade out the current (startup) scene
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            // Switch to the registration scene
            sceneManager.switchTo(SceneManager.REGISTRATION);
            Scene registrationScene = sceneManager.getScene(SceneManager.REGISTRATION);
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
}
