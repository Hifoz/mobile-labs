package com.example.hifoz.lab2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Background service responsible for keeping the RSS Feed updated
 */
public class BackgroundLoadingService extends Service {
    public static boolean isCreated = false;
    private static ArrayList<RSSItem> rssItems;
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private static BackgroundLoadingService service;
    private static ScheduledFuture scheduledDownload;


    @Override
    public void onCreate(){
        super.onCreate();
        startDownload();
        service = this;
        executor.setRemoveOnCancelPolicy(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Not Implemented");
        return null;
    }

    /**
     * Starts a download task to update the feed, and schedules a new download using the user set update frequency
     */
    private void startDownload(){
        DownloadTask dt = new DownloadTask();
        dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        scheduledDownload = executor.schedule(new Runnable() {
            @Override
            public void run() {
                startDownload();
            }
        }, getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE).getInt("updateFreq", 1), TimeUnit.HOURS);
    }

    /**
     * Force an update-attempt
     */
    public static void updateNow(){
        rssItems = new ArrayList<>();
        scheduledDownload.cancel(true);
        service.startDownload();

    }

    /**
     * @return the rss feed items
     */
    public static ArrayList<RSSItem> getFeed(){
        return rssItems;
    }

    /**
     * Async class responsible for fetching the rss feed from the web
     */
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<URL, Integer, Integer> {
        boolean hasLimit = true;
        @Override
        protected Integer doInBackground(URL... urls) {
            System.out.println("updating...");
            rssItems = new ArrayList<>();
            InputStream is = null;
            try {
                is = fetchFeed();
            } catch (Exception e){
                try{
                    hasLimit = false;
                    is = fetchFeed();
                } catch (Exception e2) {
                    System.out.println(e2.getMessage());
                    e.printStackTrace();
                }
            }
            if(is != null)
                parseFeed(is);
            return 1;
        }

        /**
         * Fetch the feed from the internet
         * @return an InputStream containing the feed
         */
        private InputStream fetchFeed() throws IOException {
            SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);// PreferenceManager.getDefaultSharedPreferences(BackgroundLoadingService.this);
            String strUrl = prefs.getString("feedLink", "https://www.vg.no/rss/feed/");
            if(hasLimit){
                strUrl += "?limit=" + prefs.getInt("itemLimit", 25);
            }

            URL url = new URL(strUrl);

            return url.openConnection().getInputStream();
        }


        /**
         * Parses the feed into the parent rssItems list
         * @param inputStream an InputStream of the feed
         */
        private void parseFeed(InputStream inputStream) {
            try {
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
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    int eventType = parser.getEventType();
                    String name = parser.getName();

                    if (name == null) {
                        continue;
                    }

                    if (eventType == XmlPullParser.END_TAG) {
                        if (name.equalsIgnoreCase("item")) {
                            isItem = false;
                        }
                        continue;
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (name.equalsIgnoreCase("item")) {
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
                        if (isItem) {
                            RSSItem item = new RSSItem(title, description, link);
                            rssItems.add(item);
                        }
                        isItem = false;

                        SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
                        if(rssItems.size() >= prefs.getInt("itemLimit", 25)){
                            return;
                        }
                    }
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
