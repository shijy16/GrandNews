package com.java.ShiJingyi;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2018/9/2.
 */

public class RssParser{
    private String url;
    private Elements items;
    private String cat;

    public RssParser(String u,String c){
        items = null;
        url = u;
        cat = c;
    }
    private String getEncoding(String firstLine){
        Pattern r = Pattern.compile("encoding=\"([^\"]+)");
        Matcher m = r.matcher(firstLine);
        if(m.find())
            firstLine = m.group(1);
        return firstLine;
    }

    public ArrayList<RssItem> work(){
        if(url == null) return null;
        if(url.equals("")) return  new ArrayList<>();
        try {
            String encoding = null;
            URL xml = new URL(url);
            InputStream urlStream = xml.openStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(urlStream));

            String firstLine = bf.readLine();

            encoding = getEncoding(firstLine);
            if(encoding == null){
                encoding = "gbk";
            }

            bf.close();
            urlStream.close();

            urlStream = xml.openStream();
            Document doc;
            try {
                doc = Jsoup.parse(urlStream,encoding,url);
            }catch (Exception e){
                doc = doc = Jsoup.parse(urlStream,"utf8",url);
            }
            items = doc.select("item");
        } catch (Exception e) {
            e.printStackTrace();
            items = null;
        }
        ArrayList<RssItem> rssItems = new ArrayList<>();
        if(items != null) {
            for (Element item : items) {
                rssItems.add(new RssItem(item,cat));
            }
        }else {
            rssItems = null;
        }
        return  rssItems;
    }
}
