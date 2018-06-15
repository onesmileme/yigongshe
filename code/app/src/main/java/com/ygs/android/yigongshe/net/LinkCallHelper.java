package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.net.callback.ServiceGenarator;
import retrofit2.Response;

/**
 * Created by xiejiangquan on 2016/6/17.
 * Linkcall封装，主要处理网络请求及回传结果,不负责call对象销毁
 */
public class LinkCallHelper {

  private LinkCallHelper() {

  }

  //private static class SingletonHolder {
  //
  //  public final static LinkCallHelper instance = new LinkCallHelper();
  //}
  //
  //public static LinkCallHelper getInstance() {
  //  return SingletonHolder.instance;
  //}

  public interface IDataCallback<L> {
    void onResponse(L entity);
  }

  public interface ILoadFinish<L> {
    void onLoadFinished(L entity);
  }

  /**
   * 发起网络请求，请求数据
   */
  public static <S> void doPullData(LinkCall<S> call, final IDataCallback callback) {
    call.enqueue(new LinkCallbackAdapter<S>() {
      @Override public void onResponse(S entity, Response<?> response, Throwable throwable) {
        //数据透传，用户自行检查数据
        callback.onResponse(entity);
      }
    });
  }

  public static ApiService getApiService() {
    return ServiceGenarator.createService(ApiService.class);
  }
}
