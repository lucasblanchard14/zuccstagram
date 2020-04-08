package com.example.myapplication.ui.Search;

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

public class SearchFragment extends AppCompatActivity {

    //private SearchViewModel searchViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SearchFragment";
    private SharedPreferenceHelper SPH;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        context = this;
        SPH = new SharedPreferenceHelper(this);

        toolbarSetUp();

        final LinearLayout ll = (LinearLayout) findViewById(R.id.searchTable);
        //
        SearchView sv = findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                UpdateList(newText, ll);
                return true;
            }
        });


    }

    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_Search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void UpdateList(String s, final LinearLayout ll){
        // Clear table
        ll.removeAllViews();

        // Don't bother if search is empty
        if(s.isEmpty())
            return;

        db.collection("Users")
                .whereGreaterThanOrEqualTo("Username", s).whereLessThanOrEqualTo("Username", s + '\uf8ff')
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData() + " | " + document.get("User"));
                                createTableRow(ll, document.get("Username").toString(), document.get("Email").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void createTableRow(LinearLayout ll, String user, final String email){
        /* Create a new row to be added. */
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trParams.bottomMargin = 10;
        tr.setLayoutParams(trParams);

        TextView tv = new TextView(this);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tv.setTextSize(18);
        tv.setText(user);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);

        // React on click
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(email);
            }
        });

        /* Add row to LinearLayout. */
        ll.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
    }

    void openProfile(final String email){
        // Are we trying to open our own profile?
        if(email.equals(SPH.getEmail())){
            SPH.switchToProfile();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
        else{
            db.collection("Users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            SharedPreferenceHelper SPH = new SharedPreferenceHelper(context);

                            String[] data = {document.get("First_Name").toString(),
                                    document.get("Last_Name").toString(),
                                    document.get("Bio").toString(),
                                    document.get("Username").toString(),
                                    document.get("Image").toString(),
                                    email
                            };

                            SPH.fetchOthersProfile(data);
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
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

    }
}