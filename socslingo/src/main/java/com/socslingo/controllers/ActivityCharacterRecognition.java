package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.socslingo.dataAccess.CharacterRecognitionDataAccess;
import com.socslingo.models.CharacterRecognition;

public class ActivityCharacterRecognition {

    @FXML
    private GridPane gridPane;

    // Data structures to hold mappings
    private List<CharacterRecognition> characterPairs;
    private Map<ToggleButton, CharacterRecognition> buttonToCharacterMap = new HashMap<>();
    private ToggleButton firstSelectedButton = null;
    private ToggleButton secondSelectedButton = null;

    // Lists to hold left and right buttons
    private List<StackPane> leftButtonPanes = new ArrayList<>();
    private List<StackPane> rightButtonPanes = new ArrayList<>();

    // Counter for correct matches
    private int correctMatches = 0;

    // Reference to the main controller
    private ActivityMainController mainController;

    // Constants for dimensions
    private static final double UNSELECTED_WIDTH = 146;
    private static final double UNSELECTED_HEIGHT = 41;
    private static final double SELECTED_WIDTH = 255;
    private static final double SELECTED_HEIGHT = 72;

    // Animation duration in milliseconds
    private static final double ANIMATION_DURATION = 300;

    @FXML
    public void initialize() {
        // Load data
        CharacterRecognitionDataAccess dataAccess = new CharacterRecognitionDataAccess();
        characterPairs = dataAccess.getCharacterRecognitionsByType("Hiragana");

        // Shuffle and pick first 5 pairs (or as needed)
        Collections.shuffle(characterPairs);
        characterPairs = characterPairs.subList(0, Math.min(5, characterPairs.size()));

        // Collect left and right button panes
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                Integer columnIndex = GridPane.getColumnIndex(node);
                if (columnIndex == null)
                    columnIndex = 0;
                if (columnIndex == 0) {
                    leftButtonPanes.add((StackPane) node);
                } else if (columnIndex == 1) {
                    rightButtonPanes.add((StackPane) node);
                }
            }
        }

        // Shuffle buttons to randomize their positions
        Collections.shuffle(leftButtonPanes);
        Collections.shuffle(rightButtonPanes);

        // Assign data to buttons
        for (int i = 0; i < characterPairs.size(); i++) {
            CharacterRecognition pair = characterPairs.get(i);

            // Left button: romaji
            StackPane leftPane = leftButtonPanes.get(i);
            setupButton(leftPane, pair.getRomaji(), "romaji", pair);

            // Right button: hiragana
            StackPane rightPane = rightButtonPanes.get(i);
            setupButton(rightPane, pair.getCharacter(), "hiragana", pair);
        }
    }

    private void setupButton(StackPane buttonStackPane, String text, String type, CharacterRecognition pair) {
        ToggleButton toggleButton = null;
        Label toggleButtonLabel = null;
        Rectangle toggleButtonBackground = null;

        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof ToggleButton) {
                toggleButton = (ToggleButton) child;
            } else if (child instanceof Label) {
                toggleButtonLabel = (Label) child;
            } else if (child instanceof Rectangle) {
                toggleButtonBackground = (Rectangle) child;
            }
        }

        if (toggleButton != null && toggleButtonLabel != null && toggleButtonBackground != null) {
            // Declare final variables for use in lambdas
            final ToggleButton finalToggleButton = toggleButton;
            final Label finalToggleButtonLabel = toggleButtonLabel;
            final Rectangle finalToggleButtonBackground = toggleButtonBackground;

            finalToggleButtonLabel.setText(text);
            buttonToCharacterMap.put(finalToggleButton, pair);
            finalToggleButton.getProperties().put("type", type);

            // Initialize label size to unselected state
            finalToggleButtonLabel.setPrefWidth(UNSELECTED_WIDTH);
            finalToggleButtonLabel.setPrefHeight(UNSELECTED_HEIGHT);

            // Add listener to toggle button selection
            finalToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                handleToggle(finalToggleButton, newValue, finalToggleButtonLabel, finalToggleButtonBackground);
            });

            // Add listeners for pressed and released states
            finalToggleButton.pressedProperty().addListener((observable, wasPressed, isPressed) -> {
                updateLabelStyle(finalToggleButton, finalToggleButtonLabel, finalToggleButtonBackground,
                        finalToggleButton.isSelected(), isPressed);
            });

            finalToggleButton.armedProperty().addListener((observable, wasArmed, isArmed) -> {
                updateLabelStyle(finalToggleButton, finalToggleButtonLabel, finalToggleButtonBackground,
                        finalToggleButton.isSelected(), finalToggleButton.isPressed());
            });

            // Add listener for selection changes to handle animation completion
            finalToggleButton.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                animateLabelSize(finalToggleButton, isSelected ? SELECTED_WIDTH : UNSELECTED_WIDTH,
                        isSelected ? SELECTED_HEIGHT : UNSELECTED_HEIGHT,
                        () -> updateLabelStyle(finalToggleButton, finalToggleButtonLabel, finalToggleButtonBackground,
                                isSelected, true));
            });

            // Set up event handler
            finalToggleButton.setOnAction(event -> {
                handleButtonSelection(finalToggleButton);
            });


        }
    }

    private void handleToggle(ToggleButton toggleButton, boolean isSelected, Label label,
                              Rectangle backgroundRectangle) {
        if (isSelected) {
            // When selected, perform the overshoot animation
            double targetWidth = SELECTED_WIDTH;
            double targetHeight = SELECTED_HEIGHT;

            // Animate label size
            animateLabelSize(toggleButton, targetWidth, targetHeight, () -> {
                // Update styles after animation
                updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, true);
                // Animate button scale
                animateButtonScale(toggleButton, () -> {
                    // Additional actions after animation if needed
                });
            });

            // Update styles immediately for pressed state
            updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, false);
        } else {
            // When deselected, revert to unselected state without animation
            double targetWidth = UNSELECTED_WIDTH;
            double targetHeight = UNSELECTED_HEIGHT;

            // Animate label size without overshoot
            animateLabelSize(toggleButton, targetWidth, targetHeight, () -> {
                // Update styles after animation
                updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, true);
            });

            // Update styles immediately
            updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, false);
        }
    }

    private void animateLabelSize(ToggleButton toggleButton, double targetWidth, double targetHeight,
                                  Runnable onFinished) {
        Label label = getLabelFromToggleButton(toggleButton);
        if (label == null)
            return;

        // Create key values for width and height
        KeyValue widthKV = new KeyValue(label.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);
        KeyValue heightKV = new KeyValue(label.prefHeightProperty(), targetHeight, Interpolator.EASE_BOTH);

        // Create key frame
        KeyFrame kf = new KeyFrame(Duration.millis(ANIMATION_DURATION), widthKV, heightKV);

        // Create timeline and add key frame
        Timeline timeline = new Timeline(kf);

        // Set the onFinished callback
        timeline.setOnFinished(event -> onFinished.run());

        // Play the animation
        timeline.play();
    }

    /**
     * Updated method to apply style classes based on the toggle button's state.
     *
     * @param toggleButton        The ToggleButton being updated.
     * @param label               The associated Label.
     * @param backgroundRectangle The associated Rectangle.
     * @param isSelected          Whether the toggle button is selected.
     * @param isPressed           Whether the toggle button is pressed.
     */
    private void updateLabelStyle(ToggleButton toggleButton, Label label, Rectangle backgroundRectangle,
                                  boolean isSelected, boolean isAnimationComplete) {
        // Clear existing style classes
        label.getStyleClass().removeAll(
                "activity-toggle-button-label-unselected-state-unpressed",
                "activity-toggle-button-label-unselected-state-pressed",
                "activity-toggle-button-label-selected-state-unpressed",
                "activity-toggle-button-label-selected-state-pressed",
                "activity-toggle-button-label-selected-state-unpressed-border",
                "activity-toggle-button-label-selected-animation-in-progress-state");

        backgroundRectangle.getStyleClass().removeAll(
                "activity-toggle-button-background-rectangle-unselected-state-unpressed",
                "activity-toggle-button-background-rectangle-unselected-state-pressed",
                "activity-toggle-button-background-rectangle-selected-state-unpressed",
                "activity-toggle-button-background-rectangle-selected-state-pressed",
                "activity-toggle-button-background-rectangle-selected-state-unpressed-border",
                "activity-toggle-button-background-rectangle-selected-animation-in-progress-state");

        toggleButton.getStyleClass().removeAll(
                "activity-toggle-button-unselected-state-pressed",
                "activity-toggle-button-selected-state-pressed",
                "activity-toggle-button-selected-state-unpressed",
                "activity-toggle-button-selected-state-pressed",
                "activity-toggle-button-selected-state-unpressed-border",
                "activity-toggle-button-selected-animation-in-progress-state");

        // Add the appropriate style class based on selection and animation state
        if (isSelected) {
            // If the toggle button is pressed and selected
            if (toggleButton.isPressed()) {
                label.getStyleClass().add("activity-toggle-button-label-selected-state-pressed");
                backgroundRectangle.getStyleClass()
                        .add("activity-toggle-button-background-rectangle-selected-state-pressed");
                toggleButton.getStyleClass().add("activity-toggle-button-selected-state-pressed");

            } else {
                // If the animation is complete and the button is selected
                if (isAnimationComplete) {
                    label.getStyleClass()
                            .add("activity-toggle-button-label-selected-state-unpressed");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-selected-state-unpressed");
                    toggleButton.getStyleClass().add("activity-toggle-button-selected-state-unpressed");

                } else {
                    // If the button is selected but the animation is not complete
                    label.getStyleClass().add("activity-toggle-button-label-selected-state-unpressed");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-selected-animation-in-progress-state");
                    toggleButton.getStyleClass().add("activity-toggle-button-selected-animation-in-progress-state");
                }
            }
        } else {
            // If the toggle button is pressed and not selected
            if (toggleButton.isPressed()) {
                label.getStyleClass().add("activity-toggle-button-label-unselected-state-pressed");
                backgroundRectangle.getStyleClass()
                        .add("activity-toggle-button-background-rectangle-unselected-state-pressed");
                toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-pressed");
            } else {
                // If the button is not selected and not pressed

                label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed");
                backgroundRectangle.getStyleClass()
                        .add("activity-toggle-button-background-rectangle-unselected-state-unpressed");
                toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed");
            }
        }

    }

    /**
     * Helper method to retrieve the Label from a ToggleButton's StackPane.
     *
     * @param toggleButton The ToggleButton.
     * @return The associated Label, or null if not found.
     */
    private Label getLabelFromToggleButton(ToggleButton toggleButton) {
        StackPane stackPane = (StackPane) toggleButton.getParent();
        for (Node child : stackPane.getChildren()) {
            if (child instanceof Label) {
                return (Label) child;
            }
        }
        return null;
    }

    /**
     * Helper method to retrieve the Rectangle from a ToggleButton's StackPane.
     *
     * @param toggleButton The ToggleButton.
     * @return The associated Rectangle, or null if not found.
     */
    private Rectangle getRectangleFromToggleButton(ToggleButton toggleButton) {
        StackPane stackPane = (StackPane) toggleButton.getParent();
        for (Node child : stackPane.getChildren()) {
            if (child instanceof Rectangle) {
                return (Rectangle) child;
            }
        }
        return null;
    }

    /**
     * Method to set the main controller reference.
     *
     * @param mainController The ActivityMainController instance.
     */
    public void setMainController(ActivityMainController mainController) {
        this.mainController = mainController;
    }

    private void handleButtonSelection(ToggleButton toggleButton) {
        if (toggleButton.isDisabled()) {
            return;
        }

        if (!toggleButton.isSelected()) {
            // The button was deselected
            if (toggleButton.equals(firstSelectedButton)) {
                firstSelectedButton = null;
            } else if (toggleButton.equals(secondSelectedButton)) {
                secondSelectedButton = null;
            }
            return;
        }

        if (firstSelectedButton == null) {
            firstSelectedButton = toggleButton;
        } else if (secondSelectedButton == null && toggleButton != firstSelectedButton) {
            secondSelectedButton = toggleButton;
            checkMatch();
        } else {
            resetSelections();
            firstSelectedButton = toggleButton;
        }
    }

    private void checkMatch() {
        CharacterRecognition cr1 = buttonToCharacterMap.get(firstSelectedButton);
        CharacterRecognition cr2 = buttonToCharacterMap.get(secondSelectedButton);

        String type1 = (String) firstSelectedButton.getProperties().get("type");
        String type2 = (String) secondSelectedButton.getProperties().get("type");

        if (cr1.equals(cr2) && !type1.equals(type2)) {
            // It's a match
            applyCorrectMatchStyles(firstSelectedButton);
            applyCorrectMatchStyles(secondSelectedButton);

            firstSelectedButton.setDisable(true);
            secondSelectedButton.setDisable(true);

            // Increment correct matches counter
            correctMatches++;

            // Check if all pairs are matched
            if (correctMatches == characterPairs.size() && mainController != null) {
                mainController.enableCheckButton();
            }
        } else {
            // Not a match - Apply incorrect styles
            applyIncorrectMatchStyles(firstSelectedButton);
            applyIncorrectMatchStyles(secondSelectedButton);
        }
    }

    private void applyCorrectMatchStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label label = getLabelFromToggleButton(toggleButton);
        Rectangle backgroundRectangle = getRectangleFromToggleButton(toggleButton);

        if (label != null && backgroundRectangle != null) {
            // Clear existing styles
            label.getStyleClass().clear();
            backgroundRectangle.getStyleClass().clear();
            toggleButton.getStyleClass().clear();
            buttonStackPane.getStyleClass().clear();

            // Apply correct answer styles
            label.getStyleClass().add("activity-correct-response-toggle-button-label");
            backgroundRectangle.getStyleClass()
                    .add("activity-correct-response-toggle-button-background-rectangle");
            toggleButton.getStyleClass().add("activity-correct-response-toggle-button");
            buttonStackPane.getStyleClass().add("activity-button-stackpane-type-correct-answer-pressed");

            // Perform overshoot animation
            Timeline timeline = new Timeline();
            KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 1.1, Interpolator.EASE_OUT);
            KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 1.1, Interpolator.EASE_OUT);
            KeyFrame kfUp = new KeyFrame(Duration.millis(150), scaleUpX, scaleUpY);

            KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
            KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);
            KeyFrame kfDown = new KeyFrame(Duration.millis(300), scaleDownX, scaleDownY);

            timeline.getKeyFrames().addAll(kfUp, kfDown);
            timeline.play();
        }
    }

    private void applyIncorrectMatchStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label label = getLabelFromToggleButton(toggleButton);
        Rectangle backgroundRectangle = getRectangleFromToggleButton(toggleButton);

        if (label != null && backgroundRectangle != null) {
            // Clear existing styles
            label.getStyleClass().clear();
            backgroundRectangle.getStyleClass().clear();
            toggleButton.getStyleClass().clear();
            buttonStackPane.getStyleClass().clear();

            // Apply incorrect answer styles
            label.getStyleClass().add("activity-button__label--large-type-incorrect-answer-pressed");
            backgroundRectangle.getStyleClass()
                    .add("activity-button__background-rectangle--large-type-incorrect-answer-pressed");
            toggleButton.getStyleClass().add("activity-button--large-type-incorrect-answer-pressed");
            buttonStackPane.getStyleClass().add("activity-button-stackpane-type-incorrect-answer-pressed");

            // Optional: Add animation for incorrect answers
            Timeline animationTimeline = new Timeline();
            KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 0.9, Interpolator.EASE_OUT);
            KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 0.9, Interpolator.EASE_OUT);
            KeyFrame kfUp = new KeyFrame(Duration.millis(150), scaleUpX, scaleUpY);

            KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
            KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);
            KeyFrame kfDown = new KeyFrame(Duration.millis(300), scaleDownX, scaleDownY);

            animationTimeline.getKeyFrames().addAll(kfUp, kfDown);
            animationTimeline.play();

            // Schedule to reset the styles after 1 second
            Timeline resetTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                resetStyles(toggleButton);
                toggleButton.setSelected(false);
                // Reset selections if necessary
                if (toggleButton.equals(firstSelectedButton)) {
                    firstSelectedButton = null;
                }
                if (toggleButton.equals(secondSelectedButton)) {
                    secondSelectedButton = null;
                }
            }));
            resetTimeline.play();
        }
    }

    private void resetSelections() {
        if (firstSelectedButton != null) {
            firstSelectedButton.setSelected(false);
            firstSelectedButton = null;
        }
        if (secondSelectedButton != null) {
            secondSelectedButton.setSelected(false);
            secondSelectedButton = null;
        }
    }

    private void resetStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label label = getLabelFromToggleButton(toggleButton);
        Rectangle backgroundRectangle = getRectangleFromToggleButton(toggleButton);

        if (label != null && backgroundRectangle != null) {
            // Clear incorrect styles
            label.getStyleClass().remove("activity-button__label--large-type-incorrect-answer-pressed");
            backgroundRectangle.getStyleClass()
                    .remove("activity-button__background-rectangle--large-type-incorrect-answer-pressed");
            toggleButton.getStyleClass().remove("activity-button--large-type-incorrect-answer-pressed");
            buttonStackPane.getStyleClass().remove("activity-button-stackpane-type-incorrect-answer-pressed");

            // Reapply default unselected styles
            label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed");
            backgroundRectangle.getStyleClass()
                    .add("activity-toggle-button-background-rectangle-unselected-state-unpressed");
            toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed");
            buttonStackPane.getStyleClass().add("activity-toggle-button-stackpane");
        }
    }



    private void animateButtonScale(ToggleButton toggleButton, Runnable onFinished) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();

        Timeline timeline = new Timeline();
        KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 1.1, Interpolator.EASE_OUT);
        KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 1.1, Interpolator.EASE_OUT);
        KeyFrame kfUp = new KeyFrame(Duration.millis(150), scaleUpX, scaleUpY);

        KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
        KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);
        KeyFrame kfDown = new KeyFrame(Duration.millis(300), scaleDownX, scaleDownY);

        timeline.getKeyFrames().addAll(kfUp, kfDown);
        timeline.setOnFinished(event -> onFinished.run());
        timeline.play();
    }
}
