package com.sturrd.sturrd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.sturrd.sturrd.R;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mMoments;
    private RecyclerView.LayoutManager mMomentsLayoutManager;
    private RecyclerView.Adapter mMomentsAdapter;
    private String currentUId;
    private View view;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_requests, container, false);

        return view;
    }



}
