package com.java.ShiJingyi;

import java.util.ArrayList;

/**
 * Created by admin on 2018/9/2.
 */

public class Url {
    String name;
    String url;
    boolean show;
    boolean refresh;
    public Url(String n,String u,boolean s){
        name = n;
        url = u;
        show = s;
        refresh = true;
    }
}
