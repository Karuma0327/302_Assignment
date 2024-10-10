package com.socslingo.services;

import com.socslingo.dataAccess.UserDataAccess;
import com.socslingo.models.User;

public class UserService {

    public UserService() {
    }
    private UserDataAccess userDataAccess;


    public UserService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public boolean registerUser(String username, String email,String hashedPassword) {
        if (userDataAccess.isUsernameTaken(username)) {
            System.out.println("Username already taken");
            return false;
        }

        return userDataAccess.insertUser(username, email, hashedPassword);
    }

    public User validateUser(String username, String hashedPassword) {
        return userDataAccess.getUserByUsernameAndPassword(username, hashedPassword);
    }
}