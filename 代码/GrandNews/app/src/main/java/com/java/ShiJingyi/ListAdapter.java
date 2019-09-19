package com.java.ShiJingyi;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018/9/4.
 */

public class ListAdapter extends BaseAdapter implements AbsListView.OnScrollListener,AdapterView.OnItemClickListener{
    ArrayList<RssItem> myItems;
    ArrayList<View> itemViews;

    boolean atBottom;

    Context context;
    String category;
    String lastPubDate;
    String url;
    ListView myLv;
    View footer;

    android.os.Handler myHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                MyImageView imgView = itemViews.get(msg.arg1).findViewById(R.id.img);
                if(imgView.getDrawable() == null) {
                    if (myItems.get(msg.arg1).imgPath != null && !myItems.get(msg.arg1).imgPath.equals("")) {
                        //                    imgView.setImageURL("http:"+myItems.get(msg.arg1).imgUrl);
                        Bitmap b = getLoacalBitmap(myItems.get(msg.arg1).imgPath);
                        if (b != null && b.getWidth() > 400) {
                            b = setImgSize(b, 1000);
                            imgView.setImageBitmap(b);
                        }
                    }
                }
            }
        }
    };


    public ListAdapter(String cat, Context c,String u) {
        super();
        context = c;
        lastPubDate = "";
        url = u;
        category = cat;
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        addItems(dbHelper.queryNews(category,15));
        dbHelper.close();
    }

    public void setLv(ListView l){
        myLv = l;
    }
    public void setFooter(View f){
        footer = f;
    }

    void setItemViewGray(View itemView){
        TextView title = (TextView) itemView.findViewById(R.id.item_title);
        TextView content = (TextView) itemView.findViewById(R.id.item_description);
        TextView pubDate = (TextView) itemView.findViewById(R.id.pub_date);
        title.setTextColor(Color.GRAY);
        content.setTextColor(Color.GRAY);
        pubDate.setTextColor(Color.GRAY);
    }

    public void initItemView(View itemView,int position){

        TextView title = (TextView) itemView.findViewById(R.id.item_title);
        title.setText(myItems.get(position).title);
        TextView content = (TextView) itemView.findViewById(R.id.item_description);
        content.setText(myItems.get(position).description);
        TextView pubDate = (TextView) itemView.findViewById(R.id.pub_date);
        pubDate.setText(myItems.get(position).pubDate);
        if(myItems.get(position).hasChecked == 1){
            title.setTextColor(Color.GRAY);
            content.setTextColor(Color.GRAY);
            pubDate.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        setItemViewGray(itemViews.get(position));
        Intent intent = new Intent(context,WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("link",myItems.get(position).link);
        intent.putExtra("title",myItems.get(position).title);
        intent.putExtra("pubDate",myItems.get(position).pubDate);
        intent.putExtra("description",myItems.get(position).description);
        intent.putExtra("category",myItems.get(position).category);
        intent.putExtra("imgPath",myItems.get(position).imgPath);
        intent.putExtra("html","");
        myItems.get(position).hasChecked = 1;

        Intent intt = new Intent(context,MyIntentService.class);
        intt.putExtra("text",myItems.get(position).title+" "+myItems.get(position).description);
        context.startService(intt);

        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        dbHelper.changeSomething("pubdate",myItems.get(position).pubDate,"checked", "1");
        dbHelper.insertHistory(myItems.get(position).title, myItems.get(position).pubDate, myItems.get(position).description, myItems.get(position).link, myItems.get(position).imgPath);
        dbHelper.close();

        context.startActivity(intent);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int max = 6;
        if(myItems.size() < 6)
            max = myItems.size() - 1;
        for(int i = 0;i <= max;i++){
            if(!myItems.get(i).getImg){
                myItems.get(i).getImg = true;
                final int pos = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myItems.get(pos).imgUrl = myItems.get(pos).getImgUrl();

                        myItems.get(pos).saveImg();

                        if (!myItems.get(pos).imgPath.equals("")){
                            Message msg = new Message();
                            msg.what = 1;
                            msg.arg1 = pos;
                            myHandler.sendMessage(msg);
                            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
                            dbHelper.changeSomething("pubdate",myItems.get(pos).pubDate,"imagepath",myItems.get(pos).imgPath);
                            dbHelper.changeSomething("pubdate",myItems.get(pos).pubDate,"haveimg","1");
                            dbHelper.close();
                        }
                    }
                }).start();
            }else{
                if(!myItems.get(i).imgPath.equals("")) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = i;
                    myHandler.sendMessage(msg);
                }
            }
        }
        return itemViews.get(position);

    }

    public int addItems(ArrayList<RssItem> newRssItems){
        int count = 0;
        if(myItems == null && newRssItems != null){
            myItems = newRssItems;
            itemViews = new ArrayList<>();
            for(int i = 0;i < newRssItems.size();i++){
                View itemView =View.inflate(context,R.layout.item_layout,null);
                initItemView(itemView,i);
                itemViews.add(itemView);
            }
        }
        else if(newRssItems!=null){
            if(myItems.size() > 0){

                lastPubDate = myItems.get(0).pubDate;
                for(int i = 0;i < newRssItems.size();i++){
                    if(newRssItems.get(i).pubDate.compareTo(lastPubDate) > 0){
                        lastPubDate = newRssItems.get(i).pubDate;
                        count++;
                    }
                    else{
                        break;
                    }
                }
                for(int i = 0;i < count;i++){
                    myItems.add(i,newRssItems.get(i));
                    View itemView =View.inflate(context,R.layout.item_layout,null);
                    initItemView(itemView,i);
                    itemViews.add(i,itemView);
                }
            }
        }
        return count;
    }

    public void addToTail(ArrayList<RssItem> oldItems){
//        int count = 0;
        if(oldItems!=null){
            if(myItems.size() > 0){
                for(int i = 0;i < oldItems.size();i++){
                    myItems.add(oldItems.get(i));
                    View itemView =View.inflate(context,R.layout.item_layout,null);
                    initItemView(itemView,itemViews.size());
                    itemViews.add(itemView);
                }
            }
        }
        notifyDataSetChanged();
//        return count;

    }

    @Override
    public int getCount() {
        if(itemViews == null) return 0;
        return itemViews.size();
    }

    @Override
    public Object getItem(int position) {
        return itemViews.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                int first = myLv.getFirstVisiblePosition();
                int last = myLv.getLastVisiblePosition();
                for(int i = 0;i < myItems.size();i++){
                    if(i <= last && i >= first) {
                        if (!myItems.get(i).getImg) {
                            myItems.get(i).getImg = true;
                            final int pos = i;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    myItems.get(pos).imgUrl = myItems.get(pos).getImgUrl();
                                    myItems.get(pos).saveImg();
                                    if(!myItems.get(pos).imgPath.equals( "")) {
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.arg1 = pos;
                                        myHandler.sendMessage(msg);
                                        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
                                        dbHelper.changeSomething("pubdate",myItems.get(pos).pubDate,"imagepath",myItems.get(pos).imgPath);
                                        dbHelper.changeSomething("pubdate",myItems.get(pos).pubDate,"haveimg","1");
                                        dbHelper.close();
                                    }
                                }
                            }).start();
                        }else{
                            if(!myItems.get(i).imgPath.equals("")) {
                                Message msg = new Message();
                                msg.what = 1;
                                msg.arg1 = i;
                                myHandler.sendMessage(msg);

                            }
                        }
                    }
                }
                if(myItems.size() != 0) {
                    Log.d(TAG, "onScroll: " + view.getLastVisiblePosition()+myItems.size());
                    if (view.getLastVisiblePosition() == myItems.size()) {
                        if(atBottom) {
                            if(footer != null)
                                footer.setVisibility(View.VISIBLE);
                            loadMore();
                            atBottom = false;
                        }
                        atBottom = true;
                    }else{
                        atBottom = false;
                    }
                }
                break;
        }
    }

    public static void releaseImageViewResouce(MyImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        imageView.setImageBitmap(null);

    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public static Bitmap getLoacalBitmap(String url) {
        try{
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        }catch(FileNotFoundException e){
            e.printStackTrace(); return null;
        }
    }
    public static Bitmap setImgSize(Bitmap bm, int newWidth){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public void loadMore(){
        ArrayList<RssItem> rssItemss;
        DatabaseHelper dbHelper = new DatabaseHelper(context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        rssItemss = dbHelper.queryOldNews(myItems.get(myItems.size() - 1).category,myItems.get(myItems.size() - 1).pubDate);
        dbHelper.close();
        addToTail(rssItemss);

        if(rssItemss.size() > 0)
            ToastUtil.showToast(myLv.getContext(),"加载了"+rssItemss.size()+"条新闻",Toast.LENGTH_SHORT);
        else{
            if(footer != null) {
                TextView text = (TextView) footer.findViewById(R.id.list_foot);
                text.setText(R.string.nomore);
            }else{
                footer = myLv.findViewById(R.id.list_foot);

            }

            ToastUtil.showToast(myLv.getContext(),"没有更多啦",Toast.LENGTH_SHORT);
        }
    }
}

