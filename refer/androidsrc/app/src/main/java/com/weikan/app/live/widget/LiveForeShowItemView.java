package com.weikan.app.live.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.DynamicHeightImageView;

/**
 * Created by liujian on 16/9/8.
 */
public class LiveForeShowItemView extends BaseListItemView<LiveListObject> {

    private TextView tvtitle;
    private TextView tvSubTitle;

    private DynamicHeightImageView ivBg;

    private LinearLayout llTags;

    public LiveForeShowItemView(Context context) {
        super(context);
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_live_list_pending_item_view;
    }

    @Override
    protected void initViews() {
        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        tvtitle = (TextView) findViewById(R.id.tv_title);

        ivBg = (DynamicHeightImageView) findViewById(R.id.iv_bg);
        ivBg.setHeightRatio(0.29);

        llTags = (LinearLayout) findViewById(R.id.ll_tags);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void set(@Nullable LiveListObject item) {
        super.set(item);

        if (item != null) {

            tvtitle.setText(item.title);

            tvSubTitle.setText("");

            if (!TextUtils.isEmpty(item.cover)) {
                ImageLoaderUtil.updateImage(ivBg, item.cover);
            } else {
                ivBg.setImageDrawable(getResources().getDrawable(R.drawable.mine_bg));
            }

        }
    }
}
