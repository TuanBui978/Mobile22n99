package com.example.myapplication.model;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String id;
    private String email;
    private String displayName;

    // Constructor
    public User(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    // Constructor without displayName (nullable field)
    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.displayName = null;
    }

    public User(FirebaseUser user) {
        this.id = user.getUid();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}