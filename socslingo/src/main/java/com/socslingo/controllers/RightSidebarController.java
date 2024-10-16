// src/main/java/com/socslingo/controllers/RightSidebarController.java
package com.socslingo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import com.socslingo.managers.ControllerManager;
import com.socslingo.services.DeckService;
import com.socslingo.services.FlashcardService;
import com.socslingo.utils.DateUtils;
import com.socslingo.managers.SessionManager;
import com.socslingo.models.Deck;
import com.socslingo.models.Flashcard;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

/**
 * Controller class for the Right Sidebar.
 */
public class RightSidebarController {

    @FXML
    private VBox right_sidebar;

    @FXML
    private ScrollPane scrollPane; // Reference to the ScrollPane

    @FXML
    private ListView<Flashcard> listView; // Updated to handle Flashcard objects

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private VBox flashcardContainer; // Reference to Flashcard Container

    @FXML
    private VBox deckContainer; // Reference to Deck Container

    @FXML
    private Button flashcardButton; // Button to toggle Flashcard Content

    @FXML
    private Button deckButton; // Button to toggle Deck Content

    // New @FXML references for toggleable content
    @FXML
    private VBox flashcardContent; // Toggleable Flashcard Content

    @FXML
    private VBox deckContent; // Toggleable Deck Content

    // New @FXML references for dynamic quick save fields
    @FXML
    private VBox quickSaveFieldsContainer;

    @FXML
    private TextField quickSaveNumberField;

    private ContextMenu sidebar_context_menu;

    private List<Pair<TextField, TextField>> quickSaveFieldPairs = new ArrayList<>();

    // Reference to FlashcardService
    private FlashcardService flashcardService;

    private DeckService deckService;  // Reference to DeckService
    private int userId;               // Current user ID

    private int currentPage = 0;             // Current page index for pagination
    private final int itemsPerPage = 5;      // Number of items per page
    private List<Flashcard> currentFlashcards; // Flashcards to display in ListView

    @FXML
    private Button nextButton;   // Next Page button
    @FXML
    private Button backButton;   // Back Page button

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        setupComboBox();
        setupListView(); 
        listView.setItems(FXCollections.observableArrayList(
            // Initial sample data as Flashcard objects
            new Flashcard(1, "Sample Front 1", "Sample Back 1"),
            new Flashcard(2, "Sample Front 2", "Sample Back 2"),
            new Flashcard(3, "Sample Front 3", "Sample Back 3"),
            new Flashcard(4, "Sample Front 4", "Sample Back 4"),
            new Flashcard(5, "Sample Front 5", "Sample Back 5")
        ));
        setupContextMenu();

        // Set fixed cell size and adjust ListView height
        listView.setFixedCellSize(50.0);
        listView.setPrefHeight(150.0);
        listView.setMinHeight(150.0);
        // Disable scrolling within the ListView
        listView.setFocusTraversable(false);

        // Allow the ListView to grow horizontally within its container
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Add event handlers for toggle buttons
        flashcardButton.setOnAction(e -> toggleFlashcardContent());
        deckButton.setOnAction(e -> toggleDeckContent());

        // Initialize FlashcardService using the singleton instance of ControllerManager
        flashcardService = ControllerManager.getInstance().getFlashcardService();
        deckService = ControllerManager.getInstance().getDeckService();
        // Initialize DeckService and get User ID
        userId = getUserId();

        // Populate ComboBox with "All Cards" and user's decks
        populateComboBox();

        // Add listener to ComboBox selection changes
        comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            handleDeckSelection(newValue);
        });

        // Initialize ListView and display all flashcards by default
        currentFlashcards = flashcardService.getAllFlashcardsForUser(userId);
        updateListView();

        
        // Add listener to quickSaveNumberField
        if (quickSaveNumberField != null) { // Null check to prevent NPE
            quickSaveNumberField.setText("3"); // Set default value to 3

            quickSaveNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
                handleQuickSaveNumberChange(newValue);
            });
        } else {
            System.err.println("quickSaveNumberField is not initialized. Check fx:id in FXML.");
        }
        updateQuickSaveFields(3);
    }

    /**
     * Populates the ComboBox with "All Cards" and user's decks.
     */
    private void populateComboBox() {
        List<Deck> userDecks = deckService.getUserDecks(userId);

        // Create an ObservableList and add "All Cards" option
        ObservableList<String> deckOptions = FXCollections.observableArrayList();
        deckOptions.add("All Cards");

        // Add user's deck names to the list
        for (Deck deck : userDecks) {
            deckOptions.add(deck.getDeckName());
        }

        // Set the items for the ComboBox
        comboBox.setItems(deckOptions);

        // Set default selection to "All Cards"
        comboBox.getSelectionModel().selectFirst();
    }

    /**
     * Handles deck selection changes from the ComboBox.
     * @param selectedDeckName The name of the selected deck.
     */
    private void handleDeckSelection(String selectedDeckName) {
        currentPage = 0; // Reset to first page on deck selection

        if ("All Cards".equals(selectedDeckName)) {
            // Get all flashcards for the user
            currentFlashcards = flashcardService.getAllFlashcardsForUser(userId);
        } else {
            // Get the selected deck by name
            Deck selectedDeck = deckService.getDeckByName(userId, selectedDeckName);
            if (selectedDeck != null) {
                // Get flashcards in the selected deck
                currentFlashcards = deckService.getFlashcardsInDeck(selectedDeck.getDeckId());
            } else {
                // If deck not found, display empty list
                currentFlashcards = new ArrayList<>();
            }
        }

        // Update the ListView to display the new set of flashcards
        updateListView();
    }

    /**
     * Updates the ListView with flashcards for the current page.
     */
    private void updateListView() {
        int fromIndex = currentPage * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, currentFlashcards.size());

        ObservableList<Flashcard> pageItems;

        if (fromIndex >= toIndex) {
            // No items to display
            pageItems = FXCollections.observableArrayList();
        } else {
            List<Flashcard> pageList = currentFlashcards.subList(fromIndex, toIndex);
            pageItems = FXCollections.observableArrayList(pageList);
        }

        listView.setItems(pageItems);

        // Remove or comment out the disabling of buttons
        // backButton.setDisable(currentPage == 0);
        // int maxPage = (currentFlashcards.size() - 1) / itemsPerPage;
        // nextButton.setDisable(currentPage >= maxPage);
    }

    /**
     * Handles the action when the Next button is clicked.
     */
    @FXML
    private void handleNextAction() {
        int maxPage = (currentFlashcards.size() - 1) / itemsPerPage;
        if (currentPage < maxPage) {
            currentPage++;
            updateListView();
        } else {
            // Optionally, wrap around to the first page
            currentPage = 0;
            updateListView();
        }
    }

    /**
     * Handles the action when the Back button is clicked.
     */
    @FXML
    private void handleBackAction() {
        if (currentPage > 0) {
            currentPage--;
            updateListView();
        } else {
            // Optionally, wrap around to the last page
            int maxPage = (currentFlashcards.size() - 1) / itemsPerPage;
            currentPage = maxPage;
            updateListView();
        }
    }

    /**
     * Handles the action of saving flashcards from the right sidebar.
     */
    @FXML
    private void handleSaveFlashcards() {
        int userId = getUserId();
        String createdDate = getCurrentDate();

        boolean allSaved = true;

        for (Pair<TextField, TextField> pair : quickSaveFieldPairs) {
            String front = pair.getKey().getText().trim();
            String back = pair.getValue().getText().trim();

            if (!front.isEmpty() && !back.isEmpty()) {
                try {
                    flashcardService.createFlashcard(userId, front, back, createdDate);
                } catch (Exception e) {
                    allSaved = false;
                    System.err.println("Error saving flashcard: " + e.getMessage());
                    // Optionally log the error
                }
            } else {
                System.out.println("Front or Back field is empty. Skipping this flashcard.");
            }
        }

        if (allSaved) {
            showAlert(Alert.AlertType.INFORMATION, "All flashcards saved successfully.");
            clearQuickSaveFields();
            // Refresh flashcard list in case new cards are added
            currentFlashcards = flashcardService.getAllFlashcardsForUser(userId);
            updateListView();
        } else {
            showAlert(Alert.AlertType.WARNING, "Some flashcards could not be saved. Please check the logs.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears all quick save text fields.
     */
    private void clearQuickSaveFields() {
        for (Pair<TextField, TextField> pair : quickSaveFieldPairs) {
            pair.getKey().clear();
            pair.getValue().clear();
        }
    }

    /**
     * Retrieves the current user's ID from the session.
     * @return The user ID.
     */
    private int getUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }

    /**
     * Retrieves the current date as a String.
     * @return The current date.
     */
    private String getCurrentDate() {
        return DateUtils.getCurrentDate();
    }

    /**
     * Handles changes in the quickSaveNumberField TextField.
     * @param newValue The new value input by the user.
     */
    private void handleQuickSaveNumberChange(String newValue) {
        // Parse the input to an integer
        int numberOfCards;
        try {
            numberOfCards = Integer.parseInt(newValue);
            if (numberOfCards < 0) {
                numberOfCards = 0;
            }
        } catch (NumberFormatException e) {
            numberOfCards = 0;
        }

        // Update the UI to match the number of cards
        updateQuickSaveFields(numberOfCards);
    }

    /**
     * Updates the quickSaveFieldsContainer with the specified number of field pairs.
     * @param count The number of front-back TextField pairs to display.
     */
    private void updateQuickSaveFields(int count) {
        // Clear existing fields
        quickSaveFieldsContainer.getChildren().clear();
        quickSaveFieldPairs.clear(); // Clear the existing list to prevent duplicates

        for (int i = 0; i < count; i++) {
            HBox fieldContainer = createQuickSaveFieldContainer(i + 1);
            quickSaveFieldsContainer.getChildren().add(fieldContainer);
        }
    }

    /**
     * Creates an HBox containing a pair of TextFields for FRONT and BACK.
     * @param index The index number for labeling (optional).
     * @return The configured HBox.
     */
    private HBox createQuickSaveFieldContainer(int index) {
        HBox hBox = new HBox();
        hBox.setAlignment(javafx.geometry.Pos.CENTER);
        hBox.getStyleClass().add("quick-save-text-field-container");

        TextField frontField = new TextField();
        frontField.setPromptText("FRONT");
        frontField.getStyleClass().add("right-sidebar-left-text-field-quick-add");

        TextField backField = new TextField();
        backField.setPromptText("BACK");
        backField.getStyleClass().add("right-sidebar-right-text-field-quick-add");

        hBox.getChildren().addAll(frontField, backField);
        quickSaveFieldPairs.add(new Pair<>(frontField, backField));

        return hBox;
    }

    /**
     * Sets up the ComboBox with custom styling.
     */
    private void setupComboBox() {
        // Initial items can be empty as they will be populated dynamically
        comboBox.setItems(FXCollections.observableArrayList());

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            getStyleClass().remove("custom-list-view-cell");
                        } else {
                            setText(item);
                            // Apply a custom style class to each cell
                            if (!getStyleClass().contains("custom-list-view-cell")) {
                                getStyleClass().add("custom-list-view-cell");
                            }
                        }
                    }
                };
            }
        });
    }

    /**
     * Sets up the ContextMenu for the sidebar.
     */
    private void setupContextMenu() {
        sidebar_context_menu = new ContextMenu();
        MenuItem hide_sidebar_item = new MenuItem("Hide Sidebar");
        hide_sidebar_item.setOnAction(e -> handleHideSidebar());
        sidebar_context_menu.getItems().add(hide_sidebar_item);

        right_sidebar.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                sidebar_context_menu.show(right_sidebar, event.getScreenX(), event.getScreenY());
            } else {
                sidebar_context_menu.hide();
            }
        });
    }

    /**
     * Handles hiding the sidebar.
     */
    private void handleHideSidebar() {
        PrimaryController.getInstance().hideRightSidebar();
    }

    /**
     * Sets up the ListView with a custom cell factory.
     */
    private void setupListView() {
        listView.setCellFactory(new Callback<ListView<Flashcard>, ListCell<Flashcard>>() {
            @Override
            public ListCell<Flashcard> call(ListView<Flashcard> param) {
                return new RightSidebarListCell();
            }
        });
    }

    /**
     * Toggles the visibility of the Flashcard Content.
     */
    private void toggleFlashcardContent() {
        boolean isVisible = flashcardContent.isVisible();
        flashcardContent.setVisible(!isVisible);
        flashcardContent.setManaged(!isVisible);
    }

    /**
     * Toggles the visibility of the Deck Content.
     */
    private void toggleDeckContent() {
        boolean isVisible = deckContent.isVisible();
        deckContent.setVisible(!isVisible);
        deckContent.setManaged(!isVisible);
    }

}