package com.socslingo.services;

import com.socslingo.dataAccess.UserDataAccess;
import com.socslingo.models.User;

public class UserService {

    private UserDataAccess userDataAccess;

    public UserService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public boolean registerUser(String username, String email, String hashed_password, String actual_name) {
        if (userDataAccess.isUsernameTaken(username)) {
            System.out.println("Username already taken");
            return false;
        }

        return userDataAccess.insertUser(username, email, hashed_password, actual_name);
    }

    public User validateUser(String username, String hashed_password) {
        return userDataAccess.getUserByUsernameAndPassword(username, hashed_password);
    }

    // Existing method to update the user's profile banner
    public boolean updateUserProfileBanner(int user_id, String profile_banner_path) {
        return userDataAccess.updateUserProfileBanner(user_id, profile_banner_path);
    }

    /**
     * Updates user details such as actual name, email, and password.
     */
    public boolean updateUser(User user) {
        return userDataAccess.updateUser(user);
    }

    /**
     * Deletes a user account based on user ID.
     */
    public boolean deleteUser(int userId) {
        return userDataAccess.deleteUser(userId);
    }
}