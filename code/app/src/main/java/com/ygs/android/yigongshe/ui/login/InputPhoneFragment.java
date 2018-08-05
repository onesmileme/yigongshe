package com.ygs.android.yigongshe.ui.login;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import org.w3c.dom.Text;

import butterknife.BindView;

public class InputPhoneFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.titlebar) CommonTitleBar titleBar;

    @BindView(R.id.phone_et) EditText mPhoneEditText;

    @BindView(R.id.next_btn) Button mNextButton;

    SwitcherListener switcherListener;

    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    switcherListener.goBack(InputPhoneFragment.this);
                }
            }
        });

        mNextButton.setOnClickListener(this);
    }

    @Override
    public int getLayoutResId(){
        return R.layout.fragment_input_phone;
    }

    protected String  getPhone(){
        return mPhoneEditText.getText().toString();
    }


    @Override
    public void onClick(View v) {

        if (v == mNextButton){
            next();
        }

    }

    private void next(){

        String phone = mPhoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)){

            Toast.makeText(getActivity(),"请输入手机号",Toast.LENGTH_SHORT).show();
            return;
        }else{
            switcherListener.goNex(this);
        }
    }

}
