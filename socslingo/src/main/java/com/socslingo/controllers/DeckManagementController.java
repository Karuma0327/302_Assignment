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
    private static final Logger LOGGER = LoggerFactory.getLogger(DeckManagementController.class);

    @FXML
    private Button preview_deck_button;

    @FXML
    private TextField deck_name_text_field;

    @FXML
    private Button create_deck_button;

    @FXML
    private ListView<Deck> decks_list_view;

    @FXML
    private Button delete_deck_button;

    private ObservableList<Deck> decks_observable_list;

    @FXML
    private Button switch_to_flashcard_creation_button;

    private Map<String, String> button_to_fxml_map;

    @FXML 
    private Button switch_to_deck_flashcard_management_button;

    private DeckService deck_service;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Initializing DeckManagementController");

        button_to_fxml_map = new HashMap<>();
        button_to_fxml_map.put("switch_to_deck_flashcard_management_button", "/com/socslingo/views/deck_flashcard_management.fxml");
        button_to_fxml_map.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");

        try {
            DatabaseManager database_manager = DatabaseManager.getInstance();
            deck_service = new DeckService(new DeckDataAccess(database_manager));
            LOGGER.info("DeckService initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize DeckService", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize application components.");
            return;
        }

        decks_observable_list = FXCollections.observableArrayList();
        decks_list_view.setItems(decks_observable_list);
        decks_list_view.setEditable(true);
        decks_list_view.setCellFactory(param -> new DeckListCell());
        loadUserDecks();

        addGlobalClickListener();

        LOGGER.info("DeckManagementController initialized successfully");
    }

    private void addGlobalClickListener() {
        decks_list_view.sceneProperty().addListener((obs, old_scene, new_scene) -> {
            if (new_scene != null) {
                new_scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node clicked_node = event.getPickResult().getIntersectedNode();
                    if (clicked_node == null) {
                        decks_list_view.getSelectionModel().clearSelection();
                        return;
                    }

                    boolean is_click_inside_list_view = isDescendant(decks_list_view, clicked_node);
                    
                    boolean is_click_on_interactive_control = isDescendantOfInteractiveControl(clicked_node);

                    if (!is_click_inside_list_view && !is_click_on_interactive_control) {
                        decks_list_view.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    private boolean isDescendant(Node parent, Node child) {
        if (child == null) return false;
        Node current = child;
        while (current != null) {
            if (current == parent) return true;
            current = current.getParent();
        }
        return false;
    }

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
        Button clicked_button = (Button) event.getSource();
        String fxml_path = button_to_fxml_map.get(clicked_button.getId());

        if (fxml_path != null) {
            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContent(fxml_path);
            } else {
                LOGGER.error("PrimaryController instance is null.");
            }
        } else {
            LOGGER.error("No FXML mapping found for button ID: {}", clicked_button.getId());
        }
    }

    @FXML
    private void handleCreateDeck(ActionEvent event) {
        String deck_name = "New Deck";
        if (deck_name.isEmpty()) {
            LOGGER.warn("Deck creation failed: Deck name is empty");
            showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
            return;
        }

        int user_id = SessionManager.getInstance().getCurrentUserId();
        if (user_id == -1) {
            LOGGER.warn("Deck creation failed: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            Deck new_deck = deck_service.createDeck(user_id, deck_name);
            if (new_deck != null) {
                LOGGER.info("Deck '{}' created successfully with ID: {}", deck_name, new_deck.getDeckId());
                decks_observable_list.add(new_deck);
                decks_list_view.getSelectionModel().select(new_deck);
                decks_list_view.edit(decks_observable_list.indexOf(new_deck));
            } else {
                LOGGER.error("Failed to create deck '{}'", deck_name);
                showAlert(Alert.AlertType.ERROR, "Failed to create deck.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating deck '{}'", deck_name, e);
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void handleDeleteDeck(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
        LOGGER.debug("Attempting to delete deckId: {}", selected_deck != null ? selected_deck.getDeckId() : "null");

        if (selected_deck == null) {
            LOGGER.warn("Delete operation failed: No deck selected.");
            showAlert(Alert.AlertType.ERROR, "Please select a deck to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Deck");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the deck \"" + selected_deck.getDeckName() + "\"?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = deck_service.deleteDeck(selected_deck.getDeckId());

                if (success) {
                    LOGGER.info("DeckId: {} deleted successfully.", selected_deck.getDeckId());
                    showAlert(Alert.AlertType.INFORMATION, "Deck deleted successfully.");
                    loadUserDecks();
                } else {
                    LOGGER.error("Failed to delete DeckId: {}", selected_deck.getDeckId());
                    showAlert(Alert.AlertType.ERROR, "Failed to delete deck.");
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred while deleting DeckId: {}", selected_deck.getDeckId(), e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while deleting the deck.");
            }
        } else {
            LOGGER.info("Deck deletion cancelled by user.");
        }
    }

    @FXML
    private void navigateToFlashcardManagement(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
        if (selected_deck == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a deck to manage its flashcards.");
            return;
        }

        try {
            String fxml_path = "/com/socslingo/views/deck_flashcard_management.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node new_content = loader.load();

            DeckFlashcardManagementController controller = loader.getController();
            controller.setDeck(selected_deck);

            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContentNode(new_content);
                primary_controller.setActiveButton(null);
            } else {
                LOGGER.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load deck_flashcard_management.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load Flashcard Management view.");
        }
    }

    private void loadUserDecks() {
        LOGGER.debug("Loading decks for current user");
        int user_id = SessionManager.getInstance().getCurrentUserId();
        if (user_id == -1) {
            LOGGER.warn("Failed to load decks: No user is currently logged in.");
            showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
            return;
        }

        try {
            List<Deck> decks = deck_service.getUserDecks(user_id);
            decks_observable_list.setAll(decks);
            LOGGER.info("Loaded {} decks for userId: {}", decks.size(), user_id);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while loading decks for userId: {}", user_id, e);
            showAlert(Alert.AlertType.ERROR, "An error occurred while loading decks.");
        }
    }

    @FXML
    private void handleDeckDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
            if (selected_deck != null) {
                LOGGER.info("DeckId: {} was double-clicked.", selected_deck.getDeckId());
                navigateToFlashcardManagement(null);
            }
        }
    }

    private void showAlert(Alert.AlertType alert_type, String message) {
        LOGGER.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
        Alert alert = new Alert(alert_type);
        alert.setContentText(message);
        alert.showAndWait();
        LOGGER.debug("Alert displayed");
    }

    private class DeckListCell extends ListCell<Deck> {
        private TextField text_field;

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
            if (text_field == null) {
                createTextField();
            }
            setText(null);
            setGraphic(text_field);
            text_field.selectAll();
            text_field.getStyleClass().add("editing-cell");
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            Deck current_deck = getItem();
            if (current_deck != null) {
                setText(current_deck.getDeckName());
            } else {
                setText(null);
            }
            setGraphic(null);
            if (text_field != null) {
                text_field.getStyleClass().remove("editing-cell");
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
                    if (text_field != null) {
                        text_field.setText(item.getDeckName());
                    }
                    setText(null);
                    setGraphic(text_field);
                    text_field.getStyleClass().add("editing-cell");
                } else {
                    setText(item.getDeckName());
                    setGraphic(null);
                    if (text_field != null) {
                        text_field.getStyleClass().remove("editing-cell");
                    }
                }
            }
        }

        private void createTextField() {
            text_field = new TextField(getItem().getDeckName());
            text_field.setOnAction(evt -> commitEdit(getItem()));
            text_field.focusedProperty().addListener((obs, was_focused, is_now_focused) -> {
                if (!is_now_focused) {
                    commitEdit(getItem());
                }
            });
        }

        @Override
        public void commitEdit(Deck new_value) {
            String new_deck_name = text_field.getText().trim();
            Deck deck = getItem();

            if (new_deck_name.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Deck name cannot be empty.");
                cancelEdit();
                return;
            }

            deck.setDeckName(new_deck_name);
            super.commitEdit(deck);

            try {
                boolean success = deck_service.updateDeckName(deck.getDeckId(), new_deck_name);
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Failed to update deck name in database.");
                } else {
                    LOGGER.info("Deck name updated to '{}' in database for deckId {}", new_deck_name, deck.getDeckId());
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred while updating deck name for deckId {}", deck.getDeckId(), e);
                showAlert(Alert.AlertType.ERROR, "An error occurred while updating the deck name.");
            }
        }
    }

    @FXML
    private void handlePreviewDeck(ActionEvent event) {
        Deck selected_deck = decks_list_view.getSelectionModel().getSelectedItem();
        if (selected_deck == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a deck to preview.");
            return;
        }
        try {
            String fxml_path = "/com/socslingo/views/deck_preview.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node new_content = loader.load();

            DeckPreviewController controller = loader.getController();
            controller.setDeck(selected_deck);

            PrimaryController primary_controller = PrimaryController.getInstance();
            if (primary_controller != null) {
                primary_controller.switchContentNode(new_content);
                primary_controller.setActiveButton(null);
            } else {
                LOGGER.error("PrimaryController instance is null.");
                showAlert(Alert.AlertType.ERROR, "Failed to switch content.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load deck preview", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load deck preview.");
        }
    }



}