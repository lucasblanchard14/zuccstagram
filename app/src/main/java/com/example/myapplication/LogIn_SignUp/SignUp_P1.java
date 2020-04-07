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
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp_P1 extends AppCompatActivity {


    EditText editText_LastName;
    EditText editText_FirstName;
    EditText editText_Email;
    EditText editText_Confirmation_Email;
    Button nextButton;
    private static final String TAG = "SP1";



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
                if(confirmationCheckUp()){
                    saveP1();
                    goToSignUp_P2();
                }
            }
        });



    }




    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_LogIn_SignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Step 1 out of 4");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void setUpUI(){

        editText_FirstName = findViewById(R.id.editText_FirstName_P1);
        editText_LastName = findViewById(R.id.editText_LastName_P1);
        editText_Email= findViewById(R.id.editText_Email_P1);
        editText_Confirmation_Email = findViewById(R.id.editText_Email_Confirmation_P1);
        nextButton = findViewById(R.id.nextButton_P1);




    }
    protected void goToSignUp_P2()
    {
        Intent intent = new Intent(this, SignUp_P2.class);
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

    public boolean emailVerification(){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(editText_Email.getText().toString());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        result[0] = true;
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        result[0] = false;
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
        return result[0];
    }

    public boolean confirmationCheckUp(){
        SharedPreferenceHelper SPH = new SharedPreferenceHelper(this);
        if (editText_FirstName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_FirstName), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(editText_LastName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_LastName), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(editText_Email.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_Email), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(emailVerification()){
            Toast.makeText(getApplicationContext(), getString(R.string.used_Email), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!editText_Confirmation_Email.getText().toString().contentEquals(editText_Email.getText().toString())){
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_ConfirmationEmail), Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }



}
