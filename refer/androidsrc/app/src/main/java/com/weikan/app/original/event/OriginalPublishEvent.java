package com.weikan.app.original.event;

import android.support.annotation.NonNull;
import com.weikan.app.original.bean.OriginalDetailItem;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/14
 */
public class OriginalPublishEvent {
    public final String tid;
    public OriginalPublishEvent(@NonNull String tid) {
        this.tid = tid;
    }
}
