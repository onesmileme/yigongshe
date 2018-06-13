package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Collections;
import java.util.List;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public class ImageEditObject {
    @JSONField(name = "content")
    public List<ImageNtsWithModifiedObject> content = Collections.emptyList();
}
