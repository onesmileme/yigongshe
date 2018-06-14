package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/6/14.
 */

public class DynamicItemBean implements Serializable {
  public DynamicItemBean(String title) {
    this.title = title;
  }

  public String title;
}
