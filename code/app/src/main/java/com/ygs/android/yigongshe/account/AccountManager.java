package com.ygs.android.yigongshe.account;

import android.content.Context;
import android.content.SharedPreferences;
import com.ygs.android.yigongshe.bean.UserInfoBean;

public class AccountManager {

  private String token;
  private int userid;
  private UserInfoBean userInfoBean;

  private static final String TOKEN_KEY = "token";
  private static final String USERID_KEY = "userid";
  //private static final String TOKEN_EXPIRE_KEY = "token_expire";
  private static final String USER_NAME_KEY = "user_name";
  private static final String AVATAR_KEY = "avatar";
  private static final String SCHOOL_KEY = "school";
  private static final String COLLEGE_KEY = "college";
  private static final String MAJOR_KEY = "major";
  private static final String ADMISSION_YEAR_KEY = "admission_year";
  private static final String QQ_KEY = "qq";
  private static final String MAIL_KEY = "mail";
  private static final String PHONE_KEY = "phone";
  private static final String DESC_KEY = "desc";

  private Context mContext;

  public AccountManager(Context context) {

    mContext = context;
    SharedPreferences sharedPreferences =
        context.getSharedPreferences("info", Context.MODE_PRIVATE);

    token = sharedPreferences.getString(TOKEN_KEY, null);
    if (token != null) {
      //            userInfoBean = sharedPreferences.getString(TOKEN_EXPIRE_KEY,null);

      userInfoBean = new UserInfoBean();
      userInfoBean.username = sharedPreferences.getString(USER_NAME_KEY, null);
      userInfoBean.avatar = sharedPreferences.getString(AVATAR_KEY, null);
      userInfoBean.school = sharedPreferences.getString(SCHOOL_KEY, null);
      userInfoBean.college = sharedPreferences.getString(COLLEGE_KEY, null);
      userInfoBean.major = sharedPreferences.getString(MAJOR_KEY, null);
      userInfoBean.admission_year = sharedPreferences.getString(ADMISSION_YEAR_KEY, null);
      userInfoBean.qq = sharedPreferences.getString(QQ_KEY, null);
      userInfoBean.mail = sharedPreferences.getString(MAIL_KEY, null);
      userInfoBean.phone = sharedPreferences.getString(PHONE_KEY, null);
      userInfoBean.desc = sharedPreferences.getString(DESC_KEY, null);
      this.userid = sharedPreferences.getInt(USERID_KEY, -1);
    }
  }

  public void updateUserInfo(UserInfoBean userInfoBean) {
    this.userInfoBean = userInfoBean;

    SharedPreferences.Editor editor =
        mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();

    editor.putString(USER_NAME_KEY, userInfoBean.username);
    editor.putString(AVATAR_KEY, userInfoBean.avatar);
    editor.putString(SCHOOL_KEY, userInfoBean.school);
    editor.putString(COLLEGE_KEY, userInfoBean.college);
    editor.putString(MAJOR_KEY, userInfoBean.major);
    editor.putString(ADMISSION_YEAR_KEY, userInfoBean.admission_year);
    editor.putString(QQ_KEY, userInfoBean.qq);
    editor.putString(PHONE_KEY, userInfoBean.phone);
    editor.putString(DESC_KEY, userInfoBean.desc);

    editor.apply();
  }

  public void updateToken(String token, String tokenExpire) {
    this.token = token;

    SharedPreferences.Editor editor =
        mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
    editor.putString(TOKEN_KEY, token);
    editor.apply();
  }

  public void updateUserId(int userid) {
    this.userid = userid;
    SharedPreferences.Editor editor =
        mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
    editor.putInt(USERID_KEY, userid);
    editor.apply();
  }

  public void updateUserName(String username){
    userInfoBean.username = username;
    updateUserInfo(userInfoBean);
  }

//  public void updatePassword(String password){
//
//  }

  public void updatePhone(String phone){
    userInfoBean.phone = phone;
    updateUserInfo(userInfoBean);
  }

  public String getToken() {
    return token;
  }

  public UserInfoBean getUserInfoBean() {
    return userInfoBean;
  }

  public int getUserid() {
    return userid;
  }
}
