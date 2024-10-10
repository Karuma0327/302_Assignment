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

    private static final Logger logger = LoggerFactory.getLogger(DeckPreviewRightSidebarController.class);

    @FXML
    private ListView<Flashcard> flashcardsListView;

    private ObservableList<Flashcard> flashcardsObservableList;

    private FlashcardService flashcardService;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            flashcardService = new FlashcardService(new com.socslingo.dataAccess.FlashcardDataAccess(dbManager));
            new DeckService(new DeckDataAccess(dbManager));
            logger.info("FlashcardService and DeckService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize services", e);
            return;
        }

        flashcardsObservableList = FXCollections.observableArrayList();
        flashcardsListView.setItems(flashcardsObservableList);

        flashcardsListView.setCellFactory(param -> new ListCell<Flashcard>() {
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

        flashcardsListView.setOnDragDetected(event -> {
            Flashcard selectedFlashcard = flashcardsListView.getSelectionModel().getSelectedItem();
            if (selectedFlashcard == null) {
                return;
            }

            Dragboard db = flashcardsListView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selectedFlashcard.getId()));
            db.setContent(content);

            event.consume();
        });
    }

    private void loadUserFlashcards() {
        try {
            int userId = getCurrentUserId();
            List<Flashcard> userFlashcards = flashcardService.getUserFlashcards(userId);
            flashcardsObservableList.setAll(userFlashcards);
            logger.info("Loaded {} flashcards for userId {}", userFlashcards.size(), userId);
        } catch (Exception e) {
            logger.error("Failed to load user flashcards", e);
        }
    }

    private int getCurrentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }
    
}
