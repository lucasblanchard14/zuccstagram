package com.example.myapplication.LogIn_SignUp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class SignUp_P4 extends AppCompatActivity {

    Profile profile;
    private CircleImageView profileImage;
    private final static  int PICK_IMAGE = 1;
    Button uploadButton;
    Button finishButton;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p4);

        toolbarSetUp();
        setUpUI();
        uploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadProfilePicture();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                saveProfile();
                cleanSharedPreferences();
                generateProfile();
            }
        });

    }






    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_LogIn_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 4 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){
        profileImage = findViewById(R.id.profile_image_P4);
        uploadButton = findViewById(R.id.uploadButton_P4);
        finishButton = findViewById(R.id.finishButton_P4);

    }

    protected void generateProfile(){
        //TODO create a Profile class and object where you pass all the shared preferences
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    protected void uploadProfilePicture()
    {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select picture"), PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }


    void saveProfile(){

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        profile = new Profile();

        //P1
        profile.setFirstName(SPH.getFirstName());
        profile.setLastName(SPH.getLastName());
        profile.setEmail(SPH.getEmail());

        //P2
        profile.setUserName(SPH.getUserName());
        profile.setBio(SPH.getBio());
        profile.setPassword(SPH.getPassword());

        //P3
        profile.setSecurityQuestion(SPH.getSecurityQuestion());
        profile.setSecurityQuestionAnswer(SPH.getSecurityQuestionAnswer());

        //P4
        //profile.setProfileImage(imageUri);

    }


    void cleanSharedPreferences(){

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        SPH.saveProfileSettings_P1(null,null,null,null);
        SPH.saveProfileSettings_P2(null,null,null,null);
        SPH.saveProfileSettings_P3(null,null);
        imageUri = null;

    }


    //TODO Implement a function that can generate a Profile


}