package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by admin on 2018/9/2.
 */

public class Content extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    @Nullable
    int id;
    Handler handler;
    String url;
    String category;
    ArrayList<RssItem> rssItems;
    SwipeRefreshLayout srl;
    ListView lv;
    ListAdapter myAdapter;
    View view;
    Context myContext;

    final static int REFRESH_FALSE = 1;
    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int count = 0;
            if(rssItems == null){
                if(msg.what == REFRESH_FALSE){
                    ToastUtil.showToast(getContext(), "更新失败，无网络连接", Toast.LENGTH_SHORT);
                    srl.setRefreshing(false);

                }else{
                    ToastUtil.showToast(getContext(), "网络错误", Toast.LENGTH_SHORT);
                }
                return;
            }

            if(myAdapter.myItems == null || myAdapter.myItems.size() == 0) {
                myAdapter = new ListAdapter(category, myContext, url);
            }else{
                if(rssItems != null || rssItems.size() != 0){
//                    DatabaseHelper dbHelper = new DatabaseHelper(myContext,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
//                    myAdapter.addItems(dbHelper.queryNewNews(myAdapter.category,myAdapter.myItems.get(0).pubDate,5));
//                    dbHelper.close();
                    count = myAdapter.addItems(rssItems);
                }
            }
            if(lv != null){
                lv.setAdapter(myAdapter);
                lv.setOnScrollListener(myAdapter);
                lv.setOnItemClickListener(myAdapter);
                myAdapter.setLv(lv);
            }
            if(msg.what == REFRESH_FALSE){
                srl.setRefreshing(false);
                ToastUtil.showToast(getContext(), "更新了"+ count +"条消息", Toast.LENGTH_SHORT);
            }

            Message tmsg = new Message();
            tmsg.what = 1;
            handler.sendMessage(tmsg);

        }
    };


    int i = 0;
    public void init(Handler h,Context c){
        handler = new Handler();
        myContext = c;
        handler = h;
        update(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.list_layout, container, false);

        if (lv == null){
            lv = view.findViewById(R.id.list_view);
            lv.setOnScrollListener(myAdapter);
            lv.setOnItemClickListener(myAdapter);
            if(myAdapter != null) myAdapter.setLv(lv);
        }
        if(srl == null) {
            srl = view.findViewById(R.id.swipe_news);
            srl.setColorSchemeResources(R.color.colorGray,
                    R.color.colorBlue, R.color.colorBlack, R.color.colorWhite);
            srl.setSize(SwipeRefreshLayout.DEFAULT);
            ;
            //srl.setProgressBackgroundColor(R.color.colorWhite);
            srl.setProgressViewEndTarget(true, 100);
            srl.setOnRefreshListener(this);
        }
        if(myAdapter == null) {
            myAdapter = new ListAdapter(category,MainActivity.context,url);
            View footer = inflater.inflate(R.layout.listfooter, null);
            footer.findViewById(R.id.load_layout);
            footer.setVisibility(View.VISIBLE);
            lv.addFooterView(footer, container, false);
            lv.setAdapter(myAdapter);
            lv.setOnScrollListener(myAdapter);
            lv.setOnItemClickListener(myAdapter);
            myAdapter.setLv(lv);
            myAdapter.setFooter(footer);
        }
        return view;
    }
    public void set(int i,String u,String cat){
        id = i;
        url = u;
        category = cat;
    }
    public int getMyId(){
        return id;
    }
    public String getUrl(){
        return url;
    }

    public void update(final int r){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RssParser rssParser = new RssParser(url,category);
                rssItems = rssParser.work();

                if(rssItems != null && rssItems.size() != 0){
                    DatabaseHelper dbHelper = new DatabaseHelper(myContext,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
                    rssItems = dbHelper.instertNewNews(rssItems);
                    dbHelper.close();
                }

                if(r == 1) {
                    Message tmsg = new Message();
                    tmsg.what = REFRESH_FALSE;
                    myHandler.sendMessage(tmsg);
                }else{
                    Message tmsg = new Message();
                    tmsg.what = 0;
                    myHandler.sendMessage(tmsg);
                }
            }
        }).start();
    }

    public void loadMore(){
        ArrayList<RssItem> rssItemss;
        DatabaseHelper dbHelper = new DatabaseHelper(myContext,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        rssItemss = dbHelper.queryOldNews(myAdapter.myItems.get(myAdapter.myItems.size() - 1).category,myAdapter.myItems.get(myAdapter.myItems.size() - 1).pubDate);
        dbHelper.close();
        myAdapter.addToTail(rssItemss);
    }

    public void onRefresh() {
        update(1);
    }
}
