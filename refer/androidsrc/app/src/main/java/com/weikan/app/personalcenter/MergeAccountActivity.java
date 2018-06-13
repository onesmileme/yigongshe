package com.weikan.app.personalcenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.personalcenter.bean.AccountBindObject;
import com.weikan.app.personalcenter.bean.BindOpenidObject;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.ShareTools;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujian on 16/4/16.
 */
public class MergeAccountActivity extends BaseActivity {

    View weixinLayout;
    View qqLayout;
    TextView weixinText;
    TextView qqText;

    boolean isWeixinBind = false;
    boolean isQQBind = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_account);

        initUI();
        updateUI();

        sendGetStatusRequest();
    }

    private void initUI() {
        weixinLayout = findViewById(R.id.ll_weixin_merge);
        qqLayout = findViewById(R.id.ll_qq_merge);
        weixinText = (TextView) findViewById(R.id.tv_weixin_merge);
        qqText = (TextView) findViewById(R.id.tv_qq_merge);


        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("登陆账号合并");

        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RxView.clicks(weixinLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ShareTools.getInstance().LoginWeixin(MergeAccountActivity.this, new ShareTools.OnThirdLoginListener() {
                            @Override
                            public void onLoginSuccess() {
                                String openid = ShareTools.getInstance().getLoginParam(MergeAccountActivity.this,
                                        "weixin_openid");
                                sendMergeRequest(openid, new SimpleJsonResponseHandler() {
                                    @Override
                                    public void success() {
                                        isWeixinBind = true;
                                        updateUI();

                                    }

                                    @Override
                                    public void end() {
                                        super.end();
                                        hideLoadingDialog();
                                    }
                                });
                            }

                            @Override
                            public void onLoginFailed() {

                            }
                        });
                    }
                });

        RxView.clicks(qqLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ShareTools.getInstance().LoginQQ(MergeAccountActivity.this, new ShareTools.OnThirdLoginListener() {
                            @Override
                            public void onLoginSuccess() {
                                String openid = ShareTools.getInstance().getLoginParam(MergeAccountActivity.this,
                                        "qq_openid");
                                sendMergeRequest(openid, new SimpleJsonResponseHandler() {
                                    @Override
                                    public void success() {
                                        isQQBind = true;
                                        updateUI();

                                    }

                                    @Override
                                    public void end() {
                                        super.end();
                                        hideLoadingDialog();
                                    }
                                });
                            }

                            @Override
                            public void onLoginFailed() {

                            }
                        });
                    }
                });

    }

    private void updateUI() {
        String loginType = ShareTools.getInstance().getLoginParam(this, "login_type");
        if (loginType.equals("weixin")) {
            weixinText.setText("已登录");
            weixinText.setTextColor(0xff999999);
            weixinLayout.setEnabled(false);
        } else if (isWeixinBind) {
            weixinText.setText("已绑定");
            weixinText.setTextColor(0xff999999);
            weixinLayout.setEnabled(false);
        }
        if (loginType.equals("qq")) {
            qqText.setText("已登录");
            qqText.setTextColor(0xff999999);
            qqLayout.setEnabled(false);
        } else if (isQQBind) {
            qqText.setText("已绑定");
            qqText.setTextColor(0xff999999);
            qqLayout.setEnabled(false);
        }
    }


    /**
     * 获取当前已绑定状态
     */
    private void sendGetStatusRequest() {
        showLoadingDialog();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.IS_ACCOUNT_BIND);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<AccountBindObject>() {
            @Override
            public void success(@NonNull AccountBindObject data) {
                isWeixinBind = data.weixin == 1;
                isQQBind = data.qq == 1;
                updateUI();
            }

            @Override
            public void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }


    /**
     * 绑定其他第三方账号
     */
    private void sendMergeRequest(String otherUid, SimpleJsonResponseHandler handler) {
        showLoadingDialog();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ACCOUNT_MERGE);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("slave_uid", otherUid);

        HttpUtils.get(builder.build().toString(), params, handler);
    }
}
