package com.ygs.android.yigongshe.ui.profile.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MsgItemBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<MsgItemBean,BaseViewHolder> {


    private int segmentIndex;

    private int placeholder;

    public MessageAdapter(Context context){
        super(R.layout.item_message_msg,null);
        mContext = context;
    }

    protected void changeSegment(int position){

        segmentIndex = position;

        notifyDataSetChanged();
    }

    protected void updateData(List<MsgItemBean> datas , boolean isNotice){

        if (isNotice){
            placeholder = R.drawable.notice_icon;// mContext.getResources().getDrawable(R.drawable.notice_icon);
        }else{
            placeholder = R.drawable.message_icon;//mContext.getResources().getDrawable(R.drawable.message_icon);
        }

        super.setNewData(datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgItemBean item) {

        helper.setText(R.id.msg_title_tv,item.username);
        helper.setText(R.id.msg_sub_title_tv,item.content);
        helper.setText(R.id.msg_detail_time_tv,item.date);

        View redDot = helper.getView(R.id.msg_red_dot);
        if (item.unread_count != null && Integer.parseInt(item.unread_count)> 0){
            redDot.setVisibility(View.VISIBLE);
        }else{
            redDot.setVisibility(View.INVISIBLE);
        }

        ImageView avatarImageView = helper.getView(R.id.msg_icon);
        ImageLoadUtil.loadImage(avatarImageView,item.avatar,placeholder);

    }

    //public void onRefresh(){
    //
    //    new android.os.Handler().postDelayed(new Runnable() {
    //        @Override
    //        public void run() {
    //            if (swipeRefreshLayoutWeakReference.get() != null) {
    //                swipeRefreshLayoutWeakReference.get().setRefreshing(false);
    //            }
    //        }
    //    },1000);
    //
    //}



}
