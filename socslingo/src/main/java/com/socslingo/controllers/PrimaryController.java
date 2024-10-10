package com.socslingo.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socslingo.managers.ControllerManager;
import com.socslingo.managers.SceneManager;
import com.socslingo.managers.SessionManager;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class PrimaryController {

    // Initialize Logger
    private static final Logger logger = LoggerFactory.getLogger(PrimaryController.class);

    private static PrimaryController instance;

    public PrimaryController() {
        instance = this;
    }

    public static PrimaryController getInstance() {
        return instance;
    }

    @FXML
    private BorderPane rootPane; // Reference to the main BorderPane

    @FXML
    private VBox contentArea;

    // Sidebar Buttons
    @FXML
    private Button sidebar_switch_to_home_button;

    @FXML
    private Button sidebar_switch_to_main_flashcard_button;

    @FXML
    private Button switch_to_deck_creation_button;

    @FXML
    private Button switch_to_deck_management_button;

    @FXML
    private Button switch_to_flashcard_management_button;

    @FXML
    private Button profileButton;

    @FXML
    private Button moreButton;

    @FXML
    private ContextMenu moreContextMenu;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private VBox leftSidebar;

    // Add these fields
    private VBox rightSidebar;
    private boolean isRightSidebarVisible = true;


    private boolean isSidebarVisible = true;
    private Map<String, String> buttonToFXMLMap;
    private List<Button> sidebarButtons; // List to hold all sidebar buttons

    private ContextMenu sidebarContextMenu;

    @FXML
    private void initialize() {
        logger.info("Initializing PrimaryController");

        try {
            // Initialize the button to FXML mapping
            buttonToFXMLMap = new HashMap<>();
            buttonToFXMLMap.put("sidebar_switch_to_home_button", "/com/socslingo/views/home.fxml");
            buttonToFXMLMap.put("sidebar_switch_to_main_flashcard_button", "/com/socslingo/views/main_flashcard.fxml");
            buttonToFXMLMap.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");
            buttonToFXMLMap.put("switch_to_deck_creation_button", "/com/socslingo/views/deck_creation.fxml");
            buttonToFXMLMap.put("switch_to_deck_management_button", "/com/socslingo/views/deck_management.fxml");
            buttonToFXMLMap.put("switch_to_flashcard_management_button", "/com/socslingo/views/flashcard_management.fxml");
            buttonToFXMLMap.put("switchToCreateFlashCardPageButton", "/com/socslingo/views/createFlashcard.fxml");
            buttonToFXMLMap.put("switchToCreateFlashcardListPageButton", "/com/socslingo/views/createFlashcardList.fxml");
            buttonToFXMLMap.put("switchToMainFlashcardPageButton", "/com/socslingo/views/mainFlashcard.fxml");
            buttonToFXMLMap.put("switchToLoginFXMLButton", "/com/socslingo/views/login.fxml");
            buttonToFXMLMap.put("switchToRegistrationPageButton", "/com/socslingo/views/registration.fxml");
            buttonToFXMLMap.put("switchToRegistrationFXMLButton", "/com/socslingo/views/registration.fxml");

            // Initialize the list of sidebar buttons
            sidebarButtons = Arrays.asList(
                sidebar_switch_to_home_button,
                sidebar_switch_to_main_flashcard_button,
                switch_to_deck_creation_button,
                switch_to_deck_management_button,
                switch_to_flashcard_management_button,
                profileButton,
                moreButton
                // Add other sidebar buttons here if any
            );

            // Optionally, set the initial active button
            setActiveButton(sidebar_switch_to_home_button);

            setupContextMenu();

            // Initially hide the toggle button
            toggleSidebarButton.setVisible(false);

            // Initialize the sidebar context menu
            sidebarContextMenu = new ContextMenu();
            MenuItem hideSidebarItem = new MenuItem("Hide Sidebar");
            hideSidebarItem.setOnAction(e -> handleHideSidebar());
            sidebarContextMenu.getItems().add(hideSidebarItem);

            loadRightSidebar();
            logger.info("PrimaryController initialized successfully");
        } catch (Exception e) {
            logger.error("Exception during PrimaryController initialization", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize the application.");
        }
    }

    /**
     * Sets the active button by updating its CSS class and resetting others.
     * @param activeButton The button to set as active. Pass null to reset all.
     */
    public void setActiveButton(Button activeButton) {
        logger.debug("Setting active button: {}", activeButton != null ? activeButton.getId() : "None");
        for (Button button : sidebarButtons) {
            if (activeButton != null && button.equals(activeButton)) {
                // Remove other styles and add the active style
                button.getStyleClass().removeAll("left-sidebar-button", "left-sidebar-button-selected", "left-sidebar-button-active");
                if (!button.getStyleClass().contains("left-sidebar-button-active")) {
                    button.getStyleClass().add("left-sidebar-button-active");
                    logger.debug("Button '{}' set to active", button.getId());
                }
            } else {
                // Remove active style and ensure the default style is applied
                boolean wasActive = button.getStyleClass().removeAll("left-sidebar-button-active", "left-sidebar-button-selected");
                if (wasActive) {
                    logger.debug("Button '{}' deactivated", button.getId());
                }
                if (!button.getStyleClass().contains("left-sidebar-button")) {
                    button.getStyleClass().add("left-sidebar-button");
                }
            }
        }
    }

    @FXML
    private void handleLeftSidebarButtonAction(ActionEvent event) {
        logger.info("Left Sidebar Button Clicked!");
        showAlert(Alert.AlertType.INFORMATION, "Left Sidebar Button Clicked!");
    }

    /**
     * Generic method to load FXML content into the contentArea with a fade transition.
     * @param fxmlPath The path to the FXML file to load.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void switchContent(String fxmlPath) throws IOException {
        logger.debug("Switching content to: {}", fxmlPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(ControllerManager.getInstance()); // Ensure controller factory is set
        Node node = loader.load();

        // Clear existing content
        contentArea.getChildren().clear();
        logger.debug("Cleared existing content in contentArea");

        // Add new content
        contentArea.getChildren().add(node);
        logger.debug("Added new content to contentArea");

        // Apply fade-in transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        logger.debug("Applied fade-in transition to new content");

        // Manage the right sidebar based on the loaded content
        if (isHomePage(fxmlPath)) {
            loadRightSidebar();
        } else {
            removeRightSidebar();
        }
    }

    private boolean isHomePage(String fxmlPath) {
        // Adjust the condition based on your actual home page FXML path
        boolean isHome = fxmlPath.equals("/com/socslingo/views/home.fxml");
        logger.debug("isHomePage check for '{}': {}", fxmlPath, isHome);
        return isHome;
    }

    private void loadRightSidebar() throws IOException {
        logger.debug("Loading right sidebar");
        if (rightSidebar == null) {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/socslingo/views/right_sidebar.fxml"));
            sidebarLoader.setControllerFactory(ControllerManager.getInstance()); // Ensure controller factory is set
            rightSidebar = sidebarLoader.load();
        }
        rootPane.setRight(rightSidebar);
        isRightSidebarVisible = true;
        logger.info("Right sidebar loaded");
    }


    private void removeRightSidebar() {
        logger.debug("Removing right sidebar");
        rootPane.setRight(null);
        isRightSidebarVisible = false;
        logger.info("Right sidebar removed");
    }


    /**
     * Public method to switch to the main application content.
     * This can be called by other controllers like LoginController.
     */
    public void switchToMain() {
        logger.info("Switching to main application content");
        try {
            switchContent("/com/socslingo/views/main.fxml"); // Adjust the path as needed
        } catch (IOException e) {
            logger.error("Failed to switch to main.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load the main application content.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        logger.info("Logout initiated");
        System.out.println("Logging out..."); // Replace with logging
        showAlert(Alert.AlertType.INFORMATION, "Logging out...");

        // Clear user session data
        clearUserSession();

        // Switch to the login scene using SceneManager
        SceneManager.getInstance().switchToLogin();

        // Optionally, hide the context menu if it's still visible
        moreContextMenu.hide();
        logger.info("Logout successful and switched to login screen");
    }

    private void clearUserSession() {
        // Clear the current user in SessionManager
        SessionManager.getInstance().setCurrentUser(null);
        logger.debug("User session cleared");
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        logger.info("Settings button clicked");
        try {
            switchContent("/com/socslingo/views/settings.fxml");
            setActiveButton(null); // Optionally reset active button
        } catch (IOException e) {
            logger.error("Failed to load settings.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load settings.");
        }
        moreContextMenu.hide();
    }

    /**
     * Handles the Help action from the ContextMenu.
     */
    @FXML
    private void handleHelp(ActionEvent event) {
        logger.info("Help option selected from ContextMenu");
        try {
            switchContent("/com/socslingo/views/help.fxml");
            setActiveButton(null); // Optionally reset active button
        } catch (IOException e) {
            logger.error("Failed to load help.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load help.");
        }
        moreContextMenu.hide();
    }

    private void setupContextMenu() {
        logger.debug("Setting up ContextMenu for 'More' button");
        // Initially hide the ContextMenu
        moreContextMenu.hide();

        // Flag to track if the ContextMenu is being shown
        final boolean[] isContextMenuVisible = {false};

        // Show the ContextMenu when hovering over the "More" button
        moreButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!isContextMenuVisible[0]) {
                logger.debug("Mouse entered 'More' button");
                // Position the ContextMenu to the right of the moreButton
                Bounds moreButtonBounds = moreButton.localToScreen(moreButton.getBoundsInLocal());
                // Position the ContextMenu slightly offset from the button
                moreContextMenu.show(moreButton, moreButtonBounds.getMaxX() + 10, moreButtonBounds.getMinY());
                isContextMenuVisible[0] = true;
                logger.info("'More' ContextMenu displayed");
            }
        });

        // Create a PauseTransition to hide the ContextMenu after a delay
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            moreContextMenu.hide();
            isContextMenuVisible[0] = false;
            logger.debug("'More' ContextMenu hidden after delay");
        });

        // Start the pause when the mouse exits the "More" button
        moreButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            logger.debug("Mouse exited 'More' button");
            pause.playFromStart();
        });

        // If the mouse enters the ContextMenu, stop the pause (keep it open)
        moreContextMenu.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            logger.debug("Mouse entered ContextMenu");
            pause.stop();
        });

        // If the mouse exits the ContextMenu, start the pause to hide it
        moreContextMenu.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            logger.debug("Mouse exited ContextMenu");
            pause.playFromStart();
        });

        logger.info("ContextMenu setup completed");
    }

    /**
     * Utility method to display alerts.
     *
     * @param alertType Type of the alert.
     * @param message   Message to display.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        logger.debug("Displaying alert of type '{}' with message: {}", alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Alert displayed");
    }

    public void switchContentNode(Node node) {
        // Clear existing content
        contentArea.getChildren().clear();
        logger.debug("Cleared existing content in contentArea");

        // Add new content
        contentArea.getChildren().add(node);
        logger.debug("Added new content to contentArea");

        // Apply fade-in transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        logger.debug("Applied fade-in transition to new content");

        // Manage the right sidebar based on the loaded content
        // Adjust this based on your needs
        removeRightSidebar();
    }

    @FXML
    private void handleToggleSidebar() {
        logger.info("Toggle button clicked");
        showSidebar();
    }

    /**
     * Handles the right-click event on the sidebar to show the context menu.
     */
    @FXML
    private void handleSidebarMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            logger.debug("Right-click detected on sidebar");
            sidebarContextMenu.show(leftSidebar, event.getScreenX(), event.getScreenY());
        }
    }

    /**
     * Handles hiding the sidebar when "Hide Sidebar" is selected from the context menu.
     */
    private void handleHideSidebar() {
        logger.info("Hide Sidebar menu item selected");
        hideSidebar();
    }

    private void hideSidebar() {
        if (isSidebarVisible) {
            logger.info("Hiding sidebar");
            // Animate sidebar hiding
            TranslateTransition hideSidebar = new TranslateTransition(Duration.millis(300), leftSidebar);
            hideSidebar.setToX(-leftSidebar.getWidth());
            hideSidebar.setInterpolator(Interpolator.EASE_IN);
            hideSidebar.play();

            // Move the toggleSidebarButton with the sidebar
            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleSidebarButton);
            moveButton.setToX(0);
            moveButton.setInterpolator(Interpolator.EASE_IN);
            moveButton.play();

            // After animation, adjust visibility
            hideSidebar.setOnFinished(event -> {
                leftSidebar.setVisible(false);
                leftSidebar.setManaged(false);
                leftSidebar.setTranslateX(0); // Reset translateX
                logger.debug("Sidebar hidden");

                // Ensure the toggle button is visible and at the correct position
                toggleSidebarButton.setVisible(true);
                toggleSidebarButton.setTranslateX(0);
                toggleSidebarButton.setOpacity(1); // Ensure opacity is reset
                logger.debug("Toggle button made visible");
            });

            isSidebarVisible = false;
        }
    }

    private void showSidebar() {
        if (!isSidebarVisible) {
            logger.info("Showing sidebar");
            // Make sidebar visible before animation
            leftSidebar.setVisible(true);
            leftSidebar.setManaged(true);
            leftSidebar.setTranslateX(-leftSidebar.getWidth());

            // Move toggleSidebarButton to the starting position
            toggleSidebarButton.setTranslateX(0);
            toggleSidebarButton.setOpacity(1); // Ensure it's fully visible

            // Animate sidebar showing
            TranslateTransition showSidebar = new TranslateTransition(Duration.millis(300), leftSidebar);
            showSidebar.setFromX(-leftSidebar.getWidth());
            showSidebar.setToX(0);
            showSidebar.setInterpolator(Interpolator.EASE_OUT);

            // Animate the toggle button moving with the sidebar
            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleSidebarButton);
            moveButton.setFromX(0);
            moveButton.setToX(leftSidebar.getWidth());
            moveButton.setInterpolator(Interpolator.EASE_OUT);

            // Create a parallel transition to animate both at the same time
            ParallelTransition parallelTransition = new ParallelTransition(showSidebar, moveButton);
            parallelTransition.play();

            // After animation, fade out the toggleSidebarButton
            parallelTransition.setOnFinished(event -> {
                // Fade out the toggleSidebarButton
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toggleSidebarButton);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    toggleSidebarButton.setVisible(false);
                    toggleSidebarButton.setTranslateX(0); // Reset position
                    toggleSidebarButton.setOpacity(1);     // Reset opacity for next time
                    logger.debug("Toggle button faded out and made invisible");
                });
                fadeOut.play();
                logger.debug("Sidebar shown");
            });

            isSidebarVisible = true;
        }
    }

    public Button getDeckManagementButton() {
        return switch_to_deck_management_button;
    }

    
    @FXML
    private Button toggleRightSidebarButton;
    // Method to instantly hide the right sidebar without animation (used during initialization)
    private void hideRightSidebarInstantly() {
        if (rightSidebar != null) {
            rootPane.setRight(null);
            isRightSidebarVisible = false;
            toggleRightSidebarButton.setTranslateX(0);
            toggleRightSidebarButton.setOpacity(1.0);
            toggleRightSidebarButton.setVisible(true);
        }
    }

    public void showRightSidebar() {
        if (rightSidebar != null && !isRightSidebarVisible) {
            logger.info("Showing right sidebar");

            double sidebarWidth = rightSidebar.getWidth();
            if (sidebarWidth <= 0) {
                sidebarWidth = rightSidebar.prefWidth(-1);
            }

            // Ensure the sidebar is added to the rootPane
            rootPane.setRight(rightSidebar);
            rightSidebar.setTranslateX(sidebarWidth);

            // Position the toggleRightSidebarButton to the initial position
            toggleRightSidebarButton.setTranslateX(0);
            toggleRightSidebarButton.setOpacity(1);
            toggleRightSidebarButton.setVisible(true);

            // Create TranslateTransition for the sidebar sliding in
            TranslateTransition showSidebar = new TranslateTransition(Duration.millis(300), rightSidebar);
            showSidebar.setFromX(sidebarWidth);
            showSidebar.setToX(0);
            showSidebar.setInterpolator(Interpolator.EASE_OUT);

            // Create TranslateTransition for the toggle button moving with the sidebar
            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleRightSidebarButton);
            moveButton.setFromX(0);
            moveButton.setToX(-sidebarWidth);
            moveButton.setInterpolator(Interpolator.EASE_OUT);

            // Combine both transitions into a ParallelTransition
            ParallelTransition parallelTransition = new ParallelTransition(showSidebar, moveButton);
            parallelTransition.play();

            // After the sidebar is shown and the button has moved, fade out the button
            parallelTransition.setOnFinished(event -> {
                isRightSidebarVisible = true;
                logger.debug("Right sidebar shown");

                // Fade out the toggleRightSidebarButton
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toggleRightSidebarButton);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> toggleRightSidebarButton.setVisible(false));
                fadeOut.play();
            });
        }
    }

    public void hideRightSidebar() {
        if (rightSidebar != null && isRightSidebarVisible) {
            logger.info("Hiding right sidebar");

            double sidebarWidth = rightSidebar.getWidth();
            if (sidebarWidth <= 0) {
                sidebarWidth = rightSidebar.prefWidth(-1);
            }

            // Create TranslateTransition for the sidebar sliding out
            TranslateTransition hideSidebar = new TranslateTransition(Duration.millis(300), rightSidebar);
            hideSidebar.setFromX(0);
            hideSidebar.setToX(sidebarWidth);
            hideSidebar.setInterpolator(Interpolator.EASE_IN);

            // Create TranslateTransition for the toggle button moving back
            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleRightSidebarButton);
            moveButton.setFromX(-sidebarWidth);
            moveButton.setToX(0);
            moveButton.setInterpolator(Interpolator.EASE_IN);

            // Combine both transitions into a ParallelTransition
            ParallelTransition parallelTransition = new ParallelTransition(hideSidebar, moveButton);
            parallelTransition.play();

            // After the sidebar is hidden and the button has moved back, fade in the button
            parallelTransition.setOnFinished(event -> {
                rootPane.setRight(null);
                isRightSidebarVisible = false;
                logger.debug("Right sidebar hidden");

                // Fade in the toggleRightSidebarButton
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toggleRightSidebarButton);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.setOnFinished(e -> toggleRightSidebarButton.setVisible(true));
                fadeIn.play();
            });
        }
    }

    @FXML
    private void handleToggleRightSidebar() {
        logger.info("Toggle right sidebar button clicked");
        if (isRightSidebarVisible) {
            hideRightSidebar();
        } else {
            showRightSidebar();
        }
    }

    @FXML
    private HBox statusBar;

    @FXML
    private Label statusLabel;

    // Existing initialize method...

    /**
     * Sets the status bar to active state with a custom message.
     * @param message The message to display in the status bar.
     */
    private void setStatusBarActive(String message) {
        // Update the label text
        Platform.runLater(() -> statusLabel.setText(message));

        // Change the style to active
        Platform.runLater(() -> {
            statusBar.getStyleClass().remove("status-bar");
            if (!statusBar.getStyleClass().contains("status-bar-active")) {
                statusBar.getStyleClass().add("status-bar-active");
            }
        });

        // Revert back after a delay
        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Adjust duration as needed
        pause.setOnFinished(event -> resetStatusBar());
        pause.play();
    }

    /**
     * Resets the status bar to its default state.
     */
    private void resetStatusBar() {
        // Reset the label text
        Platform.runLater(() -> statusLabel.setText("Ready"));

        // Reset the style
        Platform.runLater(() -> {
            statusBar.getStyleClass().remove("status-bar-active");
            if (!statusBar.getStyleClass().contains("status-bar")) {
                statusBar.getStyleClass().add("status-bar");
            }
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String fxmlPath = buttonToFXMLMap.get(clickedButton.getId());
        if (fxmlPath != null) {
            logger.info("Button '{}' clicked. Loading FXML: {}", clickedButton.getId(), fxmlPath);
            try {
                switchContent(fxmlPath);
                setActiveButton(clickedButton); // Update the active button styling

                // Change the status bar to orange with a message
                setStatusBarActive("Loaded " + clickedButton.getText() + " page.");

            } catch (IOException e) {
                logger.error("Failed to load FXML: {}", fxmlPath, e);
                showAlert(Alert.AlertType.ERROR, "Failed to load the requested page.");
            }
        } else {
            logger.warn("No FXML mapping found for button ID: {}", clickedButton.getId());
            showAlert(Alert.AlertType.WARNING, "No action defined for this button.");
        }
    }

}
