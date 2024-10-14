package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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

    // Map to hold the Timeline for each button
    private Map<ToggleButton, Timeline> buttonTimelines = new HashMap<>();

    @FXML
    public void initialize() {
        // Load data
        CharacterRecognitionDataAccess dataAccess = new CharacterRecognitionDataAccess();
        characterPairs = dataAccess.getCharacterRecognitionsByType("Hiragana");

        // Shuffle and pick first 5 pairs
        Collections.shuffle(characterPairs);
        characterPairs = characterPairs.subList(0, Math.min(5, characterPairs.size()));

        // Collect left and right button panes
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                Integer columnIndex = GridPane.getColumnIndex(node);
                if (columnIndex == null) columnIndex = 0;
                if (columnIndex == 0) {
                    leftButtonPanes.add((StackPane) node);
                } else if (columnIndex == 1) {
                    rightButtonPanes.add((StackPane) node);
                }
            }
        }

        // Shuffle buttons
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
        Label activity_label = null;

        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof ToggleButton) {
                toggleButton = (ToggleButton) child;
            } else if (child instanceof Label) {
                activity_label = (Label) child;
            }
        }

        if (toggleButton != null && activity_label != null) {
            activity_label.setText(text);
            buttonToCharacterMap.put(toggleButton, pair);
            toggleButton.getProperties().put("type", type);

            // Set up event handler
            final ToggleButton finalToggleButton = toggleButton;
            toggleButton.setOnAction(event -> {
                handleButtonSelection(finalToggleButton);
            });

            // Initialize button effects
            setupButtonEffects(buttonStackPane);
        }
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
            applyMatchStyles(firstSelectedButton);
            applyMatchStyles(secondSelectedButton);

            // Schedule disabling and style removal after 1 second
            Timeline disableTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                disableButtonAndRemoveStyles(firstSelectedButton);
                disableButtonAndRemoveStyles(secondSelectedButton);
            }));
            disableTimeline.play();
        } else {
            // Not a match - Apply incorrect styles
            applyIncorrectStyles(firstSelectedButton);
            applyIncorrectStyles(secondSelectedButton);
        }
    }

    private void disableButtonAndRemoveStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label activity_label = null;
        Rectangle activity_background_rectangle = null;

        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof Label) {
                activity_label = (Label) child;
            } else if (child instanceof Rectangle) {
                activity_background_rectangle = (Rectangle) child;
            }
        }

        if (activity_label != null && activity_background_rectangle != null) {
            // Remove correct styles
            activity_label.getStyleClass().remove("activity-button__label--large-type-correct-answer-pressed");
            activity_background_rectangle.getStyleClass().remove("activity-button__background-rectangle--large-type-correct-answer-pressed");
            toggleButton.getStyleClass().remove("activity-button--large-type-correct-answer-pressed");
            buttonStackPane.getStyleClass().remove("activity-button-stackpane-type-correct-answer-pressed");

            // Disable the button
            toggleButton.setDisable(true);
        }
    }

    private void applyMatchStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label activity_label = null;
        Rectangle activity_background_rectangle = null;

        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof Label) {
                activity_label = (Label) child;
            } else if (child instanceof Rectangle) {
                activity_background_rectangle = (Rectangle) child;
            }
        }

        if (activity_label != null && activity_background_rectangle != null) {
            // Clear existing styles
            activity_label.getStyleClass().clear();
            activity_background_rectangle.getStyleClass().clear();
            toggleButton.getStyleClass().clear();
            buttonStackPane.getStyleClass().clear();

            // Apply correct answer styles
            activity_label.getStyleClass().add("activity-button__label--large-type-correct-answer-pressed");
            activity_background_rectangle.getStyleClass().add("activity-button__background-rectangle--large-type-correct-answer-pressed");
            toggleButton.getStyleClass().add("activity-button--large-type-correct-answer-pressed");
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

    private void applyIncorrectStyles(ToggleButton toggleButton) {
        StackPane buttonStackPane = (StackPane) toggleButton.getParent();
        Label activity_label = null;
        Rectangle activity_background_rectangle = null;

        // Retrieve Label and Rectangle from the StackPane
        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof Label) {
                activity_label = (Label) child;
            } else if (child instanceof Rectangle) {
                activity_background_rectangle = (Rectangle) child;
            }
        }

        if (activity_label != null && activity_background_rectangle != null) {
            // Clear existing styles
            activity_label.getStyleClass().clear();
            activity_background_rectangle.getStyleClass().clear();
            toggleButton.getStyleClass().clear();
            buttonStackPane.getStyleClass().clear();

            // Apply incorrect answer styles
            activity_label.getStyleClass().add("activity-button__label--large-type-incorrect-answer-pressed");
            activity_background_rectangle.getStyleClass().add("activity-button__background-rectangle--large-type-incorrect-answer-pressed");
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
        Label activity_label = null;
        Rectangle activity_background_rectangle = null;

        // Retrieve Label and Rectangle from the StackPane
        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof Label) {
                activity_label = (Label) child;
            } else if (child instanceof Rectangle) {
                activity_background_rectangle = (Rectangle) child;
            }
        }

        if (activity_label != null && activity_background_rectangle != null) {
            // Clear incorrect styles
            activity_label.getStyleClass().remove("activity-button__label--large-type-incorrect-answer-pressed");
            activity_background_rectangle.getStyleClass().remove("activity-button__background-rectangle--large-type-incorrect-answer-pressed");
            toggleButton.getStyleClass().remove("activity-button--large-type-incorrect-answer-pressed");
            buttonStackPane.getStyleClass().remove("activity-button-stackpane-type-incorrect-answer-pressed");

            // Reapply default pressed styles
            activity_label.getStyleClass().add("activity-button__label--large-type-pressed");
            activity_background_rectangle.getStyleClass().add("activity-button__background-rectangle--large-type-pressed");
            toggleButton.getStyleClass().add("activity-button--large-type-pressed");
            buttonStackPane.getStyleClass().add("activity-button-stackpane-type-pressed");
        }
    }

    private void setupButtonEffects(StackPane buttonStackPane) {
        // Find the ToggleButton, Label, and Rectangle inside this StackPane
        ToggleButton toggleButton = null;
        Label activity_label = null;
        Rectangle activity_background_rectangle = null;

        for (Node child : buttonStackPane.getChildren()) {
            if (child instanceof ToggleButton) {
                toggleButton = (ToggleButton) child;
            } else if (child instanceof Label) {
                activity_label = (Label) child;
            } else if (child instanceof Rectangle) {
                activity_background_rectangle = (Rectangle) child;
            }
        }

        if (toggleButton != null && activity_label != null && activity_background_rectangle != null) {
            buttonStackPane.setScaleX(1.0);
            buttonStackPane.setScaleY(1.0);

            Timeline buttonScaleTimeline = new Timeline();
            buttonTimelines.put(toggleButton, buttonScaleTimeline);

            // Use final variables for lambda expressions
            final ToggleButton tb = toggleButton;
            final Label label = activity_label;
            final Rectangle rect = activity_background_rectangle;
            final StackPane sp = buttonStackPane;
            final Timeline tl = buttonScaleTimeline;

            tb.pressedProperty().addListener((obs, wasPressed, isNowPressed) -> {
                updateStyles(tb, label, rect);
            });

            tb.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                animateButtonScale(tb, label, rect, sp, tl);
            });
        }
    }

    private void animateButtonScale(ToggleButton toggleButton, Label activity_label,
                                    Rectangle activity_background_rectangle, StackPane buttonStackPane,
                                    Timeline buttonScaleTimeline) {
        boolean isSelected = toggleButton.isSelected();

        buttonScaleTimeline.stop();

        if (isSelected) {
            Interpolator overshootInterpolator = new Interpolator() {
                @Override
                protected double curve(double t) {
                    double s = 1.70158 * 1.02;
                    t -= 1.0;
                    return t * t * ((s + 1) * t + s) + 1.0;
                }
            };

            KeyValue scaleUpX = new KeyValue(buttonStackPane.scaleXProperty(), 1.1, overshootInterpolator);
            KeyValue scaleUpY = new KeyValue(buttonStackPane.scaleYProperty(), 1.1, overshootInterpolator);

            KeyValue scaleDownX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_IN);
            KeyValue scaleDownY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_IN);

            KeyFrame kfUp = new KeyFrame(Duration.millis(150), scaleUpX, scaleUpY);
            KeyFrame kfDown = new KeyFrame(Duration.millis(300), scaleDownX, scaleDownY);

            buttonScaleTimeline.getKeyFrames().setAll(kfUp, kfDown);

            activity_label.getStyleClass().removeAll("label-selected", "label-animating");
            activity_background_rectangle.getStyleClass().removeAll("rectangle-selected", "rectangle-pressed-selected");

            if (isSelected) {
                activity_label.getStyleClass().add("label-animating");
                activity_background_rectangle.getStyleClass().add("rectangle-selected"); // **Add this line**
            }

            buttonScaleTimeline.setOnFinished(event -> {
                activity_label.getStyleClass().remove("label-animating");

                if (isSelected) {
                    activity_label.getStyleClass().add("label-selected");
                }
            });

            buttonScaleTimeline.play();
        } else {
            Timeline resetTimeline = new Timeline();

            KeyValue resetScaleX = new KeyValue(buttonStackPane.scaleXProperty(), 1.0, Interpolator.EASE_OUT);
            KeyValue resetScaleY = new KeyValue(buttonStackPane.scaleYProperty(), 1.0, Interpolator.EASE_OUT);

            KeyFrame resetKF = new KeyFrame(Duration.millis(250), resetScaleX, resetScaleY);
            resetTimeline.getKeyFrames().add(resetKF);

            activity_label.getStyleClass().removeAll("label-selected");
            activity_background_rectangle.getStyleClass().removeAll("rectangle-selected"); // **Remove this line**

            resetTimeline.play();
        }
    }

    private void updateStyles(ToggleButton toggleButton, Label activity_label, Rectangle activity_background_rectangle) {
        boolean isPressed = toggleButton.isPressed();

        activity_label.getStyleClass().removeAll("label-pressed", "label-pressed-selected");
        activity_background_rectangle.getStyleClass().removeAll("rectangle-pressed", "rectangle-pressed-selected");

        if (isPressed) {
            if (toggleButton.isSelected()) {
                activity_label.getStyleClass().add("label-pressed-selected");
                activity_background_rectangle.getStyleClass().add("rectangle-pressed-selected");
            } else {
                activity_label.getStyleClass().add("label-pressed");
                activity_background_rectangle.getStyleClass().add("rectangle-pressed");
            }
        }
    }
}
