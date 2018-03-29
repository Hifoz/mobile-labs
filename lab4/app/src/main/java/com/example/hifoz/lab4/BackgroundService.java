package com.example.hifoz.lab4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BackgroundService extends Service {
    public static boolean appIsActive;
    public static int messageCount = 0;

    /**
     * TODO Make it all work
     * - Currently crashing when opening app from notification
     * - Only getting notification if app is closed, not if it is placed in background
     * - Getting notification even when there are no new messages
     *
     */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference msgRef = db.collection("messages");
        msgRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot result = task.getResult();
                messageCount = result.size();
            }
        });
        startMessageChecker();
        System.out.println("starting");
    }

    @Override
    public void onDestroy(){
        stopMessageChecker();
        System.out.println("stopping");
        super.onDestroy();
    }


    long lastUpdateTime;
    private boolean isRunning = false;
    private Handler handler = new Handler();

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            System.out.println("updating...");
            if(!appIsActive)
                checkForNewMessages();
            handler.postDelayed(this, 1000); // TODO delay should be a setting the user can set
        }
    };


    public void startMessageChecker() {
        if(isRunning)
            return;
        lastUpdateTime = SystemClock.uptimeMillis();
        handler.removeCallbacks(update);
        handler.post(update);
        isRunning = true;
    }

    public void stopMessageChecker(){
        handler.removeCallbacks(update);
        isRunning = false;
    }


    /**
     * Checks if there are more messages now than before, and posts a notification
     */
    private void checkForNewMessages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference msgRef = db.collection("messages");
        msgRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot result = task.getResult();
                if(result.size() > messageCount){
                    createNotification(result.size() - messageCount);
                }
            }
        });
    }


    /**
     * Create a notification showing that there are new messages
     * @param newMessageCount how many new messages has been posted
     */
    private void createNotification(int newMessageCount){
        NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pI = PendingIntent.getActivity(this, 0, intent, 0);

        setupNotificationChannel();

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), NotificationCompat.CATEGORY_MESSAGE)
                .setContentTitle("New messages in lab4")
                .setContentText(newMessageCount + " new messages since your last visit")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pI)
                .setColor(Color.CYAN)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();

        if(notifManager != null)
            notifManager.notify(421337, notification);
    }

    /**
     * Sets up a notification channel for v26 and up
     */
    private void setupNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default", "lab4 chat app", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("chat app for la4 in mobile dev");
            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
