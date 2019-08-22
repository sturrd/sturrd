package com.sturrd.sturrd.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class UserFragment extends Fragment {

    private TextView mName, mJob, mSettings, mEditProfile;

    private ImageView mProfileImage;
    View view;


    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        mProfileImage = view.findViewById(R.id.profileImage);

        mName = view.findViewById(R.id.nameTxt);
        mJob = view.findViewById(R.id.jobTxt);


        getUserInfo();

        return view;

    }

    private void getUserInfo() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    String  name = "",
                            age = "",
                            job = "",
                            profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();

                    if(dataSnapshot.child("job").getValue()!=null)
                        job = dataSnapshot.child("job").getValue().toString();

                    if(dataSnapshot.child("age").getValue()!=null)
                        age = dataSnapshot.child("age").getValue().toString();

                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name + ", " + age);
                    mJob.setText(job);
                    Glide.with(getContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}