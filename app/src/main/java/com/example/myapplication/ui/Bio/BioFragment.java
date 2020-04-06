package com.example.myapplication.ui.Bio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.R;
import com.example.myapplication.ui.Posts.PostsViewModel;

public class BioFragment extends Fragment {

    private BioViewModel bioViewModel;
    private TextView bio;

    private static final String TAG = "BioFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bioViewModel =
                ViewModelProviders.of(this).get(BioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bio, container, false);

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getActivity());
        bio = root.findViewById(R.id.textView2);

        if(SPH.isOnYourProfile()){
            //Toast toast = Toast.makeText(getActivity(), "TEST... " + SPH.getBio(), Toast.LENGTH_SHORT);
            //toast.show();
            bio.setText(SPH.getBio());
        }
        else{
            bio.setText(SPH.getOtherBio());
        }




        return root;
    }
}