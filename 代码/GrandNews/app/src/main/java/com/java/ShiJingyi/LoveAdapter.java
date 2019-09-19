package com.java.ShiJingyi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;
import static com.java.ShiJingyi.ListAdapter.getLoacalBitmap;
import static com.java.ShiJingyi.ListAdapter.setImgSize;


/**
 * Created by admin on 2018/9/8.
 */

public class LoveAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    ArrayList<SimpleRssItem> simpleItems;



   public LoveAdapter(){
       super();
       init();
   }
   public void init(){
       DatabaseHelper dbHelper = new DatabaseHelper(LoveActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
       simpleItems = dbHelper.getAllLove();
       dbHelper.close();
       if(simpleItems == null){
           simpleItems = new ArrayList<>();
       }
   }

    @Override
    public int getCount() {
       return simpleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return simpleItems.get(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

        Intent intent = new Intent(LoveActivity.context,WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("link",simpleItems.get(position).link);
        intent.putExtra("title",simpleItems.get(position).title);
        intent.putExtra("pubDate",simpleItems.get(position).pubDate);
        intent.putExtra("description",simpleItems.get(position).description);
        intent.putExtra("imgPath",simpleItems.get(position).imgPath);
        intent.putExtra("html",simpleItems.get(position).html);

        LoveActivity.context.startActivity(intent);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(LoveActivity.context).inflate(R.layout.item_layout,parent,false);
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
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(TAG, "onItemLongClick: "+arg2+arg3);
        DatabaseHelper dbHelper = new DatabaseHelper(LoveActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        dbHelper.deleteLove(simpleItems.get(arg2).title);
        simpleItems.remove(arg2);
        dbHelper.close();
        notifyDataSetChanged();

        return true;
   }


    public long getItemId(int position) {
        return position;
    }
}
