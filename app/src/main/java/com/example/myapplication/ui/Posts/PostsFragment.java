package com.example.myapplication.ui.Posts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.DetailedPost.DetailPostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class PostsFragment extends Fragment {

    private PostsViewModel postsViewModel;
    private SharedPreferenceHelper SPH;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final String TAG = "PostsFragment";

    private String email;
    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postsViewModel =
                ViewModelProviders.of(this).get(PostsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_posts, container, false);

        TableLayout tl = (TableLayout) root.findViewById(R.id.postTable);
        SPH = new SharedPreferenceHelper(getActivity());

        makeTable(tl);

        return root;
    }

    public void makeTable(final TableLayout tl){
        // Fetch values based on who's page is being viewed
        if(SPH.isOnYourProfile()){
            email = SPH.getEmail();
            username = SPH.getUserName();
        }
        else{
            email = SPH.getOtherEmail();
            username = SPH.getOtherUserName();
        }

        db.collection("Posts")
                .orderBy("ImageID", Query.Direction.DESCENDING)
                .whereEqualTo("User", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // If there's no posts made yet, just leave
                            if(task.getResult().size() == 0)
                                return;

                            // Add scale for dp measurements
                            final float dpScale = getContext().getResources().getDisplayMetrics().density;
                            TableRow tr = null;
                            int count = 0;

                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                // New row
                                if(count % 3 == 0){
                                    tr = new TableRow(getActivity());
                                    TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                                    tr.setLayoutParams(trParams);
                                }

                                // Fetch image
                                // Get image location
                                String filename = "gs://zuccstragram.appspot.com/Images/"+document.get("User")+"/" + document.get("ImageID")+".jpg";
                                Log.d(TAG, document.getId() + " => " + document.getData() + " | " + filename);
                                StorageReference gsReference = storage.getReferenceFromUrl(filename);

                                // Create an imageview for this post
                                final ImageView iv = new ImageView(getActivity());

                                TableRow.LayoutParams ivParams = new TableRow.LayoutParams((int) (125 * dpScale + 0.5f), (int) (125 * dpScale + 0.5f),1.0f);
                                ivParams.setMargins(5,5,5,5);
                                iv.setLayoutParams(ivParams);

                                // Fetch image and set it into imageview
                                final long ONE_MEGABYTE = 1024 * 1024;
                                gsReference.getBytes(ONE_MEGABYTE*4).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(final byte[] bytes) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        iv.setImageBitmap(bmp);

                                        iv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                openPost(username, document.getId(), bytes, document.get("Description").toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                                tr.addView(iv);

                                // This is the last image on row
                                if(count % 3 == 2){
                                    Log.d(TAG, "ROW IS FULL, ADDING IT", task.getException());
                                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                }

                                count++;
                            }

                            // We ended and it's not even on the 3rd image
                            if(count % 3 != 0){
                                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void openPost(String user, String postID, byte[] image, String desc){
        Intent intent = new Intent(getActivity(), DetailPostFragment.class);
        intent.putExtra("USER", user);
        intent.putExtra("POST_ID", postID);
        intent.putExtra("IMAGE", image);
        intent.putExtra("DESC", desc);
        startActivity(intent);
    }
}