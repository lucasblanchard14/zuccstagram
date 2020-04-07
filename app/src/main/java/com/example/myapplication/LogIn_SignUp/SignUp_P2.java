package com.example.myapplication.LogIn_SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

public class SignUp_P2 extends AppCompatActivity {


    EditText editText_UserName;
    EditText editText_Bio;
    EditText editText_Password;
    EditText editText_Confirmation_Password;
    Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__p2);


        toolbarSetUp();
        setUpUI();
        resumeLastProfileEntered();
        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(confirmationCheckUp()){
                    saveP2();
                    goToSignUp_P3();
                }
            }
        });



    }


    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_LogIn_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 2 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){

        editText_UserName = findViewById(R.id.editText_UserName_P2);
        editText_Bio = findViewById(R.id.editText_Bio_P2);
        editText_Password = findViewById(R.id.editText_Password_P2);
        editText_Confirmation_Password = findViewById(R.id.editText_Password_Confirmation_P2);
        nextButton = findViewById(R.id.nextButton_P2);




    }
    protected void goToSignUp_P3()
    {
        Intent intent = new Intent(this, SignUp_P3.class);
//        intent.putExtra(getString(R.string.messageKey), "Hello");
        startActivity(intent);
    }



    void saveP2(){
        String UserName = editText_UserName.getText().toString();
        String Bio = editText_Bio.getText().toString();
        String Password = editText_Password.getText().toString();
        String ConfirmationPassword = editText_Confirmation_Password.getText().toString();

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        SPH.saveProfileSettings_P2(UserName, Bio, Password, ConfirmationPassword);

    }


    void resumeLastProfileEntered(){

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);

        editText_UserName.setText(SPH.getUserName());
        editText_Bio.setText(SPH.getBio());
        editText_Password.setText(SPH.getPassword());
        editText_Confirmation_Password.setText(SPH.getConfirmationPassword());


    }

    public boolean confirmationCheckUp(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        if (editText_UserName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_Username), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(editText_Bio.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_Bio), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(editText_Password.getText().toString().isEmpty() || editText_Password.getText().toString().length() < 8 || editText_Password.getText().toString().contentEquals(SPH.getPassword())){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_Password), Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if(!editText_Password.getText().toString().contentEquals(editText_Confirmation_Password.getText().toString()) || editText_Confirmation_Password.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_ConfirmationPassword), Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }



}
