package com.weikan.app.live.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.live.bean.LiveEventObject;

/**
 * @author kailun on 16/8/31.
 */
public class LiveEventItemView extends BaseListItemView<LiveEventObject> {

    private static final String STR_SYSTEM = "[公告通知]";

    private TextView tvEvent;

    public LiveEventItemView(Context context) {
        super(context);
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_live_event_item;
    }

    @Override
    protected void initViews() {
        tvEvent = (TextView) findViewById(R.id.tv_event);
    }

    @Override
    public void set(@Nullable LiveEventObject item) {
        super.set(item);

        if (item != null) {
            if (item.type == LiveEventObject.TYPE_SYSTEM) {
                SpannableString text = new SpannableString(STR_SYSTEM + "：" + item.content);

                // "[系统通知]：" 这几个字显示为橙色
                ForegroundColorSpan fore = new ForegroundColorSpan(getResources().getColor(R.color.live_text_red));
                text.setSpan(fore, 0, STR_SYSTEM.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                tvEvent.setText(text);

            } else if (item.type == LiveEventObject.TYPE_USER) {

                SpannableString text = new SpannableString(item.nickname + "：" + item.content);

                String curUid = AccountManager.getInstance().getUserId();

                // "用户昵称：" 这几个字显示为橙色
                int color = TextUtils.equals(item.liveUid, item.uid) ? R.color.live_text_yellow: R.color.live_text_blue;
                ForegroundColorSpan fore = new ForegroundColorSpan(getResources().getColor(color));
                text.setSpan(fore, 0, item.nickname.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                tvEvent.setText(text);
            }
        }
    }
}
