package com.example.hifoz.lab4;


import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Message {
    public String d;
    public String u;
    public String m;

    public Message(){}

    public Message(DocumentSnapshot doc){
        Date date = new Date((long)doc.get("d"));
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        sfd.format(date);

        d = date.toString();
        u = (String)doc.get("u");
        m = (String)doc.get("m");
    }

}
