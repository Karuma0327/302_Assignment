package com.socslingo;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;




public class App extends Application {

    private static App instance;
    private Scene loginScene;
    private Scene registrationScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.primaryStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);

    
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        double width = screenWidth * 0.95;
        double height = screenHeight * 0.95;

        // Load the launch landing scene
        FXMLLoader launchLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/startup.fxml"));
        VBox launchRoot = launchLoader.load();
        Scene launchScene = new Scene(launchRoot, width, height);
        launchScene.getStylesheets().add(getClass().getResource("/com/socslingo/css/startup.css").toExternalForm());

        // Preload login scene
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/login.fxml"));
        VBox loginRoot = loginLoader.load();
        loginScene = new Scene(loginRoot, width, height);
        loginScene.getStylesheets().add(getClass().getResource("/com/socslingo/css/login.css").toExternalForm());

        // Preload registration scene
        FXMLLoader registrationLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/registration.fxml"));
        VBox registrationRoot = registrationLoader.load();
        registrationRoot.setOpacity(0.0); // Set initial opacity to 0
        registrationScene = new Scene(registrationRoot, width, height);
        registrationScene.getStylesheets().add(getClass().getResource("/com/socslingo/css/registration.css").toExternalForm());


        stage.setTitle("Socslingo");
        Image icon = new Image(getClass().getResourceAsStream("/com/socslingo/images/mascot.png"));
        stage.getIcons().add(icon);
        stage.setScene(launchScene);
        stage.show();

        // Create a FadeTransition to fade in the launchLanding scene
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), launchRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(event -> {
            // Create a PauseTransition to switch scenes after 1 second
            PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
            delay.setOnFinished(pauseEvent -> {
                // Create a FadeTransition to fade out the current scene
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), launchRoot);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(fadeEvent -> {
                    // Switch to the registration scene
                    switchToRegistrationScene();
                });
                fadeOut.play();
            });
            delay.play();
        });
        fadeIn.play();
    }
    public void switchToLoginScene() {
        primaryStage.setScene(loginScene);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loginScene.getRoot());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void switchToRegistrationScene() {
        primaryStage.setScene(registrationScene);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), registrationScene.getRoot());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public static App getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}