package com.weikan.app.personalcenter;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.CheckLegalUtils;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.KeyBoardUtils;
import com.weikan.app.util.URLDefine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import platform.http.HttpUtils;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Lee on 2016/12/6.
 */
public class MineForgetActivity extends BaseActivity {

    @Bind(R.id.et_regist_phone)
    EditText etRegistPhone;
    @Bind(R.id.et_regist_code)
    EditText etRegistCode;
    @Bind(R.id.et_regist_pwd)
    EditText etRegistPwd;
    @Bind(R.id.tv_timer)
    TextView tvTimer;

    private Subscription subscription;
    private int ELLIPSE_TIME = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_forget);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.tv_titlebar_title)).setText("忘记密码");
    }

    @OnClick(R.id.iv_titlebar_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_regist)
    public void regist() {
        String phone = etRegistPhone.getText().toString().trim();
        String code = etRegistCode.getText().toString().trim();
        String pwd = etRegistPwd.getText().toString().trim();
        if (CheckLegalUtils.checkPhone(phone) && CheckLegalUtils.checkCode(code) && CheckLegalUtils.checkPwd(pwd)) {
            PersonalAgent.postForgetPwd(phone, code, pwd, new SimpleJsonResponseHandler() {
                @Override
                public void success() {
                    Toast.makeText(MineForgetActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    back();
                }
            });
            KeyBoardUtils.hide(this);
        }
    }

    @OnClick(R.id.tv_deal_clickable)
    public void showDeal() {
        IntentUtils.to(this, MineDealActivity.class);
    }

    @OnClick(R.id.tv_timer)
    public void startTimer() {
        if (!sendPhoneVerifyRequest()) {
            return;
        }

        subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .limit(ELLIPSE_TIME + 1)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return ELLIPSE_TIME - aLong;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        tvTimer.setEnabled(false);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        tvTimer.setEnabled(true);
                        tvTimer.setText("再次发送");
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long l) {
                        tvTimer.setText("剩余" + l + "秒");
                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public boolean sendPhoneVerifyRequest() {
        String phoneString = etRegistPhone.getText().toString().trim();
        if (!CheckLegalUtils.checkPhone(phoneString)) {
            return false;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.SMS_SEND);

        Map<String, String> params = new HashMap();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put("mobile", phoneString);
        params.put("type", "2");
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
        return true;
    }
}
