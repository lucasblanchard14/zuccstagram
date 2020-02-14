package com.example.myapplication.ui.Bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.ui.Profile.ProfileViewModel;

public class BookmarksFragment extends Fragment {

    private BookmarksViewModel postsViewModel;

    private static final String TAG = "BookmarksFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postsViewModel =
                ViewModelProviders.of(this).get(BookmarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        return root;
    }
}