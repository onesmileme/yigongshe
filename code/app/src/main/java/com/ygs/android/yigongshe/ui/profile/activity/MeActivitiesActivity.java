package com.ygs.android.yigongshe.ui.profile.activity;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.SegmentControlView;

import butterknife.BindView;

public class MeActivitiesActivity extends BaseActivity {

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.my_mactivity_segment)
    SegmentControlView segmentControlView;

    @BindView(R.id.me_activities_recycleview)
    RecyclerView recyclerView;

    protected void initIntent(){

    }

    protected void initView(){

        titleView.setText(R.string.my_activity);


    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_activities;
    }
}
