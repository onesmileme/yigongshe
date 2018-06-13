package com.weikan.app.news.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/1/8
 */
public class NewsViewFactory {
    /**
     * 支持的模板string和type的对照表
     */
    private static final HashMap<String, NewsItemType> newsTemplateMap = new HashMap<>();
    static {
        newsTemplateMap.put("news_none", NewsItemType.NEWS_NONE);
        newsTemplateMap.put("news_single", NewsItemType.NEWS_SINGLE);
        newsTemplateMap.put("news_multiple", NewsItemType.NEWS_MULTIPLE);
        newsTemplateMap.put("news_banner", NewsItemType.NEWS_BANNER);
        newsTemplateMap.put("pic_video", NewsItemType.NEWS_PIC_VIDEO);
        newsTemplateMap.put("wx_article", NewsItemType.NEWS_WX_ARTICLE);
        newsTemplateMap.put("multi_pub_article", NewsItemType.NEWS_MULTI_ARTICLE);
    }

    public static INewsView create(NewsItemType type, Context context) {

        INewsView v = null;
        switch (type) {
            case NEWS_NONE: v = new NewsNoneView(context); break;
            case NEWS_SINGLE: v = new NewsSingleView(context); break;
            case NEWS_MULTIPLE: v = new NewsMultipleView(context); break;
            case NEWS_BANNER: v = new BannerView(context); break;
            case NEWS_PIC_VIDEO: v = new NewsPicVideoView(context); break;
            case NEWS_WX_ARTICLE: v = new NewsWXArticleView(context); break;
            case NEWS_MULTI_ARTICLE: v = new NewsMultiPubArticleView(context); break;
            default:
                break;
        }
        return v;
    }

    public static NewsItemType mapViewType(@NonNull String s) {

        NewsItemType t = NewsItemType.NEWS_NONE;
        if(newsTemplateMap.containsKey(s)){
            t = newsTemplateMap.get(s);
        }
        return t;
    }

    public static Set<String> getSupportViewTypeSet() {
        return newsTemplateMap.keySet();
    }

    public static void setPictureInto(@NonNull ImageView iv,
                                      @Nullable OriginalItem.Pic pic) {
        String imageUrl = "";
        if (pic == null) {
            return;
        }
        imageUrl = pic.getImageUrl();

        int placeHolder = TemplateConfig.getConfig().getDrawablePlaceHolderNews();
        if (TextUtils.isEmpty(imageUrl)) {
            iv.setImageResource(placeHolder);
        } else {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(iv);
        }
    }

    public static void setPictureInto(@NonNull ImageView iv,
                                      @NonNull List<OriginalItem.Pic> imgs,
                                      int index) {
        String imageUrl = "";
        if (imgs.size() > index) {
            OriginalItem.Pic pic = imgs.get(index);
            if (pic != null) {
                imageUrl = pic.getImageUrl();
            }
        }

        int placeHolder = TemplateConfig.getConfig().getDrawablePlaceHolderNews();

        if (TextUtils.isEmpty(imageUrl)) {
            iv.setVisibility(View.INVISIBLE);
        } else {
            iv.setVisibility(View.VISIBLE);
            ImageLoaderUtil.updateImage(iv,imageUrl,placeHolder);
        }
    }
    public static void setPictureIntoForceVisible(@NonNull ImageView iv,
                                                  @NonNull List<OriginalItem.Pic> imgs,
                                                  int index) {
        String imageUrl = "";
        if (imgs.size() > index) {
            OriginalItem.Pic pic = imgs.get(index);
            if (pic != null) {
                imageUrl = pic.getImageUrl();
            }
        }

        int placeHolder = TemplateConfig.getConfig().getDrawablePlaceHolderNews();

        if (TextUtils.isEmpty(imageUrl)) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(placeHolder);
        } else {
            iv.setVisibility(View.VISIBLE);
            ImageLoaderUtil.updateImage(iv,imageUrl,placeHolder);
        }
    }

    public static void setBannerPhoto(ImageView iv, String imageUrl) {
        int placeHolder = TemplateConfig.getConfig().getDrawablePlaceHolderBanner();
        if (TextUtils.isEmpty(imageUrl)) {
            iv.setImageResource(placeHolder);
        } else {
            ImageLoaderUtil.updateImage(iv,imageUrl,placeHolder,placeHolder,null);
        }
    }

    private static int parseColor(@NonNull String s, int defaultValue) {
        int color = defaultValue;
        try {
            color = Color.parseColor(s);
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return color;
    }
}
