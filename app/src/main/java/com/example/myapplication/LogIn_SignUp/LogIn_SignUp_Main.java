package com.example.myapplication.LogIn_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LogIn_SignUp_Main extends AppCompatActivity {
    EditText editText_Email;
    EditText editText_Password;
    Button signUpButton;
    Button logInButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    private String TAG = "LogIn_SignUp_Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in__sign_up__main);

        context = this;

        toolbarSetUp();
        setUpUI();
        signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToSignUp_P1();
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //TODO create an intent function to go to timeline
                ValidateLogIn();

                //goToTimeLine();
            }
        });




    }



    void ValidateLogIn(){
        db.collection("Users")
                .whereEqualTo("Email", editText_Email.getText().toString())
                .whereEqualTo("Password", editText_Password.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // User found
                            if(task.getResult().size() > 0){
                                SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                                SPH.saveProfileSettings_Login(editText_Email.getText().toString(), editText_Password.getText().toString());
                                fetchProfile();
                                Log.d("MainActivity", "Login Successful.");
                            }
                            // User not found
                            else{
                                Log.d("MainActivity", "Login Failed.");
                            }
                        } else {
                            Log.d("MainActivity", "Login Failed.");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void fetchProfile(){
        db.collection("Users").document(editText_Email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SharedPreferenceHelper SPH = new SharedPreferenceHelper(context);

                        String[] data = {document.get("First_Name").toString(),
                                document.get("Last_Name").toString(),
                                document.get("Bio").toString(),
                                document.get("Password").toString(),
                                document.get("Security_Q").toString(),
                                document.get("Security_QA").toString(),
                                document.get("Username").toString(),
                                document.get("Image").toString(),
                                document.get("ImageCount").toString()
                        };
                        SPH.fetchProfile(data);
                        goToTimeLine();
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
        Toolbar toolbar = findViewById(R.id.toolbar_LogIn_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }
    void setUpUI(){

        editText_Email = findViewById(R.id.editText_Email);
        editText_Password = findViewById(R.id.editText_Password);
        signUpButton = findViewById(R.id.signUpButton);
        logInButton = findViewById(R.id.logInButton);

    }
    protected void goToSignUp_P1()
    {
        Intent intent = new Intent(this, SignUp_P1.class);
        startActivity(intent);
    }

    protected void goToTimeLine()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}
