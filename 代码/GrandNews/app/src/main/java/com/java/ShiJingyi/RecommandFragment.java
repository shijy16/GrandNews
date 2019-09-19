package com.java.ShiJingyi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/9/9.
 */

public class RecommandFragment extends Fragment {
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout,container,false);
        ListView lv = view.findViewById(R.id.list_view);
        SwipeRefreshLayout rf = view.findViewById(R.id.swipe_news);
        RecommandAdapter adapter = new RecommandAdapter(rf);
        rf.setOnRefreshListener(adapter);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(adapter);
        return view;
    }

}
