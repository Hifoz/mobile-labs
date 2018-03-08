package com.example.hifoz.lab4;


import java.util.Date;

public class Message {
    public String d;
    public String u;
    public String m;

    public Message(){

    }

    /**
     * Create a new Message
     * @param user username
     * @param message message contents
     */
    public Message(String user, String message){
        u = user;
        m = message;
        Date date = new Date();
    }
}
