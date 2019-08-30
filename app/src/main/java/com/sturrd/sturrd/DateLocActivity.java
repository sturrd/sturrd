package com.sturrd.sturrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sturrd.sturrd.R;

public class DateLocActivity extends AppCompatActivity {

    DatabaseReference mDatabaseUser, mUserConnection;
    private ImageView mDateLoc;
    private Button mRequestDate;
    private TextView mLocationText;
    private RatingBar mRatingBar;
    private FirebaseAuth mAuth;
    private String locationId, name, thumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_loc);

        mDateLoc = findViewById(R.id.img_location_card);

        mRequestDate = findViewById(R.id.btn_find_request);

        mLocationText = findViewById(R.id.txt_location_name);
        locationId = getIntent().getExtras().getString("locationId");
        mAuth = FirebaseAuth.getInstance();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Places").child(locationId);

        getLocInfo();


    }

    private void getLocInfo() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    if (dataSnapshot.child("name").getValue() != null)
                        name = dataSnapshot.child("name").getValue().toString();
                    mLocationText.setText(name);
                    if (dataSnapshot.child("image").getValue() != null)
                        thumbnailUrl = dataSnapshot.child("image").getValue().toString();
                    Glide.with(getApplicationContext()).load(thumbnailUrl).apply(RequestOptions.centerCropTransform()).into(mDateLoc);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
