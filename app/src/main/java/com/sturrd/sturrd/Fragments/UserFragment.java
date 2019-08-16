package com.sturrd.sturrd.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sturrd.sturrd.R;

import androidx.fragment.app.Fragment;

public class UserFragment extends Fragment {

    private TextView mName, mJob, mSettings, mEditProfile;

    private ImageView mProfileImage;


    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);



        return view;
    }


}