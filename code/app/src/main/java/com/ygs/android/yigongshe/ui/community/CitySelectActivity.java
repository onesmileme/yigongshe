package com.ygs.android.yigongshe.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CityItemBean;
import com.ygs.android.yigongshe.bean.SortModel;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CityListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.utils.CharacterParser;
import com.ygs.android.yigongshe.utils.PinyinComparator;
import com.ygs.android.yigongshe.utils.PinyinUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.SideBar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/7.
 */

public class CitySelectActivity extends BaseActivity {
  @BindView(R.id.et_city_search) EditText mEtCitySearch;
  @BindView(R.id.lv_city_list) ListView mLvCityList;
  @BindView(R.id.sidebar) SideBar mSidebar;
  private CityAdapter adapter;
  private CharacterParser characterParser;
  private ArrayList<SortModel> sourceDateList;
  private PinyinComparator pinyinComparator;
  private LinkCall<BaseResultDataInfo<CityListResponse>> mCall;
  private int mId;//话题/城市
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  private String mString;//准备发起查询的字串

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("id");
  }

  @Override protected void initView() {
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
    initCityList();
    initData();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_city_select;
  }

  private void initCityList() {
    sourceDateList = new ArrayList<>();
    adapter = new CityAdapter(this, sourceDateList);
    mLvCityList.setAdapter(adapter);
    characterParser = CharacterParser.getInstance();
    pinyinComparator = new PinyinComparator();
    mSidebar.setOnLetterChangedListener(new SideBar.OnLetterChangedListener() {

      @Override public void onLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
          mLvCityList.setSelection(position);
        }
      }
    });

    mLvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String cityName = ((SortModel) adapter.getItem(position)).getName();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("key", cityName);
        bundle.putInt("id", mId);
        backForResult(BaseDetailActivity.class, bundle, 100);

        finish();
      }
    });
    mEtCitySearch.addTextChangedListener(new TextWatcher() {

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
      }

      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void afterTextChanged(Editable s) {
        mString = s.toString();
      }
    });
  }

  private void initData() {
    mCall = LinkCallHelper.getApiService().getCityList();
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CityListResponse data = entity.data;
          int count = data.city_list.size();
          for (int i = 0; i < count; i++) {
            List<CityItemBean> citys = data.city_list.get(i).citys;
            sourceDateList.addAll(fillListData(citys));
          }
          // 根据a-z进行排序源数据
          Collections.sort(sourceDateList, pinyinComparator);
          adapter.notifyDataSetChanged();
        }
      }
    });
  }

  /**
   * 为ListView填充数据
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

  /**
   * 根据输入框中的值来过滤数据
   *
   * @param filterStr 过滤的值
   */
  private void filterData(String filterStr) {
    ArrayList<SortModel> filterDateList = new ArrayList<>();
    if (TextUtils.isEmpty(filterStr)) {
      filterDateList = sourceDateList;
    } else {
      filterDateList.clear();
      for (SortModel sortModel : sourceDateList) {
        String name = sortModel.getName();
        if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
          filterDateList.add(sortModel);
        }
      }
    }
    // 排序
    Collections.sort(filterDateList, pinyinComparator);
    filterDateList.add(0, new SortModel("全部", "a"));
    adapter.updateListView(filterDateList);
  }

  protected boolean openTranslucentStatus() {
    return true;
  }

  @OnClick(R.id.search) public void onBtnClicked() {
    filterData(mString);
  }
}
