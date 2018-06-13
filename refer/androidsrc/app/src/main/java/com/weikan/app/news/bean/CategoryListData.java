package com.weikan.app.news.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kailun on 16/11/22.
 */
public class CategoryListData {

    @JSONField(name = "category_list")
    public List<CategoryObject> categoryList = new ArrayList<>();
}
