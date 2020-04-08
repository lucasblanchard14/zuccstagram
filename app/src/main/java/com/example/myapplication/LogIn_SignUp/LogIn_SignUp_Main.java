package com.example.myapplication.LogIn_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LogIn_SignUp_Main extends AppCompatActivity {
    EditText editText_Email;
    EditText editText_Password;
    Button signUpButton;
    Button logInButton;
    TextView forgotPassword;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private static final String TAG = "SignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in__sign_up__main);
        mAuth = FirebaseAuth.getInstance();

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
                ValidateLogIn();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToForgotPassword();
            }
        });
    }



    void ValidateLogIn(){

        mAuth.signInWithEmailAndPassword(editText_Email.getText().toString(), editText_Password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            if(mAuth.getCurrentUser().isEmailVerified()){
                                Log.d(TAG, "signInWithEmail:success");
                                SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                                SPH.saveProfileSettings_Login(editText_Email.getText().toString(), editText_Password.getText().toString());
                                SPH.fetchProfile();
                                Log.d("MainActivity", "Login Successful.");
                                // THIS IS WHEN WE JUMP TO
                                goToTimeLine();
                            }
                            else{
                                Toast.makeText(LogIn_SignUp_Main.this, "This account is still pending",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogIn_SignUp_Main.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // ...
                        }

                        // ...
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
        forgotPassword = findViewById(R.id.textView_Forgot_Password);
        logInButton = findViewById(R.id.logInButton);

    }
    public void goToTimeLine()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void goToSignUp_P1()
    {
        Intent intent = new Intent(this, SignUp_P1.class);
        startActivity(intent);
    }

    public void goToForgotPassword()
    {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }



}
