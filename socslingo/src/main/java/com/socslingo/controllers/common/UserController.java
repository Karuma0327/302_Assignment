package com.socslingo.controllers.common;

import com.socslingo.controllers.BaseController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * Controller class responsible for managing user-related operations.
 * <p>
 * This class handles user authentication, profile management, and other user-related functionalities.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class UserController extends BaseController {

    /**
     * Default constructor for UserController.
     */
    public UserController() {
        // Default constructor
    }
    @FXML
    private ImageView mascotImageView;

    @FXML
    public void initialize() {

    }

    @FXML
    private Button switchToMainHomeFXMLButton;

    @FXML
    private Button switchToInitialFlashcardLandingPageButton;

    @FXML
    private Button switchToCreateFlashcardFXMLButton;

    @FXML
    private Button switchToMainFlashcardFXMLButton;

    @FXML
    private Button switchToProfileSceneButton;

    @FXML
    private Button switchToCreateFlashCardPageButton;

    @FXML
    private Button switchToLoginFXMLButton;

    @FXML
    private Button switchToRegistrationPageButton;

    @FXML
    private Button switchToRegistrationFXMLButton;

    @FXML
    private Button switchToViewFlashcardFXMLButton;

    

}
