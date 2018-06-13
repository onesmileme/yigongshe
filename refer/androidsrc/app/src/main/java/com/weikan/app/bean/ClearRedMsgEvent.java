package com.weikan.app.bean;

/**
 * Created by liujian on 16/10/26.
 */
public class ClearRedMsgEvent {
    public static int CLEAR_TWEET = 1;
    public static int CLEAR_MOMENT = 2;
    public static int CLEAR_MINE = 4;

    /**
     * 按位计算
     */
    public int clearTag = 0;
    public ClearRedMsgEvent(int tag){
        clearTag = tag;
    }
}
