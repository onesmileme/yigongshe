package com.weikan.app.live.widget;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.live.LiveAgent;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.util.DialogUtils;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.DynamicHeightImageView;

import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;
import rx.functions.Action1;

/**
 * @author kailun on 16/8/16.
 */
public class LiveListItemView extends BaseListItemView<LiveListObject> {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvAudienceCount;
    private TextView tvStatus;
    private ImageView ivStartIcon;

    private DynamicHeightImageView ivBg;

    private LinearLayout llTags;

    public Action1<LiveListItemView> actionUserClick;
    private ImageView deleteLive;
    private Context context;
    public LiveListItemView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_live_list_item_view;
    }

    @Override
    protected void initViews() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionUserClick != null) {
                    actionUserClick.call(LiveListItemView.this);
                }
            }
        };

        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(onClickListener);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvName.setOnClickListener(onClickListener);

        tvTime = (TextView) findViewById(R.id.tv_status);
        tvAudienceCount = (TextView) findViewById(R.id.tv_audience_count);
        tvStatus = (TextView) findViewById(R.id.tv_live_status);
        ivStartIcon = (ImageView) findViewById(R.id.iv_start_icon);

        ivBg = (DynamicHeightImageView) findViewById(R.id.iv_bg);
        ivBg.setHeightRatio(0.8);
        llTags = (LinearLayout) findViewById(R.id.ll_tags);
        deleteLive = (ImageView)findViewById(R.id.iv_delete);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void set(@Nullable final LiveListObject item) {
        super.set(item);

        if (item != null) {
            ImageLoaderUtil.updateImage(ivAvatar, item.headImgUrl,
                    R.drawable.user_default, R.drawable.user_default, null );

            tvName.setText(item.author);

            if(item.status==1) {
                tvTime.setText("直播中");
                tvStatus.setText("直播");
                tvStatus.setSelected(true);
                deleteLive.setVisibility(GONE);
            } else if(item.status==2) {
                // 预告
                deleteLive.setVisibility(GONE);
            } else if(item.status==3) {
                tvTime.setText(new FriendlyDate(item.etime*1000).toFriendlyDate(false));
                tvStatus.setText("已结束");
                tvStatus.setSelected(false);
                if(!TextUtils.isEmpty(item.uid) && item.uid.equals(AccountManager.getInstance().getUserId())){
                    deleteLive.setVisibility(VISIBLE);
                }else{
                    deleteLive.setVisibility(GONE);
                }
            }
            if (item.status == 1) {
                tvAudienceCount.setText(item.watchNum + "人在看");
            } else {
                tvAudienceCount.setText(item.watchNum + "人看过");
            }

            ivStartIcon.setImageResource(R.drawable.video_live_item_default);
            ivBg.setBackgroundColor(getResources().getColor(R.color.live_list_item_default_bg));
            if (!TextUtils.isEmpty(item.cover)) {
                ImageLoaderUtil.updateImage(ivBg, item.cover, 0, 0, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivStartIcon.setImageResource(R.drawable.icon_live_play);
                    }

                    @Override
                    public void onError() {
                        ivStartIcon.setImageResource(R.drawable.video_live_item_default);
                    }
                });
            } else {
//                ivBg.setImageResource(R.drawable.video_live_item_default);
                ivBg.setBackgroundColor(getResources().getColor(R.color.live_list_item_default_bg));
            }

//            llTags.removeAllViews();
//            for (String tag : item.tags) {
//                llTags.addView(makeTagView(tag));
//            }
        }
    }

    public TextView makeTagView(String tag) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);

        float density = getResources().getDisplayMetrics().density;
        tv.setPadding(Math.round(20 * density), Math.round(10 * density), 0, Math.round(10 * density));

        String showedTag = "#" + tag;
        tv.setText(showedTag);

        tv.setTextColor(Color.rgb(0x6f, 0x6f, 0x6f));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        return tv;
    }
    public void setOnDeleteLiveListener(final DeletLiveListener listener){
        deleteLive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showOkCancel(context, "", "确定删除该视频么？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("删除中...");
                        progressDialog.show();
                        LiveAgent.deleteLive(item.liveId, AccountManager.getInstance().getUserId(), new SimpleJsonResponseHandler() {
                            @Override
                            public void success() {
                                listener.onSuccess();
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }

                            @Override
                            protected void failed(FailedResult r) {
                                super.failed(r);
                                listener.onFail();
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }
    public interface DeletLiveListener{
        void onSuccess();
        void onFail();
    }
}
