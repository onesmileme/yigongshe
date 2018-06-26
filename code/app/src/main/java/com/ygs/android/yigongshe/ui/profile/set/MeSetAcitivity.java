package com.ygs.android.yigongshe.ui.profile.set;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

public class MeSetAcitivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.me_set_recycleview)
    RecyclerView mRecyclerView;

    @BindView(R.id.me_set_logout_btn)
    Button mLogoutButton;

    @BindView(R.id.titlebar_backward_btn)
    Button mNavBackButton;

    @BindView(R.id.titlebar_right_btn)
    Button mNavRightButton;

    @BindView(R.id.titlebar_text_title)
    TextView mTitleView;

    protected void initIntent(Bundle bundle){

    }


    protected void initView(){

        mTitleView.setText(R.string.set);
        int color = getResources().getColor(R.color.black);
        mTitleView.setTextColor(color);


        MeSetAdapter adapter = new MeSetAdapter(this);
        mRecyclerView.setAdapter(adapter);

        mLogoutButton.setOnClickListener(this);

    }


    protected int getLayoutResId(){

        return R.layout.activity_me_set;
    }

    public void onClick(View view){

    }

}
