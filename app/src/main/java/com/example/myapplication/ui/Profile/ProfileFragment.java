package com.example.myapplication.ui.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private static final String TAG = "ProfileFragment";
    private FirebaseFirestore db;

    private boolean canFollow;
    private Button fb;

    // PLACEHOLDERS
    private String user = "test@test.com";
    private String visiting = "account1@test.com";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();

        TextView username = root.findViewById(R.id.usernameTextView);
        username.setText("Account1");

        fb = root.findViewById(R.id.followButton);

        // Don't show button if this is your own page
        if(user == visiting)
            fb.setVisibility(View.GONE);
        else
            CheckIfFollowed();

        return root;
    }

    public void Follow(){
        // Add to DB
        Map<String, Object> follow = new HashMap<>();
        follow.put("User", user);
        follow.put("Following", visiting);

        db.collection("Followers").document(user+"|"+visiting)
                .set(follow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Send Notification
                        Timestamp ts = new Timestamp(new Date());
                        Map<String, Object> notice = new HashMap<>();
                        notice.put("Sender", user);
                        notice.put("Receiver", visiting);
                        notice.put("Message", "Followed");
                        notice.put("Timestamp", ts);

                        db.collection("Notifications").document(user+"|"+visiting+"|"+ts.getSeconds())
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

                        Toast toast = Toast.makeText(getActivity(), "Following " + visiting, Toast.LENGTH_SHORT);
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
        db.collection("Followers").document(user+"|"+visiting)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                        Toast toast = Toast.makeText(getActivity(), "Unfollowed " + visiting, Toast.LENGTH_SHORT);
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
        // WHEN LOGGING IN WORKS AGAIN, SWITCH "Test" WITH ACTUAL USERNAME/EMAIL

        db.collection("Followers")
                .whereEqualTo("Following", visiting)
                .whereEqualTo("User", user)
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