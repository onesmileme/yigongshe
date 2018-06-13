package com.weikan.app.search.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.search.bean.SearchContentListObject;
import com.weikan.app.search.bean.SearchUserListObject;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.roundedimageview.CircleImageView;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/30
 */
public class SearchContentItemView extends FrameLayout {

    TextView tvContent;
    ImageView ivPic;

    @NonNull String keyword = "";
    @Nullable SearchContentListObject.Content item;

    public SearchContentItemView(Context context) {
        super(context);
        initViews(context);
    }

    public SearchContentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public SearchContentItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_item_search_content, this);
        tvContent = (TextView) findViewById(R.id.tv_content);
        ivPic = (ImageView) findViewById(R.id.iv_pic);
    }

    public void setKeyword(@NonNull String keyword) {
        this.keyword = keyword;
    }

    public void setItem(@NonNull SearchContentListObject.Content item) {
        this.item = item;
        SpannableStringBuilder builder = builderSpannableString(item.title, keyword);
        tvContent.setText(builder);
        ImageLoaderUtil.updateImage(ivPic, item.pic.s.url);
    }

    @Nullable
    public SearchContentListObject.Content getItem() {
        return this.item;
    }

    private static SpannableStringBuilder builderSpannableString(@NonNull String content, @NonNull String keyword) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);

        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#ffbf40"));

        if (!TextUtils.isEmpty(keyword)) {
            int start = 0;
            while (start < content.length()) {
                int i = content.indexOf(keyword, start);
                if (i != -1) {
                    builder.setSpan(redSpan, i, i + keyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    start = i + keyword.length();
                } else {
                    start = content.length();
                }
            }
        }
        return builder;
    }
}
