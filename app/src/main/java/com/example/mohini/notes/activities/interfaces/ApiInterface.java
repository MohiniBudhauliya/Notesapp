package com.example.mohini.notes.activities.interfaces;

import com.example.mohini.notes.activities.model.NoteModel;
import com.example.mohini.notes.activities.model.LoginUserDetails;
import com.example.mohini.notes.activities.model.SharedNotes;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    String BASE_URL = "http://192.168.20.51:1337/";

    //Api request for google login
    @POST("login")

    Call<LoginUserDetails> loginUser(@Body LoginUserDetails body);

   //Api request for getting all notes after login
    @POST("note")
    Call<List<NoteModel>> allnotes(@Body NoteModel body);

    //for adding notes
    @POST("note/add")
    Call<NoteModel> addNote(@Body NoteModel body);

    //for deleting notes
    @POST("note/delete")
    Call<JsonObject> deleteNote (@Body NoteModel body);

   //for getting id to edit notes
    @POST("note/getedit")
    Call<NoteModel> geteditNoteId (@Body NoteModel body);

    //for edit notes
    @POST("note/edit")
    Call<NoteModel> editNote(@Body NoteModel body);

    //shares note to the registered user.
    @POST("note/share")
    Call<JsonObject> shareNote(@Body SharedNotes body);

    //fetches SharedNotes.
    @POST("note/shared")
    Call<List<NoteModel>> sharedNote(@Body SharedNotes body);


    //gets id of the Shared note to be edited.
    @POST("note/shared/getedit")
    Call<NoteModel> editSharedNoteId(@Body SharedNotes body);

    //update the edits done to the Shared note.
    @POST("note/shared/edit")
    Call<JsonObject> editSharedNote(@Body SharedNotes body);

    //deletes Sharednote from server.
    @POST("note/shared/delete")
    Call<JsonObject> deleteSharedNote(@Body SharedNotes body);
}