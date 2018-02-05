package com.example.mohini.notes.activities.model;

import com.example.mohini.notes.activities.database.Appdatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by mohini on 15/1/18.
 */
@Table(database = Appdatabase.class)
public class NoteModel {
    //model class to send details of notes to server
    @Column
    @SerializedName("emails")
    @Expose
    String email;
    @Column
    @SerializedName("title")
    @Expose
    String title;
    @Column
    @SerializedName("note")
    @Expose
    String note;
    @Column
    @SerializedName("color")
    @Expose
    String color;
    @Column
    @SerializedName("tag")
    @Expose
    String tag;
    @Column
    @PrimaryKey
    @SerializedName("id")
    @Expose
    String id;
    public NoteModel()
    {
    }
    public NoteModel(String id,String email, String title, String note, String color, String tag) {
        this.title = title;
        this.note = note;
        this.email = email;
        this.tag=tag;
        this.color=color;
        this.id=id;
    }

    public NoteModel(String email, String note) {
        this.note = note;
        this.email = email;
    }
//    public NoteModel(String id,String note, String title,String tag, String color) {
//        this.id=id;
//        this.note=note;
//        this.color=color;
//        this.tag=tag;
//        this.title=title;
//    }

public String getId()
{
    return id;
}
public void setId(String id)
{
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