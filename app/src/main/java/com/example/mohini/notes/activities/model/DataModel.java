package com.example.mohini.notes.activities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohini on 15/1/18.
 */

public class DataModel{
    //String title,note;
    //String id,tag,color;
    @SerializedName("emails")
    @Expose
    String email;
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
    public DataModel()
    {
    }
    public DataModel(String email, String title, String note, String color, String tag) {
        this.title = title;
        this.note = note;
        this.email = email;
        this.tag=tag;
        this.color=color;
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}