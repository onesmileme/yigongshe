package com.ygs.android.yigongshe.ui.profile.message;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MessageItemBean;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


public class MessageAdapter extends BaseQuickAdapter<MessageItemBean,BaseViewHolder> implements SwipeRefreshLayout.OnRefreshListener {

    private List<MessageItemBean> msgList;
    private List<MessageItemBean> notificationList;
    private int segmentIndex;

    WeakReference <SwipeRefreshLayout>  swipeRefreshLayoutWeakReference;

    public MessageAdapter(){
        super(R.layout.item_message_msg,null);
        msgList = new LinkedList<>();
        notificationList = new LinkedList<>();


    }

    protected void changeSegment(int position){

        segmentIndex = position;

        notifyDataSetChanged();
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){

        swipeRefreshLayoutWeakReference = new WeakReference<>(swipeRefreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItemBean item) {

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



}
