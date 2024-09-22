package com.socslingo.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.socslingo.controllers.BaseController;
import com.socslingo.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Singleton class responsible for managing and switching between different scenes in the application.
 * <p>
 * This class handles the loading, caching, and transitioning of scenes such as login, registration,
 * and main home. It utilizes the {@code ControllerManager} to instantiate controllers with their dependencies.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private ControllerManager controllerManager;
    private Map<String, Scene> sceneCache;
    private double width;
    private double height;

    // Define constants for scene names
    /**
     * Constant for the login scene.
     */
    public static final String LOGIN = "login";

    /**
     * Constant for the main home scene.
     */
    public static final String MAIN_HOME = "mainHome";

    /**
     * Constant for the registration scene.
     */
    public static final String REGISTRATION = "registration";
    
    // Add other scene names as needed

    /**
     * Private constructor to enforce singleton pattern.
     * <p>
     * Initializes the scene cache and retrieves the instance of {@code ControllerManager}.
     * </p>
     */
    private SceneManager() {
        sceneCache = new HashMap<>();
        controllerManager = ControllerManager.getInstance();
    }

    /**
     * Retrieves the singleton instance of {@code SceneManager}.
     * <p>
     * If the instance does not exist, it is created. Otherwise, the existing instance is returned.
     * </p>
     * 
     * @return the singleton instance of {@code SceneManager}
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Initializes the {@code SceneManager} with the primary stage and preloads necessary scenes.
     * <p>
     * This method sets the primary stage dimensions and preloads scenes like login, registration,
     * and main home for quicker access during scene switches.
     * </p>
     * 
     * @param stage  the primary stage of the application
     * @param width  the desired width of the application window
     * @param height the desired height of the application window
     * @throws IOException if an I/O error occurs during scene preloading
     */
    public void initialize(Stage stage, double width, double height) throws IOException {
        this.primaryStage = stage;
        this.width = width;
        this.height = height;

        // Preload Login and Registration scenes
        preloadScene(LOGIN, "/com/socslingo/views/login.fxml", "/com/socslingo/css/login.css");
        preloadScene(REGISTRATION, "/com/socslingo/views/registration.fxml", "/com/socslingo/css/registration.css");
        preloadScene(MAIN_HOME, "/com/socslingo/views/mainHome.fxml", "/com/socslingo/css/mainHome.css");
        // You can preload other frequently used scenes here as needed
    }

    /**
     * Preloads a scene and caches it for quick access.
     * <p>
     * This method loads the FXML layout and applies the corresponding CSS stylesheet to create a {@code Scene}.
     * If the controller of the scene extends {@code BaseController}, it injects the sidebar and its controller.
     * </p>
     * 
     * @param name     the unique name identifier for the scene
     * @param fxmlPath the path to the FXML layout file
     * @param cssPath  the path to the CSS stylesheet file
     * @throws IOException if an I/O error occurs during scene loading
     */
    private void preloadScene(String name, String fxmlPath, String cssPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(controllerManager);
        Parent root = loader.load();

        // If the controller is a BaseController, set the sidebar and its controller
        Object controller = loader.getController();
        if (controller instanceof BaseController) {
            BaseController baseController = (BaseController) controller;
            baseController.setSidebar(App.getInstance().getSidebar());
            baseController.setSidebarController(App.getInstance().getSidebarController());
        }

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

        if (name.equals(SceneManager.REGISTRATION)) {
            root.setOpacity(0.0);
        }
        sceneCache.put(name, scene);
    }

    /**
     * Switches to a preloaded scene.
     * <p>
     * This method sets the scene associated with the provided scene name to the primary stage.
     * </p>
     * 
     * @param sceneName the unique name identifier of the scene to switch to
     */
    public void switchTo(String sceneName) {
        Scene scene = sceneCache.get(sceneName);
        if (scene == null) {
            System.err.println("Scene " + sceneName + " not found in cache.");
            return;
        }

        primaryStage.setScene(scene);
    }

    /**
     * Loads a new scene that wasn't preloaded and caches it.
     * <p>
     * This method dynamically loads a scene from the specified FXML and CSS files, caches it,
     * and then switches to it.
     * </p>
     * 
     * @param sceneName the unique name identifier for the scene
     * @param fxmlPath  the path to the FXML layout file
     * @param cssPath   the path to the CSS stylesheet file
     */
    public void loadAndSwitchTo(String sceneName, String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerManager);
            Parent root = loader.load();

            // If the controller is a BaseController, set the sidebar and its controller
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                BaseController baseController = (BaseController) controller;
                baseController.setSidebar(App.getInstance().getSidebar());
                baseController.setSidebarController(App.getInstance().getSidebarController());
            }

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

            // Cache the newly loaded scene
            sceneCache.put(sceneName, scene);

            // Switch to the newly loaded scene
            switchTo(sceneName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a preloaded scene by its name.
     * <p>
     * This method allows access to cached scenes for situations where direct manipulation is needed.
     * </p>
     * 
     * @param sceneName the unique name identifier of the scene
     * @return the {@code Scene} associated with the provided name, or {@code null} if not found
     */
    public Scene getScene(String sceneName) {
        return sceneCache.get(sceneName);
    }

    // Add methods to preload other scenes as needed
}
