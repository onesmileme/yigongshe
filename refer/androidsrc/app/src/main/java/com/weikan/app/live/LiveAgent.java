package com.weikan.app.live;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.weikan.app.account.AccountManager;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.common.net.CommonAgent;
import com.weikan.app.common.net.JsonArrayResponseHandler;
import com.weikan.app.common.net.Page;
import com.weikan.app.live.bean.GiftObject;
import com.weikan.app.live.bean.LiveDetailDataObject;
import com.weikan.app.live.bean.LiveEventObject;
import com.weikan.app.live.bean.LiveListDataObject;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.live.bean.NewLiveObject;
import com.weikan.app.live.bean.OnlineUserListDataObject;
import com.weikan.app.live.bean.OnlineUserObject;
import com.weikan.app.util.URLDefine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import platform.http.HttpClient;
import platform.http.HttpUtils;
import platform.http.responsehandler.AbstractJsonResponseHandler;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weikan.app.common.net.CommonAgent.makeUrl;

/**
 * @author kailun on 16/8/27.
 */
public class LiveAgent {

    /**
     * 取得直播的列表
     *
     * @param page       请求方式
     * @param lastLiveId 最后一个直播的id
     * @param handler    handler
     */
    public static void getList(@NonNull final Page page,
                               final long lastLiveId,
                               @NonNull final JsonResponseHandler<LiveListDataObject> handler) {
        String url = makeUrl(URLDefine.LIVE_LISTS);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.TYPE, page.toPageType());
        params.put("last_live_id", lastLiveId + "");

        HttpUtils.get(url, params, handler);
    }

    /**
     * 创建一个新的直播
     *
     * @param uid     uid
     * @param title   title
     * @param handler handler
     */
    public static void postNew(@NonNull final String uid,
                               @NonNull final String title,
                               @NonNull final JsonResponseHandler<NewLiveObject> handler) {
        String url = makeUrl(URLDefine.LIVE_OPEN);

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("title", title);

        HttpUtils.urlEncodedPost(url, params, handler);
    }

    /**
     * 取得在线用户列表
     * @param liveId liveId
     * @param handler handler
     */
    public static void getOnlineUsers(final long liveId,
                                      @NonNull final JsonResponseHandler<OnlineUserListDataObject> handler) {
        String url = makeUrl(URLDefine.LIVE_ONLINE_USERS);

        Map<String, String> params = Collections.singletonMap("live_id", liveId + "");
        HttpUtils.urlEncodedPost(url, params, handler);
    }

    /**
     * 获取直播主接受礼物列表
     * @param liveId
     * @param lasttime
     */
    public static void getGiftList(final long liveId
                                      ,final long lasttime ,@NonNull final JsonArrayResponseHandler<GiftObject> handler) {
        String url = makeUrl(URLDefine.LIVE_SHOW_GIFT);

        Map<String, String> params = new HashMap<>();
        params.put("live_id", Long.toString(liveId));
        params.put("lasttime",Long.toString(lasttime));
        HttpUtils.urlEncodedPost(url, params, handler);
    }
    /**
     * 取得直播的消息列表
     * @param liveId liveId
     * @param handler handler
     */
    public static void getEvents(final long liveId,
                                    @NonNull final Page page,
                                    final long lastTime,
                                    @NonNull final JsonArrayResponseHandler<LiveEventObject> handler) {
        String url = makeUrl(URLDefine.LIVE_EVENT_LIST);

        Map<String, String> params = new HashMap<>();
        params.put("live_id", liveId + "");
        params.put("type", page.toPageType());
        params.put("lasttime", lastTime + "");
        HttpUtils.get(url, params, handler);
    }

    /**
     * 在直播中发布一个新的事件，一般是用户评论之类的
     * @param uid uid
     * @param liveId liveId
     * @param content content
     * @param handler handler
     */
    public static void postNewEvent(@NonNull final String uid,
                                    final long liveId,
                                    @NonNull final String content,
                                    @NonNull final SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.LIVE_SAY);

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("live_id", liveId + "");
        params.put("content", content);

        HttpUtils.urlEncodedPost(url, params, handler);
    }

    /**
     * 取得直播native播放详情
     * @param liveId liveId
     * @param handler handler
     */
    public static void getLiveNativeDetail(final long liveId,
                                 @NonNull final AmbJsonResponseHandler<LiveListObject> handler) {
        String url = makeUrl(URLDefine.LIVE_DETAIL);

        Map<String, String> params = new HashMap<>();
        params.put("live_id", liveId + "");
        params.put("uid", AccountManager.getInstance().getUserId());
        HttpUtils.get(url, params, handler);
    }

    /**
     * 退出时关闭直播
     * @param streamId 直播流的Id
     * @param handler handler
     */
    public static void postClose(@NonNull final String streamId,
                                 @Nullable final SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.LIVE_CLOSE);

        Map<String, String> params = new HashMap<>();
        params.put("streamid", streamId);

        HttpUtils.urlEncodedPost(url, params, handler);
    }
    /**
     * 删除视频
     * @param liveId 直播流的Id
     * @param handler handler
     */
    public static void deleteLive(final long liveId,
                                  @NonNull final String uid,
                                  @Nullable final SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.DELETE_LIVE);

        Map<String, String> params = new HashMap<>();
        params.put("live_id", Long.toString(liveId));
        params.put("uid",uid);
        HttpUtils.urlEncodedPost(url, params, handler);
    }
    /**
     * 获取礼物列表
     * @param handler handler
     */
    public static void giftList(
                                  @Nullable final JsonArrayResponseHandler handler) {
        String url = makeUrl(URLDefine.GIFTS_LISTS);

        Map<String, String> params = new HashMap<>();
        params.put("uid", AccountManager.getInstance().getUserId());
        HttpUtils.urlEncodedPost(url, params, handler);
    }
    /**
     * 发送礼物
     * @param handler handler
     */
    public static void giveGift(final String giftId,final String toUid,long liveId,
            @Nullable final SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.GIVE_GIFT);
        Map<String, String> params = new HashMap<>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("to_uid", toUid);
        params.put("gift_id", giftId);
        params.put("live_id", Long.toString(liveId));
        HttpUtils.get(url, params, handler);
    }
}
