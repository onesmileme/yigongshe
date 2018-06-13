package com.weikan.app.common.net;

/**
 * @author kailun on 16/8/27.
 */
public enum Page {
    NEW("new"),
    NEXT("next");

    private String pageType;
    Page(String pageType) {
        this.pageType = pageType;
    }

    public String toPageType() {
        return pageType;
    }
}
