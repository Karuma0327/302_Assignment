package com.socslingo.managers;

import javafx.util.Callback;
import com.socslingo.controllers.LoginController;
import com.socslingo.controllers.RegistrationController;
import com.socslingo.controllers.FlashcardController;
import com.socslingo.controllers.PrimaryController;
import com.socslingo.dataAccess.*;
import com.socslingo.services.*;

public class ControllerManager implements Callback<Class<?>, Object> {
    private static ControllerManager instance;

    private UserService userService;
    private FlashcardService flashcardService;

    // Singleton instances of controllers
    private PrimaryController primaryController;

    private ControllerManager() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        UserDataAccess userDataAccess = new UserDataAccess(databaseManager);
        FlashcardDataAccess flashcardDataAccess = new FlashcardDataAccess(databaseManager);
        userService = new UserService(userDataAccess);
        flashcardService = new FlashcardService(flashcardDataAccess);
    }

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
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
            } else if (controllerClass == PrimaryController.class) {
                // Ensure only one instance of PrimaryController
                if (primaryController == null) {
                    primaryController = new PrimaryController();
                }
                return primaryController;
            } else {
                // For controllers with default constructors
                return controllerClass.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
