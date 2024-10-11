package com.socslingo.controllers;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.*;
import com.socslingo.models.*;
import com.socslingo.services.DeckService;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.Node;

import org.slf4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DeckFlashcardManagementController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeckFlashcardManagementController.class);

    @FXML
    private Label deck_name_label;

    @FXML
    private ListView<Flashcard> available_flashcards_list_view;

    @FXML
    private ListView<Flashcard> deck_flashcards_list_view;

    @FXML
    private Button add_flashcard_to_deck_button;

    @FXML
    private Button remove_flashcard_from_deck_button;

    private ObservableList<Flashcard> available_flashcards_observable_list;
    private ObservableList<Flashcard> deck_flashcards_observable_list;

    private Map<String, String> button_to_fxml_map;

    @FXML 
    private Button switch_to_deck_management_button;

    private Deck current_deck;

    private DeckService deck_service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Initializing DeckFlashcardManagementController");
        button_to_fxml_map = new HashMap<>();
        button_to_fxml_map.put("switch_to_deck_management_button", "/com/socslingo/views/deck_management.fxml");

        try {
            DatabaseManager db_manager = DatabaseManager.getInstance();
            deck_service = new DeckService(new DeckDataAccess(db_manager));
            LOGGER.info("DeckService initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize DeckService", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        available_flashcards_observable_list = FXCollections.observableArrayList();
        deck_flashcards_observable_list = FXCollections.observableArrayList();

        available_flashcards_list_view.setItems(available_flashcards_observable_list);
        deck_flashcards_list_view.setItems(deck_flashcards_observable_list);

        available_flashcards_list_view.setCellFactory(param -> new ListCell<Flashcard>() {
            @Override
            protected void updateItem(Flashcard item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Front: " + item.getFront() + " | Back: " + item.getBack());
                }
            }
        });

        deck_flashcards_list_view.setCellFactory(param -> new ListCell<Flashcard>() {
            @Override
            protected void updateItem(Flashcard item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Front: " + item.getFront() + " | Back: " + item.getBack());
                }
            }
        });

        initializeDragAndDrop(available_flashcards_list_view, deck_flashcards_list_view, true);
        initializeDragAndDrop(deck_flashcards_list_view, available_flashcards_list_view, false);

        LOGGER.info("FlashcardManagementController initialized successfully");
    }

    /**
     * Initializes drag-and-drop functionality between two ListViews.
     *
     * @param source The source ListView from which items are dragged.
     * @param target The target ListView to which items are dropped.
     * @param isAdding If true, dragging from available to deck; else, dragging from deck to available.
     */
    private void initializeDragAndDrop(ListView<Flashcard> source, ListView<Flashcard> target, boolean isAdding) {
        source.setOnDragDetected(event -> {
            Flashcard selected_flashcard = source.getSelectionModel().getSelectedItem();
            if (selected_flashcard == null) {
                return;
            }

            Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selected_flashcard.getId()));
            db.setContent(content);
            event.consume();
        });

        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        target.setOnDragEntered(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                target.getStyleClass().add("drag-over");
            }
            event.consume();
        });

        target.setOnDragExited(event -> {
            target.getStyleClass().remove("drag-over");
            event.consume();
        });

        target.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int flashcard_id = Integer.parseInt(db.getString());
                Flashcard flashcard = findFlashcardById(source.getItems(), flashcard_id);
                if (flashcard != null) {
                    if (isAdding) {
                        handleDragAddFlashcardToDeck(flashcard);
                    } else {
                        handleDragRemoveFlashcardFromDeck(flashcard);
                    }
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        source.setOnDragDone(event -> {
            event.consume();
        });
    }

    /**
     * Finds a Flashcard by its ID in a given list.
     *
     * @param list The list to search.
     * @param id The ID of the flashcard.
     * @return The Flashcard object if found; otherwise, null.
     */
    private Flashcard findFlashcardById(List<Flashcard> list, int id) {
        for (Flashcard fc : list) {
            if (fc.getId() == id) {
                return fc;
            }
        }
        return null;
    }

    /**
     * Handles adding a flashcard to the deck via drag-and-drop.
     *
     * @param flashcard The flashcard to add.
     */
    private void handleDragAddFlashcardToDeck(Flashcard flashcard) {
        try {
            boolean success = deck_service.addFlashcardToDeck(current_deck.getDeckId(), flashcard.getId());
            if (success) {
                LOGGER.info("FlashcardId: {} added to DeckId: {} via drag-and-drop", flashcard.getId(), current_deck.getDeckId());
                available_flashcards_observable_list.remove(flashcard);
                deck_flashcards_observable_list.add(flashcard);
            } else {
                LOGGER.error("Failed to add FlashcardId: {} to DeckId: {} via drag-and-drop", flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while adding FlashcardId: {} to DeckId: {} via drag-and-drop", 
                         flashcard.getId(), current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while adding the flashcard to the deck.");
        }
    }

    /**
     * Handles removing a flashcard from the deck via drag-and-drop.
     *
     * @param flashcard The flashcard to remove.
     */
    private void handleDragRemoveFlashcardFromDeck(Flashcard flashcard) {
        try {
            boolean success = deck_service.removeFlashcardFromDeck(current_deck.getDeckId(), flashcard.getId());
            if (success) {
                LOGGER.info("FlashcardId: {} removed from DeckId: {} via drag-and-drop", flashcard.getId(), current_deck.getDeckId());
                deck_flashcards_observable_list.remove(flashcard);
                available_flashcards_observable_list.add(flashcard);
            } else {
                LOGGER.error("Failed to remove FlashcardId: {} from DeckId: {} via drag-and-drop", flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while removing FlashcardId: {} from DeckId: {} via drag-and-drop", 
                         flashcard.getId(), current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while removing the flashcard from the deck.");
        }
    }
    
    public void setDeck(Deck deck) {
        this.current_deck = deck;
        deck_name_label.setText("Managing Flashcards for Deck: " + deck.getDeckName());
        loadFlashcards();
    }

    private void loadFlashcards() {
        if (current_deck == null) {
            LOGGER.warn("No deck selected. Cannot load flashcards.");
            showAlert(Alert.AlertType.ERROR, "No deck selected.");
            return;
        }

        LOGGER.debug("Loading flashcards for deckId: {}", current_deck.getDeckId());
        LOGGER.debug("Loading flashcards for userId: {}", current_deck.getUserId());

        try {
            List<Flashcard> available_flashcards = deck_service.getFlashcardsNotInDeck(current_deck.getUserId(), current_deck.getDeckId());
            LOGGER.info("Loaded {} available flashcards.", available_flashcards.size());
            available_flashcards_observable_list.setAll(available_flashcards);

            List<Flashcard> deck_flashcards = deck_service.getFlashcardsInDeck(current_deck.getDeckId());
            LOGGER.info("Loaded {} flashcards in deck.", deck_flashcards.size());
            deck_flashcards_observable_list.setAll(deck_flashcards);

        } catch (Exception e) {
            LOGGER.error("Exception occurred while loading flashcards for deckId: {}", current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading flashcards.");
        }
    }

    @FXML
    private void handleAddFlashcardToDeck(ActionEvent event) {
        Flashcard selected_flashcard = available_flashcards_list_view.getSelectionModel().getSelectedItem();

        LOGGER.debug("Attempting to add flashcardId: {}", 
                     selected_flashcard != null ? selected_flashcard.getId() : "null");

        if (selected_flashcard == null) {
            LOGGER.warn("Add operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to add.");
            return;
        }

        try {
            boolean success = deck_service.addFlashcardToDeck(current_deck.getDeckId(), selected_flashcard.getId());
            if (success) {
                LOGGER.info("FlashcardId: {} added to DeckId: {}", selected_flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                deck_flashcards_observable_list.add(selected_flashcard);
                available_flashcards_observable_list.remove(selected_flashcard);
            } else {
                LOGGER.error("Failed to add FlashcardId: {} to DeckId: {}", selected_flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", 
                         selected_flashcard.getId(), current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void handleRemoveFlashcardFromDeck(ActionEvent event) {
        Flashcard selected_flashcard = deck_flashcards_list_view.getSelectionModel().getSelectedItem();

        LOGGER.debug("Attempting to remove flashcardId: {}", 
                     selected_flashcard != null ? selected_flashcard.getId() : "null");

        if (selected_flashcard == null) {
            LOGGER.warn("Remove operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to remove.");
            return;
        }

        try {
            boolean success = deck_service.removeFlashcardFromDeck(current_deck.getDeckId(), selected_flashcard.getId());
            if (success) {
                LOGGER.info("FlashcardId: {} removed from DeckId: {}", selected_flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard removed from deck successfully.");
                available_flashcards_observable_list.add(selected_flashcard);
                deck_flashcards_observable_list.remove(selected_flashcard);
            } else {
                LOGGER.error("Failed to remove FlashcardId: {} from DeckId: {}", selected_flashcard.getId(), current_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", 
                         selected_flashcard.getId(), current_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void navigateBackToDeckManagement(ActionEvent event) {
        try {
            String fxml_path = "/com/socslingo/views/deck_management.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node new_content = loader.load();

            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContentNode(new_content);
                primary_controller.setActiveButton(primary_controller.getDeckManagementButton());
            } else {
                LOGGER.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }

        } catch (IOException e) {
            LOGGER.error("Failed to load deck_management.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Deck Management view.");
        }
    }

    private void showAlert(Alert.AlertType alert_type, String message) {
        LOGGER.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
        Alert alert = new Alert(alert_type);
        alert.setContentText(message);
        alert.showAndWait();
        LOGGER.debug("Alert displayed");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Button clicked_button = (Button) event.getSource();
        String fxml_path = button_to_fxml_map.get(clicked_button.getId());

        if (fxml_path != null) {
            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContent(fxml_path);
            } else {
                LOGGER.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }
        } else {
            LOGGER.error("No FXML mapping found for button ID: {}", clicked_button.getId());
            showAlert(Alert.AlertType.ERROR, "No FXML mapping found for button ID: " + clicked_button.getId());
        }
    }
}