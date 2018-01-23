package com.example.mohini.notes.activities.model;

/**
 * Created by mohini on 15/1/18.
 */

public class DataModel{
    String title,note;
    String id,priority;
    public DataModel()
    {
    }
    public DataModel(String id, String title, String note,String priority) {
        this.title = title;
        this.note = note;
        this.id = id;
        this.priority=priority;
    }

    public DataModel(String title, String note) {
        this.title = title;
        this.note = note;

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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}