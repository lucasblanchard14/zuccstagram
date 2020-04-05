package com.example.myapplication.ui.Upload;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.PostMaker;

public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;
    Button upload;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {





        uploadViewModel =
                ViewModelProviders.of(this).get(UploadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);

        upload = root.findViewById(R.id.buttonUplaod);
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostMaker.class);
                startActivity(intent);
            }
        });


        return root;
    }
}