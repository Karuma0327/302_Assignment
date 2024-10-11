package com.socslingo.cache;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ImageCache {
    private static ImageCache instance = null;
    private Map<Integer, Image> userBannerImages;

    private ImageCache() {
        userBannerImages = new ConcurrentHashMap<>();
        preloadAllBannerImages();
    }

    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    private void preloadAllBannerImages() {
        String profileBannersDirPath = "profile_banners";
        File profileBannersDir = new File(System.getProperty("user.dir"), profileBannersDirPath);

        if (!profileBannersDir.exists() || !profileBannersDir.isDirectory()) {
            System.err.println("Profile banners directory does not exist: " + profileBannersDir.getAbsolutePath());
            return;
        }

        File[] imageFiles = profileBannersDir.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif");
        });

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No profile banner images found in directory: " + profileBannersDir.getAbsolutePath());
            return;
        }

        ImageLoadAllService loadAllService = new ImageLoadAllService(profileBannersDirPath, imageFiles);
        loadAllService.setOnSucceeded(event -> {
            System.out.println("All profile banner images preloaded successfully.");
        });

        loadAllService.setOnFailed(event -> {
            System.err.println("Failed to preload some profile banner images.");
            loadAllService.getException().printStackTrace();
        });

        loadAllService.start();
    }

    public void preloadBannerImage(int userId, String imagePath, Consumer<Image> callback) {
        if (!userBannerImages.containsKey(userId)) {
            ImageLoadService loadService = new ImageLoadService(imagePath, userId, callback);
            loadService.setOnSucceeded(e -> {
                Image image = loadService.getValue();
                if (image != null) {
                    userBannerImages.put(userId, image);
                    System.out.println("Banner image preloaded for user ID: " + userId);
                    if (callback != null) {
                        callback.accept(image);
                    }
                }
            });

            loadService.setOnFailed(e -> {
                System.err.println("Failed to preload banner image for user ID: " + userId);
                loadService.getException().printStackTrace();
                if (callback != null) {
                    callback.accept(null);
                }
            });

            loadService.start();
        } else {
            if (callback != null) {
                callback.accept(userBannerImages.get(userId));
            }
        }
    }

    public Image getBannerImage(int userId) {
        return userBannerImages.get(userId);
    }

    public void updateBannerImage(int userId, Image image) {
        if (image != null) {
            userBannerImages.put(userId, image);
            System.out.println("Banner image updated for user ID: " + userId);
        }
    }

    private class ImageLoadAllService extends Service<Void> {
        private String imageDirPath;
        private File[] imageFiles;

        public ImageLoadAllService(String imageDirPath, File[] imageFiles) {
            this.imageDirPath = imageDirPath;
            this.imageFiles = imageFiles;
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    for (File imageFile : imageFiles) {
                        try {
                            String fileName = imageFile.getName();
                            Integer userId = extractUserIdFromFilename(fileName);
                            if (userId != null) {
                                // Load image at original size
                                Image image = new Image(imageFile.toURI().toString(), 0, 0, true, true);
                                userBannerImages.put(userId, image);
                                System.out.println("Preloaded banner image for user ID: " + userId);
                            } else {
                                System.err.println("Unable to extract user ID from filename: " + fileName);
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + imageFile.getAbsolutePath());
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
        }

        private Integer extractUserIdFromFilename(String fileName) {
            try {
                String[] parts = fileName.split("_");
                if (parts.length >= 3) {
                    return Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                System.err.println("Failed to extract user ID from filename: " + fileName);
            }
            return null;
        }
    }

    private class ImageLoadService extends Service<Image> {
        private String imagePath;
        private int userId;
        private Consumer<Image> callback;

        public ImageLoadService(String imagePath, int userId, Consumer<Image> callback) {
            this.imagePath = imagePath;
            this.userId = userId;
            this.callback = callback;
        }

        @Override
        protected Task<Image> createTask() {
            return new Task<>() {
                @Override
                protected Image call() throws Exception {
                    File imageFile = new File(System.getProperty("user.dir"), imagePath);
                    if (imageFile.exists()) {
                        // Load image at original size
                        return new Image(imageFile.toURI().toString(), 0, 0, true, true);
                    } else {
                        System.err.println("Image file not found: " + imageFile.getAbsolutePath());
                        return null;
                    }
                }
            };
        }
    }

    /**
     * Removes the banner image from the cache for the specified user.
     *
     * @param userId The ID of the user whose banner image should be removed.
     */
    public void removeBannerImage(int userId) {
        if (userBannerImages.remove(userId) != null) {
            System.out.println("Banner image removed from cache for user ID: " + userId);
        } else {
            System.out.println("No banner image found in cache for user ID: " + userId);
        }
    }
}
