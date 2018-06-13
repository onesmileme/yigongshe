package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * @author kailun on 16/8/31.
 */
public class OnlineUserListDataObject {
    @JSONField(name = "num")
    public int num;

    @JSONField(name = "list")
    public ArrayList<OnlineUserObject> list = new ArrayList<>();
}
