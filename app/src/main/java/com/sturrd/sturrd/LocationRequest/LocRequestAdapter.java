package com.sturrd.sturrd.LocationRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sturrd.sturrd.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LocRequestAdapter extends RecyclerView.Adapter<LocRequestViewHolder> {

    private Context context;
    private List<LocRequestObject> profiles;

    public LocRequestAdapter(List<LocRequestObject> profiles, Context context) {
        this.profiles = profiles;
        this.context = context;
    }

    @Override
    public LocRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_request_profile_cards, parent, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //v.setLayoutParams(lp);
        LocRequestViewHolder viewHolder = new LocRequestViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocRequestViewHolder holder, int position) {
        LocRequestObject profile = profiles.get(position);
        holder.txtName.setText(profile.getName() + ", " + profile.getAge());
        holder.txtJob.setText(profile.getJob());
        Glide.with(context).load(profile.getImage()).apply(RequestOptions.centerCropTransform()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }


}