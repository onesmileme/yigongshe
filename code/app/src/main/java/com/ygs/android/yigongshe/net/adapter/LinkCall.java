package com.ygs.android.yigongshe.net.adapter;

import com.ygs.android.yigongshe.net.callback.LinkCallback;
import java.io.IOException;
import okhttp3.Request;
import retrofit2.Call;

/**
 * Created by Joker on 2016/2/19.
 */
public interface LinkCall<T> extends Cloneable {

  /** synchronize execute */
  retrofit2.Response<T> execute() throws IOException;

  /** asynchronous by callback */
  void enqueue(LinkCallback<T> callback);

  /** cancel the request and be care of IOException */
  void cancel();

  boolean isExecuted();

  boolean isCanceled();

  Call<T> clone();

  /** The original HTTP request. */
  Request request();
}
