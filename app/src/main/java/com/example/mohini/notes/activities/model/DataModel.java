package com.example.mohini.notes.activities.model;

/**
 * Created by mohini on 15/1/18.
 */

public class DataModel{
    String title,note;
    String id,tag,color;
    public DataModel()
    {
    }
    public DataModel(String id, String title, String note,String tag,String color) {
        this.title = title;
        this.note = note;
        this.id = id;
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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