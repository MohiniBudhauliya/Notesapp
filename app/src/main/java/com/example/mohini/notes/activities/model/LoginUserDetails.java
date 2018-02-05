package com.example.mohini.notes.activities.model;

import com.example.mohini.notes.activities.database.Appdatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by mohini on 19/1/18.
 */
//model class to store loggedin user email and token
    @Table(database = Appdatabase.class)
public class LoginUserDetails extends BaseModel {
    @PrimaryKey
    @Column
    @SerializedName("email")
    @Expose
    String email;

    @Column
    @SerializedName("token")
    @Expose
    String token;
    @Column
    @SerializedName("fcm_token")
    @Expose
    String fcm_token;

    public LoginUserDetails(String email, String token) {
        this.email = email;
        this.token = token;
    }
    public LoginUserDetails(String email, String token,String fcm_token) {
        this.email = email;
        this.token = token;
        this.fcm_token=fcm_token;
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

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public LoginUserDetails()
    {

    }

}
