package com.sturrd.sturrd.DateLocation;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sturrd.sturrd.R;

import androidx.recyclerview.widget.RecyclerView;

class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;
    public LinearLayout mLayout;

    public ViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        imageView = itemView.findViewById(R.id.img_location_date_card);
        mLayout = itemView.findViewById(R.id.card_layout_dating_cards);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked", Toast.LENGTH_LONG).show();

            }
        });


    }


    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Clicked", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(v.getContext(), DateLocActivity.class);
        //Bundle b = new Bundle();
        //b.putString("locationId", mLayout.getTag().toString());
        //intent.putExtras(b);
        //v.getContext().startActivity(intent);
    }
}
