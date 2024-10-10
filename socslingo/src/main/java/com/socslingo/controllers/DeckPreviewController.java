package com.socslingo.controllers;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.*;
import com.socslingo.models.*;
import com.socslingo.services.FlashcardService;

import javafx.animation.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.transform.*;
import javafx.util.Duration;

import org.slf4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DeckPreviewController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DeckPreviewController.class);

    @FXML
    private Label deckNameLabel;

    @FXML
    private TextField deckNameTextField;

    @FXML
    private Label frontLabel;

    @FXML
    private Label backLabel;

    @FXML
    private StackPane flashcardPane;

    @FXML
    private Button previousFlashcardButton;

    @FXML
    private Button nextFlashcardButton;

    private List<Flashcard> flashcards;
    private int currentIndex = 0;

    private DeckDataAccess deckDataAccess;
    private FlashcardService flashcardService;

    private Deck currentDeck;

    private ObservableList<Flashcard> allUserFlashcards;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize Data Access Objects
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            deckDataAccess = new DeckDataAccess(dbManager);
            flashcardService = new FlashcardService(new com.socslingo.dataAccess.FlashcardDataAccess(dbManager));
            logger.info("DeckDataAccess and FlashcardService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize services", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        allUserFlashcards = FXCollections.observableArrayList();
        try {
            int userId = getCurrentUserId();
            List<Flashcard> userFlashcards = flashcardService.getUserFlashcards(userId);
            allUserFlashcards.setAll(userFlashcards);
            logger.info("Loaded {} flashcards for userId {}", userFlashcards.size(), userId);
        } catch (Exception e) {
            logger.error("Failed to load all user flashcards", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load user flashcards.");
        }

        // Set up double-click on deckNameLabel to enter edit mode
        deckNameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                enterEditMode();
            }
        });

        // Handle when the TextField loses focus to save the new name
        deckNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                exitEditMode();
            }
        });

        // Handle pressing Enter in the TextField to save the new name
        deckNameTextField.setOnAction(event -> {
            exitEditMode();
        });

        // Add a global mouse click listener to detect clicks outside the TextField
        // This ensures that clicking anywhere in the application will trigger exitEditMode
        // when in edit mode
        // We attach the listener once the scene is available
        deckNameLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();
                    if (deckNameTextField.isVisible()) {
                        if (!isDescendant(deckNameTextField, target) && target != deckNameLabel) {
                            exitEditMode();
                        }
                    }
                });
            }
        });

        initializeDragAndDrop();
    }

    private void initializeDragAndDrop() {
        flashcardPane.setOnDragOver(event -> {
            if (event.getGestureSource() != flashcardPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        flashcardPane.setOnDragEntered(event -> {
            if (event.getGestureSource() != flashcardPane && event.getDragboard().hasString()) {
                flashcardPane.getStyleClass().add("drag-over");
            }
            event.consume();
        });

        flashcardPane.setOnDragExited(event -> {
            flashcardPane.getStyleClass().remove("drag-over");
            event.consume();
        });

        flashcardPane.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                try {
                    int flashcardId = Integer.parseInt(db.getString());
                    Flashcard flashcard = findFlashcardById(flashcardId);
                    if (flashcard != null) {
                        addFlashcardToDeck(flashcard);
                        success = true;
                    } else {
                        logger.error("Flashcard with id {} not found.", flashcardId);
                        showAlert(Alert.AlertType.ERROR, "Flashcard not found.");
                    }
                } catch (NumberFormatException e) {
                    logger.error("Invalid flashcard ID format: {}", db.getString(), e);
                    showAlert(Alert.AlertType.ERROR, "Invalid flashcard data.");
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Flashcard findFlashcardById(int id) {
        for (Flashcard fc : allUserFlashcards) {
            if (fc.getId() == id) {
                return fc;
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
            boolean success = deckDataAccess.addFlashcardToDeck(currentDeck.getDeckId(), flashcard.getId());
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {}", flashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added to deck successfully.");
                flashcards = deckDataAccess.getFlashcardsInDeck(currentDeck.getDeckId());
                allUserFlashcards.remove(flashcard);
                if (!flashcards.isEmpty()) {
                    currentIndex = 0;
                    displayFlashcard(flashcards.get(currentIndex));
                }
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", flashcard.getId(), currentDeck.getDeckId());
                showAlert(Alert.AlertType.ERROR, "Failed to add flashcard to deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", flashcard.getId(), currentDeck.getDeckId(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while adding the flashcard to the deck.");
        }
    }
    private boolean isFlashcardInDeck(int flashcardId) {
        return flashcards.stream().anyMatch(fc -> fc.getId() == flashcardId);
    }

    /**
     * Sets the deck to be previewed.
     *
     * @param deck The Deck object to preview.
     */
    public void setDeck(Deck deck) {
        this.currentDeck = deck;
        deckNameLabel.setText(deck.getDeckName());
        deckNameTextField.setText(deck.getDeckName());
        try {
            flashcards = deckDataAccess.getFlashcardsInDeck(deck.getDeckId());
            if (!flashcards.isEmpty()) {
                currentIndex = 0;
                displayFlashcard(flashcards.get(currentIndex));
            } else {
                frontLabel.setText("No flashcards in this deck.");
                backLabel.setText("");
                nextFlashcardButton.setDisable(true);
                previousFlashcardButton.setDisable(true);
            }
        } catch (Exception e) {
            logger.error("Error loading flashcards for deck: " + deck.getDeckName(), e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading flashcards.");
        }
    }

    /**
     * Enters edit mode by showing the TextField and hiding the Label.
     */
    private void enterEditMode() {
        deckNameLabel.setVisible(false);
        deckNameTextField.setVisible(true);
        deckNameTextField.requestFocus();
        deckNameTextField.selectAll();
    }

    /**
     * Exits edit mode by hiding the TextField and showing the Label.
     * Also saves the new deck name to the database if it has changed.
     */
    private void exitEditMode() {
        String newDeckName = deckNameTextField.getText().trim();
        if (!newDeckName.isEmpty() && !newDeckName.equals(currentDeck.getDeckName())) {
            try {
                boolean success = deckDataAccess.updateDeck(currentDeck.getDeckId(), newDeckName);
                if (success) {
                    logger.info("Deck name updated successfully to: " + newDeckName);
                    currentDeck.setDeckName(newDeckName);
                    deckNameLabel.setText(newDeckName);
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
        deckNameLabel.setVisible(true);
        deckNameTextField.setVisible(false);
    }

    /**
     * Displays the specified flashcard.
     *
     * @param flashcard The Flashcard to display.
     */
    private void displayFlashcard(Flashcard flashcard) {
        frontLabel.setText(flashcard.getFront());
        backLabel.setText(flashcard.getBack());
        frontLabel.setVisible(true);
        backLabel.setVisible(false);

        flashcardPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();

            RotateTransition rotateOutFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateOutFront.setFromAngle(0);
            rotateOutFront.setToAngle(90);
            rotateOutFront.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateInBack.setFromAngle(-90);
            rotateInBack.setToAngle(0);
            rotateInBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateOutBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateOutBack.setFromAngle(0);
            rotateOutBack.setToAngle(90);
            rotateOutBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateInFront.setFromAngle(-90);
            rotateInFront.setToAngle(0);
            rotateInFront.setAxis(Rotate.X_AXIS);

            SequentialTransition flipToBack = new SequentialTransition(rotateOutFront, rotateInBack);
            SequentialTransition flipToFront = new SequentialTransition(rotateOutBack, rotateInFront);

            if (isFrontVisible) {
                backLabel.setVisible(true);
                flipToBack.setOnFinished(e -> frontLabel.setVisible(false));
                flipToBack.play();
            } else {
                frontLabel.setVisible(true);
                flipToFront.setOnFinished(e -> backLabel.setVisible(false));
                flipToFront.play();
            }
        });
    }

    /**
     * Handles the action for the "Next" button.
     *
     * @param event The ActionEvent triggered by clicking the "Next" button.
     */
    @FXML
    private void handleNextFlashcardAction(ActionEvent event) {
        if (flashcards != null && !flashcards.isEmpty()) {
            currentIndex = (currentIndex + 1) % flashcards.size();
            displayFlashcard(flashcards.get(currentIndex));
        }
    }

    /**
     * Handles the action for the "Previous" button.
     *
     * @param event The ActionEvent triggered by clicking the "Previous" button.
     */
    @FXML
    private void handlePreviousFlashcardAction(ActionEvent event) {
        if (flashcards != null && !flashcards.isEmpty()) {
            currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
            displayFlashcard(flashcards.get(currentIndex));
        }
    }

    /**
     * Handles the action for the "Back to Deck Management" button.
     *
     * @param event The ActionEvent triggered by clicking the button.
     */
    @FXML
    private void handleBackToDeckManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/deck_management.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node deckManagementContent = loader.load();

            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContentNode(deckManagementContent);
                primaryController.setActiveButton(null);
            } else {
                logger.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch to Deck Management.");
            }

        } catch (IOException e) {
            logger.error("Failed to load deck management view", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Deck Management view.");
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

    /**
     * Helper method to determine if a node is a descendant of another node.
     *
     * @param parent The potential ancestor node.
     * @param child  The node to check.
     * @return True if 'child' is a descendant of 'parent', false otherwise.
     */
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
}
