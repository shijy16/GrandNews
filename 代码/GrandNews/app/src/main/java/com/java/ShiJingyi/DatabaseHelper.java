package com.java.ShiJingyi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018/9/6.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    ArrayList<Url> urls;
    static String user;
    String filePath;
    String fileName;
    SQLiteDatabase mDatabase;
    public DatabaseHelper(Context context,String us, String name, SQLiteDatabase.CursorFactory factory, int version, ArrayList<Url> u){
        super(context, name, null, version);
        urls = u;
        user = us;
        fileName = name;
        filePath = "/data/data/"+context.getPackageName()+"/databases/";
    }

    public void createDb(){
        if(!isDatabaseExist()){
            this.getWritableDatabase();
            this.close();
        }
    }

    public boolean openDataBase() throws SQLException {

        mDatabase = SQLiteDatabase.openDatabase(filePath+fileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDatabase != null;
    }

    private boolean isDatabaseExist(){
        File dbFile = new File(filePath + fileName);
        return dbFile.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS '"+user+"'(love TEXT PRIMARY KEY,time INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '"+user+"History'(title TEXT,pubdate TEXT,description TEXT,link TEXT,imagepath TEXT,id INTEGER PRIMARY KEY AUTOINCREMENT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '"+user+"Save'(user TEXT,title TEXT,pubdate TEXT,description TEXT,link TEXT,imagepath TEXT,html Text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  News(category TEXT," +
                "title TEXT,link TEXT,pubdate TEXT PRIMARY KEY,description TEXT," +
                "checked NUMERIC DEFAULT 0,haveimg NUMERIC DEFAULT 0," +
                "imageurl TEXT,imagepath TEXT)");
    }



    public ArrayList<RssItem> queryNews(String category , int least) {
        ArrayList<RssItem> rssItems = new ArrayList<>();
        if(category == null || category.equals("")) return rssItems;
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("News",null,"category = ?",new String[]{category},null,null,"pubdate DESC",null);
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String link = cursor.getString(cursor.getColumnIndex("link"));
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imgUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
                String imgPath = cursor.getString(cursor.getColumnIndex("imagepath"));
                int checked = Integer.parseInt(cursor.getString(cursor.getColumnIndex("checked")));
                int hasImg = Integer.parseInt(cursor.getString(cursor.getColumnIndex("haveimg")));
                RssItem rssItem = new RssItem(title,link,description,imgUrl,pubdate,checked,category,imgPath,hasImg == 1?true:false);
//                   Log.d(TAG, "queryNews: "+pubdate);
                rssItems.add(rssItem);
            } while (cursor.moveToNext() && i++ < least);
        }
        cursor.close();
        db.close();

        return rssItems;
    }

    public ArrayList<RssItem> queryAllNews() {
        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("News", null, null, null, null, null, "pubdate DESC");

            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String link = cursor.getString(cursor.getColumnIndex("link"));
                    String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    String imgUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
                    String categ = cursor.getString(cursor.getColumnIndex("category"));
                    int checked = Integer.parseInt(cursor.getString(cursor.getColumnIndex("checked")));
                    String imgPath = cursor.getString(cursor.getColumnIndex("imagepath"));
                    int hasImg = Integer.parseInt(cursor.getString(cursor.getColumnIndex("haveimg")));
                    RssItem rssItem = new RssItem(title,link,description,imgUrl,pubdate,checked,categ,imgPath,hasImg == 1);
                    rssItems.add(rssItem);
                } while (cursor.moveToNext());
            }

        return rssItems;
    }

    public ArrayList<RssItem> queryNewNews(String category,String date,int least) {
        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("News", null, "category = ?", new String[]{category}, null, null, "pubdate DESC");

        if (cursor.moveToFirst()) {
            do {
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                if(pubdate.equals(date)) break;
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String link = cursor.getString(cursor.getColumnIndex("link"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imgUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
                String categ = cursor.getString(cursor.getColumnIndex("category"));
                int checked = Integer.parseInt(cursor.getString(cursor.getColumnIndex("checked")));
                String imgPath = cursor.getString(cursor.getColumnIndex("imagepath"));
                int hasImg = Integer.parseInt(cursor.getString(cursor.getColumnIndex("haveimg")));
                RssItem rssItem = new RssItem(title,link,description,imgUrl,pubdate,checked,categ,imgPath,hasImg == 1?true:false);
                rssItems.add(rssItem);
            } while (cursor.moveToNext());
        }

        return rssItems;
    }

    public void changeSomething(String findByWhat,String myWhat,String changeWaht,String data){
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE News SET " + changeWaht+ " = " + "'"+data+"'"
                + " where " + findByWhat + " is '" + myWhat+"'");
        db.close();
        close();
    }


    public void insertNews(RssItem item) {
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO News(category,title,link,pubdate" +
                ",description,checked,haveimg,imageurl,imagepath) values(?,?,?,?,?,?,?,?,?)";
        String[] s = new String[]{item.category, item.title, item.link
                , item.pubDate,  item.description, String.valueOf(item.hasChecked), item.getImg ? "1":"0", item.imgUrl,item.imgPath};
//        for(int i = 0;i < s.length;i++){
//            s[i] = "'"+s[i]+"'";
//        }

        try {
            db.execSQL(sql, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void insertManyNews(ArrayList<RssItem> items) {
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        for(int i = 0;i < items.size();i++) {

//            Log.d(TAG, "insertManyNews: "+items.get(i).category+items.get(i).pubDate);

            String sql = "INSERT INTO News(category,title,link,pubdate" +
                    ",description,checked,haveimg,imageurl,imagepath) values(?,?,?,?,?,?,?,?,?)";

            String[] s = new String[]{items.get(i).category, items.get(i).title, items.get(i).link
                    , items.get(i).pubDate, items.get(i).description, String.valueOf(items.get(i).hasChecked),
                    items.get(i).getImg ? "1" : "0", items.get(i).imgUrl,items.get(i).imgPath};

            try {
                db.execSQL(sql, s);
            } catch (Exception e) {
//                Log.e(TAG, "insertManyNews: "+items.get(i).category +items.get(i).pubDate+"failed", e);
            }
        }
        db.close();
    }

    public ArrayList<RssItem> instertNewNews(ArrayList<RssItem> rssItems){
        if(rssItems == null || rssItems.size() == 0){
            return null;
        }
        else{
            String category = rssItems.get(0).category;
            ArrayList<RssItem> t = queryNews(category,1);
            if(t == null || t.size() == 0){
                insertManyNews(rssItems);
                return  rssItems;
            }else{
                String latest = t.get(0).pubDate;
//                Log.d(TAG, "instertNewNews: "+latest+t.get(0).title);
                t = new ArrayList<>();
                for(int i = 0;i < rssItems.size();i++){
                    if(latest.compareTo(rssItems.get(i).pubDate) < 0){
//                        Log.d(TAG, "instertNewNews: "+rssItems.get(i).title+rssItems.get(i).pubDate);
                        t.add(rssItems.get(i));
                    }else{
                        break;
                    }
                }
                if(t.size() != 0){
                    insertManyNews(t);
                }
                return t;
            }
        }
    }

    public ArrayList<RssItem> queryOldNews(String category,String date){
        ArrayList<RssItem> rssItems = new ArrayList<>();
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("News",null,"category = ?",new String[]{category},null,null,"pubdate DESC",null);
        if (cursor.moveToFirst()) {
            while (cursor.getString(cursor.getColumnIndex("pubdate")).compareTo(date) >= 0) {
                if (!cursor.moveToNext()) {
                    db.close();
                    close();
                    return rssItems;
                }
            }
            for (int i = 0; i < 5; i++) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String link = cursor.getString(cursor.getColumnIndex("link"));
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imgUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
                String categ = cursor.getString(cursor.getColumnIndex("category"));
                int checked = Integer.parseInt(cursor.getString(cursor.getColumnIndex("checked")));
                String imgPath = cursor.getString(cursor.getColumnIndex("imagepath"));
                int hasImg = Integer.parseInt(cursor.getString(cursor.getColumnIndex("haveimg")));
                RssItem rssItem = new RssItem(title, link, description, imgUrl, pubdate, checked, categ, imgPath, hasImg == 1);
                rssItems.add(rssItem);
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        db.close();
        close();
        return rssItems;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertLove(String title,String pubdate,String description,String link,String imgPath,String html){
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO '"+user+"Save'(title,pubdate,description,link,imagepath,html) values(?,?,?,?,?,?)";

        String[] s = new String[]{title,pubdate,description
                    , link,imgPath,html};
        try {
            db.execSQL(sql, s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "insertLove: "+title);
        db.close();
        close();
    }


    public boolean checkLove(String title) {
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("'"+user+"Save'", null, "title = ?", new String[]{title}, null, null, null );

        if(cursor.moveToFirst()){
            return true;
        }
        else return false;
    }

    public void deleteLove(String title) {
        Log.d(TAG, "deleteLove: "+title);
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        db.delete("'"+user+"Save'","title = ?",new String[]{title});
        db.close();
    }

    public ArrayList<SimpleRssItem> getAllLove(){
        ArrayList<SimpleRssItem> simpleItems = new ArrayList<>();
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("'"+user+"Save'", null, null, null, null, null, "pubdate DESC");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String link = cursor.getString(cursor.getColumnIndex("link"));
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imagepath = cursor.getString(cursor.getColumnIndex("imagepath"));
                String html = cursor.getString(cursor.getColumnIndex("html"));
                SimpleRssItem t = new SimpleRssItem(title,pubdate,link,description,imagepath,html);
                simpleItems.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return simpleItems;
    }

    public void insertLoveKey(String key){
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("'"+user+"'", null, "love=?", new String[]{key}, null, null, null);
        if(cursor.moveToFirst()){
           // ContentValues values = new ContentValues();
            String query = "update '"+user+"' set time=time+1 where love='"+key+"'";
           // values.put("time","time + 1");
            db.execSQL(query);
            Log.d(TAG, "insertLoveKey: update "+key);
           // db.update("'"+user+"'",values,"love=?",new String[]{key});
        }else{
            ContentValues values = new ContentValues();
            values.put("love",key);
            values.put("time",1);
            Log.d(TAG, "insertLoveKey: "+key+cursor.getPosition());
            db.insert("'"+user+"'", null,values);
        }
        db.close();
    }

    public ArrayList<String> getBestLoveWord(int num){

        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("'"+user+"'", null, null, null, null, null, "time DESC");
        ArrayList<String> words = new ArrayList<>();
        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                String love = cursor.getString(cursor.getColumnIndex("love"));
                Log.d(TAG, "getBestLoveWord: "+cursor.getString(cursor.getColumnIndex("time")));
                words.add(love);
            } while (cursor.moveToNext() && ++index <num);
        }
        return words;
    }

    public boolean tabbleIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            createDb();
            close();
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name = '" + tableName +"';";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
        }
        return result;
    }
    public void create(String name,String content){
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS "+name+content);
        db.close();
        close();
    }

    public void insertHistory(String title,String pubdate,String description,String link,String imgPath){
        createDb();
        close();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO '"+user+"History'(title,pubdate,description,link,imagepath) values(?,?,?,?,?)";

        String[] s = new String[]{title,pubdate,description
                , link,imgPath};
        try {
            db.execSQL(sql, s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "insertHistory: "+title);
        db.close();
        close();
    }



    public void deleteHistory(String title) {
        Log.d(TAG, "deleteHistory: "+title);
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        db.delete(" '"+user+"History'","title = ?",new String[]{title});
        db.close();
    }

    public ArrayList<SimpleRssItem> getAllHistory(){
        ArrayList<SimpleRssItem> simpleItems = new ArrayList<>();
        createDb();
        close();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(" '"+user+"History'", null, null, null, null, null, "id DESC");
        int count = 0;
        int a = 0;
        if (cursor.moveToFirst()) {
            do {
                count++;
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String link = cursor.getString(cursor.getColumnIndex("link"));
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imagepath = cursor.getString(cursor.getColumnIndex("imagepath"));
                if(count == 1)
                    a = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                SimpleRssItem t = new SimpleRssItem(title,pubdate,link,description,imagepath,"");
                simpleItems.add(t);
            } while (cursor.moveToNext() && count < 20);
        }
        try {
            Log.d(TAG, "getAllHistory: ??????????????"+a);
            db.delete(" '"+user+"History'","id < ?",new String[]{String.valueOf(a - 20)});
        }catch (Exception e){
            Log.d(TAG, "getAllHistory: ??????????????");
        }

        cursor.close();
        close();
        return simpleItems;
    }
}
