package com.ygs.android.yigongshe.ui.profile.apply;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import retrofit2.Response;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.io.IOException;

/**
 * 申请时长
 */
public class MeApplyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.me_apply_submit_btn)
    Button mSubmitButton;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    @BindView(R.id.me_apply_duration_et)
    EditText mDurationEditText;

    @BindView(R.id.me_apply_reason_et)
    EditText mReasonEditText;

    @BindView(R.id.apply_container_layout)
    ConstraintLayout constraintLayout;

    private View loadingView;
    private int mDuration;

    @Override
    protected void initIntent(Bundle bundle) {

        mDuration = bundle.getInt("duration");
    }

    @Override
    protected void initView() {
        mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });
        mSubmitButton.setOnClickListener(this);
        if (mDuration > 0){
            mDurationEditText.setText(""+mDuration);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_me_apply;
    }

    @Override
    public void onClick(View view) {
        if (view == mSubmitButton) {
            submit();
        }
    }

    private void showLoading() {

        if (loadingView == null) {

            loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, constraintLayout);
            View view = loadingView.findViewById(R.id.loadingview);
            view.setBackgroundColor(Color.TRANSPARENT);

            //constraintLayout.removeView(loadingView);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loadingView.setLayoutParams(layoutParams);
            //constraintLayout.addView(loadingView, 0, layoutParams);

        }

        Log.e("APPLY", "showLoading: loadingview is: " + loadingView);

        loadingView.setVisibility(View.VISIBLE);

    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    private void submit() {

        String duration = mDurationEditText.getText().toString();
        String content = mReasonEditText.getText().toString();

        String tip = null;
        if (duration.length() == 0) {
            tip = "请输入申请时长";
        } else if (content.length() == 0) {
            tip = "请输入申请内容";
        }
        if (tip != null) {
            Toast.makeText(this, tip, Toast.LENGTH_SHORT);
            return;
        }

        float durationValue = 0;
        try {
            durationValue = Float.valueOf(duration);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请输入正确的内容", Toast.LENGTH_SHORT);
            return;
        }

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<EmptyBean>> call = LinkCallHelper.getApiService().addCharityDuration(durationValue,
            content,token);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    Toast.makeText(MeApplyActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                    mDurationEditText.setText("");
                    mReasonEditText.setText("");
                }else{
                    String msg = "申请失败";
                    if (entity != null && !TextUtils.isEmpty(entity.msg)){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MeApplyActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkError(IOException e, LinkCall call) {
                super.networkError(e, call);
                Toast.makeText(MeApplyActivity.this,"申请请求失败("+e.getMessage()+")",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
