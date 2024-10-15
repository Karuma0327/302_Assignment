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

    private void loadDefaultContent() {
        loadContent("/com/socslingo/views/default_sidebar_content.fxml");
    }

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
