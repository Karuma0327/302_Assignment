package com.socslingo.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String created_date;
    private String profile_banner_path;
    private String actual_name;
    private String email; // Added email field

    public User(int id, String username, String password, String created_date) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.created_date = created_date;
        this.profile_banner_path = null; // Initialize as null
    }
    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getActualName(){
        return actual_name;
    }

    public void setActualName(String actual_name){
        this.actual_name = actual_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(String created_date) {
        this.created_date = created_date;
    }

    public String getProfileBannerPath() {
        return profile_banner_path;
    }

    public void setProfileBannerPath(String profile_banner_path) {
        this.profile_banner_path = profile_banner_path;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +       // Include email
                ", actual_name='" + actual_name + '\'' + // Include actual name
                ", password='" + password + '\'' +
                ", created_date='" + created_date + '\'' +
                ", profile_banner_path='" + profile_banner_path + '\'' +
                '}';
    }
}
