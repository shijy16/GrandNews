package com.java.ShiJingyi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/9/3.
 */

class TabAdapter extends FragmentStatePagerAdapter {
    String lastPubDate = new String("");
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                notifyDataSetChanged();
            }
        }
    };

    ArrayList<Content> f;
    ArrayList<Url> urls;
    ArrayList<Integer> pageIndex;
    private static Context context;
    private List<String> tabList=new ArrayList<>();

    public TabAdapter(FragmentManager fm,Context c,ArrayList<Url> us) {
        super(fm);
        urls = us;
        init();
        context = c;
//        f = new ArrayList<>();
//        for(int i = 0;i < tabList.size();i++) {
//            Content fragment = new Content();
//            fragment.init(handler,context);
//            Bundle bundle = new Bundle();
//            bundle.putString("text", tabList.get(i));
//            fragment.setArguments(bundle);
//            fragment.set(i,urls.get(pageIndex.get(i)).url);
//            if(i == 0||i == 1) fragment.update(0);
//            f.add(fragment);
//        }
    }

    private void init(){
        pageIndex = new ArrayList<>();
        for(int i = 0;i < urls.size();i++){
            if(urls.get(i).show){
                tabList.add(urls.get(i).name);
                pageIndex.add(i);
            }
        }
    }




    @Override
    public Fragment getItem(int position) {
        if(pageIndex.get(position) != 0) {
            Content fragment = new Content();
            fragment.init(handler, context);
            fragment.set(position, urls.get(pageIndex.get(position)).url, urls.get(pageIndex.get(position)).name);
            fragment.update(0);
            return fragment;
        }else{
            Fragment fragment = new RecommandFragment();
            return fragment;
        }
    }
    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position);
    }


    @Override
    public int getItemPosition(Object object){
        Fragment fra = (Fragment) object;
        return POSITION_UNCHANGED;
    }
}