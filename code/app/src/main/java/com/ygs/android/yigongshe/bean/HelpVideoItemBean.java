package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoItemBean implements Serializable {
  public String title; //视频名称
  public String src;//视频url
  public String thumbnail;//视频缩略图
  public int create_id;//发送者id
  public String videoid;//视频id
  public String create_name;//发送者name
  public String create_at;
  public int is_mine;
  public String avatar;
}
