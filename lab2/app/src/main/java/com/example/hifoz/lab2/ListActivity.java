package com.example.hifoz.lab2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
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
    }

    public void onSettingsClick(View view){
        ListActivity.this.startActivity(new Intent(ListActivity.this, SettingsActivity.class));
    }


    public void updateList(){
        ListView listView = findViewById(R.id.rssFeedList);
        RSSItemAdapter adapter = new RSSItemAdapter(this, rssItems);
        listView.setAdapter(adapter);
    }


    public void forceFetch(View view){
        DownloadTask dt = new DownloadTask();
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getFeed(){
        try{
            URL url = new URL("https://www.vg.no/rss/feed/?limit=25"); // TODO: use settings
            InputStream inputStream = url.openConnection().getInputStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);


            String title = null;
            String link = null;
            String description = null;
            boolean isItem = false;

            // Based on parser from https://www.androidauthority.com/simple-rss-reader-full-tutorial-733245/
            while(parser.next() != XmlPullParser.END_DOCUMENT){
                int eventType = parser.getEventType();
                String name = parser.getName();

                if(name == null){
                    continue;
                }

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }
                if(eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String res = "";
                if (parser.next() == XmlPullParser.TEXT) {
                    res = parser.getText();
                    parser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = res;
                } else if (name.equalsIgnoreCase("link")) {
                    link = res;
                } else if (name.equalsIgnoreCase("description")) {
                    description = res;
                }
                if (title != null && link != null && description != null) {
                    if(isItem) {
                        RSSItem item = new RSSItem(title, link, description);
                        rssItems.add(item);
                    }
                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class DownloadTask extends AsyncTask<URL, Integer, Integer> {

        @Override
        protected Integer doInBackground(URL... urls) {
            getFeed();
            return 1;
        }

        protected void onPostExecute(Integer res){
            updateList();
        }
    }



}
