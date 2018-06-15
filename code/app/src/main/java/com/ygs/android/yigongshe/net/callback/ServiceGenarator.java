package com.ygs.android.yigongshe.net.callback;

import com.ygs.android.yigongshe.net.ApiService;
import com.ygs.android.yigongshe.net.adapter.LinkCallAdapterFactory;
import com.ygs.android.yigongshe.net.converter.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by ruichao on 2018/6/15.
 */

public class ServiceGenarator {
  private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
  private static Retrofit.Builder retrofitBuilder;

  private static Retrofit retrofit;

  static {
    httpClientBuilder.connectTimeout(15000, TimeUnit.MILLISECONDS);
    httpClientBuilder.readTimeout(15000, TimeUnit.MILLISECONDS);
    httpClientBuilder.writeTimeout(15000, TimeUnit.MILLISECONDS);

    ServiceGenarator.retrofitBuilder =
        new Retrofit.Builder().addCallAdapterFactory(LinkCallAdapterFactory.created())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build());
  }

  public static <S> S createService(Class<S> serviceClass) {
    if (retrofit == null || ApiService.BASE_URL.equals(retrofit.baseUrl().toString())) {
      retrofit = ServiceGenarator.retrofitBuilder.baseUrl(ApiService.BASE_URL).build();
    }
    return retrofit.create(serviceClass);
  }
}
