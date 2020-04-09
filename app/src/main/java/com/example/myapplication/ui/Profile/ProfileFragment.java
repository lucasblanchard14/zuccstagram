package com.example.myapplication.ui.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.R;
import com.example.myapplication.SectionsPageAdapter;
import com.example.myapplication.ui.Bio.BioFragment;
import com.example.myapplication.ui.Posts.PostsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private static final String TAG = "ProfileFragment";
    private FirebaseFirestore db;
    private SharedPreferenceHelper SPH;

    private boolean canFollow;
    private Button fb;

    private TextView username;
    private ImageView pfp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();

        ////

        SPH = new SharedPreferenceHelper(getActivity());
        username = root.findViewById(R.id.usernameTextView);
        pfp = root.findViewById(R.id.imageView);
        fb = root.findViewById(R.id.followButton);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    public void loadProfile(){
        // Is this our profile page?
        if(SPH.isOnYourProfile()){
            buildProfile(SPH.getUserName(), SPH.getEmail());
            fb.setVisibility(View.GONE); // Don't show follow button if this is your own page
        }
        else{

            buildProfile(SPH.getOtherUserName(), SPH.getOtherEmail());
            fb.setVisibility(View.VISIBLE);
        }
    }

    public void buildProfile(String user, String email){
        // Username
        username.setText(user);

        //Toast toast = Toast.makeText(getActivity(), user + "|" + img, Toast.LENGTH_SHORT);
        //toast.show();

        // Profile Pic
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Get image location
        String filename = "gs://zuccstragram.appspot.com/Images/"+email+"/Profile_Picture";
        StorageReference gsReference = storage.getReferenceFromUrl(filename);


        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE*4).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pfp.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        if(!SPH.isOnYourProfile())
            CheckIfFollowed();
    }

    public void Follow(){
        // Add to DB
        Map<String, Object> follow = new HashMap<>();
        follow.put("User", SPH.getEmail());
        follow.put("Following", SPH.getOtherEmail());

        db.collection("Followers").document(SPH.getEmail()+"|"+SPH.getOtherEmail())
                .set(follow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Send Notification
                        Timestamp ts = new Timestamp(new Date());
                        Map<String, Object> notice = new HashMap<>();
                        notice.put("SenderName", SPH.getUserName());
                        notice.put("Sender", SPH.getEmail());
                        notice.put("Receiver", SPH.getOtherEmail());
                        notice.put("Message", "Followed");
                        notice.put("Timestamp", ts);

                        db.collection("Notifications").document(SPH.getEmail()+"|"+SPH.getOtherEmail()+"|"+ts.getSeconds())
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

                        Toast toast = Toast.makeText(getActivity(), "Following " + SPH.getOtherUserName(), Toast.LENGTH_SHORT);
                        toast.show();

                        // Ensures changes occurred
                        CheckIfFollowed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }

    public void Unfollow(){
        db.collection("Followers").document(SPH.getEmail()+"|"+SPH.getOtherEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                        Toast toast = Toast.makeText(getActivity(), "Unfollowed " + SPH.getOtherUserName(), Toast.LENGTH_SHORT);
                        toast.show();

                        // Ensures changes occurred
                        CheckIfFollowed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void CheckIfFollowed(){
        db.collection("Followers")
                .whereEqualTo("Following", SPH.getOtherEmail())
                .whereEqualTo("User", SPH.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "CANNOT FOLLOW USER");
                                    canFollow = false;
                                    fb.setText("UNFOLLOW");
                                    fb.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Unfollow();
                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "CAN FOLLOW USER");
                                canFollow = true;
                                fb.setText("FOLLOW");
                                fb.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Follow();
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        mSectionsPageAdapter = new SectionsPageAdapter(getChildFragmentManager());
        mViewPager = getView().findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = getView().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new PostsFragment(), "Posts");
        adapter.addFragment(new BioFragment(), "Bio");
        viewPager.setAdapter(adapter);
    }

}