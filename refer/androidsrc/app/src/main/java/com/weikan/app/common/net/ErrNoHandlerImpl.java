package com.weikan.app.common.net;

import android.app.Activity;
import com.weikan.app.AppManager;
import com.weikan.app.account.AccountManager;
import platform.http.responsehandler.IErrNoHandler;

/**
 * Created by liujian on 16/12/19.
 */
public class ErrNoHandlerImpl implements IErrNoHandler {
    @Override
    public boolean onErrNo(int errNo, String errMsg, String url) {
        boolean ret = false;
        switch (errNo){
            case 10011:
            case 20002:
            case 20003: {
                // 登陆失效，跳登陆
                Activity context = AppManager.currentActivity();
                if (context != null) {
                    AccountManager.getInstance().logout( context);
                    AccountManager.getInstance().gotoLogin(context);
                }
                ret = true;
                break;
            }
            default:
                ret = false;
                break;
        }
        return ret;
    }
}