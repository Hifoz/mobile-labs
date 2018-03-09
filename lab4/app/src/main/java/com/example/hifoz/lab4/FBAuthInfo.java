package com.example.hifoz.lab4;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Contains the authentication info and the user info from Firebase
 */
public class FBAuthInfo {
    private static boolean isInitialized;

    public static FirebaseAuth auth;
    public static FirebaseUser user;

    public static void init(){
        if(isInitialized)
            return;
        isInitialized = true;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

}
