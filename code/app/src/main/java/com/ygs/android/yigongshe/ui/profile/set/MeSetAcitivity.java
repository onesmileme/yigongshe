package com.ygs.android.yigongshe.ui.profile.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.MainActivity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.webview.WebViewActivity;

import butterknife.BindView;

public class MeSetAcitivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.me_set_recycleview)
    RecyclerView mRecyclerView;

    @BindView(R.id.me_set_logout_btn)
    Button mLogoutButton;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    @Override
    protected void initIntent(Bundle bundle){

    }

    @Override
    protected void initView(){

        MeSetAdapter adapter = new MeSetAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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

    @Override
    protected int getLayoutResId(){

        return R.layout.activity_me_set;
    }
    @Override
    public void onClick(View view){

        if (view == mLogoutButton){
            YGApplication.accountManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }

    private void handleClickItem(int index){

        switch (index){
            case MeSetAdapter.FRQ_QUESTION:{
                openWebView("qa.html","常见问题");
                break;
            }
            case MeSetAdapter.ABOUT_US:{
                openWebView("about_us.html","关于我们");
                break;
            }
            case MeSetAdapter.CLEAR_CACHE:{
                break;
            }
            case MeSetAdapter.USER_PROTOCOL:{
                openWebView("user_protocol.html","用户协议");
                break;
            }
        }
    }

    private void openWebView(String htmlName , String title){

        Intent intent = new Intent(this,WebViewActivity.class);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(WebViewActivity.TITLE_KEY, title);
        }
        intent.putExtra(WebViewActivity.LOCAL_HTML_PATH,htmlName);

        startActivity(intent);

    }


}
