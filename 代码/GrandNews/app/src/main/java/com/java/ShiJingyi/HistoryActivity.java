package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by admin on 2018/9/10.
 */

public class HistoryActivity extends Activity {
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.love_layout);
        ListView lv = findViewById(R.id.love_list);
        TextView tx = findViewById(R.id.love_text);
        tx.setText("历史记录");
        HistoryAdapter adapter = new HistoryAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(adapter);
        lv.setOnItemLongClickListener(adapter);
        ToastUtil.showToast(getApplicationContext(),"长按删除\n浏览记录保留最近20条", Toast.LENGTH_LONG);
    }
}
