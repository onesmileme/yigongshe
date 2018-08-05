package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.ui.otherhomepage.OtherHomePageActivity;
import com.ygs.android.yigongshe.view.CommunityDetailHeaderView;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityDetailActivity extends BaseDetailActivity {
    private CommunityDetailHeaderView mHeaderView;
    private CommunityItemBean mCommunityItemBean;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_community_detail;
    }

    @Override
    protected void initIntent(Bundle bundle) {
        mId = bundle.getInt("pubcircle_id");
        mTitle = "详情";
        mCommunityItemBean = (CommunityItemBean)bundle.getSerializable("item");
        mType = TYPE_COMMUNITY;
    }

    @Override
    protected void addHeaderView() {
        mHeaderView = new CommunityDetailHeaderView(this, mRecyclerView,
            new CommunityDetailHeaderView.ItemClickListener() {
                @Override
                public void onItemClicked(CommunityDetailHeaderView.ItemClickType itemClickType) {
                    switch (itemClickType) {
                        case DELTE:
                            finish();
                            break;
                        case AVATAR:
                            showOtherPage();
                            break;
                    }

                }
            });
        mHeaderView.setViewData(mCommunityItemBean);
        mAdapter.addHeaderView(mHeaderView.getView());
    }

    @Override
    protected void initView() {
        super.initView();
        requestCommentData(TYPE_COMMUNITY, true);
    }

    private void showOtherPage() {

        String uid = mCommunityItemBean.create_id + "";
        if (mCommunityItemBean.create_id == YGApplication.accountManager.getUserid()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("userid", mCommunityItemBean.create_id + "");
        goToOthers(OtherHomePageActivity.class, bundle);
    }
}
