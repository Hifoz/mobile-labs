package com.example.hifoz.lab4;


import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public String d;
    public String u;
    public String m;

    public Message(DocumentSnapshot doc){
        Date date = new Date((long)doc.get("d"));
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        d = sfd.format(date);
        u = (String)doc.get("u");
        m = (String)doc.get("m");
    }

}
