package com.socslingo;

import com.socslingo.managers.SceneManager;

import javafx.application.Application;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        
        // Initialize SceneManager with the primary stage
        SceneManager.initialize(stage);
        stage.initStyle(StageStyle.UNIFIED);
        // Show the startup scene
        SceneManager.getInstance().showStartupScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
