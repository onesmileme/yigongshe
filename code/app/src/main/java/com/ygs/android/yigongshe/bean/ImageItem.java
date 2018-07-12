package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/7/12.
 */

public class ImageItem implements Serializable, Cloneable {
  public String imageUrl;
  public boolean isSelected;

  public ImageItem newInstance() {
    try {
      return (ImageItem) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }
}
