package com.example.zuccstagram_signup;


import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class signUp_P4 extends AppCompatActivity {


    private Profile profile;
    private CircleImageView profileImage;
    private final static  int PICK_IMAGE = 1;
    Button uploadButton;
    Button finishButton;
    Uri imageUri;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p4);

        profile = new Profile();

        toolbarSetUp();
        setUpUI();
        uploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadProfilePicture();
                uploadCloudImage();
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 4 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){
        profileImage = (CircleImageView) findViewById(R.id.profile_image_P4);
        uploadButton = findViewById(R.id.uploadButton_P4);
        finishButton = findViewById(R.id.finishButton_P4);

    }

    protected void generateProfile(){
        //TODO create a Profile class and object where you pass all the shared preferences
       //Intent intent = new Intent(this, signUp_P4.class);
        //startActivity(intent);
        Map<String, Object> docData = new HashMap<>();
        docData.put("First_Name", profile.getFirstName());
        docData.put("Last_Name", profile.getLastName());
        docData.put("Email", profile.getEmail());

        docData.put("Username", profile.getUserName());
        docData.put("Bio", profile.getBio());
        docData.put("Password", profile.getPassword());

        docData.put("Security_Q", profile.getSecurityQuestion());
        docData.put("Security_QA", profile.getSecurityQuestionAnswer());
        docData.put("Image", "uhh filename goes here(?)");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(profile.getEmail())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                        // SUCCESS
                        // uhh do something here like return to main page
                        Log.d("signUp_P4", "Account Created.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        // ERROR HANDLER
                    }
                });

    }


    protected void uploadProfilePicture()
    {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select picture"), PICK_IMAGE);
    }



    // CODE TO UPLOAD IMMAGE TO CLOUD
    private void uploadCloudImage() {
        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri )
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(signUp_P4.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(signUp_P4.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
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
        profile.setProfileImage(imageUri);

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
