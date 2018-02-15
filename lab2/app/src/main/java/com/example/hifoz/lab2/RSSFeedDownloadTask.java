package com.example.hifoz.lab2;

import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Created by Hifoz on 2/15/2018.
 */

public class RSSFeedDownloadTask extends AsyncTask<URL, Integer, String[]> {
    public Callable<String> doOnResult;
    public String result;

    @Override
    protected String[] doInBackground(URL... urls) {
        int count = urls.length;
        String[] resultData = new String[count];
        try{
            for(int i = 0; i < count; i++){
                InputStreamReader isr = new InputStreamReader(urls[i].openStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                System.out.print(br.readLine());
                String in;
                while ((in = br.readLine()) != null) {
                    sb.append(in);
                }
                br.close();
                resultData[i] = sb.toString();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return resultData;
    }

    protected void onPostExecute(String[] result){
        try{
        this.result = result[0];
            doOnResult.call();
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
