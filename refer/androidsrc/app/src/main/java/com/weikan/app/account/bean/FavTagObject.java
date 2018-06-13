package com.weikan.app.account.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 兴趣标签数据
 * Created by liujian on 16/3/12.
 */
public class FavTagObject {
    @JSONField(name="interest_tag")
    public List<String> interest_tag;
}
