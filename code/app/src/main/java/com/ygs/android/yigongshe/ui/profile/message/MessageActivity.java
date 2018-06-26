package com.ygs.android.yigongshe.ui.profile.message;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.SegmentControlView;

import butterknife.BindView;

public class MessageActivity extends BaseActivity {


    @BindView(R.id.message_segment)
    SegmentControlView segmentControlView;

    @BindView(R.id.message_list)
    RecyclerView recyclerView;

    @BindView(R.id.message_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private MessageAdapter messageAdapter;

    protected void initIntent(Bundle bundle){

    }

    protected void initView(){


        messageAdapter = new MessageAdapter();
        segmentControlView.setOnSegmentChangedListener(new SegmentControlView.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                changeSegment(newSelectedIndex);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(messageAdapter);
        messageAdapter.setSwipeRefreshLayout(swipeRefreshLayout);
    }

    protected  int getLayoutResId(){
        return R.layout.activity_message;
    }


    private void changeSegment(int position){

    }

}
