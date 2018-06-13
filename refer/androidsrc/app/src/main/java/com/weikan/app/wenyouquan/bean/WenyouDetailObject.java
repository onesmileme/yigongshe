package com.weikan.app.wenyouquan.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by liujian on 16/7/31.
 */
public class WenyouDetailObject {
    @JSONField(name = "content")
    public WenyouListData.WenyouListItem content;
}
