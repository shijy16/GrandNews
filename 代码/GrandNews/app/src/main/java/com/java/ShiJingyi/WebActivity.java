package com.java.ShiJingyi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Attr;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.java.ShiJingyi.ListAdapter.getLoacalBitmap;
import static com.java.ShiJingyi.ListAdapter.setImgSize;
import static java.util.logging.Level.CONFIG;


/**
 * Created by admin on 2018/9/5.
 */

public class WebActivity extends Activity implements View.OnClickListener {
    String pubDate;
    String title;
    String link;
    String desciption;
    String imgPath;
    Intent intent;
    String html;
    WebSettings webSettings;
    FloatingActionsMenu fam;
    FloatingActionButton download, share;

    boolean down = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_main);
        final WebView web = findViewById(R.id.webView);
        fam = findViewById(R.id.fam);
        share = findViewById(R.id.share);
        download = findViewById(R.id.like);

        intent = getIntent();
        webSettings = web.getSettings();
        title = intent.getStringExtra("title");
        pubDate = intent.getStringExtra("pubDate");
        link = intent.getStringExtra("link");
        desciption = intent.getStringExtra("description");
        imgPath = intent.getStringExtra("imgPath");
        html = intent.getStringExtra("html");


        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(), MainActivity.userName, "news.db", null, 1, MainActivity.urls);
        if (dbHelper.checkLove(title)) {
            download.setImageResource(R.drawable.downloaded);
        } else {
            download.setImageResource(R.drawable.download);
        }
        dbHelper.close();
        fam.setOnClickListener(this);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent share_intent = new Intent(Intent.ACTION_SEND);
//
//                share_intent.setType("text/plain"); // 纯文本
//
//                share_intent.putExtra(Intent.EXTRA_SUBJECT, "title");
//                share_intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+pubDate+"\n"+desciption+"\n"+link);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                share_intent = Intent.createChooser(share_intent, "分享");
//                startActivity(share_intent);
//                ToastUtil.showToast(getApplicationContext(),"分享",Toast.LENGTH_SHORT);

                Bitmap b = null;

                if (imgPath != null && !imgPath.equals("")) {
                    b = getLoacalBitmap(imgPath);
                    if (b != null) {
                        b = setImgSize(b, 1000);
                    }
                }
                b = getShareingBitmap(50, b);

                if(!imgPath.equals("")) {
                    Intent share_intent = new Intent(Intent.ACTION_SEND);
                    share_intent.setAction(Intent.ACTION_SEND);
                    share_intent.setType("image/*");
                    share_intent.putExtra(Intent.EXTRA_SUBJECT, title);
                    share_intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + pubDate + "\n" + desciption + "\n" + link);
                    share_intent.putExtra("Kdescription", title + "\n" + pubDate + "\n" + desciption + "\n" + link);
                    share_intent.putExtra(Intent.EXTRA_STREAM, saveBitmap(b, title.substring(0, 10)));
                    share_intent = Intent.createChooser(share_intent, "分享");
                    startActivity(share_intent);
                    Toast.makeText(getApplicationContext(), "分享", Toast.LENGTH_SHORT).show();
                }else{
                        Intent share_intent = new Intent(Intent.ACTION_SEND);
                        share_intent.setAction(Intent.ACTION_SEND);
                        share_intent.setType("text/plain");
                        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);
                        share_intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + pubDate + "\n" + desciption + "\n" + link);
                        share_intent.putExtra("Kdescription", title + "\n" + pubDate + "\n" + desciption + "\n" + link);
                        share_intent = Intent.createChooser(share_intent, "分享");
                        startActivity(share_intent);
                        Toast.makeText(getApplicationContext(), "分享", Toast.LENGTH_SHORT).show();
                }

            }
        });


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context, MainActivity.userName, "news.db", null, 1, MainActivity.urls);
                if (dbHelper.checkLove(title)) {
                    dbHelper.deleteLove(title);
                    download.setImageResource(R.drawable.download);
                    ToastUtil.showToast(getApplicationContext(), "取消收藏", Toast.LENGTH_SHORT);
                } else {
                    web.loadUrl("javascript:window.customName.showSource(document.body.innerHTML)");
                    ToastUtil.showToast(getApplicationContext(), "已收藏", Toast.LENGTH_SHORT);
                    download.setImageResource(R.drawable.downloaded);
                }
            }
        });


        final ProgressBar pgb = findViewById(R.id.progressBarLoading);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        class JsBridge {
            @JavascriptInterface
            public void showSource(String html) {
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.context, MainActivity.userName, "news.db", null, 1, MainActivity.urls);
                dbHelper.insertLove(title, pubDate, desciption, link, imgPath, html);
            }
        }
        web.addJavascriptInterface(new JsBridge(), "customName");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pgb.setVisibility(View.GONE);
                    //progressBar.setProgress(newProgress);
                } else {
                    pgb.setVisibility(View.VISIBLE);
                    pgb.setProgress(newProgress);
                }
            }
        });
        if (html.equals(""))
            web.loadUrl(link);
        else {
            Log.d(TAG, "onCreate: " + html);
            if (isNetworkAvailable(getApplicationContext())) {
                web.loadUrl(link);
            } else {
                ToastUtil.showToast(getApplicationContext(), "网络连接不可用，加载本地文件", Toast.LENGTH_SHORT);
                web.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
        }
    }


    @Override
    public void onClick(View v){
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    private Bitmap getShareingBitmap(int textSize,Bitmap imageBitmap) {
        if(imageBitmap == null){
            imageBitmap = Bitmap.createBitmap(1000,5,Bitmap.Config.ARGB_8888);
        }
        Bitmap.Config config = imageBitmap.getConfig();

        int sourceBitmapHeight = imageBitmap.getHeight();
        int sourceBitmapWidth = imageBitmap.getWidth();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        TextPaint textpaint = new TextPaint(paint);
        textpaint.setTextSize(textSize);
        textpaint.setAntiAlias(true);
        StaticLayout title_layout = new StaticLayout(title, textpaint,
                sourceBitmapWidth, Layout.Alignment.ALIGN_CENTER, 1f, 1f, true);

        paint = new Paint();
        paint.setColor(Color.GRAY);
        textpaint = new TextPaint(paint);
        textpaint.setTextSize(textSize - 10);

        StaticLayout desc_layout = new StaticLayout(desciption, textpaint,
                sourceBitmapWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true);

        Bitmap share_bitmap = Bitmap.createBitmap(sourceBitmapWidth, sourceBitmapHeight +
                        title_layout.getHeight() + desc_layout.getHeight()+250,config );

        Canvas canvas = new Canvas(share_bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(imageBitmap, 0, 0, paint);
        canvas.translate(0, sourceBitmapHeight);
        title_layout.draw(canvas);

        canvas.translate(0, title_layout.getHeight());
        desc_layout.draw(canvas);

        Bitmap qrCode = create2DCoderBitmap(link,200,200);
        canvas.translate(0, desc_layout.getHeight());
        if(qrCode != null)
            canvas.drawBitmap(qrCode, sourceBitmapWidth/2-100,20, paint);

        return share_bitmap;
    }

    private static Uri saveBitmap(Bitmap bm, String picName) {
        try {
            String dir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/sharepic/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH, int QR_HEIGHT) {
        try {

            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = new QRCodeWriter().encode(url,BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];

            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }




}
