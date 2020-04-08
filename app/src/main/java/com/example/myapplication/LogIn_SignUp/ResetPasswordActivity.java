package com.example.myapplication.LogIn_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    Button button_sendEmail;
    EditText editText_Email;
    FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        toolbarSetUp();
        setUpUI();
        auth = FirebaseAuth.getInstance();

        button_sendEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String userEmail = editText_Email.getText().toString();
                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPasswordActivity.this, "Please insert an email", Toast.LENGTH_SHORT).show();
                }
                else{

                    auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>(){

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this,"", Toast.LENGTH_SHORT);
                                startActivity(new Intent(ResetPasswordActivity.this, LogIn_SignUp_Main.class));
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error Occurred" + message, Toast.LENGTH_SHORT);
                            }

                        }
                    });
                }
            }
        });


    }






    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_LogIn_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
    }
    void setUpUI(){
        editText_Email = findViewById(R.id.editText_Email_RP);
        button_sendEmail = findViewById(R.id.button_SendResettingPassword);
    }




}