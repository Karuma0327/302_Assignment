package com.socslingo.controllers;

import com.socslingo.dataAccess.*;
import com.socslingo.managers.*;
import com.socslingo.models.Flashcard;
import com.socslingo.services.*;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import org.slf4j.*;

import java.net.URL;
import java.util.*;

public class DeckPreviewRightSidebarController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeckPreviewRightSidebarController.class);

    @FXML
    private ListView<Flashcard> flashcards_list_view;

    private ObservableList<Flashcard> flashcards_observable_list;

    private FlashcardService flashcard_service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseManager database_manager = DatabaseManager.getInstance();
            flashcard_service = new FlashcardService(new com.socslingo.dataAccess.FlashcardDataAccess(database_manager));
            new DeckService(new DeckDataAccess(database_manager));
            LOGGER.info("FlashcardService and DeckService initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize services", e);
            return;
        }

        flashcards_observable_list = FXCollections.observableArrayList();
        flashcards_list_view.setItems(flashcards_observable_list);

        flashcards_list_view.setCellFactory(param -> new ListCell<Flashcard>() {
            @Override
            protected void updateItem(Flashcard item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFront() + " - " + item.getBack());
                }
            }
        });

        loadUserFlashcards();

        flashcards_list_view.setOnDragDetected(event -> {
            Flashcard selected_flashcard = flashcards_list_view.getSelectionModel().getSelectedItem();
            if (selected_flashcard == null) {
                return;
            }

            Dragboard dragboard = flashcards_list_view.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selected_flashcard.getId()));
            dragboard.setContent(content);

            event.consume();
        });
    }

    private void loadUserFlashcards() {
        try {
            int user_id = getCurrentUserId();
            List<Flashcard> user_flashcards = flashcard_service.getUserFlashcards(user_id);
            flashcards_observable_list.setAll(user_flashcards);
            LOGGER.info("Loaded {} flashcards for userId {}", user_flashcards.size(), user_id);
        } catch (Exception e) {
            LOGGER.error("Failed to load user flashcards", e);
        }
    }

    private int getCurrentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }
    
}