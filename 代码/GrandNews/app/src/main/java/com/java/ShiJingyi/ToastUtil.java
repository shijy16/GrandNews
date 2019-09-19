package com.java.ShiJingyi;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 2018/9/9.
 */

public class ToastUtil {
private static Toast mToast;

    public static void showToast(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        }else {
            mToast.setText(msg);
            }
        mToast.show();
    }
}