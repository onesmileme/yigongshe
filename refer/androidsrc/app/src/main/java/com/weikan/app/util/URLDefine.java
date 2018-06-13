package com.weikan.app.util;

/**
 * Created with IntelliJ IDEA. User: liujian06 Date: 2015/1/25 Time: 19:09
 */
public class URLDefine {

    public static final String SCHEME = "http";

    public static final String TYPE = "type";

    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String GROUP_ID = "group_id";
    public static final String PAGE = "pn";
    public static final String LIVE_ID = "liveid";

    public static final String FIRST_TID = "first_tid";

    public static final String LAST_TID = "last_tid";

    public static final String FIRST_MID = "first_mid";

    public static final String LAST_MID = "last_mid";

    public static final String FIRST_ID = "first_id";
    public static final String LAST_TIME = "last_time";
    public static final String LAST_ID = "last_id";

    public static final String LAST_CID = "last_cid";

    public static final String PN = "pn";

    public static final String RN = "rn";

    public static final String TID = "tid";

    public static final String PARENT_TID = "parent_tid";

    public static final String UID = "uid";

    public static final String UNAME = "uname";

    public static final String CID = "cid";

    public static final String XG_TOKEN = "xg_device_token";

    public static final String JPUSH_TOKEN = "jpush_device_token";

    public static final String DEVICE_TYPE = "device_type";

    public static final String PUSH_SRC = "push_src";

    // 推送同步
    public static final String INDEX_TIME = "indextime";
    public static final String LIMIT_TIME = "limittime";
    public static final String RANK_TIME = "ranktime";

    /**
     * 聊天对象的id
     */
    public static final String SEARCH_UID = "search_uid";

    public static final String TO_UID = "to_uid";

    /**
     * 发表行业
     */
    public static final String CATALOG = "catalog";

    /**
     * 发表分类
     */
    public static final String INDUSTRY = "industry";

    /**
     * 发表内容
     */
    public static final String CONTENT = "content";

    /**
     * 聊天对象的id
     */
    public static final String REPLY_CID = "reply_cid";

    /**
     * 聊天对象的id
     */
    public static final String REPLY_UID = "reply_uid";

    /**
     * 聊天对象的id
     */
    public static final String SNAME = "sname";

    /**
     * 聊天对象的id
     */
    public static final String OWNERID = "ownerid";

    /**
     * 聊天对象的id
     */
    public static final String ONAME = "oname";

    /**
     * 获取对方的uid
     */
    public static final String OTHER_UID = "other_uid";

    /**
     * 获取对方的uid
     */
    public static final String FOLLOWER_UID = "follower_uid";

    public static final String TOKEN = "token";

    public static final String BW_TOKEN = "token";

    public static final String URL = "url";

    public static final String AVATAR = "avatar";

    public static final String CONFIG = "config";

    public static final String SHARE_POPUP = "share_popup";

    /**
     * 推送绑定地址
     */
    public static final String PUSH_BIND_PATH = "/push/bind";

    /**
     * 获取app配置地址
     */
    public static final String CONFIG_APP = "/config/app";
    /**
     * 获取app用户协议
     */
    public static final String CONFIG_PROTO = "/config/protocol";
    /**
     * 赞赏支付订单预生成接口
     */
    public static final String PAY_UNIFIEDORDER = "/pay/unifiedorder";

    public final static String SHARE_URL = "http://api.tigerinksy.com";



    public static final String HUN_WATER_HOST_API_TEST = "123.57.30.63:8041"; // 测试环境
//    public static final String HUN_WATER_HOST_API_TEST = "123.57.30.63:20083"; // 测试环境
    public static final String HUN_WATER_HOST_API_ONLINE = "api.baithu.com";  // 线上环境
    public static String HUN_WATER_HOST_API = HUN_WATER_HOST_API_ONLINE;

//    public static final String HUN_WATER_HOST_API = "123.57.30.63:20083";
//    public static final String HUN_WATER_HOST_API = "apps.hunwater.com";

    public final static String USER_OALIST = "/user/oalist";
    public final static String USER_INFO = "/user/userinfo";
    public final static String EDIT_USER_INFO = "/user/useredit";
    public final static String ADMIRE_LIST = "/admire/mylist";
    public final static String SELECT_OA = "/user/select_oa";
    public final static String UPLOAD_USER_PIC = "/upload/user_pic";
    public final static String ATTENTION_LIST = "/relation/followee_list";
    public final static String FOLLOWER_LIST = "/relation/follower_list";
    public final static String MSG_CMTLIST = "/system_message/cmtlist";
    public final static String MSG_ZANLIST = "/system_message/zanlist";
    public final static String MSG_SYSLIST = "/system_message/syslist";
    public final static String SYNC_INFO = "/original/sync";
    public final static String TWEET_TAGLIST = "/tweet/taglist";
    public final static String TWEET_USER_TOP = "/tweet/usertop";
    public final static String TWEET_INNO_LIST = "/tweet/innolist";

    public final static String UPLOAD_TWEET_PIC = "/upload/tweet_pic";

    public final static String USER_FEEDBACK = "/setting/leave_msg";
    public final static String USER_HOME = "/user/home";
    public final static String USER_EDIT = "/user/useredit";
    public final static String USER_REL_LIST = "/user/rel_list";
    public final static String USER_ADD_FOLLOW = "/user/add_follow";
    public final static String USER_CANCEL_FOLLOW = "/user/cancel_follow";
    public final static String LIVE_LISTS = "/live/lists";
    public final static String LIVE_OPEN = "/live/open";
    public final static String LIVE_ONLINE_USERS = "/live/online_users";
    public final static String LIVE_SHOW_GIFT = "/gift/show_gift";
    public final static String LIVE_EVENT_LIST= "/live/event_list";
    public static final String LIVE_SAY = "/live/say";
    public static final String LIVE_DETAIL = "/live/detail";
    public static final String LIVE_CLOSE = "/live/close";
    public static final String DELETE_LIVE = "/live/del_live";
    public final static String MATERIAL_LISTS = "/material/lists";
    public final static String MONEY_USERMONEY = "/money/user_money";
    public final static String USER_VERIFY_SUBMIT = "user_verify/submit";
    public final static String USER_SELF_LOGIN = "/user/self_login";
    public final static String USER_MOBILE_REGISTER = "/user/mobile_register";
    public final static String USER_FORGET_PASSWORD = "/user/forget_password";

    public final static String GIFTS_LISTS = "/gift/lists";
    public final static String GIVE_GIFT = "/gift/give";
    // 获取标签列表
    public final static String TAG_LIST = "/tweet/tag";


    /**
     * 小红点同步接口
     */
    public final static String MSG_NUM = "/info/msgnum";


    public final static String OFC_ACCOUNTS = "ofc_accounts";

    /**
     * 登录注册
     *
     * USER_APP_LOGIN - 登录
     * SMS_APP_SEND - 请求验证码
     * SMS_VERIFY - 校验验证码
     * USER_REWRITE_PASS - 修改密码
     * INVITATION_CHECKINVITATIONCODE - 校验邀请码
     * USER_REGISTER - 注册
     */
    public static final String USER_LOGIN = "/user/login";
    public static final String USERINFO_USER_INFO = "/user/userinfo";
    public static final String USER_APP_LOGIN = "/user/app_login";
    public static final String SMS_APP_SEND = "sms/app_send";
    public static final String SMS_VERIFY = "/sms/verify";
    public static final String USER_REWRITE_PASS = "/user/rewrite_pass";
    public static final String INVITATION_CHECKINVITATIONCODE = "/invitation/checkinvitationcode";
    public static final String USER_REGISTER = "/user/register";

    // 绑定手机号
    public static final String USER_BIND = "/user/bind";
    // 发送验证码
    public static final String SMS_SEND = "/sms/send";

    // 原创部分
    public static final String ORIGINAL_LIST = "tweet/recommend";
    public static final String ORIGINAL_DETAIL = "/tweet/detail";
    public static final String ORIGINAL_BANNER = "/tweet/banner";
    public static final String TWEET_MATERIAL = "/tweet/material";
    public static final String TWEET_TOP = "tweet/top";
    public static final String TWEET_USERLIST = "tweet/userlist";
    public static final String TWEET_EDIT = "tweet/tweetedit";
    public static final String TWEET_REMOVE = "tweet/remove";
    public static final String TWEET_SEARCH = "tweet/search";
    public static final String TWEET_TWEET_CONTENT = "/tweet/tweet_content";
    public static final String TWEET_INNO_PUB = "/tweet/innopub";
    public static final String WENYOU_LIST = "/moments/lists_all";
    public static final String WENYOU_PUB = "/moments/tweetpub";
    public static final String WENYOU_DETAIL = "/moments/detail";

    public static final String MINE_LIST = "/moments/my_lists";
    public static final String DEL_LIST = "/moments/remove";
    public static final String DEL_COMMENT = "/comment/delcmt";
    public static final String USER_MOMENTS = "/user/moments";

    public static final String DEL_ORIGINAL_COMMENT = "/comment/delcmt";

    //分享到文友圈
    public static final String SHARE_ARTICLE_MOMENT = "/moments/share_article_moment";

    // 多tab模板的list数据
    public static final String MULTI_TWEET_ARTICLE_LIST = "/tweet/news_article_list";

    // 多tab模板的tab数据
    public static final String MULTI_TWEET_CATEGORYS = "/category/get_list";

    // 获取热门话题列表
    public static final String TOPIC_LIST = "/topic/lists";
    // 获取话题帖子列表
    public static final String MOMENTS_TOPIC_LIST = "/moments/topic";
    // 获取文友圈banner
    public static final String BANNER_LISTS = "/banner/lists";

    // 我的消息列表
    public final static String MY_MSG_LIST = "/system_message/syslist";
    // 我的收藏
    public final static String MY_COLLECTION = "/collection/lists";
    public final static String MY_MSG_NOTIFY = "/system_message/new_msg_num";
    // 我的对话列表
    public final static String MY_TALK_LIST = "/message/usermsg";

    // 对话详情
    public final static String TALK_DETAIL = "/message/talk";

    // 发送对话
    public final static String SEND_TALK = "/message/newmsg";

    // 点赞
    public static final String ZAN_ADD = "/zan/add";
    public static final String ZAN_CANCEL = "/zan/cancel";

    // 评论
    public static final String COMMENT_LIST = "/comment/getArticleCommentList";
    // 添加收藏
    public static final String COLLECTION_ADD = "/collection/add";
    // 删除收藏
    public static final String COLLECTION_CANCLE = "/collection/cancel";
    public static final String COMMENT_PUBLISH = "/comment/publish";
    public static final String ZAN_ADDARTICLE = "/zan/addArticle";
    // 关注
    public static final String RELATION_FOLLOW = "/relation/follow";
    public static final String RELATION_UNFOLLOW = "/relation/unfollow";

    // 红点
    public static final String RED_INFO = "/info/msgnum";

    // 设置兴趣标签
    public final static String USER_INTREST = "/user/interest";
    // 获取兴趣标签
    public static final String GET_INTEREST = "/user/userInterest";
    // 上传openid绑定对应数据
    public static final String GET_USER_OPENID_BIND = "/user/userel";
    // share
    public static final String SHARE_LOG = "/share/add";

    // 看吧列表页
    public static final String BAR_SHARE = "/channel/tweetpub";

    // 创建看吧
    public static final String BAR_ADD = "/channel/add";

    // 修改看吧
    public static final String BAR_EDIT = "/channel/edit";

    // 看吧详情页
    public static final String BAR_DETAIL = "/channel/detail";

    // 看吧加用户
    public static final String BAR_ADD_USER = "/channel/user_add";

    // 看吧用户列表
    public static final String BAR_USER_LIST = "/channel/member_list";

    // 分享到看吧
    public static final String BAR_INDEX = "/channel/index";


    // 获取第三方已绑定状态
    public static final String IS_ACCOUNT_BIND = "/user/is_account_bind";

    // 绑定其他第三方账号
    public static final String ACCOUNT_MERGE = "/user/account_merge";


    // 添加收藏
    public static final String FAV_ADD = "/collection/add";
    // 取消收藏
    public static final String FAV_DEL = "/collection/cancel";
    // 收藏列表
    public static final String FAV_LIST = "/collection/lists";

    // 添加不喜欢
    public static final String DISLIKE_ADD = "/dislike/add";
    // 取消不喜欢
    public static final String DISLIKE_DEL = "/dislike/cancel";
    // 群组详情页
    public static final String GROUP_DETAIL = "/topic_group/main_page";
    // 群组关注
    public static final String GROUP_FOLLOW = "/topic_group/follow";
    // 群组取消关注
    public static final String CANCEL_GROUP_FOLLOW = "/topic_group/unfollow";
    // 群组文章列表
    public static final String GROUP_ARTICLE_LIST = "/topic_group/article_list";
    // 用户关注群组列表
    public static final String USER_FOLLOW_GROUPS = "/topic_group/user_followed_groups";
    // 发帖选择群组列表
    public static final String PUB_ARTICLE_GROUPS = "/topic_group/pub_article_groups";
    // 热门群组
    public static final String HOT_GROUPS = "/topic_group/hot_groups";
    // 全部群组
    public static final String ALL_GROUPS_LIST = "/topic_group/lists";
    /**
     * 长连接路径
     */
    public static String LONG_LINK_PATH = "/conn_info/get";
}
