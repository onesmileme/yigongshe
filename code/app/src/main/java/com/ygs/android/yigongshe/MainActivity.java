package com.ygs.android.yigongshe;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.CityItemBean;
import com.ygs.android.yigongshe.bean.LocationEvent;
import com.ygs.android.yigongshe.bean.SortModel;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CityListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.activity.ActivityFragment;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.community.CommunityFragment;
import com.ygs.android.yigongshe.ui.dynamic.DynamicFragment;
import com.ygs.android.yigongshe.ui.fragment.MeFragment;
import com.ygs.android.yigongshe.ui.login.LoginActivity;
import com.ygs.android.yigongshe.utils.BottomNavigationViewHelper;
import com.ygs.android.yigongshe.utils.LocationService;
import com.ygs.android.yigongshe.utils.PinyinComparator;
import com.ygs.android.yigongshe.utils.PinyinUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
  @BindView(R.id.viewpager) ViewPager viewPager;
  private MenuItem menuItem;
  @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
  private LocationService locationService;
  private LinkCall<BaseResultDataInfo<CityListResponse>> mCityListCall;
  private ArrayList<SortModel> sourceDateList;
  private PinyinComparator pinyinComparator;
  private final int BAIDU_READ_PHONE_STATE = 0;

  @Override protected void initIntent(Bundle bundle) {

  }

  @Override protected void initView() {
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
    list.add(new ActivityFragment());
    list.add(new CommunityFragment());
    //list.add(new DynamicFragment());
    list.add(new MeFragment());
    TabFragmentPagerAdapter adapter =
        new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
    viewPager.setAdapter(adapter);
    viewPager.setCurrentItem(0);
    viewPager.setOffscreenPageLimit(4);
  }

  @Override protected void onStart() {
    super.onStart();
    if (Build.VERSION.SDK_INT >= 23) {
      showContacts();
    } else {
      initLocationService();
    }

    //注册监听
    AccountManager accountManager = YGApplication.accountManager;
    if (accountManager.getToken() == null) {
      showLogin();
    }
  }

  @Override protected void onStop() {
    locationService.unregisterListener(mLocationListener); //注销掉监听
    locationService.stop(); //停止定位服务
    super.onStop();
  }

  private void initLocationService() {
    locationService = ((YGApplication) getApplication()).locationService;
    //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
    locationService.registerListener(mLocationListener);
    locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    locationService.start();// 定位SDK
  }

  @TargetApi(Build.VERSION_CODES.M) private void showContacts() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {
      //判断是否需要向用户解释为什么需要申请该权限
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.ACCESS_COARSE_LOCATION)) {
        Toast.makeText(MainActivity.this, "自Android 6.0开始需要打开位置权限", Toast.LENGTH_SHORT).show();
      }
      // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
      ActivityCompat.requestPermissions(MainActivity.this, new String[] {
          Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.READ_PHONE_STATE
      }, BAIDU_READ_PHONE_STATE);
    } else {
      initLocationService();
    }
  }

  private void showLogin() {

    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }

  //for different fragments
  protected boolean openTranslucentStatus() {
    return true;
  }

  private String mCityName = "全国";
  private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
    @Override public void onReceiveLocation(BDLocation bdLocation) {
      if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
        mCityName = bdLocation.getCity();
        initCityListData();
      }
    }
  };

  private void initCityListData() {
    mCityListCall = LinkCallHelper.getApiService().getCityList();
    mCityListCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          sourceDateList = new ArrayList<>();
          CityListResponse data = entity.data;
          int count = data.city_list.size();
          for (int i = 0; i < count; i++) {
            List<CityItemBean> citys = data.city_list.get(i).citys;
            sourceDateList.addAll(fillListData(citys));
          }
          // 根据a-z进行排序源数据
          pinyinComparator = new PinyinComparator();
          Collections.sort(sourceDateList, pinyinComparator);
          Iterator iterator = sourceDateList.iterator();
          while (iterator.hasNext()) {
            SortModel sortModel = (SortModel) iterator.next();
            if (TextUtils.isEmpty(mCityName)) {
              mCityName = "全国";
              EventBus.getDefault().post(new LocationEvent(mCityName));
              return;
            } else if (mCityName.equals(sortModel.getName())) {
              EventBus.getDefault().post(new LocationEvent(mCityName));
              return;
            }
          }

          //放到文件中
        }
      }
    });
  }

  /**
   * 为List填充数据
   */
  private ArrayList<SortModel> fillListData(List<CityItemBean> cityList) {
    ArrayList<SortModel> mSortList = new ArrayList<>();
    for (int i = 0; i < cityList.size(); i++) {
      CityItemBean result = cityList.get(i);
      if (result != null) {
        SortModel sortModel = new SortModel();
        String cityName = result.citysName;
        //汉字转换成拼音
        String pinyin = PinyinUtils.getPinYin(cityName);
        if (!TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(pinyin)) {
          sortModel.setName(cityName);
          String sortString = pinyin.substring(0, 1).toUpperCase();
          // 正则表达式，判断首字母是否是英文字母
          if (sortString.matches("[A-Z]")) {
            sortModel.setSortLetters(sortString.toUpperCase());
          } else {
            sortModel.setSortLetters("#");
          }
          mSortList.add(sortModel);
        }
      }
    }
    return mSortList;
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      // requestCode即所声明的权限获取码，在checkSelfPermission时传入
      case BAIDU_READ_PHONE_STATE:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
          initLocationService();
        } else {
          // 没有获取到权限，做特殊处理
          Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }
  }
}
