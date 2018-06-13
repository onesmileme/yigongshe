package com.weikan.app.personalcenter;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.SimpleJsonResponseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaorenhui on 2015/12/18.
 */
public class FeedbackActivity extends BaseActivity {

    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initTitle();
        etContent = (EditText) findViewById(R.id.et_Content);
    }

    private void initTitle() {
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("给我们留言");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView view = (TextView) findViewById(R.id.tv_titlebar_right);
        view.setVisibility(View.VISIBLE);
        view.setText("发送");
        view.setTextColor(Color.WHITE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
    }


    private void sendRequest() {
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_FEEDBACK);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN,AccountManager.getInstance().getSession());
        params.put("leave_msg", content);
        String name = "";
        if (AccountManager.getInstance().getUserData() != null) {
            name = AccountManager.getInstance().getUserData().nick_name;
        }
        params.put("username", name);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                Toast.makeText(FeedbackActivity.this.getApplicationContext(), "提交成功。", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
