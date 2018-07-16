package com.ygs.android.yigongshe.bean;

/**
 * Created by ruichao on 2018/7/17.
 */

public class LocationEvent {
  private String cityname;

  public LocationEvent(String cityname) {
    this.cityname = cityname;
  }

  public String getCityname() {
    return cityname;
  }

  public void setCityname(String cityname) {
    this.cityname = cityname;
  }
}
