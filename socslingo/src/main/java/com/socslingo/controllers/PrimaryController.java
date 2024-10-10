package com.socslingo.controllers;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import com.socslingo.managers.*;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimaryController {

    private static final Logger logger = LoggerFactory.getLogger(PrimaryController.class);

    private static PrimaryController instance;

    public PrimaryController() {
        instance = this;
    }

    public static PrimaryController getInstance() {
        return instance;
    }

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox contentArea;

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
    private Button switch_to_profile_button;

    @FXML
    private Button moreButton;

    @FXML
    private ContextMenu moreContextMenu;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private VBox leftSidebar;

    private VBox rightSidebar;
    private boolean isRightSidebarVisible = true;

    private boolean isSidebarVisible = true;
    private Map<String, String> buttonToFXMLMap;
    private List<Button> sidebarButtons;

    private ContextMenu sidebarContextMenu;

    @FXML
    private HBox statusBar;

    @FXML
    private void initialize() {
        logger.info("Initializing PrimaryController");

        try {
            buttonToFXMLMap = new HashMap<>();
            buttonToFXMLMap.put("sidebar_switch_to_home_button", "/com/socslingo/views/home.fxml");
            buttonToFXMLMap.put("sidebar_switch_to_main_flashcard_button", "/com/socslingo/views/main_flashcard.fxml");
            buttonToFXMLMap.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");
            buttonToFXMLMap.put("switch_to_deck_creation_button", "/com/socslingo/views/deck_creation.fxml");
            buttonToFXMLMap.put("switch_to_deck_management_button", "/com/socslingo/views/deck_management.fxml");
            buttonToFXMLMap.put("switch_to_profile_button", "/com/socslingo/views/profile.fxml");
            buttonToFXMLMap.put("switch_to_flashcard_management_button",
                    "/com/socslingo/views/flashcard_management.fxml");
            buttonToFXMLMap.put("switchToCreateFlashCardPageButton", "/com/socslingo/views/createFlashcard.fxml");
            buttonToFXMLMap.put("switchToCreateFlashcardListPageButton",
                    "/com/socslingo/views/createFlashcardList.fxml");
            buttonToFXMLMap.put("switchToMainFlashcardPageButton", "/com/socslingo/views/mainFlashcard.fxml");
            buttonToFXMLMap.put("switchToLoginFXMLButton", "/com/socslingo/views/login.fxml");
            buttonToFXMLMap.put("switchToRegistrationPageButton", "/com/socslingo/views/registration.fxml");
            buttonToFXMLMap.put("switchToRegistrationFXMLButton", "/com/socslingo/views/registration.fxml");

            sidebarButtons = Arrays.asList(
                    sidebar_switch_to_home_button,
                    sidebar_switch_to_main_flashcard_button,
                    switch_to_deck_creation_button,
                    switch_to_deck_management_button,
                    switch_to_flashcard_management_button,
                    switch_to_profile_button,
                    moreButton);

            setActiveButton(sidebar_switch_to_home_button);

            setupContextMenu();

            toggleSidebarButton.setVisible(false);

            sidebarContextMenu = new ContextMenu();
            MenuItem hideSidebarItem = new MenuItem("Hide Sidebar");
            hideSidebarItem.setOnAction(e -> handleHideSidebar());
            sidebarContextMenu.getItems().add(hideSidebarItem);

            loadRightSidebar();
            logger.info("PrimaryController initialized successfully");

            statusBar.prefWidthProperty().bind(stackPane.widthProperty());
            applyAnimatedGlowEffect();

            // applyLetterSpacingToStyleClass(leftSidebar, "left-sidebar-button", 0.1);

        } catch (Exception e) {
            logger.error("Exception during PrimaryController initialization", e);
            showAlert(Alert.AlertType.ERROR, "Failed to initialize the application.");
        }
    }

    private void applyLetterSpacingToStyleClass(Parent parent, String styleClass, double spacing) {
        for (Node node : parent.lookupAll("." + styleClass)) {
            if (node instanceof Label) {
                Label labelNode = (Label) node;
                labelNode.setText(applyLetterSpacing(labelNode.getText(), spacing));
            } else if (node instanceof Button) {
                Button buttonNode = (Button) node;
                buttonNode.setText(applyLetterSpacing(buttonNode.getText(), spacing));
            }
        }
    }

    private String applyLetterSpacing(String text, double spacing) {
        StringBuilder spacedText = new StringBuilder();
        String smallSpace = "\u200A"; // Hair space character
        int fullSpaces = (int) spacing;
        int fractionalSpaces = (int) ((spacing - fullSpaces) * 10);

        for (char c : text.toCharArray()) {
            spacedText.append(c);
            for (int i = 0; i < fullSpaces; i++) {
                spacedText.append(" ");
            }
            for (int i = 0; i < fractionalSpaces; i++) {
                spacedText.append(smallSpace);
            }
        }
        return spacedText.toString();
    }

    /**
     * Sets the active button by updating its CSS class and resetting others.
     * @param activeButton The button to set as active. Pass null to reset all.
     */
    public void setActiveButton(Button activeButton) {
        logger.debug("Setting active button: {}", activeButton != null ? activeButton.getId() : "None");
        for (Button button : sidebarButtons) {
            if (activeButton != null && button.equals(activeButton)) {
                button.getStyleClass().removeAll("left-sidebar-button", "left-sidebar-button-selected",
                        "left-sidebar-button-active");
                if (!button.getStyleClass().contains("left-sidebar-button-active")) {
                    button.getStyleClass().add("left-sidebar-button-active");
                    logger.debug("Button '{}' set to active", button.getId());
                }
            } else {
                boolean wasActive = button.getStyleClass().removeAll("left-sidebar-button-active",
                        "left-sidebar-button-selected");
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
    public void switchContent(String fxmlPath, Consumer<FXMLLoader> controllerConsumer) throws IOException {
        logger.debug("Switching content to: {}", fxmlPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(ControllerManager.getInstance());
        Node node = loader.load();

        if (controllerConsumer != null) {
            controllerConsumer.accept(loader);
        }

        contentArea.getChildren().clear();
        logger.debug("Cleared existing content in contentArea");

        contentArea.getChildren().add(node);
        logger.debug("Added new content to contentArea");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        logger.debug("Applied fade-in transition to new content");

        if (shouldShowRightSidebar(fxmlPath)) {
            loadRightSidebar();
        } else {
            removeRightSidebar();
        }
    }

    public void switchContent(String fxmlPath) throws IOException {
        switchContent(fxmlPath, null);
    }

    private boolean shouldShowRightSidebar(String fxmlPath) {
        List<String> fxmlWithSidebar = Arrays.asList(
                "/com/socslingo/views/home.fxml",
                "/com/socslingo/views/deck_management.fxml",
                "/com/socslingo/views/deck_preview.fxml");
        boolean shouldShow = fxmlWithSidebar.contains(fxmlPath);
        logger.debug("shouldShowRightSidebar check for '{}': {}", fxmlPath, shouldShow);
        return shouldShow;
    }

    private void loadRightSidebar() throws IOException {
        logger.debug("Loading right sidebar");
        if (rightSidebar == null) {
            FXMLLoader sidebarLoader = new FXMLLoader(
                    getClass().getResource("/com/socslingo/views/right_sidebar.fxml"));
            sidebarLoader.setControllerFactory(ControllerManager.getInstance());
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
            switchContent("/com/socslingo/views/main.fxml");
        } catch (IOException e) {
            logger.error("Failed to switch to main.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load the main application content.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        logger.info("Logout initiated");
        System.out.println("Logging out...");
        showAlert(Alert.AlertType.INFORMATION, "Logging out...");

        clearUserSession();

        SceneManager.getInstance().switchToLogin();

        moreContextMenu.hide();
        logger.info("Logout successful and switched to login ");
    }

    private void clearUserSession() {
        SessionManager.getInstance().setCurrentUser(null);
        logger.debug("User session cleared");
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        logger.info("Settings button clicked");
        try {
            switchContent("/com/socslingo/views/settings.fxml");
            setActiveButton(null);
        } catch (IOException e) {
            logger.error("Failed to load settings.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load settings.");
        }
        moreContextMenu.hide();
    }

    @FXML
    private void handleHelp(ActionEvent event) {
        logger.info("Help option selected from ContextMenu");
        try {
            switchContent("/com/socslingo/views/help.fxml");
            setActiveButton(null);
        } catch (IOException e) {
            logger.error("Failed to load help.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Failed to load help.");
        }
        moreContextMenu.hide();
    }

    private void setupContextMenu() {
        logger.debug("Setting up ContextMenu for 'More' button");
        moreContextMenu.hide();
        final boolean[] isContextMenuVisible = { false };

        moreButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!isContextMenuVisible[0]) {
                logger.debug("Mouse entered 'More' button");
                Bounds moreButtonBounds = moreButton.localToScreen(moreButton.getBoundsInLocal());
                moreContextMenu.show(moreButton, moreButtonBounds.getMaxX() + 10, moreButtonBounds.getMinY());
                isContextMenuVisible[0] = true;
                logger.info("'More' ContextMenu displayed");
            }
        });

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            moreContextMenu.hide();
            isContextMenuVisible[0] = false;
            logger.debug("'More' ContextMenu hidden after delay");
        });

        moreButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            logger.debug("Mouse exited 'More' button");
            pause.playFromStart();
        });

        moreContextMenu.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            logger.debug("Mouse entered ContextMenu");
            pause.stop();
        });

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
        contentArea.getChildren().clear();
        logger.debug("Cleared existing content in contentArea");

        contentArea.getChildren().add(node);
        logger.debug("Added new content to contentArea");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        logger.debug("Applied fade-in transition to new content");

        manageToggleButtonsVisibility();
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
            TranslateTransition hideSidebar = new TranslateTransition(Duration.millis(300), leftSidebar);
            hideSidebar.setToX(-leftSidebar.getWidth());
            hideSidebar.setInterpolator(Interpolator.EASE_IN);
            hideSidebar.play();

            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleSidebarButton);
            moveButton.setToX(0);
            moveButton.setInterpolator(Interpolator.EASE_IN);
            moveButton.play();

            hideSidebar.setOnFinished(event -> {
                leftSidebar.setVisible(false);
                leftSidebar.setManaged(false);
                leftSidebar.setTranslateX(0);
                logger.debug("Sidebar hidden");

                toggleSidebarButton.setVisible(true);
                toggleSidebarButton.setTranslateX(0);
                toggleSidebarButton.setOpacity(1);
                logger.debug("Toggle button made visible");
            });

            isSidebarVisible = false;
        }
    }

    private void showSidebar() {
        if (!isSidebarVisible) {
            logger.info("Showing sidebar");
            leftSidebar.setVisible(true);
            leftSidebar.setManaged(true);
            leftSidebar.setTranslateX(-leftSidebar.getWidth());

            toggleSidebarButton.setTranslateX(0);
            toggleSidebarButton.setOpacity(1);

            TranslateTransition showSidebar = new TranslateTransition(Duration.millis(300), leftSidebar);
            showSidebar.setFromX(-leftSidebar.getWidth());
            showSidebar.setToX(0);
            showSidebar.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleSidebarButton);
            moveButton.setFromX(0);
            moveButton.setToX(leftSidebar.getWidth());
            moveButton.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition parallelTransition = new ParallelTransition(showSidebar, moveButton);
            parallelTransition.play();

            parallelTransition.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toggleSidebarButton);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    toggleSidebarButton.setVisible(false);
                    toggleSidebarButton.setTranslateX(0);
                    toggleSidebarButton.setOpacity(1);
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

    public void showRightSidebar() {
        if (rightSidebar != null && !isRightSidebarVisible) {
            logger.info("Showing right sidebar");

            double sidebarWidth = rightSidebar.getWidth();
            if (sidebarWidth <= 0) {
                sidebarWidth = rightSidebar.prefWidth(-1);
            }

            rootPane.setRight(rightSidebar);
            rightSidebar.setTranslateX(sidebarWidth);

            toggleRightSidebarButton.setTranslateX(0);
            toggleRightSidebarButton.setOpacity(1);
            toggleRightSidebarButton.setVisible(true);

            TranslateTransition showSidebar = new TranslateTransition(Duration.millis(300), rightSidebar);
            showSidebar.setFromX(sidebarWidth);
            showSidebar.setToX(0);
            showSidebar.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleRightSidebarButton);
            moveButton.setFromX(0);
            moveButton.setToX(-sidebarWidth);
            moveButton.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition parallelTransition = new ParallelTransition(showSidebar, moveButton);
            parallelTransition.play();

            parallelTransition.setOnFinished(event -> {
                isRightSidebarVisible = true;
                logger.debug("Right sidebar shown");

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

            TranslateTransition hideSidebar = new TranslateTransition(Duration.millis(300), rightSidebar);
            hideSidebar.setFromX(0);
            hideSidebar.setToX(sidebarWidth);
            hideSidebar.setInterpolator(Interpolator.EASE_IN);

            TranslateTransition moveButton = new TranslateTransition(Duration.millis(300), toggleRightSidebarButton);
            moveButton.setFromX(-sidebarWidth);
            moveButton.setToX(0);
            moveButton.setInterpolator(Interpolator.EASE_IN);

            ParallelTransition parallelTransition = new ParallelTransition(hideSidebar, moveButton);
            parallelTransition.play();

            parallelTransition.setOnFinished(event -> {
                rootPane.setRight(null);
                isRightSidebarVisible = false;
                logger.debug("Right sidebar hidden");

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

    private void applyAnimatedGlowEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("rgba(0, 0, 255, 0.6)"));
        glow.setRadius(30);
        glow.setOffsetX(0);
        glow.setOffsetY(10);
        statusBar.setEffect(glow);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), statusBar);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.8);
        fadeIn.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), statusBar);
        fadeOut.setFromValue(0.8);
        fadeOut.setToValue(1.0);
        fadeOut.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition pulsate = new SequentialTransition(fadeIn, fadeOut);
        pulsate.setCycleCount(Animation.INDEFINITE);
        pulsate.play();

        logger.info("Animated glow effect applied to status bar");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String fxmlPath = buttonToFXMLMap.get(clickedButton.getId());
        if (fxmlPath != null) {
            logger.info("Button '{}' clicked. Loading FXML: {}", clickedButton.getId(), fxmlPath);
            try {
                if (fxmlPath.equals("/com/socslingo/views/deck_management.fxml")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    loader.setControllerFactory(ControllerManager.getInstance());
                    Node newContent = loader.load();

                    switchContentNode(newContent);
                    setActiveButton(clickedButton);

                } else {
                    switchContent(fxmlPath);
                    setActiveButton(clickedButton);
                }

                applyWaveAnimation();

            } catch (IOException e) {
                logger.error("Failed to load FXML: {}", fxmlPath, e);
                showAlert(Alert.AlertType.ERROR, "Failed to load the requested page.");
            }
        } else {
            logger.warn("No FXML mapping found for button ID: {}", clickedButton.getId());
            showAlert(Alert.AlertType.WARNING, "No action defined for this button.");
        }
    }

    private void manageToggleButtonsVisibility() {
        if (!isSidebarVisible) {
            toggleSidebarButton.setVisible(true);
        } else {
            toggleSidebarButton.setVisible(false);
        }

        if (!isRightSidebarVisible) {
            toggleRightSidebarButton.setVisible(true);
        } else {
            toggleRightSidebarButton.setVisible(false);
        }
    }

    private final Color lightOrange = Color.rgb(255, 214, 129);
    private final Color lightBlue = Color.rgb(173, 216, 230);

    private void applyWaveAnimation() {
        HBox gradientBox = new HBox();
        gradientBox.setPrefWidth(statusBar.getWidth() * 1.5);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, lightOrange),
                new Stop(0.5, lightBlue),
                new Stop(1, lightOrange));

        gradientBox.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, null)));

        statusBar.getChildren().add(gradientBox);

        TranslateTransition bounceTransition = new TranslateTransition(Duration.seconds(2), gradientBox);
        bounceTransition.setFromX(-gradientBox.getPrefWidth() / 2);
        bounceTransition.setToX(statusBar.getWidth() / 2);
        bounceTransition.setAutoReverse(true);
        bounceTransition.setCycleCount(2);

        TranslateTransition offScreenTransition = new TranslateTransition(Duration.seconds(2), gradientBox);
        offScreenTransition.setFromX(statusBar.getWidth() / 2);
        offScreenTransition.setToX(statusBar.getWidth());

        SequentialTransition fullAnimation = new SequentialTransition(bounceTransition, offScreenTransition);

        fullAnimation.play();
    }

}
