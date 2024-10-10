package com.socslingo.controllers;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.managers.*;
import com.socslingo.models.*;
import com.socslingo.services.DeckService;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.Node;

import org.slf4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DeckManagementController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DeckManagementController.class);


    @FXML
    private Button previewDeckButton;

    @FXML
    private TextField deckNameTextField;

    @FXML
    private Button createDeckButton;

    @FXML
    private ListView<Deck> decksListView;

    @FXML
    private Button deleteDeckButton;

    private ObservableList<Deck> decksObservableList;
    private Map<String, String> buttonToFXMLMap;

    @FXML 
    private Button switch_to_deck_flashcard_management_button;


    private DeckService deckService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing DeckManagementController");

        buttonToFXMLMap = new HashMap<>();
        buttonToFXMLMap.put("switch_to_deck_flashcard_management_button", "/com/socslingo/views/deck_flashcard_management.fxml");

        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            deckService = new DeckService(new DeckDataAccess(dbManager));
            logger.info("DeckService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize DeckService", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        decksObservableList = FXCollections.observableArrayList();
        decksListView.setItems(decksObservableList);
        decksListView.setEditable(true);
        decksListView.setCellFactory(param -> new DeckListCell());
        loadUserDecks();

        addGlobalClickListener();

        logger.info("DeckManagementController initialized successfully");
    }

    /**
     * Adds a global mouse click listener to the scene to handle deselection
     * when clicking outside the decksListView and interactive controls.
     */
    private void addGlobalClickListener() {
        // Listen for when the scene is available
        decksListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    if (clickedNode == null) {
                        // Clicked on empty space
                        decksListView.getSelectionModel().clearSelection();
                        return;
                    }

                    // Check if the click is inside decksListView
                    boolean isClickInsideListView = isDescendant(decksListView, clickedNode);

                    // Check if the click is on an interactive control (e.g., Button, TextField)
                    boolean isClickOnInteractiveControl = isDescendantOfInteractiveControl(clickedNode);

                    if (!isClickInsideListView && !isClickOnInteractiveControl) {
                        decksListView.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    /**
     * Helper method to determine if 'child' is a descendant of 'parent'.
     *
     * @param parent The potential ancestor node.
     * @param child  The node to check.
     * @return True if 'child' is a descendant of 'parent', false otherwise.
     */
    private boolean isDescendant(Node parent, Node child) {
        if (child == null) return false;
        Node current = child;
        while (current != null) {
            if (current == parent) return true;
            current = current.getParent();
        }
        return false;
    }

    /**
     * Helper method to determine if the clicked node is part of an interactive control.
     * You can expand this method to include other interactive controls as needed.
     *
     * @param node The node that was clicked.
     * @return True if the node is part of an interactive control, false otherwise.
     */
    private boolean isDescendantOfInteractiveControl(Node node) {
        if (node == null) return false;
        Node current = node;
        while (current != null) {
            if (current instanceof Button || current instanceof TextField || current instanceof CheckBox
                || current instanceof RadioButton || current instanceof ComboBox<?> || current instanceof MenuBar
                || current instanceof Slider || current instanceof ToggleButton) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String fxmlPath = buttonToFXMLMap.get(clickedButton.getId());

        if (fxmlPath != null) {
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContent(fxmlPath);
            } else {
                System.out.println("PrimaryController instance is null.");
            }
        } else {
            System.out.println("No FXML mapping found for button ID: " + clickedButton.getId());
        }
    }

    @FXML
    private void handleCreateDeck(ActionEvent event) {
        String deckName = "New Deck";
        if (deckName.isEmpty()) {
            logger.warn("Deck creation failed: Deck name is empty");
            showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
            return;
        }

        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("Deck creation failed: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            Deck newDeck = deckService.createDeck(userId, deckName);
            if (newDeck != null) {
                logger.info("Deck '{}' created successfully with ID: {}", deckName, newDeck.getDeckId());
                decksObservableList.add(newDeck);
                decksListView.getSelectionModel().select(newDeck);
                decksListView.edit(decksObservableList.indexOf(newDeck));
            } else {
                logger.error("Failed to create deck '{}'", deckName);
                showAlert(Alert.AlertType.ERROR, "Failed to create deck.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating deck '{}'", deckName, e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }


    @FXML
    private void handleDeleteDeck(ActionEvent event) {
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        logger.debug("Attempting to delete deckId: {}", selectedDeck != null ? selectedDeck.getDeckId() : "null");

        if (selectedDeck == null) {
            logger.warn("Delete operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Deck");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the deck \"" + selectedDeck.getDeckName() + "\"?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = deckService.deleteDeck(selectedDeck.getDeckId());

                if (success) {
                    logger.info("DeckId: {} deleted successfully.", selectedDeck.getDeckId());
                    showAlert(Alert.AlertType.INFORMATION, "Deck deleted successfully.");
                    loadUserDecks();
                } else {
                    logger.error("Failed to delete DeckId: {}", selectedDeck.getDeckId());
                    showAlert(Alert.AlertType.ERROR, "Failed to delete deck.");
                }
            } catch (Exception e) {
                logger.error("Exception occurred while deleting DeckId: {}", selectedDeck.getDeckId(), e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while deleting the deck.");
            }
        } else {
            logger.info("Deck deletion cancelled by user.");
        }
    }

    @FXML
    private void navigateToFlashcardManagement(ActionEvent event) {
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        if (selectedDeck == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a deck to manage its flashcards.");
            return;
        }

        try {
            String fxmlPath = "/com/socslingo/views/deck_flashcard_management.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node newContent = loader.load();

            // Get the controller and set the deck
            DeckFlashcardManagementController controller = loader.getController();
            controller.setDeck(selectedDeck);

            // Use the PrimaryController to switch content
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContentNode(newContent);

                // Optionally, update the active button if needed
                primaryController.setActiveButton(null);
            } else {
                logger.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }
        } catch (IOException e) {
            logger.error("Failed to load deck_flashcard_management.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Flashcard Management view.");
        }
    }

    private void loadUserDecks() {
        logger.debug("Loading decks for current user");
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) {
            logger.warn("Failed to load decks: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Deck> decks = deckService.getUserDecks(userId);
            decksObservableList.setAll(decks);
            logger.info("Loaded {} decks for userId: {}", decks.size(), userId);
        } catch (Exception e) {
            logger.error("Exception occurred while loading decks for userId: {}", userId, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading decks.");
        }
    }

    @FXML
    private void handleDeckDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
            if (selectedDeck != null) {
                logger.info("DeckId: {} was double-clicked.", selectedDeck.getDeckId());
                navigateToFlashcardManagement(null);
            }
        }
    }


    private void showAlert(Alert.AlertType alertType, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }

    private class DeckListCell extends ListCell<Deck> {
        private TextField textField;

        public DeckListCell() {
            this.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !isEmpty()) {
                    startEdit();
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.getStyleClass().add("editing-cell");
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem().getDeckName());
            setGraphic(null);
            if (textField != null) {
                textField.getStyleClass().remove("editing-cell");
            }
        }

        @Override
        public void updateItem(Deck item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(item.getDeckName());
                    }
                    setText(null);
                    setGraphic(textField);
                    textField.getStyleClass().add("editing-cell");
                } else {
                    setText(item.getDeckName());
                    setGraphic(null);
                    if (textField != null) {
                        textField.getStyleClass().remove("editing-cell");
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getItem().getDeckName());
            textField.setOnAction(evt -> commitEdit(getItem()));
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitEdit(getItem());
                }
            });
        }

        @Override
        public void commitEdit(Deck newValue) {
            String newDeckName = textField.getText().trim();
            Deck deck = getItem();

            if (newDeckName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
                cancelEdit();
                return;
            }

            deck.setDeckName(newDeckName);
            super.commitEdit(deck);

            try {
                boolean success = deckService.updateDeckName(deck.getDeckId(), newDeckName);
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Failed to update deck name in database.");
                } else {
                    logger.info("Deck name updated to '{}' in database for deckId {}", newDeckName, deck.getDeckId());
                }
            } catch (Exception e) {
                logger.error("Exception occurred while updating deck name for deckId {}", deck.getDeckId(), e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while updating the deck name.");
            }
        }
    }

    @FXML
    private void handlePreviewDeck(ActionEvent event) {
        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        if (selectedDeck == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a deck to preview.");
            return;
        }
        try {
            // Load the deck preview FXML
            String fxmlPath = "/com/socslingo/views/deck_preview.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node newContent = loader.load();

            // Get the controller and pass the selected deck
            DeckPreviewController controller = loader.getController();
            controller.setDeck(selectedDeck);

            // Use the PrimaryController to switch content
            PrimaryController primaryController = PrimaryController.getInstance();
            if (primaryController != null) {
                primaryController.switchContentNode(newContent);
                primaryController.setActiveButton(null);
            } else {
                logger.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }
        } catch (IOException e) {
            logger.error("Failed to load deck preview", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load deck preview.");
        }
    }
}
