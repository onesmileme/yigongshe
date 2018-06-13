package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.wenyouquan.bean.WenyouListData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ylp on 2016/11/6.
 */

public class OriginalListData implements Serializable{

    @JSONField(name = "content")
    public Comment content;

    public static class Comment implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "comment_list")
        public ArrayList<WenyouListData.CommentItem> commentList = new ArrayList<>();
    }
}
