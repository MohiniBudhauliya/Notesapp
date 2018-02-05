package com.example.mohini.notes.activities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.model.NoteModel;

import java.util.ArrayList;

import static com.example.mohini.notes.activities.activities.Notes.adapter;
import static com.example.mohini.notes.activities.activities.Notes.notes;
import static com.example.mohini.notes.activities.activities.Notes.recyclerView;

/**
 * Created by mohini on 24/1/18.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    Context context;
    public static ArrayList<String> tagModels;
    public static ArrayList<NoteModel> noteTags = new ArrayList<>();

    public static String getNoteByTag = null;

    public TagAdapter(Context context, ArrayList<String> tagModels) {
        this.context=context;
        this.tagModels = tagModels;
    }


    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagadapter, parent, false);

        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagAdapter.ViewHolder holder, int position) {
        //getting the ride of the specified position
        //String tag;
        String tag = tagModels.get(position);
        //tag = note.getTag();
        holder.tagtextview.setText(tag);
        holder.tagcardView.setBackgroundColor(Color.parseColor("#803B444B"));
    }

    @Override
    public int getItemCount() {
        return tagModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView  tagtextview;
        public static CardView tagcardView;
        Context context;
        int count=0;
        public ViewHolder(final View itemView) {
            super(itemView);
            tagcardView = (CardView) itemView.findViewById(R.id.tagcardview);
            tagtextview = (TextView) itemView.findViewById(R.id.tagtextview);
            tagcardView.setRadius(12);
            context = itemView.getContext();
            noteTags.clear();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(count==0) {
                        int p = getLayoutPosition();
                        count++;
                        itemView.setBackgroundColor(Color.parseColor("#0f5455"));
                        getNoteByTag = tagModels.get(p);
                        for (NoteModel n : notes) {
                            if (n.getTag().equals(getNoteByTag)) {
                                noteTags.add(n);
                            }

                        }
                    }
                    else{
                        int p = getLayoutPosition();
                        itemView.setBackgroundColor(Color.parseColor("#339988"));
                        count--;
                        getNoteByTag = tagModels.get(p);
                        for (NoteModel n : notes) {
                            if (n.getTag().equals(getNoteByTag)) {
                                noteTags.remove(n);
                            }

                        }

                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager;
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                    adapter = new NoteAdapter(context, noteTags);
                    recyclerView.setAdapter(adapter);

                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }
}
