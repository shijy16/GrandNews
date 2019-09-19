package com.java.ShiJingyi;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin on 2018/9/9.
 */

public class MyIntentService extends IntentService {


    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(),MainActivity.userName,"news.db", null, 1,MainActivity.urls);

        String a = intent.getStringExtra("text");
        Log.d("?????????????????????", "onHandleIntent: "+"begin!!!!!!!!!!!!!!!!!!!!!!!!"+a);
        ArrayList<String> b = SentenseDivider.divideChinese(a);
        Log.d("?????????????????????", "onHandleIntent: "+"middle!!!!!!!!!!!!!!!!!!!!!!!!"+b);
        ArrayList<String> c = new ArrayList<>();
        for(int i = 0;i < b.size();i++){
            if(b.get(i).length() > 1 && !b.get(i).equals("...")){
                dbHelper.insertLoveKey(b.get(i));
            }
        }
        Log.d("?????????????????????", "onHandleIntent: "+"finish!!!!!!!!!!!!!!!!!!!!!!!!"+dbHelper.getBestLoveWord(3).toString());
        dbHelper.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

}
