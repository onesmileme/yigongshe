package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 评论列表对象
 * Created by liujian on 16/3/20.
 */
public class OriginalCommentListObject {
    @JSONField(name = "content")
    public List<OriginalCommentObject> content;
}
