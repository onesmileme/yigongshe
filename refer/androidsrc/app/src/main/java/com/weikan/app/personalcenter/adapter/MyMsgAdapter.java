package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.personalcenter.bean.MyMsgObject;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LLog;
import rx.functions.Action1;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liujian on 16/7/30.
 */
public class MyMsgAdapter extends BaseAdapter {

    private Context context;
    private List<MyMsgObject.ContentObject> data;
    private LayoutInflater inflater;

    public MyMsgAdapter(Context context, List<MyMsgObject.ContentObject> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.my_msg_item, null);
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_sys_msg_avatar);
            holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_sys_msg_pic);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tv_sys_msg_nickname);
            holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_sys_msg_content);
            holder.tvReplyContent = (TextView) convertView.findViewById(R.id.tv_sys_msg_zantext);
            holder.ivZan = (ImageView) convertView.findViewById(R.id.iv_my_msg_zan);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MyMsgObject.ContentObject msg = data.get(position);
        if (msg != null) {
            holder.tvNickname.setText("");
            holder.ivPic.setImageDrawable(null);
            if (msg.fromUser != null) {
                if (!TextUtils.isEmpty(msg.fromUser.headimgurl)) {
                    ImageLoaderUtil.updateImage(holder.ivAvatar, msg.fromUser.headimgurl, R.drawable.user_default);
                }
                holder.tvNickname.setText(msg.fromUser.nickname);
            }

            if (msg.pic != null && msg.pic.t != null && !TextUtils.isEmpty(msg.pic.t.url)) {
                ImageLoaderUtil.updateImage(holder.ivPic, msg.pic.t.url);
                holder.tvContent.setVisibility(View.GONE);
                holder.ivPic.setVisibility(View.VISIBLE);
            } else {
                holder.ivPic.setVisibility(View.GONE);
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(FaceConversionUtil.getInstace().getExpressionString(context,msg.msgDesc));
            }

            switch (msg.actionType){
                case 1:
                    // 点赞
                    holder.ivZan.setVisibility(View.VISIBLE);
                    holder.tvReplyContent.setVisibility(View.GONE);
                    break;
                case 2:
                    // 评论
                    holder.ivZan.setVisibility(View.GONE);
                    holder.tvReplyContent.setVisibility(View.VISIBLE);
                    holder.tvReplyContent.setText( FaceConversionUtil.getInstace().getExpressionString(context,msg.commentContent));
                    break;
                case 3:
                    // 回复评论
                    holder.ivZan.setVisibility(View.GONE);
                    holder.tvReplyContent.setVisibility(View.VISIBLE);
                    if(msg.replyUser!=null && !TextUtils.isEmpty(msg.replyUser.nickname)){
//                        SpannableStringBuilder ssb = new SpannableStringBuilder(
//                                "回复了" + msg.replyUser.nickname + "：" + msg.commentContent);
                        SpannableStringBuilder ssb = new SpannableStringBuilder(
                                FaceConversionUtil.getInstace().getExpressionString(context,"回复了" + msg.replyUser.nickname + "：" + msg.commentContent));
                        ForegroundColorSpan nameSpan = new ForegroundColorSpan(Color.parseColor("#576b95"));
                        ssb.setSpan(nameSpan, 3,3+msg.replyUser.nickname.length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        holder.tvReplyContent.setText(ssb);
                    } else {
                        holder.tvReplyContent.setText(FaceConversionUtil.getInstace().getExpressionString(context,msg.commentContent));
                    }
                    break;
            }

            FriendlyDate friendlyDate = new FriendlyDate(msg.ctime * 1000 );
            holder.tvCreateTime.setText(friendlyDate.toFriendlyDate(false));

//            RxView.clicks(holder.ivAvatar)
//                    .throttleFirst(500, TimeUnit.MILLISECONDS)
//                    .subscribe(new Action1<Void>() {
//                        @Override
//                        public void call(Void a) {
//                            if (msg.fromUser == null) {
//                                return;
//                            }
//                            Intent intent = new Intent();
//                            intent.setClass(context, UserHomeActivity.class);
//                            intent.putExtra("uid", msg.fromUser.uid);
//                            context.startActivity(intent);
//                        }
//                    });
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView ivAvatar;

        ImageView ivPic;

        TextView tvNickname;

        TextView tvReplyContent;

        TextView tvCreateTime;

        TextView tvContent;

        ImageView ivZan;
    }
}

