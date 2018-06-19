package com.ygs.android.yigongshe.ui.login;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity  implements View.OnClickListener{


    @BindView(R.id.register_phone_et)
    EditText mPhoneEditText;

//    @BindView(R.id.register_user_type_et)
//    EditText mUserTypeEditText;

    @BindView(R.id.register_usertype_sp)
    Spinner mUserTypeSpinner;

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

    @BindView(R.id.titlebar_backward_btn)
    Button mNavBackButton;

    @BindView(R.id.titlebar_right_btn)
    Button mNavRightButton;

    private ArrayAdapter<String> spinnerAdapter;
    private String mUserType = null;

    public void initView(){

        mRegisterButton.setOnClickListener(this);
        mCalendarBtn.setOnClickListener(this);

        String [] mUserTypes = getResources().getStringArray(R.array.user_type_names);
        spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,mUserTypes);
        mUserTypeSpinner.setAdapter(spinnerAdapter);

        mUserType = mUserTypes[0];

//        mUserTypeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String type = adapterView.getItemAtPosition(i).toString();
//                mUserType = type;
//            }
//        });


        this.mNavRightButton.setText(R.string.login);
        this.mNavRightButton.setVisibility(View.VISIBLE);
        this.mNavRightButton.setOnClickListener(this);
    }

    public void initIntent(){

    }


    public int getLayoutResId(){
        return R.layout.activity_resiter;
    }

    public void onClick(View view){

        if (view == mNavRightButton){
            showLogin();
        }
    }

    private void showLogin(){

        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }


}
