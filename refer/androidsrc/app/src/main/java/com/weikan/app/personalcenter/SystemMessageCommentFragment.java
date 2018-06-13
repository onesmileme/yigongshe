package com.weikan.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshFragment;
import com.weikan.app.original.OriginalDetailActivity;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.personalcenter.bean.SystemMessageData;
import com.weikan.app.util.*;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统通知-评论
 * Created by liujian on 16/2/19.
 */
public class SystemMessageCommentFragment extends BasePullToRefreshFragment {

    private List<SystemMessageData.SystemMessage> mDataList = new ArrayList<>();
    private BaseAdapter mAdapter;
    private String tabName = "评论";


    private OnDataReceiveListener onDataReceiveListener;

    public interface OnDataReceiveListener {
        void onNewReceive(String tabName);
        void onNextReceive(String tabName);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener listener){
        onDataReceiveListener = listener;
    }

    public List<SystemMessageData.SystemMessage> getData(){
        return mDataList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = getArguments();
        if (b != null) {
            tabName = b.getString(Constants.TAB_NAME);
        }

        if (mDataList.size() == 0
                && tabName.equals("评论")) {
            sendNewRequest(-1);
        }

        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDataList == null) {
                    return;
                }
                SystemMessageData.SystemMessage originalObject = mDataList.get(position - 1);
                if (originalObject == null) {
                    return;
                }
                if(!TextUtils.isEmpty(originalObject.tid)){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OriginalDetailActivity.class);
                    intent.putExtra("tid", originalObject.tid);
                    startActivity(intent);
                } else if(originalObject.from_user!=null && !TextUtils.isEmpty(originalObject.from_user.uid)){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UserHomeActivity.class);
                    intent.putExtra("uid", originalObject.from_user.uid);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SystemMessageCommentAdapter(getActivity(), mDataList);
        }
        return mAdapter;
    }


    @Override
    protected void onPullDown() {
        sendNewRequest(-1);
    }

    @Override
    protected void onPullUp() {
        SystemMessageData.SystemMessage msg = mDataList.isEmpty() ? null
                : mDataList.get(mDataList.size() - 1);
        if (msg != null) {
            sendNextRequest(msg.sys_msg_id);
        }
    }


    public void sendNewRequest(long id) {
        Uri.Builder builder = new Uri.Builder();
        Map<String, String> params = new HashMap<>();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        makePath(builder);
        params.put(URLDefine.TYPE, TYPE_NEW);
        if (id != -1) {
            params.put("first_sid", String.valueOf(id));
        }
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        httpHandler.setRefreshType(TYPE_NEW);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    private void sendNextRequest(long id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        makePath(builder);
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("last_sid", String.valueOf(id));
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        httpHandler.setRefreshType(TYPE_NEXT);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    private void makePath(Uri.Builder builder) {
        if (tabName.equals("评论")) {
            builder.encodedPath(URLDefine.MSG_CMTLIST);
        } else if(tabName.equals("赞")){
            builder.encodedPath(URLDefine.MSG_ZANLIST);
        } else {
            builder.encodedPath(URLDefine.MSG_SYSLIST);
        }
    }

    private MyHttpResponseHandler httpHandler = new MyHttpResponseHandler();

    public class MyHttpResponseHandler extends JsonResponseHandler<SystemMessageData> {

        private String refreshType = TYPE_NEW;

        public void setRefreshType(String refreshType) {
            this.refreshType = refreshType;
        }

        @Override
        public void success(@NonNull SystemMessageData data) {
            List<SystemMessageData.SystemMessage> newOriginalObjects = data.content;
            if (newOriginalObjects != null && newOriginalObjects.size() > 0) {
                if (refreshType.equals(TYPE_NEW)) {
                    // 下拉刷新，清空后最新的从头开始加
                    if (mDataList != null) {
                        mDataList.clear();
                        mDataList.addAll(0, newOriginalObjects);
                        mAdapter.notifyDataSetChanged();

                        if (onDataReceiveListener != null) {
                            onDataReceiveListener.onNewReceive(tabName);
                        }
                    }
                } else if (refreshType.equals(TYPE_APPEND)) {
                    if (mDataList != null) {
                        mDataList.addAll(0, newOriginalObjects);
                        mAdapter.notifyDataSetChanged();

                        if (onDataReceiveListener != null) {
                            onDataReceiveListener.onNextReceive(tabName);
                        }
                    }
                } else {
                    // 上拉加载更多，从最后加
                    if (mDataList != null) {
                        int size = mDataList.size();
                        mDataList.addAll(size, newOriginalObjects);
                        mAdapter.notifyDataSetChanged();

                        if (onDataReceiveListener != null) {
                            onDataReceiveListener.onNextReceive(tabName);
                        }
                    }

                }
            }
        }

        @Override
        public void end() {
            super.end();

            if (getPullRefreshListView() != null) {
                getPullRefreshListView().onRefreshComplete();
            }
        }
    }


    static class SystemMessageCommentAdapter extends BaseAdapter {

        private Context context;
        private List<SystemMessageData.SystemMessage> data;
        private LayoutInflater inflater;

        public SystemMessageCommentAdapter(Context context, List<SystemMessageData.SystemMessage> data) {
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
                convertView = inflater.inflate(R.layout.system_message_row, null);
                holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_sys_msg_avatar);
                holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_sys_msg_pic);
                holder.tvNickname = (TextView) convertView.findViewById(R.id.tv_sys_msg_nickname);
                holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_sys_msg_content);
                holder.tvZanText = (TextView) convertView.findViewById(R.id.tv_sys_msg_zantext);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final SystemMessageData.SystemMessage msg = data.get(position);
            if (msg != null) {
                holder.tvNickname.setText("");
                holder.ivPic.setImageDrawable(null);
                if (msg.from_user != null) {
                    if (!TextUtils.isEmpty(msg.from_user.headimgurl)) {
                        ImageLoaderUtil.updateImage(holder.ivAvatar, msg.from_user.headimgurl, R.drawable.user_default);
                    }
                    holder.tvNickname.setText(msg.from_user.nickname);
                }

                if (msg.pic != null && msg.pic.t != null && !TextUtils.isEmpty(msg.pic.t.url)) {
                    ImageLoaderUtil.updateImage(holder.ivPic, msg.pic.t.url);
                }

                holder.tvZanText.setText(msg.action_content);
                if (TextUtils.isEmpty(msg.digest)) {
                    holder.tvContent.setVisibility(View.GONE);
                } else {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(msg.digest);
                }

                FriendlyDate friendlyDate = new FriendlyDate(msg.ctime * 1000);
                holder.tvCreateTime.setText(friendlyDate.toFriendlyDate(false));

                RxView.clicks(holder.ivAvatar)
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void a) {
                                if (msg.from_user == null) {
                                    return;
                                }
                                Intent intent = new Intent();
                                intent.setClass(context, UserHomeActivity.class);
                                intent.putExtra("uid", msg.from_user.uid);
                                context.startActivity(intent);

                                LLog.e("key:" + System.currentTimeMillis());
                            }
                        });
            }
            return convertView;
        }

        public class ViewHolder {
            ImageView ivAvatar;

            ImageView ivPic;

            TextView tvNickname;

            TextView tvZanText;

            TextView tvCreateTime;

            TextView tvContent;
        }
    }
}
