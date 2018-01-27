package com.example.mohini.notes.activities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.fragment.AddNoteFragment;
import com.example.mohini.notes.activities.model.DataModel;
import com.example.mohini.notes.activities.model.TagModel;

import java.util.ArrayList;

/**
 * Created by mohini on 24/1/18.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    Context context;
    //public static ArrayList<TagModel> tagModels;
    public static ArrayList<String> tagModels;

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

        public ViewHolder(final View itemView) {
            super(itemView);
            tagcardView = (CardView) itemView.findViewById(R.id.tagcardview);
            tagtextview = (TextView) itemView.findViewById(R.id.tagtextview);
            tagcardView.setRadius(12);
            context = itemView.getContext();
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }
}
