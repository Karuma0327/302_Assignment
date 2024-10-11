package com.socslingo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import com.socslingo.cache.ImageCache;
import com.socslingo.models.User;
import com.socslingo.managers.SessionManager;
import com.socslingo.services.UserService;
import com.socslingo.dataAccess.UserDataAccess;
import com.socslingo.managers.DatabaseManager;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class ProfileController implements Initializable {

    @FXML
    private Label username_label;

    @FXML
    private Label joined_date_label;

    @FXML
    private ImageView profile_banner_image_view;

    private UserService user_service;

    private Image profile_banner_image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ProfileController initialized.");
        user_service = new UserService(new UserDataAccess(DatabaseManager.getInstance()));

        User current_user = SessionManager.getInstance().getCurrentUser();

        if (current_user != null) {
            username_label.setText(current_user.getUsername());
            joined_date_label.setText("Joined " + current_user.getCreatedDate());

            profile_banner_image = ImageCache.getInstance().getBannerImage(current_user.getId());

            if (profile_banner_image != null) {
                set_profile_banner(profile_banner_image);
            } else {
                set_profile_banner_placeholder();
                System.out.println("Banner image not found in cache for user ID: " + current_user.getId());
                set_default_banner_image();
            }
        } else {
            username_label.setText("Guest");
            joined_date_label.setText("");
            set_profile_banner_placeholder();
        }
    }

    @FXML
    private void handle_profile_banner_click() {
        System.out.println("handle_profile_banner_click() method called.");
        try {
            FileChooser file_chooser = new FileChooser();
            file_chooser.setTitle("Select Profile Banner Image");
            file_chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selected_file = file_chooser.showOpenDialog(profile_banner_image_view.getScene().getWindow());

            if (selected_file != null) {
                System.out.println("Selected file: " + selected_file.getAbsolutePath());
                User current_user = SessionManager.getInstance().getCurrentUser();

                if (current_user != null) {
                    // Step 1: Delete the old profile banner image
                    String oldImagePath = current_user.getProfileBannerPath();
                    if (oldImagePath != null && !oldImagePath.isEmpty()) {
                        File oldImageFile = new File(System.getProperty("user.dir"), oldImagePath);
                        if (oldImageFile.exists()) {
                            boolean deleted = oldImageFile.delete();
                            if (deleted) {
                                System.out.println("Deleted old profile banner image: " + oldImageFile.getAbsolutePath());
                                // Remove the old image from the cache
                                ImageCache.getInstance().removeBannerImage(current_user.getId());
                            } else {
                                System.err.println("Failed to delete old profile banner image: " + oldImageFile.getAbsolutePath());
                                // Optionally, inform the user about the failure
                            }
                        } else {
                            System.out.println("Old profile banner image file does not exist: " + oldImageFile.getAbsolutePath());
                        }
                    }

                    // Step 2: Save the new profile banner image
                    String image_path = save_profile_banner_image(selected_file, current_user.getId());

                    if (image_path != null) {
                        current_user.setProfileBannerPath(image_path);

                        update_profile_banner(image_path);

                        // Preload and cache the new image
                        ImageCache.getInstance().preloadBannerImage(current_user.getId(), image_path, new Consumer<Image>() {
                            @Override
                            public void accept(Image image) {
                                if (image != null) {
                                    System.out.println("New banner image preloaded successfully.");
                                    javafx.application.Platform.runLater(() -> {
                                        set_profile_banner(image);
                                    });
                                }
                            }
                        });

                        // Update the database with the new banner path
                        boolean success = user_service.updateUserProfileBanner(current_user.getId(), image_path);

                        if (success) {
                            System.out.println("Profile banner updated successfully in database.");
                        } else {
                            System.err.println("Failed to update profile banner in the database.");
                            // Optionally, revert changes or notify the user
                        }
                    }
                } else {
                    System.err.println("Current user is null.");
                }
            } else {
                System.err.println("No file selected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred while opening the FileChooser: " + e.getMessage());
        }
    }

    private void set_profile_banner(Image image) {
        if (image != null) {
            profile_banner_image_view.setImage(image);
            profile_banner_image_view.setStyle("");
            System.out.println("Profile banner set successfully.");
        }
    }

    private void set_profile_banner_placeholder() {
        profile_banner_image_view.setImage(null);
        profile_banner_image_view.setStyle("-fx-background-color: grey;");
        System.out.println("Profile banner placeholder set.");
    }

    private void set_default_banner_image() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/socslingo/images/default_banner.png"));
            profile_banner_image_view.setImage(defaultImage);
            profile_banner_image_view.setStyle("");
            System.out.println("Default profile banner set.");
        } catch (Exception e) {
            System.err.println("Failed to load default banner image.");
            e.printStackTrace();
        }
    }

    private String save_profile_banner_image(File selected_file, int user_id) {
        try {
            String current_dir = System.getProperty("user.dir");
            File dest_dir = new File(current_dir, "profile_banners");

            if (!dest_dir.exists()) {
                boolean dir_created = dest_dir.mkdirs();
                System.out.println("Destination directory created: " + dir_created);
            } else {
                System.out.println("Destination directory already exists.");
            }

            String file_extension = get_file_extension(selected_file.getName());
            String new_file_name = "banner_user_" + user_id + "_" + System.currentTimeMillis() + file_extension;
            File dest_file = new File(dest_dir, new_file_name);

            System.out.println("Destination file path: " + dest_file.getAbsolutePath());

            Files.copy(selected_file.toPath(), dest_file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (dest_file.exists()) {
                System.out.println("File copied successfully to: " + dest_file.getAbsolutePath());
                return "profile_banners/" + new_file_name;
            } else {
                System.err.println("File copy failed.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error copying the image file: " + e.getMessage());
            return null;
        }
    }

    private String get_file_extension(String file_name) {
        int dot_index = file_name.lastIndexOf('.');
        if (dot_index > 0 && dot_index < file_name.length() - 1) {
            return file_name.substring(dot_index);
        }
        return "";
    }

    private void preload_and_set_banner_image(User user) {
        String image_path = user.getProfileBannerPath();
        if (image_path != null && !image_path.isEmpty()) {
            ImageCache.getInstance().preloadBannerImage(user.getId(), image_path, new Consumer<Image>() {
                @Override
                public void accept(Image image) {
                    if (image != null) {
                        javafx.application.Platform.runLater(() -> {
                            set_profile_banner(image);
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> {
                            set_profile_banner_placeholder();
                        });
                    }
                }
            });
        }
    }

    private void update_profile_banner(String image_path) {
        if (image_path != null) {
            try {
                String current_dir = System.getProperty("user.dir");
                File image_file = new File(current_dir, image_path);
                System.out.println("Updating profile banner with image: " + image_file.getAbsolutePath());

                if (!image_file.exists()) {
                    System.err.println("Image file does not exist: " + image_file.getAbsolutePath());
                    return;
                }

                Image image = new Image(image_file.toURI().toString(), 700, 400, true, true);

                if (image.isError()) {
                    System.err.println("Error loading image: " + image.getException());
                    return;
                }

                profile_banner_image_view.setImage(image);
                profile_banner_image_view.setStyle("");
                System.out.println("Profile banner updated successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error updating the profile banner: " + e.getMessage());
            }
        }
    }
}
