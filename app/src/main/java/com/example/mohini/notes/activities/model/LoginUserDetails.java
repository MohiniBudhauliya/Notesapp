package com.example.mohini.notes.activities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohini on 19/1/18.
 */

public class LoginUserDetails {

    String uId,username,password,loginstatus;
    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("token")
    @Expose
    String token;

    public LoginUserDetails(String email, String token) {
        this.email = email;
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

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
