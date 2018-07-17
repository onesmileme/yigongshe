package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.FollowPersonDataBean;
import com.ygs.android.yigongshe.bean.LoginBean;
import com.ygs.android.yigongshe.bean.OtherUserInfoBean;
import com.ygs.android.yigongshe.bean.SchoolListBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.base.BaseResultInfo;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.bean.response.AttentionResponse;
import com.ygs.android.yigongshe.bean.response.CircleDeleteResponse;
import com.ygs.android.yigongshe.bean.response.CityListResponse;
import com.ygs.android.yigongshe.bean.response.CommentDeleteResponse;
import com.ygs.android.yigongshe.bean.response.CommentListResponse;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.bean.response.DaCallResponse;
import com.ygs.android.yigongshe.bean.response.DynamicDetailResponse;
import com.ygs.android.yigongshe.bean.response.DynamicListResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoResponse;
import com.ygs.android.yigongshe.bean.response.ListLikeResponse;
import com.ygs.android.yigongshe.bean.response.PublishCommunityResponse;
import com.ygs.android.yigongshe.bean.response.SchoolInfoListResponse;
import com.ygs.android.yigongshe.bean.response.ScrollPicResponse;
import com.ygs.android.yigongshe.bean.response.ShoucangResponse;
import com.ygs.android.yigongshe.bean.response.SigninResponse;
import com.ygs.android.yigongshe.bean.response.SignupResponse;
import com.ygs.android.yigongshe.bean.response.TopicListResponse;
import com.ygs.android.yigongshe.bean.response.UnAttentionResponse;
import com.ygs.android.yigongshe.bean.response.UnShoucangResponse;
import com.ygs.android.yigongshe.bean.response.UploadImageBean;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
   * 动态详情
   */
  @GET("app/api/news/getnewsdetail")
  LinkCall<BaseResultDataInfo<DynamicDetailResponse>> getDynamicDetail(@Query("id") int id);

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

  @POST("app/api/login/out") LinkCall<BaseResultDataInfo<EmptyBean>> doLogout(
      @Field("token") String token);

  @GET("app/api/user/base") LinkCall<BaseResultDataInfo<UserInfoBean>> getUserInfo(
      @Query("token") String token);

  @GET("app/api/user/otherInfo") LinkCall<BaseResultDataInfo<OtherUserInfoBean>> getOtherInfo(
      @Query("token") String token, @Query("userid") String userid);

  @POST("app/api/user/modifyschool") LinkCall<BaseResultDataInfo<UserInfoBean>> modifySchool(
      @Field("token") String token, @Field("new_school") String school);

  @POST("app/api/user/forgetpassword") LinkCall<BaseResultDataInfo<EmptyBean>> forgetPassword(
      @Field("phone") String phone, @Field("verif_code") String verifyCode,
      @Field("password") String newPassword);

  @POST("app/api/user/modifyavatar") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyAvatar(
      @Field("token") String token, @Field("new_avatar") String newAvatar);

  @POST("app/api/user/modifyphone") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyPhone(
      @Field("token") String token, @Field("new_phone") String newPhone,
      @Field("verif_code") String verifyCode);

  @POST("app/api/user/modifyusername") LinkCall<BaseResultDataInfo<UserInfoBean>> modifyUsername(
      @Field("token") String token, @Field("new_username") String newName);

  @POST("app/api/user/modifypassword") LinkCall<BaseResultDataInfo<EmptyBean>> modifyPassword(
      @Field("token") String token, @Field("new_password") String newPassword,
      @Field("confirm_password") String confirmPassword);

  @GET("app/api/user/beforemodifyschool")
  LinkCall<BaseResultDataInfo<SchoolListBean>> getSchoolLisst();

  /**
   * 我关注的人
   */
  @POST("app/api/followperson/getlist")
  LinkCall<BaseResultDataInfo<FollowPersonDataBean>> getFolloPersonList(
      @Field("token") String token, @Field("page") int pageindex);//page index start from 1

  /**
   * 关注某人
   */
  @POST("app/api/followperson/follow") LinkCall<BaseResultDataInfo<EmptyBean>> doFollow(
      @Field("token") String token, @Field("userid") String userid);

  /**
   * 取消关注某人
   */
  @POST("/app/api/followperson/unfollow") LinkCall<BaseResultDataInfo<EmptyBean>> unFollow(
      @Field("token") String token, @Field("userid") String userid);

  /**
   * 获取我报名参加的活动
   */
  @POST("app/api/activity/getmyregisteractivity")
  LinkCall<BaseResultDataInfo<ActivityListResponse>> getMyRegisterActivity(
      @Field("token") String token);

  /**
   * 获取我收藏的活动
   */
  @POST("app/api/activity/getmystoreactivity")
  LinkCall<BaseResultDataInfo<ActivityListResponse>> getMyStoreActivity(
      @Field("token") String token);

  /**
   * 获取我签到的活动
   */
  @POST("app/api/activity/getmysigninactivity")
  LinkCall<BaseResultDataInfo<ActivityListResponse>> getMySigninActivity(
      @Field("token") String token);

  /**
   * 获取我的益公圈列表
   */
  @GET("app/api/pubcircle/getmylist")
  LinkCall<BaseResultDataInfo<CommunityListResponse>> getMyCommunityList(
      @Query("token") String token);

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
  @FormUrlEncoded @POST("app/api/activity/detail")
  LinkCall<BaseResultDataInfo<ActivityDetailResponse>> getActivityDetail(
      @Field("activityid") int activityid);

  /**
   * 圈子列表
   * type	类型，全部：为空或all; 城市：city; 社团：association, 关注的人：follow	是
   */
  @GET("app/api/pubcircle/getlist")
  LinkCall<BaseResultDataInfo<CommunityListResponse>> getCommunityList(@Query("page") int page,
      @Query("perpage") int perpage, @Query("type") String type);

  /**
   * 视频列表
   */
  @FormUrlEncoded @POST("app/api/video/getvideolist")
  LinkCall<BaseResultDataInfo<HelpVideoListResponse>> getHelpVideoList(@Field("page") int page,
      @Field("perpage") int perpage, @Field("activityid") int activityid);

  /**
   * 城市选择列表
   */
  @POST("app/api/common/getcitylist") LinkCall<BaseResultDataInfo<CityListResponse>> getCityList();

  /**
   * 添加新闻评论
   */
  @FormUrlEncoded @POST("app/api/news/addcomment") LinkCall<BaseResultInfo> postNewsComment(
      @Field("news_id") int news_id, @Field("content") String content,
      @Field("token") String token);

  /**
   * 添加活动评论
   */
  @FormUrlEncoded @POST("app/api/activity/addcomment") LinkCall<BaseResultInfo> postActivityComment(
      @Field("activity_id") int activity_id, @Field("content") String content,
      @Field("token") String token);

  /**
   * 添加益工圈评论
   */
  @FormUrlEncoded @POST("app/api/pubcircle/addcomment")
  LinkCall<BaseResultInfo> postCommunityComment(@Field("pubcircle_id") int pubcircle_id,
      @Field("content") String content, @Field("token") String token);

  /**
   * 轮播图:活动、动态等
   */
  @FormUrlEncoded @POST("app/api/slide/getlist")
  LinkCall<BaseResultDataInfo<ScrollPicResponse>> getScrollPicList(@Field("type") String type);

  /**
   * 文件上传
   */
  @Multipart @POST("app/api/upload")
  LinkCall<BaseResultDataInfo<UploadImageBean>> uploadRemarkImage(
      @Part("description") RequestBody description, @Part MultipartBody.Part file,
      @Query("md5") String md5);

  /**
   * 报名活动
   */
  @FormUrlEncoded @POST("app/api/activity/registe")
  LinkCall<BaseResultDataInfo<SignupResponse>> signupActivity(@Field("activity_id") int activity_id,
      @Field("token") String token);

  /**
   * 签到
   */
  @FormUrlEncoded @POST("app/api/activity/signin")
  LinkCall<BaseResultDataInfo<SigninResponse>> signinActivity(@Field("activity_id") int activity_id,
      @Field("token") String token);

  /**
   * 发布公益圈
   */
  @FormUrlEncoded @POST("app/api/pubcircle/publish")
  LinkCall<BaseResultDataInfo<PublishCommunityResponse>> publishCommunity(
      @Field("token") String token, @Field("topic") String topic, @Field("content") String content,
      @Field("pic") String pic);

  /**
   * 收藏活动
   */
  @FormUrlEncoded @POST("app/api/activity/strore")
  LinkCall<BaseResultDataInfo<ShoucangResponse>> restoreActivity(
      @Field("activity_id") int activity_id, @Field("token") String token);

  /**
   * 取消收藏活动
   */
  @FormUrlEncoded @POST("app/api/activity/unstoractivity")
  LinkCall<BaseResultDataInfo<UnShoucangResponse>> unrestoreActivity(
      @Field("activity_id") int activity_id, @Field("token") String token);

  /**
   * 应援视频上传
   */
  @FormUrlEncoded @POST("app/api/video/add")
  LinkCall<BaseResultDataInfo<HelpVideoResponse>> uploadHelpVideo(
      @Field("video_path") String video_path, @Query("activityid") int activityid,
      @Query("token") String token);

  /**
   * 关注用户
   */
  @FormUrlEncoded @POST("app/api/followperson/follow")
  LinkCall<BaseResultDataInfo<AttentionResponse>> attentionUser(@Field("userid") int userid,
      @Field("token") String token);

  /**
   * 取消关注用户
   */
  @FormUrlEncoded @POST("app/api/followperson/unfollow")
  LinkCall<BaseResultDataInfo<UnAttentionResponse>> unAttentionUser(@Field("userid") int userid,
      @Field("token") String token);

  /**
   * 益工圈列表页点赞
   */
  @FormUrlEncoded @POST("app/api/pubcircle/like")
  LinkCall<BaseResultDataInfo<ListLikeResponse>> likeCircle(@Field("id") int id,
      @Field("token") String token);

  /**
   * 益工圈删除一个自己发的圈子
   */
  @FormUrlEncoded @POST("app/api/pubcircle/del")
  LinkCall<BaseResultDataInfo<CircleDeleteResponse>> deleteCircle(@Field("id") int id,
      @Field("token") String token);

  /**
   * 删除评论
   */
  @FormUrlEncoded @POST("app/api/pubcircle/del")
  LinkCall<BaseResultDataInfo<CommentDeleteResponse>> deleteMyComment(@Field("id") int id,
      @Field("token") String token, @Field("comment_id") int comment_id);

  /**
   * 为活动打call
   */
  @FormUrlEncoded @POST("app/api/activity/makecall")
  LinkCall<BaseResultDataInfo<DaCallResponse>> daCallActivity(@Field("activity_id") int activity_id,
      @Field("token") String token);

  /**
   * 话题列表
   */
  @POST("app/api/pubcircle/gettopics")
  LinkCall<BaseResultDataInfo<TopicListResponse>> getTopicList();
}

