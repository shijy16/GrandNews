package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018/9/8.
 */

public class SearchEngine {
    public static ArrayList<RssItem> search(String key,Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        ArrayList<RssItem> rssItems = dbHelper.queryAllNews();
        dbHelper.close();
        ArrayList<RssItem> result = new ArrayList<>();
        Log.d(TAG, "search: "+rssItems.size()+"???????????????????");
        String[] keys = key.split(" ");
        for(int i =0;i < rssItems.size();i++){
            String title = rssItems.get(i).title;
            String content = rssItems.get(i).description;
            String pubdate = rssItems.get(i).pubDate;
            for(int j = 0;j < keys.length;j++){
                if(!(title.contains(keys[j]) || content.contains(keys[j]) || pubdate.contains(keys[j]))){
                    rssItems.remove(i);
                    i--;
                    break;
                }
            }
        }
        dbHelper.close();
        for(int i = 0;i < rssItems.size();i++){
            for(int j = 0;j < keys.length;j++) {
                rssItems.get(i).title = rssItems.get(i).title.replace(keys[j], "<font color=\"#ff0000\">" + keys[j] + "</font>");
                rssItems.get(i).description = rssItems.get(i).description.replace(keys[j], "<font color=\"#ff0000\">" + keys[j] + "</font>");
                rssItems.get(i).pubDate = rssItems.get(i).pubDate.replace(keys[j], "<font color=\"#ff0000\">" + keys[j] + "</font>");
            }
        }
        return rssItems;
    }
    public static ArrayList<RssItem> search_love(ArrayList<String> key,Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        ArrayList<RssItem> rssItems = dbHelper.queryAllNews();
        ArrayList<RssItem> tempItems = new ArrayList<>();
        dbHelper.close();
        ArrayList<RssItem> result = new ArrayList<>();
        Log.d(TAG, "search: "+rssItems.size()+"???????????????????");
        for(int i =0;i < rssItems.size();i++){
            String title = rssItems.get(i).title;
            String content = rssItems.get(i).description;
            String pubdate = rssItems.get(i).pubDate;
            int count = 0;
            for(int j = 0;j < key.size();j++){
                if(title.contains(key.get(j)) || content.contains(key.get(j)) || pubdate.contains(key.get(j))){
                    count++;
                }
            }
            if(count == 1){
                tempItems.add(rssItems.get(i));
            }else if(count > 1 && rssItems.get(i).hasChecked == 0){
                tempItems.add(0,rssItems.get(i));
            }else if(count > 1){
                tempItems.add(rssItems.get(i));
            }
        }
        dbHelper.close();
        return tempItems;
    }
}
