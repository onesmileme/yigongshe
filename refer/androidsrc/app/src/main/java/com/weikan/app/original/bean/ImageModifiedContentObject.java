package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public class ImageModifiedContentObject {

    @JSONField(name = "origin")
    public ImageNtsObject origin;

    @JSONField(name = "modified")
    public ImageNtsObject modified;

    @JSONField(name = "overlays")
    public List<OverlayObject> overlays = Collections.emptyList();

}
