package com.sturrd.sturrd.DateLocation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sturrd.sturrd.DateLocActivity;
import com.sturrd.sturrd.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class DateLocAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private List<DateLocObject> images;

    public DateLocAdapter(Context context, List<DateLocObject> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_dating_location_cards, parent, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //v.setLayoutParams(lp);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DateLocObject image = images.get(position);

        //holder.textViewName.setText(image.getName());

        Glide.with(context).load(image.getImage()).apply(RequestOptions.centerCropTransform()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


}