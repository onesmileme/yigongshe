package com.weikan.app.wxapi;

/**
 * Created by wutong on 1/7/16.
 */
public class WechatPaymentEvent {
    public int errno;
    public WechatPaymentEvent(int errno){
        this.errno = errno;
    }
}
