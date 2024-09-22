package com.socslingo.managers;

import javafx.util.Callback;
import com.socslingo.controllers.BaseController;
import com.socslingo.controllers.authentication.LoginController;
import com.socslingo.controllers.authentication.RegistrationController;
import com.socslingo.controllers.flashcard.FlashcardController;
import com.socslingo.services.FlashcardService;
import com.socslingo.services.UserService;
import com.socslingo.dao.FlashcardDAO;
import com.socslingo.dao.UserDAO;
import com.socslingo.App;

/**
 * Singleton class responsible for managing and instantiating controllers with their dependencies.
 * <p>
 * This class implements the {@code Callback} interface to provide controller instances
 * with their required services and DAOs. It ensures that each controller is properly
 * initialized with the necessary dependencies.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class ControllerManager implements Callback<Class<?>, Object> {
    private static ControllerManager instance;

    private UserService userService;
    private FlashcardService flashcardService;

    /**
     * Private constructor to enforce singleton pattern.
     * <p>
     * Initializes the {@code UserService} and {@code FlashcardService} with their respective DAOs.
     * </p>
     */
    private ControllerManager() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        UserDAO userDAO = new UserDAO(databaseManager);
        FlashcardDAO flashcardDAO = new FlashcardDAO(databaseManager);
        userService = new UserService(userDAO);
        flashcardService = new FlashcardService(flashcardDAO);
    }

    /**
     * Retrieves the singleton instance of {@code ControllerManager}.
     * 
     * @return the singleton instance of {@code ControllerManager}
     */
    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    /**
     * Creates and returns an instance of the specified controller class with its dependencies.
     * <p>
     * This method checks the type of the controller class and instantiates it with the necessary
     * services. For controllers that extend {@code BaseController}, it also injects the sidebar
     * and sidebar controller instances.
     * </p>
     * 
     * @param controllerClass the {@code Class} object of the controller to be instantiated
     * @return an instance of the specified controller class
     * @throws RuntimeException if an error occurs during controller instantiation
     */
    @Override
    public Object call(Class<?> controllerClass) {
        try {
            if (controllerClass == LoginController.class) {
                return new LoginController(userService);
            } else if (controllerClass == RegistrationController.class) {
                return new RegistrationController(userService);
            } else if (controllerClass == FlashcardController.class) {
                return new FlashcardController(flashcardService);
            } else {
                // For other controllers, use default constructor
                Object controller = controllerClass.getDeclaredConstructor().newInstance();
                if (controller instanceof BaseController) {
                    BaseController baseController = (BaseController) controller;
                    // Inject the sidebar and sidebarController
                    baseController.setSidebar(App.getInstance().getSidebar());
                    baseController.setSidebarController(App.getInstance().getSidebarController());
                }
                return controller;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
