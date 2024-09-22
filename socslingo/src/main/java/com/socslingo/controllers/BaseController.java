package com.socslingo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.ContextMenu;
import com.socslingo.App;
import com.socslingo.controllers.common.SidebarController;
import com.socslingo.managers.ControllerManager;

import java.io.IOException;

/**
 * Base controller class providing common functionalities for all controllers in the application.
 * <p>
 * This class handles the initialization of the sidebar, scene switching, and provides utility methods
 * for retrieving screen dimensions. Subclasses can extend this class to inherit these functionalities.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class BaseController {
    
    /**
     * Default constructor for BaseController.
     */
    public BaseController() {
        // Default constructor
    }
    /**
     * The sidebar UI component.
     */
    protected VBox sidebar;

    /**
     * The controller for the sidebar.
     */
    protected SidebarController sidebarController;

    /**
     * Initializes the controller. Subclasses can override this method to perform additional initialization.
     */
    @FXML
    public void initialize() {
        // Default implementation; can be overridden by subclasses
    }
    
    /**
     * Sets the sidebar UI component.
     * 
     * @param sidebar the {@code VBox} representing the sidebar
     */
    public void setSidebar(VBox sidebar) {
        this.sidebar = sidebar;
    }

    /**
     * Sets the sidebar controller.
     * 
     * @param sidebarController the {@code SidebarController} instance
     */
    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    /**
     * Switches the current scene to the specified view.
     * <p>
     * This method loads the FXML and CSS files corresponding to the provided view name,
     * sets up the controller, and updates the primary stage with the new scene.
     * It also updates the sidebar button styles to reflect the active view.
     * </p>
     * 
     * @param viewName the identifier of the view to switch to
     */
    protected void switchScene(String viewName) {
        try {
            String fxmlPath = "";
            String cssPath = "";

            switch (viewName) {
                case "switchToFlashcardDeckPageButton":
                    fxmlPath = "/com/socslingo/views/flashcardDeck.fxml";
                    cssPath = "/com/socslingo/css/flashcardDeck.css";
                    break;
                case "switchToCreateFlashcardFXMLButton":
                    fxmlPath = "/com/socslingo/views/createFlashcard.fxml";
                    cssPath = "/com/socslingo/css/createFlashcard.css";
                    break;
                case "switchToLoginFXMLButton":
                    fxmlPath = "/com/socslingo/views/login.fxml";
                    cssPath = "/com/socslingo/css/login.css";
                    break;
                case "switchToRegistrationFXMLButton":
                    fxmlPath = "/com/socslingo/views/registration.fxml";
                    cssPath = "/com/socslingo/css/registration.css";
                    break;
                case "switchToViewFlashcardFXMLButton":
                    fxmlPath = "/com/socslingo/views/viewFlashcard.fxml";
                    cssPath = "/com/socslingo/css/viewFlashcard.css";
                    break;
                case "switchToMainFlashcardFXMLButton":
                    fxmlPath = "/com/socslingo/views/mainFlashcard.fxml";
                    cssPath = "/com/socslingo/css/mainFlashcard.css";
                    break;
                case "switchToMainHomeFXMLButton":
                    fxmlPath = "/com/socslingo/views/mainHome.fxml";
                    cssPath = "/com/socslingo/css/mainHome.css";
                    break;
                case "switchToProfileSceneButton":
                    fxmlPath = "/com/socslingo/views/profile.fxml";
                    cssPath = "/com/socslingo/css/profile.css";
                    break;
                default:
                    System.err.println("Unknown view: " + viewName);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance());
            Parent root = loader.load();

            // After loading the controller, set sidebar and sidebarController
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                BaseController baseController = (BaseController) controller;
                baseController.setSidebar(App.getInstance().getSidebar());
                baseController.setSidebarController(App.getInstance().getSidebarController());
            }

            double width = getScreenWidth() * 0.95;
            double height = getScreenHeight() * 0.95;

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

            Stage primaryStage = App.getInstance().getPrimaryStage();
            primaryStage.setScene(scene);

            // Update the sidebar styles
            if (App.getInstance().getSidebarController() != null) {
                App.getInstance().getSidebarController().updateButtonStyles(viewName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of switching scenes when a button is clicked.
     * 
     * @param event the action event triggered by clicking a button
     */
    @FXML
    protected void handleSceneSwitchAction(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String viewName = sourceButton.getId();
        System.out.println("Switching to view: " + viewName);
        this.switchScene(viewName);
    }

    /**
     * Retrieves the primary screen's width.
     * 
     * @return the width of the primary screen
     */
    protected double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    /**
     * Retrieves the primary screen's height.
     * 
     * @return the height of the primary screen
     */
    protected double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
}
