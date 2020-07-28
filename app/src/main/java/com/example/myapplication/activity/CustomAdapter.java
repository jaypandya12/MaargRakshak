package com.example.myapplication.activity;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.EventViewHolder> {


    List<DataModel> eventLists;
    Context context;

    public CustomAdapter(List<DataModel> eventLists, Context context) {
        this.eventLists = eventLists;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cards_layout, viewGroup, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {

        final DataModel eventList = eventLists.get(i);
        eventViewHolder.event_name.setText(eventList.getName());
        eventViewHolder.event_desc.setText(eventList.getDesc());



        Picasso.get()
                .load(eventList.getImageUrl())
                .into(eventViewHolder.event_image);

        eventViewHolder.event_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("key", eventList.getName());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        int size = eventLists.size();
        return size;
    }


    public class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView event_image;
        TextView event_name;
        TextView event_desc;
        TextView like;
        LinearLayout event_card;

        public EventViewHolder(View itemView) {
            super(itemView);
            event_image = (ImageView) itemView.findViewById(R.id.image1);
            event_name = itemView.findViewById(R.id.event_name1);
            event_desc = itemView.findViewById(R.id.event_desc1);
            event_card = itemView.findViewById(R.id.card);
            like = itemView.findViewById(R.id.like);
        }
    }
}
