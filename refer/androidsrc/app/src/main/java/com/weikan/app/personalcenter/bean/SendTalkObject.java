package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by ylp on 2016/11/21.
 */

public class SendTalkObject {

    @JSONField(name = "mid")
    public  Long mid= 0l;

    @JSONField(name = "content")
    public  String content= null;

    public Long getMid() {
        return mid;
    }

    public String getContent() {
        return content;
    }
}
