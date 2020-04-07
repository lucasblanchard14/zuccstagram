package com.example.myapplication.LogIn_SignUp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SharedPreferenceHelper {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences_Profile;        // This is the current user's data
    private SharedPreferences sharedPreferences_Other;         // This is the data of the account you're visiting
    private Profile profile;
    private static final String TAG = "__SPH";

    private Context context;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences_Profile = context.getSharedPreferences(context.getString(R.string.sharedPreferences_Profile), Context.MODE_PRIVATE);
        sharedPreferences_Other = context.getSharedPreferences(context.getString(R.string.sharedPreferences_Other), Context.MODE_PRIVATE);
        this.context = context;
    }


    public void saveProfileSettings_Login(String Email, String Password) {
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putString("editText_Email", Email);
        editor.putString("editText_Password", Password);
        editor.commit();

    }

    public void saveProfileSettings_P1(String FirstName, String LastName, String Email, String ConfirmationEmail) {
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putString("editText_FirstName", FirstName);
        editor.putString("editText_LastName", LastName);
        editor.putString("editText_Email", Email);
        editor.putString("editText_ConfirmationEmail", ConfirmationEmail);
        editor.commit();

    }

    public void saveProfileSettings_P2(String UserName, String Bio, String Password, String ConfirmationPassword) {
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putString("editText_UserName", UserName);
        editor.putString("editText_Bio", Bio);
        editor.putString("editText_Password", Password);
        editor.putString("editText_ConfirmationPassword", ConfirmationPassword);
        editor.commit();

    }

    public void saveProfileSettings_P3(String SecurityQuestion, String SecurityQuestionAnswer) {
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putString("editText_SecurityQuestion", SecurityQuestion);
        editor.putString("editText_SecurityQuestionAnswer", SecurityQuestionAnswer);
        editor.commit();

    }

    public void saveProfileSettings_P4(String FullName, String Age, String StudentID) {
        //TODO find a way to save a picture, temporarily

    }

    public void fetchProfile(){
        db.collection("Users").document(getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
                        editor.putString("editText_FirstName", document.get("First_Name").toString());
                        editor.putString("editText_LastName", document.get("Last_Name").toString());
                        editor.putString("editText_Bio", document.get("Bio").toString());
                        editor.putString("editText_Password", document.get("Password").toString());
                        editor.putString("editText_SecurityQuestion", document.get("Security_Q").toString());
                        editor.putString("editText_SecurityQuestionAnswer", document.get("Security_QA").toString());
                        editor.putString("editText_UserName", document.get("Username").toString());
                        editor.putString("currentProfilePictureID", document.get("Image").toString());
                        editor.putString("ImageCount", document.get("ImageCount").toString());
                        editor.commit();


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

    public void fetchProfile(String[] data){
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putString("editText_FirstName", data[0]);
        editor.putString("editText_LastName", data[1]);
        editor.putString("editText_Bio", data[2]);
        editor.putString("editText_Password", data[3]);
        editor.putString("editText_SecurityQuestion", data[4]);
        editor.putString("editText_SecurityQuestionAnswer", data[5]);
        editor.putString("editText_UserName", data[6]);
        editor.putString("currentProfilePictureID", data[7]);
        //editor.putString("ImageCount", document.get("ImageCount").toString());
        editor.commit();

        switchToProfile();

    }

    //Get function P1

    public String getFirstName() {
        return sharedPreferences_Profile.getString("editText_FirstName", null);
    }

    public String getLastName() {
        return sharedPreferences_Profile.getString("editText_LastName", null);
    }

    public String getEmail() {
        return sharedPreferences_Profile.getString("editText_Email", null);
    }

    public String getEmailConfirmation() {
        return sharedPreferences_Profile.getString("editText_ConfirmationEmail", null);
    }


    //Get function P3

    public String getSecurityQuestion() {
        return sharedPreferences_Profile.getString("editText_SecurityQuestion", null);
    }

    public String getSecurityQuestionAnswer() {
        return sharedPreferences_Profile.getString("editText_SecurityQuestionAnswer", null);
    }


    //Get function P3

    public String getUserName() {
        return sharedPreferences_Profile.getString("editText_UserName", null);
    }

    public String getBio() {
        return sharedPreferences_Profile.getString("editText_Bio", null);
    }

    public String getPassword() {
        return sharedPreferences_Profile.getString("editText_Password", null);
    }

    public String getConfirmationPassword() {
        return sharedPreferences_Profile.getString("editText_ConfirmationPassword", null);
    }


    public String getImageCount() {
        return sharedPreferences_Profile.getString("ImageCount", null);
    }

    public String getCurrentProfilePictureID() {
        return sharedPreferences_Profile.getString("currentProfilePictureID", null);
    }


    //Get function P2
    public String getE() {
        return sharedPreferences_Profile.getString("editText_Age", null);
    }
    //TODO a setter for Profile name and a class profile

    void saveProfile() {


    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }




    // SECTION FOR FETCHING AND STORING OTHER USER'S INFORMATION
    public boolean isOnYourProfile(){
        return sharedPreferences_Profile.getBoolean("On_Your_Profile", true);
    }

    public void switchToProfile(){
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putBoolean("On_Your_Profile", true);
        editor.commit();
    }

    public void switchToOther(){
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        editor.putBoolean("On_Your_Profile", false);
        editor.commit();
    }

    public boolean isLastAccountVisited(){
        return sharedPreferences_Profile.getBoolean("Last_Account_Visited", false);
    }

    public void setNewLastAccountVisited(){
        SharedPreferences.Editor editor = sharedPreferences_Profile.edit();
        boolean last = sharedPreferences_Profile.getBoolean("On_Your_Profile", false);
        editor.putBoolean("Last_Account_Visited", last);
        editor.commit();
    }

    public void fetchOthersProfile(String[] data){
        SharedPreferences.Editor editor = sharedPreferences_Other.edit();
        editor.putString("editText_FirstName", data[0]);
        editor.putString("editText_LastName", data[1]);
        editor.putString("editText_Bio", data[2]);
        editor.putString("editText_UserName", data[3]);
        editor.putString("currentProfilePictureID", data[4]);
        editor.putString("editText_Email", data[5]);
        editor.commit();
        switchToOther();
    }

    public String getOtherUserName() {
        return sharedPreferences_Other.getString("editText_UserName", null);
    }

    public String getOtherBio() {
        return sharedPreferences_Other.getString("editText_Bio", null);
    }

    public String getOtherEmail() {
        return sharedPreferences_Other.getString("editText_Email", null);
    }

    public String getOtherProfilePictureID() {
        return sharedPreferences_Other.getString("currentProfilePictureID", null);
    }
}
