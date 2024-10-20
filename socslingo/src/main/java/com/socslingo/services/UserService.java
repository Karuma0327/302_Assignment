package com.socslingo.services;

import com.socslingo.dataAccess.UserDataAccess;
import com.socslingo.models.User;

public class UserService {

    private UserDataAccess user_data_access;

    public UserService(UserDataAccess user_data_access) {
        this.user_data_access = user_data_access;
    }

    public boolean registerUser(String username, String email, String hashed_password, String actual_name) {
        if (user_data_access.isUsernameTaken(username)) {
            System.out.println("Username already taken");
            return false;
        }

        
        return user_data_access.insertUser(username, email, hashed_password, actual_name);
    }

    public User validateUser(String username, String hashed_password) {
        return user_data_access.getUserByUsernameAndPassword(username, hashed_password);
    }

    // New method to update the user's profile banner
    public boolean updateUserProfileBanner(int user_id, String profile_banner_path) {
        return user_data_access.updateUserProfileBanner(user_id, profile_banner_path);
    }
}
