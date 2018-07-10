package com.ygs.android.yigongshe.net.interceptor;

import android.text.TextUtils;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ruichao on 2018/7/10.
 */

public class HeaderInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    AccountManager accountManager = YGApplication.accountManager;

    if (!TextUtils.isEmpty(accountManager.getToken())) {
      builder.addHeader("token", accountManager.getToken());
    }
    return chain.proceed(builder.build());
  }
}
