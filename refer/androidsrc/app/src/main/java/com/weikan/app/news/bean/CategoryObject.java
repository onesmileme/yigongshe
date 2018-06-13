package com.weikan.app.news.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author kailun on 16/11/22.
 */
public class CategoryObject {
    @JSONField(name = "uid")
    public String uid = "";

    @JSONField(name = "sort")
    public String sort = "";

    @JSONField(name = "status")
    public String status = "";

    @JSONField(name = "ctime")
    public String ctime = "";

    @JSONField(name = "catid")
    public int catid = 0;

    @JSONField(name = "cname")
    public String cname = "";
}
