package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * @author kailun on 2016/12/26.
 */
public class OriginalItemData {

    @JSONField(name = "content")
    public ArrayList<OriginalItem> content = new ArrayList<>();
}
