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
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.original.bean.KanBarListObject;
import com.weikan.app.util.*;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liujian on 16/5/8.
 */
public class ShareKanbarActivity extends BasePullToRefreshActivity {

    private ShareKanbarAdapter mAdapter;
    private List<KanBarListObject.BarObject> mDataList = new ArrayList<>();
    private boolean[] isChecklist;

    private String tid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle b = intent.getExtras();
            tid = b.getString(BundleParamKey.TID);
            if (TextUtils.isEmpty(tid)) {
                finish();
            }
        }


        getPullRefreshListView().setMode(PullToRefreshBase.Mode.DISABLED);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        showLoadingDialog();
        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "分享到看吧";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new ShareKanbarAdapter(ShareKanbarActivity.this, mDataList);
        }
        return mAdapter;
    }

    @Override
    protected View makeHeadView() {
        return LayoutInflater.from(this).inflate(R.layout.share_kanbar_header, null);
    }

    @Override
    protected View makeFooterView() {
        View view = LayoutInflater.from(this).inflate(R.layout.share_kanbar_footer, null);
        RxView.clicks(view.findViewById(R.id.bt_share_kanbar_confirm))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        boolean isChecked = false;
                        for (boolean b : isChecklist) {
                            if (b) {
                                isChecked = true;
                            }
                        }
                        if (isChecked) {
                            sendShareRequest();
                        } else {
                            LToast.showToast("请至少选择一个看吧进行分享");
                        }
                    }
                });

        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendNewRequest() {
        Uri.Builder builder = new Uri.Builder();
        Map<String, String> params = new HashMap<>();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.BAR_INDEX);
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("attach", "mine");
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<KanBarListObject>() {
            @Override
            public void success(@NonNull KanBarListObject data) {
                mDataList.clear();
                mDataList.addAll(data.mine);
                isChecklist = new boolean[mDataList.size()];
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                super.end();
                if (getPullRefreshListView() != null) {
                    getPullRefreshListView().onRefreshComplete();
                }
                hideLoadingDialog();
            }
        });
    }

    private void sendShareRequest() {
        Uri.Builder builder = new Uri.Builder();
        Map<String, String> params = new HashMap<>();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.BAR_SHARE);
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TID, tid);
        String ids = "";
        for (int i = 0; i < isChecklist.length; i++) {
            if (isChecklist[i]) {
                ids += mDataList.get(i).channel_id;
                ids += ",";
            }
        }
        if (ids.length() > 1) {
            ids = ids.substring(0, ids.length() - 2);
        }
        params.put("channel_id", ids);
        params.put("act", "1");
        showLoadingDialog();
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("转发成功");
                finish();
            }

            @Override
            public void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }

    class ShareKanbarAdapter extends BaseAdapter {

        private Context context;
        private List<KanBarListObject.BarObject> data;
        private LayoutInflater inflater;

        public ShareKanbarAdapter(Context context, List<KanBarListObject.BarObject> data) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.share_kanbar_listitem, null);
                holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tvNickname = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ivLock = (ImageView) convertView.findViewById(R.id.iv_kanbar_lock);
                holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_check);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            KanBarListObject.BarObject info = data.get(position);
            if (!TextUtils.isEmpty(info.background_pic)) {
                ImageLoaderUtil.updateImage(holder.ivAvatar, info.background_pic, R.drawable.user_default);
            }
            holder.tvNickname.setText(info.channel_name);
            if (info.is_public == 1) {
                holder.ivLock.setVisibility(View.GONE);
            } else {
                holder.ivLock.setVisibility(View.VISIBLE);
            }
            holder.ivCheck.setImageResource(R.drawable.icon_fav_grey);
            setCheck(position, holder.ivCheck);

            RxView.clicks(holder.ivCheck)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            isChecklist[position] = !isChecklist[position];
                            setCheck(position, holder.ivCheck);
                        }
                    });

            return convertView;
        }

        void setCheck(int position, ImageView iv) {
            if (isChecklist != null && isChecklist.length == getCount()) {
                if (isChecklist[position]) {
                    iv.setImageResource(R.drawable.attention_checked);
                } else {
                    iv.setImageResource(R.drawable.attention_normal);
                }
            }
        }

        public class ViewHolder {

            ImageView ivAvatar;
            ImageView ivLock;

            TextView tvNickname;

            ImageView ivCheck;
        }
    }

}