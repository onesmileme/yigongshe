package com.weikan.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.base.BaseActivity;
import com.weikan.app.common.widget.SimpleNavigationView;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import platform.http.responsehandler.SimpleJsonResponseHandler;

/**
 * @author Lee on 2016/12/25.
 */
public class ShareToCircleActivity extends BaseActivity {
    @Bind(R.id.navigation)
    SimpleNavigationView navigationView;
    @Bind(R.id.ed_pub_dis)
    EditText edPubDis;
    @Bind(R.id.iv_share_circle)
    ImageView ivShareCircle;
    @Bind(R.id.tv_share_circle)
    TextView tvShareCircle;

    String articleId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_circle);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imgUrl = bundle.getString("imgUrl");
            String shareContent = bundle.getString("shareTitle");
            articleId = bundle.getString("articleId");

            if (!TextUtils.isEmpty(imgUrl)) {
                ivShareCircle.setVisibility(View.VISIBLE);
                ImageLoaderUtil.updateImage(ivShareCircle, imgUrl);
            }
            tvShareCircle.setText(TextUtils.isEmpty(shareContent) ? "我分享了图片，你看不看" : shareContent);
        }

        navigationView.setLeftOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                finish();
            }
        });

        navigationView.setRightOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                String content = edPubDis.getText().toString();
                if (content.length() > Constants.pubContentMaxLength) {
                    LToast.showToast(getResources().getString(R.string.wenyou_pub_beyond, Constants.pubContentMaxLength));
                    return;
                }
                ShareCircleAgent.shareToCircle(articleId, content, new SimpleJsonResponseHandler() {
                    @Override
                    public void begin() {
                        super.begin();
                        showLoadingDialog();
                    }

                    @Override
                    public void success() {
                        LToast.showToast("分享成功");
                        finish();
                    }

                    @Override
                    public void end() {
                        super.end();
                        hideLoadingDialog();
                    }
                });
            }
        });

    }
}
