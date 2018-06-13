package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * @author kailun on 16/8/31.
 */
public class LiveDetailDataObject {

    @JSONField(name = "live_uid")
    public String liveUid = "";

    @JSONField(name = "live_headimg")
    public String liveHeadImg = "";

    @JSONField(name = "stime")
    public int stime;

    @JSONField(name = "etime")
    public int etime;

    @JSONField(name = "status")
    public int status;

    @JSONField(name = "online_num")
    public int onlineNum;

    @JSONField(name = "url")
    public String url = "";

    @JSONField(name = "online_users")
    public ArrayList<OnlineUserObject> onlineUsers = new ArrayList<>();

}
