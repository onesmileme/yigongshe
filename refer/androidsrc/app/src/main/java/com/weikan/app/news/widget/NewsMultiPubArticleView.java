package com.weikan.app.news.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.common.widget.IHasLayoutResource;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.util.ImageLoaderUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liujian on 16/12/12.
 */
public class NewsMultiPubArticleView extends BaseListItemView<OriginalItem> implements IHasLayoutResource, INewsView {

    @Bind(R.id.iv_wemoney_head)
    ImageView authorIcon;
    @Bind(R.id.tv_wemoney_name)
    TextView authorName;
    @Bind(R.id.tv_wemoney_time)
    TextView time;
    @Bind(R.id.tv_wemoney_tite)
    TextView title;
    @Bind(R.id.tv_wemoney_des)
    TextView content;
    @Bind(R.id.iv_wemoney_photo)
    ImageView contentIconImageView;
    @Bind(R.id.tv_wemoney_yuedu)
    TextView readNum;


    public NewsMultiPubArticleView(Context context) {
        super(context);
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    public void set(@Nullable OriginalItem item) {
        super.set(item);
        if (item != null) {
            if (!TextUtils.isEmpty(item.headimgurl)) {
                ImageLoaderUtil.updateImage(authorIcon, item.headimgurl, R.drawable.user_default);
            } else {
                authorIcon.setImageResource(R.drawable.user_default);
            }
            if (!TextUtils.isEmpty(item.pic.s.url)) {
                ImageLoaderUtil.updateImage(contentIconImageView, item.pic.s.url, R.drawable.image_bg);
            } else {
                contentIconImageView.setImageResource(R.drawable.image_bg);
            }
            setText(readNum, Integer.toString(item.read_num));
            setText(title, item.title);
            setText(content, item.contentAbstract);
            setText(time, String.valueOf(getShowTime(item.pubtime)));
            setText(authorName, item.author);
        }
    }

    @Override
    public int layoutResourceId() {
        return TemplateConfig.getConfig().getLayoutMultiArticle();
    }

    @Override
    public void setOnItemClickListener(OnClickListener listener) {
        setOnClickListener(listener);
    }

    private void setText(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public String getShowTime(long time) {
        Date date = new Date(time * 1000);
        return new SimpleDateFormat("M月d日", Locale.getDefault()).format(date);
    }
}
