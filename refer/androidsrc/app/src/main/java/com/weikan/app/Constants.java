package com.weikan.app;

/**
 * 共通常量类
 * 
 * @author Patrick.li
 * 
 */
public class Constants {

	public static final int commentMaxLength = 280;
	public static final int pubContentMaxLength = 500;

    public static final String WECHAT_APP_ID = BuildConfig.WECHAT_ID; // "wx3ffa01c54b13cb97", "wx4d52503b0ce117b6";

    public static final String WECHAT_SECRET_KEY = BuildConfig.WECHAT_KEY;
	public static final String WEIBO_APP_ID = BuildConfig.WEIBO_ID;
	public static final String WEIBO_SECRET_KEY = BuildConfig.WEIBO_KEY;
	public static final String QQ_APP_ID = BuildConfig.QQ_ID;
	public static final String QQ_APP_KEY = BuildConfig.QQ_KEY;

	public static final String WECHAT_PAY_APP_ID = "wxda06b3af66e3cf9b";

	public static final String WECHAT_PAY_SECRET_KEY = "ad1efe53d0293c605848b89e440b2fe5";

    public static final String CELLPHONE_PATTERN = "^[0-9]{11}$";

	// 列表进详情的req
	public static final int ARTICLE_DETAIL_REQUEST_CODE = 10001;


	// 详情进编辑的req
	public static final int ARTICLE_EDIT_REQUEST_CODE = 10002;


	// 进用户主页的req
	public static final int USERHOME_REQUEST_CODE = 10003;


	// 列表进搜索的req
	public static final int ARTICLE_SEARCH_REQUEST_CODE = 10004;


	// 屏幕高度
	public static int SCREEN_HEIGHT = 800;

	// 屏幕宽度
	public static int SCREEN_WIDTH = 480;

	// 屏幕密度
	public static float SCREEN_DENSITY = 1.5f;

	public static final String TAB_NAME = "tab_name";
	public static final String TAB_ID = "tab_id";

	public static final String ATTENTION_TYPE = "attention_type";

	public static final String OTHER_UID = "other_uid";

	public static final String OTHER_NICKNAME = "other_nickname";

	public static final String DETAIL_OBJECT = "detailobject";

	public static final String GROUP_DETAIL = "groupdetail";

	public static final String IS_EDIT = "is_edit";

	public static final String IS_DELETE = "is_del";

	public static final String BAR_DETAIL_ID = "bar_detail_id";


	public static final String SEARCH_KEY = "searchkey";

	public static final String SEARCH_FILTER = "filter";

	public static final String TITLE = "title";

	// sharepref 文件名
	public static final String PREF_COMMON_NAME = "pref_common";

	// 热门活动列表的pref key
	public static final String PREF_TOPIC = "topic";
}
