package com.sturrd.sturrd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sturrd.sturrd.R;

import androidx.fragment.app.Fragment;

public class DatesFragment extends Fragment {



    View view;

    public DatesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dates, container, false);


        return view;
    }



}