package com.socslingo.managers;

import javafx.util.Callback;
import com.socslingo.controllers.*;
import com.socslingo.dataAccess.*;
import com.socslingo.services.*;

public class ControllerManager implements Callback<Class<?>, Object> {
    private static ControllerManager instance;

    private UserService user_service;
    private FlashcardService flashcard_service;
    private DeckService deck_service;

    private PrimaryController primary_controller;

    private ControllerManager() {
        DatabaseManager database_manager = DatabaseManager.getInstance();
        UserDataAccess user_data_access = new UserDataAccess(database_manager);
        FlashcardDataAccess flashcard_data_access = new FlashcardDataAccess(database_manager);
        DeckDataAccess deck_data_access = new DeckDataAccess(database_manager);
        user_service = new UserService(user_data_access);
        flashcard_service = new FlashcardService(flashcard_data_access);
        deck_service = new DeckService(deck_data_access);
    }

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    public FlashcardService getFlashcardService() {
        return flashcard_service;
    }

    public DeckService getDeckService() {
        return deck_service;
    }

    @Override
    public Object call(Class<?> controller_class) {
        try {
            if (controller_class == LoginController.class) {
                return new LoginController(user_service);
            } else if (controller_class == RegistrationController.class) {
                return new RegistrationController(user_service);
            } else if (controller_class == FlashcardController.class) {
                return new FlashcardController(flashcard_service);
            } else if (controller_class == DeckPreviewController.class) {
                return new DeckPreviewController();
            } else if (controller_class == DeckPreviewRightSidebarController.class) {
                return new DeckPreviewRightSidebarController();
            } else if (controller_class == PrimaryController.class) {
                if (primary_controller == null) {
                    primary_controller = new PrimaryController();
                }
                return primary_controller;
            } else {
                return controller_class.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
