package com.socslingo;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import com.socslingo.controllers.common.SidebarController;
import com.socslingo.helpers.TransitionHelper;
import com.socslingo.managers.SceneManager;
import com.socslingo.managers.SessionManager;
import com.socslingo.models.User;

/**
 * The main application class for Socslingo.
 * <p>
 * This class initializes the application, sets up the primary stage, loads the sidebar,
 * manages scene transitions, and handles user sessions.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */

public class App extends Application {

    /**
     * Default constructor for App.
     */
    public App() {
        // Default constructor
    }

    /**
     * Singleton instance of the {@code App} class.
     */
    private static App instance;

    /**
     * The primary stage of the application.
     */
    private Stage primaryStage;

    /**
     * The currently logged-in user.
     */
    private User currentUser; // Track the current user

    /**
     * The sidebar UI component.
     */
    private VBox sidebar;

    /**
     * The controller for the sidebar.
     */
    private SidebarController sidebarController;

    /**
     * The width of the application window.
     */
    private double width;

    /**
     * The height of the application window.
     */
    private double height;

    /**
     * The entry point for the JavaFX application.
     * <p>
     * This method initializes the primary stage, loads the sidebar, preloads scenes,
     * and sets up initial scene transitions.
     * </p>
     * 
     * @param stage the primary stage for this application
     * @throws IOException if an I/O error occurs during loading of FXML files
     */
    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.primaryStage = stage;
        stage.initStyle(StageStyle.UNIFIED);
        stage.setResizable(true);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        this.width = screenWidth * 0.95;
        this.height = screenHeight * 0.95;

        // Load the sidebar first
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/sidebar.fxml"));
        sidebar = sidebarLoader.load(); // Ensure sidebar is loaded as VBox
        sidebar.getStylesheets().add(getClass().getResource("/com/socslingo/css/sidebar.css").toExternalForm());
        sidebarController = sidebarLoader.getController();

        // Initialize SceneManager after loading the sidebar
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(stage, width, height);

        // Load the launch landing scene
        FXMLLoader launchLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/startup.fxml"));
        VBox launchRoot = launchLoader.load();
        Scene launchScene = new Scene(launchRoot, width, height);
        launchScene.getStylesheets().add(getClass().getResource("/com/socslingo/css/startup.css").toExternalForm());

        stage.setTitle("Socslingo");
        Image icon = new Image(getClass().getResourceAsStream("/com/socslingo/images/mascot.png"));
        stage.getIcons().add(icon);
        stage.setScene(launchScene);
        stage.show();

        // Initialize TransitionHelper
        TransitionHelper transitionHelper = new TransitionHelper(primaryStage, sceneManager);

        // Create a FadeTransition to fade in the launchLanding scene
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), launchRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(event -> {
            // Create a PauseTransition to switch scenes after 0.8 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
            delay.setOnFinished(pauseEvent -> {
                // Use TransitionHelper to handle the transition
                transitionHelper.transitionToRegistration(launchRoot);
            });
            delay.play();
        });
        fadeIn.play();
    }

    /**
     * Retrieves the primary stage of the application.
     * 
     * @return the primary {@code Stage} of the application
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Retrieves the singleton instance of the {@code App} class.
     * 
     * @return the singleton instance of {@code App}
     */
    public static App getInstance() {
        return instance;
    }

    /**
     * Retrieves the sidebar UI component.
     * 
     * @return the {@code VBox} representing the sidebar
     */
    public VBox getSidebar() {
        return sidebar;
    }

    /**
     * Retrieves the controller for the sidebar.
     * 
     * @return the {@code SidebarController} instance
     */
    public SidebarController getSidebarController() {
        return sidebarController;
    }

    /**
     * The main method to launch the JavaFX application.
     * 
     * @param args the command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
