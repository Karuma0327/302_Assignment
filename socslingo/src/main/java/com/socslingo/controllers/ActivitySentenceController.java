package com.socslingo.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActivitySentenceController {

    @FXML
    private FlowPane wordButtonsPane;

    @FXML
    private HBox sentenceLine;

    @FXML
    private VBox rootVBox; // Reference to the root VBox

    @FXML
    private Pane animationPane; // Pane for animations

    // Map to store original StackPane of each ToggleButton
    private final Map<ToggleButton, StackPane> originalStackPanes = new HashMap<>();

    // Add this field to track animating buttons
    private final Set<ToggleButton> animatingButtons = new HashSet<>();

    private final Map<ToggleButton, Integer> targetPositions = new HashMap<>();

    private ActivityMainTestController mainController;

    // Duration for the shift animation
    private static final Duration SHIFT_DURATION = Duration.millis(300);

    @FXML
    public void initialize() {
        // Iterate through FlowPane's children (HBox)
        for (Node hboxNode : wordButtonsPane.getChildren()) {
            if (hboxNode instanceof HBox) {
                HBox hbox = (HBox) hboxNode;
                // Iterate through HBox's children (StackPane)
                for (Node stackPaneNode : hbox.getChildren()) {
                    if (stackPaneNode instanceof StackPane) {
                        StackPane stackPane = (StackPane) stackPaneNode;
                        // Iterate through StackPane's children to find ToggleButton
                        for (Node node : stackPane.getChildren()) {
                            if (node instanceof ToggleButton) {
                                ToggleButton toggleButton = (ToggleButton) node;
                                originalStackPanes.put(toggleButton, stackPane);
                                break; // Assuming only one ToggleButton per StackPane
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void handleWordButtonPress(javafx.event.ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();

        if (button.isSelected()) {
            // Use Platform.runLater to ensure layout is complete
            Platform.runLater(() -> animateButtonMovement(button, true));
        } else {
            Platform.runLater(() -> animateButtonMovement(button, false));
        }
    }

    /**
     * Animates the movement of a ToggleButton to or from the sentence line.
     *
     * @param button         The ToggleButton to animate.
     * @param toSentenceLine If true, animate to the sentence line; otherwise, animate back.
     */
    private void animateButtonMovement(ToggleButton button, boolean toSentenceLine) {
        animatingButtons.add(button);

        // Calculate the target index dynamically
        int targetIndex = calculateTargetIndex(button);

        System.out.println("Button '" + button.getText() + "' assigned to target index " + targetIndex);

        // Store the target position for this button
        targetPositions.put(button, targetIndex);

        // Get the Scene to convert coordinates
        Scene scene = rootVBox.getScene();

        if (scene == null) {
            // Scene is not yet initialized
            return;
        }

        // Calculate start position in scene coordinates
        Bounds buttonBoundsInScene = button.localToScene(button.getBoundsInLocal());

        double startXScene = buttonBoundsInScene.getMinX();
        double startYScene = buttonBoundsInScene.getMinY();

        // Convert start position to animationPane's local coordinates
        Point2D startPointInAnimationPane = animationPane.sceneToLocal(startXScene, startYScene);

        // Determine target container
        Parent targetContainer = toSentenceLine ? sentenceLine : originalStackPanes.get(button);
        if (targetContainer == null) {
            // If original container not found, log and exit
            System.err.println("Original container not found for button: " + button.getText());
            return;
        }

        double endXScene = 0;
        double endYScene = 0;

        if (toSentenceLine && targetContainer instanceof HBox) {
            HBox hbox = (HBox) targetContainer;

            // Calculate the starting position of the HBox in scene coordinates
            Bounds hboxBoundsInScene = hbox.localToScene(hbox.getBoundsInLocal());

            double hboxStartXScene = hboxBoundsInScene.getMinX();
            double hboxStartYScene = hboxBoundsInScene.getMinY();

            // Determine the index where the button will be placed
            int index = hbox.getChildren().size();

            // Calculate the cumulative width of existing buttons
            double cumulativeWidth = 0;
            for (int i = 0; i < index; i++) {
                Node child = hbox.getChildren().get(i);
                double childWidth = 0;
                if (child instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) child;
                    tb.applyCss();
                    tb.layout();
                    childWidth = tb.getWidth();
                } else if (child instanceof StackPane) {
                    StackPane sp = (StackPane) child;
                    sp.applyCss();
                    sp.layout();
                    childWidth = sp.getWidth();
                }
                cumulativeWidth += childWidth;
                cumulativeWidth += hbox.getSpacing();
            }

            // Calculate end position in scene coordinates
            endXScene = hboxStartXScene + cumulativeWidth;
            endYScene = hboxStartYScene;
        } else if (!toSentenceLine && targetContainer instanceof StackPane) {
            StackPane stackPane = (StackPane) targetContainer;

            // Calculate the starting position of the StackPane in scene coordinates
            Bounds stackBoundsInScene = stackPane.localToScene(stackPane.getBoundsInLocal());

            double stackStartXScene = stackBoundsInScene.getMinX();
            double stackStartYScene = stackBoundsInScene.getMinY();

            // Calculate end position in scene coordinates
            endXScene = stackStartXScene;
            endYScene = stackStartYScene;
        } else {
            // Fallback: use targetContainer's top-left position in scene coordinates
            Bounds targetBoundsInScene = targetContainer.localToScene(targetContainer.getBoundsInLocal());

            endXScene = targetBoundsInScene.getMinX();
            endYScene = targetBoundsInScene.getMinY();
        }

        // Convert end position to animationPane's local coordinates
        Point2D endPointInAnimationPane = animationPane.sceneToLocal(endXScene, endYScene);

        // Debugging: Print start and end positions
        System.out.println("Animating button '" + button.getText() + "' from ("
                + startPointInAnimationPane.getX() + ", " + startPointInAnimationPane.getY()
                + ") to (" + endPointInAnimationPane.getX() + ", " + endPointInAnimationPane.getY() + ")");

        // Create a snapshot of the button to animate
        ImageView snapshot = new ImageView(button.snapshot(null, null));

        // Position the snapshot at the button's current location within animationPane
        snapshot.setLayoutX(startPointInAnimationPane.getX());
        snapshot.setLayoutY(startPointInAnimationPane.getY());
        snapshot.setPickOnBounds(false); // Ensure the snapshot doesn't interfere with mouse events

        // Add the snapshot to the animationPane
        animationPane.getChildren().add(snapshot);

        

        // Fade out the original button instead of using TranslateTransition
        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(50), button);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeOutTransition.setOnFinished(e -> button.setVisible(false));
        fadeOutTransition.play();

        // Calculate the translation deltas
        double deltaX = endPointInAnimationPane.getX() - startPointInAnimationPane.getX();
        double deltaY = endPointInAnimationPane.getY() - startPointInAnimationPane.getY();

        // Create the transition for the snapshot
        TranslateTransition transition = new TranslateTransition(Duration.millis(150), snapshot);
        transition.setFromX(0);
        transition.setFromY(0);
        transition.setToX(deltaX);
        transition.setToY(deltaY);
        transition.setInterpolator(Interpolator.EASE_BOTH); // Smooth easing

        transition.setOnFinished(e -> {
            // Remove the snapshot after animation
            animationPane.getChildren().remove(snapshot);

            if (toSentenceLine) {
                moveButtonToSentenceLine(button, targetIndex);
            } else {
                moveButtonBack(button);
            }

            // Make the button visible again in its new location
            Platform.runLater(() -> {
                button.setOpacity(1.0);
                button.setVisible(true);
            });

            // Remove the button from the animating set
            animatingButtons.remove(button);
            targetPositions.remove(button);
        });

        transition.play();
    }

    private int calculateTargetIndex(ToggleButton button) {
        // Start with the current number of children in sentenceLine
        int index = sentenceLine.getChildren().size();
        
        // Count the number of buttons already animating to the sentence line
        long animatingToSentenceLine = targetPositions.values().stream()
            .filter(targetIdx -> targetIdx >= index)
            .count();
        
        // The new target index is the current size plus the number already animating
        return index + (int) animatingToSentenceLine;
    }
    
    
    /**
     * Moves the ToggleButton to the sentence line without animation.
     *
     * @param button The ToggleButton to move.
     */
    private void moveButtonToSentenceLine(ToggleButton button, int targetIndex) {
        // Remove from original StackPane
        StackPane originalStackPane = originalStackPanes.get(button);
        if (originalStackPane != null && originalStackPane.getChildren().contains(button)) {
            originalStackPane.getChildren().remove(button);
        }

        // Add to sentence line at the calculated target index
        if (!sentenceLine.getChildren().contains(button)) {
            // Ensure targetIndex is within bounds
            if (targetIndex > sentenceLine.getChildren().size()) {
                targetIndex = sentenceLine.getChildren().size();
            }
            sentenceLine.getChildren().add(targetIndex, button);
            System.out.println("Button '" + button.getText() + "' moved to sentenceLine at index " + targetIndex + ".");
        } else {
            System.out.println("Button '" + button.getText() + "' is already in sentenceLine.");
        }
    }

    /**
     * Moves the ToggleButton back to its original StackPane without animation
     * and animates the shifting of trailing buttons.
     *
     * @param button The ToggleButton to move.
     */
    private void moveButtonBack(ToggleButton button) {
        // Identify the index of the button to be removed
        int removedIndex = sentenceLine.getChildren().indexOf(button);

        // Remove from sentence line if present
        if (sentenceLine.getChildren().contains(button)) {
            sentenceLine.getChildren().remove(button);
        } else {
            System.out.println("Button '" + button.getText() + "' is not present in sentenceLine.");
        }

        // Add back to original StackPane only if it's not already present
        StackPane originalStackPane = originalStackPanes.get(button);
        if (originalStackPane != null && !originalStackPane.getChildren().contains(button)) {
            originalStackPane.getChildren().add(button);
            System.out.println("Button '" + button.getText() + "' moved back to original StackPane.");
        } else {
            System.out.println(
                "Button '" + button.getText() + "' is already in original StackPane or originalStackPane is null.");
            // Optionally handle case where original StackPane is not found
            boolean added = false;
            for (Node hboxNode : wordButtonsPane.getChildren()) {
                if (hboxNode instanceof HBox) {
                    HBox hbox = (HBox) hboxNode;
                    for (Node stackPaneNode : hbox.getChildren()) {
                        if (stackPaneNode instanceof StackPane) {
                            StackPane stackPane = (StackPane) stackPaneNode;
                            if (!stackPane.getChildren().contains(button)) {
                                stackPane.getChildren().add(button);
                                System.out.println("Button '" + button.getText()
                                        + "' added to a StackPane within wordButtonsPane as a fallback.");
                                added = true;
                                break;
                            }
                        }
                    }
                }
                if (added)
                    break;
            }
            if (!added) {
                System.err.println("Failed to move button '" + button.getText() + "' back to original location.");
            }
        }

        // Animate the shifting of remaining buttons
        animateShiftLeft(removedIndex);
    }

    /**
     * Animates the shifting of buttons to the left starting from the specified index.
     *
     * @param startIndex The index from which the shift should start.
     */
    private void animateShiftLeft(int startIndex) {
        for (int i = startIndex; i < sentenceLine.getChildren().size(); i++) {
            Node node = sentenceLine.getChildren().get(i);

            // Set the initial translation to the right position
            double initialTranslateX = node.getBoundsInParent().getWidth() + sentenceLine.getSpacing();
            node.setTranslateX(initialTranslateX);

            // Create a TranslateTransition for each node
            TranslateTransition transition = new TranslateTransition(SHIFT_DURATION, node);
            transition.setFromX(initialTranslateX);
            transition.setToX(0);
            transition.setInterpolator(Interpolator.EASE_BOTH);

            transition.play();
        }
    }

    public void setMainController(ActivityMainTestController mainController) {
        this.mainController = mainController;
    }
}