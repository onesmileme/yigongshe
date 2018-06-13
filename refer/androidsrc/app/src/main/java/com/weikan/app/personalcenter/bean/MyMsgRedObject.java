package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by liujian on 16/7/31.
 */
public class MyMsgRedObject {

    @JSONField(name = "sys_num")
    public  int sysNum;
    @JSONField(name = "new_fans_num")
    public  int new_fans_num;
    @JSONField(name = "pmsg")
    public  int pmsg;

//    @JSONField(name = "mine_num")
//    public  int mine_num;
    @JSONField(name = "moments_num")
    public  int moments_num;
    @JSONField(name = "tweet_num")
    public  int tweet_num;
}
