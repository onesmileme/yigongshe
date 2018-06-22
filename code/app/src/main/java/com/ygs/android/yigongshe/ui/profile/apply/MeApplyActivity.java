package com.ygs.android.yigongshe.ui.profile.apply;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

public class MeApplyActivity extends BaseActivity implements View.OnClickListener{



    @BindView(R.id.me_apply_submit_btn)
    Button mSubmitButton;

    @BindView(R.id.titlebar_backward_btn)
    Button mNavBackButton;

    @BindView(R.id.titlebar_text_title)
    TextView mTitleView;

    @BindView(R.id.me_apply_duration_et)
    EditText mDurationEditText;

    @BindView(R.id.me_apply_reason_et)
    EditText mReasonEditText;

    protected void initIntent(){

    }

    protected void initView(){

        mNavBackButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);

        mTitleView.setText(R.string.apply_duration);

    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_apply;
    }


    public void onClick(View view){
        if (view == mSubmitButton){

        }else if(view == mNavBackButton){
            this.finish();
        }
    }
}
