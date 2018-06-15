package com.ygs.android.yigongshe;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import butterknife.BindView;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.community.CommunityFragment;
import com.ygs.android.yigongshe.ui.dynamic.DynamicFragment;
import com.ygs.android.yigongshe.utils.BottomNavigationViewHelper;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
  @BindView(R.id.viewpager) ViewPager viewPager;
  private MenuItem menuItem;
  @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

  @Override protected void initIntent() {

  }

  @Override protected void iniView() {
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.nav_dynamic:
                viewPager.setCurrentItem(0);
                break;
              case R.id.nav_activity:
                viewPager.setCurrentItem(1);
                break;
              case R.id.nav_community:
                viewPager.setCurrentItem(2);
                break;
              case R.id.nav_me:
                viewPager.setCurrentItem(3);
                break;
            }
            return false;
          }
        });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        if (menuItem != null) {
          menuItem.setChecked(false);
        } else {
          bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        menuItem = bottomNavigationView.getMenu().getItem(position);
        menuItem.setChecked(true);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });

    //禁止ViewPager滑动

    viewPager.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
      }
    });

    setupViewPager(viewPager);
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_main;
  }

  private void setupViewPager(ViewPager viewPager) {

    //把Fragment添加到List集合里面
    List list = new ArrayList<>();
    //        list.add(new LotteryFrag());
    list.add(new DynamicFragment());
    list.add(new DynamicFragment());
    list.add(new CommunityFragment());
    list.add(new DynamicFragment());
    TabFragmentPagerAdapter adapter =
        new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
    viewPager.setAdapter(adapter);
    viewPager.setCurrentItem(0);
  }
}
