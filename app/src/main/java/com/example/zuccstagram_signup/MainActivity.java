package com.example.zuccstagram_signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editText_Email;
    EditText editText_Password;
    Button signUpButton;
    Button logInButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                //goToTimeLine();
            }
        });




    }






    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        Intent intent = new Intent(this, signUp_P1.class);
        startActivity(intent);
    }



}
