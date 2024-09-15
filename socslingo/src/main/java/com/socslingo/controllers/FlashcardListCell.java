package com.socslingo.controllers;

import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import com.socslingo.models.Flashcard;

public class FlashcardListCell extends ListCell<Flashcard> {
    private HBox hbox = new HBox();
    private Label frontLabel = new Label();
    private Label backLabel = new Label();

    public FlashcardListCell() {
        super();
        frontLabel.getStyleClass().add("front-label");
        backLabel.getStyleClass().add("back-label");
        hbox.getChildren().addAll(frontLabel, backLabel);
        hbox.setSpacing(10); // Adjust spacing as needed
    }

    @Override
    protected void updateItem(Flashcard flashcard, boolean empty) {
        super.updateItem(flashcard, empty);
        if (empty || flashcard == null) {
            setText(null);
            setGraphic(null);
        } else {
            frontLabel.setText(flashcard.getFront());
            backLabel.setText(flashcard.getBack());
            setGraphic(hbox);
        }
    }
}