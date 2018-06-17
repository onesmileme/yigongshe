package com.ygs.android.yigongshe.ui.login;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity  implements View.OnClickListener{


    @BindView(R.id.register_phone_et)
    EditText mPhoneEditText;

    @BindView(R.id.register_user_type_et)
    EditText mUserTypeEditText;

    @BindView(R.id.register_school_et)
    EditText mSchoolEditText;

    @BindView(R.id.register_academy_et)
    EditText mAcademyEditText;

    @BindView(R.id.register_enroll_year_et)
    EditText mEnrollEditText;

    @BindView(R.id.register_calendar_btn)
    Button mCalendarBtn;

    @BindView(R.id.register_captcha_et)
    EditText mCaptchaEditText;

    @BindView(R.id.send_captcha_btn)
    Button mCaptchaButton;

    @BindView(R.id.register_invite_et)
    EditText mInviteEditText;


    @BindView(R.id.register_register_btn)
    Button mRegisterButton;

    public void initView(){

        mRegisterButton.setOnClickListener(this);
        mCalendarBtn.setOnClickListener(this);

    }

    public void initIntent(){

    }


    public int getLayoutResId(){
        return R.layout.activity_resiter;
    }

    public void onClick(View view){

    }


}
