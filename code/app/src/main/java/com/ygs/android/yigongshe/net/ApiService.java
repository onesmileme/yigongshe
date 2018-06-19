package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.DynamicListResponse;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ruichao on 2018/6/15.
 */

public interface ApiService {
  /**
   * 动态列表
   */
  @GET("path/app/api/news/getnews") LinkCall<BaseResultDataInfo<DynamicListResponse>> getDynamicLists(
      @Query("page") int page, @Query("perpage") int perpage);
}