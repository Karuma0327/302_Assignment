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

    private Stage primary_stage;
    private static SceneManager instance;

    private final Map<String, Parent> preloaded_roots = new HashMap<>();

    private Scene current_scene;

    private SceneManager(Stage stage) {
        this.primary_stage = stage;

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

    private void preloadScene(String fxml_path, String css_path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Parent root = loader.load();
            if (css_path != null && !css_path.isEmpty()) {
                root.getStylesheets().add(getClass().getResource(css_path).toExternalForm());
            }
            preloaded_roots.put(fxml_path, root);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to preload the scene: " + fxml_path);
        }
    }

    private void switchSceneInternal(String fxml_path, String css_path, Duration fade_in_duration) {
        try {
            Rectangle2D screen_bounds = Screen.getPrimary().getBounds();
            double screen_width = screen_bounds.getWidth();
            double screen_height = screen_bounds.getHeight();

            double scene_width = screen_width * 0.95;
            double scene_height = screen_height * 0.95;
            Parent root;
            if (preloaded_roots.containsKey(fxml_path)) {
                root = preloaded_roots.get(fxml_path);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
                loader.setControllerFactory(ControllerManager.getInstance());
                root = loader.load();
                if (css_path != null && !css_path.isEmpty()) {
                    root.getStylesheets().add(getClass().getResource(css_path).toExternalForm());
                }
            }

            if (current_scene == null) {
                current_scene = new Scene(root, scene_width, scene_height);
                primary_stage.setScene(current_scene);
                primary_stage.show();

                primary_stage.centerOnScreen();

                if (fade_in_duration != null) {
                    FadeTransition fade_in = new FadeTransition(fade_in_duration, root);
                    fade_in.setFromValue(0.0);
                    fade_in.setToValue(1.0);
                    fade_in.play();
                }
            } else {
                Parent old_root = current_scene.getRoot();

                if (fade_in_duration != null) {
                    FadeTransition fade_out = new FadeTransition(Duration.seconds(0.2), old_root);
                    fade_out.setFromValue(1.0);
                    fade_out.setToValue(0.0);
                    fade_out.setOnFinished(event -> {
                        current_scene.setRoot(root);
                        current_scene.getStylesheets().clear();
                        if (css_path != null && !css_path.isEmpty()) {
                            current_scene.getStylesheets().add(getClass().getResource(css_path).toExternalForm());
                        }
                        FadeTransition fade_in = new FadeTransition(fade_in_duration, root);
                        fade_in.setFromValue(0.0);
                        fade_in.setToValue(1.0);
                        fade_in.play();

                        current_scene = primary_stage.getScene();
                    });
                    fade_out.play();
                } else {
                    current_scene.setRoot(root);
                    current_scene.getStylesheets().clear();
                    if (css_path != null && !css_path.isEmpty()) {
                        current_scene.getStylesheets().add(getClass().getResource(css_path).toExternalForm());
                    }

                    current_scene = primary_stage.getScene();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the scene: " + fxml_path);
        }
    }

    public void switchScene(String fxml_path, String css_path) {
        switchSceneInternal(fxml_path, css_path, null);
    }

    public void switchScene(String fxml_path, String css_path, Duration fade_in_duration) {
        switchSceneInternal(fxml_path, css_path, fade_in_duration);
    }

    public void showStartupScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/startup.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance());
            Parent root = loader.load();

            Rectangle2D screen_bounds = Screen.getPrimary().getBounds();
            double screen_width = screen_bounds.getWidth();
            double screen_height = screen_bounds.getHeight();

            double scene_width = screen_width * 0.95;
            double scene_height = screen_height * 0.95;

            Scene scene = new Scene(root, scene_width, scene_height);
            scene.getStylesheets().add(getClass().getResource("/com/socslingo/css/startup.css").toExternalForm());

            primary_stage.setTitle("Socslingo");
            Image icon = new Image(getClass().getResourceAsStream("/com/socslingo/images/mascot.png"));
            primary_stage.getIcons().add(icon);
            primary_stage.setScene(scene);
            primary_stage.show();

            primary_stage.centerOnScreen();

            current_scene = scene;

            FadeTransition fade_in = new FadeTransition(Duration.seconds(0.2), root);
            fade_in.setFromValue(0.0);
            fade_in.setToValue(1.0);
            fade_in.setOnFinished(event -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
                delay.setOnFinished(pause_event -> transitionToRegistration(root));
                delay.play();
            });
            fade_in.play();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the startup scene.");
        }
    }

    public void transitionToRegistration(Parent current_root) {
        FadeTransition fade_out = new FadeTransition(Duration.seconds(1), current_root);
        fade_out.setFromValue(1.0);
        fade_out.setToValue(0.0);
        fade_out.setOnFinished(event -> {
            switchToRegistration();
            Scene registration_scene = primary_stage.getScene();

            if (registration_scene != null) {
                Parent registration_root = registration_scene.getRoot();
                registration_root.setOpacity(0.0);

                FadeTransition fade_in = new FadeTransition(Duration.seconds(1), registration_root);
                fade_in.setFromValue(0.0);
                fade_in.setToValue(1.0);
                fade_in.play();
            } else {
                System.err.println("Registration scene not found in SceneManager.");
            }
        });
        fade_out.play();
    }

    public Scene getCurrentScene() {
        return current_scene;
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

    public void switchToMain(String fxml_path, String css_path) {
        switchScene(
            fxml_path,
            css_path
        );
    }

    public void switchToMain(String fxml_path, String css_path, Duration fade_in_duration) {
        switchScene(
            fxml_path,
            css_path,
            fade_in_duration
        );
    }

    private void setupWindowedMode98Percent() {
        primary_stage.initStyle(StageStyle.UNDECORATED);

        Rectangle2D screen_bounds = Screen.getPrimary().getVisualBounds();

        double width = screen_bounds.getWidth() * 0.98;
        double height = screen_bounds.getHeight() * 0.98;

        double x_pos = screen_bounds.getMinX() + (screen_bounds.getWidth() - width) / 2;
        double y_pos = screen_bounds.getMinY() + (screen_bounds.getHeight() - height) / 2;

        primary_stage.setX(x_pos);
        primary_stage.setY(y_pos);
        primary_stage.setWidth(width);
        primary_stage.setHeight(height);
    }
}
