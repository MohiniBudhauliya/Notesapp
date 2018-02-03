package com.example.mohini.notes.activities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohini on 28/1/18.
 */
//Model for SharedNotes.
public class SharedNotes {

    @SerializedName("sender_email")
    @Expose
    String sender_email;
    @SerializedName("reciever_email")
    @Expose
    String reciever_email;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("note")
    @Expose
    String note;
    @SerializedName("color")
    @Expose
    String color;
    @SerializedName("tag")
    @Expose
    String tag;
    @SerializedName("fcm_token")
    @Expose
    String fcmToken;
    @SerializedName("id")
    @Expose
    String id;


    public SharedNotes(String sender_email, String reciever_email, String title, String note, String color, String tag, String fcmToken, String id ) {
        this.sender_email = sender_email;
        this.reciever_email = reciever_email;
        this.title = title;
        this.note = note;
        this.color = color;
        this.tag = tag;
        this.fcmToken=fcmToken;
        this.id=id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSender_Email() {
        return sender_email;
    }

    public void setSender_Email(String email) {
        this.sender_email = email;
    }

    public String getReciever_email() {
        return reciever_email;
    }

    public void setReciever_email(String reciever_email) {
        this.reciever_email = reciever_email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
