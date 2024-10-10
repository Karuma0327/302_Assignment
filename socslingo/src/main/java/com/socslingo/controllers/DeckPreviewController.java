package com.socslingo.controllers;

import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.ControllerManager;
import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.Deck;
import com.socslingo.models.Flashcard;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

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

    private Deck currentDeck;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize Data Access Objects
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            deckDataAccess = new DeckDataAccess(dbManager);
            logger.info("DeckDataAccess initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize DeckDataAccess", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
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
                // Focus lost
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
                        // If the click is outside the TextField and Label, exit edit mode
                        if (!isDescendant(deckNameTextField, target) && target != deckNameLabel) {
                            exitEditMode();
                        }
                    }
                });
            }
        });
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
            // Update deck name in the database
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
            // Load the deck management FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/deck_management.fxml"));
            loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controller factory is set
            Node deckManagementContent = loader.load();

            // Use the PrimaryController to switch content
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContentNode(deckManagementContent);
                // Optionally, update the active button if needed
                primaryController.setActiveButton(null);
            } else {
                logger.error("PrimaryController instance is null.");
                // Handle the error appropriately
                showAlert(Alert.AlertType.ERROR, "Failed to switch to Deck Management.");
            }

        } catch (IOException e) {
            logger.error("Failed to load deck management view", e);
            // Handle the error appropriately
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
}
