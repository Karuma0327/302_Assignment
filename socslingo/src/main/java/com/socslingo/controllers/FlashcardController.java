package com.socslingo.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import com.socslingo.models.Flashcard;
import com.socslingo.services.FlashcardService;

import com.socslingo.utils.DateUtils;
import com.socslingo.managers.SessionManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashcardController {

    private static final Logger logger = LoggerFactory.getLogger(FlashcardController.class);

    private FlashcardService flashcardService;

    // Constructor accepting FlashcardService
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
        logger.info("FlashcardController initialized with FlashcardService");
    }

    @FXML
    private Button saveFlashcardButton;

    @FXML
    private Label frontLabel;

    @FXML
    private Label backLabel;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button nextFlashcardButton;

    @FXML
    private Button previousFlashcardButton;

    @FXML
    private TextField frontTextField;

    @FXML
    private TextField backTextField;

    @FXML
    private Button previewFlashcardButton;

    private List<Flashcard> flashcards;
    private int currentIndex = 0;

    @FXML
    private void handleLogout() {
        logger.info("Logout button clicked");
        // Implement logout functionality here
        logger.info("User logged out successfully");
    }

    @FXML
    public void handleSaveFlashcardAction() {
        logger.info("Save Flashcard button clicked");
        // Retrieve text from TextFields
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        // Validate input
        if (frontText.isEmpty() || backText.isEmpty()) {
            logger.warn("Attempted to save flashcard with empty front or back text");
            // Optionally, display an error message to the user
            return;
        }

        int userId = getUserId();
        String createdDate = getCurrentDate();

        // Save flashcard to database using flashcardService
        try {
            flashcardService.createFlashcard(userId, frontText, backText, createdDate);
            logger.info("Flashcard saved successfully for userId: {}", userId);
            showAlert(Alert.AlertType.INFORMATION, "Flashcard saved successfully.");
            frontTextField.clear();
            backTextField.clear();
        } catch (Exception e) {
            logger.error("Error saving flashcard for userId: {}", userId, e);
            showAlert(Alert.AlertType.ERROR, "Failed to save flashcard.");
        }
    }

    private String getCurrentDate() {
        String currentDate = DateUtils.getCurrentDate();
        logger.debug("Current date retrieved: {}", currentDate);
        return currentDate;
    }

    private int getUserId() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        logger.debug("Retrieved User ID from SessionManager: {}", userId);
        return userId;
    }

    @FXML
    public void handleViewFlashcardsAction() {
        logger.info("View Flashcards button clicked");

        int userId = getUserId();
        logger.debug("Current User ID: {}", userId);

        try {
            flashcards = flashcardService.getUserFlashcards(userId);
            logger.info("Number of flashcards retrieved: {}", flashcards.size());

            if (!flashcards.isEmpty()) {
                currentIndex = 0; // Reset to the first flashcard
                displayFlashcard(flashcards.get(currentIndex));
                logger.debug("Displayed flashcard at index: {}", currentIndex);
            } else {
                logger.info("No flashcards to display for userId: {}", userId);
                frontLabel.setText("");
                backLabel.setText("");
            }
            logger.info("Flashcards displayed successfully");
        } catch (Exception e) {
            logger.error("Error retrieving flashcards for userId: {}", userId, e);
            showAlert(Alert.AlertType.ERROR, "Failed to load flashcards.");
        }
    }

    @FXML
    public void handleNextFlashcardAction() {
        logger.info("Next Flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stackPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                currentIndex = (currentIndex + 1) % flashcards.size();
                logger.debug("Navigated to flashcard index: {}", currentIndex);
                displayFlashcard(flashcards.get(currentIndex));
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), stackPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                logger.debug("Fade-in transition played for flashcard index: {}", currentIndex);
            });
            fadeOut.play();
            logger.debug("Fade-out transition played");
        } else {
            logger.warn("No flashcards available to navigate next");
        }
    }

    @FXML
    public void handlePreviousFlashcardAction() {
        logger.info("Previous Flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stackPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
                logger.debug("Navigated to flashcard index: {}", currentIndex);
                displayFlashcard(flashcards.get(currentIndex));
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), stackPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                logger.debug("Fade-in transition played for flashcard index: {}", currentIndex);
            });
            fadeOut.play();
            logger.debug("Fade-out transition played");
        } else {
            logger.warn("No flashcards available to navigate previous");
        }
    }

    private void displayFlashcard(Flashcard flashcard) {
        logger.debug("Displaying flashcard: {}", flashcard);
        frontLabel.setText(flashcard.getFront());
        backLabel.setText(flashcard.getBack());
        frontLabel.setVisible(true);
        backLabel.setVisible(false);

        stackPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();
            logger.debug("StackPane clicked. Front visible: {}", isFrontVisible);

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
                flipToBack.setOnFinished(e -> {
                    frontLabel.setVisible(false);
                    logger.debug("Switched to back of flashcard");
                });
                flipToBack.play();
                logger.debug("Flip to back animation started");
            } else {
                frontLabel.setVisible(true);
                flipToFront.setOnFinished(e -> {
                    backLabel.setVisible(false);
                    logger.debug("Switched to front of flashcard");
                });
                flipToFront.play();
                logger.debug("Flip to front animation started");
            }
        });
    }

    @FXML
    public void handlePreviewFlashcardAction() {
        logger.info("Preview Flashcard button clicked");
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        if (frontText.isEmpty() || backText.isEmpty()) {
            logger.warn("Attempted to preview flashcard with empty front or back text");
            // Optionally, display an error message to the user
            return;
        }

        frontLabel.setText(frontText);
        backLabel.setText(backText);
        frontLabel.setVisible(true);
        backLabel.setVisible(false);
        logger.debug("Previewing flashcard with front: '{}' and back: '{}'", frontText, backText);

        stackPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();
            logger.debug("StackPane clicked during preview. Front visible: {}", isFrontVisible);

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
                flipToBack.setOnFinished(e -> {
                    frontLabel.setVisible(false);
                    logger.debug("Switched to back of preview flashcard");
                });
                flipToBack.play();
                logger.debug("Flip to back animation for preview started");
            } else {
                frontLabel.setVisible(true);
                flipToFront.setOnFinished(e -> {
                    backLabel.setVisible(false);
                    logger.debug("Switched to front of preview flashcard");
                });
                flipToFront.play();
                logger.debug("Flip to front animation for preview started");
            }
        });
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
