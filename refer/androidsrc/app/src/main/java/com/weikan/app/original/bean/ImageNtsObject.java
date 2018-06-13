package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/8
 */
public class ImageNtsObject implements Serializable {

    @JSONField(name = "n")
    public ImageObject n;

    @JSONField(name = "t")
    public ImageObject t;

    @JSONField(name = "s")
    public ImageObject s;
}
