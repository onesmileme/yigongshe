package com.ygs.android.yigongshe.ui.profile.charitytime;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.CharityDurationBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.apply.MeApplyActivity;
import com.ygs.android.yigongshe.ui.profile.record.MeRecordListActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 我的公益时
 */
public class MeCharityTimeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.me_charity_time_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.me_charity_time_tv)
    TextView charityTimeTextView;

    @BindView(R.id.me_charity_confirm_btn)
    Button charityConfirmButton;

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    MeCharityMedalAdapter medalAdapter;

    LinkCall<BaseResultDataInfo<CharityDurationBean>> mCall;

    CharityDurationBean mCharityDurationBean;

    @Override
    protected void initIntent(Bundle bundle) {

    }

    @Override
    protected void initView() {

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }else if (action == CommonTitleBar.ACTION_RIGHT_TEXT){
                    showRecord();
                }
            }
        });

        charityConfirmButton.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        medalAdapter = new MeCharityMedalAdapter();
        recyclerView.setAdapter(medalAdapter);

        loadData();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_me_charity_time;
    }

    @Override
    public void onClick(View view) {
        if (view == charityConfirmButton) {
            Bundle bundle = new Bundle();
            bundle.putInt("duration",mCharityDurationBean.duration);
            goToOthers(MeApplyActivity.class,bundle);
        }
    }

    private void showRecord(){

        goToOthers(MeRecordListActivity.class,null);

    }

    private void loadData() {

        AccountManager accountManager = YGApplication.accountManager;
        mCall = LinkCallHelper.getApiService().getCharityDuration(accountManager.getToken());
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CharityDurationBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<CharityDurationBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    mCharityDurationBean = entity.data;
                    charityTimeTextView.setText(mCharityDurationBean.duration + "");
                    medalAdapter.setNewData(mCharityDurationBean.achievements);
                }
            }
        });
    }

}
