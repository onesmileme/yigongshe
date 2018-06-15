package com.ygs.android.yigongshe.net.callback;

import android.support.annotation.CallSuper;
import android.util.Log;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import java.io.IOException;
import retrofit2.Response;

/**
 * Created by Joker on 2016/2/19.
 */
public class LinkCallbackAdapter<T> implements LinkCallback<T> {

  private static final String TAG = LinkCallbackAdapter.class.getSimpleName();

  @Override public void onResponse(T entity, Response<?> response, Throwable throwable) {
  }

  @Override public void success(T entity, LinkCall call) {
    handleCallCancel(entity, null, null, call);
  }

  @Override public void failure(Response<?> response, LinkCall call) {
    handleCallCancel(null, response, null, call);
  }

  @Override public void exception(Throwable t, LinkCall call) {
    handleCallCancel(null, null, t, call);
  }

  @Override @CallSuper public void noContent(Response<?> response, LinkCall call) {
    failure(response, call);
  }

  @Override @CallSuper public void unauthenticated(Response<?> response, LinkCall call) {
    failure(response, call);
  }

  @Override @CallSuper public void clientError(Response<?> response, LinkCall call) {
    failure(response, call);
  }

  @Override @CallSuper public void serverError(Response<?> response, LinkCall call) {
    failure(response, call);
  }

  @Override @CallSuper public void networkError(IOException e, LinkCall call) {
    exception(e, call);
  }

  @Override @CallSuper public void unexpectedError(Throwable t, LinkCall call) {
    exception(t, call);
  }

  //处理call被cancel的情况
  private void handleCallCancel(T entity, Response<?> response, Throwable throwable,
      LinkCall call) {
    if (throwable != null) {
      Log.e(TAG, "exception: ", throwable);
    }
    if (call != null && call.isCanceled()) {
      Log.e("cancel api ", "");
      return;
    } else {
      onResponse(entity, response, throwable);
    }
  }
}
