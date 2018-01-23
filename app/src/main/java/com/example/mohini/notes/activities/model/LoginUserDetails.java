package com.example.mohini.notes.activities.model;

/**
 * Created by mohini on 19/1/18.
 */

public class LoginUserDetails {

    String uId,username,password,loginstatus;

    public LoginUserDetails(String uId, String username, String password) {
        this.uId = uId;
        this.username = username;
        this.password = password;
    }
    public LoginUserDetails(String uId, String username, String password,String loginstatus) {
        this.uId = uId;
        this.username = username;
        this.password = password;
        this.loginstatus=loginstatus;
    }

    public LoginUserDetails()
    {

    }
    public String getuId() {
        return uId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
