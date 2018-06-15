package com.ygs.android.yigongshe.bean.base;

/**
 * 返回基本数据
 *
 * @author hlwang
 */
public class BaseResultInfo {
  /**
   * 返回数据Header
   */
  // public ResultHeader responseHeader;

  /**
   * 错误码
   */
  public int errno;

  /**
   * 错误原因
   */
  public String error;

  public String request_id;

  public int getErrno() {
    return errno;
  }

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getRequest_id() {
    return request_id;
  }

  public void setRequest_id(String request_id) {
    this.request_id = request_id;
  }
}
