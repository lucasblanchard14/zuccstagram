package com.example.myapplication.LogIn_SignUp;

import android.net.Uri;

public class Profile {



    //P1
    private String FirstName;
    private String LastName;
    private String Email;

    //P2
    private String UserName;
    private String Bio;
    private String Password;

    //P3
    private String SecurityQuestion;
    private String SecurityQuestionAnswer;

    //P4
    private Uri profileImage;
    private String ImageCount;






    public Profile (){

        //P1
        FirstName = null;
        LastName = null;
        Email = null;


        //P2
        UserName = null;
        Bio = null;
        Password = null;

        //P3
        SecurityQuestion = null;
        SecurityQuestionAnswer = null;

        //P4
        profileImage = null;
        ImageCount = null;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSecurityQuestion() {
        return SecurityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        SecurityQuestion = securityQuestion;
    }

    public String getSecurityQuestionAnswer() {
        return SecurityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        SecurityQuestionAnswer = securityQuestionAnswer;
    }

    public Uri getProfileImage() {
        return profileImage;
    }
    public String getImageCount() {
        return ImageCount;
    }

    public void setProfileImage(Uri profileImage) {
        this.profileImage = profileImage;
    }











}

