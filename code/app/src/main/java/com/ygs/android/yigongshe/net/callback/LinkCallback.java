package com.ygs.android.yigongshe.net.callback;

import com.ygs.android.yigongshe.net.adapter.LinkCall;
import java.io.IOException;
import retrofit2.Response;

/**
 * Created by Joker on 2016/2/19.
 */
public interface LinkCallback<T> {

  void onResponse(T entity, Response<?> response, Throwable throwable);

  /** Called for [200, 300) responses. */
  void success(T entity, LinkCall call);

  /** Called for failure response */
  void failure(Response<?> response, LinkCall call);

  /** Called for when occur throwable */
  void exception(Throwable t, LinkCall call);

  /** Called for 204 and 205 */
  void noContent(Response<?> response, LinkCall call);

  /** Called for 401 responses. */
  void unauthenticated(Response<?> response, LinkCall call);

  /** Called for [400, 500) responses, except 401. */
  void clientError(Response<?> response, LinkCall call);

  /** Called for [500, 600) response. */
  void serverError(Response<?> response, LinkCall call);

  /** Called for network errors while making the call. */
  void networkError(IOException e, LinkCall call);

  /** Called for unexpected errors while making the call. */
  void unexpectedError(Throwable t, LinkCall call);
}
