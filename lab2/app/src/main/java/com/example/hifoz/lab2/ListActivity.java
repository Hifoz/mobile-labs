package com.example.hifoz.lab2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList<RSSItem> rssItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if(!BackgroundLoadingService.isCreated){
            Intent intent = new Intent(ListActivity.this, BackgroundLoadingService.class);
            startService(intent);
        }
        new RefreshTask().execute();

        updateList();
    }

    public void onSettingsClick(View view){
        ListActivity.this.startActivity(new Intent(ListActivity.this, SettingsActivity.class));
    }

    public void forceFetch(){
        getSystemService(BackgroundLoadingService.class).updateNow();
        new RefreshTask().execute();
    }


    public void updateList(){
        ListView listView = findViewById(R.id.rssFeedList);
        RSSItemAdapter adapter = new RSSItemAdapter(this, rssItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                WebView webView = findViewById(R.id.webView);
                webView.loadUrl(rssItems.get(position).getLink());

                /*
                Intent intent = new Intent(ListActivity.this, ReaderActivity.class);
                intent.putExtra("link", rssItems.get(position).getLink());
                intent.putExtra("title", rssItems.get(position).getTitle());
                ListActivity.this.startActivity(intent);*/
            }
        });

    }

    private class RefreshTask extends AsyncTask<URL, Integer, Integer> {

        @Override
        protected Integer doInBackground(URL... urls) {
            while(BackgroundLoadingService.getFeed() == null){
                try{
                    Thread.sleep(1);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
            return 1;
        }

        protected void onPostExecute(Integer res){
            rssItems = BackgroundLoadingService.getFeed();
            updateList();
        }

    }

}
