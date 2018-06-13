package com.weikan.app.personalcenter;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.weikan.app.BuildConfig;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.bean.LoginEvent;
import com.weikan.app.account.bean.LogoutEvent;
import com.weikan.app.base.BaseFragment;
import de.greenrobot.event.EventBus;

/**
 * @author liujian on 16/7/26.
 */
public class MineContainerFragment extends BaseFragment {

    Fragment loginFragment;
    NewMineFragment mineFragment = new NewMineFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(BuildConfig.IS_PHONE_LOGIN_SUPPORT){
            loginFragment = new MineLoginWithPhoneFragment();
        } else {
            loginFragment = new MineLoginFragment();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content,null);

        setStatusBarTransparent(view.findViewById(R.id.status_margin));

        refreshFragment();

        EventBus.getDefault().register(this);

        return view;
    }


    /**
     * 由于MainActivity不能设置status bar，改为页面内设置status bar，透明全屏
     * @param view
     */
    public void setStatusBarTransparent(View view){
        if(Build.VERSION.SDK_INT>=19) {
            view.setVisibility(View.VISIBLE);
            int statusHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = statusHeight;
            view.setLayoutParams(lp);
        }
    }

    public void onEventMainThread(LoginEvent event){
        refreshFragment();
    }

    public void onEventMainThread(LogoutEvent event){
        refreshFragment();
    }

    void refreshFragment(){
        if(AccountManager.getInstance().isLogin()){
            replaceFragment(mineFragment);
        } else {
            replaceFragment(loginFragment);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}