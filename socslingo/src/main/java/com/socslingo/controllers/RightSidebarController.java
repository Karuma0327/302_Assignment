package com.socslingo.controllers;

import java.io.IOException;

import com.socslingo.managers.ControllerManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RightSidebarController {

    @FXML
    private VBox right_sidebar;

    @FXML
    private StackPane dynamic_content;

    private ContextMenu sidebar_context_menu;

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
        sidebar_context_menu = new ContextMenu();
        MenuItem hide_sidebar_item = new MenuItem("Hide Sidebar");
        hide_sidebar_item.setOnAction(e -> handleHideSidebar());
        sidebar_context_menu.getItems().add(hide_sidebar_item);

        right_sidebar.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                sidebar_context_menu.show(right_sidebar, event.getScreenX(), event.getScreenY());
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
    public void loadContent(String fxml_path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node content = loader.load();
            dynamic_content.getChildren().clear();
            dynamic_content.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
