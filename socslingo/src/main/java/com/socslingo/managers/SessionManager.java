package com.socslingo.managers;

import com.socslingo.models.User;

/**
 * Singleton class responsible for managing the current user session.
 * <p>
 * This class handles the storage and retrieval of the currently logged-in user,
 * ensuring that only one instance exists throughout the application lifecycle.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class SessionManager {
    /**
     * The singleton instance of {@code SessionManager}.
     */
    private static SessionManager instance;
    
    /**
     * The currently logged-in user.
     */
    private User currentUser;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private SessionManager() {
    }

    /**
     * Retrieves the singleton instance of {@code SessionManager}.
     * <p>
     * If the instance does not exist, it is created. Otherwise, the existing instance is returned.
     * </p>
     * 
     * @return the singleton instance of {@code SessionManager}
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the current user for the session.
     * 
     * @param user the {@code User} object representing the currently logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Retrieves the current user of the session.
     * 
     * @return the {@code User} object representing the currently logged-in user, or {@code null} if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Retrieves the ID of the current user.
     * 
     * @return the ID of the currently logged-in user, or {@code -1} if no user is logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
