package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

import com.socslingo.models.Flashcard;

public class RightSidebarListCell extends ListCell<Flashcard> {

    @FXML
    private HBox root;

    @FXML
    private Label frontLabel;

    @FXML
    private Label backLabel;

    public RightSidebarListCell() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/right_sidebar_list_view_item.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Flashcard item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            frontLabel.setText(item.getFront());
            backLabel.setText(item.getBack());
            setGraphic(root);

            HBox.setHgrow(root, Priority.ALWAYS);
            root.setMaxWidth(Double.MAX_VALUE);
        }
    }
}