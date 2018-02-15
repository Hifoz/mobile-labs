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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        URL url = null;
        try {
            url = new URL("https://www.vg.no/rss/feed/?limit=25"); // TODO: use settings
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            rssItems.add(new RSSItem("<foo>URL Error</foo>", "<foo>URL Error</foo>", ""));
        }

        DownloadTask dt = new DownloadTask();
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Deprecated
    public String getFeed() {
        String feed = "";
        try {
            URL url = new URL("https://www.vg.no/rss/feed/?limit=25"); // TODO: use settings

            InputStreamReader isr = new InputStreamReader(url.openStream());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            System.out.print(br.readLine());
            String in;
            while ((in = br.readLine()) != null) {
                sb.append(in + "\n");
            }
            br.close();
            feed = sb.toString();

        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return feed;
    }

    public void parseFeed(){
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


    private class DownloadTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... urls) {
            parseFeed();
            return "OK";
        }

        protected void onPostExecute(String result){
            updateList();
            for (RSSItem item:rssItems) {
                System.out.println(item.getTitle() + " :: " + item.getDesc() + " :: " + item.getLink());
            }
            System.out.println(rssItems.size());
        }
    }



}
