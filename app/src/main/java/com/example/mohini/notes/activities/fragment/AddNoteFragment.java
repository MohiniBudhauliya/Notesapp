package com.example.mohini.notes.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.activities.Notes;
import com.example.mohini.notes.activities.adapter.NoteAdapter;
import com.example.mohini.notes.activities.model.DataModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by mohini on 15/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    public static EditText note,noteTitle;
    Button addNote, cancel;
    public static String notes,title;
    // Write a message to the database
    //FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference rootreference,titlerefrence,noterefrence;
    public static String userId;
    DataModel dataModel;
    public static ArrayList<String> keyStore=new ArrayList<>();
    int i=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.addnotefragment, container, false);
    }
    public void addNote()
    {
        //Notes.rootreference = Notes.database.getInstance().getReference("Your Note");
        //Creating new user node, which returns the unique key value
        userId = Notes.userId;
        keyStore.add(i,Notes.userId);
        titlerefrence=Notes.rootreference.child("Title");
        noterefrence=Notes.rootreference.child("Note");
        dataModel=new DataModel(userId,title,notes,"Normal");
        Notes.rootreference.child(Notes.userId).setValue(dataModel);
        i++;
    }
    public static void editNote(String editId, String editNoteTitle, String editNote,String priority) {
        //editNoteId = null;
//        DatabaseReference editreference;
        DataModel noteList = new DataModel(editId, editNoteTitle, editNote,priority);
//        editreference = FirebaseDatabase.getInstance().getReference("NoteList").child(editId);
        Notes.rootreference.child(editId).setValue(noteList);
//        Toast.makeText(getActivity(), "Updated Realtime Database!!!", Toast.LENGTH_SHORT).show();

    }


    public static void removeNote(final String deleteNoteId)
    {
        Notes.rootreference.child(deleteNoteId).removeValue();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        note = (EditText) getActivity().findViewById(R.id.note);
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
        addNote.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }
    //on Click method to perform action according to the button being clicked.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.addNote: {
                notes=note.getText().toString();
                title = noteTitle.getText().toString();
                if (notes.length() == 0)
                    Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                else if((title.length() == 0)){
                    Toast.makeText(getActivity(), "Title Empty", Toast.LENGTH_SHORT).show();
                  }
                   else {
                    Intent intent = new Intent(getActivity(),Notes.class);
                    addNote();
                    Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                break;
            }
            case R.id.cancel: {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getBaseContext(),
                        Notes.class);
                getActivity().startActivity(intent);
                break;

            }
        }
    }

}
