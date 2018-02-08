package com.example.hifoz.lab1;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            String text1Contents = intent.getStringExtra("et1");
            TextView textView = findViewById(R.id.textView1);
            textView.setText("Hello " + text1Contents);
        }
    }

    public void onButton2Click(View view){
        Intent intent = new Intent(Activity2.this, Activity3.class);
        Activity2.this.startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getExtras() != null && requestCode == 1)
        {
            String text3Contents = data.getStringExtra("et3");

            TextView et2 = findViewById(R.id.textView2);
            et2.setText("From A3: " + text3Contents);
        }
    }





}
