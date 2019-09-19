package com.java.ShiJingyi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by admin on 2018/9/6.
 */

public class AccountDialog extends Dialog {
    private Activity context;

    private Button btn_sure;
    private Button btn_cancel;
    public EditText account;
    Handler handler;
    int method;


    public AccountDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public AccountDialog(Activity context, int theme,Handler h,int m) {
        super(context, theme);
        this.context = context;
        handler = h;
        method = m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_layout);

        account = findViewById(R.id.account_input);
        btn_cancel = findViewById(R.id.account_cancel);
        btn_sure = findViewById(R.id.account_sure);


        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = dialogWindow.getAttributes();

//        dialogWindow.setAttributes(p);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = method;
                Bundle bundle = new Bundle();
                bundle.putString("name",account.getText().toString());
                msg.setData(bundle);
                handler.sendMessage(msg);
                hide();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account.setText("");
                hide();
            }
        });
    }

}
