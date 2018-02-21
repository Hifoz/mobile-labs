package com.example.hifoz.lab2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ArrayList<RSSItem> rssItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("RSS2.0 Reader");
        if(!BackgroundLoadingService.isCreated){
            Intent intent = new Intent(ListActivity.this, BackgroundLoadingService.class);
            startService(intent);
        }
        new RefreshTask().execute();
    }

    /**
     * Open settings activity
     */
    public void onSettingsClick(View view){
        ListActivity.this.startActivity(new Intent(ListActivity.this, SettingsActivity.class));
    }

    /**
     * Force an update
     */
    public void forceFetch(View view){
        new RefreshTask().execute();
    }


    /**
     * Update the RSS list
     */
    public void updateList(){
        ListView listView = findViewById(R.id.rssFeedList);
        RSSItemAdapter adapter = new RSSItemAdapter(this, rssItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                WebView webView = findViewById(R.id.webView);
                webView.loadUrl(rssItems.get(position).getLink());
            }
        });

    }


    /**
     * Used to load the feed when activity is opened or feed is updated
     */
    private class RefreshTask extends AsyncTask<Void, Integer, Integer> {

        /**
         * Waits for the rss feed to be loaded
         */
        @Override
        protected Integer doInBackground(Void... v) {
            while(BackgroundLoadingService.getFeed() == null || BackgroundLoadingService.getFeed().isEmpty()){
                try{
                    Thread.sleep(1);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
            return 1;
        }

        /**
         * Get the rss items from the background service and update the rss list
         */
        protected void onPostExecute(Integer res){
            rssItems = BackgroundLoadingService.getFeed();
            updateList();
        }

    }

}
