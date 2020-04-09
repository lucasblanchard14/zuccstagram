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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    //private String TAG = "LogIn_SignUp_Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in__sign_up__main);
        mAuth = FirebaseAuth.getInstance();

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
      /*db.collection("Users")
                .whereEqualTo("Email", editText_Email.getText().toString())
                .whereEqualTo("Password", editText_Password.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {*/ 

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
                                fetchProfile();
                                Log.d("MainActivity", "Login Successful.");

                                // THIS IS WHEN WE JUMP TO
                                //goToTimeLine();

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
                                //document.get("Image").toString(),
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
        // getSupportActionBar().setTitle("");

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

    /* TEST FUNCTIONS*/

    public boolean validateTestInput(String  s){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(s);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        SPH.testVerification(true);
                        Log.d(TAG, "Document exists!    " + true);
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        SPH.testVerification(false);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }


    public boolean validateTestFirst_Name(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("First_Name").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }


    public boolean validateTestLast_Name(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Last_Name").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestBio(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Bio").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestPassword(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Password").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestSecurity_Q(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Security_Q").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestSecurity_QA(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Security_QA").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestUsername(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Username").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }

    public boolean validateTestImage(String  email , final String keyValue){
        final boolean[] result = new boolean[1];
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
                    if (document.exists()) {
                        if(keyValue.equals(document.get("Image").toString())){
                            SPH.testVerification(true);
                            Log.d(TAG, "Value exists!    " + true);
                        }
                        else{
                            Log.d(TAG, "value does not exist!");
                            SPH.testVerification(false);
                        }
                    } else {

                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        SharedPreferenceHelper SPH = new SharedPreferenceHelper(getApplicationContext());
        return Boolean.parseBoolean(SPH.getTestVerification());
    }


    public void testEmailFunction(){

        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";


        testArr[1][0] = "True";
        testArr[1][1] = "False";
        testArr[1][2] = "True";
        testArr[1][3] = "False";
        testArr[1][4] = "False";
        testArr[1][5] = "False";
        testArr[1][6] = "False";
        testArr[1][7] = "False";
        testArr[1][8] = "False";
        testArr[1][9] = "False";


        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            testArr[2][i]=Boolean.toString(validateTestInput(userEmail));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[1][i].equals(testArr[2][i])) {
                testArr[3][i] = "True";
            }
            else{
                testArr[3][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[3][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with email Test Function , occured in row 4 column"+i);
                break;
            }
        }


    }


    public void testFirst_Name(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "Test";
        testArr[1][1] = "John";
        testArr[1][2] = "Zucc";
        testArr[1][3] = "";
        testArr[1][4] = "!@@###@#";
        testArr[1][5] = "Mary";
        testArr[1][6] = "Thomas";
        testArr[1][7] = "Boris";
        testArr[1][8] = "432231";
        testArr[1][9] = "Tony";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userFirstName = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestFirst_Name(userEmail,userFirstName));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with FirstName Test Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testBio(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "I Test Stuff";
        testArr[1][1] = "My name is John";
        testArr[1][2] = "I test stuff.";
        testArr[1][3] = "";
        testArr[1][4] = "!@@###@#";
        testArr[1][5] = "My name is Mary";
        testArr[1][6] = "My name is Thomas";
        testArr[1][7] = "My name is Boris";
        testArr[1][8] = "432231";
        testArr[1][9] = "Genius, billionaire, playboy, philanthropist.";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userBio = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestBio(userEmail,userBio));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with Bio Test Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testLast_Name(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "Test";
        testArr[1][1] = "Johnson";
        testArr[1][2] = "Gram";
        testArr[1][3] = "";
        testArr[1][4] = "#&*$(#$";
        testArr[1][5] = "Maryson";
        testArr[1][6] = "Thomasson";
        testArr[1][7] = "Borisson";
        testArr[1][8] = "221321";
        testArr[1][9] = "Stark";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userLastName = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestLast_Name(userEmail,userLastName));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with LastName Test Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testPassword(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "test";
        testArr[1][1] = "Johnson123";
        testArr[1][2] = "zuccstagram";
        testArr[1][3] = "";
        testArr[1][4] = "$%&&*";
        testArr[1][5] = "Maryson123";
        testArr[1][6] = "Thomasson123";
        testArr[1][7] = "Borisson123";
        testArr[1][8] = "342342";
        testArr[1][9] = "Pepper";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userPassword = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestPassword(userEmail,userPassword));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with PasswordTest Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testSecurity_Q(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "What do I do?";
        testArr[1][1] = "What is my First name";
        testArr[1][2] = "What do I do?";
        testArr[1][3] = "";
        testArr[1][4] = "$%&&*";
        testArr[1][5] = "What is my bio";
        testArr[1][6] = "What is my age";
        testArr[1][7] = "What is my age";
        testArr[1][8] = "342342";
        testArr[1][9] = "Where is Thanos";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userSecurity_Q = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestSecurity_Q(userEmail,userSecurity_Q));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with Security_QTest Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testSecurity_QA(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "Test?";
        testArr[1][1] = "John";
        testArr[1][2] = "Test";
        testArr[1][3] = "";
        testArr[1][4] = "$%^&*(";
        testArr[1][5] = "My name is Mary";
        testArr[1][6] = "27";
        testArr[1][7] = "72";
        testArr[1][8] = "342342";
        testArr[1][9] = "Asgard";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userSecurity_QA = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestSecurity_QA(userEmail,userSecurity_QA));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with Security_QATest Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testUsername(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "Tester";
        testArr[1][1] = "User John";
        testArr[1][2] = "Zmode";
        testArr[1][3] = "";
        testArr[1][4] = "$%^&*(";
        testArr[1][5] = "User Mary";
        testArr[1][6] = "User Thomas";
        testArr[1][7] = "User Boris";
        testArr[1][8] = "213141";
        testArr[1][9] = "User Tony";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "True";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userUsername = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestUsername(userEmail,userUsername));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with Username Function , occured in row 4 column"+i);
                break;
            }
        }

    }

    public void testImage(){
        String testArr[][] = new String[10][3];

        testArr[0][0] = "test@test.com";
        testArr[0][1] = "John123.gmail.com";
        testArr[0][2] = "zuccstagramtest@gmail.com";
        testArr[0][3] = "";
        testArr[0][4] = "!@!$%^&*(";
        testArr[0][5] = "Mary345@gmail.com";
        testArr[0][6] = "Thomas@yahoocom";
        testArr[0][7] = "BorisB!!!!";
        testArr[0][8] = "T";
        testArr[0][9] = "TonyStark@starkindustries.com";

        testArr[1][0] = "08c77b8b-e4a3-4145-ae00-40c1d33b0275";
        testArr[1][1] = "08c77b8b-e4a3-4145-ae00-40c1d33b0271";
        testArr[1][2] = "";
        testArr[1][3] = "";
        testArr[1][4] = "$%^&*(";
        testArr[1][5] = "08c77b8b-e4a3-4145-ae00-40c1d33b0273";
        testArr[1][6] = "08c77b8b-e4a3-4145-ae00-40c1d33b02754";
        testArr[1][7] = "08c77b8b-e4a3-4145-ae00-40c1d33b0276";
        testArr[1][8] = "213141";
        testArr[1][9] = "08c77b8b-e4a3-4145-ae00-40c1d33b0277";

        testArr[2][0] = "True";
        testArr[2][1] = "False";
        testArr[2][2] = "False";
        testArr[2][3] = "False";
        testArr[2][4] = "False";
        testArr[2][5] = "False";
        testArr[2][6] = "False";
        testArr[2][7] = "False";
        testArr[2][8] = "False";
        testArr[2][9] = "False";

        for (int i = 0 ; i<testArr.length;i++){
            String userEmail = testArr[0][i];
            String userImage = testArr[1][i];
            testArr[3][i]=Boolean.toString(validateTestImage(userEmail,userImage));
        }

        for (int i = 0 ; i<testArr.length;i++){

            if(testArr[2][i].equals(testArr[3][i])) {
                testArr[4][i] = "True";
            }
            else{
                testArr[4][i] = "False";
            }
        }

        Boolean Bug = true;

        for (int i = 0 ; i<testArr.length;i++){
            if(testArr[4][i].equals("False")){
                Bug = false;
                Log.d(TAG, "There is a problem with Image Function , occured in row 4 column"+i);
                break;
            }
        }

    }

}
