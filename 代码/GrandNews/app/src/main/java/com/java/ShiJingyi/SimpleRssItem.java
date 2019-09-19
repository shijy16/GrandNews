package com.java.ShiJingyi;

/**
 * Created by admin on 2018/9/8.
 */

public class SimpleRssItem {
    public String title;
    public String pubDate;
    public String link;
    public String description;
    public String imgPath;
    public String html;

    public SimpleRssItem(String ti,String pu,String li,String de,String im,String ht){
        title = ti;
        pubDate = pu;
        link = li;
        description = de;
        imgPath = im;
        html = ht;
    }
}
