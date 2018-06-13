package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/10
 */
public class UploadImageObject {
    @JSONField(name = "img")
    public ImageNtsObject img;
}
