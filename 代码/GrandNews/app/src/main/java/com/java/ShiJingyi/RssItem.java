package com.java.ShiJingyi;

import org.jsoup.nodes.Element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018/9/2.
 */

public class RssItem{
    public String title;
    public String link;
    public String category;
    public String pubDate;
    public String description;
    public String imgUrl;
    public String imgPath;

    public int hasChecked;
    public boolean getImg;
    public boolean loadImg;
    public Bitmap image;
    public RssItem(String a,String b,String c,String d,String e,int checked,String categ,String imgP,boolean has){
        loadImg = false;
        getImg = false;
        title = a;
        link = b;
        description = c;
        hasChecked = checked;
        category = categ;
        imgUrl = d;
        imgPath = imgP;
        pubDate = e;
        getImg = has;
    }
    public RssItem(Element element,String c){
        loadImg = false;
        getImg = false;
        title = "";
        link = "";
        category = c;
        pubDate = "";
        description = "";
        imgUrl = "";
        imgPath = "";
        title = element.select("title").first().text();
//        link = element.select("link").first().text();
        if(link == "") link = getLink(element.toString());
//        category = element.select("category").first().text();
        pubDate = element.select("pubDate").first().text();
        description = element.select("description").first().text();
        hasChecked = 0;
//        imgUrl = getImgUrl();
//        Log.i("?", "RssItem: "+imgUrl);
        if(description != null){
            if(description.length() > 60){
                description = description.substring(0,60)+"...";
            }
        }
    }

    public String print(){
       // Log.d("error", title+"\n"+category+"\n"+link+"\n"+pubDate+"\n"+description+"\n");
        return title+"\n"+category+"\n"+link+"\n"+pubDate+"\n"+description+"\n";
    }

    public String getLink(String content){
        Pattern r = Pattern.compile("<link />(http://[^<\n]+)");
        Matcher m = r.matcher(content);
        String nLine = "";
        if(m.find())
            nLine = m.group(1);
        return nLine;
    }

    public String getImgUrl(){
        getImg = true;
        try{
            URL xml = new URL(link);
            InputStream urlStream = xml.openStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(urlStream));

            String content = "";
            String nLine = null;
            nLine = bf.readLine();
            while (nLine != null){
                content += nLine;
                nLine = bf.readLine();
            }

            Pattern r = Pattern.compile("img src=\"([^\"]+)");
            Matcher m = r.matcher(content);
            nLine = "";
            if(m.find()) {
                nLine = m.group(1);
            }

            return nLine;
        }
        catch (Exception e){
            return "";
        }
    }

    public void saveImg(){
        if(imgUrl == null || imgUrl == "") return;;
        try {
            if(!imgUrl.contains("http:")){
                imgUrl = "http:"+imgUrl;
            }
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500);
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap.getWidth() < 400)
                    return;
                Message msg = Message.obtain();

                inputStream.close();

                File appDir = new File("/data/data/"+MainActivity.context.getPackageName()+"/", "tempPic");
                if (!appDir.exists()) {
                    Log.d("???", "run: "+"\n"+ appDir.mkdirs());
                }
                String fileName = imgUrl.substring(imgUrl.length() - 10,imgUrl.length()).replace(".","a") + ".jpg";
                fileName = fileName.replace("/","a");
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    if(bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)) {
                        imgPath = "/data/data/" + MainActivity.context.getPackageName() + "/tempPic/" + fileName;
                        Log.d(TAG, "saveImg: "+file.getPath());
                    }
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    imgPath = "";
                    e.printStackTrace();
                } catch (IOException e) {
                    imgPath = "";
                    e.printStackTrace();
                }catch (Exception e){
                    imgPath = "";
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            imgPath = "";
            e.printStackTrace();
        }
    }



}
