package com.java.ShiJingyi;

import android.app.Dialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.EventLog;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DatabaseHelper dbHelper;

    public static ArrayList<Url> urls;
    private TabLayout tablayout;
    private ViewPager viewpager;
    public static Context context;
    AccountDialog dialog;
    public static  String userName;
    public static boolean smartMode = false;
    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 2){
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(),MainActivity.userName,"news.db", null, 1,MainActivity.urls);
                String name = msg.getData().getString("name");
                if(dbHelper.tabbleIsExist(name)){
                    ToastUtil.showToast(context,"用户"+userName+"已经存在,已为您切换",Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.showToast(context,"创建新用户"+name,Toast.LENGTH_SHORT);
                    dbHelper.create("'"+name+"'","(love TEXT PRIMARY KEY,time INTEGER)");
                    dbHelper.create("'"+name+"History'","(title TEXT,pubdate TEXT,description TEXT,link TEXT,imagepath TEXT,id INTEGER PRIMARY KEY AUTOINCREMENT)");
                    dbHelper.create("'"+name+"Save'","(user TEXT,title TEXT,pubdate TEXT,description TEXT,link TEXT,imagepath TEXT,html Text)");
                }
                dbHelper.close();
                userName = msg.getData().getString("name");
                TextView v = findViewById(R.id.username);
                v.setText(userName);
            }else{
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(),MainActivity.userName,"news.db", null, 1,MainActivity.urls);
                String name = msg.getData().getString("name");
                if(dbHelper.tabbleIsExist(name)){
                    userName = msg.getData().getString("name");
                    ToastUtil.showToast(context,"已切换用户至"+userName,Toast.LENGTH_SHORT);
                    TextView v = findViewById(R.id.username);
                    v.setText(userName);
                }else{
                    ToastUtil.showToast(context,"用户"+name+"不存在",Toast.LENGTH_SHORT);
                }
                dbHelper.insertLoveKey("明天");
                dbHelper.insertLoveKey("今天");
                dbHelper.insertLoveKey("今天");
                Log.d("?", "handleMessage: "+dbHelper.getBestLoveWord(1));

                dbHelper.close();
                DatabaseHelper.user = userName;

            }
            viewpager.getAdapter().notifyDataSetChanged();

        }
    };


    private long lastPress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initList();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Grand News");
        toolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView usname = navigationView.getHeaderView(0).findViewById(R.id.username);
        userName = usname.getText().toString();

        context = this.getApplicationContext();

        tablayout = findViewById(R.id.tablayout);
        viewpager = new MyViewPager(getApplicationContext());

        viewpager = (MyViewPager) findViewById(R.id.viewpager);

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition(),true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(),context,urls);
        tablayout.setTabsFromPagerAdapter(tabAdapter);
        viewpager.setAdapter(tabAdapter);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            create_checklog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_account) {
            create_dialog(1);
        } else if (id == R.id.load_account) {
            create_dialog(2);
        }else if(id == R.id.mylove){
            Intent intent = new Intent(context,LoveActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(id == R.id.search_menu){
            Intent intent = new Intent(context,SearchActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(id == R.id.mode){
            new AlertDialog.Builder(this)
                    .setTitle("开启/关闭爆炸智能模式")
                    .setMessage("您当前使用的是"+ (!smartMode?"普通智能":"爆炸智能")+"模式，开启爆炸智能模式后会加强推荐页使用体验，但是可能会出现卡顿，请确认选择")
                    .setPositiveButton("爆炸智能模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.smartMode = true;
                            ToastUtil.showToast(context,"已开启爆炸智能推荐模式",Toast.LENGTH_SHORT);
                        }
                    })

                    .setNegativeButton("普通智能模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.smartMode = false;
                            ToastUtil.showToast(context,"已开启普通智能模式",Toast.LENGTH_SHORT);
                        }
                    }).show();
        }else if(id == R.id.history){
            Intent intent = new Intent(context,HistoryActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - lastPress) > 2000) {
                ToastUtil.showToast(this, "再按一次退出程序", Toast.LENGTH_SHORT);
                lastPress = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void create_dialog(int m) {
        dialog = new AccountDialog(this, R.style.Theme_AppCompat_Dialog,handler,m);
        dialog.show();
    }

    public void create_checklog(){
        final ArrayList<String> names = new ArrayList<>();
        final ArrayList<Boolean> shows = new ArrayList<>();
        for(int i = 0;i < urls.size();i++){
            names.add(urls.get(i).name);
            shows.add(urls.get(i).show);
        }
        final CheckListAdapter adapter = new CheckListAdapter(context,names,shows);
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("选择类别").
                setNegativeButton("取消",null).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateList(adapter.getState());
                    }
                }).
                setAdapter(adapter,null).create();
        alertDialog.show();
    }

    private void updateList(ArrayList<Boolean> s){
        if(s == null || s.size() != urls.size()){
           ToastUtil.showToast(context,"程序出错，修改失败",Toast.LENGTH_SHORT);
        }else{
            boolean flag = false;
            for(int i = 0;i < s.size();i++){
                if(urls.get(i).show != s.get(i)){
                    urls.get(i).show = s.get(i);
                    flag = true;
                }
            }
            if(flag) {
                TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), context, urls);
                tablayout.setTabsFromPagerAdapter(tabAdapter);
                viewpager.setAdapter(tabAdapter);
                ToastUtil.showToast(context, "修改成功", Toast.LENGTH_SHORT);
            }else{
                ToastUtil.showToast(context, "未修改", Toast.LENGTH_SHORT);
            }
        }
    }

    private void initList() {
        urls = new ArrayList<>();
//        urls.add(new Url("推荐", "", true));
//        urls.add(new Url("即时新闻", "http://www.chinanews.com/rss/scroll-news.xml", true));
//        urls.add(new Url("财经新闻", "http://tech.qq.com/web/rss_web.xml", true));
//        urls.add(new Url("国内新闻", "http://news.qq.com/newsgn/rss_newsgn.xml", true));
//        urls.add(new Url("国际要闻", "http://news.qq.com/newsgj/rss_newswj.xml", true));
//        urls.add(new Url("社会新闻","http://news.qq.com/newssh/rss_newssh.xml",true));
//        urls.add(new Url("军事","http://news.qq.com/milite/rss_milit.xml",true));
//        urls.add(new Url("体育", "http://sports.qq.com/rss_newssports.xml", false));
//        urls.add(new Url("娱乐", "http://ent.qq.com/movie/rss_movie.xml", true));
//        urls.add(new Url("北京","http://news.qq.com/bj/rss_bj.xml",true));
        urls.add(new Url("推荐", "", true));
        urls.add(new Url("国内新闻", "http://news.qq.com/newsgn/rss_newsgn.xml", true));
        urls.add(new Url("财经新闻", "http://tech.qq.com/web/rss_web.xml", true));
        urls.add(new Url("IT新闻", "http://www.chinanews.com/rss/it.xml", true));
        urls.add(new Url("社会新闻","http://news.qq.com/newssh/rss_newssh.xml",true));
        urls.add(new Url("娱乐", "http://ent.qq.com/movie/rss_movie.xml", true));
        urls.add(new Url("军事","http://news.qq.com/milite/rss_milit.xml",true));
        urls.add(new Url("体育", "http://sports.qq.com/rss_newssports.xml", false));
        urls.add(new Url("北京","http://news.qq.com/bj/rss_bj.xml",true));
        urls.add(new Url("国际要闻", "http://www.chinanews.com/rss/world.xml", false));
        urls.add(new Url("即时新闻", "http://www.chinanews.com/rss/scroll-news.xml", false));

    }

}
