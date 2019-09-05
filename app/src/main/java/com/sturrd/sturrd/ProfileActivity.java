package com.sturrd.sturrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage, backNav;
    private TextView txtName, txtAbout, txtDistance, txtAge;
    private String key;
    private FusedLocationProviderClient client;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        key = getIntent().getExtras().getString("userId");


        profileImage = findViewById(R.id.img_profile);
        backNav = findViewById(R.id.btn_back_nav);
        txtName = findViewById(R.id.txt_name);
        txtAge = findViewById(R.id.txt_age);
        txtAbout = findViewById(R.id.txt_about);
        txtDistance = findViewById(R.id.txt_distance);

        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
                finish();
            }
        });
        client = LocationServices.getFusedLocationProviderClient(this);


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String profileImageUrl = "",
                        name = "",
                        age = "",
                        about = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        Latitude = "",
                        Longitude = "";

                if (dataSnapshot.child("profileImageUrl").getValue() != null)
                    profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                if (dataSnapshot.child("name").getValue() != null)
                    name = dataSnapshot.child("name").getValue().toString();
                if (dataSnapshot.child("age").getValue() != null)
                    age = dataSnapshot.child("age").getValue().toString();
                if (dataSnapshot.child("about").getValue() != null)
                    about = dataSnapshot.child("about").getValue().toString();
                if (dataSnapshot.child("Latitude").getValue() != null)
                    Latitude = dataSnapshot.child("Latitude").getValue().toString();
                if (dataSnapshot.child("Longitude").getValue() != null)
                    Longitude = dataSnapshot.child("Longitude").getValue().toString();

                final String finalLatitude = Latitude;
                final String finalLongitude = Longitude;
                final String finalAge = age;
                final String finalAbout = about;
                final String finalName = name;
                client.getLastLocation().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            final double longitude = location.getLongitude();
                            final double latitude = location.getLatitude();

                            Location locationA = new Location("point A");
                            locationA.setLatitude(latitude);
                            locationA.setLongitude(longitude);
                            Location locationB = new Location("point B");
                            locationB.setLatitude(Double.parseDouble(finalLatitude));
                            locationB.setLongitude(Double.parseDouble(finalLongitude));

                            double distance = locationA.distanceTo(locationB);

                            //TODO correct the distance
                            //convert distance to km
                            distance = Math.round(distance / 1000);
                            int distanceAB = (int) distance;
                            String finalDist = String.valueOf(distanceAB);

                            txtDistance.setText(finalDist + " km");
                            txtAge.setText(finalAge + " yrs");
                            txtAbout.setText(finalAbout);

                            txtName.setText(finalName);
                        }

                    }
                });


                Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.centerCropTransform()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
