package com.weikan.app.base;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.weikan.app.AppManager;
import com.weikan.app.Constants;
import com.weikan.app.LoadingDialog;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.ShareTools;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/2/7
 * Time: 11:58
 */
public class BaseActivity extends SwipeBackActivity {
    static boolean isinit = false;

    public static final String TYPE_NEW = "new";
    public static final String TYPE_APPEND = "append";
    public static final String TYPE_NEXT = "next";

    private DialogFragment loadingDialog;

//    protected UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.addActivity(this, isSupportSwipBack());
        if (!isSupportSwipBack()) {
            setSwipeBackEnable(false);
        }

        if(!isinit) {
            // 初始化图片库
            ImageLoaderUtil.initImageLoaderUtil(this);

            AccountManager.getInstance().init(this);

            ShareTools.getInstance().init(this);


            Global.getInstance().initScreenParam(this);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Constants.SCREEN_DENSITY = metrics.density;
            Constants.SCREEN_HEIGHT = metrics.heightPixels;
            Constants.SCREEN_WIDTH = metrics.widthPixels;
            isinit = true;
        }
//        if (openTranslucentStatus()) {
//            setTranslucentStatus(true);
//        }
    }

    /**
     * 是否支持左滑返回效果
     * @return
     */
    public boolean isSupportSwipBack(){
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (openTranslucentStatus()) {
            setTranslucentStatus(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);// 必须要调用这句
        AppManager.finishActivity(this);
    }

    protected boolean openTranslucentStatus(){
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String nameString = this.getClass().getSimpleName();
        MobclickAgent.onPageEnd(nameString); // 保证 onPageEnd 在onPause
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String nameString = this.getClass().getSimpleName();
        MobclickAgent.onPageStart(nameString); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.finishActivity(this);
    }


    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance();
        }

        if (loadingDialog.isAdded()) {
            return;
        }

        loadingDialog.show(getFragmentManager(), "");
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.add(loadingDialog, null);
//        transaction.commit();
    }

    public void hideLoadingDialog() {
        if (loadingDialog == null) {
            return;
        }

        loadingDialog.dismissAllowingStateLoss();
    }

    /**
     * 设置状态栏背景状态
     */
    protected void setTranslucentStatus(boolean on)
    {
        if(Build.VERSION.SDK_INT>=19) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
            //创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            //激活状态栏设置
            tintManager.setStatusBarTintEnabled(on);
            //设置颜色
            if(on) {
                tintManager.setStatusBarTintResource(R.color.news_titlebar_bg);
            } else {
                tintManager.setStatusBarTintResource(R.color.transparent);
            }
        }
    }
}
