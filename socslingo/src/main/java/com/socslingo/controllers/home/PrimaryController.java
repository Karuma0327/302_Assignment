package com.socslingo.controllers.home;

import com.socslingo.controllers.BaseController;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.ImageView;

import javafx.scene.layout.VBox;

/**
 * Controller class for managing the primary home screen interactions.
 * <p>
 * This class handles the initialization of the home screen, manages sidebar interactions,
 * and handles user actions such as logout and sidebar button clicks.
 * </p>
 * 
 * @version 1.0
 * @since 2024-09-18
 */
public class PrimaryController extends BaseController {

    @FXML
    private ImageView mascotImageView;

    @FXML
    private Button moreButton;

    @FXML
    private ContextMenu moreMenu;

    @FXML
    private VBox sidebarContainer;

    @FXML
    private Button leftSidebarButton;

    /**
     * Constructs a new {@code PrimaryController}.
     */
    public PrimaryController() {
        super();
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML
     * file has been loaded. It sets up the sidebar and logs initialization details.
     */
    @FXML
    public void initialize() {
        System.out.println("Initializing " + this.getClass().getSimpleName());
        if (sidebarContainer == null) {
            System.err.println("sidebarContainer is null in initialize of " + this.getClass().getSimpleName());
        } else {
            System.out.println("sidebarContainer is initialized.");
        }
        if (this.sidebar != null) {
            System.out.println("sidebar is not null, adding to sidebarContainer.");
            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(this.sidebar);
        } else {
            System.err.println("sidebar is null in initialize of " + this.getClass().getSimpleName());
        }
    }

    /**
     * Handles the logout action triggered by the user.
     * <p>
     * This method performs the necessary steps to log out the current user.
     * </p>
     */
    @FXML
    private void handleLogout() {
        // Add your logout logic here
        System.out.println("Logged out!");
    }

    /**
     * Handles the action when the left sidebar button is clicked.
     * <p>
     * This method performs the actions associated with the left sidebar button click.
     * </p>
     */
    @FXML
    public void handleLeftSidebarButtonAction() {
        // Add your button action logic here
        System.out.println("Left Sidebar Button Clicked!");
    }
}
