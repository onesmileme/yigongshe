package com.weikan.app.bean;

/**
 * Created by liujian on 16/3/14.
 */
public class ShareResultEvent {
    public boolean isShareSuccess;
    public String tid;

    public ShareResultEvent(boolean suc, String tid){
        isShareSuccess = suc;
        this.tid = tid;
    }
}
