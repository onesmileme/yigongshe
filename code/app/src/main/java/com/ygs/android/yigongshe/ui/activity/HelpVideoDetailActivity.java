package com.ygs.android.yigongshe.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

/**
 * Created by ruichao on 2018/7/10.
 */

public class HelpVideoDetailActivity extends BaseActivity {
  private String mSrcUrl; //播放视频链接

  @BindView(R.id.videoView) VideoView mVideoView;

  protected boolean openTranslucentStatus() {
    return true;
  }

  @Override protected void initIntent(Bundle bundle) {
    mSrcUrl = bundle.getString("src");
  }

  @Override protected void initView() {
    Uri uri = Uri.parse(mSrcUrl);
    //设置视频控制器
    mVideoView.setMediaController(new MediaController(this));

    //播放完成回调
    mVideoView.setOnCompletionListener(null);

    //设置视频路径
    mVideoView.setVideoURI(uri);

    //开始播放视频
    mVideoView.start();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_helpvideo_detail;
  }
}
