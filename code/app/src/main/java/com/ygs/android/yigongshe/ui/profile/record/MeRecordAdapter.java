package com.ygs.android.yigongshe.ui.profile.record;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CharityRecordItemBean;

import java.lang.ref.WeakReference;
import java.util.List;

public class MeRecordAdapter extends BaseQuickAdapter<CharityRecordItemBean,BaseViewHolder> {

    List<CharityRecordItemBean> itemBeans;

    WeakReference<SwipeRefreshLayout> swipeRefreshLayoutWeakReference;

    public MeRecordAdapter(Context context){
        super(R.layout.item_me_record,null);

    }

    @Override
    protected void convert(BaseViewHolder helper, CharityRecordItemBean item) {

        helper.setText(R.id.me_record_title_tv,item.content);
        helper.setText(R.id.me_record_time_tv,item.create_at);
        helper.setText(R.id.me_record_duration_tv,"+"+item.duration+"h");

    }


    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){

        swipeRefreshLayoutWeakReference = new WeakReference<>(swipeRefreshLayout);
    }

    public void onRefresh(){

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayoutWeakReference.get() != null) {
                    swipeRefreshLayoutWeakReference.get().setRefreshing(false);
                }
            }
        },1000);

    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        super.setOnItemClickListener(listener);

    }
}
