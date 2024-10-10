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
    private static final Logger logger = LoggerFactory.getLogger(DeckFlashcardManagementController.class);

    @FXML
    private Label deckNameLabel;

    @FXML
    private ListView<Flashcard> availableFlashcardsListView;

    @FXML
    private ListView<Flashcard> deckFlashcardsListView;

    @FXML
    private Button addFlashcardToDeckButton;

    @FXML
    private Button removeFlashcardFromDeckButton;

    private ObservableList<Flashcard> availableFlashcardsObservableList;
    private ObservableList<Flashcard> deckFlashcardsObservableList;

    private Map<String, String> buttonToFXMLMap;

    @FXML 
    private Button switch_to_deck_management_button;

    private Deck currentDeck;

    private DeckService deckService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing DeckFlashcardManagementController");
        buttonToFXMLMap = new HashMap<>();
        buttonToFXMLMap.put("switch_to_deck_management_button", "/com/socslingo/views/deck_management.fxml");

        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            deckService = new DeckService(new DeckDataAccess(dbManager));
            logger.info("DeckService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize DeckService", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        availableFlashcardsObservableList = FXCollections.observableArrayList();
        deckFlashcardsObservableList = FXCollections.observableArrayList();

        availableFlashcardsListView.setItems(availableFlashcardsObservableList);
        deckFlashcardsListView.setItems(deckFlashcardsObservableList);

        availableFlashcardsListView.setCellFactory(param -> new ListCell<Flashcard>() {
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

        deckFlashcardsListView.setCellFactory(param -> new ListCell<Flashcard>() {
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

        // Initialize Drag-and-Drop for Available Flashcards ListView
        initializeDragAndDrop(availableFlashcardsListView, deckFlashcardsListView, true);

        // Initialize Drag-and-Drop for Deck Flashcards ListView
        initializeDragAndDrop(deckFlashcardsListView, availableFlashcardsListView, false);

        logger.info("FlashcardManagementController initialized successfully");
    }

    /**
     * Initializes drag-and-drop functionality between two ListViews.
     *
     * @param source The source ListView from which items are dragged.
     * @param target The target ListView to which items are dropped.
     * @param isAdding If true, dragging from available to deck; else, dragging from deck to available.
     */
    private void initializeDragAndDrop(ListView<Flashcard> source, ListView<Flashcard> target, boolean isAdding) {
        // Set up drag detected on source ListView
        source.setOnDragDetected(event -> {
            Flashcard selectedFlashcard = source.getSelectionModel().getSelectedItem();
            if (selectedFlashcard == null) {
                return;
            }

            Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selectedFlashcard.getId()));
            db.setContent(content);
            event.consume();
        });

        // Set up drag over on target ListView
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Set up drag entered to add 'drag-over' style
        target.setOnDragEntered(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                target.getStyleClass().add("drag-over");
            }
            event.consume();
        });

        // Set up drag exited to remove 'drag-over' style
        target.setOnDragExited(event -> {
            target.getStyleClass().remove("drag-over");
            event.consume();
        });

        // Set up drag dropped on target ListView
        target.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int flashcardId = Integer.parseInt(db.getString());
                Flashcard flashcard = findFlashcardById(source.getItems(), flashcardId);
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

        // Optional: Handle drag done if you need to perform additional actions
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
            boolean success = deckService.addFlashcardToDeck(currentDeck.getDeckId(), flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {} via drag-and-drop", flashcard.getId(), currentDeck.getDeckId());
                availableFlashcardsObservableList.remove(flashcard);
                deckFlashcardsObservableList.add(flashcard);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {} via drag-and-drop", flashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {} via drag-and-drop", 
                         flashcard.getId(), currentDeck.getDeckId(), e);
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
            boolean success = deckService.removeFlashcardFromDeck(currentDeck.getDeckId(), flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {} via drag-and-drop", flashcard.getId(), currentDeck.getDeckId());
                deckFlashcardsObservableList.remove(flashcard);
                availableFlashcardsObservableList.add(flashcard);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {} via drag-and-drop", flashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {} via drag-and-drop", 
                         flashcard.getId(), currentDeck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while removing the flashcard from the deck.");
        }
    }
    
    public void setDeck(Deck deck) {
        this.currentDeck = deck;
        deckNameLabel.setText("Managing Flashcards for Deck: " + deck.getDeckName());
        loadFlashcards();
    }

    private void loadFlashcards() {
        if (currentDeck == null) {
            logger.warn("No deck selected. Cannot load flashcards.");
            showAlert(Alert.AlertType.ERROR, "No deck selected.");
            return;
        }

        logger.debug("Loading flashcards for deckId: {}", currentDeck.getDeckId());
        logger.debug("Loading flashcards for userId: {}", currentDeck.getUserId());

        try {
            List<Flashcard> availableFlashcards = deckService.getFlashcardsNotInDeck(currentDeck.getUserId(), currentDeck.getDeckId());
            logger.info("Loaded {} available flashcards.", availableFlashcards.size());
            availableFlashcardsObservableList.setAll(availableFlashcards);

            List<Flashcard> deckFlashcards = deckService.getFlashcardsInDeck(currentDeck.getDeckId());
            logger.info("Loaded {} flashcards in deck.", deckFlashcards.size());
            deckFlashcardsObservableList.setAll(deckFlashcards);

        } catch (Exception e) {
            logger.error("Exception occurred while loading flashcards for deckId: {}", currentDeck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading flashcards.");
        }
    }

    @FXML
    private void handleAddFlashcardToDeck(ActionEvent event) {
        Flashcard selectedFlashcard = availableFlashcardsListView.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to add flashcardId: {}", 
                     selectedFlashcard != null ? selectedFlashcard.getId() : "null");

        if (selectedFlashcard == null) {
            logger.warn("Add operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to add.");
            return;
        }

        try {
            boolean success = deckService.addFlashcardToDeck(currentDeck.getDeckId(), selectedFlashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {}", selectedFlashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                deckFlashcardsObservableList.add(selectedFlashcard);
                availableFlashcardsObservableList.remove(selectedFlashcard);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", selectedFlashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", 
                         selectedFlashcard.getId(), currentDeck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void handleRemoveFlashcardFromDeck(ActionEvent event) {
        Flashcard selectedFlashcard = deckFlashcardsListView.getSelectionModel().getSelectedItem();

        logger.debug("Attempting to remove flashcardId: {}", 
                     selectedFlashcard != null ? selectedFlashcard.getId() : "null");

        if (selectedFlashcard == null) {
            logger.warn("Remove operation failed: No flashcard selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a flashcard to remove.");
            return;
        }

        try {
            boolean success = deckService.removeFlashcardFromDeck(currentDeck.getDeckId(), selectedFlashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {}", selectedFlashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard removed from deck successfully.");
                availableFlashcardsObservableList.add(selectedFlashcard);
                deckFlashcardsObservableList.remove(selectedFlashcard);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {}", selectedFlashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to remove flashcard from deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", 
                         selectedFlashcard.getId(), currentDeck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void navigateBackToDeckManagement(ActionEvent event) {
        try {
            String fxmlPath = "/com/socslingo/views/deck_management.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node newContent = loader.load();

            // Use the PrimaryController to switch content
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContentNode(newContent);
                primaryController.setActiveButton(primaryController.getDeckManagementButton());
            } else {
                logger.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }

        } catch (IOException e) {
            logger.error("Failed to load deck_management.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Deck Management view.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String fxmlPath = buttonToFXMLMap.get(clickedButton.getId());

        if (fxmlPath != null) {
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContent(fxmlPath);
            } else {
                System.out.println("PrimaryController instance is null.");
            }
        } else {
            System.out.println("No FXML mapping found for button ID: " + clickedButton.getId());
        }
    }
}
