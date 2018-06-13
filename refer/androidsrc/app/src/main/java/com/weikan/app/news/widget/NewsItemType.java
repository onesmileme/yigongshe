package com.weikan.app.news.widget;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/5/6
 */
public enum NewsItemType {
    EMPTY(0),
    NEWS_NONE(1),
    NEWS_SINGLE(2),
    NEWS_MULTIPLE(3),
    NEWS_BANNER(4),
    NEWS_PIC_VIDEO(5),
    NEWS_WX_ARTICLE(6),
    NEWS_MULTI_ARTICLE(7);

    int type;

    NewsItemType(int type) {
        this.type = type;
    }

    public int toInt() {
        return type;
    }

    public static int count() {
        return values().length;
    }
}
