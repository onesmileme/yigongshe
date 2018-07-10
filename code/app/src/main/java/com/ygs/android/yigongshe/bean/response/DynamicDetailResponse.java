package com.ygs.android.yigongshe.bean.response;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/7/10.
 */

public class DynamicDetailResponse implements Serializable {
  public DynamicDetailBean news_info;

  public class DynamicDetailBean {
    public String content;
  }
}
