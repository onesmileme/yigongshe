package com.ygs.android.yigongshe.ui.profile.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.TalkItemBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

import java.util.List;

public class MsgTalkAdapter extends BaseQuickAdapter<TalkItemBean,BaseViewHolder> {

    private Context mContext;
    private int TIME_SPLIT = 10*60;//10分钟

    public MsgTalkAdapter(Context context){
        super(R.layout.item_msg_talk,null);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, TalkItemBean item) {

        if (item.sender_id == null){
            return;
        }

        int myuid = YGApplication.accountManager.getUserid();
        int senderId = Integer.valueOf(item.sender_id);

        View left = helper.getView(R.id.rl_talk_item_left);
        View right = helper.getView(R.id.rl_talk_item_right);
        TextView textView;
        if (senderId == myuid){
            //my
            left.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);

            textView = helper.getView(R.id.tv_talk_item_text_right);

        }else{
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
            textView = helper.getView(R.id.tv_talk_item_text_left);
        }

        textView.setText(item.content);

        ImageView avatarImageView = helper.getView(R.id.iv_talk_item_header_right);
        Drawable avatar = mContext.getResources().getDrawable(R.drawable.defalutavar);
        ImageLoadUtil.loadImage(avatarImageView,item.avatar,avatar);

        //List<TalkItemBean> list = this.getData();
        //int index = list.indexOf(item);
        //if (index < 0){
        //    return;
        //}
        //int lastCreateTime = 0;
        //if (index > 0){
        //    TalkItemBean lastTalkItem = list.get(index - 1);
        //    lastCreateTime = lastCreateTime
        //}

        TextView timelineTv = helper.getView(R.id.time_line);
        if (item.create_at != null){
            timelineTv.setText(item.create_at);
            timelineTv.setVisibility(View.VISIBLE);
        }else{
            timelineTv.setVisibility(View.GONE);
        }

    }
}
