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


    // Initialize Logger
    private static final Logger logger = LoggerFactory.getLogger(DeckController.class);

    // UI Components
    @FXML
    private TextField deck_name_text_field;

    @FXML
    private Button createDeckButton;

    @FXML
    private ListView<Deck> decksListView;

    @FXML
    private ListView<Flashcard> availableFlashcardsListView;

    @FXML
    private ListView<Flashcard> deckFlashcardsListView;

    @FXML
    private Button addFlashcardToDeckButton;

    @FXML
    private Button removeFlashcardFromDeckButton;

    @FXML
    private Label selectedDeckLabel;

    @FXML
    private Button deleteDeckButton; // Optional: For deleting decks

    // Data Access
    private DeckDataAccess deckDataAccess;

    // Observable Lists for UI
    private ObservableList<Deck> decksObservableList;
    private ObservableList<Flashcard> availableFlashcardsObservableList;
    private ObservableList<Flashcard> deckFlashcardsObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing Deck_Controller");

        // Initialize Data Access Objects
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            deckDataAccess = new DeckDataAccess(dbManager);
            new FlashcardDataAccess(dbManager);
            logger.info("Data Access Objects initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Data Access Objects", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        // Initialize Observable Lists
        decksObservableList = FXCollections.observableArrayList();
        availableFlashcardsObservableList = FXCollections.observableArrayList();
        deckFlashcardsObservableList = FXCollections.observableArrayList();

        // Set up ListViews
        decksListView.setItems(decksObservableList);
        availableFlashcardsListView.setItems(availableFlashcardsObservableList);
        deckFlashcardsListView.setItems(deckFlashcardsObservableList);

        // Customize ListView Cells for Flashcards
        availableFlashcardsListView.setCellFactory(new Callback<ListView<Flashcard>, ListCell<Flashcard>>() {
            @Override
            public ListCell<Flashcard> call(ListView<Flashcard> param) {
                return new ListCell<Flashcard>() {
                    @Override
                    protected void updateItem(Flashcard item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Display only the front and back text for clarity
                            setText("Front: " + item.getFront() + " | Back: " + item.getBack());
                        }
                    }
                };
            }
        });

        deckFlashcardsListView.setCellFactory(new Callback<ListView<Flashcard>, ListCell<Flashcard>>() {
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

        // Customize ListView Cells for Decks
        decksListView.setCellFactory(new Callback<ListView<Deck>, ListCell<Deck>>() {
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

        // Load initial data
        loadUserDecks();
        loadAvailableFlashcards();

        // Add listener to decksListView to display selected deck's flashcards
        decksListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            displaySelectedDeck(newValue);
        });

        // Optionally, set up double-click handler for decksListView
        decksListView.setOnMouseClicked(this::handleDeckDoubleClick);

        logger.info("Deck_Controller initialized successfully");
    }

    /**
     * Handles the creation of a new deck.
     *
     * @param event ActionEvent triggered by clicking the createDeckButton.
     */
    @FXML
    private void handleCreateDeck(ActionEvent event) {
        String deckName = deck_name_text_field.getText().trim();
        logger.debug("Attempting to create deck with name: '{}'", deckName);

        if (deckName.isEmpty()) {
            logger.warn("Deck creation failed: Deck name is empty");
            showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
            return;
        }

        // Retrieve the current user's ID using SessionManager
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("Deck creation failed: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        String createdDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        logger.debug("Creating deck for userId: {} at {}", userId, createdDate);

        try {
            int deckId = deckDataAccess.createDeck(userId, deckName, createdDate);
            if (deckId != -1) {
                logger.info("Deck '{}' created successfully with ID: {}", deckName, deckId);
                showAlert(Alert.AlertType.INFORMATION, "Deck created successfully with ID: " + deckId);
                deck_name_text_field.clear();
                loadUserDecks();
            } else {
                logger.error("Failed to create deck '{}'", deckName);
                showAlert(Alert.AlertType.ERROR, "Failed to create deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating deck '{}'", deckName, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while creating the deck.");
        }
    }

    /**
     * Loads all decks associated with the current user and populates the decksListView.
     */
    private void loadUserDecks() {
        logger.debug("Loading decks for current user");
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("Failed to load decks: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Deck> decks = deckDataAccess.getUserDecks(userId);
            decksObservableList.setAll(decks);
            logger.info("Loaded {} decks for userId: {}", decks.size(), userId);
        } catch (Exception e) {
            logger.error("Exception occurred while loading decks for userId: {}", userId, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading decks.");
        }
    }

    /**
     * Loads all available flashcards (not associated with any deck) and populates the availableFlashcardsListView.
     */
    private void loadAvailableFlashcards() {
        logger.debug("Loading available flashcards for current user");
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("Failed to load flashcards: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Flashcard> availableFlashcards = deckDataAccess.getFlashcardsNotInAnyDeck1(userId);
            availableFlashcardsObservableList.setAll(availableFlashcards);
            logger.info("Loaded {} available flashcards for userId: {}", availableFlashcards.size(), userId);
        } catch (Exception e) {
            logger.error("Exception occurred while loading available flashcards for userId: {}", userId, e);
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
            selectedDeckLabel.setText("No Deck Selected");
            deckFlashcardsListView.setItems(FXCollections.observableArrayList());
            availableFlashcardsListView.setDisable(true);
            addFlashcardToDeckButton.setDisable(true);
            removeFlashcardFromDeckButton.setDisable(true);
            return;
        }

        selectedDeckLabel.setText("Selected Deck: " + deck.getDeckName());
        logger.debug("Displaying flashcards for deckId: {}", deck.getDeckId());

        try {
            List<Flashcard> flashcardsInDeck = deckDataAccess.getFlashcardsInDeck(deck.getDeckId());
            deckFlashcardsObservableList.setAll(flashcardsInDeck);
            logger.info("Loaded {} flashcards for deckId: {}", flashcardsInDeck.size(), deck.getDeckId());

            // Enable add/remove buttons and available flashcards list
            availableFlashcardsListView.setDisable(false);
            addFlashcardToDeckButton.setDisable(false);
            removeFlashcardFromDeckButton.setDisable(false);
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
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        Flashcard selectedFlashcard = availableFlashcardsListView.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to add flashcardId: {} to deckId: {}", 
                     selectedFlashcard != null ? selectedFlashcard.getId() : "null", 
                     selectedDeck != null ? selectedDeck.getDeckId() : "null");

        if (selectedDeck == null) {
            logger.warn("Add operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck.");
            return;
        }

        if (selectedFlashcard == null) {
            logger.warn("Add operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to add.");
            return;
        }

        try {
            boolean success = deckDataAccess.addFlashcardToDeck(selectedDeck.getDeckId(), selectedFlashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {}", selectedFlashcard.getId(), selectedDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                deckFlashcardsObservableList.add(selectedFlashcard);
                availableFlashcardsObservableList.remove(selectedFlashcard);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", selectedFlashcard.getId(), selectedDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", 
                         selectedFlashcard.getId(), selectedDeck.getDeckId(), e);
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
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        Flashcard selectedFlashcard = deckFlashcardsListView.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to remove flashcardId: {} from deckId: {}", 
                     selectedFlashcard != null ? selectedFlashcard.getId() : "null", 
                     selectedDeck != null ? selectedDeck.getDeckId() : "null");

        if (selectedDeck == null) {
            logger.warn("Remove operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck.");
            return;
        }

        if (selectedFlashcard == null) {
            logger.warn("Remove operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to remove.");
            return;
        }

        try {
            boolean success = deckDataAccess.removeFlashcardFromDeck(selectedDeck.getDeckId(), selectedFlashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {}", selectedFlashcard.getId(), selectedDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard removed from deck successfully.");
                deckFlashcardsObservableList.remove(selectedFlashcard);
                availableFlashcardsObservableList.add(selectedFlashcard);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {}", selectedFlashcard.getId(), selectedDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", 
                         selectedFlashcard.getId(), selectedDeck.getDeckId(), e);
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
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to delete deckId: {}", 
                     selectedDeck != null ? selectedDeck.getDeckId() : "null");

        if (selectedDeck == null) {
            logger.warn("Delete operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Deck");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the deck \"" + selectedDeck.getDeckName() + "\"?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = deckDataAccess.deleteDeck(selectedDeck.getDeckId());

                if (success) {
                    logger.info("DeckId: {} deleted successfully.", selectedDeck.getDeckId());
                    showAlert(Alert.AlertType.INFORMATION, "Deck deleted successfully.");
                    loadUserDecks();
                    selectedDeckLabel.setText("No Deck Selected");
                    deckFlashcardsListView.setItems(FXCollections.observableArrayList());
                    availableFlashcardsListView.setDisable(true);
                    addFlashcardToDeckButton.setDisable(true);
                    removeFlashcardFromDeckButton.setDisable(true);
                } else {
                    logger.error("Failed to delete DeckId: {}", selectedDeck.getDeckId());
                    showAlert(Alert.AlertType.ERROR, "Failed to delete deck.");
                }
            } catch (Exception e) {
                logger.error("Exception occurred while deleting DeckId: {}", selectedDeck.getDeckId(), e);
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
            Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
            if (selectedDeck != null) {
                logger.info("DeckId: {} was double-clicked.", selectedDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Double-clicked on deck: " + selectedDeck.getDeckName());
            }
        }
    }

    /**
     * Utility method to display alerts.
     *
     * @param alertType Type of the alert.
     * @param message   Message to display.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }
}
