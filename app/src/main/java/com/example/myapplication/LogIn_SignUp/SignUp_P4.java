package com.example.myapplication.LogIn_SignUp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;





public class SignUp_P4 extends AppCompatActivity {

    Profile profile;
    private CircleImageView profileImage;
    private final static  int PICK_IMAGE = 1;
    Button uploadButton;
    Button finishButton;
    Uri imageUri;
    private StorageReference mStorageRef;
    private FirebaseStorage storage = FirebaseStorage.getInstance();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p4);

        mStorageRef = FirebaseStorage.getInstance().getReference();
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
                confirmationCheckUp();
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

        Map<String, Object> docData = new HashMap<>();
        docData.put("First_Name", profile.getFirstName());
        docData.put("Last_Name", profile.getLastName());
        docData.put("Email", profile.getEmail());

        docData.put("Username", profile.getUserName());
        docData.put("Bio", profile.getBio());
        docData.put("Password", profile.getPassword());

        docData.put("Security_Q", profile.getSecurityQuestion());
        docData.put("Security_QA", profile.getSecurityQuestionAnswer());
        docData.put("ImageCount", "0");

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



    void goToTimeLine(){
        Intent intent = new Intent(this, LogIn_SignUp_Main.class);
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


    //TODO Implement a function that can generate a Profile


    private void uploadImage(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference


            String filename = "gs://zuccstragram.appspot.com/Images/" + SPH.getEmail() + "/" + "Profile_Picture";
            StorageReference gsReference = storage.getReferenceFromUrl(filename);


            // adding listeners on upload
            // or failure of image
            gsReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {

                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress
                                    = (100.0
                                    * taskSnapshot.getBytesTransferred()
                                    / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage(
                                    "Uploaded "
                                            + (int)progress + "%");
                        }
                    });
        }
    }


    public  void updateProfile(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        SPH.fetchProfile();
    }

    public void confirmationCheckUp(){
        if(profileImage.getDrawable() == null){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            generateProfile();
                            uploadImage();
                            updateProfile();
                            goToTimeLine();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No profile picture has been chosen. Would you still like to proceed ?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }



}
