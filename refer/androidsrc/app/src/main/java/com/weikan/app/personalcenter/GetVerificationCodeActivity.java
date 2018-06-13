package com.weikan.app.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaorenhui on 2015/12/17.
 */
public class GetVerificationCodeActivity extends BaseActivity implements View.OnClickListener {
    private EditText etPhone;
    private EditText etVerificationCode;
    private TextView tvGet;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_verification_code);
        initTitleBar();
        initView();
    }

    private void initView() {
        etPhone = (EditText) findViewById(R.id.forgot_password_cellphone_edit_text);
        etPhone.setOnClickListener(this);
        etVerificationCode = (EditText) findViewById(R.id.forgot_password_captcha_edit_text);
        etVerificationCode.setOnClickListener(this);
        tvGet = (TextView) findViewById(R.id.forgot_password_request_captcha_button);
        tvGet.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.forgot_password_cellphone_next_step_button);
        btnNext.setOnClickListener(this);
    }

    private void initTitleBar() {
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("修改密码");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password_cellphone_edit_text:
                break;
            case R.id.forgot_password_captcha_edit_text:
                break;
            case R.id.forgot_password_request_captcha_button:
                getVerificationCode();
                break;
            case R.id.forgot_password_cellphone_next_step_button:
                gotoNextStep();
                break;
        }
    }

    private void gotoNextStep() {
        Intent i = new Intent();
        i.setClass(this,ResetPwdActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(i,101);
    }

    private void getVerificationCode() {
        String phoneNum = etPhone.getText().toString();
        boolean flag = isMobileNO(phoneNum);
        if (!flag) {
            Toast.makeText(GetVerificationCodeActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        sendVerificationCodeRequest();
    }

    private void sendVerificationCodeRequest() {

    }


    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


}
