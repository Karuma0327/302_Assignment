package com.socslingo.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.*;

import com.socslingo.dataAccess.*;
import com.socslingo.managers.*;
import com.socslingo.models.*;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class DeckController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DeckController.class);

    @FXML
    private TextField deck_name_text_field;

    @FXML
    private Button create_deck_button;

    @FXML
    private ListView<Deck> decks_list_view;

    @FXML
    private ListView<Flashcard> available_flashcards_list_view;

    @FXML
    private ListView<Flashcard> deck_flashcards_list_view;

    @FXML
    private Button add_flashcard_to_deck_button;

    @FXML
    private Button remove_flashcard_from_deck_button;

    @FXML
    private Label selected_deck_label;

    @FXML
    private Button delete_deck_button;

    private DeckDataAccess deck_data_access;
    private ObservableList<Deck> decks_observable_list;
    private ObservableList<Flashcard> available_flashcards_observable_list;
    private ObservableList<Flashcard> deck_flashcards_observable_list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing Deck_Controller");

        try {
            DatabaseManager db_manager = DatabaseManager.getInstance();
            deck_data_access = new DeckDataAccess(db_manager);
            new FlashcardDataAccess(db_manager);
            logger.info("Data Access Objects initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Data Access Objects", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        decks_observable_list = FXCollections.observableArrayList();
        available_flashcards_observable_list = FXCollections.observableArrayList();
        deck_flashcards_observable_list = FXCollections.observableArrayList();

        decks_list_view.setItems(decks_observable_list);
        available_flashcards_list_view.setItems(available_flashcards_observable_list);
        deck_flashcards_list_view.setItems(deck_flashcards_observable_list);

        available_flashcards_list_view.setCellFactory(new Callback<ListView<Flashcard>, ListCell<Flashcard>>() {
            @Override
            public ListCell<Flashcard> call(ListView<Flashcard> param) {
                return new ListCell<Flashcard>() {
                    @Override
                    protected void updateItem(Flashcard item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText("Front: " + item.getFront() + " | Back: " + item.getBack());
                        }
                    }
                };
            }
        });

        deck_flashcards_list_view.setCellFactory(new Callback<ListView<Flashcard>, ListCell<Flashcard>>() {
            @Override
            public ListCell<Flashcard> call(ListView<Flashcard> param) {
                return new ListCell<Flashcard>() {
                    @Override
                    protected void updateItem(Flashcard item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText("Front: " + item.getFront() + " | Back: " + item.getBack());
                        }
                    }
                };
            }
        });

        decks_list_view.setCellFactory(new Callback<ListView<Deck>, ListCell<Deck>>() {
            @Override
            public ListCell<Deck> call(ListView<Deck> param) {
                return new ListCell<Deck>() {
                    @Override
                    protected void updateItem(Deck item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getDeckName());
                        }
                    }
                };
            }
        });

        loadUserDecks();
        loadAvailableFlashcards();

        decks_list_view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            displaySelectedDeck(newValue);
        });

        decks_list_view.setOnMouseClicked(this::handleDeckDoubleClick);

        logger.info("Deck_Controller initialized successfully");
    }

    /**
     * Handles the creation of a new deck.
     *
     * @param event ActionEvent triggered by clicking the createDeckButton.
     */
    @FXML
    private void handleCreateDeck(ActionEvent event) {
        String deck_name = deck_name_text_field.getText().trim();
        logger.debug("Attempting to create deck with name: '{}'", deck_name);

        if (deck_name.isEmpty()) {
            logger.warn("Deck creation failed: Deck name is empty");
            showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
            return;
        }

        int user_id = SessionManager.getInstance().getCurrentUserId();
        if (user_id == -1) {
            logger.warn("Deck creation failed: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        String created_date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        logger.debug("Creating deck for userId: {} at {}", user_id, created_date);

        try {
            int deck_id = deck_data_access.createDeck(user_id, deck_name, created_date);
            if (deck_id != -1) {
                logger.info("Deck '{}' created successfully with ID: {}", deck_name, deck_id);
                showAlert(Alert.AlertType.INFORMATION, "Deck created successfully with ID: " + deck_id);
                deck_name_text_field.clear();
                loadUserDecks();
            } else {
                logger.error("Failed to create deck '{}'", deck_name);
                showAlert(Alert.AlertType.ERROR, "Failed to create deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating deck '{}'", deck_name, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while creating the deck.");
        }
    }

    /**
     * Loads all decks associated with the current user and populates the decksListView.
     */
    private void loadUserDecks() {
        logger.debug("Loading decks for current user");
        int user_id = SessionManager.getInstance().getCurrentUserId();
        if (user_id == -1) {
            logger.warn("Failed to load decks: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Deck> decks = deck_data_access.getUserDecks(user_id);
            decks_observable_list.setAll(decks);
            logger.info("Loaded {} decks for userId: {}", decks.size(), user_id);
        } catch (Exception e) {
            logger.error("Exception occurred while loading decks for userId: {}", user_id, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading decks.");
        }
    }

    /**
     * Loads all available flashcards (not associated with any deck) and populates the availableFlashcardsListView.
     */
    private void loadAvailableFlashcards() {
        logger.debug("Loading available flashcards for current user");
        int user_id = SessionManager.getInstance().getCurrentUserId();
        if (user_id == -1) {
            logger.warn("Failed to load flashcards: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Flashcard> available_flashcards = deck_data_access.getUnassignedFlashcardsForUser(user_id);
            available_flashcards_observable_list.setAll(available_flashcards);
            logger.info("Loaded {} available flashcards for userId: {}", available_flashcards.size(), user_id);
        } catch (Exception e) {
            logger.error("Exception occurred while loading available flashcards for userId: {}", user_id, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading available flashcards.");
        }
    }

    /**
     * Displays the selected deck and its associated flashcards.
     *
     * @param deck The selected Deck object.
     */
    private void displaySelectedDeck(Deck deck) {
        if (deck == null) {
            logger.debug("No deck selected. Displaying all available flashcards.");
            selected_deck_label.setText("No Deck Selected");
            deck_flashcards_list_view.setItems(FXCollections.observableArrayList());
            available_flashcards_list_view.setDisable(true);
            add_flashcard_to_deck_button.setDisable(true);
            remove_flashcard_from_deck_button.setDisable(true);
            return;
        }

        selected_deck_label.setText("Selected Deck: " + deck.getDeckName());
        logger.debug("Displaying flashcards for deckId: {}", deck.getDeckId());

        try {
            List<Flashcard> flashcards_in_deck = deck_data_access.getFlashcardsForDeck(deck.getDeckId());
            deck_flashcards_observable_list.setAll(flashcards_in_deck);
            logger.info("Loaded {} flashcards for deckId: {}", flashcards_in_deck.size(), deck.getDeckId());

            available_flashcards_list_view.setDisable(false);
            add_flashcard_to_deck_button.setDisable(false);
            remove_flashcard_from_deck_button.setDisable(false);
        } catch (Exception e) {
            logger.error("Exception occurred while loading flashcards for deckId: {}", deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading flashcards for the selected deck.");
        }
    }

    /**
     * Handles adding a selected flashcard to the selected deck.
     *
     * @param event ActionEvent triggered by clicking the addFlashcardToDeckButton.
     */
    @FXML
    private void handleAddFlashcardToDeck(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
        Flashcard selected_flashcard = available_flashcards_list_view.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to add flashcardId: {} to deckId: {}", 
                     selected_flashcard != null ? selected_flashcard.getId() : "null", 
                     selected_deck != null ? selected_deck.getDeckId() : "null");

        if (selected_deck == null) {
            logger.warn("Add operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck.");
            return;
        }

        if (selected_flashcard == null) {
            logger.warn("Add operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to add.");
            return;
        }

        try {
            boolean success = deck_data_access.addFlashcardToDeck(selected_deck.getDeckId(), selected_flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {}", selected_flashcard.getId(), selected_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                deck_flashcards_observable_list.add(selected_flashcard);
                available_flashcards_observable_list.remove(selected_flashcard);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", selected_flashcard.getId(), selected_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", 
                         selected_flashcard.getId(), selected_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while adding the flashcard to the deck.");
        }
    }

    /**
     * Handles removing a selected flashcard from the selected deck.
     *
     * @param event ActionEvent triggered by clicking the removeFlashcardFromDeckButton.
     */
    @FXML
    private void handleRemoveFlashcardFromDeck(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
        Flashcard selected_flashcard = deck_flashcards_list_view.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to remove flashcardId: {} from deckId: {}", 
                     selected_flashcard != null ? selected_flashcard.getId() : "null", 
                     selected_deck != null ? selected_deck.getDeckId() : "null");

        if (selected_deck == null) {
            logger.warn("Remove operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck.");
            return;
        }

        if (selected_flashcard == null) {
            logger.warn("Remove operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to remove.");
            return;
        }

        try {
            boolean success = deck_data_access.removeFlashcardFromDeck(selected_deck.getDeckId(), selected_flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {}", selected_flashcard.getId(), selected_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard removed from deck successfully.");
                deck_flashcards_observable_list.remove(selected_flashcard);
                available_flashcards_observable_list.add(selected_flashcard);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {}", selected_flashcard.getId(), selected_deck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", 
                         selected_flashcard.getId(), selected_deck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while removing the flashcard from the deck.");
        }
    }

    /**
     * Handles deleting the selected deck.
     *
     * @param event ActionEvent triggered by clicking the deleteDeckButton.
     */
    @FXML
    private void handleDeleteDeck(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to delete deckId: {}", 
                     selected_deck != null ? selected_deck.getDeckId() : "null");

        if (selected_deck == null) {
            logger.warn("Delete operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Deck");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the deck \"" + selected_deck.getDeckName() + "\"?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = deck_data_access.deleteDeck(selected_deck.getDeckId());

                if (success) {
                    logger.info("DeckId: {} deleted successfully.", selected_deck.getDeckId());
                    showAlert(Alert.AlertType.INFORMATION, "Deck deleted successfully.");
                    loadUserDecks();
                    selected_deck_label.setText("No Deck Selected");
                    deck_flashcards_list_view.setItems(FXCollections.observableArrayList());
                    available_flashcards_list_view.setDisable(true);
                    add_flashcard_to_deck_button.setDisable(true);
                    remove_flashcard_from_deck_button.setDisable(true);
                } else {
                    logger.error("Failed to delete DeckId: {}", selected_deck.getDeckId());
                    showAlert(Alert.AlertType.ERROR, "Failed to delete deck.");
                }
            } catch (Exception e) {
                logger.error("Exception occurred while deleting DeckId: {}", selected_deck.getDeckId(), e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while deleting the deck.");
            }
        } else {
            logger.info("Deck deletion cancelled by user.");
        }
    }

    /**
     * Handles double-click events on the decksListView.
     *
     * @param event MouseEvent triggered by double-clicking on a deck.
     */
    @FXML
    private void handleDeckDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
            if (selected_deck != null) {
                logger.info("DeckId: {} was double-clicked.", selected_deck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Double-clicked on deck: " + selected_deck.getDeckName());
            }
        }
    }

    /**
     * Utility method to display alerts.
     *
     * @param alertType Type of the alert.
     * @param message   Message to display.
     */
    private void showAlert(Alert.AlertType alert_type, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
        Alert alert = new Alert(alert_type);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }
}