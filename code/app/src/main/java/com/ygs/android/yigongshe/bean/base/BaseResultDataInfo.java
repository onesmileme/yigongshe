package com.ygs.android.yigongshe.bean.base;

/**
 * 接口返回基本数据结构
 *
 * @param <T> 返回数据对象
 * @author hlwang
 */
public class BaseResultDataInfo<T> extends BaseResultInfo {

  /**
   * 返回数据对象
   */
  public T data;

  public T getData() {
    return data;
  }
}
