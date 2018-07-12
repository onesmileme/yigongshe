package com.ygs.android.yigongshe.ui.profile.set;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;

public class MeSetAcitivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.me_set_recycleview)
    RecyclerView mRecyclerView;

    @BindView(R.id.me_set_logout_btn)
    Button mLogoutButton;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    protected void initIntent(Bundle bundle){

    }


    protected void initView(){

        MeSetAdapter adapter = new MeSetAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                handleClickItem(position);
            }
        });

        mLogoutButton.setOnClickListener(this);

        mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    MeSetAcitivity.this.finish();
                }
            }
        });

    }


    @Override
    protected boolean openTranslucentStatus() {
        return true;
    }

    protected int getLayoutResId(){

        return R.layout.activity_me_set;
    }

    public void onClick(View view){

        if (view == mLogoutButton){

        }
    }

    private void handleClickItem(int index){

        if (index == MeSetAdapter.CLEAR_CACHE){

        }else{
            //jump webview activity
        }

    }


}
