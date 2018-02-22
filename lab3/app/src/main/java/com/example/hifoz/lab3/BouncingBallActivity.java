package com.example.hifoz.lab3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class BouncingBallActivity extends AppCompatActivity {
    private BouncingBallView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        view = new BouncingBallView(this);

        setContentView(view);
    }
}
