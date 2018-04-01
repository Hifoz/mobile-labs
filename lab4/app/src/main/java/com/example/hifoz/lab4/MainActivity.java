package com.example.hifoz.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Main Activity
 */
public class MainActivity extends AppCompatActivity implements ChatFragment.OnMessageSubmitListener, FriendsListFragment.OnUserSelectListener{
    public Fragment[] fragments;

    private ViewPager viewPager;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Authenticate user:
         */
        FBAuthInfo.init();
        if(FBAuthInfo.user == null){
            signInUser();
        }

        System.out.println("USER NULL? MA " + (FBAuthInfo.user == null));
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sharedPrefs.getString("username", "NE");
        if(name.equals("NE")){
            registerName();
        } else {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            FBAuthInfo.user.updateProfile(profileUpdate);
        }

        /*
         * Set up fragments and tabs:
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragments = new Fragment[]{
          new ChatFragment(), new FriendsListFragment()
        };
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(tabPagerAdapter);

        // Set tab icons and remove text:
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        if(tab0 != null){
            tab0.setIcon(R.drawable.ic_chat_bubble);
            tab0.setText("");
        }
        if(tab1 != null){
            tab1.setIcon(R.drawable.ic_people);
            tab1.setText("");
        }

        /*
         * Start background service:
         */
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
        BackgroundService.appIsActive = true;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Open name-registration activity
     */
    private void registerName() {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivity(intent);

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
                        Toast.makeText(MainActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                        System.out.println("Authenticated");

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String name = sharedPrefs.getString("username", "NE");
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        FBAuthInfo.user.updateProfile(profileUpdate);

                    } else {
                        Toast.makeText(MainActivity.this, "Failed to authenticate", Toast.LENGTH_LONG).show();
                        System.out.println("Failed to authenticate");
                    }

                }
            }
        ).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to authenticate.", Toast.LENGTH_LONG).show();
                System.out.println("Failed to authenticate.");
            }
        });
    }


    @Override
    public void onResume() {
        BackgroundService.appIsActive = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        BackgroundService.appIsActive = false;
        super.onPause();
    }

    /**
     * Called when the user submits a message. Redirects the view to the chat fragment for handling
     */
    @Override
    public void onMessageSubmit(View view) {
        ((ChatFragment)fragments[0]).submitMessage();
    }


    /**
     * Called when the user select sa username in the friends tab. Redirects to the friends fragment for handeling
     * @param pos position of username clicked
     */
    @Override
    public void onUserSelect(View v, int pos, long id) {
        String name = ((UserListAdapter.ViewHolder)v.getTag()).username.getText().toString();
        ((ChatFragment)fragments[0]).updateDisplayedList(name);
        ((FriendsListFragment)fragments[1]).updateDisplayedUser(v, pos, id);
    }
}
