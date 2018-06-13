package com.weikan.app.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.weikan.app.BuildConfig;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.AccountObtainEvent;
import com.weikan.app.account.bean.LoginEvent;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.Global;
import de.greenrobot.event.EventBus;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 12:24
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        updateData();

        titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("应用设置");

        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.ll_set_pwd).setOnClickListener(this);
        findViewById(R.id.ll_set_help_feedback).setOnClickListener(this);
        findViewById(R.id.ll_set_guide).setOnClickListener(this);
        findViewById(R.id.ll_set_about).setOnClickListener(this);
        findViewById(R.id.ll_log_out).setOnClickListener(this);
        findViewById(R.id.ll_log_in).setOnClickListener(this);

        TextView versionText = (TextView) findViewById(R.id.tv_set_version);
        versionText.setText("当前版本 v"+ BuildConfig.VERSION_NAME);

        if(AccountManager.getInstance().isLogin()){
            findViewById(R.id.ll_log_in).setVisibility(View.GONE);
            findViewById(R.id.ll_log_out).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ll_log_in).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_log_out).setVisibility(View.GONE);
        }


    }

    public void onEventMainThread(AccountObtainEvent event){
        if(AccountManager.getInstance().isLogin()){
            findViewById(R.id.ll_log_in).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_log_in).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(LoginEvent event){
        if(AccountManager.getInstance().isLogin()){
            finish();
        }
    }

    private void updateData(){

    }

    @Override
    public void onClick(View view) {
        if(view==null){
            return;
        }
        switch (view.getId()){
            case R.id.ll_log_in: {
                AccountManager.getInstance().gotoLogin(this);
            }
            break;
//            case R.id.ll_set_pwd: {
//                Intent i = new Intent();
//                i.setClass(this, AccountForgotPasswordActivity.class);
//                i.putExtra("pwd_type",1);
//                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(i);
//            }
//            break;
            case R.id.ll_set_help_feedback: {
                Intent i = new Intent();
                i.setClass(this, FeedbackActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
            break;
//            case R.id.ll_set_guide:
//                NavigateUtil.gotoWebshell(this, "http://182.92.212.76/views/help.html");
//                break;
            case R.id.ll_log_out: {
                AccountManager.getInstance().logout(this);
                Toast.makeText(this, "已经退出登录。", Toast.LENGTH_SHORT).show();
                finish();
            }
//            break;
//            case R.id.ll_set_about: {
//                Intent intent = new Intent();
//                intent.setClass(this, AboutActivity.class);
//                startActivity(intent);
//            }
//                break;


//            case R.id.ll_set_note:
//                NavigateUtil.gotoWebshell(this, URLDefine.NOTE_URL);
//                break;
//            case R.id.ll_set_noti: {
//                Intent intent = new Intent();
//                intent.setClass(this, NotifSetActivity.class);
//                startActivity(intent);
//            }
//                break;
            default:
                break;
        }
    }

}