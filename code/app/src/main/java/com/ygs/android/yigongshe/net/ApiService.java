package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommentListResponse;
import com.ygs.android.yigongshe.bean.response.DynamicListResponse;
import com.ygs.android.yigongshe.bean.response.SchoolInfoListResponse;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ruichao on 2018/6/15.
 */

public interface ApiService {
  /**
   * 动态列表s
   */
  @GET("app/api/news/getnews") LinkCall<BaseResultDataInfo<DynamicListResponse>> getDynamicLists(
      @Query("page") int page, @Query("perpage") int perpage);

  @GET("app/api/register")
  LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> getSchoolInfoLists();

  @POST("app/api/register") LinkCall<BaseResultDataInfo<EmptyBean>> postRegsiter(
      @Field("phone") String name, @Field("role") String role, @Field("school") String school,
      @Field("college") String college, @Field("admission_year") String adYear,
      @Field("invite_code") String inviteCode, @Field("verify_code") String verifyCode);

  @GET("app/api/news/getcomments")
  LinkCall<BaseResultDataInfo<CommentListResponse>> getDynamicCommentLists(
      @Query("news_id") int news_id, @Query("page") int page, @Query("perpage") int perpage);
}