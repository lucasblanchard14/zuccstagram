package com.example.myapplication.ui.Bio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.ui.Posts.PostsViewModel;

public class BioFragment extends Fragment {

    private BioViewModel bioViewModel;

    private static final String TAG = "BioFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bioViewModel =
                ViewModelProviders.of(this).get(BioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bio, container, false);
        return root;
    }
}