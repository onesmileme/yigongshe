package com.weikan.app.news.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.common.widget.IHasLayoutResource;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.PicObject;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.DynamicHeightImageView;
import com.weikan.app.widget.DynamicHeightSketchImageView;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liujian on 16/12/12.
 */
public class NewsWXArticleView extends BaseListItemView<OriginalItem> implements IHasLayoutResource, INewsView {

    @Bind(R.id.original_list_author_icon)
    CircleImageView authorIcon;
    @Bind(R.id.original_list_author_name)
    TextView authorName;
    @Bind(R.id.original_list_time)
    TextView time;
    @Bind(R.id.original_list_title)
    TextView title;
    @Bind(R.id.original_list_brief_introduction)
    TextView content;
    @Bind(R.id.original_list_content_icon)
    DynamicHeightSketchImageView contentIconImageView;
    @Bind(R.id.original_list_read_num)
    TextView readNum;


    public NewsWXArticleView(Context context) {
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
            if (item.pic != null ){
                PicObject pic = item.pic.s != null ? item.pic.s : item.pic.t;
                if(pic != null) {

                    float dv = (float) pic.h / (float) pic.w;
                    contentIconImageView.setHeightRatio(dv);
                    ImageLoaderUtil.updateImage(contentIconImageView, pic.url, R.drawable.image_bg);
                }
            }
            setText(title, item.title);
            setText(content, item.contentAbstract);
            ImageLoaderUtil.updateImageBetweenUrl(authorIcon, item.headimgurl, R.drawable.user_default);

            setText(readNum, item.read_num+"");

            setText(authorName, item.oa_nick_name);
            setText(time, String.valueOf(getShowTime(item.pubtime)));
        }
    }

    @Override
    public int layoutResourceId() {
        return TemplateConfig.getConfig().getLayoutWxArticle();
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

    public String getShowTime (long time) {
//        FriendlyDate friendlyDate = new FriendlyDate(time * 1000);
//        return friendlyDate.toFriendlyDate(false);
        Date date = new Date(time * 1000);
        return new SimpleDateFormat("M月d日", Locale.getDefault()).format(date);
    }
}
