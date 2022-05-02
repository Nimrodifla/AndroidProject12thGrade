package com.example.finalproject;

public class User {
    private String password;
    private String username;

    public User() {
    }

    public User(String u, String p)
    {
        this.password = p;
        this.username = u;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
