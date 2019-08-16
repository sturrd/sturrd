package com.sturrd.sturrd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.sturrd.sturrd.R;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment {

    private RecyclerView mMatch, mChat, mLikes;
    private RecyclerView.Adapter mMatchesAdapter, mChatAdapter, mLikesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager, mChatLayoutManager, mLikesLayoutManager;

    private String currentUserID, currentUId;
    private FirebaseAuth mAuth;
    private View view;

    public ExploreFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_explore, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance();



        return view;
    }



}