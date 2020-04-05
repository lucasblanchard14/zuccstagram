package com.example.myapplication.LogIn_SignUp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
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
                        goToTimeLine();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        // ERROR HANDLER
                    }
                });



        //TODO create a Profile class and object where you pass all the shared preferences
    }



    void goToTimeLine(){
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


    }


    void cleanSharedPreferences(){



    }


    //TODO Implement a function that can generate a Profile


    private void uploadImageToFireBase(){
        StorageReference imageProfile = mStorageRef.child("images/rivers.jpg");

        imageProfile.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


}
