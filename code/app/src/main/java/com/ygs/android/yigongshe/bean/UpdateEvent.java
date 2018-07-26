package com.ygs.android.yigongshe.bean;

/**
 * Created by ruichao on 2018/7/26.
 */

public class UpdateEvent extends BaseEvent {
  private int page;//0,1,2,3

  public UpdateEvent(int page) {
    this.page = page;
  }

  public int getPage() {
    return page;
  }
}
