package com.ygs.android.yigongshe.ui.login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import butterknife.BindView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.RoleInfoBean;
import com.ygs.android.yigongshe.bean.SchoolInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.SchoolInfoListResponse;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.push.PushManager;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.ZProgressHUD;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.OnClick;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    //@BindView(R.id.titlebar) CommonTitleBar titleBar;
    @BindView(R.id.login_login_btn) Button mLoginButton;

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

    @BindView(R.id.register_school_layout) LinearLayout mSchoolLayout;
    @BindView(R.id.register_academy_layout) LinearLayout mAcademyLayout;
    @BindView(R.id.register_enroll_year_layout) LinearLayout mEnroolYearLayout;

    private CountDownTimer countDownTimer;

    private ArrayAdapter<String> spinnerAdapter;
    private int mUserType = 0;

    private List<SchoolInfoBean> mSchoolRoleBeanList;
    private List<RoleInfoBean> mRoleBeanList;
    private LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> mSchoolCall;
    private LinkCall<BaseResultDataInfo<EmptyBean>> mRegisterCall;

    private Date schollDate;
    private String phoneNum;


    private static final int SCHOOL_CHOOSE_CODE = 2;
    private static final String SOCIETY_PEOPLE = "社会爱心人士";

    @Override
    public void initView() {
        mUserTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RoleInfoBean bean = RegisterActivity.this.mRoleBeanList.get(position);
                updateUserType(bean);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSchoolEditText.setCursorVisible(false);
        mSchoolEditText.setFocusable(false);
        mSchoolEditText.setFocusableInTouchMode(false);
        loadSchoolInfo();

        //mockData();
    }

    private void updateRoles() {

        if (mRoleBeanList == null || mRoleBeanList.size() == 0){
            return;
        }
        String[] userTypes = new String[mRoleBeanList.size()];
        for (int i = 0; i < mRoleBeanList.size(); i++) {
            userTypes[i] = mRoleBeanList.get(i).title;
        }
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.item_register_spinner, userTypes);
        mUserTypeSpinner.setAdapter(spinnerAdapter);
        mUserType = mRoleBeanList.get(0).id;
    }

    private void mockData() {
        mPhoneEditText.setText("13683393431");
        mSchoolEditText.setText("北京大学");
        mAcademyEditText.setText("新闻专业");
        mEnrollEditText.setText("2000");
        mCaptchaEditText.setText("1234");
        mPasswordEditText.setText("123456");
        mConfirmPasswordEditText.setText("123456");
    }

    private void updateUserType(RoleInfoBean bean){
        mUserType = bean.id;
        if (SOCIETY_PEOPLE.equals(bean.title)){
            mSchoolLayout.setVisibility(View.GONE);
            mAcademyLayout.setVisibility(View.GONE);
            mEnroolYearLayout.setVisibility(View.GONE);
        }else{
            mSchoolLayout.setVisibility(View.VISIBLE);
            mAcademyLayout.setVisibility(View.VISIBLE);
            mEnroolYearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initIntent(Bundle bundle) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_resiter;
    }

    @OnClick({R.id.register_register_btn, R.id.register_calendar_btn,
        R.id.send_captcha_btn,R.id.register_school_et,R.id.login_login_btn})
    public void click(View view) {

        if (view == mRegisterButton) {
            doRegister();
        } else if (view == mCalendarBtn) {
            chooseYear();
        } else if (view == mCaptchaButton) {
            sendCaptcha();
        }else if(view == mSchoolEditText){
            chooseSchool();
        }else if(view == mLoginButton) {
            showLoginAlert();
        }
    }

    private void showLoginAlert(){
        new AlertDialog.Builder(this)
            .setMessage("放弃正在注册的信息，前往登录页面？")
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLogin();
                }
            }).show();
    }

    private void showLogin() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private String phoneTip(String phone){
        if (TextUtils.isEmpty(phone)){
            return "请输入手机号";
        }

        if (phone.length() != 11 || !phone.startsWith("1")){
            return "请输入正确的手机号";
        }

        return null;
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

        String msg = phoneTip(phone);
        if (TextUtils.isEmpty(verifyCode)) {
            msg = "请输入验证码";
        } else if (TextUtils.isEmpty(password)) {
            msg = "请输入密码";
        } else if (TextUtils.isEmpty(confirmPassword)) {
            msg = "请输入确认密码";
        } else if (!password.equals(confirmPassword)) {
            msg = "两次密码不一致";
        } else if (password.length() < 6){
            msg = "密码至少6位";
        }

        if (SOCIETY_PEOPLE.equals(mUserType)){
            school = "";
            academy = "";
            year= "";
        }else{
            if ((TextUtils.isEmpty(school))) {
                msg = "请选择学校";
            } else if (TextUtils.isEmpty(academy)) {
                msg = "请选择专业";
            } else if (TextUtils.isEmpty(year)) {
                msg = "请选择入学年份";
            }
        }

        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        String registrationId = PushManager.getInstance().getToken(this);
        mRegisterCall = LinkCallHelper.getApiService()
            .postRegsiter(phone, mUserType, school, academy, year,inviteCode, verifyCode ,password,confirmPassword, registrationId);
        final ZProgressHUD hud = ZProgressHUD.getInstance(this);
        hud.setMessage("注册中...");
        hud.show();
        mRegisterCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                hud.dismiss();
                if (entity != null && entity.error == ApiStatus.OK) {
                    showLogin();
                    Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();

                } else {
                    String msg = "注册失败";
                    if (entity != null && entity.msg != null) {
                        msg =  entity.msg ;
                    }
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mEnrollEditText.setText("" + year);
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
                    //NumberPicker mMonthSpinner = (NumberPicker)findViewById(
                    //    getContext().getResources().getIdentifier("android:id/month", null, null));
                    NumberPicker mYearSpinner = (NumberPicker)findViewById(
                        getContext().getResources().getIdentifier("android:id/year", null, null));
                    mSpinners.removeAllViews();
                    //if (mMonthSpinner != null) {
                    //    mSpinners.addView(mMonthSpinner);
                    //}
                    if (mYearSpinner != null) {
                        mSpinners.addView(mYearSpinner);
                    }
                }
                View dayPickerView = findViewById(
                    getContext().getResources().getIdentifier("android:id/day", null, null));
                if (dayPickerView != null) {
                    dayPickerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                super.onDateChanged(view, year, month, day);
                setTitle("请选择入学年份");
                RegisterActivity.this.onDateSet(view, year, month, day);
            }
        };

        dlg.show();
    }

    private void loadSchoolInfo() {

        LinkCall<BaseResultDataInfo<SchoolInfoListResponse>> call = LinkCallHelper.getApiService().getSchoolInfoLists();
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<SchoolInfoListResponse>>() {
            @Override
            public void success(BaseResultDataInfo<SchoolInfoListResponse> entity, LinkCall call) {
                super.success(entity, call);
                if (entity != null && entity.error == ApiStatus.OK) {

                    mSchoolRoleBeanList = entity.data.schools;
                    mRoleBeanList = entity.data.roles;
                    updateRoles();
                } else {
                    String msg = "请求学校信息失败";
                    if (entity != null && !TextUtils.isEmpty(entity.msg)) {
                        msg = msg + "(" + entity.msg + ")";
                    }
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(Response<?> response, LinkCall call) {
                super.failure(response, call);

            }
        });

    }

    private void sendCaptcha() {

        phoneNum = mPhoneEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(RegisterActivity.this, "请先填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        LinkCall<BaseResultDataInfo<EmptyBean>> call = LinkCallHelper.getApiService().sendVerifycode(phoneNum);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatus.OK) {
                    startCountdown();
                    Toast.makeText(RegisterActivity.this, "验证码已经发送，请注意查收", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountdown() {

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    refreshCountdownTv(millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    refreshCountdownTv(0);
                }
            };
        }
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void refreshCountdownTv(long secondsLeft) {
        if (mCaptchaButton == null) {
            return;
        }
        if (secondsLeft > 0) {
            mCaptchaButton.setText(secondsLeft + "s后再次发送");
            int color = getResources().getColor(R.color.gray2);
            mCaptchaButton.setBackgroundColor(color);
            mCaptchaButton.setEnabled(false);
        } else {
            mCaptchaButton.setText("点击发送验证码");
            int color = getResources().getColor(R.color.green);
            mCaptchaButton.setBackgroundColor(color);
            mCaptchaButton.setEnabled(true);
        }
    }

    private void chooseSchool(){

        if (mSchoolRoleBeanList != null) {
            Bundle bundle = new Bundle();
            ArrayList<String> schools = new ArrayList<>();
            for (SchoolInfoBean schoolInfoBean : mSchoolRoleBeanList) {
                schools.addAll(schoolInfoBean.schools);
            }
            bundle.putStringArrayList("schools", schools);

            goToOthersForResult(SchoolSelectActivity.class, bundle, SCHOOL_CHOOSE_CODE);
        }else{
            new AlertDialog.Builder(this)
                .setMessage("学校信息获取失败，请稍后再试")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadSchoolInfo();
                    }
                }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SCHOOL_CHOOSE_CODE == requestCode){
            try {
                Bundle bundle = data.getBundleExtra(BaseActivity.PARAM_INTENT);
                String name = bundle.getString("name");
                if (!TextUtils.isEmpty(name)) {
                    mSchoolEditText.setText(name);
                }
            }catch (Exception e){

            }

        }
    }
}
