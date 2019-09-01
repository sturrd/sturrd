package com.sturrd.sturrd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sturrd.sturrd.DateLocation.DateLocObject;
import com.sturrd.sturrd.LocationRequest.LocRequestAdapter;
import com.sturrd.sturrd.LocationRequest.LocRequestObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;
import static java.security.AccessController.getContext;

public class ProfilesRequest extends AppCompatActivity {

    List<LocRequestObject> profiles;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageView mBack;
    private String currentUserID;
    private String locationId;
    private TextView txtLocation;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<LocRequestObject> resultsProfile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles_request);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decor = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        mBack = findViewById(R.id.btn_back_nav);
        locationId = getIntent().getExtras().getString("locationId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        txtLocation = findViewById(R.id.txt_location_title);

        txtLocation.setText(locationId);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilesRequest.super.onBackPressed();
                finish();
            }
        });
        getProfileKey();
        getProfiles();

    }

    private void getProfiles() {
        recyclerView = findViewById(R.id.location_request_recycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        int numberOfColumns = 2;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new LocRequestAdapter(getDataSetProfiles(), this);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LocRequestObject profile = profiles.get(position);
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(adapter);
    }

    private void getProfileKey() {
        DatabaseReference profileDb = FirebaseDatabase.getInstance().getReference().child("Users");
        profileDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot profile : dataSnapshot.getChildren()) {
                        FetchProfileInformation(profile.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchProfileInformation(String key) {
        for (int i = 0; i < resultsProfile.size(); i++) {
            if (resultsProfile.get(i).getUserId().equals(key))
                return;
        }

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = "",
                            name = "",
                            job = "",
                            userId = dataSnapshot.getKey();

                    if (dataSnapshot.child("name").getValue() != null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("profileImageUrl").getValue() != null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    if (dataSnapshot.child("job").getValue() != null)
                        job = dataSnapshot.child("job").getValue().toString();


                    for (int i = 0; i < resultsProfile.size(); i++) {
                        if (resultsProfile.get(i).getUserId().equals(userId))
                            return;
                    }

                    LocRequestObject obj = new LocRequestObject(profileImageUrl, name, job, userId);
                    resultsProfile.add(obj);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private List<LocRequestObject> getDataSetProfiles() {
        return resultsProfile;
    }

}
