package com.socslingo.services;

import com.socslingo.dao.UserDAO;
import com.socslingo.models.User;

/**
 * Service class responsible for managing user-related operations.
 * <p>
 * This class provides methods to register new users and validate existing users.
 * It acts as an intermediary between the controllers and the data access layer (DAO),
 * encapsulating the business logic associated with user management.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class UserService {
    private UserDAO userDAO;

    /**
     * Constructs a new {@code UserService} with the specified {@code UserDAO}.
     * 
     * @param userDAO the DAO responsible for user data operations
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user with the provided username, email, and hashed password.
     * <p>
     * This method first checks if the username is already taken. If not, it proceeds
     * to insert the new user into the database.
     * </p>
     * 
     * @param username       the desired username for the new user
     * @param email          the email address of the new user
     * @param hashedPassword the hashed password of the new user
     * @return {@code true} if the registration was successful; {@code false} otherwise
     */
    public boolean registerUser(String username, String email, String hashedPassword) {
        // Business logic to check if username already exists
        if (userDAO.isUsernameTaken(username)) {
            System.out.println("Username already taken");
            return false;
        }

        return userDAO.insertUser(username, email, hashedPassword);
    }

    /**
     * Validates a user's credentials by checking the provided username and hashed password.
     * 
     * @param username       the username of the user attempting to log in
     * @param hashedPassword the hashed password provided by the user
     * @return a {@code User} object if the credentials are valid; {@code null} otherwise
     */
    public User validateUser(String username, String hashedPassword) {
        return userDAO.getUserByUsernameAndPassword(username, hashedPassword);
    }
}
