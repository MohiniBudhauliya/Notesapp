package com.example.mohini.notes.activities.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.example.mohini.notes.activities.model.DataModel;
import com.example.mohini.notes.activities.model.TagModel;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by mohini on 15/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    public static EditText note,noteTitle,entertag;
    Button addNote, cancel,editing;
    public static String notes,title,tag;
    public static ArrayList<String>taglist=new ArrayList<>();
    HorizontalScrollView colormenu;
    FrameLayout addNoteFrameLyout;
    //Button for color
    Button color1,color2,color3,color4,color5,color6,color7,defaultcolor;
    public static DatabaseReference rootreference,titlerefrence,noterefrence,tagrefrence;
    public static String userId,changecolor;
    DataModel dataModel;
    TagModel tagModel;
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
        tagrefrence=Notes.rootreference.child("TAG");
        dataModel=new DataModel(userId,title,notes,tag,changecolor);
        taglist.add(i,tag);
        tagModel=new TagModel(tag);
        Notes.rootreference.child(Notes.userId).setValue(dataModel);
        i++;
    }
    public  void editNote(String editId, String editNoteTitle, String editNote,String tag,String coloroption)
    {
        DataModel noteList = new DataModel(editId, editNoteTitle, editNote,tag,coloroption);

        Notes.rootreference.child(editId).setValue(noteList);
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
                    Intent intent = new Intent(getActivity(), Notes.class);
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
