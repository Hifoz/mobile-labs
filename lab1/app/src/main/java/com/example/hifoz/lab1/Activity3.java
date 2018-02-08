package com.example.hifoz.lab1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Activity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
    }


    public void onButton3Click(View view){
        finishActivity();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    public void finishActivity(){
        EditText et3 = findViewById(R.id.editText3);

        Intent intent = new Intent();
        intent.putExtra("et3", et3.getText().toString());
        setResult(Activity.RESULT_OK, intent);

        Activity3.this.finish();
    }
}
