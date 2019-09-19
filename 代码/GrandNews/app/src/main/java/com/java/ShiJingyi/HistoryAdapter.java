package com.java.ShiJingyi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.java.ShiJingyi.ListAdapter.getLoacalBitmap;
import static com.java.ShiJingyi.ListAdapter.setImgSize;

/**
 * Created by admin on 2018/9/10.
 */

public class HistoryAdapter extends LoveAdapter {

    public HistoryAdapter(){
        super();
    }
    @Override
    public void init(){
        DatabaseHelper dbHelper = new DatabaseHelper(HistoryActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        simpleItems = dbHelper.getAllHistory();
//        simpleItems = dbHelper.getAllLove();

        dbHelper.close();
        if(simpleItems == null){
            simpleItems = new ArrayList<>();
        }else{
            while (simpleItems.size() > 20){
                simpleItems.remove(20);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

        Intent intent = new Intent(HistoryActivity.context,WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("link",simpleItems.get(position).link);
        intent.putExtra("title",simpleItems.get(position).title);
        intent.putExtra("pubDate",simpleItems.get(position).pubDate);
        intent.putExtra("description",simpleItems.get(position).description);
        intent.putExtra("imgPath",simpleItems.get(position).imgPath);
        intent.putExtra("html",simpleItems.get(position).html);

        HistoryActivity.context.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(TAG, "onItemLongClick: "+arg2+arg3);
        DatabaseHelper dbHelper = new DatabaseHelper(HistoryActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        dbHelper.deleteHistory(simpleItems.get(arg2).title);
//        dbHelper.deleteLove(simpleItems.get(arg2).title);
        simpleItems.remove(arg2);
        dbHelper.close();
        notifyDataSetChanged();

        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(HistoryActivity.context).inflate(R.layout.item_layout,parent,false);
        TextView title = convertView.findViewById(R.id.item_title);
        title.setText(simpleItems.get(position).title);
        TextView date = convertView.findViewById(R.id.pub_date);
        date.setText(simpleItems.get(position).pubDate);
        TextView des = convertView.findViewById(R.id.item_description);
        des.setText(simpleItems.get(position).description);
        ImageView img = convertView.findViewById(R.id.img);
        if(img.getDrawable() == null) {
            if (simpleItems.get(position).imgPath != null && !simpleItems.get(position).imgPath.equals("")) {
                Log.d(TAG, "handleMessage: " + simpleItems.get(position).imgPath);
                Bitmap b = getLoacalBitmap(simpleItems.get(position).imgPath);
                if (b != null && b.getWidth() > 400) {
                    b = setImgSize(b, 1000);
                    img.setImageBitmap(b);
                }
            }
        }


        return convertView;

    }
}
