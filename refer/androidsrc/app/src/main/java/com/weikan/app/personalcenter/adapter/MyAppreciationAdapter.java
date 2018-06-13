package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.personalcenter.bean.AppreciationInfo;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.List;

/**
 * Created by zhaorenhui on 2015/11/28.
 */
public class MyAppreciationAdapter extends BaseAdapter {
    private Context context;
    private List<AppreciationInfo> data;
    private LayoutInflater inflater;

    public MyAppreciationAdapter(Context context, List<AppreciationInfo> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.appreciation_info_row, null);

            com.weikan.app.bean.UserInfoObject.UserInfoContent userData = AccountManager.getInstance().getUserData();

                    convertView.findViewById(R.id.ll_sync).setVisibility(View.GONE);

            holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvSync = (TextView) convertView.findViewById(R.id.tv_sync);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppreciationInfo object = (AppreciationInfo) getItem(position);
        if (!TextUtils.isEmpty(object.img)) {
            ImageLoaderUtil.updateImage(holder.ivPic, object.img);
        }
        holder.ivPic.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = holder.ivPic.getLayoutParams();
                layoutParams.width = holder.ivPic.getMeasuredWidth();
                layoutParams.height = holder.ivPic.getMeasuredWidth() / 9 * 5;
                holder.ivPic.setLayoutParams(layoutParams);
            }
        });
        if (object.cTime > 0) {
            FriendlyDate friendlyDate = new FriendlyDate(object.cTime * 1000);
            holder.tvCreateTime.setText(friendlyDate.toFriendlyDate(false));
        }
        holder.tvPrice.setText(String.valueOf(object.totalPrice));
        if (!TextUtils.isEmpty(object.title)) {
            holder.tvTitle.setText(object.title);
        }
        if (object.isSync == 0) {
            holder.tvSync.setText("同步至微信公众号素材库");
        } else {
            holder.tvSync.setText("已同步");
        }
        holder.tvSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object.isSync == 0) {
                    if(listener!=null){
                        listener.onInfoSync(object,position);
                    }
                }
            }
        });

        return convertView;
    }

    private InfoSyncListener listener;

    public void setInfoSyncListener(InfoSyncListener listener) {
        this.listener = listener;
    }

    public interface InfoSyncListener {
        void onInfoSync(AppreciationInfo info ,int position);
    }


    public class ViewHolder {
        ImageView ivPic;

        TextView tvCreateTime;

        TextView tvPrice;

        TextView tvTitle;

        TextView tvSync;


    }


}
