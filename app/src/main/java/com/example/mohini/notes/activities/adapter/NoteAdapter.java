package com.example.mohini.notes.activities.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.activities.Notes;
import com.example.mohini.notes.activities.fragment.AddNoteFragment;
import com.example.mohini.notes.activities.model.DataModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by mohini on 15/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    public static ArrayList<DataModel> dataModels;

    //we are storing all the rides in a list
    public static ArrayList<String> noteList;
    public static  ArrayList<String> titleList;
    public static  ArrayList<String> tag;
    public static String editNoteId, editNoteTitle, editNote,editNotetag;
    public static String priorityNoteId,priorityNote,priorityNoteTitle,priorityNotePriority;

    public NoteAdapter(Context context, ArrayList<DataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
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
        DataModel note = dataModels.get(position);
        notes = note.getNote();
        title = note.getTitle();
        tag=note.getTag();
        color=note.getColor();
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
        //return noteList.size();
        return dataModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView writeNote, title,tag;
        public static CardView cardView;
        int p;
        String deleteNoteId;
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


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("OPTIONS:");
            MenuItem editnote = menu.add(0, view.getId(), 0, "Edit Note");
            MenuItem remove = menu.add(0, view.getId(), 0, "Remove Note");//groupId, itemId, order, title
//            MenuItem imp = menu.add(0, view.getId(), 0, "Mark as Important").setIcon(R.drawable.star);
//            MenuItem normal = menu.add(0, view.getId(), 0, "Make it normal").setIcon(R.drawable.normalstar);
            MenuItem cancel = menu.add(0, view.getId(), 0, "Cancel");

            remove.setOnMenuItemClickListener(onEditMenu);
            //imp.setOnMenuItemClickListener(onEditMenu);
            cancel.setOnMenuItemClickListener(onEditMenu);
            editnote.setOnMenuItemClickListener(onEditMenu);
            //normal.setOnMenuItemClickListener(onEditMenu);
        }

        private  MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle() == "Remove Note") {
                    p = getAdapterPosition();
                    DataModel note = dataModels.get(p);
                    deleteNoteId = note.getId();
                    Notes.arraydata.remove(p);
                    AddNoteFragment.removeNote(deleteNoteId);
                    Intent intent = new Intent(context, Notes.class);
                    context.startActivity(intent);
                }
                else if(item.getTitle()=="Edit Note")
                {
                    p = getLayoutPosition();
                    DataModel note = dataModels.get(p);
                    editNoteId = note.getId();
                    editNote = note.getNote();
                    editNoteTitle = note.getTitle();
                    editNotetag=note.getTag();
                    String color=note.getColor();
                    writeNote.setText(editNote);
                    title.setText(editNoteTitle);
                    tag.setText(editNotetag);
                    Notes obj=new Notes();
                    obj.showlayoutforaddingnote();
                    Toast.makeText(itemView.getContext(), "Edited", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Notes.class);
                    context.startActivity(intent);
                }

                else if (item.getTitle() == "Cancel") {

                    return false;
                }
                return false;
            }

        };


    }
}

