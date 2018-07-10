package com.ygs.android.yigongshe.ui.profile.record;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindInt;
import butterknife.BindView;

/**
 * 公益记录
 */
public class MeRecordListActivity extends BaseActivity {


    @BindView(R.id.me_record_recycle_view)
    RecyclerView recyclerView;

    MeRecordAdapter recordAdapter;

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.message_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    protected  void initIntent(Bundle bundle){

    }

    protected  void initView(){

        recordAdapter = new MeRecordAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordAdapter);

        titleView.setText(R.string.charity_record);


    }

    protected  int getLayoutResId(){
        return R.layout.activity_record_list;
    }
}
