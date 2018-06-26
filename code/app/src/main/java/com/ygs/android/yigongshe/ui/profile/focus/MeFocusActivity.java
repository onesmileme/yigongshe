package com.ygs.android.yigongshe.ui.profile.focus;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import org.w3c.dom.Text;

import butterknife.BindView;

public class MeFocusActivity extends BaseActivity {

    @BindView(R.id.me_focus_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_backward_btn)
    Button backButton;

    MeFocusAdapter focusAdapter;

    @BindView(R.id.me_focus_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    protected  void initIntent(Bundle bundle){

    }

    protected  void initView(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setText(R.string.my_focus);

        focusAdapter = new MeFocusAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        focusAdapter = new MeFocusAdapter(this);
        recyclerView.setAdapter(focusAdapter);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_focus;
    }
}
