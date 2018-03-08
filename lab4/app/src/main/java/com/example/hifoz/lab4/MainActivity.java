package com.example.hifoz.lab4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    DatabaseReference dbRef;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbRef = FirebaseDatabase.getInstance().getReference();
        username = "Bob";


        findViewById(R.id.messageBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");

                submitMessage(view);
            }
        });

    }


    private void submitMessage(View view) {
        EditText et = findViewById(R.id.messageET);
        String messageText = et.getText().toString();

        if(messageText.isEmpty())
            return;

        String key = dbRef.child("messages").push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("/messages/" + key + "/d", System.currentTimeMillis());
        map.put("/messages/" + key + "/u", username);
        map.put("/messages/" + key + "/m", messageText);

        dbRef.updateChildren(map);

        et.setText("");

    }
}
