package com.example.myapplication.ui.Following;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FollowingActivity extends AppCompatActivity {

    //private SearchViewModel searchViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FollowingFragment";
    private SharedPreferenceHelper SPH;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        context = this;
        SPH = new SharedPreferenceHelper(this);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.followTable);
        toolbarSetUp();
        UpdateList(ll);

    }

    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_Following);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Following");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void UpdateList(final LinearLayout ll){
        // Clear table
        ll.removeAllViews();

        db.collection("Following")
                .whereEqualTo("User", SPH.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData() + " | " + document.get("User"));

                                db.collection("Users")
                                        .whereEqualTo("Username", document.get("Following"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData() + " | " + document.get("User"));

                                                        String[] data = {document.get("First_Name").toString(),
                                                                document.get("Last_Name").toString(),
                                                                document.get("Bio").toString(),
                                                                document.get("Username").toString(),
                                                                document.get("Image").toString(),
                                                                document.get("Email").toString()
                                                        };

                                                        createTableRow(ll, data);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });



                                //createTableRow(ll, document.get("Following").toString(), document.get("Following").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void createTableRow(LinearLayout ll, final String[] data){
        /* Create a new row to be added. */
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trParams.bottomMargin = 10;
        tr.setLayoutParams(trParams);

        TextView tv = new TextView(this);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tv.setTextSize(18);
        tv.setText(data[3]);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);

        // React on click
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(data);
            }
        });

        /* Add row to LinearLayout. */
        ll.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
    }

    void openProfile(String[] data){
        // Are we trying to open our own profile?
        if(data[5].equals(SPH.getEmail())){
            SPH.switchToProfile();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
        else{
            SharedPreferenceHelper SPH = new SharedPreferenceHelper(context);
            SPH.fetchOthersProfile(data);
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

    }
}
