package com.weikan.app.live;

 import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.live.bean.LiveClosEvent;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.live.widget.MediaController;
import com.weikan.app.util.DialogUtils;
import com.weikan.app.util.NetWorkUtil;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created by liujian on 16/8/27.
 */
public class LivePlayNativeActivity extends BaseActivity {

    private static final String TAG = LivePlayNativeActivity.class.getSimpleName();

    private MediaController mMediaController;
    private PLVideoView mVideoView;
    private Toast mToast = null;
    private LivePlayCoverFragment livePlayCoverFragment;
//    private String mVideoPath = null;
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT;
    private boolean mIsActivityPaused = true;

    private long liveId;
    private static final String BUNDLE_OBJ = "bundle_obj";
    private LiveListObject liveData;
    private LivePlayNativeCoverDialog dialog;
    private View backView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_video_view);
        mVideoView = (PLVideoView) findViewById(R.id.VideoView);
        View loadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(loadingView);
        backView = findViewById(R.id.background);
//        mVideoPath = getIntent().getStringExtra("videoPath");
        liveData = (LiveListObject) getIntent().getSerializableExtra(BUNDLE_OBJ);
        if(liveData == null){
            liveId =Long.parseLong( getIntent().getStringExtra("liveid"));
        }else{
            liveId = liveData.liveId;
        }

        AVOptions options = new AVOptions();

        int isLiveStreaming = getIntent().getIntExtra("liveStreaming", 1);
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 20000);
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 60000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, 0);
        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        mVideoView.setAVOptions(options);

        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
//        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        // You can also use a custom `MediaController` widget
//        mMediaController = new MediaController(this, false, isLiveStreaming == 1);
        mVideoView.setMediaController(null);
        LiveAgent.getLiveNativeDetail(liveId, new AmbJsonResponseHandler<LiveListObject>() {
            @Override
            public void success(@NonNull LiveListObject data) {
                if(data != null ){
                    liveData = data;
                    makeLiveRecordCoverFragment();
                    if(!TextUtils.isEmpty(liveData.url)){
                        mVideoView.setVideoPath(data.url);
                    }else{
                        showToastTips("系统异常，请稍后重试");
                        finish();
                    }
                }

            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                showToastTips("系统异常，请稍后重试");
                finish();
            }
        });

    }
    private void makeLiveRecordCoverFragment() {
        dialog = new LivePlayNativeCoverDialog(this,liveData,this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        backView.setVisibility(View.GONE);
//        mVideoView.setVisibility(View.VISIBLE);
        mIsActivityPaused = false;
//        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToast = null;
        backView.setVisibility(View.VISIBLE);
        mIsActivityPaused = true;
//        mVideoView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null){
            dialog.dismiss();
            dialog.cancel();
        }
        mVideoView.stopPlayback();
    }

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
//           LToast.showToast(String.valueOf(plMediaPlayer.getDuration()));
            if(what == PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){

                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.start();
            }
            if(what == PLMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED){
                dialog.setTotalTime(plMediaPlayer.getDuration());
               dialog.setPlayStatus(LivePlayNativeCoverDialog.START_PLAY);
            }
            return false;
        }
    };
    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    DialogUtils.showOk(LivePlayNativeActivity.this, "", "播放资源不存在", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                    return true;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    playException();
                    return true;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                    mVideoView.setVisibility(View.GONE);
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "网络异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                    return true;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                    mVideoView.setVisibility(View.GONE);
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "系统异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                    return true;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    playException();
                    return true;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
            }
            return false;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
        isContiue();
        }
    };
    private void playException(){
        LiveAgent.getLiveNativeDetail(liveData.liveId, new AmbJsonResponseHandler<LiveListObject>() {
            @Override
            public void success(@Nullable final LiveListObject data) {
                if(data != null){
                    if(data.status == 1){
                        LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                        mVideoView.setVisibility(View.GONE);
                        DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "系统异常，请重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!TextUtils.isEmpty(data.url)){
                                    showToastTips("正在尝试重连");
                                    mVideoView.setVideoPath(data.url);
                                    mVideoView.start();
                                }else{
                                    LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.CLOSE_PLAY);
                                    DialogUtils.showOk(LivePlayNativeActivity.this, "", "观看结束，请确认退出", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(liveData.status == 1){
                                                EventBus.getDefault().post(new LiveClosEvent());
                                            }
                                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                                            finish();
                                        }
                                    });
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LivePlayNativeActivity.this.dialog.closeInputMethod();
                                finish();
                            }
                        });
                    }else{
                        LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.CLOSE_PLAY);
                        DialogUtils.showOk(LivePlayNativeActivity.this, "", "观看结束，请确认退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(liveData.status == 1){
                                    EventBus.getDefault().post(new LiveClosEvent());
                                }
                                LivePlayNativeActivity.this.dialog.closeInputMethod();
                                finish();
                            }
                        });
                    }
                }
            }
            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                if(!NetWorkUtil.isNetworkConnected()){
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "网络异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                }else{
                    LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "系统异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                }
            }
        });
    }
    private void isContiue(){
        LiveAgent.getLiveNativeDetail(liveData.liveId, new AmbJsonResponseHandler<LiveListObject>() {
            @Override
            public void success(@Nullable final LiveListObject data) {
                if(data != null){
                    if(data.status == 1){
                        if(!TextUtils.isEmpty(data.url)){
                            showToastTips("正在尝试重连");
                            mVideoView.setVideoPath(data.url);
                            mVideoView.start();
                            mVideoView.setVisibility(View.VISIBLE);
                        }else{
                            LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.CLOSE_PLAY);
                            DialogUtils.showOk(LivePlayNativeActivity.this, "", "观看结束，请确认退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(liveData.status == 1){
                                        EventBus.getDefault().post(new LiveClosEvent());
                                    }
                                    LivePlayNativeActivity.this.dialog.closeInputMethod();
                                    finish();
                                }
                            });
                        }
                    }else{
                        LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.CLOSE_PLAY);
                        DialogUtils.showOk(LivePlayNativeActivity.this, "", "观看结束，请确认退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(liveData.status == 1){
                                    EventBus.getDefault().post(new LiveClosEvent());
                                }
                                LivePlayNativeActivity.this.dialog.closeInputMethod();
                                finish();
                            }
                        });
                    }
                }
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                mVideoView.setVisibility(View.GONE);
                LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                if(!NetWorkUtil.isNetworkConnected()){
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "网络异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                }else{
                    LivePlayNativeActivity.this.dialog.setPlayStatus(LivePlayNativeCoverDialog.STOP_PLAY);
                    DialogUtils.showOkCancel(LivePlayNativeActivity.this, "", "系统异常，请重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isContiue();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePlayNativeActivity.this.dialog.closeInputMethod();
                            finish();
                        }
                    });
                }
            }
        });
    }
    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
//            mVideoView.start();
            dialog.setPlayStatus(LivePlayNativeCoverDialog.START_PLAY);
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: " + width + "," + height);
        }
    };

    private void showToastTips(final String tips) {
        if (mIsActivityPaused) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(LivePlayNativeActivity.this, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }
    public void seekTo(double position ){
//        mVideoView.pause();
        mVideoView.seekTo((long)position);
    }
    public void startPlay(){
        mVideoView.start();
    }
    public void stopPlay(){
        mVideoView.pause();
    }
}
