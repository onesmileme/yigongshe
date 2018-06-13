package com.weikan.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.original.HomeConst;
import com.weikan.app.original.OriginalItemFragment;

/**
 * Created by liujian on 16/5/26.
 */
public class MainFragment extends BaseFragment {
//    private TabPageIndicator indicator;
//    private TabPageIndicatorAdapter pagerAdapter;
    private View fragmentView;
//    private ImageView userBtn;
//    private ViewPager pager;
//    private ImageView userBtnRed;

//    private String[] tabNames = {"推荐", "最新", "文章", "图片", "视频", "十大", "看吧", "我的"};
//    private int[] tabType = {HomeConst.HOME_RECOMMAND, HomeConst.HOME_LATEST, HomeConst.HOME_ARTICLE, HomeConst.HOME_PIC, HomeConst.HOME_VIDEO,
//            HomeConst.HOME_TOP, HomeConst.HOME_BAR, HomeConst.HOME_MINE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != fragmentView) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (null != parent) {
                parent.removeView(fragmentView);
            }
        } else {
            fragmentView = inflater.inflate(R.layout.fragment_main, null);
            initView(fragmentView);// 控件初始化
        }
        return fragmentView;
    }

    private void initView(View view){
        TextView titleView = (TextView) view.findViewById(R.id.tv_titlebar_title);
        titleView.setText(R.string.app_name);
        ImageView backView = (ImageView) view.findViewById(R.id.iv_titlebar_back);
        backView.setVisibility(View.GONE);


        setStatusBarTransparent(view.findViewById(R.id.status_margin));

//        userBtn = (ImageView) view.findViewById(R.id.uv_titlebar_right);
//        userBtn.setVisibility(View.VISIBLE);
//        userBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), MineActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        userBtnRed = (ImageView) view.findViewById(R.id.iv_titlebar_right_red);

        view.findViewById(R.id.iv_titlebar_back).setVisibility(View.GONE);

        OriginalItemFragment fragment = new OriginalItemFragment();
        Bundle args = new Bundle();
                args.putInt(Constants.TAB_ID, HomeConst.HOME_LATEST);
                fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ll_main_place, fragment);
        transaction.commitAllowingStateLoss();

//        if (pagerAdapter == null) {
//            pagerAdapter = new TabPageIndicatorAdapter(getFragmentManager());
//        }
//        pager = (ViewPager)view.findViewById(R.id.vp_news_aggregation);
//        pager.setAdapter(pagerAdapter);
//
//        indicator = (TabPageIndicator)view.findViewById(R.id.tpi_news_aggregation);
//        indicator.setViewPager(pager);
//        indicator.setOnTabReselectedListener(new TabPageIndicator.OnTabReselectedListener() {
//            @Override
//            public void onTabReselected(int position) {
//                // 重新选中此项时做一次下拉刷新
//                ItemFragment fragment = (ItemFragment) pagerAdapter.getItem(position);
//                fragment.executeRefreshing();
//            }
//        });
//
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int arg0) {
//
//                // 选中此项时做一次下拉刷新
//                ItemFragment fragment = (ItemFragment) pagerAdapter.getItem(arg0);
//                fragment.requestIfNeed();
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//
//            }
//        });
    }

//    private void updateUserIcon(){
//        if (AccountManager.getInstance().isLogin()
//                && AccountManager.getInstance().getUserData() != null) {
//            ImageLoaderUtil.updateImageBetweenUrl(userBtn, AccountManager.getInstance().getUserData().headimgurl);
//        } else {
//            userBtn.setImageResource(R.drawable.user_button);
//            userBtn.setTag(null);
//        }
//        userBtnRed.setVisibility(View.GONE);
//        if(MineRedModel.redObject!=null && MineRedModel.redObject.data!=null){
//            if(MineRedModel.redObject.data.type_1>0
//                    || MineRedModel.redObject.data.type_2>0
//                    || MineRedModel.redObject.data.type_3>0){
//                userBtnRed.setVisibility(View.VISIBLE);
//            }
//        }
//    }


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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

//        EventBus.getDefault().register(this);
//        updateUserIcon();
    }

    @Override
    public void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

//    public void onEventMainThread(MineRedEvent event){
//        updateUserIcon();
//    }
//
//    public void onEventMainThread(AccountObtainEvent event){
//        updateUserIcon();
//    }

//    @SuppressWarnings("unused")
//    public void onEventMainThread(OriginalPublishEvent event) {
//        int index = -1;
//        for (int i = 0; i<tabType.length; i++) {
//            if (tabType[i] == HomeConst.HOME_PIC) {
//                index = i;
//                break;
//            }
//        }
//
//        Fragment[] fragments = pagerAdapter.fragments;
//        if (index != -1 && fragments.length > index) {
//            Fragment item = fragments[index];
//            if (item instanceof OriginalItemFragment) {
//                indicator.setCurrentItem(index);
//                ((OriginalItemFragment)item).executeRefreshing();
//            }
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface ItemFragment{
        void requestIfNeed();
        void executeRefreshing();
    }

//    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
//
//        Fragment[] fragments;
//
//        public TabPageIndicatorAdapter(FragmentManager fm) {
//            super(fm);
//
//            fragments = new Fragment[tabNames.length];
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            Fragment fragment = fragments[position];
//            if (fragment == null) {
//                if(tabType[position]==HomeConst.HOME_BAR){
//                    fragment = new KanBarFragment();
//                } else {
//                    fragment = new OriginalItemFragment();
//                }
//                fragments[position] = fragment;
//
//            }
//            if(!fragment.isAdded()) {
//                Bundle args = new Bundle();
//                args.putString(Constants.TAB_NAME, tabNames[position]);
//                args.putInt(Constants.TAB_ID, tabType[position]);
//                fragment.setArguments(args);
//            }
//            return fragment;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tabNames[position];
//        }
//
//        @Override
//        public int getCount() {
//            return tabNames.length;
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Fragment fragment = pagerAdapter.getItem(pager.getCurrentItem());
//        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
