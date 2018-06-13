package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.personalcenter.bean.MyTalkListObject;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.List;

/**
 * Created by liujian on 16/11/13.
 */
public class MyTalkListAdapter extends BaseAdapter {

    private Context context;
    private List<MyTalkListObject.MyTalkListContent> data;
    private LayoutInflater inflater;

    public MyTalkListAdapter(Context context, List<MyTalkListObject.MyTalkListContent> data) {
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
            convertView = inflater.inflate(R.layout.my_talk_list_item, null);
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tvRed = (TextView) convertView.findViewById(R.id.tv_red);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MyTalkListObject.MyTalkListContent msg = data.get(position);
        if (msg != null) {
            if (!TextUtils.isEmpty(msg.headimgurl)) {
                ImageLoaderUtil.updateImage(holder.ivAvatar, msg.headimgurl, R.drawable.user_default);
            } else {
                holder.ivAvatar.setImageResource(R.drawable.user_default);
            }
            holder.tvNickname.setText(msg.nickname);
            holder.tvContent.setText(FaceConversionUtil.getInstace().getExpressionString(context, msg.content));

//            FriendlyDate friendlyDate = new FriendlyDate(msg.ctime * 1000 );
//            holder.tvCreateTime.setText(friendlyDate.toFriendlyDate(false));
            holder.tvCreateTime.setText(msg.ctime);
            if(msg.msg_num>0){
                holder.tvRed.setVisibility(View.VISIBLE);
                holder.tvRed.setText(msg.msg_num + "");
            } else {

                holder.tvRed.setVisibility(View.GONE);
                holder.tvRed.setText("");
            }

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

        TextView tvNickname;

        TextView tvCreateTime;

        TextView tvContent;

        TextView tvRed;
    }
}
