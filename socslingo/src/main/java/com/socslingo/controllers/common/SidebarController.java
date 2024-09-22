package com.socslingo.controllers.common;

import com.socslingo.controllers.BaseController;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import javafx.scene.control.Button;

/**
 * Controller class for managing the sidebar interactions.
 * <p>
 * This class handles the navigation between different scenes through the sidebar buttons,
 * manages the styling of active buttons, and controls the display of the "MORE" context menu.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class SidebarController extends BaseController {

    @FXML
    private Button switchToMainHomeFXMLButton;

    @FXML
    private Button switchToMainFlashcardFXMLButton;

    @FXML
    private Button kahootButton;

    @FXML
    private Button charactersButton;

    @FXML
    private Button createButton;

    @FXML
    private Button switchToProfileSceneButton;

    @FXML
    private Button moreButton;

    @FXML
    private ContextMenu moreMenu;

    private boolean canSwitch = true;
    private final PauseTransition cooldown = new PauseTransition(Duration.seconds(1));

    /**
     * Constructs a new {@code SidebarController}.
     */
    public SidebarController() {
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML
     * file has been loaded. It sets up event handlers and bindings for UI components.
     */
    @FXML
    public void initialize() {
        System.out.println("Initializing " + this.getClass().getSimpleName());
        System.out.println("Sidebar: " + this.sidebar);
        System.out.println("SidebarController: " + this.sidebarController);
        
        // Flag to track if the ContextMenu is being shown
        final boolean[] isContextMenuVisible = {false};

        // Show the ContextMenu when hovering over the "MORE" button
        moreButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!isContextMenuVisible[0]) {
                System.out.println("Mouse entered moreButton"); // Debug statement
                // Position the ContextMenu to the right of the moreButton
                Bounds moreButtonBounds = moreButton.localToScreen(moreButton.getBoundsInLocal());
                // Position the ContextMenu to the right of the moreButton
                moreMenu.show(moreButton, moreButtonBounds.getMaxX() + 10, moreButtonBounds.getMinY());
                isContextMenuVisible[0] = true;
            }
        });

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            moreMenu.hide();
            isContextMenuVisible[0] = false;
        });

        moreButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            System.out.println("Mouse exited moreButton"); // Debug statement
            pause.playFromStart();
        });

        moreMenu.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> pause.stop());
        moreMenu.addEventHandler(MouseEvent.MOUSE_EXITED, event -> pause.playFromStart());
    }

    /**
     * Handles the action of switching scenes when a sidebar button is clicked.
     * 
     * @param event the action event triggered by clicking a button
     */
    @FXML
    public void handleSceneSwitchAction(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String viewName = sourceButton.getId();

        System.out.println("Switching to view: " + viewName);

        // Call a method in App to switch scenes
        this.switchScene(viewName);
    }

    /**
     * Updates the styles of sidebar buttons based on the current active view.
     * <p>
     * This method highlights the button corresponding to the current view and resets the styles of other buttons.
     * </p>
     * 
     * @param currentView the identifier of the current active view
     */
    public void updateButtonStyles(String currentView) {
        System.out.println("Resetting button styles");
        resetButtonStyles();
        System.out.println("Current view: " + currentView);
        switch (currentView) {
            case "switchToMainHomeFXMLButton":
                System.out.println("Highlighting mainHome button");
                switchToMainHomeFXMLButton.getStyleClass().add("left-sidebar-button-active");
                break;
            case "switchToMainFlashcardFXMLButton":
                System.out.println("Highlighting mainFlashcard button");
                switchToMainFlashcardFXMLButton.getStyleClass().add("left-sidebar-button-active");
                break;
            case "kahoot":
                System.out.println("Highlighting kahoot button");
                kahootButton.getStyleClass().add("left-sidebar-button-active");
                break;
            case "characters":
                System.out.println("Highlighting characters button");
                charactersButton.getStyleClass().add("left-sidebar-button-active");
                break;
            case "create":  
                System.out.println("Highlighting create button");
                createButton.getStyleClass().add("left-sidebar-button-active");
                break;
            case "profile":
                System.out.println("Highlighting profile button");
                switchToProfileSceneButton.getStyleClass().add("left-sidebar-button-active");
                break;
            // Add other cases as needed
        }
    }

    /**
     * Resets the styles of all sidebar buttons to their default state.
     */
    private void resetButtonStyles() {
        System.out.println("Removing active style from all buttons");
        switchToMainHomeFXMLButton.getStyleClass().remove("left-sidebar-button-active");
        switchToMainFlashcardFXMLButton.getStyleClass().remove("left-sidebar-button-active");
        kahootButton.getStyleClass().remove("left-sidebar-button-active");
        charactersButton.getStyleClass().remove("left-sidebar-button-active");
        createButton.getStyleClass().remove("left-sidebar-button-active");
        switchToProfileSceneButton.getStyleClass().remove("left-sidebar-button-active");
        // Reset other buttons as needed
    }
}
