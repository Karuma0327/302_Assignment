package com.socslingo.managers;

import java.io.IOException;
import java.util.*;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.util.Duration;

public class SceneManager {

    private Stage primaryStage;
    private static SceneManager instance;

    private final Map<String, Parent> preloadedRoots = new HashMap<>();

    private Scene currentScene;

    private SceneManager(Stage stage) {
        this.primaryStage = stage;

        setupWindowedMode98Percent();

        preloadScenes();
    }

    public static void initialize(Stage stage) {
        if (instance == null) {
            instance = new SceneManager(stage);
        }
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SceneManager not initialized. Call initialize() first.");
        }
        return instance;
    }

    private void preloadScenes() {
        preloadScene("/com/socslingo/views/login.fxml", "/com/socslingo/css/login.css");
        preloadScene("/com/socslingo/views/registration.fxml", "/com/socslingo/css/registration.css");
    }

    private void preloadScene(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance());
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

    private void switchSceneInternal(String fxmlPath, String cssPath, Duration fadeInDuration) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            double sceneWidth = screenWidth * 0.95;
            double sceneHeight = screenHeight * 0.95;
            Parent root;
            if (preloadedRoots.containsKey(fxmlPath)) {
                root = preloadedRoots.get(fxmlPath);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(ControllerManager.getInstance());
                root = loader.load();
                if (cssPath != null && !cssPath.isEmpty()) {
                    root.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                }
            }

            if (currentScene == null) {
                currentScene = new Scene(root, sceneWidth, sceneHeight);
                primaryStage.setScene(currentScene);
                primaryStage.show();

                primaryStage.centerOnScreen();

                if (fadeInDuration != null) {
                    FadeTransition fadeIn = new FadeTransition(fadeInDuration, root);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }
            } else {
                Parent oldRoot = currentScene.getRoot();

                if (fadeInDuration != null) {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), oldRoot);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> {
                        currentScene.setRoot(root);
                        currentScene.getStylesheets().clear();
                        if (cssPath != null && !cssPath.isEmpty()) {
                            currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                        }
                        FadeTransition fadeIn = new FadeTransition(fadeInDuration, root);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                        currentScene = primaryStage.getScene();
                    });
                    fadeOut.play();
                } else {
                    currentScene.setRoot(root);
                    currentScene.getStylesheets().clear();
                    if (cssPath != null && !cssPath.isEmpty()) {
                        currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                    }

                    currentScene = primaryStage.getScene();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the scene: " + fxmlPath);
        }
    }

    public void switchScene(String fxmlPath, String cssPath) {
        switchSceneInternal(fxmlPath, cssPath, null);
    }

    public void switchScene(String fxmlPath, String cssPath, Duration fadeInDuration) {
        switchSceneInternal(fxmlPath, cssPath, fadeInDuration);
    }

    public void showStartupScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/startup.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance());
            Parent root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            double sceneWidth = screenWidth * 0.95;
            double sceneHeight = screenHeight * 0.95;

            Scene scene = new Scene(root, sceneWidth, sceneHeight);
            scene.getStylesheets().add(getClass().getResource("/com/socslingo/css/startup.css").toExternalForm());

            primaryStage.setTitle("Socslingo");
            Image icon = new Image(getClass().getResourceAsStream("/com/socslingo/images/mascot.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.centerOnScreen();

            currentScene = scene;

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

    public void transitionToRegistration(Parent currentRoot) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            switchToRegistration();
            Scene registrationScene = primaryStage.getScene();

            if (registrationScene != null) {
                Parent registrationRoot = registrationScene.getRoot();
                registrationRoot.setOpacity(0.0);

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

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void showErrorAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    public void switchToRegistrationWithFade() {
        switchScene(
            "/com/socslingo/views/registration.fxml",
            "/com/socslingo/css/registration.css",
            Duration.seconds(1)
        );
    }

    public void switchToRegistration() {
        switchScene(
            "/com/socslingo/views/registration.fxml",
            "/com/socslingo/css/registration.css"
        );
    }

    public void switchToLogin() {
        switchScene(
            "/com/socslingo/views/login.fxml",
            "/com/socslingo/css/login.css"
        );
    }

    public void switchToMain(String fxmlPath, String cssPath) {
        switchScene(
            fxmlPath,
            cssPath
        );
    }

    public void switchToMain(String fxmlPath, String cssPath, Duration fadeInDuration) {
        switchScene(
            fxmlPath,
            cssPath,
            fadeInDuration
        );
    }


    private void setupWindowedMode98Percent() {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double width = screenBounds.getWidth() * 0.98;
        double height = screenBounds.getHeight() * 0.98;

        double xPos = screenBounds.getMinX() + (screenBounds.getWidth() - width) / 2;
        double yPos = screenBounds.getMinY() + (screenBounds.getHeight() - height) / 2;

        primaryStage.setX(xPos);
        primaryStage.setY(yPos);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }


}
