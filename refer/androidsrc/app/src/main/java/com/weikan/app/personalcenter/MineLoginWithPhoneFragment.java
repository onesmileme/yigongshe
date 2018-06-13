package com.weikan.app.personalcenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.bean.LoginResult;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.util.CheckLegalUtils;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.KeyBoardUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

/**
 * Created by liujian on 16/12/4.
 */
public class MineLoginWithPhoneFragment extends BaseFragment {
    private boolean isTitleShow = true;
    @Bind(R.id.et_mine_login_phone)
    EditText etLoginPhone;
    @Bind(R.id.et_mine_login_pwd)
    EditText etLoginPwd;

    @Bind(R.id.tv_mine_login_register)
    TextView tvRegist;
    @Bind(R.id.tv_mine_login_forget_pwd)
    TextView tvFoegetPwd;

    @Bind(R.id.iv_mine_login_avatar)
    ImageView ivWeixinAvatar;
    @Bind(R.id.tv_mine_login_text)
    TextView tvWeixinText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTitleShow(boolean titleShow) {
        isTitleShow = titleShow;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_login_with_phone, null);
        ButterKnife.bind(this, view);//绑定framgent
        etLoginPhone.setSaveEnabled(false);
        etLoginPwd.setSaveEnabled(false);
        if (isTitleShow) {
            view.findViewById(R.id.iv_titlebar_back).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.tv_titlebar_title)).setText("登录");
        } else {
            view.findViewById(R.id.base_pull_title).setVisibility(View.INVISIBLE);
        }
        RxView.clicks(tvRegist)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        IntentUtils.to(getActivity(), MineRegistActivity.class);
                    }
                });
        RxView.clicks(tvFoegetPwd)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        IntentUtils.to(getActivity(), MineForgetActivity.class);
                    }
                });
        RxView.clicks(ivWeixinAvatar)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        AccountManager.getInstance().logout(getActivity());
                        AccountManager.getInstance().gotoDirectWeiXinLogin(getActivity(), null);
                    }
                });
        RxView.clicks(tvWeixinText)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        AccountManager.getInstance().logout(getActivity());
                        AccountManager.getInstance().gotoDirectWeiXinLogin(getActivity(), null);
                    }
                });
        return view;
    }

    @OnClick(R.id.btn_mine_login_phone)
    public void login() {
        String phone = etLoginPhone.getText().toString().trim();
        String pwd = etLoginPwd.getText().toString().trim();

        if (CheckLegalUtils.checkPhone(phone) && CheckLegalUtils.checkPwd(pwd)) {
            PersonalAgent.postLoginByPhone(phone, pwd, new JsonResponseHandler<LoginResult.UserInfoContent>() {
                @Override
                public void success(LoginResult.UserInfoContent data) {
                    AccountManager.getInstance().onUserLoginSuccess(getActivity(), data);
                }
            });
            KeyBoardUtils.hide(getActivity());
        }
    }
}
