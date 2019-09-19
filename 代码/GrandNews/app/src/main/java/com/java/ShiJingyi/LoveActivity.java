package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by admin on 2018/9/8.
 */

public class LoveActivity extends Activity {
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.love_layout);
        ListView lv = findViewById(R.id.love_list);
        LoveAdapter adapter = new LoveAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(adapter);
        lv.setOnItemLongClickListener(adapter);
        ToastUtil.showToast(getApplicationContext(),"长按删除", Toast.LENGTH_LONG);
    }
}
