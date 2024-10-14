package com.socslingo.controllers;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.*;
import com.socslingo.models.*;
import com.socslingo.services.FlashcardService;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;

import javafx.scene.control.*;
import javafx.scene.input.*;

import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import javafx.util.Duration;

import org.slf4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DeckPreviewController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DeckPreviewController.class);

    @FXML
    private Label deck_name_label;

    @FXML
    private TextField deck_name_text_field;

    @FXML
    private Label front_label;

    @FXML
    private Label back_label;

    @FXML
    private StackPane flashcard_pane;

    @FXML
    private Button previous_flashcard_button;

    @FXML
    private Button next_flashcard_button;

    private List<Flashcard> flashcards;
    private int current_index = 0;

    private DeckDataAccess deck_data_access;
    private FlashcardService flashcard_service;

    private Deck current_deck;

    private ObservableList<Flashcard> all_user_flashcards;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseManager database_manager = DatabaseManager.getInstance();
            deck_data_access = new DeckDataAccess(database_manager);
            flashcard_service = new FlashcardService(new com.socslingo.dataAccess.FlashcardDataAccess(database_manager));
            logger.info("DeckDataAccess and FlashcardService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize services", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        all_user_flashcards = FXCollections.observableArrayList();
        try {
            int user_id = getCurrentUserId();
            List<Flashcard> user_flashcards = flashcard_service.getUserFlashcards(user_id);
            all_user_flashcards.setAll(user_flashcards);
            logger.info("Loaded {} flashcards for userId {}", user_flashcards.size(), user_id);
        } catch (Exception e) {
            logger.error("Failed to load all user flashcards", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load user flashcards.");
        }

        deck_name_label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                enterEditMode();
            }
        });

        deck_name_text_field.focusedProperty().addListener((observable, old_value, new_value) -> {
            if (!new_value) {
                exitEditMode();
            }
        });

        deck_name_text_field.setOnAction(event -> {
            exitEditMode();
        });

        deck_name_label.sceneProperty().addListener((obs, old_scene, new_scene) -> {
            if (new_scene != null) {
                new_scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();
                    if (deck_name_text_field.isVisible()) {
                        if (!isDescendant(deck_name_text_field, target) && target != deck_name_label) {
                            exitEditMode();
                        }
                    }
                });
            }
        });

        initializeDragAndDrop();
    }

    private double calculateTextWidth(String text, Font font) {
        Text temp_text = new Text(text);
        temp_text.setFont(font);
        return temp_text.getLayoutBounds().getWidth();
    }

    private void adjustTextFieldWidth() {
        String label_text = deck_name_label.getText();
        Font label_font = deck_name_label.getFont();
        double text_width = calculateTextWidth(label_text, label_font);
        double padding = 20; // Adjust padding as needed
        deck_name_text_field.setPrefWidth(text_width + padding);
    }

    private void initializeDragAndDrop() {
        flashcard_pane.setOnDragOver(event -> {
            if (event.getGestureSource() != flashcard_pane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        flashcard_pane.setOnDragEntered(event -> {
            if (event.getGestureSource() != flashcard_pane && event.getDragboard().hasString()) {
                flashcard_pane.getStyleClass().add("drag-over");
            }
            event.consume();
        });

        flashcard_pane.setOnDragExited(event -> {
            flashcard_pane.getStyleClass().remove("drag-over");
            event.consume();
        });

        flashcard_pane.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                try {
                    int flashcard_id = Integer.parseInt(dragboard.getString());
                    Flashcard flashcard = findFlashcardById(flashcard_id);
                    if (flashcard != null) {
                        addFlashcardToDeck(flashcard);
                        success = true;
                    } else {
                        logger.error("Flashcard with id {} not found.", flashcard_id);
                        showAlert(Alert.AlertType.ERROR, "Flashcard not found.");
                    }
                } catch (NumberFormatException e) {
                    logger.error("Invalid flashcard ID format: {}", dragboard.getString(), e);
                    showAlert(Alert.AlertType.ERROR, "Invalid flashcard data.");
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Flashcard findFlashcardById(int id) {
        for (Flashcard flashcard : all_user_flashcards) {
            if (flashcard.getId() == id) {
                return flashcard;
            }
        }
        return null;
    }

    private void addFlashcardToDeck(Flashcard flashcard) {
        if (isFlashcardInDeck(flashcard.getId())) {
            showAlert(Alert.AlertType.WARNING, "Flashcard is already in the deck.");
            return;
        }
        try {
            boolean success = deck_data_access.addFlashcardToDeck(current_deck.getDeckId(), flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {}", flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                flashcards = deck_data_access.getFlashcardsForDeck(current_deck.getDeckId());
                all_user_flashcards.remove(flashcard);
                if (!flashcards.isEmpty()) {
                    current_index = 0;
                    displayFlashcard(flashcards.get(current_index));
                }
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", flashcard.getId(),
                    current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while adding the flashcard to the deck.");
        }
    }

    private boolean isFlashcardInDeck(int flashcard_id) {
        return flashcards.stream().anyMatch(flashcard -> flashcard.getId() == flashcard_id);
    }

    public void setDeck(Deck deck) {
        this.current_deck = deck;
        deck_name_label.setText(deck.getDeckName());
        deck_name_text_field.setText(deck.getDeckName());
        adjustTextFieldWidth(); // Adjust TextField width based on Label

        try {
            flashcards = deck_data_access.getFlashcardsForDeck(deck.getDeckId());
            if (!flashcards.isEmpty()) {
                current_index = 0;
                displayFlashcard(flashcards.get(current_index));
            } else {
                front_label.setText("No flashcards in this deck.");
                back_label.setText("");
                next_flashcard_button.setDisable(true);
                previous_flashcard_button.setDisable(true);
            }
        } catch (Exception e) {
            logger.error("Error loading flashcards for deck: " + deck.getDeckName(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading flashcards.");
        }
    }

    private void enterEditMode() {
        deck_name_label.setVisible(false);
        deck_name_text_field.setVisible(true);
        deck_name_text_field.requestFocus();
        deck_name_text_field.selectAll();
        adjustTextFieldWidth(); // Adjust width based on current label text

        // Add listener to adjust width as the user types
        deck_name_text_field.textProperty().addListener((obs, old_text, new_text) -> {
            double text_width = calculateTextWidth(new_text, deck_name_text_field.getFont());
            double padding = 20; // Adjust padding as needed
            deck_name_text_field.setPrefWidth(text_width + padding);
        });
    }

    private void exitEditMode() {
        String new_deck_name = deck_name_text_field.getText().trim();
        if (!new_deck_name.isEmpty() && !new_deck_name.equals(current_deck.getDeckName())) {
            try {
                boolean success = deck_data_access.updateDeck(current_deck.getDeckId(), new_deck_name);
                if (success) {
                    logger.info("Deck name updated successfully to: " + new_deck_name);
                    current_deck.setDeckName(new_deck_name);
                    deck_name_label.setText(new_deck_name);
                    adjustTextFieldWidth(); // Adjust TextField width based on new label text
                    showAlert(Alert.AlertType.INFORMATION, "Deck name updated successfully.");
                } else {
                    logger.error("Failed to update deck name in the database.");
                    showAlert(Alert.AlertType.ERROR, "Failed to update deck name.");
                }
            } catch (Exception e) {
                logger.error("Error updating deck name", e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while updating the deck name.");
            }
        }
        deck_name_label.setVisible(true);
        deck_name_text_field.setVisible(false);

        // Remove the listener to prevent memory leaks
        deck_name_text_field.textProperty().removeListener(text_change_listener);
    }

    private void displayFlashcard(Flashcard flashcard) {
        front_label.setText(flashcard.getFront());
        back_label.setText(flashcard.getBack());
        front_label.setVisible(true);
        back_label.setVisible(false);

        flashcard_pane.setOnMouseClicked(event -> {
            boolean is_front_visible = front_label.isVisible();

            RotateTransition rotate_out_front = new RotateTransition(Duration.millis(150), front_label);
            rotate_out_front.setFromAngle(0);
            rotate_out_front.setToAngle(90);
            rotate_out_front.setAxis(Rotate.X_AXIS);

            RotateTransition rotate_in_back = new RotateTransition(Duration.millis(150), back_label);
            rotate_in_back.setFromAngle(-90);
            rotate_in_back.setToAngle(0);
            rotate_in_back.setAxis(Rotate.X_AXIS);

            RotateTransition rotate_out_back = new RotateTransition(Duration.millis(150), back_label);
            rotate_out_back.setFromAngle(0);
            rotate_out_back.setToAngle(90);
            rotate_out_back.setAxis(Rotate.X_AXIS);

            RotateTransition rotate_in_front = new RotateTransition(Duration.millis(150), front_label);
            rotate_in_front.setFromAngle(-90);
            rotate_in_front.setToAngle(0);
            rotate_in_front.setAxis(Rotate.X_AXIS);

            SequentialTransition flip_to_back = new SequentialTransition(rotate_out_front, rotate_in_back);
            SequentialTransition flip_to_front = new SequentialTransition(rotate_out_back, rotate_in_front);

            if (is_front_visible) {
                back_label.setVisible(true);
                flip_to_back.setOnFinished(e -> front_label.setVisible(false));
                flip_to_back.play();
            } else {
                front_label.setVisible(true);
                flip_to_front.setOnFinished(e -> back_label.setVisible(false));
                flip_to_front.play();
            }
        });
    }

    @FXML
    private void handleNextFlashcardAction(ActionEvent event) {
        if (flashcards != null && !flashcards.isEmpty()) {
            current_index = (current_index + 1) % flashcards.size();
            displayFlashcard(flashcards.get(current_index));
        }
    }

    @FXML
    private void handlePreviousFlashcardAction(ActionEvent event) {
        if (flashcards != null && !flashcards.isEmpty()) {
            current_index = (current_index - 1 + flashcards.size()) % flashcards.size();
            displayFlashcard(flashcards.get(current_index));
        }
    }

    @FXML
    private void handleBackToDeckManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/deck_management.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node deck_management_content = loader.load();

            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContentNode(deck_management_content);
                primary_controller.setActiveButton(null);
            } else {
                logger.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch to Deck Management.");
            }

        } catch (IOException e) {
            logger.error("Failed to load deck management view", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Deck Management view.");
        }
    }

    private void showAlert(Alert.AlertType alert_type, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
        Alert alert = new Alert(alert_type);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }

    private boolean isDescendant(Node parent, Node child) {
        while (child != null) {
            if (child == parent) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }

    private int getCurrentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }

    private ChangeListener<String> text_change_listener = (obs, old_text, new_text) -> {
        double text_width = calculateTextWidth(new_text, deck_name_text_field.getFont());
        double padding = 20; // Adjust padding as needed
        deck_name_text_field.setPrefWidth(text_width + padding);
    };
}