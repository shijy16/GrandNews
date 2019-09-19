package com.java.ShiJingyi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.java.ShiJingyi.ListAdapter.getLoacalBitmap;
import static com.java.ShiJingyi.ListAdapter.setImgSize;

/**
 * Created by admin on 2018/9/9.
 */

public class RecommandAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener,AbsListView.OnScrollListener{
    private ArrayList<RssItem> rssItems;
    private ArrayList<View> itemViews;
    SwipeRefreshLayout mysf;
    private int[] getImg;
    public RecommandAdapter(SwipeRefreshLayout sf){
        super();
        rssItems = new ArrayList<>();
        itemViews = new ArrayList<>();
        mysf = sf;
        init();
    }
    public void init(){
        itemViews.clear();
        rssItems.clear();
        ArrayList<String> loves;
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        loves = dbHelper.getBestLoveWord(10);
        dbHelper.close();
        rssItems = SearchEngine.search_love(loves,MainActivity.context);
        if(rssItems.size() == 0){
            View view =View.inflate(MainActivity.context,R.layout.item_layout,null);
            TextView title = view.findViewById(R.id.item_title);
            title.setText("暂无推荐");
            TextView date = view.findViewById(R.id.pub_date);
            date.setText("");
            TextView des = view.findViewById(R.id.item_description);
            des.setText("");
            itemViews.add(view);
            return;
        }

        while (rssItems.size() > 20){
            rssItems.remove(20);
        }
        getImg = new int[rssItems.size()];
        for(int i = 0;i <rssItems.size();i++){
            Log.d(TAG, "init: "+loves.toString()+i);
            View view =View.inflate(MainActivity.context,R.layout.item_layout,null);
            TextView title = view.findViewById(R.id.item_title);
            title.setText(rssItems.get(i).title);
            TextView date = view.findViewById(R.id.pub_date);
            date.setText(rssItems.get(i).pubDate);
            TextView des = view.findViewById(R.id.item_description);
            des.setText(rssItems.get(i).description);
            if(rssItems.get(i).hasChecked == 1) setItemViewGray(view);
            if(i <= 5) {
                getImg[i] = 1;
                if (rssItems.get(i).imgPath != null && !rssItems.get(i).imgPath.equals("")) {
                    ImageView img = view.findViewById(R.id.img);
                    try {

                        Bitmap b = getLoacalBitmap(rssItems.get(i).imgPath);
                        if (b != null && b.getWidth() > 400) {
                            b = setImgSize(b, 1000);
                            img.setImageBitmap(b);
                        }
                    }catch (Exception e){}
                }
            }
            itemViews.add(view);
        }
        notifyDataSetChanged();
    }

    void setItemViewGray(View itemView){
        TextView title = (TextView) itemView.findViewById(R.id.item_title);
        TextView content = (TextView) itemView.findViewById(R.id.item_description);
        TextView pubDate = (TextView) itemView.findViewById(R.id.pub_date);
        title.setTextColor(Color.GRAY);
        content.setTextColor(Color.GRAY);
        pubDate.setTextColor(Color.GRAY);
    }

    @Override
    public int getCount() {
        return itemViews.size();
    }

    @Override
    public Object getItem(int position) {
        return itemViews.get(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(rssItems.size() == 0) return;
        Intent intent = new Intent(MainActivity.context,WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("link",rssItems.get(position).link);
        intent.putExtra("title",rssItems.get(position).title);
        intent.putExtra("pubDate",rssItems.get(position).pubDate);
        intent.putExtra("description",rssItems.get(position).description);
        intent.putExtra("imgPath",rssItems.get(position).imgPath);
        intent.putExtra("html","");

        setItemViewGray(itemViews.get(position));

        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context,MainActivity.userName,"news.db", null, 1,MainActivity.urls);
        dbHelper.changeSomething("pubdate",rssItems.get(position).pubDate,"checked", "1");
        dbHelper.insertHistory(rssItems.get(position).title, rssItems.get(position).pubDate, rssItems.get(position).description, rssItems.get(position).link, rssItems.get(position).imgPath);
        dbHelper.close();

        MainActivity.context.startActivity(intent);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return itemViews.get(position);

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(rssItems.size() == 0){
            ToastUtil.showToast(MainActivity.context,"要看了新闻才能推荐哦~", Toast.LENGTH_SHORT);
        }
        int start = view.getFirstVisiblePosition();
        int end = view.getLastVisiblePosition();
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                for(int i = start;i <= end;i++){
                    if(getImg[i] == 0){
                        getImg[i] = 1;
                        if (rssItems.get(i).imgPath != null && !rssItems.get(i).imgPath.equals("")) {
                            ImageView img = itemViews.get(i).findViewById(R.id.img);
                            Bitmap b = getLoacalBitmap(rssItems.get(i).imgPath);
                            if (b != null && b.getWidth() > 400) {
                                b = setImgSize(b, 1000);
                                img.setImageBitmap(b);
                            }
                        }
                    }
                }
                break;
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onRefresh(){
        init();
        if(rssItems.size() == 0){
            ToastUtil.showToast(MainActivity.context,"要看了新闻才能推荐哦", Toast.LENGTH_SHORT);
        }else if(rssItems.size() < 20){
            ToastUtil.showToast(MainActivity.context,"多看一些新闻才会推荐更多", Toast.LENGTH_SHORT);
        }
        else{
            ToastUtil.showToast(MainActivity.context,"推荐页已是最新", Toast.LENGTH_SHORT);
        }
        mysf.setRefreshing(false);
    }
}
