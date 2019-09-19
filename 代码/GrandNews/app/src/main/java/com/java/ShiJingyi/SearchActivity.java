package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by admin on 2018/9/8.
 */

public class SearchActivity extends Activity {
    public EditText editText;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.search_layout);
        editText = findViewById(R.id.search_input);
        ListView lv = findViewById(R.id.search_list);
        SearchAdapter adapter = new SearchAdapter();
        adapter.setEditText(editText);
        lv.setOnScrollListener(adapter);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(adapter);
    }
}
