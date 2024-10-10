package com.socslingo.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import com.socslingo.managers.ControllerManager;
import java.io.IOException;

public class RightSidebarController {


    @FXML
    private VBox rightSidebar;

    @FXML
    private StackPane dynamicContent;

    private ContextMenu sidebarContextMenu;

    @FXML
    private void initialize() {
        setupContextMenu();
        loadDefaultContent();
    }


    @FXML
    private void handleDeckPreviewRightSidebar() {
        System.out.println("Deck Preview Right Sidebar selected");
        loadContent("/com/socslingo/views/deck_preview_right_sidebar.fxml");
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
        PrimaryController.getInstance().hideRightSidebar();
    }

    /**
     * Loads default content into the dynamicContent container.
     */
    private void loadDefaultContent() {
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
            loader.setControllerFactory(ControllerManager.getInstance());
            Node content = loader.load();
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
