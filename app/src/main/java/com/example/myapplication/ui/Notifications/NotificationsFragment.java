package com.example.myapplication.ui.Notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.LogIn_SignUp.SharedPreferenceHelper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private SharedPreferenceHelper SPH;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "NotificationsFragment";
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final TableLayout tl = (TableLayout) root.findViewById(R.id.notificationTable);

        context = getActivity();
        SPH = new SharedPreferenceHelper(getActivity());
        // WHEN LOGGING IN WORKS AGAIN, SWITCH "Test" WITH ACTUAL USERNAME/EMAIL
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications")
                //.orderBy("Timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("Receiver", SPH.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData() + " | " + document.get("Sender"));
                                Timestamp ts = (Timestamp) document.get("Timestamp");
                                Date date = ts.toDate();
                                createTableRow(tl, document.get("SenderName").toString(), document.get("Sender").toString(), document.get("Message").toString(), getDateTimeFromTimeStamp(ts.getSeconds(), "dd/MM/yy h:mm a"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        return root;
    }

    public static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }

    public void createTableRow(TableLayout tl, String user, final String email, String msg, String time){

        /* Create a new row to be added. */
        TableRow tr = new TableRow(getActivity());
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trParams.bottomMargin = 10;
        tr.setLayoutParams(trParams);

        // Add scale for dp measurements
        final float dpScale = getContext().getResources().getDisplayMetrics().density;

        TextView tv1 = new TextView(getActivity());
        TableRow.LayoutParams tv1Params = new TableRow.LayoutParams((int) (180 * dpScale + 0.5f), TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tv1.setTextSize(18);
        tv1.setText(user);
        tv1.setLayoutParams(tv1Params);
        tr.addView(tv1);

        TextView tv2 = new TextView(getActivity());
        TableRow.LayoutParams tv2Params = new TableRow.LayoutParams((int) (100 * dpScale + 0.5f), TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tv2.setTextSize(18);
        tv2.setText(msg);
        tv2.setLayoutParams(tv2Params);
        tr.addView(tv2);

        TextView tv3 = new TextView(getActivity());
        TableRow.LayoutParams tv3Params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tv3.setTextSize(18);
        tv3.setText(time);
        tv3.setLayoutParams(tv3Params);
        tr.addView(tv3);

        // React on click
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(email);
            }
        });

        /* Add row to TableLayout. */
        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
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