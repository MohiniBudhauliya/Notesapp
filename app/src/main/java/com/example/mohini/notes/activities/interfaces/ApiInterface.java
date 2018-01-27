package com.example.mohini.notes.activities.interfaces;

import com.example.mohini.notes.activities.model.DataModel;
import com.example.mohini.notes.activities.model.LoginUserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    String BASE_URL = "http://10.30.255.35/";

    @POST("login")
    Call<LoginUserDetails> loginUser(@Body LoginUserDetails body);

    @POST("note")
    Call<List<DataModel>> notes(@Body DataModel body);
    @POST("note/add")
    Call<DataModel> addNote(@Body DataModel body);
}