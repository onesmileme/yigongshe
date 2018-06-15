package com.ygs.android.yigongshe.net.adapter;

import android.os.Handler;
import android.os.Looper;
import com.homelink.android.newhouse.libcore.global.api.bean.base.BaseResultInfo;
import com.homelink.android.newhouse.libcore.global.net.ResponseFilterImpl;
import com.homelink.android.newhouse.libcore.global.net.callback.ResponseFilter;
import com.homelink.android.newhouse.libcore.global.net.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by Joker on 2016/2/19.
 */
public class LinkCallAdapterFactory extends CallAdapter.Factory {

  private MainThreadExecutor mainThreadExecutor;

  private LinkCallAdapterFactory() {
    mainThreadExecutor = new MainThreadExecutor();
  }

  public static LinkCallAdapterFactory created() {
    return new LinkCallAdapterFactory();
  }

  @Override
  public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

    if (Types.getRawType(returnType) != LinkCall.class) {
      return null;
    }

    if (!(returnType instanceof ParameterizedType)) {
            /* 返回结果应该指定一个泛型，最起码也需要一个ResponseBody作为泛型 */
      throw new IllegalStateException(
          "LinkCall must have generic type (e.g., LinkCall<ResponseBody>)");
    }

    final Type responseType = Types.getParameterUpperBound(0, (ParameterizedType) returnType);

    return new CallAdapter<LinkCall<?>>() {
      @Override public Type responseType() {
        return responseType;
      }

      @Override public <R> LinkCall<?> adapt(Call<R> call) {
        LinkCallAdapter adapter = new LinkCallAdapter<>(call, mainThreadExecutor);
        ResponseFilter<BaseResultInfo> filter = new ResponseFilterImpl();
        adapter.setFilter(filter);
        return adapter;
      }
    };
  }

  class MainThreadExecutor implements Executor {

    Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override public void execute(Runnable command) {
      mainHandler.post(command);
    }
  }
}
