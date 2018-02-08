package com.example.hifoz.lab2;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ListActivity extends AppCompatActivity {
    ArrayList<RSSItem> rssItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rssItems = new ArrayList<RSSItem>();
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
        String feed = getFeed();
        parseFeed(feed);
        updateList();
    }

    public String getFeed() {
        String feed = "";
        try {
            URL url = new URL("https://www.vg.no/rss/feed/?limit=25"); // TODO: use settings

            System.out.println("aa " + url.openStream().toString());

            InputStreamReader isr = new InputStreamReader(url.openStream());

            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            System.out.print(br.readLine());
            String in;
            while ((in = br.readLine()) != null) {
                sb.append(in);
            }
            br.close();
            feed = sb.toString();
        } catch (Exception e) {
            System.out.println("Error in getFeed(): " + e.getMessage());
            e.printStackTrace();
        }

        return feed;
    }



    private void parseFeed(String feed){
        if(feed == ""){
            rssItems.add(new RSSItem("Feed could not be loaded.", "Feed could not be loaded.", ""));
            return;
        }

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(feed));

            int eventType = parser.getEventType();

            String title = null;
            String link = null;
            String description = null;
            boolean isItem = false;
            // Based on parser from https://www.androidauthority.com/simple-rss-reader-full-tutorial-733245/
            while(eventType != XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                if(name == null){
                    continue;
                }
                if(eventType == XmlPullParser.END_TAG){
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }
                if(eventType == XmlPullParser.START_TAG){
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                    }
                    continue;
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
                eventType = parser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
