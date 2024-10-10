package com.socslingo.controllers;

import java.io.IOException;

import com.socslingo.managers.ControllerManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RightSidebarController {

    @FXML
    private Button option1Button;

    @FXML
    private Button option2Button;

    @FXML
    private Button option3Button;

    @FXML
    private VBox rightSidebar;

    @FXML
    private StackPane dynamicContent; // Dynamic content container

    private ContextMenu sidebarContextMenu;

    @FXML
    private void initialize() {
        setupContextMenu();
        loadDefaultContent(); // Load initial content if necessary
    }

    @FXML
    private void handleOption1() {
        System.out.println("Option 1 selected");
        loadContent("/com/socslingo/views/option1_content.fxml");
    }

    @FXML
    private void handleOption2() {
        System.out.println("Option 2 selected");
        loadContent("/com/socslingo/views/option2_content.fxml");
    }

    @FXML
    private void handleOption3() {
        System.out.println("Option 3 selected");
        loadContent("/com/socslingo/views/option3_content.fxml");
    }

    private void setupContextMenu() {
        sidebarContextMenu = new ContextMenu();
        MenuItem hideSidebarItem = new MenuItem("Hide Sidebar");
        hideSidebarItem.setOnAction(e -> handleHideSidebar());
        sidebarContextMenu.getItems().add(hideSidebarItem);

        rightSidebar.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                sidebarContextMenu.show(rightSidebar, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void handleHideSidebar() {
        // Call the method in PrimaryController to hide the sidebar
        PrimaryController.getInstance().hideRightSidebar();
    }

    /**
     * Loads default content into the dynamicContent container.
     */
    private void loadDefaultContent() {
        // Optionally load default content on initialization
        // For example, load a welcome view or instructions
        loadContent("/com/socslingo/views/default_sidebar_content.fxml");
    }

    /**
     * Loads the specified FXML into the dynamicContent container.
     *
     * @param fxmlPath The path to the FXML file to load.
     */
    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controller factory is set
            Node content = loader.load();
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert or log the error
        }
    }
}
