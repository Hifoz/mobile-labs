package com.example.hifoz.lab4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BackgroundService extends Service {
    public static boolean appIsActive;
    private ArrayList<Message> messages;
    private int messageCount = 0;

    public BackgroundService() {
    }


    private void checkForNewMessages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference msgRef = db.collection("messages");
        msgRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot result = task.getResult();
                if(result.size() != messageCount){ // TODO compare with number of messages last time the app was open instead of this, only update the messageCount when the user opens the activity
                    createNotification(result.size() - messageCount);
                    messageCount = result.size();
                }
            }
        });
    }



    private void createNotification(int newMessageCount){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pI = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationCompat.CATEGORY_MESSAGE);
        builder.setContentTitle("New messages in lab4")
                .setContentText(newMessageCount + " new messages since your last visit")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pI)
                .setAutoCancel(true);

        // TODO finish this thing
    }




    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
