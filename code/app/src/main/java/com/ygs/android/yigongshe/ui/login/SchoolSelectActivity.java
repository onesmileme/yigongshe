package com.ygs.android.yigongshe.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.SortModel;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.ui.community.CityAdapter;
import com.ygs.android.yigongshe.utils.CharacterParser;
import com.ygs.android.yigongshe.utils.PinyinComparator;
import com.ygs.android.yigongshe.utils.PinyinUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

public class SchoolSelectActivity extends BaseActivity {
    @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
    @BindView(R.id.et_city_search) EditText mEtCitySearch;
    @BindView(R.id.lv_chool_list) ListView mLvSchoolList;
    @BindView(R.id.sidebar) SideBar mSidebar;
    
    private CityAdapter adapter;
    private CharacterParser characterParser;
    private ArrayList<SortModel> sourceDateList;
    private PinyinComparator pinyinComparator;
    private  ArrayList<String> schools;
    private String mString;//准备发起查询的字串

    @Override protected void initIntent(Bundle bundle) {
        schools = bundle.getStringArrayList("schools");
        if (schools == null){
            finish();
        }
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
    }

    @Override protected int getLayoutResId() {
        return R.layout.activity_school_select;
    }

    private void initCityList() {
        sourceDateList = new ArrayList<>();
        adapter = new CityAdapter(this, sourceDateList);
        mLvSchoolList.setAdapter(adapter);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSidebar.setOnLetterChangedListener(new SideBar.OnLetterChangedListener() {

            @Override public void onLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mLvSchoolList.setSelection(position);
                }
            }
        });

        mLvSchoolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String schoolName = ((SortModel) adapter.getItem(position)).getName();
                Bundle bundle = new Bundle();
                bundle.putString("name", schoolName);
                backForResult(RegisterActivity.class, bundle, 100);
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

        if (schools == null){
            return;
        }
        sourceDateList.addAll(fillListData(schools));
        // 根据a-z进行排序源数据
        Collections.sort(sourceDateList, pinyinComparator);
        adapter.notifyDataSetChanged();
    }

    /**
     * 为ListView填充数据
     */
    private ArrayList<SortModel> fillListData(List<String> cityList) {
        ArrayList<SortModel> mSortList = new ArrayList<>();
        for (int i = 0; i < cityList.size(); i++) {
            String name = cityList.get(i);
            if (!TextUtils.isEmpty(name)) {
                SortModel sortModel = new SortModel();
                //汉字转换成拼音
                String pinyin = PinyinUtils.getPinYin(name);
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pinyin)) {
                    sortModel.setName(name);
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
        adapter.updateListView(filterDateList);
    }

    @Override
    protected boolean openTranslucentStatus() {
        return true;
    }

    @OnClick(R.id.search) public void onBtnClicked() {
        filterData(mString);
    }
}
