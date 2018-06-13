package com.weikan.app.common.Model;

import android.app.Activity;
import android.content.SharedPreferences;
import com.weikan.app.MainApplication;
import com.weikan.app.personalcenter.bean.MyMsgRedObject;
import com.weikan.app.util.PrefDefine;

/**
 * Created by liujian on 16/10/26.
 */
public class RedNoticeModel {
    /** 文友圈最新刷新时间 */
    private static long momentRefreshTime = -1;
    /** 帖子列表最新刷新时间 */
    private static long tweetRefreshTime = -1;
    /** 最近一次的红点数据 */
    public static MyMsgRedObject lastRedObject = null;

    private static SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(PrefDefine.PREF_FILE, Activity.MODE_PRIVATE);

    private static String REF_TIME_TWEET = "TweetRefreshTime";
    private static String REF_TIME_MOMENT = "MomentRefreshTime";

    /**
     * 保存文友圈最新刷新时间
     */
    public static void saveMomentRefreshTime(){
        momentRefreshTime = System.currentTimeMillis();

        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(REF_TIME_MOMENT, momentRefreshTime);
        editor.apply();
    }

    public static long getMomentRefreshTime(){
        if (momentRefreshTime < 0) {
            momentRefreshTime = sp.getLong(REF_TIME_MOMENT, 0);
        }
        return momentRefreshTime;
    }

    /**
     * 保存帖子列表最新刷新时间
     */
    public static void saveTweetRefreshTime(){
        tweetRefreshTime = System.currentTimeMillis();

        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(REF_TIME_TWEET, tweetRefreshTime);
        editor.apply();
    }

    public static long getTweetRefreshTime(){
        if (tweetRefreshTime < 0) {
            tweetRefreshTime = sp.getLong(REF_TIME_TWEET, 0);
        }
        return tweetRefreshTime;
    }
}
