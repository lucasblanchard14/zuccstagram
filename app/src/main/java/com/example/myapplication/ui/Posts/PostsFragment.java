package com.example.myapplication.ui.Posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

public class PostsFragment extends Fragment {

    private PostsViewModel postsViewModel;

    private static final String TAG = "PostsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postsViewModel =
                ViewModelProviders.of(this).get(PostsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_posts, container, false);
        return root;
    }
}