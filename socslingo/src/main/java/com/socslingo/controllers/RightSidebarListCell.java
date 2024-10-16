// src/main/java/com/socslingo/controllers/RightSidebarListCell.java
package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

import com.socslingo.models.Flashcard;

/**
 * Custom ListCell for RightSidebar ListView.
 */
public class RightSidebarListCell extends ListCell<Flashcard> {

    @FXML
    private HBox root; // Root container from FXML

    @FXML
    private Label frontLabel; // Label for the front of the flashcard

    @FXML
    private Label backLabel; // Label for the back of the flashcard

    /**
     * Constructor that loads the FXML layout.
     */
    public RightSidebarListCell() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/right_sidebar_list_view_item.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    /**
     * Updates the content of the cell.
     *
     * @param item  The Flashcard to display.
     * @param empty Indicates whether the cell is empty.
     */
    @Override
    protected void updateItem(Flashcard item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            frontLabel.setText(item.getFront());
            backLabel.setText(item.getBack()); // Set the actual back text
            setGraphic(root);

            // Ensure the cell fills the entire width of the ListView
            HBox.setHgrow(root, Priority.ALWAYS);
            root.setMaxWidth(Double.MAX_VALUE);
        }
    }
}