package com.example.hifoz.lab2;

public class RSSItem {
    private String title;
    private String desc;
    private String link;

    public RSSItem(String title, String desc, String link){
        this.title = title;
        this.desc = desc;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getLink() {
        return link;
    }

}
