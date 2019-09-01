package com.sturrd.sturrd.LocationRequest;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sturrd.sturrd.R;

import androidx.recyclerview.widget.RecyclerView;

class LocRequestViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public LinearLayout mLayout;
    public TextView txtName, txtJob;

    public LocRequestViewHolder(View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.img_request_profile);
        mLayout = itemView.findViewById(R.id.layout_request_profile_cards);
        txtJob = itemView.findViewById(R.id.txt_job);
        txtName = itemView.findViewById(R.id.txt_name);


    }
}
