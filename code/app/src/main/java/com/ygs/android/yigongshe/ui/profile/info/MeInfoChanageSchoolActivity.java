package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;

public class MeInfoChanageSchoolActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    CommonTitleBar titleBar;


    @BindView(R.id.city_sp)
    Spinner citySpinner;

    @BindView(R.id.school_sp)
    Spinner schoolSpinner;

    protected void initIntent(Bundle bundle){

    }

    protected void initView(){


        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });
    }

    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_school;
    }

}
