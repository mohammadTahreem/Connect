package com.devtahreem.testproject;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.testproject.R;

import java.util.ArrayList;

public class UserCardDetails {
    private Bitmap userImage;
    private String Name, Age, Occupation, Address;
    private String currentUserId;


    UserCardDetails(Bitmap mUserImage, String mUserName, String mUserAge, String mUserAddress, String mUserOccupation, String currentUserID){
        userImage = mUserImage;
        Name = mUserName;
        Age = mUserAge;
        Address = mUserAddress;
        Occupation = mUserOccupation;
        currentUserId = currentUserID;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public String getName() {
        return Name;
    }

    public String getAge() {
        return Age;
    }

    public String getOccupation() {
        return Occupation;
    }

    public String getAddress() {
        return Address;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }


}

