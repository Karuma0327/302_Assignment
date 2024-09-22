package com.socslingo.controllers.flashcard;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import com.socslingo.models.Flashcard;
import com.socslingo.services.FlashcardService;
import com.socslingo.controllers.BaseController;
import com.socslingo.controllers.common.SidebarController;
import com.socslingo.utils.DateUtils;
import com.socslingo.managers.SessionManager;

import java.util.List;

/**
 * Controller class for managing flashcard-related interactions.
 * <p>
 * This class handles the creation, viewing, and navigation of flashcards within the application.
 * It interacts with the {@code FlashcardService} to perform database operations and manages UI transitions.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class FlashcardController extends BaseController {

    
    private FlashcardService flashcardService;

    /**
     * Constructs a new {@code FlashcardController} with the specified {@code FlashcardService}.
     * 
     * @param flashcardService the service responsible for flashcard-related operations
     */
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @FXML
    private Button saveFlashcardButton;

    @FXML
    private Label frontLabel;

    @FXML
    private Label backLabel;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button nextFlashcardButton;

    @FXML
    private Button previousFlashcardButton;

    private List<Flashcard> flashcards;
    private int currentIndex = 0;

    @FXML
    private Pane sidebarContainer;

    /**
     * Initializes the controller class. This method is automatically called after the FXML
     * file has been loaded. It sets up the sidebar and logs initialization details.
     */
    @FXML
    public void initialize() {
        super.initialize();
        if (sidebarContainer != null && sidebar != null) {
            sidebarContainer.getChildren().add(sidebar);
        }

        System.out.println("FlashcardController initialized");
        System.out.println("FlashcardController.sidebar: " + this.sidebar);
        System.out.println("FlashcardController.sidebarController: " + this.sidebarController);
    }

    /**
     * Handles the logout action triggered by the user.
     * <p>
     * This method logs out the current user and performs necessary cleanup.
     * </p>
     */
    @FXML
    private void handleLogout() {
        System.out.println("Logged out!");
    }

    /**
     * Handles the action of saving a new flashcard.
     * <p>
     * This method retrieves the input from the front and back text fields, validates the input,
     * hashes the password, and saves the flashcard using the {@code FlashcardService}.
     * </p>
     */
    @FXML
    public void handleSaveFlashcardAction() {
        // Retrieve text from TextFields
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        // Validate input
        if (frontText.isEmpty() || backText.isEmpty()) {
            System.out.println("Front text or back text is empty.");
            // Optionally, display an error message to the user
            return;
        }

        int userId = getUserId();
        String createdDate = getCurrentDate();

        // Save flashcard to database using flashcardService
        flashcardService.createFlashcard(userId, frontText, backText, createdDate);

        System.out.println("Save Flashcard Button Clicked!");
    }

    /**
     * Retrieves the current date in a specific format.
     * 
     * @return the current date as a {@code String}
     */
    private String getCurrentDate() {
        // Use DateUtils to get the current date
        return DateUtils.getCurrentDate();
    }

    /**
     * Retrieves the current user's ID from the session.
     * 
     * @return the current user's ID
     */
    private int getUserId() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        System.out.println("Retrieved User ID from SessionManager: " + userId);
        return userId;
    }

    /**
     * Handles the action of viewing all flashcards for the current user.
     * <p>
     * This method retrieves flashcards from the {@code FlashcardService} and displays the first
     * flashcard if available.
     * </p>
     */
    @FXML
    public void handleViewFlashcardsAction() {
        System.out.println("handleViewFlashcardsAction called");

        int userId = getUserId();
        System.out.println("Current User ID: " + userId);

        flashcards = flashcardService.getUserFlashcards(userId);
        System.out.println("Number of flashcards retrieved: " + flashcards.size());

        if (!flashcards.isEmpty()) {
            currentIndex = 0; // Reset to the first flashcard
            displayFlashcard(flashcards.get(currentIndex));
        } else {
            System.out.println("No flashcards to display.");
            frontLabel.setText("");
            backLabel.setText("");
        }
        System.out.println("Flashcards displayed");
    }

    /**
     * Handles the action of displaying the next flashcard.
     * <p>
     * This method transitions to the next flashcard in the list with a fade animation.
     * </p>
     */
    @FXML
    public void handleNextFlashcardAction() {
        System.out.println("Next flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stackPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                currentIndex = (currentIndex + 1) % flashcards.size();
                displayFlashcard(flashcards.get(currentIndex));
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), stackPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        }
    }

    /**
     * Handles the action of displaying the previous flashcard.
     * <p>
     * This method transitions to the previous flashcard in the list with a fade animation.
     * </p>
     */
    @FXML
    public void handlePreviousFlashcardAction() {
        System.out.println("Previous flashcard button clicked");
        if (flashcards != null && !flashcards.isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stackPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
                displayFlashcard(flashcards.get(currentIndex));
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), stackPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        }
    }

    /**
     * Displays the specified flashcard in the UI.
     * <p>
     * This method updates the front and back labels with the flashcard's content and sets up
     * a mouse click handler to flip the flashcard between front and back views.
     * </p>
     * 
     * @param flashcard the {@code Flashcard} to display
     */
    private void displayFlashcard(Flashcard flashcard) {
        frontLabel.setText(flashcard.getFront());
        backLabel.setText(flashcard.getBack());
        frontLabel.setVisible(true);
        backLabel.setVisible(false);

        stackPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();

            RotateTransition rotateOutFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateOutFront.setFromAngle(0);
            rotateOutFront.setToAngle(90);
            rotateOutFront.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateInBack.setFromAngle(-90);
            rotateInBack.setToAngle(0);
            rotateInBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateOutBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateOutBack.setFromAngle(0);
            rotateOutBack.setToAngle(90);
            rotateOutBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateInFront.setFromAngle(-90);
            rotateInFront.setToAngle(0);
            rotateInFront.setAxis(Rotate.X_AXIS);

            SequentialTransition flipToBack = new SequentialTransition(rotateOutFront, rotateInBack);
            SequentialTransition flipToFront = new SequentialTransition(rotateOutBack, rotateInFront);

            if (isFrontVisible) {
                backLabel.setVisible(true);
                flipToBack.setOnFinished(e -> frontLabel.setVisible(false));
                flipToBack.play();
            } else {
                frontLabel.setVisible(true);
                flipToFront.setOnFinished(e -> backLabel.setVisible(false));
                flipToFront.play();
            }
        });
    }

    @FXML
    private Button previewFlashcardButton;

    @FXML
    private TextField frontTextField;

    @FXML
    private TextField backTextField;

    /**
     * Handles the action of previewing a flashcard before saving.
     * <p>
     * This method retrieves the input from the front and back text fields, validates the input,
     * and displays a preview of the flashcard.
     * </p>
     */
    @FXML
    public void handlePreviewFlashcardAction() {
        System.out.println("Preview flashcard button clicked");
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        if (frontText.isEmpty() || backText.isEmpty()) {
            System.out.println("Front text or back text is empty.");
            // Optionally, display an error message to the user
            return;
        }

        frontLabel.setText(frontText);
        backLabel.setText(backText);
        frontLabel.setVisible(true);
        backLabel.setVisible(false);

        stackPane.setOnMouseClicked(event -> {
            boolean isFrontVisible = frontLabel.isVisible();

            RotateTransition rotateOutFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateOutFront.setFromAngle(0);
            rotateOutFront.setToAngle(90);
            rotateOutFront.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateInBack.setFromAngle(-90);
            rotateInBack.setToAngle(0);
            rotateInBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateOutBack = new RotateTransition(Duration.millis(150), backLabel);
            rotateOutBack.setFromAngle(0);
            rotateOutBack.setToAngle(90);
            rotateOutBack.setAxis(Rotate.X_AXIS);

            RotateTransition rotateInFront = new RotateTransition(Duration.millis(150), frontLabel);
            rotateInFront.setFromAngle(-90);
            rotateInFront.setToAngle(0);
            rotateInFront.setAxis(Rotate.X_AXIS);

            SequentialTransition flipToBack = new SequentialTransition(rotateOutFront, rotateInBack);
            SequentialTransition flipToFront = new SequentialTransition(rotateOutBack, rotateInFront);

            if (isFrontVisible) {
                backLabel.setVisible(true);
                flipToBack.setOnFinished(e -> frontLabel.setVisible(false));
                flipToBack.play();
            } else {
                frontLabel.setVisible(true);
                flipToFront.setOnFinished(e -> backLabel.setVisible(false));
                flipToFront.play();
            }
        });
    }

    /**
     * Sets the sidebar UI component.
     * <p>
     * This method injects the sidebar into the sidebar container pane.
     * </p>
     * 
     * @param sidebar the {@code VBox} representing the sidebar
     */
    @Override
    public void setSidebar(VBox sidebar) {
        super.setSidebar(sidebar);
        if (sidebarContainer != null && sidebar != null) {
            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidebar);
        } else {
            if (sidebarContainer == null) {
                System.err.println("sidebarContainer is null in " + this.getClass().getSimpleName());
            }
            if (sidebar == null) {
                System.err.println("sidebar is null in " + this.getClass().getSimpleName());
            }
        }
    }

    /**
     * Sets the sidebar controller.
     * <p>
     * This method injects the {@code SidebarController} into the flashcard controller.
     * </p>
     * 
     * @param sidebarController the {@code SidebarController} instance
     */
    @Override
    public void setSidebarController(SidebarController sidebarController) {
        super.setSidebarController(sidebarController);
        System.out.println("SidebarController injected into FlashcardController");
    }
}
