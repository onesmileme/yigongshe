package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/7/18.
 */

public class ShareBean implements Serializable {
  public String title;
  public String description;
  public String url;

  public ShareBean(String title, String description, String url) {
    this.title = title;
    this.description = description;
    this.url = url;
  }
}
