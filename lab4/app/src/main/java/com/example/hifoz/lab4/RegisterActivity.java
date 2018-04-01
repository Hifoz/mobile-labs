package com.example.hifoz.lab4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Random;

/**
 * Activity for registering a username for a new user
 */
public class RegisterActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n") // Suppressed because i18n isn't relevant to the random username
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Random rng = new Random(System.currentTimeMillis());
        ((EditText)findViewById(R.id.usernameET)).setText("user" + rng.nextInt());

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
        editor.apply();

        // Update profile
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        if(FBAuthInfo.user != null)
            FBAuthInfo.user.updateProfile(profileUpdate);

        Toast.makeText(RegisterActivity.this, "Username saved as \"" + name + "\"", Toast.LENGTH_LONG).show();

        // Return to previous activity
        setResult(Activity.RESULT_OK);
        RegisterActivity.this.finish();
    }
}
