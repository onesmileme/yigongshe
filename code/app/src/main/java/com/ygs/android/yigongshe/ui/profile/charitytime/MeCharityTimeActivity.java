package com.ygs.android.yigongshe.ui.profile.charitytime;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

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

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_backward_btn)
    Button backButton;

    MeCharityMedalAdapter medalAdapter;

    protected void initIntent(Bundle bundle){

    }

    protected  void initView(){

        backButton.setOnClickListener(this);
        charityConfirmButton.setOnClickListener(this);
        titleView.setText(R.string.my_charity_time);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

        medalAdapter = new MeCharityMedalAdapter();
        recyclerView.setAdapter(medalAdapter);

    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_charity_time;
    }

    public void onClick(View view){
        if (view == charityConfirmButton){

        }else if(view == backButton){

        }
    }
}
