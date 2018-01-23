package com.example.mohini.notes.activities.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    public static String editNoteId, editNoteTitle, editNote,editNotePrioirty;
    public static String priorityNoteId,priorityNote,priorityNoteTitle,priorityNotePriority;
    AddNoteFragment Fragment = new AddNoteFragment();
    //public static   String notes,title;


    public NoteAdapter(Context context, ArrayList<String> noteList, ArrayList<String> titleList) {
        this.context = context;
        this.noteList = noteList;
        this.titleList = titleList;
    }
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
        String notes, title;
        DataModel note = dataModels.get(position);
        notes = note.getNote();
        title = note.getTitle();
        if(note.getPriority().equals("Important"))
           holder.cardView.setBackgroundResource(R.drawable.impcardview);//Color.parseColor("#ffac99")
        if(note.getPriority().equals("Normal"))
            holder.cardView.setBackgroundResource(R.drawable.normalcardview);//Color.parseColor("#803B444B")
        holder.writeNote.setText("Note: " + notes);
        holder.title.setText("Title: " + title);

    }

    @Override
    public int getItemCount() {
        //return noteList.size();
        return dataModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView writeNote, title;
        public  CardView cardView;
        int p;
        String deleteNoteId;
        Context context;
        public ViewHolder(final View itemView) {
            super(itemView);
            writeNote = (TextView) itemView.findViewById(R.id.writenote);
            title = (TextView) itemView.findViewById(R.id.title);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            cardView.setRadius(12);
            context = itemView.getContext();
            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("OPTIONS:");
            MenuItem editnote = menu.add(0, view.getId(), 0, "Edit Note");
            MenuItem remove = menu.add(0, view.getId(), 0, "Remove Note");//groupId, itemId, order, title
            MenuItem imp = menu.add(0, view.getId(), 0, "Mark as Important").setIcon(R.drawable.star);
            MenuItem normal = menu.add(0, view.getId(), 0, "Make it normal").setIcon(R.drawable.normalstar);
            MenuItem cancel = menu.add(0, view.getId(), 0, "Cancel");

            remove.setOnMenuItemClickListener(onEditMenu);
            imp.setOnMenuItemClickListener(onEditMenu);
            cancel.setOnMenuItemClickListener(onEditMenu);
            editnote.setOnMenuItemClickListener(onEditMenu);
            normal.setOnMenuItemClickListener(onEditMenu);
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
                    //Notes.deletedNotes(deleteNoteId);
                    Intent intent = new Intent(context, Notes.class);
                    context.startActivity(intent);
                }
                else if (item.getTitle() == "Mark as Important") {
                    p = getAdapterPosition();
                    DataModel note = dataModels.get(p);
                    priorityNoteId = note.getId();
                    priorityNote = note.getNote();
                    priorityNoteTitle = note.getTitle();
                    note.setPriority("Important");
                    priorityNotePriority="Important";
                    AddNoteFragment.editNote(priorityNoteId,priorityNoteTitle,priorityNote,priorityNotePriority);
                    Intent intent = new Intent(context, Notes.class);
                    context.startActivity(intent);
                    //cardView.setCardBackgroundColor(Color.parseColor("#ffac99"));
                }
                else if (item.getTitle() == "Make it normal") {
                    p = getAdapterPosition();
                    DataModel note = dataModels.get(p);
                    priorityNoteId = note.getId();
                    priorityNote = note.getNote();
                    priorityNoteTitle = note.getTitle();
                    note.setPriority("Normal");
                    priorityNotePriority="Normal";
                    AddNoteFragment.editNote(priorityNoteId,priorityNoteTitle,priorityNote,priorityNotePriority);
                    Intent intent = new Intent(context, Notes.class);
                    context.startActivity(intent);
                    //cardView.setCardBackgroundColor(Color.parseColor("#ffac99"));
                }
                else if(item.getTitle()=="Edit Note")
                {
                    p = getLayoutPosition();
                    DataModel note = dataModels.get(p);
                    editNoteId = note.getId();
                    editNote = note.getNote();
                    editNoteTitle = note.getTitle();
                    editNotePrioirty=note.getPriority();
                    note.getId().replace(editNoteId,"xyz");
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

