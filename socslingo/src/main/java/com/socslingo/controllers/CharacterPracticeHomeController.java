package com.socslingo.controllers;

import com.socslingo.data.SelectedCategory;
import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.DatabaseManager;
import com.socslingo.managers.SessionManager;
import com.socslingo.models.CharacterModel;
import com.socslingo.models.Deck;
import com.socslingo.models.Flashcard;
import com.socslingo.services.CharacterService;
import com.socslingo.services.DeckService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterPracticeHomeController {

    private static final Logger logger = LoggerFactory.getLogger(CharacterPracticeHomeController.class);

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
    private StackPane learnCharactersContainer; 

    @FXML
    private VBox userSection; // Updated to hold dynamic deck sections

    private DeckService deckService;
    private ObservableList<Deck> userDecksObservableList;
    private ObservableList<Flashcard> flashcardsObservableList;

    @FXML
    private void initialize() {
        if (!toggleGroup.getToggles().isEmpty()) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
            handleToggle();
        }
    
        try {
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            DeckDataAccess deckDataAccess = new DeckDataAccess(databaseManager);
            deckService = new DeckService(deckDataAccess);
            logger.info("DeckService initialized successfully in CharacterPracticeHomeController");
        } catch (Exception e) {
            logger.error("Failed to initialize DeckService in CharacterPracticeHomeController", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize deck services.");
        }
    
        userDecksObservableList = FXCollections.observableArrayList();
        flashcardsObservableList = FXCollections.observableArrayList();

        // Verify that all essential UI components are injected
        if (learnCharactersContainer == null) {
            logger.warn("learnCharactersContainer is not injected. Check FXML.");
        }
        if (characterButtonsContainer == null) {
            logger.warn("characterButtonsContainer is not injected. Check FXML.");
        }
        // Add similar checks for other essential UI components
    }

    /**
     * Utility method to set both visibility and managed properties.
     *
     * @param node    The UI node to update.
     * @param visible The visibility state.
     */
    private void setVisibleAndManaged(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
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
                    setVisibleAndManaged(learnCharactersContainer, true);
                    break;
                case "KATAKANA":
                    practiceMainText.setText("Let's learn Katakana!");
                    practiceSubText.setText("Practice characters used for loanwords");
                    loadKatakanaCharacters();
                    showOptionalSections(true);
                    setVisibleAndManaged(learnCharactersContainer, true);
                    break;
                case "KANJI":
                    practiceMainText.setText("Kanji!");
                    practiceSubText.setText("Practice Hiragana and Katakana, then practice Kanji!");
                    loadKanjiCharacters();
                    setVisibleAndManaged(learnCharactersContainer, false);
                    showOptionalSections(false);
                    break;
                case "USER":
                    practiceMainText.setText("Your Character Sets");
                    practiceSubText.setText("Practice your custom character sets");
                    clearAllContainers();
                    loadUserDecksSection();
                    setVisibleAndManaged(learnCharactersContainer, false);
                    showOptionalSections(false);
                    break;
                default:
                    clearAllContainers();
                    showOptionalSections(false);
                    setVisibleAndManaged(learnCharactersContainer, false);
                    break;
            }
        }
    }

    /**
     * Loads the User Decks Section by dynamically creating deck UI elements.
     */
    private void loadUserDecksSection() {
        userSection.getChildren().clear(); // Clear any existing content

        // Make the userSection visible and managed
        setVisibleAndManaged(userSection, true);

        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Deck> userDecks = deckService.getUserDecks(userId);
            logger.info("Loaded {} decks for userId: {}", userDecks.size(), userId);

            for (Deck deck : userDecks) {
                VBox deckSection = createDeckSection(deck);
                userSection.getChildren().add(deckSection);
            }


        } catch (Exception e) {
            logger.error("Failed to load user decks for userId: {}", userId, e);
            showAlert(Alert.AlertType.ERROR, "Failed to load your decks.");
        }
    }

    /**
     * Creates a VBox representing a single deck section, utilizing flashcard rows.
     * Modified to match deck name styling as per FXML.
     *
     * @param deck The Deck object containing deck information.
     * @return A VBox node representing the deck.
     */
    private VBox createDeckSection(Deck deck) {
        VBox deckVBox = new VBox();
        deckVBox.setPrefWidth(500.0);
        deckVBox.setSpacing(10.0);
        deckVBox.setPadding(new Insets(10.0));
    
        // Deck Title with styling similar to FXML
        HBox deckTitleHBox = new HBox();
        deckTitleHBox.setAlignment(Pos.CENTER);
    
        VBox deckTitleVBox = new VBox();
        deckTitleVBox.setPrefWidth(475.0);
        deckTitleVBox.setVisible(true); // Ensuring visibility
        Label deckTitle = new Label(deck.getDeckName());
        deckTitle.getStyleClass().add("practice-text-label-small-subheading");
        VBox.setMargin(deckTitle, new Insets(0, 0, 10, 0)); // Bottom margin
    
        deckTitleVBox.getChildren().add(deckTitle);
        deckTitleHBox.getChildren().add(deckTitleVBox);
    
        // Flashcards Container
        VBox flashcardsContainer = new VBox();
        List<Flashcard> flashcards = deckService.getFlashcardsInDeck(deck.getDeckId());
        List<HBox> flashcardRows = createFlashcardRows(flashcards);
        flashcardsContainer.getChildren().addAll(flashcardRows);
    
        // **Customized manageDeckButton with styling**
        // Create Label for background
        Label backgroundLabel = new Label("Label");
        backgroundLabel.getStyleClass().add("learn-the-characters-button-background-label");
    
        // Create Button and apply style
        Button manageDeckButton = new Button("PRACTICE");
        manageDeckButton.getStyleClass().add("learn-the-characters-button");
        manageDeckButton.setMnemonicParsing(false);
        manageDeckButton.setOnAction(e -> handlePracticeDeck());
    
        // Create StackPane and add Label and Button
        StackPane manageDeckStackPane = new StackPane();
        manageDeckStackPane.getChildren().addAll(backgroundLabel, manageDeckButton);
    
        // Wrap StackPane in HBox for alignment and sizing
        HBox manageDeckButtonHBox = new HBox(manageDeckStackPane);
        manageDeckButtonHBox.setAlignment(Pos.CENTER);
        manageDeckButtonHBox.setPrefHeight(100.0);
        manageDeckButtonHBox.setPrefWidth(350.0);
    
        // Add all components to deckVBox, including the styled button
        deckVBox.getChildren().addAll(deckTitleHBox, flashcardsContainer, manageDeckButtonHBox);
    
        return deckVBox;
    }

    private void handlePracticeDeck() {
        try {
            // Set the selected category to Hiragana
            SelectedCategory.setSelectedCategory(SelectedCategory.Category.HIRAGANA);
            logger.info("Selected category set to HIRAGANA.");

            // Switch to the Character Practice Activity view
            PrimaryController.getInstance().switchContent("/com/socslingo/views/character_practice_activity_main.fxml");
            logger.info("Switched to Character Practice Activity view.");
        } catch (IOException ex) {
            logger.error("Failed to switch to Character Practice Activity view.", ex);
            showAlert(Alert.AlertType.ERROR, "Failed to start practice session.");
        }
    }
    /**
     * Creates a StackPane representing a single flashcard, similar to createCharacterButton.
     *
     * @param flashcard The Flashcard object containing flashcard information.
     * @return A StackPane node representing the flashcard.
     */
    private StackPane createFlashcardButton(Flashcard flashcard) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(87, 68);
        stackPane.getStyleClass().add("practice-character-button-stack-pane");

        Label backgroundLabel = new Label("Label");
        backgroundLabel.setPrefSize(87, 68);
        backgroundLabel.getStyleClass().add("practice-character-button-background-label");

        Label characterLabel = new Label(flashcard.getFront());
        characterLabel.setPrefSize(87, 68);
        characterLabel.getStyleClass().add("practice-character-text-layer-japanese-bottom-layer");
        characterLabel.setAlignment(Pos.CENTER);

        // Adjust style if the character text is longer than one character
        if (flashcard.getFront().length() > 1) {
            characterLabel.setStyle("-fx-letter-spacing: 2px;");
        }

        Button romajiButton = new Button(flashcard.getBack());
        romajiButton.setPrefSize(87, 68);
        romajiButton.getStyleClass().add("practice-character-button-top-layer");
        romajiButton.setMnemonicParsing(false);
        romajiButton.setOnAction(e -> handleFlashcardAction(flashcard));

        stackPane.getChildren().addAll(backgroundLabel, characterLabel, romajiButton);

        return stackPane;
    }

    /**
     * Creates a list of HBox rows containing flashcard buttons.
     *
     * @param flashcards The list of flashcards to display.
     * @return A list of HBox rows with flashcard buttons.
     */
    private List<HBox> createFlashcardRows(List<Flashcard> flashcards) {
        List<HBox> rows = new ArrayList<>();
        HBox row = null;
        int count = 0;
        for (Flashcard flashcard : flashcards) {
            if (count % 5 == 0) {
                row = new HBox(15);
                row.setPadding(new Insets(10, 0, 0, 0));
                row.setAlignment(Pos.CENTER);
                rows.add(row);
            }

            StackPane stackPane = createFlashcardButton(flashcard);

            row.getChildren().add(stackPane);
            count++;
        }
        return rows;
    }

    /**
     * Creates a StackPane representing a single character button.
     *
     * @param character The CharacterModel object containing character information.
     * @return A StackPane node representing the character button.
     */
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
        characterLabel.setAlignment(Pos.CENTER);

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

    /**
     * Creates a list of HBox rows containing character buttons.
     *
     * @param characters The list of characters to display.
     * @return A list of HBox rows with character buttons.
     */
    private List<HBox> createCharacterRows(List<CharacterModel> characters) {
        List<HBox> rows = new ArrayList<>();
        HBox row = null;
        int count = 0;
        for (CharacterModel character : characters) {
            if (count % 5 == 0) {
                row = new HBox(15);
                row.setPadding(new Insets(10, 0, 0, 0));
                row.setAlignment(Pos.CENTER);
                rows.add(row);
            }

            StackPane stackPane = createCharacterButton(character);

            row.getChildren().add(stackPane);
            count++;
        }
        return rows;
    }

    /**
     * Handles the action when a flashcard's Romaji button is clicked.
     *
     * @param flashcard The Flashcard that was clicked.
     */
    private void handleFlashcardAction(Flashcard flashcard) {
        // Implement the desired action, e.g., show details or flip the card
        logger.info("Flashcard clicked: Front - {}, Back - {}", flashcard.getFront(), flashcard.getBack());
        showAlert(Alert.AlertType.INFORMATION, "Flashcard: " + flashcard.getFront() + " - " + flashcard.getBack());
    }

    /**
     * Handles the action to manage a specific deck.
     *
     * @param deck The Deck to manage.
     */
    private void handleManageDeck(Deck deck) {
        try {
            // Implement deck management logic, e.g., open a new window or switch view
            // For example:
            // PrimaryController.getInstance().switchContent("/com/socslingo/views/deck_management.fxml");
            logger.info("Managing deck: {}", deck.getDeckName());
            showAlert(Alert.AlertType.INFORMATION, "Managing deck: " + deck.getDeckName());
        } catch (Exception e) {
            logger.error("Failed to manage deck: {}", deck.getDeckName(), e);
            showAlert(Alert.AlertType.ERROR, "Failed to manage the deck.");
        }
    }

    @FXML
    private void handleManageDecks(ActionEvent event) {
        try {
            // Handle global deck management if needed
            // For example, open a dialog to create a new deck
            logger.info("Opening Deck Management view.");
            PrimaryController primaryController = PrimaryController.getInstance();
            primaryController.switchContent("/com/socslingo/views/deck_management.fxml");
        } catch (IOException e) {
            logger.error("Failed to switch to Deck Management view.", e);
            showAlert(Alert.AlertType.ERROR, "Failed to open Deck Management.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }

    @FXML
    private void handleRefreshUserSection(ActionEvent event) {
        loadUserDecksSection();
        logger.info("User section refreshed.");
    }

    private void showOptionalSections(boolean visible) {
        setVisibleAndManaged(dakuonButtonsContainer, visible);
        setVisibleAndManaged(comboButtonsContainer, visible);
        setVisibleAndManaged(smallTsuButtonsContainer, visible);
        setVisibleAndManaged(longVowelsButtonsContainer, visible);

        setVisibleAndManaged(dakuonDescriptionContainer, visible);
        setVisibleAndManaged(comboDescriptionContainer, visible);
        setVisibleAndManaged(smallTsuDescriptionContainer, visible);
        setVisibleAndManaged(longVowelsDescriptionContainer, visible);

        setVisibleAndManaged(mainDivider, visible);
        setVisibleAndManaged(dakuonDivider, visible);
        setVisibleAndManaged(comboDivider, visible);
        setVisibleAndManaged(smallTsuDivider, visible);
        setVisibleAndManaged(longVowelsDivider, visible);
    }

    private void clearAllContainers() {
        characterButtonsContainer.getChildren().clear();
        dakuonButtonsContainer.getChildren().clear();
        comboButtonsContainer.getChildren().clear();
        smallTsuButtonsContainer.getChildren().clear();
        longVowelsButtonsContainer.getChildren().clear();

        setVisibleAndManaged(dakuonDescriptionContainer, false);
        setVisibleAndManaged(comboDescriptionContainer, false);
        setVisibleAndManaged(smallTsuDescriptionContainer, false);
        setVisibleAndManaged(longVowelsDescriptionContainer, false);

        setVisibleAndManaged(mainDivider, false);
        setVisibleAndManaged(dakuonDivider, false);
        setVisibleAndManaged(comboDivider, false);
        setVisibleAndManaged(smallTsuDivider, false);
        setVisibleAndManaged(longVowelsDivider, false);
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
            dakuonDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> hiraganaCombo = CharacterService.getCharactersByType("Hiragana_Combo");
        if (!hiraganaCombo.isEmpty()) {
            comboButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaCombo));
            comboDescriptionContainer.setVisible(true);
            comboDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> hiraganaLongVowels = CharacterService.getCharactersByType("Hiragana_Long Vowels");
        if (!hiraganaLongVowels.isEmpty()) {
            longVowelsButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaLongVowels));
            longVowelsDescriptionContainer.setVisible(true);
            longVowelsDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> hiraganaSmallTsu = CharacterService.getCharactersByType("Hiragana_Small_tsu");
        if (!hiraganaSmallTsu.isEmpty()) {
            smallTsuButtonsContainer.getChildren().addAll(createCharacterRows(hiraganaSmallTsu));
            smallTsuDescriptionContainer.setVisible(true);
            smallTsuDescriptionContainer.setManaged(true);
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
            dakuonDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> katakanaCombo = CharacterService.getCharactersByType("Katakana_Combo");
        if (!katakanaCombo.isEmpty()) {
            comboButtonsContainer.getChildren().addAll(createCharacterRows(katakanaCombo));
            comboDescriptionContainer.setVisible(true);
            comboDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> katakanaLongVowels = CharacterService.getCharactersByType("Katakana_Long Vowels");
        if (!katakanaLongVowels.isEmpty()) {
            longVowelsButtonsContainer.getChildren().addAll(createCharacterRows(katakanaLongVowels));
            longVowelsDescriptionContainer.setVisible(true);
            longVowelsDescriptionContainer.setManaged(true);
        }

        List<CharacterModel> katakanaSmallTsu = CharacterService.getCharactersByType("Katakana_Small_tsu");
        if (!katakanaSmallTsu.isEmpty()) {
            smallTsuButtonsContainer.getChildren().addAll(createCharacterRows(katakanaSmallTsu));
            smallTsuDescriptionContainer.setVisible(true);
            smallTsuDescriptionContainer.setManaged(true);
        }
    }

    private void loadKanjiCharacters() {
        clearAllContainers();

        List<CharacterModel> kanji = CharacterService.getCharactersByType("Kanji_Main");
        if (!kanji.isEmpty()) {
            characterButtonsContainer.getChildren().addAll(createCharacterRows(kanji));
        }
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
            showAlert(Alert.AlertType.ERROR, "Failed to learn characters.");
        }
    }
}