package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
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

    private static final double ANIMATION_DURATION = 125;

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {

        // Load data
        CharacterRecognitionDataAccess dataAccess = new CharacterRecognitionDataAccess();
        characterPairs = dataAccess.getCharacterRecognitionsByType("Hiragana_Main");

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

            // Left button: romaji (first in pair)
            StackPane leftPane = leftButtonPanes.get(i);
            setupButton(leftPane, pair.getRomaji(), "romaji", pair, 1); // pairOrder = 1

            // Right button: hiragana (second in pair)
            StackPane rightPane = rightButtonPanes.get(i);
            setupButton(rightPane, pair.getCharacter(), "hiragana", pair, 2); // pairOrder = 2
        }

        titleLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double newBottomMargin = newBounds.getHeight() * 0.05; // 5% of the height
            double newLeftMargin = newBounds.getWidth() * -0.05; // -5% of the width
            titleLabel.setPadding(new Insets(0, 0, newBottomMargin, newLeftMargin));
        });
    }

    private void setupButton(StackPane buttonStackPane, String text, String type, CharacterRecognition pair,
            int pairOrder) {
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
            finalToggleButton.getProperties().put("pairOrder", pairOrder); // Assign pairOrder

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
        if (toggleButton.isDisabled()) {
            return;
        }

        if (isSelected) {
            // Perform selection animations and style updates
            animateLabelSize(toggleButton, SELECTED_WIDTH, SELECTED_HEIGHT, () -> {
                updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, true);
                animateButtonScale(toggleButton, () -> {
                    // Additional actions after animation if needed
                });
            });
            updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, false);
        } else {
            // Revert to unselected styles without animation
            animateLabelSize(toggleButton, UNSELECTED_WIDTH, UNSELECTED_HEIGHT, () -> {
                updateLabelStyle(toggleButton, label, backgroundRectangle, isSelected, true);
            });
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
     * Updated method to apply style classes based on the toggle button's state and
     * type.
     *
     * @param toggleButton        The ToggleButton being updated.
     * @param label               The associated Label.
     * @param backgroundRectangle The associated Rectangle.
     * @param isSelected          Whether the toggle button is selected.
     * @param isPressed           Whether the toggle button is pressed.
     */
    private void updateLabelStyle(ToggleButton toggleButton, Label label, Rectangle backgroundRectangle,
            boolean isSelected, boolean isPressed) {
        // Clear existing style classes
        label.getStyleClass().removeAll(
                "activity-toggle-button-label-unselected-state-unpressed",
                "activity-toggle-button-label-unselected-state-pressed",
                "activity-toggle-button-label-selected-state-unpressed",
                "activity-toggle-button-label-selected-state-pressed",
                "activity-toggle-button-label-selected-state-unpressed-border",
                "activity-toggle-button-label-selected-animation-in-progress-state",
                "activity-toggle-button-label-unselected-state-pressed-grey",
                "activity-toggle-button-label-unselected-state-pressed-grey-japanese",
                "activity-toggle-button-label-unselected-state-unpressed-japanese", // Added for Japanese
                "activity-toggle-button-label-unselected-state-pressed-japanese", // Added for Japanese
                "activity-toggle-button-label-selected-state-unpressed-japanese", // Added for Japanese
                "activity-toggle-button-label-selected-state-pressed-japanese" // Added for Japanese

        );

        backgroundRectangle.getStyleClass().removeAll(
                "activity-toggle-button-background-rectangle-unselected-state-unpressed",
                "activity-toggle-button-background-rectangle-unselected-state-pressed",
                "activity-toggle-button-background-rectangle-selected-state-unpressed",
                "activity-toggle-button-background-rectangle-selected-state-pressed",
                "activity-toggle-button-background-rectangle-selected-state-unpressed-border",
                "activity-toggle-button-background-rectangle-selected-animation-in-progress-state",
                "activity-toggle-button-background-rectangle-unselected-state-unpressed-japanese", // Added
                "activity-toggle-button-background-rectangle-unselected-state-pressed-japanese", // Added
                "activity-toggle-button-background-rectangle-selected-state-unpressed-japanese", // Added
                "activity-toggle-button-background-rectangle-selected-state-pressed-japanese" // Added
        );

        toggleButton.getStyleClass().removeAll(
                "activity-toggle-button-unselected-state-pressed",
                "activity-toggle-button-selected-state-pressed",
                "activity-toggle-button-selected-state-unpressed",
                "activity-toggle-button-selected-state-pressed",
                "activity-toggle-button-selected-state-unpressed-border",
                "activity-toggle-button-selected-animation-in-progress-state",
                "activity-toggle-button-unselected-state-unpressed-japanese", // Added
                "activity-toggle-button-unselected-state-pressed-japanese", // Added
                "activity-toggle-button-selected-state-unpressed-japanese", // Added
                "activity-toggle-button-selected-state-pressed-japanese" // Added
        );

        // Retrieve the button type
        String type = (String) toggleButton.getProperties().get("type");

        // Determine if the button is Japanese (hiragana)
        boolean isJapanese = "hiragana".equalsIgnoreCase(type);

        // Define style class suffix based on button type
        String suffix = isJapanese ? "-japanese" : "";

        // Add the appropriate style class based on selection and animation state
        if (isSelected) {
            // If the toggle button is pressed and selected
            if (toggleButton.isPressed()) {
                label.getStyleClass().add("activity-toggle-button-label-selected-state-pressed" + suffix);
                backgroundRectangle.getStyleClass()
                        .add("activity-toggle-button-background-rectangle-selected-state-pressed" + suffix);
                toggleButton.getStyleClass().add("activity-toggle-button-selected-state-pressed" + suffix);

            } else {
                // If the animation is complete and the button is selected
                if (isPressed) { // Changed from 'isAnimationComplete' to 'isPressed' to ensure correct state
                    label.getStyleClass()
                            .add("activity-toggle-button-label-selected-state-unpressed" + suffix);
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-selected-state-unpressed" + suffix);
                    toggleButton.getStyleClass().add("activity-toggle-button-selected-state-unpressed" + suffix);

                } else {
                    // If the button is selected but the animation is not complete
                    label.getStyleClass().add("activity-toggle-button-label-selected-state-unpressed" + suffix);
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-selected-animation-in-progress-state"
                                    + suffix);
                    toggleButton.getStyleClass()
                            .add("activity-toggle-button-selected-animation-in-progress-state" + suffix);
                }
            }
        } else {
            // If the toggle button is pressed and not selected
            if (toggleButton.isPressed()) {
                if (isJapanese) {
                    Integer pairOrder = (Integer) toggleButton.getProperties().get("pairOrder");
                    if (pairOrder != null && pairOrder == 2) {
                        label.getStyleClass()
                                .add("activity-toggle-button-label-unselected-state-pressed-grey-japanese");
                    } else {
                        label.getStyleClass().add("activity-toggle-button-label-unselected-state-pressed-japanese");
                    }
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-pressed-japanese");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-pressed-japanese");
                } else {
                    Integer pairOrder = (Integer) toggleButton.getProperties().get("pairOrder");
                    if (pairOrder != null && pairOrder == 2) {
                        label.getStyleClass().add("activity-toggle-button-label-unselected-state-pressed-grey");
                    } else {
                        label.getStyleClass().add("activity-toggle-button-label-unselected-state-pressed");
                    }
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-pressed");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-pressed");
                }
            } else {
                // If the button is not selected and not pressed
                if (isJapanese) {
                    label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed-japanese");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-unpressed-japanese");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed-japanese");
                } else {
                    label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-unpressed");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed");
                }
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

            // Determine if the button is Japanese (hiragana)
            String type = (String) toggleButton.getProperties().get("type");
            boolean isJapanese = "hiragana".equalsIgnoreCase(type);

            // Apply correct answer styles based on type
            if (isJapanese) {
                label.getStyleClass().add("activity-correct-response-toggle-button-label-japanese");
                backgroundRectangle.getStyleClass()
                        .add("activity-correct-response-toggle-button-background-rectangle-japanese");
                toggleButton.getStyleClass().add("activity-correct-response-toggle-button-japanese");
                buttonStackPane.getStyleClass().add("activity-correct-response-toggle-button-stackpane-japanese");
            } else {
                label.getStyleClass().add("activity-correct-response-toggle-button-label");
                backgroundRectangle.getStyleClass()
                        .add("activity-correct-response-toggle-button-background-rectangle");
                toggleButton.getStyleClass().add("activity-correct-response-toggle-button");
                buttonStackPane.getStyleClass().add("activity-correct-response-toggle-button-stackpane");
            }

            // Disable the button to prevent further interactions
            toggleButton.setDisable(true);

            // Ensure the selected state is reset to prevent default styles
            toggleButton.setSelected(false);

            // Perform overshoot animation
            Timeline timeline = new Timeline();
            KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 1.1, Interpolator.EASE_OUT);
            KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 1.1, Interpolator.EASE_OUT);
            KeyFrame kfUp = new KeyFrame(Duration.millis(ANIMATION_DURATION), scaleUpX, scaleUpY);

            KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
            KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);
            KeyFrame kfDown = new KeyFrame(Duration.millis(ANIMATION_DURATION*2), scaleDownX, scaleDownY);

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

            // Determine if the button is Japanese (hiragana)
            String type = (String) toggleButton.getProperties().get("type");
            boolean isJapanese = "hiragana".equalsIgnoreCase(type);

            // Apply incorrect answer styles based on type
            if (isJapanese) {
                label.getStyleClass().add("activity-incorrect-response-toggle-button-label-japanese");
                backgroundRectangle.getStyleClass()
                        .add("activity-incorrect-response-toggle-button-background-rectangle-japanese");
                toggleButton.getStyleClass().add("activity-incorrect-response-toggle-button-japanese");
                buttonStackPane.getStyleClass().add("activity-incorrect-response-toggle-button-stackpane-japanese");
            } else {
                label.getStyleClass().add("activity-incorrect-response-toggle-button-label");
                backgroundRectangle.getStyleClass()
                        .add("activity-incorrect-response-toggle-button-background-rectangle");
                toggleButton.getStyleClass().add("activity-incorrect-response-toggle-button");
                buttonStackPane.getStyleClass().add("activity-incorrect-response-toggle-button-stackpane");
            }

            // Ensure the selected state is reset to prevent default styles
            toggleButton.setSelected(false);
            // Optional: Add animation for incorrect answers
            Timeline animationTimeline = new Timeline();
            KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 0.9, Interpolator.EASE_OUT);
            KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 0.9, Interpolator.EASE_OUT);
            KeyFrame kfUp = new KeyFrame(Duration.millis(ANIMATION_DURATION), scaleUpX, scaleUpY);

            KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
            KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);
            KeyFrame kfDown = new KeyFrame(Duration.millis(ANIMATION_DURATION*2), scaleDownX, scaleDownY);

            animationTimeline.getKeyFrames().addAll(kfUp, kfDown);
            animationTimeline.play();

            // Schedule to reset the styles after 1 second
            Timeline resetTimeline = new Timeline(new KeyFrame(Duration.millis(750), ev -> {
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
            // Step 1: Fade Out and Scale Down
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), buttonStackPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), buttonStackPane);
            scaleDown.setFromX(1.0);
            scaleDown.setFromY(1.0);
            scaleDown.setToX(0.9);
            scaleDown.setToY(0.9);

            ParallelTransition fadeAndScaleOut = new ParallelTransition(fadeOut, scaleDown);

            fadeAndScaleOut.setOnFinished(evt -> {
                // Clear incorrect styles
                label.getStyleClass().removeAll(
                        "activity-incorrect-response-toggle-button-label",
                        "activity-incorrect-response-toggle-button-label-japanese");
                backgroundRectangle.getStyleClass().removeAll(
                        "activity-incorrect-response-toggle-button-background-rectangle",
                        "activity-incorrect-response-toggle-button-background-rectangle-japanese");
                toggleButton.getStyleClass().removeAll(
                        "activity-incorrect-response-toggle-button",
                        "activity-incorrect-response-toggle-button-japanese");
                buttonStackPane.getStyleClass().removeAll(
                        "activity-incorrect-response-toggle-button-stackpane",
                        "activity-incorrect-response-toggle-button-stackpane-japanese");

                // Determine if the button is Japanese (hiragana)
                String type = (String) toggleButton.getProperties().get("type");
                boolean isJapanese = "hiragana".equalsIgnoreCase(type);

                // Reapply default unselected styles based on type
                if (isJapanese) {
                    label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed-japanese");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-unpressed-japanese");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed-japanese");
                    buttonStackPane.getStyleClass().add("activity-toggle-button-stackpane-japanese");
                } else {
                    label.getStyleClass().add("activity-toggle-button-label-unselected-state-unpressed");
                    backgroundRectangle.getStyleClass()
                            .add("activity-toggle-button-background-rectangle-unselected-state-unpressed");
                    toggleButton.getStyleClass().add("activity-toggle-button-unselected-state-unpressed");
                    buttonStackPane.getStyleClass().add("activity-toggle-button-stackpane");
                }

                // Step 2: Fade In and Scale Up
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), buttonStackPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), buttonStackPane);
                scaleUp.setFromX(0.9);
                scaleUp.setFromY(0.9);
                scaleUp.setToX(1.0);
                scaleUp.setToY(1.0);

                ParallelTransition fadeAndScaleIn = new ParallelTransition(fadeIn, scaleUp);
                fadeAndScaleIn.setOnFinished(event -> {
                    // Optionally, perform additional actions after the transition
                });

                fadeAndScaleIn.play();
            });

            // Play fade and scale out transitions
            fadeAndScaleOut.play();
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
