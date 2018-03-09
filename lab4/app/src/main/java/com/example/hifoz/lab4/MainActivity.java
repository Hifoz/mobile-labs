package com.example.hifoz.lab4;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements ChatFragment.OnMessageSubmitListener{
    Fragment[] fragments;


    private ViewPager viewPager;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FBAuthInfo.init();
        if(FBAuthInfo.user == null){
            signInUser();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fragments = new Fragment[]{
          new ChatFragment(), new FriendsList()
        };

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(tabPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    /**
     * Signs the user in anonymously
     */
    private void signInUser() {
        FBAuthInfo.auth.signInAnonymously()
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FBAuthInfo.user = FBAuthInfo.auth.getCurrentUser();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to authenticate", Toast.LENGTH_LONG).show();

                    }

                }
            }
        );
    }


    @Override
    public void onMessageSubmit(View view) {
        ((ChatFragment)fragments[0]).submitMessage();
    }
}
