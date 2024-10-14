package com.socslingo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socslingo.managers.SessionManager;
import com.socslingo.models.Flashcard;
import com.socslingo.services.FlashcardService;
import com.socslingo.utils.DateUtils;

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

public class FlashcardController {

    private static final Logger logger = LoggerFactory.getLogger(FlashcardController.class);

    private FlashcardService flashcard_service;

    public FlashcardController(FlashcardService flashcard_service) {
        this.flashcard_service = flashcard_service;
        logger.info("FlashcardController initialized with FlashcardService");
    }

    @FXML
    private Button save_flashcard_button;

    @FXML
    private Label front_label;

    @FXML
    private Label back_label;

    @FXML
    private StackPane stack_pane;

    @FXML
    private Button next_flashcard_button;

    @FXML
    private Button previous_flashcard_button;

    @FXML
    private TextField front_text_field;

    @FXML
    private TextField back_text_field;

    @FXML
    private Button preview_flashcard_button;

    private List<Flashcard> flashcards;
    private int current_index = 0;

    @FXML
    private void handleLogout() {
        logger.info("Logout button clicked");
        logger.info("User logged out successfully");
    }

    @FXML
    public void handleSaveFlashcardAction() {
        logger.info("Save Flashcard button clicked");
        String front_text = front_text_field.getText().trim();
        String back_text = back_text_field.getText().trim();

        if (front_text.isEmpty() || back_text.isEmpty()) {
            logger.warn("Attempted to save flashcard with empty front or back text");
            return;
        }

        int user_id = getUserId();
        String created_date = getCurrentDate();

        try {
            flashcard_service.createFlashcard(user_id, front_text, back_text, created_date);
            logger.info("Flashcard saved successfully for userId: {}", user_id);
            showAlert(Alert.AlertType.INFORMATION, "Flashcard saved successfully.");
            front_text_field.clear();
            back_text_field.clear();
        } catch (Exception e) {
            logger.error("Error saving flashcard for userId: {}", user_id, e);
            showAlert(Alert.AlertType.ERROR, "Failed to save flashcard.");
        }
    }

    private String getCurrentDate() {
        String current_date = DateUtils.getCurrentDate();
        logger.debug("Current date retrieved: {}", current_date);
        return current_date;
    }

    private int getUserId() {
        int user_id = SessionManager.getInstance().getCurrentUserId();
        logger.debug("Retrieved User ID from SessionManager: {}", user_id);
        return user_id;
    }

    @FXML
    public void handleViewFlashcardsAction() {
        logger.info("View Flashcards button clicked");

        int user_id = getUserId();
        logger.debug("Current User ID: {}", user_id);

        try {
            flashcards = flashcard_service.getUserFlashcards(user_id);
            logger.info("Number of flashcards retrieved: {}", flashcards.size());

            if (!flashcards.isEmpty()) {
                current_index = 0;
                displayFlashcard(flashcards.get(current_index));
                logger.debug("Displayed flashcard at index: {}", current_index);
            } else {
                logger.info("No flashcards to display for userId: {}", user_id);
                front_label.setText("");
                back_label.setText("");
            }
            logger.info("Flashcards displayed successfully");
        } catch (Exception e) {
            logger.error("Error retrieving flashcards for userId: {}", user_id, e);
            showAlert(Alert.AlertType.ERROR, "Failed to load flashcards.");
        }
    }

    @FXML
    public void handleNextFlashcardAction() {
        logger.info("Next Flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fade_out = new FadeTransition(Duration.millis(150), stack_pane);
            fade_out.setFromValue(1.0);
            fade_out.setToValue(0.0);
            fade_out.setOnFinished(event -> {
                current_index = (current_index + 1) % flashcards.size();
                logger.debug("Navigated to flashcard index: {}", current_index);
                displayFlashcard(flashcards.get(current_index));
                FadeTransition fade_in = new FadeTransition(Duration.millis(150), stack_pane);
                fade_in.setFromValue(0.0);
                fade_in.setToValue(1.0);
                fade_in.play();
                logger.debug("Fade-in transition played for flashcard index: {}", current_index);
            });
            fade_out.play();
            logger.debug("Fade-out transition played");
        } else {
            logger.warn("No flashcards available to navigate next");
        }
    }

    @FXML
    public void handlePreviousFlashcardAction() {
        logger.info("Previous Flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fade_out = new FadeTransition(Duration.millis(150), stack_pane);
            fade_out.setFromValue(1.0);
            fade_out.setToValue(0.0);
            fade_out.setOnFinished(event -> {
                current_index = (current_index - 1 + flashcards.size()) % flashcards.size();
                logger.debug("Navigated to flashcard index: {}", current_index);
                displayFlashcard(flashcards.get(current_index));
                FadeTransition fade_in = new FadeTransition(Duration.millis(150), stack_pane);
                fade_in.setFromValue(0.0);
                fade_in.setToValue(1.0);
                fade_in.play();
                logger.debug("Fade-in transition played for flashcard index: {}", current_index);
            });
            fade_out.play();
            logger.debug("Fade-out transition played");
        } else {
            logger.warn("No flashcards available to navigate previous");
        }
    }

    private void displayFlashcard(Flashcard flashcard) {
        logger.debug("Displaying flashcard: {}", flashcard);
        front_label.setText(flashcard.getFront());
        back_label.setText(flashcard.getBack());
        front_label.setVisible(true);
        back_label.setVisible(false);

        stack_pane.setOnMouseClicked(event -> {
            boolean is_front_visible = front_label.isVisible();
            logger.debug("StackPane clicked. Front visible: {}", is_front_visible);

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
                flip_to_back.setOnFinished(e -> {
                    front_label.setVisible(false);
                    logger.debug("Switched to back of flashcard");
                });
                flip_to_back.play();
                logger.debug("Flip to back animation started");
            } else {
                front_label.setVisible(true);
                flip_to_front.setOnFinished(e -> {
                    back_label.setVisible(false);
                    logger.debug("Switched to front of flashcard");
                });
                flip_to_front.play();
                logger.debug("Flip to front animation started");
            }
        });
    }

    @FXML
    public void handlePreviewFlashcardAction() {
        logger.info("Preview Flashcard button clicked");
        String front_text = front_text_field.getText().trim();
        String back_text = back_text_field.getText().trim();

        if (front_text.isEmpty() || back_text.isEmpty()) {
            logger.warn("Attempted to preview flashcard with empty front or back text");
            return;
        }

        front_label.setText(front_text);
        back_label.setText(back_text);
        front_label.setVisible(true);
        back_label.setVisible(false);
        logger.debug("Previewing flashcard with front: '{}' and back: '{}'", front_text, back_text);

        stack_pane.setOnMouseClicked(event -> {
            boolean is_front_visible = front_label.isVisible();
            logger.debug("StackPane clicked during preview. Front visible: {}", is_front_visible);

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
                flip_to_back.setOnFinished(e -> {
                    front_label.setVisible(false);
                    logger.debug("Switched to back of preview flashcard");
                });
                flip_to_back.play();
                logger.debug("Flip to back animation for preview started");
            } else {
                front_label.setVisible(true);
                flip_to_front.setOnFinished(e -> {
                    back_label.setVisible(false);
                    logger.debug("Switched to front of preview flashcard");
                });
                flip_to_front.play();
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
    private void showAlert(Alert.AlertType alert_type, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
        Alert alert = new Alert(alert_type);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }
}