package com.socslingo.controllers.flashcard;

import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import com.socslingo.models.Flashcard;

/**
 * Custom ListCell implementation for displaying Flashcard objects in a ListView.
 * <p>
 * Each cell displays the front and back text of a flashcard. Clicking on the cell toggles
 * the visibility between the front and back text.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class FlashcardListCell extends ListCell<Flashcard> {
    private HBox hbox = new HBox();
    private Label frontLabel = new Label();
    private Label backLabel = new Label();
    private StackPane stackPane = new StackPane();

    /**
     * Constructs a new {@code FlashcardListCell} and initializes the UI components.
     */
    public FlashcardListCell() {
        super();
        stackPane.getChildren().addAll(frontLabel, backLabel);
        backLabel.setVisible(false);
        hbox.getChildren().add(stackPane);
        hbox.setSpacing(10); // Adjust spacing as needed

        stackPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();
            frontLabel.setVisible(!isFrontVisible);
            backLabel.setVisible(isFrontVisible);
        });
    }

    /**
     * Updates the content of the cell to display the given {@code Flashcard}.
     * <p>
     * If the cell is empty or the flashcard is {@code null}, the cell is cleared.
     * Otherwise, the front and back text of the flashcard are set to the respective labels.
     * </p>
     *
     * @param flashcard the {@code Flashcard} to display
     * @param empty     {@code true} if the cell is empty; {@code false} otherwise
     */
    @Override
    protected void updateItem(Flashcard flashcard, boolean empty) {
        super.updateItem(flashcard, empty);
        if (empty || flashcard == null) {
            setText(null);
            setGraphic(null);
        } else {
            frontLabel.setText(flashcard.getFront());
            backLabel.setText(flashcard.getBack());
            frontLabel.setVisible(true);
            backLabel.setVisible(false);
            setGraphic(hbox);
        }
    }
}
