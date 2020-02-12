package com.example.zuccstagram_signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signUp_P3 extends AppCompatActivity {


    EditText editText_SecurityQuestionAnswer;
    EditText editText_SecurityQuestion;
    Button nextButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p3);



        toolbarSetUp();
        setUpUI();
        resumeLastProfileEntered();
        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                saveP3();
                goToSignUp_P4();
            }
        });



    }







    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 3 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){

        editText_SecurityQuestionAnswer = findViewById(R.id.editText_SecurityQuestionAnswer_P3);
        editText_SecurityQuestion = findViewById(R.id.editText_SecurityQuestion_P3);
        nextButton = findViewById(R.id.nextButton_P3);




    }
    protected void goToSignUp_P4()
    {
        Intent intent = new Intent(this, signUp_P4.class);
//        intent.putExtra(getString(R.string.messageKey), "Hello");
        startActivity(intent);
    }




    void saveP3(){
        String SecurityQuestion = editText_SecurityQuestion.getText().toString();
        String SecurityQuestionAnswer = editText_SecurityQuestionAnswer.getText().toString();


        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        SPH.saveProfileSettings_P3(SecurityQuestion, SecurityQuestionAnswer);

    }


    void resumeLastProfileEntered(){

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        editText_SecurityQuestion.setText(SPH.getSecurityQuestion());
        editText_SecurityQuestionAnswer.setText(SPH.getSecurityQuestionAnswer());

    }


}
