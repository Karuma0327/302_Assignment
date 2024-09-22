package com.socslingo.models;

/**
 * Represents a user in the Socslingo application.
 * <p>
 * This class contains user-related information such as ID, username, password, and email.
 * It provides getters and setters for accessing and modifying these fields.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;

    /**
     * Constructs a new {@code User} with the specified ID, username, and password.
     * 
     * @param id       the unique identifier for the user
     * @param username the username of the user
     * @param password the password of the user
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the unique identifier of the user.
     * 
     * @return the user's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * 
     * @param id the new ID for the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the email address of the user.
     * 
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * 
     * @param email the new email address for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the username of the user.
     * 
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the new username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the password of the user.
     * 
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password the new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the user, including ID, username, and password.
     * 
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
