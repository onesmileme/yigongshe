package com.ygs.android.yigongshe.ui.profile.info;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.SchoolInfoBean;
import com.ygs.android.yigongshe.bean.SchoolListBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CityListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class MeInfoChangeSchoolActivity extends BaseActivity {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;


    @BindView(R.id.city_sp)
    Spinner citySpinner;

    @BindView(R.id.school_sp)
    Spinner schoolSpinner;

    @BindView(R.id.change_school_submit_btn)
    Button submitButton;

    private LinkCall<BaseResultDataInfo<SchoolListBean>> mSchollListCall;

    private String[] provinces;
    private SchoolListBean schoolListBean;

    private MeInfoChangeSchoolAdapter cityAdapter;
    private MeInfoChangeSchoolAdapter schoolAdapter;

    private String chooseCity;
    private String chooseSchool;


    private LinkCall<BaseResultDataInfo<UserInfoBean>> mChangeCall;

    @Override
    protected void initIntent(Bundle bundle){

    }

    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseCity = cityAdapter.nameAtIndex(position);
                SchoolInfoBean schoolInfoBean = schoolListBean.school_list.get(position);
                String[] schools = new String[schoolInfoBean.schools.size()];
                for (int i = 0 ; i < schools.length ; i++){
                    schools[i] = schoolInfoBean.schools.get(i);
                }
                schoolAdapter = new MeInfoChangeSchoolAdapter(MeInfoChangeSchoolActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1,schools);
                schoolSpinner.setAdapter(schoolAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseSchool = schoolAdapter.nameAtIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSchool();
            }
        });
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_school;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (schoolListBean == null){
            getSchoolList();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mChangeCall != null && !mChangeCall.isCanceled()){
            mChangeCall.cancel();
        }
    }

    private void getSchoolList(){

        String token = YGApplication.accountManager.getToken();
        mSchollListCall =  LinkCallHelper.getApiService().getSchoolList(token);
        mSchollListCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<SchoolListBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<SchoolListBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    schoolListBean = entity.getData();
                    provinces = new String[schoolListBean.school_list.size()];

                    for (int i = 0 ; i < schoolListBean.school_list.size();i++){
                        SchoolInfoBean schoolInfoBean = entity.getData().school_list.get(i);
                        provinces[i] = schoolInfoBean.province;
                    }

                    cityAdapter = new MeInfoChangeSchoolAdapter(MeInfoChangeSchoolActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1,provinces);
                    String[] schools = {schoolListBean.cur_school};
                    schoolAdapter = new MeInfoChangeSchoolAdapter(MeInfoChangeSchoolActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1,schools);

                    citySpinner.setAdapter(cityAdapter);
                    schoolSpinner.setAdapter(schoolAdapter);

                }else{
                    Toast.makeText(MeInfoChangeSchoolActivity.this,entity.msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeSchool(){
        if (chooseSchool == null){
            Toast.makeText(this,"请选择要更改想学校",Toast.LENGTH_SHORT).show();
            return;
        }

        String token = YGApplication.accountManager.getToken();

        mChangeCall = LinkCallHelper.getApiService().modifySchool(token,chooseSchool);
        mChangeCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    Toast.makeText(MeInfoChangeSchoolActivity.this,"更改学校成功",Toast.LENGTH_SHORT).show();
                    YGApplication.accountManager.updateSchool(chooseSchool);
                }else{
                    Toast.makeText(MeInfoChangeSchoolActivity.this,"更改学校失败("+entity.msg+")",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
