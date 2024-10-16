package com.socslingo.controllers;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ActivityMainTestController {

    @FXML
    private HBox activity_content_area;

    @FXML
    private Button continue_button; // Continue Button

    @FXML
    private StackPane continue_button_stackpane;

    @FXML
    private Label continue_button_label;

    @FXML
    private Rectangle continue_button_background_rectangle;

    @FXML
    private Button exit_button;

    @FXML
    private Button skip_button;

    @FXML
    private VBox skip_control_container;

    @FXML
    private Button report_button;

    @FXML
    private VBox user_control_container;

    @FXML
    private HBox bottom_section; // Reference to Bottom Section

    @FXML
    private ProgressBar progress_bar; // Reference to the ProgressBar

    // Store the original skip_control_container content for resetting
    private Node originalSkipControlContent;

    private int completionCount = 0; // Tracks the number of successful completions
    private static final int TOTAL_COMPLETIONS = 3; // Total required completions

    private boolean isSkipped = false; // Flag to track if the activity was skipped

    private Node preloadedActivity = null; // Stores the preloaded activity
    private boolean isPreloading = false; // Indicates if preloading is in progress

    @FXML
    public void initialize() {
        // Store the original skip_control_container content
        if (!skip_control_container.getChildren().isEmpty()) {
            originalSkipControlContent = skip_control_container.getChildren().get(0);
        }

        // Initialize the progress bar
        progress_bar.setProgress(0.0);

        // Load the first activity synchronously
        try {
            loadCharacterRecognitionActivity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start preloading the next activity
        preloadNextActivity();

        // Set exit button action
        exit_button.setOnAction(event -> {
            try {
                PrimaryController.getInstance().switchToHome();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Initially disable the Continue button
        continue_button.setDisable(true);

        // Set event handlers
        skip_button.setOnAction(event -> handleSkipButton());
        continue_button.setOnAction(event -> handleContinueButton());

        // Initialize bottom_section background to neutral color
        bottom_section
                .setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, null)));
    }

    /**
     * Animates the background color of bottom_section.
     *
     * @param targetColor    The target Color to transition to.
     * @param durationMillis Duration of the transition in milliseconds.
     */
    private void animateBottomSectionBackground(Color targetColor, double durationMillis) {
        // Retrieve the current background color
        Background currentBackground = bottom_section.getBackground();
        final Color currentColor; // Declare as final to ensure it's effectively final

        if (currentBackground != null && !currentBackground.getFills().isEmpty()) {
            BackgroundFill fill = currentBackground.getFills().get(0);
            if (fill.getFill() instanceof Color) {
                currentColor = (Color) fill.getFill();
            } else {
                currentColor = Color.WHITE; // Default neutral color
            }
        } else {
            currentColor = Color.WHITE; // Default neutral color
        }

        // Create a color property to animate
        ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(currentColor);
        colorProperty.addListener((obs, oldColor, newColor) -> {
            bottom_section.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, null)));
        });

        // Create and play the timeline for color transition
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(colorProperty, currentColor)),
                new KeyFrame(Duration.millis(durationMillis), new KeyValue(colorProperty, targetColor)));

        timeline.play();
    }

    /**
     * Handles the Skip button action. Sets the skip flag and updates UI
     * accordingly.
     */
    private void handleSkipButton() {
        // Set the skip flag to true
        isSkipped = true;

        // Animate background color to incorrect color
        Color incorrectColor = Color.rgb(242, 224, 224);
        animateBottomSectionBackground(incorrectColor, 500);

        // Change the Continue Button styles to 'incorrect-hover' styles
        setContinueButtonToIncorrectHover();

        // Load the replacement FXML for skip action
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/socslingo/views/incorrect_replacement.fxml"));
            Node replacement = loader.load();
            skip_control_container.getChildren().setAll(replacement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the Continue button to use the 'incorrect-hover' styles.
     */
    private void setContinueButtonToIncorrectHover() {
        // Remove existing style classes related to the default or enabled state
        continue_button_label.getStyleClass().removeAll(
                "activity-continue-button__label--medium-type-hover",
                "activity-button__label--medium-type-check-answer-unclickable");
        continue_button.getStyleClass().removeAll(
                "activity-continue-button--medium-type-hover",
                "activity-button--medium-type-check-answer-unclickable");
        continue_button_background_rectangle.getStyleClass().removeAll(
                "activity-continue-button__background-rectangle--medium-type-hover",
                "activity-button__background-rectangle--medium-type-check-answer-unclickable");
        continue_button_stackpane.getStyleClass().removeAll(
                "activity-continue-button-stackpane-type-hover",
                "activity-button-stackpane-type-check-answer-unclickable");

        // Add 'incorrect-hover' style classes
        continue_button_label.getStyleClass().add("activity-continue-button__label--medium-type-incorrect-hover");
        continue_button.getStyleClass().add("activity-continue-button--medium-type-incorrect-hover");
        continue_button_background_rectangle.getStyleClass()
                .add("activity-continue-button__background-rectangle--medium-type-incorrect-hover");
        continue_button_stackpane.getStyleClass().add("activity-continue-button-stackpane-type-incorrect-hover");

        // Optionally, enable the Continue button if it was disabled
        continue_button.setDisable(false);
        continue_button_label.setDisable(false);
    }

    /**
     * Handles the Continue button action. Uses the preloaded activity if available.
     */
    private void handleContinueButton() {
        // Disable the Continue button to prevent multiple clicks
        continue_button.setDisable(true);

        // Start fade-out transition on activity_content_area
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), activity_content_area);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            if (!isSkipped) { // Only count if not skipped
                // Increment the completion count
                completionCount++;

                // Calculate the new progress value
                double newProgress = (double) completionCount / TOTAL_COMPLETIONS;

                // Animate the progress bar to the new progress value over 500 milliseconds
                animateProgressBar(newProgress, 500);
            }

            if (completionCount < TOTAL_COMPLETIONS) {
                if (preloadedActivity != null) {
                    // Use the preloaded activity
                    Platform.runLater(() -> {
                        activity_content_area.getChildren().clear();
                        activity_content_area.getChildren().add(preloadedActivity);

                        // Reset UI styles and flags
                        resetBottomSectionStyle();
                        resetSkipControlContainer();
                        resetContinueButtonStyles();
                        isSkipped = false;

                        // Start fade-in transition
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), activity_content_area);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                        // Start preloading the next activity
                        preloadNextActivity();
                    });
                } else {
                    // Preloaded activity not ready, load synchronously
                    try {
                        loadCharacterRecognitionActivity();

                        // Reset UI styles and flags
                        resetBottomSectionStyle();
                        resetSkipControlContainer();
                        resetContinueButtonStyles();
                        isSkipped = false;

                        // Start fade-in transition
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), activity_content_area);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                        // Start preloading the next activity
                        preloadNextActivity();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // If required completions are met, navigate back home
                try {
                    PrimaryController.getInstance().switchToHome();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fadeOut.play();
    }

    /**
     * Animates the ProgressBar from its current progress to the target progress.
     *
     * @param targetProgress The target progress value (between 0.0 and 1.0).
     * @param durationMillis The duration of the animation in milliseconds.
     */
    private void animateProgressBar(double targetProgress, double durationMillis) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progress_bar.progressProperty(), progress_bar.getProgress())),
                new KeyFrame(Duration.millis(durationMillis),
                        new KeyValue(progress_bar.progressProperty(), targetProgress)));
        timeline.play();
    }

    /**
     * Resets the bottom_section style to its default state.
     */
    private void resetBottomSectionStyle() {
        // Animate background color to neutral color
        Color neutralColor = Color.rgb(255, 255, 255);
        animateBottomSectionBackground(neutralColor, 500);

        // Remove 'bottom-section-correct' and 'bottom-section-incorrect' styles
        bottom_section.getStyleClass().removeAll("bottom-section-correct", "bottom-section-incorrect");
        // Add 'bottom-section-neutral' if not already present
        if (!bottom_section.getStyleClass().contains("bottom-section-neutral")) {
            bottom_section.getStyleClass().add("bottom-section-neutral");
        }
    }

    /**
     * Resets the skip_control_container to its original state with the skip button.
     */
    private void resetSkipControlContainer() {
        // Clear existing children
        skip_control_container.getChildren().clear();

        // Restore the original skip_control_container content
        if (originalSkipControlContent != null) {
            skip_control_container.getChildren().add(originalSkipControlContent);
        }
    }

    /**
     * Resets the Continue button styles to their default state.
     */
    private void resetContinueButtonStyles() {
        // Remove 'incorrect-hover' and 'hover' style classes from the label
        continue_button_label.getStyleClass().removeAll(
                "activity-continue-button__label--medium-type-incorrect-hover",
                "activity-continue-button__label--medium-type-hover");

        // Remove 'incorrect-hover' and 'hover' style classes from the button
        continue_button.getStyleClass().removeAll(
                "activity-continue-button--medium-type-incorrect-hover",
                "activity-continue-button--medium-type-hover");

        // Remove 'incorrect-hover' and 'hover' style classes from the background
        // rectangle
        continue_button_background_rectangle.getStyleClass().removeAll(
                "activity-continue-button__background-rectangle--medium-type-incorrect-hover",
                "activity-continue-button__background-rectangle--medium-type-hover");

        // Remove 'incorrect-hover' and 'hover' style classes from the stack pane
        continue_button_stackpane.getStyleClass().removeAll(
                "activity-continue-button-stackpane-type-incorrect-hover",
                "activity-continue-button-stackpane-type-hover");

        // Re-add 'check-answer-unclickable' style classes
        continue_button_label.getStyleClass().add("activity-button__label--medium-type-check-answer-unclickable");
        continue_button.getStyleClass().add("activity-button--medium-type-check-answer-unclickable");
        continue_button_background_rectangle.getStyleClass()
                .add("activity-button__background-rectangle--medium-type-check-answer-unclickable");
        continue_button_stackpane.getStyleClass().add("activity-button-stackpane-type-check-answer-unclickable");

        // Disable the Continue button
        continue_button.setDisable(true);
        continue_button_label.setDisable(true);
    }

    /**
     * Loads the character recognition activity synchronously.
     *
     * @throws IOException if the FXML file cannot be loaded.
     */
    private void loadCharacterRecognitionActivity() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/socslingo/views/activity_sentence.fxml"));

        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found at /com/socslingo/views/activity_sentence.fxml");
        }

        Node characterRecognitionActivity = loader.load();

        // Get the controller of ActivityCharacterRecognition
        ActivitySentenceController crcController = loader.getController();

        // Set this main controller in the ActivityCharacterRecognition controller
        crcController.setMainController(this);

        activity_content_area.getChildren().add(characterRecognitionActivity);
    }

    /**
     * Preloads the next character recognition activity in the background.
     */
    private void preloadNextActivity() {
        if (isPreloading) {
            return; // Prevent multiple preload tasks
        }
        isPreloading = true;

        Task<Node> preloadTask = new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/socslingo/views/activity_sentence.fxml"));
                Node activity = loader.load();

                // Retrieve and configure the controller
                ActivitySentenceController crcController = loader.getController();
                crcController.setMainController(ActivityMainTestController.this);

                return activity;
            }
        };

        preloadTask.setOnSucceeded(event -> {
            preloadedActivity = preloadTask.getValue();
            isPreloading = false;
        });

        preloadTask.setOnFailed(event -> {
            preloadTask.getException().printStackTrace();
            isPreloading = false;
        });

        Thread preloadThread = new Thread(preloadTask);
        preloadThread.setDaemon(true);
        preloadThread.start();
    }

    /**
     * Method to enable and update the "Check" button once all pairs are correctly
     * matched.
     */
    public void enableCheckButton() {
        continue_button.setDisable(false);
        continue_button_label.setDisable(false);

        // Remove classes related to the unclickable state
        continue_button_label.getStyleClass().remove("activity-button__label--medium-type-check-answer-unclickable");
        continue_button.getStyleClass().remove("activity-button--medium-type-check-answer-unclickable");
        continue_button_background_rectangle.getStyleClass()
                .remove("activity-button__background-rectangle--medium-type-check-answer-unclickable");
        continue_button_stackpane.getStyleClass().remove("activity-button-stackpane-type-check-answer-unclickable");

        // Add hover styles
        continue_button_label.getStyleClass().add("activity-continue-button__label--medium-type-correct-hover");
        continue_button.getStyleClass().add("activity-continue-button--medium-type-correct-hover");
        continue_button_background_rectangle.getStyleClass()
                .add("activity-continue-button__background-rectangle--medium-type-correct-hover");
        continue_button_stackpane.getStyleClass().add("activity-continue-button-stackpane-type-correct-hover");

        // Animate background color to correct color
        Color correctColor = Color.rgb(227, 250, 197);
        animateBottomSectionBackground(correctColor, 500);

        // Change bottom_section's style to 'bottom-section-correct'
        if (bottom_section.getStyleClass().contains("bottom-section-neutral")) {
            bottom_section.getStyleClass().remove("bottom-section-neutral");
        }
        if (bottom_section.getStyleClass().contains("bottom-section-incorrect")) {
            bottom_section.getStyleClass().remove("bottom-section-incorrect");
        }
        bottom_section.getStyleClass().add("bottom-section-correct");

        // Replace the skip button with 'correct_replacement.fxml'
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/correct_replacement.fxml"));
            Node correctReplacement = loader.load();
            skip_control_container.getChildren().setAll(correctReplacement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
