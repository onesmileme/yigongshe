package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.LoginBean;
import com.ygs.android.yigongshe.bean.OtherUserInfoBean;
import com.ygs.android.yigongshe.bean.SchoolListBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.bean.response.CommentListResponse;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
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
   * 动态列表
   */
  @GET("app/api/news/getnews") LinkCall<BaseResultDataInfo<DynamicListResponse>> getDynamicLists(
      @Query("page") int page, @Query("perpage") int perpage);

  @GET("app/api/register")
  LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> getSchoolInfoLists();

  /**
   * 动态评论列表
   */
  @GET("app/api/news/getcomments")
  LinkCall<BaseResultDataInfo<CommentListResponse>> getDynamicCommentLists(
      @Query("news_id") int news_id, @Query("page") int page, @Query("perpage") int perpage);

  @POST("app/api/register") LinkCall<BaseResultDataInfo<EmptyBean>> postRegsiter(
      @Field("phone") String name, @Field("role") String role, @Field("school") String school,
      @Field("college") String college, @Field("admission_year") String adYear,
      @Field("invite_code") String inviteCode, @Field("verify_code") String verifyCode);

  @GET("app/api/login") LinkCall<BaseResultDataInfo<LoginBean>> doLogin(
      @Query("phone") String phone, @Query("password") String password);

  @POST("/app/api/login/out") LinkCall<BaseResultDataInfo<EmptyBean>> doLogout(
      @Field("token") String token);

  @GET("/app/api/user/base") LinkCall<BaseResultDataInfo<UserInfoBean>> getUserInfo(
      @Query("token") String token);

  @GET("/app/api/user/otherInfo") LinkCall<BaseResultDataInfo<OtherUserInfoBean>> getOtherInfo(
      @Query("token") String token, @Query("userid") String userid);

  @POST("/app/api/user/modifyschool") LinkCall<BaseResultDataInfo<UserInfoBean>> modifySchool(
      @Field("token") String token, @Field("new_school") String school);

  @POST("/app/api/user/forgetpassword") LinkCall<BaseResultDataInfo<EmptyBean>> forgetPassword(
      @Field("phone") String phone, @Field("verif_code") String verifyCode,
      @Field("password") String newPassword);

  @POST("/app/api/user/modifyavatar") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyAvatar(
      @Field("token") String token, @Field("new_avatar") String newAvatar);

  @POST("/app/api/user/modifyphone") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyPhone(
      @Field("token") String token, @Field("new_phone") String newPhone,
      @Field("verif_code") String verifyCode);

  @POST("/app/api/user/modifyusername") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyUsername(
      @Field("token") String token, @Field("new_username") String newName);

  @POST("/app/api/user/modifypassword") LinkCall<BaseResultDataInfo<EmptyBean>> modifyPassword(
      @Field("token") String token, @Field("new_password") String newPassword,
      @Field("confirm_password") String confirmPassword);

  @GET("/app/api/user/beforemodifyschool")
  LinkCall<BaseResultDataInfo<SchoolListBean>> getSchoolLisst();

  /**
   * 活动评论列表
   */
  @GET("app/api/activity/getcomments")
  LinkCall<BaseResultDataInfo<CommentListResponse>> getActivityCommentLists(
      @Query("activity_id") int news_id, @Query("page") int page, @Query("perpage") int perpage);

  /**
   * 圈子评论列表
   */
  @GET("app/api/pubcircle/getcomments")
  LinkCall<BaseResultDataInfo<CommentListResponse>> getCommunityCommentLists(
      @Query("pubcircle_id") int news_id, @Query("page") int page, @Query("perpage") int perpage);

  /**
   * 活动列表
   */
  @GET("app/api/activity/getactivities")
  LinkCall<BaseResultDataInfo<ActivityListResponse>> getActivityLists(@Query("page") int page,
      @Query("perpage") int perpage, @Query("cate") String cate,
      @Query("progress") String progress);

  /**
   * 活动详情
   */
  @POST("app/api/activity/detail")
  LinkCall<BaseResultDataInfo<ActivityDetailResponse>> getActivityDetail(
      @Field("activityid") int activityid);

  /**
   * 圈子列表
   * type	类型，全部：为空或all; 城市：city; 社团：association, 关注的人：follow	是
   */
  @GET("app/api/pubcircle/getlist")
  LinkCall<BaseResultDataInfo<CommunityListResponse>> getCommunityList(@Query("page") int page,
      @Query("perpage") int perpage, @Query("type") String type);
}