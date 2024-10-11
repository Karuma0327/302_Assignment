package com.socslingo.managers;

import com.socslingo.models.User;


public class SessionManager {
    private static SessionManager instance;
    private User current_user;
    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.current_user = user;
    }

    public User getCurrentUser() {
        return current_user;
    }

    public int getCurrentUserId() {
        return current_user != null ? current_user.getId() : -1;
    }
    
}
