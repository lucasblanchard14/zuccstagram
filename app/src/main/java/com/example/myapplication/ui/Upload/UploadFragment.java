package com.example.myapplication.ui.Upload;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;
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
    String currentPhotoPath;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "UploadFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();

        uploadViewModel =
                ViewModelProviders.of(this).get(UploadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        final Toast uploadFail = Toast.makeText(getActivity().getApplicationContext(),
                "Error, select a picture before uploading",
                Toast.LENGTH_LONG);
        final Toast uploadSuccess = Toast.makeText(getActivity().getApplicationContext(),
                "Post uploaded",
                Toast.LENGTH_LONG);
        description = root.findViewById(R.id.description);
        imageView = root.findViewById(R.id.imageView);
        cameraButton = root.findViewById(R.id.cameraButton);
        galleryButton = root.findViewById(R.id.galleryButton);
        uploadButton = root.findViewById(R.id.uploadButton);
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
                if(imageUri == null || description.getText().toString() == null){
                    uploadFail.show();
                    return;
                }
                descriptionFinal = description.getText().toString();
                uploadPost();
                uploadCloudImage();
                uploadSuccess.show();
            }
        });


        return root;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchCameraPicture() {
        REQUEST_IMAGE_CAPTURE = 1;
        PICK_IMAGE = 0;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                imageUri =photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK) {//not sure
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);// not sure if this will work
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {// not sure
            Bundle extras = data.getExtras();
            imageView.setImageURI(imageUri);
        }
    }

    void uploadPost() {
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getActivity().getApplicationContext());
        SPH.fetchProfile();
        final Timestamp ts = new Timestamp(new Date());
        Map<String, Object> docData = new HashMap<>();
        docData.put("Description", descriptionFinal);
        docData.put("ImageID", SPH.getImageCount());
        docData.put("User", SPH.getEmail());
        docData.put("Timestamp", ts);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Posts").document(SPH.getEmail() + "_" + SPH.getImageCount())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // SUCCESS
                        // uhh do something here like return to main page
                        Log.d("PostMaker", "Post Created.");

                        // Send Notifications to Followers about the new post
                        SendNotifications(ts);

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
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getActivity().getApplicationContext());
        SPH.fetchProfile();
        StorageReference postsRef = storageReference.child("Images/" + SPH.getEmail() + "/" + SPH.getImageCount() + ".jpg");
        //following increments the Image in database
        DocumentReference noteRef = db.collection("Users").document(SPH.getEmail());
        int imgCnt;
        if (!TextUtils.isEmpty(SPH.getImageCount()) && TextUtils.isDigitsOnly(SPH.getImageCount())) {
            imgCnt= Integer.parseInt(SPH.getImageCount());
        } else {
            imgCnt = 0;
        }
        imgCnt++;
        noteRef.update("ImageCount", Integer.toString(imgCnt));

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

    private void SendNotifications(final Timestamp ts){
        SharedPreferenceHelper SPH1 = new SharedPreferenceHelper(getActivity());
        // Check all your followers
        db.collection("Following")
                .whereEqualTo("User", SPH1.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SharedPreferenceHelper SPH2 = new SharedPreferenceHelper(getActivity());
                                Map<String, Object> notice = new HashMap<>();
                                notice.put("SenderName", SPH2.getUserName());
                                notice.put("Sender", SPH2.getEmail());
                                notice.put("Receiver", document.get("Following"));
                                notice.put("Message", "New Post");
                                notice.put("Timestamp", ts);

                                db.collection("Notifications").document(SPH2.getEmail()+"|"+SPH2.getOtherEmail()+"|"+ts.getSeconds())
                                        .set(notice)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}