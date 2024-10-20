package com.socslingo.controllers;

import com.socslingo.data.SelectedCategory;
import com.socslingo.models.CharacterModel;
import com.socslingo.services.CharacterService;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;

public class CharacterPracticeHomeController {

    @FXML
    private Label practiceMainText;

    @FXML
    private Label practiceSubText;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private VBox characterButtonsContainer;

    @FXML
    private VBox dakuonButtonsContainer;

    @FXML
    private VBox comboButtonsContainer;

    @FXML
    private VBox smallTsuButtonsContainer;

    @FXML
    private VBox longVowelsButtonsContainer;

    @FXML
    private VBox dakuonDescriptionContainer;

    @FXML
    private VBox comboDescriptionContainer;

    @FXML
    private VBox smallTsuDescriptionContainer;

    @FXML
    private VBox longVowelsDescriptionContainer;

    @FXML
    private HBox mainDivider;

    @FXML
    private HBox dakuonDivider;

    @FXML
    private HBox comboDivider;

    @FXML
    private HBox smallTsuDivider;

    @FXML
    private HBox longVowelsDivider;

    @FXML
    private StackPane learnCharactersContainer; // Added: StackPane for Learn Characters button

    @FXML
    private void initialize() {
        if (!toggleGroup.getToggles().isEmpty()) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
            handleToggle();
        }
    }

    @FXML
    private void handleToggle() {
        Toggle selectedToggle = toggleGroup.getSelectedToggle();
        if (selectedToggle != null) {
            String selectedText = ((ToggleButton) selectedToggle).getText();
            switch (selectedText) {
                case "HIRAGANA":
                    practiceMainText.setText("Let's learn Hiragana!");
                    practiceSubText.setText("Get to know the main writing system in Japanese");
                    loadHiraganaCharacters();
                    showOptionalSections(true);
                    learnCharactersContainer.setVisible(true);
                    break;
                case "KATAKANA":
                    practiceMainText.setText("Let's learn Katakana!");
                    practiceSubText.setText("Practice characters used for loanwords");
                    loadKatakanaCharacters();
                        showOptionalSections(true);
                    learnCharactersContainer.setVisible(true);
                    break;
                case "KANJI":
                    practiceMainText.setText("Kanji!");
                    practiceSubText.setText("Practice Hiragana and Katakana, then practice Kanji!");
                    loadKanjiCharacters();
                    learnCharactersContainer.setVisible(false);
                    showOptionalSections(false);
                    break;
                case "USER":
                    practiceMainText.setText("Practice what you want!");
                    practiceSubText.setText("Create and practice your own sets");
                    clearAllContainers();
                    learnCharactersContainer.setVisible(false);
                    showOptionalSections(false);
                    break;
                default:
                    clearAllContainers();
                    showOptionalSections(false);
                    learnCharactersContainer.setVisible(false);
                    break;
            }
        }
    }

    private void showOptionalSections(boolean visible) {
        dakuonButtonsContainer.setVisible(visible);
        comboButtonsContainer.setVisible(visible);
        smallTsuButtonsContainer.setVisible(visible);
        longVowelsButtonsContainer.setVisible(visible);

        dakuonDescriptionContainer.setVisible(visible);
        comboDescriptionContainer.setVisible(visible);
        smallTsuDescriptionContainer.setVisible(visible);
        longVowelsDescriptionContainer.setVisible(visible);

        mainDivider.setVisible(visible);
        dakuonDivider.setVisible(visible);
        comboDivider.setVisible(visible);
        smallTsuDivider.setVisible(visible);
        longVowelsDivider.setVisible(visible);
    }

    private void clearAllContainers() {
        characterButtonsContainer.getChildren().clear();
        dakuonButtonsContainer.getChildren().clear();
        comboButtonsContainer.getChildren().clear();
        smallTsuButtonsContainer.getChildren().clear();
        longVowelsButtonsContainer.getChildren().clear();

        dakuonDescriptionContainer.setVisible(false);
        comboDescriptionContainer.setVisible(false);
        smallTsuDescriptionContainer.setVisible(false);
        longVowelsDescriptionContainer.setVisible(false);

        mainDivider.setVisible(false);
        dakuonDivider.setVisible(false);
        comboDivider.setVisible(false);
        smallTsuDivider.setVisible(false);
        longVowelsDivider.setVisible(false);
    }

    private void loadHiraganaCharacters() {
        clearAllContainers();

        List<CharacterModel> hiragana = CharacterService.getCharactersByType("Hiragana_Main");
        if (!hiragana.isEmpty()) {
            characterButtonsContainer.getChildren().addAll(createCharacterRows(hiragana));
        }

        List<CharacterModel> hiraganaDakuon = CharacterService.getCharactersByType("Hiragana_Dakuon");
        if (!hiraganaDakuon.isEmpty()) {
            dakuonButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaDakuon));
            dakuonDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> hiraganaCombo = CharacterService.getCharactersByType("Hiragana_Combo");
        if (!hiraganaCombo.isEmpty()) {
            comboButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaCombo));
            comboDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> hiraganaLongVowels = CharacterService.getCharactersByType("Hiragana_Long Vowels");
        if (!hiraganaLongVowels.isEmpty()) {
            longVowelsButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaLongVowels));
            longVowelsDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> hiraganaSmallTsu = CharacterService.getCharactersByType("Hiragana_Small_tsu");
        if (!hiraganaSmallTsu.isEmpty()) {
            smallTsuButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaSmallTsu));
            smallTsuDescriptionContainer.setVisible(true);
        }
    }

    private void loadKatakanaCharacters() {
        clearAllContainers();

        List<CharacterModel> katakana = CharacterService.getCharactersByType("Katakana_Main");
        if (!katakana.isEmpty()) {
            characterButtonsContainer.getChildren().addAll(createCharacterRows(katakana));
        }

        List<CharacterModel> katakanaDakuon = CharacterService.getCharactersByType("Katakana_Dakuon");
        if (!katakanaDakuon.isEmpty()) {
            dakuonButtonsContainer.getChildren().addAll(createCharacterRows(katakanaDakuon));
            dakuonDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> katakanaCombo = CharacterService.getCharactersByType("Katakana_Combo");
        if (!katakanaCombo.isEmpty()) {
            comboButtonsContainer.getChildren().addAll(createCharacterRows(katakanaCombo));
            comboDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> katakanaLongVowels = CharacterService.getCharactersByType("Katakana_Long Vowels");
        if (!katakanaLongVowels.isEmpty()) {
            longVowelsButtonsContainer.getChildren().addAll(createCharacterRows(katakanaLongVowels));
            longVowelsDescriptionContainer.setVisible(true);
        }

        List<CharacterModel> katakanaSmallTsu = CharacterService.getCharactersByType("Katakana_Small_tsu");
        if (!katakanaSmallTsu.isEmpty()) {
            smallTsuButtonsContainer.getChildren().addAll(createCharacterRows(katakanaSmallTsu));
            smallTsuDescriptionContainer.setVisible(true);
        }
    }

    private void loadKanjiCharacters() {
        clearAllContainers();

        List<CharacterModel> kanji = CharacterService.getCharactersByType("Kanji_Main");
        if (!kanji.isEmpty()) {
            characterButtonsContainer.getChildren().addAll(createCharacterRows(kanji));
        }

    }

    private List<HBox> createCharacterRows(List<CharacterModel> characters) {
        List<HBox> rows = new ArrayList<>();
        HBox row = null;
        int count = 0;
        for (CharacterModel character : characters) {
            if (count % 5 == 0) {
                row = new HBox(15);
                row.setPadding(new Insets(10, 0, 0, 0));
                row.setAlignment(javafx.geometry.Pos.CENTER);
                rows.add(row);
            }

            StackPane stackPane = createCharacterButton(character);

            row.getChildren().add(stackPane);
            count++;
        }
        return rows;
    }

    private StackPane createCharacterButton(CharacterModel character) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(87, 68);
        stackPane.getStyleClass().add("practice-character-button-stack-pane");

        Label backgroundLabel = new Label("Label");
        backgroundLabel.setPrefSize(87, 68);
        backgroundLabel.getStyleClass().add("practice-character-button-background-label");

        Label characterLabel = new Label(character.getCharacter());
        characterLabel.setPrefSize(87, 68);
        characterLabel.getStyleClass().add("practice-character-text-layer-japanese-bottom-layer");
        characterLabel.setAlignment(javafx.geometry.Pos.CENTER);

        if (character.getCharacter().length() > 1) {
            characterLabel.setStyle("-fx-letter-spacing: 2px;");
        }

        Button romajiButton = new Button(character.getRomaji());
        romajiButton.setPrefSize(87, 68);
        romajiButton.getStyleClass().add("practice-character-button-top-layer");
        romajiButton.setMnemonicParsing(false);
        romajiButton.setOnAction(e -> {
            System.out.println("Clicked on: " + character.getCharacter() + " (" + character.getRomaji() + ")");
        });

        stackPane.getChildren().addAll(backgroundLabel, characterLabel, romajiButton);

        return stackPane;
    }

    @FXML
    private void handleLearnCharacters() {
        try {
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            if (selectedToggle == null) {
                System.err.println("No category selected.");
                return;
            }

            String selectedText = ((ToggleButton) selectedToggle).getText();

            SelectedCategory.Category category;
            switch (selectedText) {
                case "HIRAGANA":
                    category = SelectedCategory.Category.HIRAGANA;
                    break;
                case "KATAKANA":
                    category = SelectedCategory.Category.KATAKANA;
                    break;
                case "KANJI":
                    category = SelectedCategory.Category.KANJI;
                    break;
                case "USER":
                    category = SelectedCategory.Category.USER;
                    break;
                default:
                    System.err.println("Unknown category selected: " + selectedText);
                    return;
            }

            SelectedCategory.setSelectedCategory(category);

            PrimaryController.getInstance().switchContent("/com/socslingo/views/character_practice_activity_main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}