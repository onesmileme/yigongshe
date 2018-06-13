package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author kailun on 16/9/3.
 */
public class NewLiveObject {

    @JSONField(name = "live_info")
    public String liveInfo;

    @JSONField(name = "live_id")
    public long liveId;
}
