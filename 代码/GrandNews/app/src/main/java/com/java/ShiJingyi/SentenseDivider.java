package com.java.ShiJingyi;

/**
 * Created by admin on 2018/9/9.
 */

import android.util.Log;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.ArrayList;
import java.util.List;


public class SentenseDivider {
//    public static ArrayList<String> divideChinese(String sentense){
//        JiebaSegmenter segmenter = new JiebaSegmenter();
//        ArrayList<String> output = new ArrayList<>();
//        List<SegToken> tokens =  segmenter.process(sentense, JiebaSegmenter.SegMode.SEARCH);
//        for( SegToken token : tokens){
//            if( !token.word.isEmpty() )	output.add(token.word);
//        }
//        return output;
//    }

    public static ArrayList<String> divideChinese(String sentense){
        if(!MainActivity.smartMode) {
            ArrayList<String> output = new ArrayList<>();
            String tmpString = sentense.replaceAll("[^\u4E00-\u9FA5]", " ");
            Log.d(">>>>>>>>>>>>>>>>>>>>>", "divideChinese: " + tmpString);
            String[] splitString = tmpString.split(" ");
            for(int i = 0;i < splitString.length;i++){
                for(int j = 0;j < splitString[i].length();j+=2){
                    splitString[i] = splitString[i].replace("在","");
                    splitString[i] = splitString[i].replace("的","");
                    splitString[i] = splitString[i].replace("和","");
                    splitString[i] = splitString[i].replace("了","");
                    splitString[i] = splitString[i].replace("中新网","");
                    splitString[i] = splitString[i].replace("中新社","");
                    splitString[i] = splitString[i].replace("月","");
                    splitString[i] = splitString[i].replace("日","");
                    splitString[i] = splitString[i].replace("电","");
                    if(j+2 < splitString[i].length())
                        output.add(splitString[i].substring(j,j+2).replace("的",""));
                }
            }
            return output;
        }else{
            JiebaSegmenter segmenter = new JiebaSegmenter();
            ArrayList<String> output = new ArrayList<>();
            List<SegToken> tokens =  segmenter.process(sentense, JiebaSegmenter.SegMode.SEARCH);
            for( SegToken token : tokens){
                if( !token.word.isEmpty() )	output.add(token.word);
            }
            return output;
        }
    }
}
