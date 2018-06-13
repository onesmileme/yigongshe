package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.original.bean.OriginalItem;

import java.util.ArrayList;

/**
 * Created by ylp on 2016/11/30.
 */

public class MyCollectListObject {
        @JSONField(name = "tweet_list")
        public ArrayList<OriginalItem> tweet_list;
}
