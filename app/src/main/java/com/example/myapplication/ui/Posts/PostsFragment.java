package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.DetailPostFragment;

public class PostsFragment extends Fragment {



    private static final String TAG = "PostsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_posts, container, false);


        // need to change this to take the image id as argument and load its own detailed post
        ImageView img = root.findViewById(R.id.post1);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //LOAD DETAIL POST FRAGMENT
                openDetailPostActivity();
            }
        });

        return root;
    }

    public void openDetailPostActivity(){
        Intent intent = new Intent(getActivity(), DetailPostFragment.class);
        startActivity(intent);
/*
        DetailPostFragment detailPostFragment = new DetailPostFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, detailPostFragment,"DetailPostFragment")
                    .addToBackStack(null)
                    .commit();
*/
    }
}
