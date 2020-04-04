package com.example.myapplication.ui.Settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;



public class EditProfile extends AppCompatActivity {

    EditText editText_LastName;
    EditText editText_FirstName;

    EditText editText_UserName;
    EditText editText_Bio;
    EditText editText_Password;
    EditText editText_Confirmation_Password;

    EditText editText_SecurityQuestionAnswer;
    EditText editText_SecurityQuestion;
    ImageView profilePicture;

    private CircleImageView profileImage;
    private final static  int PICK_IMAGE = 1;
    Button uploadButton;
    Button savedButton;
    Uri imageUri;
    private StorageReference mStorageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Edit Profile";
    private DatabaseReference ref;
    StorageReference storageReference;


    //Profile Key
    private static final String KEY_FirstName = "First_Name";
    private static final String KEY_LastName = "Last_Name";
    private static final String KEY_Bio = "Bio";
    private static final String KEY_Security_Q = "Security_Q";
    private static final String KEY_Security_QA = "Security_QA";
    private static final String KEY_Username = "Username";
    private static final String KEY_Email = "Image";
    private static final String KEY_ProfileImage = "Image";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    //private String tempProfilePictureID = UUID.randomUUID().toString();
    private String currentProfilePictureID;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        toolbarSetUp();
        setUpUI();

        fetchProfile();


        uploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadProfilePicture();
            }
        });
        savedButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadImage();
                updateProfile();
                //deleteCurrentProfilePicture();
            }
        });



    }

    public void updateProfile(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        //TODO can optimize the uploading the algorithm
        DocumentReference noteRef = db.collection("Users").document(SPH.getEmail());
        noteRef.update(KEY_FirstName, editText_FirstName.getText().toString());
        noteRef.update(KEY_LastName, editText_LastName.getText().toString());
        noteRef.update(KEY_Bio, editText_Bio.getText().toString());
        noteRef.update(KEY_Username, editText_UserName.getText().toString());
        noteRef.update(KEY_Security_Q, editText_SecurityQuestion.getText().toString());
        noteRef.update(KEY_Security_QA, editText_SecurityQuestionAnswer.getText().toString());
        noteRef.update(KEY_ProfileImage, currentProfilePictureID);



    }


    public void fetchProfile(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);



        db.collection("Users").document(SPH.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        // Get image location

                        String filename = "gs://zuccstragram.appspot.com/Images/User_Profile/" + document.get(KEY_Email).toString() + document.get(KEY_ProfileImage).toString();
                        StorageReference gsReference = storage.getReferenceFromUrl(filename);


                        final long ONE_MEGABYTE = 1024 * 1024;
                        gsReference.getBytes(ONE_MEGABYTE*4).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileImage.setImageBitmap(bmp);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });



                        editText_FirstName.setText(document.get("First_Name").toString());
                        editText_LastName.setText(document.get("Last_Name").toString());
                        editText_Bio.setText(document.get("Bio").toString());
                        editText_Password.setText(document.get("Password").toString());
                        editText_SecurityQuestion.setText(document.get("Security_Q").toString());
                        editText_SecurityQuestionAnswer.setText(document.get("Security_QA").toString());
                        editText_UserName.setText(document.get("Username").toString());
                        currentProfilePictureID = document.get("Image").toString();

                    }
                    else{
                        Log.d(TAG, "No documents: ");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_Settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @SuppressLint("WrongViewCast")
    void setUpUI(){

        editText_FirstName = findViewById(R.id.editText_FirstName_EP);
        editText_LastName = findViewById(R.id.editText_LastName_EP);

        editText_UserName = findViewById(R.id.editText_UserName_EP);
        editText_Bio = findViewById(R.id.editText_Bio_EP);
        editText_Password = findViewById(R.id.editText_Password_EP);
        editText_Confirmation_Password = findViewById(R.id.editText_Password_Confirmation_EP);

        editText_SecurityQuestionAnswer = findViewById(R.id.editText_SecurityQuestionAnswer_EP);
        editText_SecurityQuestion = findViewById(R.id.editText_SecurityQuestion_EP);

        profileImage = findViewById(R.id.profile_image_EP);
        uploadButton = findViewById(R.id.uploadButton_EP);
        savedButton = findViewById(R.id.saveButton_EP);


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

    private void uploadImage()
    {
        storageReference = FirebaseStorage.getInstance().getReference();
        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference


            String filename = "gs://zuccstragram.appspot.com/Images/User_Profile/" + currentProfilePictureID;
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

    public void deleteCurrentProfilePicture(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        //TODO can optimize the uploading the algorithm
        String filename = "gs://zuccstragram.appspot.com/Images/User_Profile/" + currentProfilePictureID;
        StorageReference gsReference = storage.getReferenceFromUrl(filename);
        gsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });
    }






}
