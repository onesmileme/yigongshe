package com.ygs.android.yigongshe.ui.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindInt;
import butterknife.BindView;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
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
import com.ygs.android.yigongshe.view.CommonTitleBar;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener , DatePickerDialog.OnDateSetListener {

  @BindView(R.id.titlebar) CommonTitleBar titleBar;

  @BindView(R.id.register_phone_et) EditText mPhoneEditText;

  @BindView(R.id.register_usertype_sp) Spinner mUserTypeSpinner;

  @BindView(R.id.register_school_et) EditText mSchoolEditText;

  @BindView(R.id.register_academy_et) EditText mAcademyEditText;

  @BindView(R.id.register_enroll_year_et) EditText mEnrollEditText;

  @BindView(R.id.register_calendar_btn) Button mCalendarBtn;

  @BindView(R.id.register_captcha_et) EditText mCaptchaEditText;

  @BindView(R.id.register_password_et) EditText mPasswordEditText;

  @BindView(R.id.register_reinput_password_et) EditText mConfirmPasswordEditText;

  @BindView(R.id.send_captcha_btn) Button mCaptchaButton;

  @BindView(R.id.register_invite_et) EditText mInviteEditText;

  @BindView(R.id.register_register_btn) Button mRegisterButton;


  private ArrayAdapter<String> spinnerAdapter;
  private String mUserType = null;

  private List<SchoolInfoBean> schoolRoleBeanList;
  private LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> mSchoolCall;

  private LinkCall<BaseResultDataInfo<EmptyBean>> mRegisterCall;

  private Date schollDate;

  @Override
  public void initView() {

    titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
          finish();
        }else if(action == CommonTitleBar.ACTION_RIGHT_TEXT || action == CommonTitleBar.ACTION_RIGHT_BUTTON){
          showLogin();
        }
      }
    });

    mCalendarBtn.setOnClickListener(this);

    final String[] mUserTypes = getResources().getStringArray(R.array.user_type_names);
    spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mUserTypes);
    mUserTypeSpinner.setAdapter(spinnerAdapter);

    mUserType = mUserTypes[0];

    mUserTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String type = mUserTypes[position];
        mUserType = type;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mEnrollEditText.setEnabled(false);

    mRegisterButton.setOnClickListener(this);


    mockData();


  }

  private void mockData(){

    mPhoneEditText.setText("13683393431");
    mSchoolEditText.setText("北京大学");
    mAcademyEditText.setText("新闻专业");
    mEnrollEditText.setText("2000");
    mCaptchaEditText.setText("1234");
    mPasswordEditText.setText("123456");
    mConfirmPasswordEditText.setText("123456");

  }

  @Override
  public void initIntent(Bundle bundle) {

  }

  @Override
  public int getLayoutResId() {
    return R.layout.activity_resiter;
  }

  @Override
  public void onClick(View view) {

   if (view == mRegisterButton) {
      doRegister();
    }else if (view == mCalendarBtn){
     chooseYear();
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
    String password = mPasswordEditText.getText().toString();
    String confirmPassword = mConfirmPasswordEditText.getText().toString();

    String msg = null;
    if (TextUtils.isEmpty(phone)){
      msg = "请输入手机号";
    }else if ((TextUtils.isEmpty(school))){
      msg = "请选择学校";
    }else if(TextUtils.isEmpty(academy)){
      msg = "请选择专业";
    }else if(TextUtils.isEmpty(year)){
      msg = "请选择入学年份";
    }else if(TextUtils.isEmpty(verifyCode)){
      msg = "请输入验证码";
    }else if(TextUtils.isEmpty(password)){
      msg = "请输入密码";
    }else if(TextUtils.isEmpty(confirmPassword)){
      msg = "请输入确认密码";
    }else if(!password.equals(confirmPassword)){
      msg = "两次密码不一致";
    }

    if (msg != null){
      Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
      return;
    }

    mRegisterCall = LinkCallHelper.getApiService()
        .postRegsiter(phone, mUserType, school, academy, year, verifyCode, inviteCode);
    mRegisterCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
      @Override public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == ApiStatusInterface.OK){

          showLogin();
          Toast.makeText(RegisterActivity.this,"注册成功，请登录",Toast.LENGTH_SHORT).show();

        }else{
          String msg = "注册失败";
          if (entity != null && entity.msg != null){
            msg += "("+entity.msg+")";
          }
          Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_SHORT).show();
        }
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

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
    mEnrollEditText.setText(""+year);
  }


  private void chooseYear() {

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog dlg = new DatePickerDialog(new ContextThemeWrapper(this,
        android.R.style.Theme_Holo_Light_Dialog_NoActionBar), null, yy, mm, dd) {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mSpinners = (LinearLayout)findViewById(
            getContext().getResources().getIdentifier("android:id/pickers", null, null));
        if (mSpinners != null) {
          NumberPicker mMonthSpinner = (NumberPicker)findViewById(
              getContext().getResources().getIdentifier("android:id/month", null, null));
          NumberPicker mYearSpinner = (NumberPicker)findViewById(
              getContext().getResources().getIdentifier("android:id/year", null, null));
          mSpinners.removeAllViews();
          if (mMonthSpinner != null) {
            mSpinners.addView(mMonthSpinner);
          }
          if (mYearSpinner != null) {
            mSpinners.addView(mYearSpinner);
          }
        }
        View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
        if (dayPickerView != null) {
          dayPickerView.setVisibility(View.GONE);
        }
      }

      @Override
      public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle("请选择入学年份");
        RegisterActivity.this.onDateSet(view,year,month,day);
      }
    };

    dlg.show();
  }

}
