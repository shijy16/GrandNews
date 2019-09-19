package com.java.ShiJingyi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by admin on 2018/9/6.
 */

public class CheckListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<Boolean> state = new ArrayList<>();

    //构造函数
    public CheckListAdapter(Context context,ArrayList<String> i,ArrayList<Boolean> s) {
        this.context = context;
        items = i;
        state = s;
        for(int x = 0;x < s.size();x++){
            Log.d("?????????????", "CheckListAdapter: "+state.get(x));
        }
    }

    @Override
    public int getCount() {
        return state.size();
    }

    @Override
    public Object getItem(int position) {
        return state.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Boolean> getState(){
        return state;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.checkbox_layout,parent,false);
        final TextView text = view.findViewById(R.id.checkbox_text);
        text.setText(items.get(position));
        CheckBox checkBox = view.findViewById(R.id.box);
        checkBox.setChecked(state.get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                state.set(position, isChecked);
                if(isChecked)
                    text.setTextColor(Color.BLACK);
                else
                    text.setTextColor(Color.GRAY);
            }
        });
        return view;
    }

}
