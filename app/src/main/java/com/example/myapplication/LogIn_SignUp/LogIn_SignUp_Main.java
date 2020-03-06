package com.example.myapplication.LogIn_SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;

public class LogIn_SignUp_Main extends AppCompatActivity {
    EditText editText_Email;
    EditText editText_Password;
    Button signUpButton;
    Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in__sign_up__main);

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
                //ValidateLogIn();

                //goToTimeLine();
            }
        });




    }


    /*
    void ValidateLogIn(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                Log.d("MainActivity", "Login Successful.");
								// THIS IS WHEN WE JUMP TO
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
     */



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
}
