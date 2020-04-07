package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class PostMaker extends AppCompatActivity {
    private static int REQUEST_IMAGE_CAPTURE = 0;
    private static int PICK_IMAGE = 0;
    static Uri imageUri;
    File tempImageFile;
    private ImageView imageView;
    String descriptionFinal;
    EditText description;
    Button galleryButton;
    Button cameraButton;
    Button uploadButton;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_post_maker);
        setUpUI();
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchGalleryPicture();
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchCameraPicture();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionFinal = description.getText().toString();
                uploadPost();
                uploadCloudImage();

            }
        });
    }

    private void dispatchCameraPicture() {
        REQUEST_IMAGE_CAPTURE = 1;
        PICK_IMAGE = 0;
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //file code to not working to create image uri for direct camera posting **NEED TO FIX**
        /*tempImageFile = new File(this.getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(tempImageFile);*/
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void dispatchGalleryPicture() {
        PICK_IMAGE = 1;
        REQUEST_IMAGE_CAPTURE = 0;
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    void setUpUI() {
        description = findViewById(R.id.description);
        imageView = findViewById(R.id.imageView);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        uploadButton = findViewById(R.id.uploadButton);

    }

    void uploadPost() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("Description", descriptionFinal);
        docData.put("Image", "TBD, filename goes here");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Posts").document("test")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // SUCCESS
                        // uhh do something here like return to main page
                        Log.d("PostMaker", "Post Created.");
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
    // code to upload image to cloud
    private void uploadCloudImage() {
        StorageReference postsRef = storageReference.child("images/posts.jpg");

        postsRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("PostMaker", "Image Uploaded");
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
