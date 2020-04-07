package com.example.myapplication.ui.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.ui.Notifications.NotificationsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class SearchFragment extends AppCompatActivity {

    //private SearchViewModel searchViewModel;
    private static final String TAG = "SearchFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

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



    public void UpdateList(String s, final LinearLayout ll){
        // Clear table
        ll.removeAllViews();
        //

        // Don't bother if search is empty
        if(s.isEmpty())
            return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    void openProfile(String user){
        Toast toast = Toast.makeText(this, "OPEN ACCOUNT: " + user, Toast.LENGTH_SHORT);
        toast.show();
    }
}