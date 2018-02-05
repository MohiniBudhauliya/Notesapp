package com.example.mohini.notes.activities.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.activities.Notes;
import com.example.mohini.notes.activities.fragment.AddNoteFragment;
import com.example.mohini.notes.activities.interfaces.ApiInterface;
import com.example.mohini.notes.activities.model.NoteModel;
import com.example.mohini.notes.activities.model.SharedNotes;
import com.google.firebase.iid.FirebaseInstanceId;
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
import static com.example.mohini.notes.activities.activities.Notes.notes;
import static com.example.mohini.notes.activities.activities.Notes.pinnedNote;
import static com.example.mohini.notes.activities.activities.Notes.pinnedNoteLayout;
import static com.example.mohini.notes.activities.activities.Notes.pinnedTag;
import static com.example.mohini.notes.activities.activities.Notes.pinnedTitle;
import static com.example.mohini.notes.activities.activities.Notes.pinrecyclerView;
import static com.example.mohini.notes.activities.activities.Notes.recyclerView;
import static com.example.mohini.notes.activities.activities.Notes.tagList;
import static com.example.mohini.notes.activities.activities.Notes.tagRecyclerView;

/**
 * Created by mohini on 15/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    public static ArrayList<NoteModel> noteModels;
    public static SharedPreferences pinned;
    //we are storing all the rides in a list
    public static String editNoteid, editNoteTitle, editNote,editNotetag,editNoteColor,pinnedNotes, pinnedNoteTag, pinnedNoteTitle, pinnedNoteColor;
    public NoteAdapter(Context context, ArrayList<NoteModel> noteModels) {
        this.context = context;
        this.noteModels = noteModels;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteadapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //getting the ride of the specified position
        String notes, title,tag,color;
        NoteModel note = noteModels.get(position);
        notes = note.getNote();
        title = note.getTitle();
        tag=note.getTag();
        color=note.getColor();
        holder.itemView.setTag(false);
        if(color==null)
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#803B444B"));
        }
        else {
            holder.cardView.setBackgroundColor(Color.parseColor(color));//setBackgroundResource(R.drawable.normalcardview);
        }
        holder.writeNote.setText("Note: " + notes);
        holder.title.setText("Title: " + title);
        holder.tag.setText(tag);
    }

    @Override
    public int getItemCount() {
        return noteModels.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView writeNote, title,tag;
        public  CardView cardView;
        public  int p;
        Context context;
        public ViewHolder(final View itemView) {
            super(itemView);
            writeNote = (TextView) itemView.findViewById(R.id.writenote);
            title = (TextView) itemView.findViewById(R.id.title);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            tag=(TextView)itemView.findViewById(R.id.tag);
            cardView.setRadius(12);
            context = itemView.getContext();
            itemView.setOnCreateContextMenuListener(this);

        }
        MenuItem pintotop,editnote,remove,cancel;
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("OPTIONS:");
            if(isShared==1)
            {
                editnote = menu.add(0, view.getId(), 0, "EditNote");
                remove = menu.add(0, view.getId(), 0, "Remove Note");
                cancel = menu.add(0, view.getId(), 0, "Cancel");
                remove.setOnMenuItemClickListener(onEditMenu);
                editnote.setOnMenuItemClickListener(onEditMenu);
                cancel.setOnMenuItemClickListener(onEditMenu);
            }
            else {
                MenuItem editnote = menu.add(0, view.getId(), 0, "Edit Note");
                pintotop = menu.add(0, view.getId(), 0, "Pin to top");
                MenuItem remove = menu.add(0, view.getId(), 0, "Remove Note");
                MenuItem share = menu.add(0, view.getId(), 0, "Share Note");
                cancel = menu.add(0, view.getId(), 0, "Cancel");
                remove.setOnMenuItemClickListener(onEditMenu);
                pintotop.setOnMenuItemClickListener(onEditMenu);
                cancel.setOnMenuItemClickListener(onEditMenu);
                editnote.setOnMenuItemClickListener(onEditMenu);
                share.setOnMenuItemClickListener(onEditMenu);
            }
        }
        OkHttpClient defaultHttpClient;
        Retrofit retrofit;
        private  MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick( MenuItem item) {

                if (item.getTitle()=="Remove Note") {
                    p = getLayoutPosition();
                    //check if note is sharednote or normal note and performs suitable function.
                    if (isShared == 1) {
                        deleteSharedNote(email, noteModels.get(p).getNote());
                    }
                    else {
                        final String notetobedeleted = noteModels.get(p).getNote();
                        final String tagtodelete = noteModels.get(p).getTag();
                        //HttpCLient to Add Authorization Header.
                        defaultHttpClient = new OkHttpClient.Builder().addInterceptor(
                                new Interceptor() {
                                    @Override
                                    public okhttp3.Response intercept(Chain chain) throws IOException {
                                        Request request = chain.request().newBuilder()
                                                .addHeader("authorization", "bearer " + serverToken).build();
                                        return chain.proceed(request);
                                    }
                                }).build();

                        //Retrofit to retrieve JSON data from server.
                        retrofit = new Retrofit.Builder()
                                .baseUrl(ApiInterface.BASE_URL)
                                .client(defaultHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                                .build();

                        ApiInterface apiService = retrofit.create(ApiInterface.class);
                        try {
                            final NoteModel noteList = new NoteModel(email, notetobedeleted);
                            apiService.deleteNote(noteList).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if (response.isSuccessful()) {

                                        int len, count = 0;
                                        len = noteModels.size();

                                        for (int i = 0; i < len; i++) {
                                            String tagtotest = noteModels.get(i).getTag();
                                            if ((tagtotest.equals(tagtodelete))) {
                                                count++;
                                            }
                                            if (count == 1) {
                                                tagList.remove(tagtodelete);
                                            }

                                        }
                                        Notes.notes.remove(p);
                                        //notifies adapter that an item is removed.
                                        notifyItemRemoved(p);
                                        //deletes the tag related to the note being deleted.
                                        notifyItemRangeChanged(p, getItemCount());
                                        StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                                        tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                                StaggeredGridLayoutManager.HORIZONTAL);
                                        tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                                        tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                                        TagAdapter tagAdapter = new TagAdapter(context, tagList);
                                        tagRecyclerView.setAdapter(tagAdapter);
                                        Toast.makeText(itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    } else if (response.code() == 500) {
                                        Toast.makeText(itemView.getContext(), "Some Error occured", Toast.LENGTH_SHORT).show();
                                    } else if (response.code() == 404) {
                                        Toast.makeText(itemView.getContext(), "wrong..", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(itemView.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(item.getTitle()=="Share Note"){
                    p = getLayoutPosition();
                    final NoteModel note = noteModels.get(p);
                    //creating alert dialog with text input to take email id of recipient.
                    @SuppressLint("RestrictedApi") AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(itemView.getContext(),R.style.myDialog));
                    builder.setTitle("Share Note");
                    // I'm using fragment here so I'm using getView() to provide ViewGroup
                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.share_alert, (ViewGroup) itemView, false);
                    // Set up the input
                    final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    builder.setView(viewInflated);
                    // Set up the buttons
                    //in case ok is pressed.
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            String reciever_email = input.getText().toString();
                            String[] recieverList = reciever_email.split(" ");

                            for (int i = 0; i < recieverList.length; i++) {
                            shareNote(email, recieverList[i], note.getTitle(), note.getNote(), note.getColor(), note.getTag(), FirebaseInstanceId.getInstance().getToken());
                            }

                        }

                    });
                    //else cancel the dialog without any action.
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }

                else if(item.getTitle()=="Edit Note")
                {
                    p = getLayoutPosition();
                    //check if the note is shared or normal.
                    if (isShared == 1)

                        editSharedNoteid(email, noteModels.get(p).getNote());
                    else {
                        String notetobeedited = noteModels.get(p).getNote();
                        //HttpCLient to Add Authorization Header.
                        defaultHttpClient = new OkHttpClient.Builder().addInterceptor(
                                new Interceptor() {
                                    @Override
                                    public okhttp3.Response intercept(Chain chain) throws IOException {
                                        Request request = chain.request().newBuilder()
                                                .addHeader("authorization", "bearer " + serverToken).build();
                                        return chain.proceed(request);
                                    }
                                }).build();

                        //Retrofit to retrieve JSON data from server.
                        retrofit = new Retrofit.Builder()
                                .baseUrl(ApiInterface.BASE_URL)
                                .client(defaultHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                                .build();

                        ApiInterface apiService = retrofit.create(ApiInterface.class);
                        try {
                            NoteModel noteList = new NoteModel(email, notetobeedited);
                            apiService.geteditNoteId(noteList).enqueue(new Callback<NoteModel>() {
                                @Override
                                public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                                    if (response.isSuccessful()) {
                                        NoteModel noteModel = response.body();
                                        editNoteid = noteModel.getId();
                                        editNoteTitle = noteModel.getTitle();
                                        editNote = noteModel.getNote();
                                        editNotetag = noteModel.getTag();
                                        editNoteColor = noteModel.getColor();
                                        Intent intent = new Intent(context, Notes.class);
                                        context.startActivity(intent);

                                    } else if (response.code() == 500) {
                                        Toast.makeText(itemView.getContext(), "Some Error occured", Toast.LENGTH_SHORT).show();
                                    } else if (response.code() == 404) {
                                        Toast.makeText(itemView.getContext(), "Note not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<NoteModel> call, Throwable t) {
                                    Toast.makeText(itemView.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                else if (item.getTitle()=="Cancel") {

                    return false;
                }
                else if(item.getTitle()=="Pin to top")
                {
                    p = getLayoutPosition();
                    NoteModel note = notes.get(p);
                    pinnedNotes = note.getNote();
                    pinnedNoteTag = note.getTag();
                    pinnedNoteTitle = note.getTitle();
                    if (note.getColor() != null)
                        pinnedNoteColor = note.getColor();
                    else
                        pinnedNoteColor = "#803B444B";

                    //storing pinned note data in sharedPreferences.
                    pinned = context.getSharedPreferences("pinned", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pinned.edit();
                    editor.putBoolean("isPinned", true);
                    editor.putString("pinnedNote", pinnedNotes);
                    editor.putString("pinnedNoteTag", pinnedNoteTag);
                    editor.putString("pinnedNoteTitle", pinnedNoteTitle);
                    editor.putString("pinnedNoteColor", pinnedNoteColor);
                    editor.commit();
                    setPinnedNote(pinnedNoteTitle, pinnedNotes, pinnedNoteTag, pinnedNoteColor);
                    notifyDataSetChanged();
                } else if (item.getTitle() == "Cancel") {
                    Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

        };
        //sets data to the top layout for pinned Note.
        void setPinnedNote(String title, String note, String tag, String color) {
            pinnedNote.setText(note);
            pinnedTag.setText(tag);
            pinnedTitle.setText(title);
            if (color != null)
                pinnedNoteLayout.setBackgroundColor(Color.parseColor(color));
            ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
            params.height = 300;
        }
        //shares note in the server database with particular user.
        public void shareNote(String sender_email, String reciever_email, String title, String note, String color, String tag, String fcmToken) {

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
                SharedNotes sharedNotes = new SharedNotes(sender_email, reciever_email, title, note, color, tag, fcmToken, "");
                apiService.shareNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                    //IN case server responds.
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            Toast.makeText(itemView.getContext(), "Note has been sent", Toast.LENGTH_SHORT).show();

                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "Email id not found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    // In case of Failure.
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(itemView.getContext(), "failed to share note", Toast.LENGTH_SHORT).show();

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //deletes sharedNotes from server.
        public void deleteSharedNote(String email, String note) {
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
                SharedNotes sharedNotes = new SharedNotes("", email, "", note, "", "", "", "");
                apiService.deleteSharedNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            Notes.notes.remove(p);
                            notifyItemRemoved(p);
                            Notes.tagList.remove(p);
                            notifyItemRangeChanged(p, getItemCount());
                            StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                            tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                    StaggeredGridLayoutManager.HORIZONTAL);
                            tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                            tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                            TagAdapter tagAdapter = new TagAdapter(context, tagList);
                            tagRecyclerView.setAdapter(tagAdapter);

                            Toast.makeText(itemView.getContext(), "Removing Note" , Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" , Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "note found" , Toast.LENGTH_SHORT).show();
                        }

                    }

                    //IN case of failure to connect to server.
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("here", "Unable to submit post to API.");
                        //System.out.print("throwable" + t);
                        Toast.makeText(itemView.getContext(), "failed ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //gets Id of SharedNote which is to be edited.
        public void editSharedNoteid(String email, String note) {

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
                SharedNotes sharedNotes = new SharedNotes("", email, "", note, "", "", "", "");
                apiService.editSharedNoteId(sharedNotes).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            NoteModel noteList = response.body();
                            NoteModel noteModel = response.body();
                            editNoteid = noteModel.getId();
                            editNoteTitle = noteModel.getTitle();
                            editNote = noteModel.getNote();
                            editNotetag = noteModel.getTag();
                            editNoteColor = noteModel.getColor();
                            Intent intent = new Intent(context, Notes.class);
                            context.startActivity(intent);
                            //Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" , Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "note found" , Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        Log.e("here", "Unable to submit post to API.");
                        //System.out.print("throwable" + t);
                        Toast.makeText(itemView.getContext(), "failed ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }





}

