package com.weikan.app.account;

import com.weikan.app.bean.UserInfoObject;

/**
 * 获取到用户信息的事件.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 17:28
 */
public class AccountObtainEvent {
    public UserInfoObject.UserInfoContent data;
    AccountObtainEvent(UserInfoObject.UserInfoContent d){
        data = d;
    }
}
