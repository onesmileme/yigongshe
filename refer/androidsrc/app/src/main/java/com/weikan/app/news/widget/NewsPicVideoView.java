package com.weikan.app.news.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.common.widget.IHasLayoutResource;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.PicObject;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.DynamicHeightSketchImageView;
import com.weikan.app.widget.roundedimageview.CircleImageView;

/**
 * Created by Lee on 2016/12/11.
 */
public class NewsPicVideoView extends BaseListItemView<OriginalItem> implements IHasLayoutResource, INewsView {

    @Bind(R.id.original_list_author_icon)
    CircleImageView authorIcon;
    @Bind(R.id.original_list_author_name)
    TextView authorName;
    @Bind(R.id.original_list_time)
    TextView time;
    @Bind(R.id.original_list_title)
    TextView title;
    @Bind(R.id.original_list_content_icon)
    DynamicHeightSketchImageView content;
    @Bind(R.id.iv_original_list_cover)
    ImageView cover;
    @Bind(R.id.original_list_brief_introduction)
    TextView introduction;


    public NewsPicVideoView(Context context) {
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

            ImageLoaderUtil.updateImageBetweenUrl(authorIcon, item.headimgurl, R.drawable.user_default);
            authorName.setText(item.author);

            FriendlyDate favDate = new FriendlyDate(item.pubtime * 1000L);
            time.setText(favDate.toFriendlyDate(false));
            if (TextUtils.isEmpty(item.title)) {
                title.setVisibility(GONE);
            } else {
                title.setText(item.title);
                title.setVisibility(VISIBLE);
            }

            if (item.pic != null) {
                OriginalItem.Pic picInfo = item.pic;
                PicObject pic = picInfo.t != null ? picInfo.t : picInfo.s;
                if (pic != null) {

                    float dv = (float) pic.h / (float) pic.w;
                    content.setHeightRatio(dv);
                    content.getOptions().setDecodeGifImage(true);
                    content.displayImage(pic.url);
                }
            }

            if (item.category == 3) {
                cover.setImageResource(R.drawable.icon_cover_video);
                cover.setVisibility(VISIBLE);
            } else if (item.category == 4) {
                cover.setImageResource(R.drawable.icon_cover_gif);
                cover.setVisibility(VISIBLE);
            } else {
                cover.setVisibility(GONE);
            }

            if (!TextUtils.isEmpty(item.contentAbstract)) {
                introduction.setVisibility(VISIBLE);
                introduction.setText(item.contentAbstract);
            } else {
                introduction.setVisibility(GONE);
            }
        }
    }

    @Override
    public int layoutResourceId() {
        return TemplateConfig.getConfig().getLayoutNewsPicVideo();
    }

    @Override
    public void setOnItemClickListener(OnClickListener listener) {
        setOnClickListener(listener);
    }
}
