package com.java.ShiJingyi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.OnTextChanged;

import static android.content.ContentValues.TAG;
import static com.java.ShiJingyi.ListAdapter.getLoacalBitmap;
import static com.java.ShiJingyi.ListAdapter.setImgSize;

/**
 * Created by admin on 2018/9/8.
 */

public class SearchAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,TextWatcher,AbsListView.OnScrollListener{
    private ArrayList<RssItem> rssItems;
    private EditText editText;
    private int[] getImg;
    private ArrayList<View> itemViews;

    public SearchAdapter(){
        super();
        rssItems = new ArrayList<>();
    }
    public void setEditText(EditText t){
        editText = t;
        editText.addTextChangedListener(this);
    }

    @Override
    public int getCount() {
        return rssItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rssItems.get(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick:?????????");
        Intent intent = new Intent(SearchActivity.context,WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("link",rssItems.get(position).link);
        intent.putExtra("title",rssItems.get(position).title);
        intent.putExtra("pubDate",rssItems.get(position).pubDate);
        intent.putExtra("description",rssItems.get(position).description);
        intent.putExtra("imgPath",rssItems.get(position).imgPath);
        intent.putExtra("html","");

        SearchActivity.context.startActivity(intent);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//
//        convertView = LayoutInflater.from(SearchActivity.context).inflate(R.layout.item_layout,parent,false);
//        TextView title = convertView.findViewById(R.id.item_title);
//        title.setText(Html.fromHtml(rssItems.get(position).title));
//        TextView date = convertView.findViewById(R.id.pub_date);
//        date.setText(Html.fromHtml(rssItems.get(position).pubDate));
//        TextView des = convertView.findViewById(R.id.item_description);
//        des.setText(Html.fromHtml(rssItems.get(position).description));
//        ImageView img = convertView.findViewById(R.id.img);
//        if(getImg[position] == 0) {
//            getImg[position] = 1;
//            if (rssItems.get(position).imgPath != null && rssItems.get(position).imgPath != "") {
//                Log.d(TAG, "getView: "+rssItems.get(position).imgPath);
//                Bitmap b = getLoacalBitmap(rssItems.get(position).imgPath);
//                if (b != null && b.getWidth() > 500) {
//                    b = setImgSize(b, 1000);
//                    img.setImageBitmap(b);
//                }
//            }
//        }

        return itemViews.get(position);

    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!editText.getText().toString().equals("")) {
            rssItems = SearchEngine.search(editText.getText().toString(), SearchActivity.context);
            getImg = new int[rssItems.size()];
            itemViews = new ArrayList<>();
            for(int i = 0;i <rssItems.size();i++){
                View view =View.inflate(SearchActivity.context,R.layout.item_layout,null);
                TextView title = view.findViewById(R.id.item_title);
                title.setText(Html.fromHtml(rssItems.get(i).title));
                TextView date = view.findViewById(R.id.pub_date);
                date.setText(Html.fromHtml(rssItems.get(i).pubDate));
                TextView des = view.findViewById(R.id.item_description);
                des.setText(Html.fromHtml(rssItems.get(i).description));
                if(i <= 5) {
                    getImg[i] = 1;
                    if (rssItems.get(i).imgPath != null && !rssItems.get(i).imgPath.equals("")) {
                        ImageView img = view.findViewById(R.id.img);
                        Bitmap b = getLoacalBitmap(rssItems.get(i).imgPath);
                        if (b != null && b.getWidth() > 400) {
                            b = setImgSize(b, 1000);
                            img.setImageBitmap(b);
                        }
                    }
                }
                itemViews.add(view);
            }

        }else{
                rssItems = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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

}
