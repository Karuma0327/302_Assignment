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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.function.Consumer;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import javax.imageio.stream.ImageOutputStream;

public class ProfileController implements Initializable {

    @FXML
    private Label username_label;

    @FXML
    private Label actual_name_label;

    @FXML
    private Label joined_date_label;

    @FXML
    private ImageView profile_banner_image_view;

    private UserService user_service;

    private Image profile_banner_image;

    @FXML
    private StackPane profile_banner_stack_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ProfileController initialized.");
        user_service = new UserService(new UserDataAccess(DatabaseManager.getInstance()));

        User current_user = SessionManager.getInstance().getCurrentUser();

        if (current_user != null) {
            username_label.setText(current_user.getUsername());
            actual_name_label.setText(current_user.getActualName());
            joined_date_label.setText("Joined " + current_user.getCreatedDate());

            profile_banner_image = ImageCache.getInstance().getBannerImage(current_user.getId());
            Rectangle clip = new Rectangle(profile_banner_image_view.getFitWidth(), profile_banner_image_view.getFitHeight());
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            profile_banner_image_view.setClip(clip);
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

        // Removed viewport settings
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

                    // Step 2: Save the new profile banner image with resizing
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

                        if (!success) {
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

    /**
     * Resizes and crops the given BufferedImage to exactly fit the specified width and height.
     *
     * @param originalImage The original BufferedImage.
     * @param targetWidth   The target width.
     * @param targetHeight  The target height.
     * @param hasAlpha      Whether the target format supports alpha transparency.
     * @return The resized and cropped BufferedImage.
     */
    private BufferedImage resizeAndCropImage(BufferedImage originalImage, int targetWidth, int targetHeight, boolean hasAlpha) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double ratio = (double) targetWidth / originalWidth;
        int scaledHeight = (int) (originalHeight * ratio);

        // Choose image type based on alpha support
        int imageType = hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

        // Resize the image to targetWidth while maintaining aspect ratio
        BufferedImage resizedImage = new BufferedImage(targetWidth, scaledHeight, imageType);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, targetWidth, scaledHeight, null);
        g.dispose();

        // Crop the image if necessary
        if (scaledHeight > targetHeight) {
            BufferedImage croppedImage = resizedImage.getSubimage(0, (scaledHeight - targetHeight) / 2, targetWidth, targetHeight);
            return croppedImage;
        } else {
            // If scaled height is less than targetHeight, center the image vertically
            BufferedImage finalImage = new BufferedImage(targetWidth, targetHeight, imageType);
            Graphics2D gFinal = finalImage.createGraphics();
            gFinal.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int y = (targetHeight - scaledHeight) / 2;
            gFinal.drawImage(resizedImage, 0, y, null);
            gFinal.dispose();
            return finalImage;
        }
    }

    /**
     * Saves the profile banner image after resizing and cropping it to exactly 700x400 pixels.
     *
     * @param selected_file The original uploaded file.
     * @param user_id       The ID of the current user.
     * @return The relative path to the saved image, or null if failed.
     */
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

            String file_extension = get_file_extension(selected_file.getName()).toLowerCase();
            // Ensure the file extension is suitable for ImageIO
            if (!file_extension.equals(".png") && !file_extension.equals(".jpg") && 
                !file_extension.equals(".jpeg") && !file_extension.equals(".gif")) {
                System.err.println("Unsupported file extension: " + file_extension);
                return null;
            }

            String formatName = file_extension.substring(1); // Remove the leading dot

            String new_file_name = "banner_user_" + user_id + "_" + System.currentTimeMillis() + file_extension;
            File dest_file = new File(dest_dir, new_file_name);

            System.out.println("Destination file path: " + dest_file.getAbsolutePath());

            // Load original image
            BufferedImage originalImage = ImageIO.read(selected_file);
            if (originalImage == null) {
                System.err.println("Failed to read the image file.");
                return null;
            }

            // Determine if the target format supports alpha transparency
            boolean hasAlpha = formatName.equals("png") || formatName.equals("gif");

            // Resize and crop image to 700x400
            BufferedImage finalImage = resizeAndCropImage(originalImage, 720, 405, hasAlpha);

            // If saving as JPEG, ensure the image does not have an alpha channel
            if (!hasAlpha) {
                BufferedImage rgbImage = new BufferedImage(finalImage.getWidth(), finalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = rgbImage.createGraphics();
                // Fill the background with white to replace transparency
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setColor(java.awt.Color.WHITE);
                g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
                g.drawImage(finalImage, 0, 0, null);
                g.dispose();
                finalImage = rgbImage;
            }

            boolean writeSuccess;

            if (formatName.equals("jpg") || formatName.equals("jpeg")) {
                // Use ImageWriter for JPEG to set compression quality
                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(1.0f); // Maximum quality

                try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(dest_file)) {
                    jpgWriter.setOutput(outputStream);
                    IIOImage outputImage = new IIOImage(finalImage, null, null);
                    jpgWriter.write(null, outputImage, jpgWriteParam);
                    writeSuccess = true;
                } finally {
                    jpgWriter.dispose();
                }
            } else {
                // For PNG and GIF, use default ImageIO write
                writeSuccess = ImageIO.write(finalImage, formatName, dest_file);
            }

            if (writeSuccess && dest_file.exists()) {
                System.out.println("Resized and cropped image saved successfully to: " + dest_file.getAbsolutePath());
                return "profile_banners/" + new_file_name;
            } else {
                System.err.println("Image save failed.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error processing the image file: " + e.getMessage());
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

                // **Updated Line:** Load image without specifying width and height
                Image image = new Image(image_file.toURI().toString());

                if (image.isError()) {
                    System.err.println("Error loading image: " + image.getException());
                    return;
                }

                profile_banner_image_view.setImage(image);
                // Removed viewport settings
                profile_banner_image_view.setStyle("");
                System.out.println("Profile banner updated successfully with dynamic sizing.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error updating the profile banner: " + e.getMessage());
            }
        }
    }

    private void set_profile_banner(Image image) {
        if (image != null) {
            profile_banner_image_view.setImage(image);
    
            // Set dimensions for the user-uploaded banner
            profile_banner_image_view.setFitWidth(684);
            profile_banner_image_view.setFitHeight(384);
            profile_banner_stack_pane.setStyle("-fx-background-color: transparent;");
            profile_banner_image_view.setTranslateY(0);
            profile_banner_image_view.setSmooth(true);
            System.out.println("Profile banner set successfully with dynamic sizing.");
        }
    }

    private void set_profile_banner_placeholder() {
        profile_banner_image_view.setImage(null);
        profile_banner_image_view.setStyle("-fx-background-color: grey;");
        System.out.println("Profile banner placeholder set.");
    }

    private void set_default_banner_image() {
        try (InputStream is = getClass().getResourceAsStream("/com/socslingo/images/profile_default.png")) {
            if (is == null) {
                System.err.println("Failed to locate default banner image at /com/socslingo/images/profile_default.png");
                return;
            }
            Image defaultImage = new Image(is);
            profile_banner_image_view.setImage(defaultImage);
            profile_banner_image_view.setFitWidth(600); 
            profile_banner_image_view.setFitHeight(250); 
            profile_banner_image_view.setTranslateY(25);
            profile_banner_stack_pane.setStyle("-fx-background-color: #ddf4ff;");
            System.out.println("Default profile banner set.");
        } catch (Exception e) {
            System.err.println("Failed to load default banner image.");
            e.printStackTrace();
        }
    }


    
}
