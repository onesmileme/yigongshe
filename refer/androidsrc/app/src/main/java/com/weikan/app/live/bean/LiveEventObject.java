package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author kailun on 16/8/31.
 */
public class LiveEventObject {

    public static final int TYPE_USER = 1;
    public static final int TYPE_SYSTEM = 2;

    @JSONField(name = "timestamp")
    public long timestamp;

    @JSONField(name = "content")
    public String content = "";

    @JSONField(name = "uid")
    public String uid = "";

    @JSONField(name = "nickname")
    public String nickname = "";

    @JSONField(name = "type")
    public int type;

    public String liveUid="";

    @Override
    public boolean equals(Object o) {
        if (o instanceof LiveEventObject) {
            return timestamp == ((LiveEventObject) o).timestamp && content.equals(((LiveEventObject) o).content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (content + timestamp).hashCode();
    }
}
