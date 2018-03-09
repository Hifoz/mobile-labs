package com.example.hifoz.lab4;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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


        // Todo: Let the user select username themselves and store that name for future use
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName("Bob").build();
        FBAuthInfo.user.updateProfile(profileUpdate);


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

        setupSnapshotListener();
    }

    private void setupSnapshotListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    System.out.println("Snapshot listener fail");
                    return;
                } else if(documentSnapshots == null){
                    System.out.println("Snapshots are null");
                    return;
                }
                ArrayList<DocumentSnapshot> newDocs = new ArrayList<>();
                for(DocumentChange dc : documentSnapshots.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED)
                        newDocs.add(dc.getDocument());
                }
                if(!newDocs.isEmpty())
                    ((ChatFragment)fragments[0]).updateMessageList(newDocs);
            }
        });
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
