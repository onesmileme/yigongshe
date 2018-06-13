package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kailun on 16/8/27.
 */
public class LiveListDataObject {

    @JSONField(name = "content")
    public List<LiveListObject> content = new ArrayList<>();
}
