package com.weikan.app.news.bean;

import android.text.TextUtils;

import com.weikan.app.original.bean.OriginalItem;

/**
 * @author kailun on 16/11/22.
 */
public class BannerContent {

    public String title = "";
    public String tid = "";
    public String imgUrl = "";
    public String schema = "";

    public static BannerContent from(OriginalItem.BannerContent bannerContent) {
        BannerContent c = new BannerContent();
        c.tid = bannerContent.tid;
        c.title = bannerContent.title;
        c.schema = bannerContent.schema;
        if (bannerContent.imgs.size() != 0) {
            OriginalItem.Pic pic = bannerContent.imgs.get(0);
            if (pic != null) {
                if (pic.s != null && !TextUtils.isEmpty(pic.s.url)) {
                    c.imgUrl = pic.s.url;
                } else if (pic.t != null && !TextUtils.isEmpty(pic.t.url)) {
                    c.imgUrl = pic.t.url;
                }
            }
        }
        return c;
    }
}
