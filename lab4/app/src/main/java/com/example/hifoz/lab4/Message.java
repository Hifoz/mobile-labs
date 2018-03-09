package com.example.hifoz.lab4;


import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Message {
    public String d;
    public String u;
    public String m;

    public Message(){}

    public Message(DocumentSnapshot doc){
        d = "" + (long)doc.get("d");
        u = (String)doc.get("u");
        m = (String)doc.get("m");
    }

}
