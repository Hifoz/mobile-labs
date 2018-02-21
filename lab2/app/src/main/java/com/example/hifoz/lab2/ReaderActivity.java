package com.example.hifoz.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);


        Intent intent = getIntent();
        if(intent.getExtras() != null){
            String link = intent.getStringExtra("link");
            WebView webView = new WebView(this);
            setContentView(webView);
            String title = intent.getStringExtra("title");
            webView.loadUrl(link);
            setTitle(title);
        } else {
            setTitle("Error");
        }

    }
}
