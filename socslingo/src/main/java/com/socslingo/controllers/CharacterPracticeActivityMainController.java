package com.socslingo.controllers;

import java.io.IOException;

import com.socslingo.data.SelectedCategory;

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

public class CharacterPracticeActivityMainController {

    @FXML
    private HBox activity_content_area;

    @FXML
    private Button check_button;

    @FXML
    private StackPane check_button_stackpane;

    @FXML
    private Label check_button_label;

    @FXML
    private Rectangle check_button_background_rectangle;

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
    private HBox bottom_section;

    @FXML
    private ProgressBar progress_bar;

    @FXML
    private Label progress_label;

    private Node preloadedActivity;
    private boolean isPreloading = false;
    private boolean isSkipped = false;
    private int completionCount = 0;
    private final int TOTAL_COMPLETIONS = 3;
    private Node originalSkipControlContent;

    private boolean lastAnswerCorrect;

    // **Added:** Heart management fields
    private int hearts = 5;

    @FXML
    public void initialize() {
        System.out.println("CharacterPracticeActivityMainController initialized.");
        if (!skip_control_container.getChildren().isEmpty()) {
            originalSkipControlContent = skip_control_container.getChildren().get(0);
        }

        hearts = 5; // **Added:** Initialize hearts
        progress_label.setText(String.valueOf(hearts)); // **Added:** Display initial hearts

        progress_bar.setProgress(0.0);

        try {
            loadCharacterRecognitionActivity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        preloadNextActivity();

        exit_button.setOnAction(event -> {
            System.out.println("Exit button clicked.");
            try {
                PrimaryController.getInstance().switchToHome();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        check_button.setDisable(true);

        skip_button.setOnAction(event -> {
            System.out.println("Skip button clicked.");
            handleSkipButton();
            decreaseHeart(); // **Added:** Decrease heart on skip
        });


        bottom_section.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, null)));
    }

    public void enableCheckButton() {
        System.out.println("Enabling CHECK button.");
        check_button.setDisable(false);
        check_button_label.setDisable(false);
        
        check_button_label.getStyleClass().remove("activity-button__label--medium-type-check-answer-unclickable");
        check_button.getStyleClass().remove("activity-button--medium-type-check-answer-unclickable");
        check_button_background_rectangle.getStyleClass().remove("activity-button__background-rectangle--medium-type-check-answer-unclickable");
        check_button_stackpane.getStyleClass().remove("activity-button-stackpane-type-check-answer-unclickable");

        check_button_label.getStyleClass().add("activity-check-button__label");
        check_button.getStyleClass().add("activity-check-button");
        check_button_background_rectangle.getStyleClass().add("activity-check-button__background-rectangle");
        check_button_stackpane.getStyleClass().add("activity-check-button-stackpane");
    }

    private void animateBottomSectionBackground(Color targetColor, double durationMillis) {
        Background currentBackground = bottom_section.getBackground();
        final Color currentColor;

        if (currentBackground != null && !currentBackground.getFills().isEmpty()) {
            BackgroundFill fill = currentBackground.getFills().get(0);
            if (fill.getFill() instanceof Color) {
                currentColor = (Color) fill.getFill();
            } else {
                currentColor = Color.WHITE;
            }
        } else {
            currentColor = Color.WHITE;
        }

        ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(currentColor);
        colorProperty.addListener((obs, oldColor, newColor) -> {
            bottom_section.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, null)));
        });

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorProperty, currentColor)),
            new KeyFrame(Duration.millis(durationMillis), new KeyValue(colorProperty, targetColor))
        );

        timeline.play();
    }

    public void handleCheckButton() {
        System.out.println("CHECK button clicked.");
        check_button.setDisable(true);

        CharacterPracticeActivityThreeOptionController activityController = getActivityController();
        if (activityController != null) {
            boolean isCorrect = activityController.checkAnswer();
            System.out.println("Answer correctness: " + isCorrect);

            lastAnswerCorrect = isCorrect;

            if (isCorrect) {
                
                resetBottomSectionStyle();

                bottom_section.getStyleClass().remove("bottom-section-neutral");
                bottom_section.getStyleClass().add("bottom-section-correct");

                Color correctColor = Color.rgb(227, 250, 197);
                animateBottomSectionBackground(correctColor, 500);
                activityController.disableToggleButtons(); 
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/correct_replacement.fxml"));
                    Node correctReplacement = loader.load();
                    skip_control_container.getChildren().setAll(correctReplacement);
                    System.out.println("Loaded correct_replacement.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                enableContinueButton();
            } else {
                
                resetBottomSectionStyle();

                bottom_section.getStyleClass().remove("bottom-section-neutral");
                bottom_section.getStyleClass().add("bottom-section-incorrect");

                Color incorrectColor = Color.rgb(242, 224, 224);
                animateBottomSectionBackground(incorrectColor, 500);
                activityController.disableToggleButtons(); 
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/incorrect_replacement.fxml"));
                    Node incorrectReplacement = loader.load();
                    skip_control_container.getChildren().setAll(incorrectReplacement);
                    System.out.println("Loaded incorrect_replacement.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                enableContinueButton();
                decreaseHeart(); // **Added:** Decrease heart on wrong answer
            }
        } else {
            System.err.println("Activity controller is null.");
        }
    }

    public void enableContinueButton() {
        System.out.println("Enabling CONTINUE button.");
        check_button_label.setText("CONTINUE");
    
        removeCheckButtonStyles();
    
        if (lastAnswerCorrect) {
            check_button_label.getStyleClass().add("activity-continue-button__label--medium-type-correct-hover");
            check_button.getStyleClass().add("activity-continue-button--medium-type-correct-hover");
            check_button_background_rectangle.getStyleClass().add("activity-continue-button__background-rectangle--medium-type-correct-hover");
            check_button_stackpane.getStyleClass().add("activity-continue-button-stackpane-type-correct-hover");
        } else {
            check_button_label.getStyleClass().add("activity-continue-button__label--medium-type-incorrect-hover");
            check_button.getStyleClass().add("activity-continue-button--medium-type-incorrect-hover");
            check_button_background_rectangle.getStyleClass().add("activity-continue-button__background-rectangle--medium-type-incorrect-hover");
            check_button_stackpane.getStyleClass().add("activity-continue-button-stackpane-type-incorrect-hover");
        }
    
        check_button.setOnAction(event -> {
            System.out.println("CONTINUE button clicked.");
            handleContinueButton();
        });
    
        check_button.setDisable(false);
        check_button_label.setDisable(false);
    }

    private void removeCheckButtonStyles() {
        check_button_label.getStyleClass().removeAll(
            "activity-button__label--medium-type-check-answer-unclickable",
            "activity-check-button__label--medium-type-correct-hover",
            "activity-check-button__label--medium-type-incorrect-hover",
            "activity-check-button__label",
            "activity-continue-button__label--medium-type-correct-hover",
            "activity-continue-button__label--medium-type-incorrect-hover"
        );
        check_button.getStyleClass().removeAll(
            "activity-button--medium-type-check-answer-unclickable",
            "activity-check-button--medium-type-correct-hover",
            "activity-check-button--medium-type-incorrect-hover",
            "activity-check-button",
            "activity-continue-button--medium-type-correct-hover",
            "activity-continue-button--medium-type-incorrect-hover"
        );
        check_button_background_rectangle.getStyleClass().removeAll(
            "activity-button__background-rectangle--medium-type-check-answer-unclickable",
            "activity-check-button__background-rectangle--medium-type-correct-hover",
            "activity-check-button__background-rectangle--medium-type-incorrect-hover",
            "activity-check-button__background-rectangle",
            "activity-continue-button__background-rectangle--medium-type-correct-hover",
            "activity-continue-button__background-rectangle--medium-type-incorrect-hover"
        );
        check_button_stackpane.getStyleClass().removeAll(
            "activity-button-stackpane-type-check-answer-unclickable",
            "activity-check-button-stackpane-type-correct-hover",
            "activity-check-button-stackpane-type-incorrect-hover",
            "activity-check-button-stackpane",
            "activity-continue-button-stackpane-type-correct-hover",
            "activity-continue-button-stackpane-type-incorrect-hover"
        );
    }

    private void resetCheckButton() {
        System.out.println("Resetting CHECK button to default state.");
        check_button_label.setText("CHECK");
    
        removeContinueButtonStyles();
    
        check_button_label.getStyleClass().add("activity-button__label--medium-type-check-answer-unclickable");
        check_button.getStyleClass().add("activity-button--medium-type-check-answer-unclickable");
        check_button_background_rectangle.getStyleClass().add("activity-button__background-rectangle--medium-type-check-answer-unclickable");
        check_button_stackpane.getStyleClass().add("activity-button-stackpane-type-check-answer-unclickable");
    
        check_button.setOnAction(event -> handleCheckButton());
    
        check_button.setDisable(true);
        check_button_label.setDisable(true);
    }
    
    private void removeContinueButtonStyles() {
        check_button_label.getStyleClass().removeAll(
            "activity-continue-button__label--medium-type-incorrect-hover",
            "activity-continue-button__label--medium-type-hover",
            "activity-continue-button__label--medium-type-correct-hover"
        );
        check_button.getStyleClass().removeAll(
            "activity-continue-button--medium-type-incorrect-hover",
            "activity-continue-button--medium-type-hover",
            "activity-continue-button--medium-type-correct-hover"
        );
        check_button_background_rectangle.getStyleClass().removeAll(
            "activity-continue-button__background-rectangle--medium-type-incorrect-hover",
            "activity-continue-button__background-rectangle--medium-type-hover",
            "activity-continue-button__background-rectangle--medium-type-correct-hover"
        );
        check_button_stackpane.getStyleClass().removeAll(
            "activity-continue-button-stackpane-type-incorrect-hover",
            "activity-continue-button-stackpane-type-hover",
            "activity-continue-button-stackpane-type-correct-hover"
        );
    }

    private CharacterPracticeActivityThreeOptionController getActivityController() {
        if (!activity_content_area.getChildren().isEmpty()) {
            Node node = activity_content_area.getChildren().get(0);
            CharacterPracticeActivityThreeOptionController controller = (CharacterPracticeActivityThreeOptionController) node.getUserData();
            System.out.println("Retrieved activity controller: " + controller);
            return controller;
        }
        System.err.println("No children in activity_content_area.");
        return null;
    }

    private void handleContinueButton() {
        System.out.println("Handling CONTINUE action.");
        check_button.setDisable(true);
    
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), activity_content_area);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            System.out.println("Fade out completed.");
            if (lastAnswerCorrect) {
                completionCount++;
                System.out.println("Completion count: " + completionCount);
    
                double newProgress = (double) completionCount / TOTAL_COMPLETIONS;
                animateProgressBar(newProgress, 500);
            }
    
            if (completionCount < TOTAL_COMPLETIONS) {
                if (preloadedActivity != null) {
                    Platform.runLater(() -> {
                        System.out.println("Loading preloaded activity.");
                        activity_content_area.getChildren().clear();
                        activity_content_area.getChildren().add(preloadedActivity);
    
                        resetBottomSectionStyle();
                        resetSkipControlContainer();
                        resetContinueButtonStyles();
                        isSkipped = false;
    
                        resetCheckButton();
    
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), activity_content_area);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                        System.out.println("Fade in preloaded activity.");
    
                        preloadNextActivity();
                    });
                } else {
                    try {
                        System.out.println("Preloaded activity is null. Loading new activity.");
                        loadCharacterRecognitionActivity();
    
                        resetBottomSectionStyle();
                        resetSkipControlContainer();
                        resetContinueButtonStyles();
                        isSkipped = false;
    
                        resetCheckButton();
    
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), activity_content_area);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                        System.out.println("Fade in new activity.");
    
                        preloadNextActivity();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // End activity when required number of correct answers is reached
                try {
                    System.out.println("Required number of correct answers reached. Switching to home.");
                    PrimaryController.getInstance().switchToHome();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    
        fadeOut.play();
    }
    

    private void animateProgressBar(double targetProgress, double durationMillis) {
        System.out.println("Animating progress bar to: " + targetProgress);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(progress_bar.progressProperty(), progress_bar.getProgress())
            ),
            new KeyFrame(Duration.millis(durationMillis),
                new KeyValue(progress_bar.progressProperty(), targetProgress)
            )
        );
        timeline.play();
    }

    private void resetBottomSectionStyle() {
        System.out.println("Resetting bottom section style to neutral.");
        Color neutralColor = Color.rgb(255, 255, 255);
        animateBottomSectionBackground(neutralColor, 500);

        bottom_section.getStyleClass().removeAll("bottom-section-correct", "bottom-section-incorrect");
        if (!bottom_section.getStyleClass().contains("bottom-section-neutral")) {
            bottom_section.getStyleClass().add("bottom-section-neutral");
        }
    }

    private void resetSkipControlContainer() {
        System.out.println("Resetting skip control container.");
        skip_control_container.getChildren().clear();

        if (originalSkipControlContent != null) {
            skip_control_container.getChildren().add(originalSkipControlContent);
        }
    }

    private void resetContinueButtonStyles() {
        System.out.println("Resetting continue button styles.");
        check_button_label.getStyleClass().removeAll(
            "activity-continue-button__label--medium-type-incorrect-hover",
            "activity-continue-button__label--medium-type-hover",
            "activity-continue-button__label--medium-type-correct-hover"
        );
        
        check_button.getStyleClass().removeAll(
            "activity-continue-button--medium-type-incorrect-hover",
            "activity-continue-button--medium-type-hover",
            "activity-continue-button--medium-type-correct-hover"
        );
        
        check_button_background_rectangle.getStyleClass().removeAll(
            "activity-continue-button__background-rectangle--medium-type-incorrect-hover",
            "activity-continue-button__background-rectangle--medium-type-hover",
            "activity-continue-button__background-rectangle--medium-type-correct-hover"
        );
        
        check_button_stackpane.getStyleClass().removeAll(
            "activity-continue-button-stackpane-type-incorrect-hover",
            "activity-continue-button-stackpane-type-hover",
            "activity-continue-button-stackpane-type-correct-hover"
        );

        check_button_label.getStyleClass().add("activity-button__label--medium-type-check-answer-unclickable");
        check_button.getStyleClass().add("activity-button--medium-type-check-answer-unclickable");
        check_button_background_rectangle.getStyleClass().add("activity-button__background-rectangle--medium-type-check-answer-unclickable");
        check_button_stackpane.getStyleClass().add("activity-button-stackpane-type-check-answer-unclickable");

        check_button.setDisable(true);
        check_button_label.setDisable(true);
    }

    // **Added:** Method to decrease heart count
    private void decreaseHeart() {
        if (hearts > 0) {
            hearts--;
            progress_label.setText(String.valueOf(hearts));
            System.out.println("Heart decreased. Remaining hearts: " + hearts);
            if (hearts == 0) {
                endActivity();
            }
        }
    }

    
    // **Added:** Method to handle end of activity
    private void endActivity() {
        // Display a message or perform any cleanup if necessary
        System.out.println("Activity over. Redirecting to Character Practice Home.");

        // Switch to Character Practice Home instead of Main Home
        try {
            PrimaryController.getInstance().switchContent("/com/socslingo/views/character_practice_home.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCharacterRecognitionActivity() throws IOException {
        System.out.println("Loading Character Recognition Activity.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/character_practice_activity_three_option.fxml"));
        
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found at /com/socslingo/views/character_practice_activity_three_option.fxml");
        }
    
        Node characterRecognitionActivity = loader.load();
    
        CharacterPracticeActivityThreeOptionController crcController = loader.getController();
    
        crcController.setMainController(this);
    
        SelectedCategory.Category category = SelectedCategory.getSelectedCategory();
        crcController.setCategory(category);
    
        crcController.loadQuestionAndOptions();
    
        characterRecognitionActivity.setUserData(crcController);
        System.out.println("Character Recognition Activity loaded and controller set.");
    
        activity_content_area.getChildren().add(characterRecognitionActivity);
    }

    private void preloadNextActivity() {
        if (isPreloading) {
            System.out.println("Already preloading next activity.");
            return;
        }
        System.out.println("Preloading next activity.");
        isPreloading = true;
    
        Task<Node> preloadTask = new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/character_practice_activity_three_option.fxml"));
                Node activity = loader.load();
    
                CharacterPracticeActivityThreeOptionController crcController = loader.getController();
                crcController.setMainController(CharacterPracticeActivityMainController.this);
    
                SelectedCategory.Category category = SelectedCategory.getSelectedCategory();
                crcController.setCategory(category);
    
                crcController.loadQuestionAndOptions();
    
                activity.setUserData(crcController);
    
                return activity;
            }
        };
    
        preloadTask.setOnSucceeded(event -> {
            preloadedActivity = preloadTask.getValue();
            isPreloading = false;
            System.out.println("Preloaded activity successfully.");
        });
    
        preloadTask.setOnFailed(event -> {
            preloadTask.getException().printStackTrace();
            isPreloading = false;
            System.err.println("Failed to preload activity.");
        });
    
        Thread preloadThread = new Thread(preloadTask);
        preloadThread.setDaemon(true);
        preloadThread.start();
    }
    
    private void handleSkipButton() {
        System.out.println("Handling SKIP action.");
        isSkipped = true;

        Color incorrectColor = Color.rgb(242, 224, 224);
        animateBottomSectionBackground(incorrectColor, 500);

        setContinueButtonToIncorrectHover();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socslingo/views/incorrect_replacement.fxml"));
            Node replacement = loader.load();
            skip_control_container.getChildren().setAll(replacement);
            System.out.println("Loaded incorrect_replacement.fxml via skip.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContinueButtonToIncorrectHover() {
        System.out.println("Setting CONTINUE button to incorrect hover style.");
    
        check_button_label.setText("CONTINUE");
    
        removeCheckButtonStyles();
        removeContinueButtonStyles();
    
        check_button_label.getStyleClass().add("activity-continue-button__label--medium-type-incorrect-hover");
        check_button.getStyleClass().add("activity-continue-button--medium-type-incorrect-hover");
        check_button_background_rectangle.getStyleClass().add("activity-continue-button__background-rectangle--medium-type-incorrect-hover");
        check_button_stackpane.getStyleClass().add("activity-continue-button-stackpane-type-incorrect-hover");
    
        check_button.setOnAction(event -> {
            System.out.println("CONTINUE button clicked.");
            handleContinueButton();
        });
    
        check_button.setDisable(false); 
        check_button_label.setDisable(false);
    }
    
}