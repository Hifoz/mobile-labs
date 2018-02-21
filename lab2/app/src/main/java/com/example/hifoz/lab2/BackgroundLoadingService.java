package com.example.hifoz.lab2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundLoadingService extends Service {
    public static boolean isCreated = false;
    private static ArrayList<RSSItem> rssItems;

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);


    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
        startDownload();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startDownload(){
        DownloadTask dt = new DownloadTask();
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        executor.schedule(new Runnable() {
            @Override
            public void run() {
                startDownload();
            }
        }, getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE).getInt("updateFreq", 5000), TimeUnit.MILLISECONDS);
    }

    public void updateNow(){
        executor.shutdown();
    }


    public static ArrayList<RSSItem> getFeed(){
        return rssItems;
    }


    private class DownloadTask extends AsyncTask<URL, Integer, Integer> {

        @Override
        protected Integer doInBackground(URL... urls) {
            rssItems = new ArrayList<>();
            getFeed();
            return 1;
        }

        protected void onPostExecute(Integer res){
        }

        public void getFeed(){
            try{

                SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);// PreferenceManager.getDefaultSharedPreferences(BackgroundLoadingService.this);
                String baseUrl = prefs.getString("feedLink", "https://www.vg.no/rss/feed/");
                int limit = prefs.getInt("itemLimit", 10);
                URL url = new URL(baseUrl + "?limit=" + limit);
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
                            title = null;
                            link = null;
                            description = null;
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
                    } else if (name.equalsIgnoreCase("description")) {
                        description = res;
                    } else if (name.equalsIgnoreCase("link")) {
                        link = res;
                    }
                    if (title != null && link != null && description != null) {
                        if(isItem) {
                            RSSItem item = new RSSItem(title, description, link);
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



    }



}
