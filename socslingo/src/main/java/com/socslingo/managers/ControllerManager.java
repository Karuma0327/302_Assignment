package com.socslingo.managers;

import javafx.util.Callback;
import com.socslingo.controllers.*;
import com.socslingo.dataAccess.*;
import com.socslingo.services.*;

public class ControllerManager implements Callback<Class<?>, Object> {
    private static ControllerManager instance;

    private UserService userService;
    private FlashcardService flashcardService;
    private DeckService deckService;

    private PrimaryController primaryController;

    private ControllerManager() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        UserDataAccess userDataAccess = new UserDataAccess(databaseManager);
        FlashcardDataAccess flashcardDataAccess = new FlashcardDataAccess(databaseManager);
        DeckDataAccess deckDataAccess = new DeckDataAccess(databaseManager);
        userService = new UserService(userDataAccess);
        flashcardService = new FlashcardService(flashcardDataAccess);
        deckService = new DeckService(deckDataAccess);
    }

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    public FlashcardService getFlashcardService() {
        return flashcardService;
    }

    public DeckService getDeckService() {
        return deckService;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            if (controllerClass == LoginController.class) {
                return new LoginController(userService);
            } else if (controllerClass == RegistrationController.class) {
                return new RegistrationController(userService);
            } else if (controllerClass == FlashcardController.class) {
                return new FlashcardController(flashcardService);
            } else if (controllerClass == DeckPreviewController.class) {
                return new DeckPreviewController();
            } else if (controllerClass == DeckPreviewRightSidebarController.class) {
                return new DeckPreviewRightSidebarController();
            } else if (controllerClass == PrimaryController.class) {
                if (primaryController == null) {
                    primaryController = new PrimaryController();
                }
                return primaryController;
            } else {
                return controllerClass.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
