package com.example.mohini.notes.activities.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.activities.Notes;
import com.example.mohini.notes.activities.adapter.NoteAdapter;
import com.example.mohini.notes.activities.interfaces.ApiInterface;
import com.example.mohini.notes.activities.model.NoteModel;
import com.example.mohini.notes.activities.model.SharedNotes;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.mohini.notes.activities.activities.LoginActivity.email;
import static com.example.mohini.notes.activities.activities.LoginActivity.serverToken;
import static com.example.mohini.notes.activities.activities.Notes.isShared;
import static com.example.mohini.notes.activities.adapter.NoteAdapter.editNoteid;

/**
 * Created by mohini on 15/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    public static EditText note,noteTitle,entertag;
    Button addNote, cancel,editing;
    public static String notes,title,tag,noteid;
    //Scroll view for colors
    HorizontalScrollView colormenu;
    FrameLayout addNoteFrameLyout;
    //Button for color
    Button color1,color2,color3,color4,color5,color6,color7,defaultcolor;
    public static String changecolor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.addnotefragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //finding controls of xml using id's
        note = (EditText) getActivity().findViewById(R.id.note);
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        entertag=(EditText)getActivity().findViewById(R.id.entertag);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
        editing=(Button)getActivity().findViewById(R.id.editing);
        addNoteFrameLyout=(FrameLayout)getActivity().findViewById(R.id.addnoteframe);

        colormenu=(HorizontalScrollView)getActivity().findViewById(R.id.colormenu);
        color1=(Button)getActivity().findViewById(R.id.color1);
        color2=(Button)getActivity().findViewById(R.id.color2);
        color3=(Button)getActivity().findViewById(R.id.color3);
        color4=(Button)getActivity().findViewById(R.id.color4);
        color5=(Button)getActivity().findViewById(R.id.color5);
        color6=(Button)getActivity().findViewById(R.id.color6);
        color7=(Button)getActivity().findViewById(R.id.color7);
        defaultcolor=(Button)getActivity().findViewById(R.id.defaultcolor);
        //Setting listener on buttons
        colormenu.setVisibility(View.GONE);
        addNote.setOnClickListener(this);
        cancel.setOnClickListener(this);
        editing.setOnClickListener(this);
        color1.setOnClickListener(this);
        color2.setOnClickListener(this);
        color2.setOnClickListener(this);
        color3.setOnClickListener(this);
        color4.setOnClickListener(this);
        color5.setOnClickListener(this);
        color6.setOnClickListener(this);
        color7.setOnClickListener(this);
        defaultcolor.setOnClickListener(this);
        //condition to check weather we are editing the note or adding a fresh note
        if(!(editNoteid==null))
        {
            entertag.setText(NoteAdapter.editNotetag);
            noteTitle.setText(NoteAdapter.editNoteTitle);
            note.setText(NoteAdapter.editNote);
            noteid= editNoteid;
            changecolor=NoteAdapter.editNoteColor;

        }
    }


       //adds note in the server.
       public void addNote(String email, String title, String note, String color, String tag) {


        //HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            NoteModel noteList = new NoteModel(editNoteid,email, title, note, color, tag);
            apiService.addNote(noteList).enqueue(new Callback<NoteModel>() {
                @Override
                public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), Notes.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Something went wrong on server", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "Request not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<NoteModel> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to add Note", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //edit note to the server
    public void editNote(String id,String note,String title,String tag,String color) {

        editNoteid=null;
        //HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            NoteModel noteList = new NoteModel(id,email,title,note,color,tag);
            apiService.editNote(noteList).enqueue(new Callback<NoteModel>() {
                @Override
                public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), Notes.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Edited", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Some Error occured", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "Note not found to be edited", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NoteModel> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to edit note ", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //edits SharedNote and save it on the server.
    private void editSharedNote(String email, String title, String note, String color, String tag, String editNoteId) {

        //HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            SharedNotes sharedNotes = new SharedNotes("", email, title, note, color, tag, "", editNoteId);
            apiService.editSharedNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                //In case server responds.
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        editNoteid = null;
                        Intent intent = new Intent(getActivity(), Notes.class);
                        startActivity(intent);
                       // getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        Toast.makeText(getActivity(), "Note edit successfully..!! ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                //incase of failure to connect to server.
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getActivity(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //on Click method to perform action according to the button being clicked.
    int count=0;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        String color;
        switch (id) {
            case R.id.addNote: {

                    notes = note.getText().toString();
                    title = noteTitle.getText().toString();
                    tag = entertag.getText().toString();
                    if (notes.length() == 0)
                        Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                    else if ((title.length() == 0)) {
                        Toast.makeText(getActivity(), "Title Empty", Toast.LENGTH_SHORT).show();
                    } else {

                        if (editNoteid == null) {
                            addNote(email, title, notes, changecolor, tag);
                        } else {
                            if (isShared == 1)
                                editSharedNote(email, title, notes, changecolor, tag, noteid);
                            else {
                                if(changecolor==null)
                                {
                                    changecolor="";
                                    editNote(editNoteid, notes, title, tag, changecolor);
                                }
                                else {
                                    editNote(editNoteid, notes, title, tag, changecolor);
                                }
                            }
                        }

                    }

                break;
            }
            case R.id.cancel: {
                editNoteid=null;
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getBaseContext(),
                        Notes.class);
                getActivity().startActivity(intent);
                break;

            }
            case R.id.editing:

                if (count == 0) {
                    count++;
                    colormenu.setVisibility(View.VISIBLE);
                } else {
                    colormenu.setVisibility(View.GONE);
                    count--;
                }
                break;

            case R.id.defaultcolor:
                Snackbar snackbar = Snackbar.make(addNoteFrameLyout, "It will set " +
                        "your card view with default color", Snackbar.LENGTH_LONG);
                snackbar.show();
                color =null;
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor("#3B444B"));//87CEEB
                break;

            case R.id.color1:
              color ="#893F45";
               changecolor=color;
               addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color2:
                color ="#ffac99";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color3:
                color ="#87A96B";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color4:
                color ="#3D2B1F";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color5:
                color ="#004225";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color6:
                color ="#800020";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            case R.id.color7:
                color ="#614051";
                changecolor=color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
        }

       }


   }
