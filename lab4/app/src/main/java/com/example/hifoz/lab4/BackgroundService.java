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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Background service for checking for new messages periodically
 */
public class BackgroundService extends Service {
    public static boolean appIsActive;
    public static int messageCount = 0;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        stopMessageChecker();
        super.onDestroy();
    }


    // Helper variables for update
    private boolean isRunning = false;
    private Handler handler = new Handler();

    /**
     * Runnable running periodically to check for new messages
     */
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if(!appIsActive)
                checkForNewMessages();
            handler.postDelayed(this, 1000);
        }
    };


    /**
     *Start checking for messages periodically
     */
    public void startMessageChecker() {
        if(isRunning)
            return;
        handler.removeCallbacks(update);
        handler.post(update);
        isRunning = true;
    }

    /**
     * Stop checking for messages
     */
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
     * Android 26 (Oreo) and above needs a notification channel to be set up in order to send notifications
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

}
