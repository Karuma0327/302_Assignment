package com.socslingo.controllers;

import com.socslingo.data.SelectedCategory;
import com.socslingo.dataAccess.CharacterRecognitionStatisticsDAO;
import com.socslingo.managers.SessionManager;
import com.socslingo.models.CharacterModel;
import com.socslingo.models.CharacterRecognitionStatistics;
import com.socslingo.services.CharacterService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterPracticeActivityThreeOptionController {

    private CharacterPracticeActivityMainController mainController;

    public void setMainController(CharacterPracticeActivityMainController mainController) {
        this.mainController = mainController;
        System.out.println("MainController set in ThreeOptionController.");
    }

    private SelectedCategory.Category category;

    public void setCategory(SelectedCategory.Category category) {
        this.category = category;
        System.out.println("Category set to: " + category);
    }

    @FXML
    private Label titleLabel;

    @FXML
    private Label questionLabel;

    @FXML
    private ToggleButton toggleButton1;

    @FXML
    private ToggleButton toggleButton2;

    @FXML
    private ToggleButton toggleButton3;

    @FXML
    private Label backgroundLabel1;

    @FXML
    private Label backgroundLabel2;

    @FXML
    private Label backgroundLabel3;

    private List<ToggleButton> toggleButtons = new ArrayList<>();

    private String correctRomaji;
    private String selectedRomaji;

    private int userId; 
    @FXML
    public void initialize() {
        System.out.println("CharacterPracticeActivityThreeOptionController initialized.");
        toggleButtons.add(toggleButton1);
        toggleButtons.add(toggleButton2);
        toggleButtons.add(toggleButton3);
        
        ToggleGroup toggleGroup = new ToggleGroup();
        for (ToggleButton button : toggleButtons) {
            button.setToggleGroup(toggleGroup);
        }

        for (ToggleButton button : toggleButtons) {
            button.setOnAction(event -> {
                if (button.isSelected()) {
                    selectedRomaji = button.getText();
                    System.out.println("Toggle selected: " + selectedRomaji);
                    mainController.enableCheckButton();
                }
            });
        }

        
        // Fetch current user ID from SessionManager
        userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            System.err.println("No user is currently logged in.");
            // Handle the case where no user is logged in
            // For example, disable the activity or prompt login
        } else {
            // Ensure statistics exist for the user
            initializeUserStatistics();
        }
    }

        private void initializeUserStatistics() {
        CharacterRecognitionStatisticsDAO statsDAO = CharacterRecognitionStatisticsDAO.getInstance();
        CharacterRecognitionStatistics stats = statsDAO.getStatisticsByUserId(userId);
        if (stats == null) {
            boolean created = statsDAO.createStatistics(userId);
            if (created) {
                System.out.println("Statistics initialized for user ID " + userId);
            } else {
                System.err.println("Failed to initialize statistics for user ID " + userId);
                // Handle the error appropriately
            }
        } else {
            System.out.println("Statistics already exist for user ID " + userId);
        }
    }

    public void disableToggleButtons() {
        System.out.println("Disabling all toggle buttons.");
        toggleButton1.setDisable(true);
        toggleButton2.setDisable(true);
        toggleButton3.setDisable(true);
    }

    public void loadQuestionAndOptions() {
        System.out.println("Loading question and options.");
        CharacterModel questionCharacter = CharacterService.getRandomCharacterByCategory(category);
        if (questionCharacter != null) { 
            questionLabel.setText(questionCharacter.getCharacter());
            System.out.println("Question character set to: " + questionCharacter.getCharacter());
        } else {
            System.err.println("No character retrieved from database for category: " + category);
            return;
        }

        List<String> options = generateOptions();

        if (options.size() < toggleButtons.size()) {
            System.err.println("Not enough options generated.");
            return;
        }

        for (int i = 0; i < toggleButtons.size(); i++) {
            ToggleButton toggleButton = toggleButtons.get(i);
            toggleButton.setText(options.get(i));
            toggleButton.getStyleClass().add("practice-three-option-toggle-button");
            System.out.println("Setting ToggleButton" + (i+1) + " text to: " + options.get(i));

            addEventHandlers(toggleButton);
        }
    }

    private List<String> generateOptions() {
        String questionCharacter = questionLabel.getText();
        correctRomaji = CharacterService.getRomajiForCharacter(questionCharacter, category);
        System.out.println("Correct Romaji for question: " + correctRomaji);

        if (correctRomaji == null) {
            System.err.println("No romaji found for character: " + questionCharacter);
            return Collections.emptyList();
        }

        List<String> options = new ArrayList<>();
        options.add(correctRomaji);

        List<String> randomOptions = CharacterService.getRandomRomaji(2, correctRomaji, category);
        System.out.println("Random options retrieved: " + randomOptions);

        if (randomOptions.size() < 2) {
            System.err.println("Not enough random romaji options available.");
        }

        options.addAll(randomOptions);

        Collections.shuffle(options);
        System.out.println("Shuffled options: " + options);
        return options;
    }


    private void addEventHandlers(ToggleButton button) {
        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                button.getStyleClass().add("practice-three-option-toggle-button-selected");
                
                button.getStyleClass().remove("practice-three-option-toggle-button-default");

                updateBackgroundLabelStyle(button, true);

                selectedRomaji = button.getText();
                System.out.println("Selected Romaji updated to: " + selectedRomaji);

                mainController.enableCheckButton();
            } else {
                button.getStyleClass().remove("practice-three-option-toggle-button-selected");
                button.getStyleClass().remove("practice-three-option-toggle-button-hover");
                button.getStyleClass().add("practice-three-option-toggle-button-default");
                
                updateBackgroundLabelStyle(button, false);
                System.out.println("ToggleButton deselected: " + button.getText());
            }
        });

        button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!button.isSelected()) {
                button.getStyleClass().add("practice-three-option-toggle-button-hover");
                
            }
        });

        button.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            button.getStyleClass().remove("practice-three-option-toggle-button-hover");
            
        });

    }

    private void updateBackgroundLabelStyle(ToggleButton button, boolean isSelected) {
        Label backgroundLabel = null;
        if(button == toggleButton1){
            backgroundLabel = backgroundLabel1;
        }
        else if(button == toggleButton2){
            backgroundLabel = backgroundLabel2;
        }
        else if(button == toggleButton3){
            backgroundLabel = backgroundLabel3;
        }

        if (backgroundLabel != null) {
            if (isSelected) {
                backgroundLabel.getStyleClass().add("practice-three-option-toggle-button-background-label-selected");
                backgroundLabel.getStyleClass().remove("practice-three-option-toggle-button-background-label-default");
            } else {
                backgroundLabel.getStyleClass().remove("practice-three-option-toggle-button-background-label-selected");
                backgroundLabel.getStyleClass().add("practice-three-option-toggle-button-background-label-default");
            }
        }
    }


    private void handleToggleSelection(ToggleButton selectedButton) {
        String selectedRomaji = selectedButton.getText();
        String questionCharacter = questionLabel.getText();
        String correctRomaji = CharacterService.getRomajiForCharacter(questionCharacter, category);

        System.out.println("Handling toggle selection. Selected: " + selectedRomaji + ", Correct: " + correctRomaji);

        if (correctRomaji == null) {
            System.err.println("No romaji found for character: " + questionCharacter);
            return;
        }

        if (selectedRomaji.equals(correctRomaji)) {
            System.out.println("Correct!");
            loadNextQuestion();
            mainController.enableCheckButton();
        } else {
            System.out.println("Incorrect! The correct answer was: " + correctRomaji);
            loadNextQuestion();
        }
    }

    public void loadNextQuestion() {
        System.out.println("Loading next question.");
        selectedRomaji = null;

        CharacterModel newQuestion = CharacterService.getRandomCharacterByCategory(category);
        if (newQuestion != null) {
            questionLabel.setText(newQuestion.getCharacter());
            System.out.println("Next question character set to: " + newQuestion.getCharacter());
        } else {
            System.err.println("No character retrieved from database for category: " + category);
            return;
        }

        List<String> newOptions = generateOptions();

        for (int i = 0; i < toggleButtons.size(); i++) {
            ToggleButton toggleButton = toggleButtons.get(i);
            toggleButton.setText(newOptions.get(i));
            System.out.println("Setting ToggleButton" + (i+1) + " text to: " + newOptions.get(i));

            toggleButton.setSelected(false);
            toggleButton.getStyleClass().removeAll("practice-three-option-toggle-button-selected", "practice-three-option-toggle-button-hover", "practice-three-option-toggle-button-default");
            toggleButton.getStyleClass().add("practice-three-option-toggle-button-default");

            resetBackgroundLabelStyle(toggleButton);
        }
    }

    private void resetBackgroundLabelStyle(ToggleButton button) {
        Label backgroundLabel = null;
        if(button == toggleButton1){
            backgroundLabel = backgroundLabel1;
        }
        else if(button == toggleButton2){
            backgroundLabel = backgroundLabel2;
        }
        else if(button == toggleButton3){
            backgroundLabel = backgroundLabel3;
        }

        if (backgroundLabel != null) {
            backgroundLabel.getStyleClass().remove("practice-three-option-toggle-button-background-label-selected");
            backgroundLabel.getStyleClass().add("practice-three-option-toggle-button-background-label-default");
        }
    }

    public boolean checkAnswer() {
        System.out.println("Checking answer.");
        if (selectedRomaji == null) {
            System.err.println("No option selected.");
            return false;
        }

        if (selectedRomaji.equals(correctRomaji)) {
            System.out.println("Answer is correct.");

            // Update statistics for correct answer
            boolean updated = CharacterRecognitionStatisticsDAO.getInstance().incrementCorrect(userId);
            if (updated) {
                System.out.println("Correct answer count incremented.");
            } else {
                System.err.println("Failed to increment correct answer count.");
            }

            return true;
        } else {
            System.out.println("Answer is incorrect. Correct answer: " + correctRomaji);

            // Update statistics for incorrect answer
            boolean updated = CharacterRecognitionStatisticsDAO.getInstance().incrementIncorrect(userId);
            if (updated) {
                System.out.println("Incorrect answer count incremented.");
            } else {
                System.err.println("Failed to increment incorrect answer count.");
            }

            return false;
        }
    }


}