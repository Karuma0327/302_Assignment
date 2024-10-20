package com.socslingo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import java.io.IOException;

import com.socslingo.managers.SceneManager;

public class MoreContextMenuController {

    @FXML
    private MenuItem settings_menu_item;

    @FXML
    private MenuItem help_menu_item;

    @FXML
    private MenuItem logout_menu_item;

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            PrimaryController.getInstance().switchContent("/com/socslingo/views/settings.fxml");
        } catch (IOException e) {
            e.printStackTrace(); // or handle the exception in a way that's appropriate for your application
        }
        PrimaryController.getInstance().setActiveButton(null);
        PrimaryController.getInstance().getMoreContextMenu().hide();
    }

    @FXML
    private void handleHelp(ActionEvent event) {
        try {
            PrimaryController.getInstance().switchContent("/com/socslingo/views/character_practice_activity_three_option.fxml");
        } catch (IOException e) {
            e.printStackTrace(); // or handle the exception in a way that's appropriate for your application
        }
        PrimaryController.getInstance().setActiveButton(null);
        PrimaryController.getInstance().getMoreContextMenu().hide();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Assuming clearUserSession() is a public method in PrimaryController
        PrimaryController.getInstance().clearUserSession();
        
        // Assuming SceneManager is a class in the same package or needs to be imported
        try {
            SceneManager.getInstance().switchToLogin();
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        
        PrimaryController.getInstance().getMoreContextMenu().hide();
    }
}
