package com.ygs.android.yigongshe.net.callback;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.widget.Toast;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.base.BaseResultInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.ui.login.LoginActivity;
import java.io.IOException;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Joker on 2016/2/19.
 */
public class LinkCallbackAdapter<T> implements LinkCallback<T> {

  private static final String TAG = LinkCallbackAdapter.class.getSimpleName();

  @Override public void onResponse(T entity, Response<?> response, Throwable throwable) {
    if (entity != null
        && entity instanceof BaseResultInfo
        && ((BaseResultInfo) entity).error == ApiStatus.TOKEN_ERROR) {
      {
        AccountManager accountManager = YGApplication.accountManager;
        accountManager.updateToken(null, null);
        Toast.makeText(YGApplication.mApplication, "token失效，重新登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(YGApplication.mApplication, LoginActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        YGApplication.mApplication.startActivity(intent);
      }
    }
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
