package com.java.ShiJingyi;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by admin on 2018/9/4.
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(Context context){
        super(context);
    }
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e){
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(e);
    }
}
