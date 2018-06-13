package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Collections;
import java.util.List;

/**
 * 贴图素材列表请求结果
 *
 * @author kailun on 16/4/8
 */
public class MaterialListObject {

    @JSONField(name = "content")
    public Content content = new Content();

    public static class Content {

        @JSONField(name = "chartlet")
        public List<ImageNtsObject> chartlet = Collections.emptyList();
    }
}
