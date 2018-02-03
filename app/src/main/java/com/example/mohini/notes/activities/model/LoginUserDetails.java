package com.example.mohini.notes.activities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohini on 19/1/18.
 */
//model class to store loggedin user email and token
public class LoginUserDetails {

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


    public LoginUserDetails()
    {

    }

}
