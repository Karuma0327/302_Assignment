    package com.socslingo.controllers;

    import java.io.IOException;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.function.Consumer;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import com.socslingo.managers.ControllerManager;
    import com.socslingo.managers.SceneManager;
    import com.socslingo.managers.SessionManager;

    import javafx.animation.Animation;
    import javafx.animation.FadeTransition;
    import javafx.animation.Interpolator;
    import javafx.animation.ParallelTransition;
    import javafx.animation.PauseTransition;
    import javafx.animation.SequentialTransition;
    import javafx.animation.TranslateTransition;
    import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

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
        private StackPane stack_pane;

        @FXML
        private BorderPane root_pane;

        @FXML
        private VBox content_area;

        @FXML
        private Button sidebar_switch_to_home_button;

        @FXML
        private Button sidebar_switch_to_main_flashcard_button;

        @FXML
        private Button switch_to_activity_main_button;

        @FXML
        private Button switch_to_deck_management_button;

        @FXML
        private Button switch_to_flashcard_management_button;

        @FXML
        private Button switch_to_profile_button;

        @FXML
        private Button more_button;

        @FXML
        private ContextMenu more_context_menu;

        @FXML
        private Button toggle_sidebar_button;

        @FXML
        private VBox left_sidebar;

        private VBox right_sidebar;
        private boolean is_right_sidebar_visible = true;

        private boolean is_sidebar_visible = true;
        private Map<String, String> button_to_fxml_map;
        private List<Button> sidebar_buttons;

        private ContextMenu sidebar_context_menu;

        @FXML
        private HBox status_bar;

        @FXML
        private void initialize() {
            logger.info("Initializing PrimaryController");

            try {
                button_to_fxml_map = new HashMap<>();
                button_to_fxml_map.put("sidebar_switch_to_home_button", "/com/socslingo/views/home.fxml");
                button_to_fxml_map.put("sidebar_switch_to_main_flashcard_button", "/com/socslingo/views/main_flashcard.fxml");
                button_to_fxml_map.put("switch_to_flashcard_creation_button", "/com/socslingo/views/create_flashcard.fxml");
                button_to_fxml_map.put("switch_to_deck_creation_button", "/com/socslingo/views/deck_creation.fxml");
                button_to_fxml_map.put("switch_to_deck_management_button", "/com/socslingo/views/deck_management.fxml");
                button_to_fxml_map.put("switch_to_profile_button", "/com/socslingo/views/profile.fxml");
                button_to_fxml_map.put("switch_to_flashcard_management_button","/com/socslingo/views/flashcard_management.fxml");
                button_to_fxml_map.put("switch_to_create_flashcard_page_button", "/com/socslingo/views/create_flashcard.fxml");
                button_to_fxml_map.put("switch_to_create_flashcard_list_page_button", "/com/socslingo/views/create_flashcard_list.fxml");
                button_to_fxml_map.put("switch_to_main_flashcard_page_button", "/com/socslingo/views/main_flashcard.fxml");
                button_to_fxml_map.put("switch_to_login_fxml_button", "/com/socslingo/views/login.fxml");
                button_to_fxml_map.put("switch_to_registration_page_button", "/com/socslingo/views/registration.fxml");
                button_to_fxml_map.put("switch_to_registration_fxml_button", "/com/socslingo/views/registration.fxml");
                button_to_fxml_map.put("switch_to_activity_main_button", "/com/socslingo/views/activity_main.fxml");

                sidebar_buttons = Arrays.asList(
                        sidebar_switch_to_home_button,
                        sidebar_switch_to_main_flashcard_button,
                        switch_to_activity_main_button,
                        switch_to_deck_management_button,
                        switch_to_flashcard_management_button,
                        switch_to_profile_button,
                        more_button);

                setActiveButton(sidebar_switch_to_home_button);

                setupContextMenu();

                toggle_sidebar_button.setVisible(false);

                sidebar_context_menu = new ContextMenu();
                MenuItem hide_sidebar_item = new MenuItem("Hide Sidebar");
                hide_sidebar_item.setOnAction(e -> handleHideSidebar());
                sidebar_context_menu.getItems().add(hide_sidebar_item);

                loadRightSidebar();
                logger.info("PrimaryController initialized successfully");

                status_bar.prefWidthProperty().bind(stack_pane.widthProperty());
                applyAnimatedGlowEffect();

            } catch (Exception e) {
                logger.error("Exception during PrimaryController initialization", e);
                showAlert(Alert.AlertType.ERROR, "Failed to initialize the application.");
            }
        }

        /**
         * Sets the active button by updating its CSS class and resetting others.
         * @param activeButton The button to set as active. Pass null to reset all.
         */
        public void setActiveButton(Button active_button) {
            logger.debug("Setting active button: {}", active_button != null ? active_button.getId() : "None");
            for (Button button : sidebar_buttons) {
                if (active_button != null && button.equals(active_button)) {
                    button.getStyleClass().removeAll("left-sidebar-button", "left-sidebar-button-selected",
                            "left-sidebar-button-active");
                    if (!button.getStyleClass().contains("left-sidebar-button-active")) {
                        button.getStyleClass().add("left-sidebar-button-active");
                        logger.debug("Button '{}' set to active", button.getId());
                    }
                } else {
                    boolean was_active = button.getStyleClass().removeAll("left-sidebar-button-active",
                            "left-sidebar-button-selected");
                    if (was_active) {
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
        public void switchContent(String fxml_path, Consumer<FXMLLoader> controller_consumer) throws IOException {
            logger.debug("Switching content to: {}", fxml_path);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
            loader.setControllerFactory(ControllerManager.getInstance());
            Node node = loader.load();

            if (controller_consumer != null) {
                controller_consumer.accept(loader);
            }

            content_area.getChildren().clear();
            logger.debug("Cleared existing content in content_area");

            content_area.getChildren().add(node);
            logger.debug("Added new content to content_area");

            FadeTransition fade_in = new FadeTransition(Duration.millis(500), node);
            fade_in.setFromValue(0);
            fade_in.setToValue(1);
            fade_in.play();
            logger.debug("Applied fade-in transition to new content");

            if (shouldShowRightSidebar(fxml_path)) {
                loadRightSidebar();
            } else {
                removeRightSidebar();
            }
        }

        public void switchContent(String fxml_path) throws IOException {
            switchContent(fxml_path, null);
        }

        private boolean shouldShowRightSidebar(String fxml_path) {
            List<String> fxml_with_sidebar = Arrays.asList(
                    "/com/socslingo/views/home.fxml",
                    "/com/socslingo/views/deck_management.fxml",
                    "/com/socslingo/views/deck_preview.fxml");
            boolean should_show = fxml_with_sidebar.contains(fxml_path);
            logger.debug("shouldShowRightSidebar check for '{}': {}", fxml_path, should_show);
            return should_show;
        }

        private void loadRightSidebar() throws IOException {
            logger.debug("Loading right sidebar");
            if (right_sidebar == null) {
                FXMLLoader sidebar_loader = new FXMLLoader(
                        getClass().getResource("/com/socslingo/views/right_sidebar.fxml"));
                sidebar_loader.setControllerFactory(ControllerManager.getInstance());
                right_sidebar = sidebar_loader.load();
            }
            root_pane.setRight(right_sidebar);
            is_right_sidebar_visible = true;
            logger.info("Right sidebar loaded");
        }

        private void removeRightSidebar() {
            logger.debug("Removing right sidebar");
            root_pane.setRight(null);
            is_right_sidebar_visible = false;
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

            more_context_menu.hide();
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
            more_context_menu.hide();
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
            more_context_menu.hide();
        }

        private void setupContextMenu() {
            logger.debug("Setting up ContextMenu for 'More' button");
            more_context_menu.hide();
            final boolean[] is_context_menu_visible = { false };

            more_button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                if (!is_context_menu_visible[0]) {
                    logger.debug("Mouse entered 'More' button");
                    Bounds more_button_bounds = more_button.localToScreen(more_button.getBoundsInLocal());
                    more_context_menu.show(more_button, more_button_bounds.getMaxX() + 10, more_button_bounds.getMinY());
                    is_context_menu_visible[0] = true;
                    logger.info("'More' ContextMenu displayed");
                }
            });

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                more_context_menu.hide();
                is_context_menu_visible[0] = false;
                logger.debug("'More' ContextMenu hidden after delay");
            });

            more_button.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                logger.debug("Mouse exited 'More' button");
                pause.playFromStart();
            });

            more_context_menu.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                logger.debug("Mouse entered ContextMenu");
                pause.stop();
            });

            more_context_menu.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
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
        private void showAlert(Alert.AlertType alert_type, String message) {
            logger.debug("Displaying alert of type '{}' with message: {}", alert_type, message);
            Alert alert = new Alert(alert_type);
            alert.setContentText(message);
            alert.showAndWait();
            logger.debug("Alert displayed");
        }

        public void switchContentNode(Node node) {
            content_area.getChildren().clear();
            logger.debug("Cleared existing content in content_area");

            content_area.getChildren().add(node);
            logger.debug("Added new content to content_area");

            FadeTransition fade_in = new FadeTransition(Duration.millis(500), node);
            fade_in.setFromValue(0);
            fade_in.setToValue(1);
            fade_in.play();
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
                sidebar_context_menu.show(left_sidebar, event.getScreenX(), event.getScreenY());
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
            if (is_sidebar_visible) {
                logger.info("Hiding sidebar");
                TranslateTransition hide_sidebar = new TranslateTransition(Duration.millis(300), left_sidebar);
                hide_sidebar.setToX(-left_sidebar.getWidth());
                hide_sidebar.setInterpolator(Interpolator.EASE_IN);
                hide_sidebar.play();

                TranslateTransition move_button = new TranslateTransition(Duration.millis(300), toggle_sidebar_button);
                move_button.setToX(0);
                move_button.setInterpolator(Interpolator.EASE_IN);
                move_button.play();

                hide_sidebar.setOnFinished(event -> {
                    left_sidebar.setVisible(false);
                    left_sidebar.setManaged(false);
                    left_sidebar.setTranslateX(0);
                    logger.debug("Sidebar hidden");

                    toggle_sidebar_button.setVisible(true);
                    toggle_sidebar_button.setTranslateX(0);
                    toggle_sidebar_button.setOpacity(1);
                    logger.debug("Toggle button made visible");
                });

                is_sidebar_visible = false;
            }
        }

        private void showSidebar() {
            if (!is_sidebar_visible) {
                logger.info("Showing sidebar");
                left_sidebar.setVisible(true);
                left_sidebar.setManaged(true);
                left_sidebar.setTranslateX(-left_sidebar.getWidth());

                toggle_sidebar_button.setTranslateX(0);
                toggle_sidebar_button.setOpacity(1);

                TranslateTransition show_sidebar = new TranslateTransition(Duration.millis(300), left_sidebar);
                show_sidebar.setFromX(-left_sidebar.getWidth());
                show_sidebar.setToX(0);
                show_sidebar.setInterpolator(Interpolator.EASE_OUT);

                TranslateTransition move_button = new TranslateTransition(Duration.millis(300), toggle_sidebar_button);
                move_button.setFromX(0);
                move_button.setToX(left_sidebar.getWidth());
                move_button.setInterpolator(Interpolator.EASE_OUT);

                ParallelTransition parallel_transition = new ParallelTransition(show_sidebar, move_button);
                parallel_transition.play();

                parallel_transition.setOnFinished(event -> {
                    FadeTransition fade_out = new FadeTransition(Duration.millis(200), toggle_sidebar_button);
                    fade_out.setFromValue(1.0);
                    fade_out.setToValue(0.0);
                    fade_out.setOnFinished(e -> {
                        toggle_sidebar_button.setVisible(false);
                        toggle_sidebar_button.setTranslateX(0);
                        toggle_sidebar_button.setOpacity(1);
                        logger.debug("Toggle button faded out and made invisible");
                    });
                    fade_out.play();
                    logger.debug("Sidebar shown");
                });

                is_sidebar_visible = true;
            }
        }

        public Button getDeckManagementButton() {
            return switch_to_deck_management_button;
        }

        @FXML
        private Button toggle_right_sidebar_button;

        public void showRightSidebar() {
            if (right_sidebar != null && !is_right_sidebar_visible) {
                logger.info("Showing right sidebar");

                double sidebar_width = right_sidebar.getWidth();
                if (sidebar_width <= 0) {
                    sidebar_width = right_sidebar.prefWidth(-1);
                }

                root_pane.setRight(right_sidebar);
                right_sidebar.setTranslateX(sidebar_width);

                toggle_right_sidebar_button.setTranslateX(0);
                toggle_right_sidebar_button.setOpacity(1);
                toggle_right_sidebar_button.setVisible(true);

                TranslateTransition show_sidebar = new TranslateTransition(Duration.millis(300), right_sidebar);
                show_sidebar.setFromX(sidebar_width);
                show_sidebar.setToX(0);
                show_sidebar.setInterpolator(Interpolator.EASE_OUT);

                TranslateTransition move_button = new TranslateTransition(Duration.millis(300), toggle_right_sidebar_button);
                move_button.setFromX(0);
                move_button.setToX(-sidebar_width);
                move_button.setInterpolator(Interpolator.EASE_OUT);

                ParallelTransition parallel_transition = new ParallelTransition(show_sidebar, move_button);
                parallel_transition.play();

                parallel_transition.setOnFinished(event -> {
                    is_right_sidebar_visible = true;
                    logger.debug("Right sidebar shown");

                    FadeTransition fade_out = new FadeTransition(Duration.millis(200), toggle_right_sidebar_button);
                    fade_out.setFromValue(1.0);
                    fade_out.setToValue(0.0);
                    fade_out.setOnFinished(e -> toggle_right_sidebar_button.setVisible(false));
                    fade_out.play();
                });
            }
        }

        public void hideRightSidebar() {
            if (right_sidebar != null && is_right_sidebar_visible) {
                logger.info("Hiding right sidebar");

                double sidebar_width = right_sidebar.getWidth();
                if (sidebar_width <= 0) {
                    sidebar_width = right_sidebar.prefWidth(-1);
                }

                TranslateTransition hide_sidebar = new TranslateTransition(Duration.millis(300), right_sidebar);
                hide_sidebar.setFromX(0);
                hide_sidebar.setToX(sidebar_width);
                hide_sidebar.setInterpolator(Interpolator.EASE_IN);

                TranslateTransition move_button = new TranslateTransition(Duration.millis(300), toggle_right_sidebar_button);
                move_button.setFromX(-sidebar_width);
                move_button.setToX(0);
                move_button.setInterpolator(Interpolator.EASE_IN);

                ParallelTransition parallel_transition = new ParallelTransition(hide_sidebar, move_button);
                parallel_transition.play();

                parallel_transition.setOnFinished(event -> {
                    root_pane.setRight(null);
                    is_right_sidebar_visible = false;
                    logger.debug("Right sidebar hidden");

                    FadeTransition fade_in = new FadeTransition(Duration.millis(200), toggle_right_sidebar_button);
                    fade_in.setFromValue(0.0);
                    fade_in.setToValue(1.0);
                    fade_in.setOnFinished(e -> toggle_right_sidebar_button.setVisible(true));
                    fade_in.play();
                });
            }
        }

        @FXML
        private void handleToggleRightSidebar() {
            logger.info("Toggle right sidebar button clicked");
            if (is_right_sidebar_visible) {
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
            status_bar.setEffect(glow);

            FadeTransition fade_in = new FadeTransition(Duration.seconds(1), status_bar);
            fade_in.setFromValue(1.0);
            fade_in.setToValue(0.8);
            fade_in.setInterpolator(Interpolator.EASE_IN);

            FadeTransition fade_out = new FadeTransition(Duration.seconds(1), status_bar);
            fade_out.setFromValue(0.8);
            fade_out.setToValue(1.0);
            fade_out.setInterpolator(Interpolator.EASE_OUT);

            SequentialTransition pulsate = new SequentialTransition(fade_in, fade_out);
            pulsate.setCycleCount(Animation.INDEFINITE);
            pulsate.play();

            logger.info("Animated glow effect applied to status bar");
        }

        @FXML
        private void handleButtonAction(ActionEvent event) {
            Button clicked_button = (Button) event.getSource();
            String fxml_path = button_to_fxml_map.get(clicked_button.getId());
            if (fxml_path != null) {
                logger.info("Button '{}' clicked. Loading FXML: {}", clicked_button.getId(), fxml_path);
                try {
                    if (fxml_path.equals("/com/socslingo/views/deck_management.fxml")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_path));
                        loader.setControllerFactory(ControllerManager.getInstance());
                        Node new_content = loader.load();

                        switchContentNode(new_content);
                        setActiveButton(clicked_button);

                    } else {
                        switchContent(fxml_path);
                        setActiveButton(clicked_button);
                    }

                    applyWaveAnimation();

                } catch (IOException e) {
                    logger.error("Failed to load FXML: {}", fxml_path, e);
                    showAlert(Alert.AlertType.ERROR, "Failed to load the requested page.");
                }
            } else {
                logger.warn("No FXML mapping found for button ID: {}", clicked_button.getId());
                showAlert(Alert.AlertType.WARNING, "No action defined for this button.");
            }
        }

        private void manageToggleButtonsVisibility() {
            if (!is_sidebar_visible) {
                toggle_sidebar_button.setVisible(true);
            } else {
                toggle_sidebar_button.setVisible(false);
            }

            if (!is_right_sidebar_visible) {
                toggle_right_sidebar_button.setVisible(true);
            } else {
                toggle_right_sidebar_button.setVisible(false);
            }
        }

        private final Color light_orange = Color.rgb(255, 214, 129);
        private final Color light_blue = Color.rgb(173, 216, 230);

        private void applyWaveAnimation() {
            HBox gradient_box = new HBox();
            gradient_box.setPrefWidth(status_bar.getWidth() * 1.5);

            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, light_orange),
                    new Stop(0.5, light_blue),
                    new Stop(1, light_orange));

            gradient_box.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, null)));

            status_bar.getChildren().add(gradient_box);

            TranslateTransition bounce_transition = new TranslateTransition(Duration.seconds(2), gradient_box);
            bounce_transition.setFromX(-gradient_box.getPrefWidth() / 2);
            bounce_transition.setToX(status_bar.getWidth() / 2);
            bounce_transition.setAutoReverse(true);
            bounce_transition.setCycleCount(2);

            TranslateTransition off_screen_transition = new TranslateTransition(Duration.seconds(2), gradient_box);
            off_screen_transition.setFromX(status_bar.getWidth() / 2);
            off_screen_transition.setToX(status_bar.getWidth());

            SequentialTransition full_animation = new SequentialTransition(bounce_transition, off_screen_transition);

            full_animation.play();
        }

    }
