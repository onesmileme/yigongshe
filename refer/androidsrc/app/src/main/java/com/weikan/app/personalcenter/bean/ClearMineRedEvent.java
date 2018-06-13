package com.weikan.app.personalcenter.bean;

/**
 * Created by liujian on 16/10/26.
 */
public class ClearMineRedEvent {

    public static int CLEAR_SYSMSG = 1;
    public static int CLEAR_FANS = 2;
    public static int CLEAR_TALK = 4;

    /**
     * 按位计算
     */
    public int clearTag = 0;
    public ClearMineRedEvent(int tag){
        clearTag = tag;
    }
}
