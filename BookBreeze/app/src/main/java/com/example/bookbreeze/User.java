package com.example.bookbreeze;

public class User {

    private String UserId , UserEmail , UserPassword , UserName;

    public User(){}

    public User(String userEmail, String userPassword) {
        UserEmail = userEmail;
        UserPassword = userPassword;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }
}
