package com.weikan.app.personalcenter.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.personalcenter.bean.MyAttentionObject;
import com.weikan.app.util.DateUtils;
import com.weikan.app.util.URLDefine;
import rx.functions.Action1;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author kailun on 16/8/9.
 */
public class MyAttentionItemView extends BaseListItemView<MyAttentionObject> {

    private ImageView ivAvatar;
    private ImageView ivV;
    private TextView tvName;
    private TextView tvDesc;
    private TextView tvAttentionStatus;
    private View vSplitter;

    public Action1<MyAttentionItemView> actionAttentionClick;

    public MyAttentionItemView(Context context) {
        super(context);
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_my_attention_item_view;
    }

    @Override
    protected void initViews() {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivV = (ImageView) findViewById(R.id.iv_v);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvAttentionStatus = (TextView) findViewById(R.id.tv_attention_status);
        RxView.clicks(tvAttentionStatus)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (actionAttentionClick != null) {
                            actionAttentionClick.call(MyAttentionItemView.this);
                        }
                    }
                });

        vSplitter = findViewById(R.id.v_splitter);
    }

    @Override
    public void set(@Nullable final MyAttentionObject item) {
        super.set(item);

        if (item != null) {
            if(!TextUtils.isEmpty(item.headimgurl)) {
                Picasso.with(getContext())
                        .load(item.headimgurl)
                        .error(R.drawable.user_default)
                        .placeholder(R.drawable.user_default)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.user_default);
            }
            tvName.setText(item.nickname);
            long timemillis = ((long)item.birthday) * 1000;
            String cons = DateUtils.getConstellation(timemillis);
            tvDesc.setText(cons + " " + item.city);

            if (item.role == 1) {
                ivV.setVisibility(VISIBLE);
            } else {
                ivV.setVisibility(GONE);
            }

            refreshAttentionStatus(item.followType);

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), UserHomeActivity.class);
                    intent.putExtra(URLDefine.UID,item.uid);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    private void refreshAttentionStatusIcon(@DrawableRes int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        drawable.setBounds(0, 0,
                bitmapDrawable.getBitmap().getWidth(),
                bitmapDrawable.getBitmap().getHeight());

        tvAttentionStatus.setCompoundDrawables(null, drawable, null, null);
    }

    private void refreshAttentionStatus(final int followType) {
        switch (followType) {
            case -1:
                tvAttentionStatus.setVisibility(View.GONE);
                break;

            case 0:
                tvAttentionStatus.setVisibility(View.VISIBLE);
                tvAttentionStatus.setText("+关注");
                refreshAttentionStatusIcon(R.drawable.attention_mine_icon);

                break;

            case 1: {
                tvAttentionStatus.setVisibility(View.VISIBLE);
                tvAttentionStatus.setText("已关注");
                refreshAttentionStatusIcon(R.drawable.alresdy_mine_icon);
                break;
            }

            case 2:
                tvAttentionStatus.setVisibility(View.VISIBLE);
                tvAttentionStatus.setText("互相关注");
                refreshAttentionStatusIcon(R.drawable.eachother_mine_icon);
                break;

            default:
                Log.e(MyAttentionItemView.class.getSimpleName(), "unknown follow type: " + followType);
                break;
        }
    }
}
