package com.weikan.app.group.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.group.GroupAgent;
import com.weikan.app.group.GroupDetailActivity;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.LToast;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.List;

import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created by Lee on 2017/01/09.
 */
public class AllGroupAdapter extends BaseAdapter {
    private Context context;
    private List<GroupDetailBean> list;

    public static final int allGroupRequestCode = 1100;

    public AllGroupAdapter(Context context, List<GroupDetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null || list.size() == 0 ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_all_group, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GroupDetailBean groupDetailBean = list.get(position);
        if (groupDetailBean != null) {
            if (TextUtils.isEmpty(groupDetailBean.avatar.getImageUrlLittle())) {
                viewHolder.groupPhoto.setImageResource(R.drawable.ic_launcher);
            } else {
                ImageLoaderUtil.updateImage(viewHolder.groupPhoto, groupDetailBean.avatar.getImageUrlLittle(), R.drawable.ic_launcher);
            }
            viewHolder.tvName.setText(groupDetailBean.groupName);
            viewHolder.tvFollowNum.setText(groupDetailBean.followCount + "人");
            viewHolder.tvGrohpInfo.setText(groupDetailBean.intro);
            if (position == list.size() - 1) {
                viewHolder.line.setVisibility(View.GONE);
            } else {
                viewHolder.line.setVisibility(View.VISIBLE);
            }
            if (groupDetailBean.isFollowed == 1) {
                viewHolder.ivGroupAdd.setImageResource(R.drawable.icon_group_follow);
            } else {
                viewHolder.ivGroupAdd.setImageResource(R.drawable.icon_group_add);
            }

            viewHolder.ivGroupAdd.setOnClickListener(new OnNoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    if (!AccountManager.getInstance().isLogin()) {
                        AccountManager.getInstance().gotoLogin((Activity) context);
                        return;
                    }
                    if (groupDetailBean.isFollowed == 1) {
                        pendingCancelFollow(groupDetailBean);
                    } else {
                        groupFollow(groupDetailBean);
                    }
                }
            });

            convertView.setOnClickListener(new OnNoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BundleParamKey.GROUPID, groupDetailBean.groupId);
                    bundle.putString(BundleParamKey.GROUPNAME, groupDetailBean.groupName);
                    IntentUtils.toForResult((Activity) context, GroupDetailActivity.class, bundle, allGroupRequestCode);
                }
            });

        }
        return convertView;
    }

    private void cancelGroupFollow(final GroupDetailBean groupDetailBean) {
        GroupAgent.cancelGroupFollow(groupDetailBean.groupId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("取消关注成功");
                groupDetailBean.isFollowed = 0;
                if (groupDetailBean.followCount > 0) {
                    groupDetailBean.followCount--;
                }
                notifyDataSetChanged();
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                r.setIsHandled(true);
                LToast.showToast("取消关注失败");
            }
        });
    }

    private void groupFollow(final GroupDetailBean groupDetailBean) {
        GroupAgent.groupFollow(groupDetailBean.groupId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("关注成功");
                groupDetailBean.isFollowed = 1;
                groupDetailBean.followCount++;
                notifyDataSetChanged();
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                r.setIsHandled(true);
                LToast.showToast("关注失败");
            }
        });
    }

    private void pendingCancelFollow(final GroupDetailBean groupDetailBean) {
        final AlertDialog dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setMessage("是否取消关注")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelGroupFollow(groupDetailBean);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    static class ViewHolder {
        CircleImageView groupPhoto;
        TextView tvName;
        TextView tvFollowNum;
        TextView tvGrohpInfo;
        ImageView ivGroupAdd;
        View line;

        public ViewHolder(View view) {
            groupPhoto = (CircleImageView) view.findViewById(R.id.iv_avatar);
            tvName = (TextView) view.findViewById(R.id.tv_group_name);
            tvFollowNum = (TextView) view.findViewById(R.id.tv_group_follow_num);
            tvGrohpInfo = (TextView) view.findViewById(R.id.tv_group_info);
            ivGroupAdd = (ImageView) view.findViewById(R.id.iv_group_add);
            line = view.findViewById(R.id.line1);
        }
    }
}
