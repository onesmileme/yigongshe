package com.ygs.android.yigongshe.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.SchoolInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.SchoolInfoListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import java.util.List;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.register_phone_et) EditText mPhoneEditText;

  //    @BindView(R.id.register_user_type_et)
  //    EditText mUserTypeEditText;

  @BindView(R.id.register_usertype_sp) Spinner mUserTypeSpinner;

  @BindView(R.id.register_school_et) EditText mSchoolEditText;

  @BindView(R.id.register_academy_et) EditText mAcademyEditText;

  @BindView(R.id.register_enroll_year_et) EditText mEnrollEditText;

  @BindView(R.id.register_calendar_btn) Button mCalendarBtn;

  @BindView(R.id.register_captcha_et) EditText mCaptchaEditText;

  @BindView(R.id.send_captcha_btn) Button mCaptchaButton;

  @BindView(R.id.register_invite_et) EditText mInviteEditText;

  @BindView(R.id.register_register_btn) Button mRegisterButton;

  @BindView(R.id.titlebar_backward_btn) Button mNavBackButton;

  @BindView(R.id.titlebar_right_btn) Button mNavRightButton;

  private ArrayAdapter<String> spinnerAdapter;
  private String mUserType = null;

  private List<SchoolInfoBean> schoolRoleBeanList;
  private LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> mSchoolCall;

  private LinkCall<BaseResultDataInfo<EmptyBean>> mRegisterCall;

  public void initView() {

    mRegisterButton.setOnClickListener(this);
    mCalendarBtn.setOnClickListener(this);

    String[] mUserTypes = getResources().getStringArray(R.array.user_type_names);
    spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mUserTypes);
    mUserTypeSpinner.setAdapter(spinnerAdapter);

    //        mUserTypeSpinner.get
    mUserType = mUserTypes[0];

    mUserTypeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String type = adapterView.getItemAtPosition(i).toString();
        mUserType = type;
      }
    });

    this.mNavRightButton.setText(R.string.login);
    this.mNavRightButton.setVisibility(View.VISIBLE);
    this.mNavRightButton.setOnClickListener(this);
  }

  public void initIntent(Bundle bundle) {

  }

  public int getLayoutResId() {
    return R.layout.activity_resiter;
  }

  public void onClick(View view) {

    if (view == mNavRightButton) {
      showLogin();
    } else if (view == mRegisterButton) {
      doRegister();
    }
  }

  private void showLogin() {

    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity(intent);
  }

  private void doRegister() {

    String phone = mPhoneEditText.getText().toString();
    String school = mSchoolEditText.getText().toString();
    String academy = mAcademyEditText.getText().toString();
    String year = mEnrollEditText.getText().toString();
    String verifyCode = mCaptchaEditText.getText().toString();
    String inviteCode = mInviteEditText.getText().toString();

    mRegisterCall = LinkCallHelper.getApiService()
        .postRegsiter(phone, mUserType, school, academy, year, verifyCode, inviteCode);
    mRegisterCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
      @Override public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
      }
    });
  }

  private void getSchoolRoleList() {

    mSchoolCall = LinkCallHelper.getApiService().getSchoolInfoLists();
    mSchoolCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<SchoolInfoListResponse>>() {

      @Override public void onResponse(BaseResultDataInfo<SchoolInfoListResponse> entity,
          Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == ApiStatusInterface.OK) {
          schoolRoleBeanList = entity.data.schools;
        }
      }
    });
  }
}
