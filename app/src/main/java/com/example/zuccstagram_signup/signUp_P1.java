package com.example.zuccstagram_signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signUp_P1 extends AppCompatActivity {


    EditText editText_LastName;
    EditText editText_FirstName;
    EditText editText_Email;
    EditText editText_Confirmation_Email;
    Button nextButton;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p1);

        toolbarSetUp();
        setUpUI();
        resumeLastProfileEntered();

        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                saveP1();
                goToSignUp_P2();
            }
        });





    }





    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 1 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){

        editText_FirstName = findViewById(R.id.editText_SecurityQuestion_P3);
        editText_LastName = findViewById(R.id.editText_SecurityQuestionAnswer_P3);
        editText_Email= findViewById(R.id.editText_Password_P2);
        editText_Confirmation_Email = findViewById(R.id.Password_Confirmation_P2);
        nextButton = findViewById(R.id.nextButton_P2);




    }
    protected void goToSignUp_P2()
    {
        Intent intent = new Intent(this, signUp_P2.class);
        startActivity(intent);
    }


    void saveP1(){
        String FirstName = editText_FirstName.getText().toString();
        String LastName = editText_LastName.getText().toString();
        String Email = editText_Email.getText().toString();
        String ConfirmationEmail = editText_Confirmation_Email.getText().toString();

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        SPH.saveProfileSettings_P1(FirstName, LastName, Email, ConfirmationEmail);

    }


    void resumeLastProfileEntered(){

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        editText_FirstName.setText(SPH.getFirstName());
        editText_LastName.setText(SPH.getLastName());
        editText_Email.setText(SPH.getEmail());
        editText_Confirmation_Email.setText(SPH.getEmailConfirmation());


    }


}
