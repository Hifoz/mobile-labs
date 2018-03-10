package com.example.hifoz.lab4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // TODO make username suggestion.


        findViewById(R.id.usernameBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @SuppressLint("ApplySharedPref") // Becasue I want it to be written immediatley
    private void finishActivity() {
        // Get the name
        String name = ((EditText)findViewById(R.id.usernameET)).getText().toString();
        if(name.length() < 5){
            Toast.makeText(RegisterActivity.this, "Name must be at least 5 character long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save name in settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("username", name);
        editor.commit();

        // Update profile
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        FBAuthInfo.user.updateProfile(profileUpdate);

        Toast.makeText(RegisterActivity.this, "Username saved as \"" + name + "\"", Toast.LENGTH_LONG).show();

        // Return to previous activity
        setResult(Activity.RESULT_OK);
        RegisterActivity.this.finish();
    }
}
