package com.weikan.app.live;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingPreviewCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.weikan.app.R;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.live.bean.LiveClosEvent;
import com.weikan.app.live.gles.FBO;
import com.weikan.app.live.ui.RotateLayout;
import com.weikan.app.live.widget.CameraPreviewFrameView;
import com.weikan.app.util.LToast;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.SimpleJsonResponseHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 直播，推流界面的视频显示层
 * @author kailun on 16/8/26.
 * @see LiveRecordActivity
 */
public class LiveRecordFragment extends BaseFragment
        implements
        View.OnLayoutChangeListener,
        StreamStatusCallback,
        StreamingPreviewCallback,
        SurfaceTextureCallback,
        AudioSourceCallback,
        CameraPreviewFrameView.Listener,
        StreamingSessionListener,
        StreamingStateChangedListener {

    private static final String TAG = "LiveRecordFragment";

    public static final String BUNDLE_LIVE_INFO = "bundle_live_info";

    public static final int ENCODING_LEVEL = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private static final int ZOOM_MINIMUM_WAIT_MILLIS = 33; //ms

    private RotateLayout mRotateLayout;

    private TextView mLogTextView;
    private TextView tvStreamStatus;

    protected boolean mShutterButtonPressed = false;

    private static final int MSG_START_STREAMING = 0;
    private static final int MSG_STOP_STREAMING = 1;
    private static final int MSG_SET_ZOOM = 2;
    private static final int MSG_MUTE = 3;
    private static final int MSG_FB = 4;
    private static final int MSG_SWITCH_CAMERA = 5;

    protected String mStatusMsgContent;

    protected String mLogContent = "\n";

    private View mRootView;

    protected MediaStreamingManager mMediaStreamingManager;
    protected CameraStreamingSetting mCameraStreamingSetting;
    protected MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    protected StreamingProfile mProfile;
    protected JSONObject mJSONObject;
    private String streamId = "";

    private boolean mOrientationChanged = false;

    protected boolean mIsReady = false;
    private int mCurrentZoom = 0;

    private int mMaxZoom = 0;

    private FBO mFBO = new FBO();

    private Switcher mSwitcher = new Switcher();

    private int mCurrentCamFacingIndex;//当前的摄像头状态（用于 改变 前置摄像头 和 后置摄像头）
    private AspectFrameLayout afl;

    private CameraPreviewFrameView cameraPreviewFrameView;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_STREAMING:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // disable the shutter button before startStreaming
                            boolean res = mMediaStreamingManager.startStreaming();
                        }
                    }).start();
                    break;

                case MSG_STOP_STREAMING:
//                    if (mShutterButtonPressed) {
                        // disable the shutter button before stopStreaming
//                        boolean res = mMediaStreamingManager.stopStreaming();
//                    }
                    if(getActivity()!=null){
                                                getActivity().finish();
                                           }

//                    getActivity().finish();
                    break;

                case MSG_SET_ZOOM:
                    mMediaStreamingManager.setZoomValue(mCurrentZoom);
                    break;

                case MSG_SWITCH_CAMERA:
                    mSwitcher.run();
                    break;

                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    };

    public static LiveRecordFragment makeFragment(@NonNull final String pubUrl) {
        LiveRecordFragment fragment = new LiveRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_LIVE_INFO, pubUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_live_record, container, false);

        String liveInfoFromServer = getArguments().getString(BUNDLE_LIVE_INFO);
        Log.i(TAG, "publishUrlFromServer:" + liveInfoFromServer);
        //设置AudioProfile的参数   Profile:配置文件
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024);
        //设置VideoProfile的参数 fps，coderate，maxfps
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(30, 1000 * 1024, 48);
        //avProfile:Audio and video profile for encoding.
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        mProfile = new StreamingProfile();
        try {
            mJSONObject = new JSONObject(liveInfoFromServer);
            streamId = mJSONObject.optString("id", "");
            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
            mProfile.setStream(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //对mProfile进行 各种参数设置
        mProfile
                //. setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)//fps:30   bitrate:2000 kbps
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)//sample rate:44100 HZ bitrate:48 kbps
//                .setPreferredVideoEncodingSize(960, 544)
                .setEncodingSizeLevel(ENCODING_LEVEL)//设置推流的分辨率
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)//通过setEncoderRCMode设置质量优先还是码率优先-
                .setAVProfile(avProfile)
                .setDnsManager(getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))//设置stream的配置 （api没有明确的说明，故不管）
                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000))
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
//Running has no effect at current version.

        CameraStreamingSetting.CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();
        mCurrentCamFacingIndex = cameraFacingId.ordinal();
        mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting
                .setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)////通过setCameraId可以指定使用前置摄像头或者是后置摄像头, CAMERA_FACING_FRONT为前置, CAMERA_FACING_BACK为后置
                .setContinuousFocusModeEnabled(true)// //通过setContinuousFocusModeEnabled设置自动对焦
                .setRecordingHint(false)//RecordingHint  提升数据源的帧率
                .setCameraFacingId(cameraFacingId)// 此方法也可以用来设置前置或者后置摄像头
                .setBuiltInFaceBeautyEnabled(true)//设置美颜
                .setResetTouchFocusDelayInMs(3000)
//                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE)
                //使用 PREVIEW_SIZE_LEVEL 和 PREVIEW_SIZE_RATIO 共同确定一个预览Size
                //PREVIEW_SIZE_LEVEL和相机预览的清晰度有关系, 设置为SMALL预览的画面会很不清晰
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.6f, 0.5f, 0.8f))//设置美颜的参数
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);

        //------------------------麦克风参数配置-----------------------------
        mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);

        initUI(view);
        afl = (AspectFrameLayout) view.findViewById(R.id.cameraPreview_afl);
        //AspectFrameLayout.SHOW_MODE.REAL: Show the content with same ratio with preview size.
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);

        Point outSize = new Point();
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        defaultDisplay.getSize(outSize);
        afl.setAspectRatio(outSize.y / outSize.x);

        //CameraPreviewFrameView:对GLSurfaceView 进行的一层封装
        cameraPreviewFrameView = (CameraPreviewFrameView) view.findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);

//        //设置水印
//        WatermarkSetting watermarksetting = new WatermarkSetting(this);
//        watermarksetting.setResourceId(R.drawable.qiniu_logo)
//                .setAlpha(100)
//                .setSize(WatermarkSetting.WATERMARK_SIZE.MEDIUM)
//                .setCustomPosition(0.5f, 0.5f);
        /**
         * 对父类的mMediaStreamingManager进行设置
         *
         */
        //设置 MediaStreamingManager 解码方式为软解软解
        AVCodecType codec = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                ? AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC
                : AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC;
        mMediaStreamingManager = new MediaStreamingManager(getActivity(), afl, cameraPreviewFrameView, codec); // sw codec

        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, null, mProfile);

        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setSurfaceTextureCallback(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
//        mMediaStreamingManager.setNativeLoggingEnabled(false);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        // update the StreamingProfile
//        mProfile.setStream(new Stream(mJSONObject1));
//        mMediaStreamingManager.setStreamingProfile(mProfile);
        setFocusAreaIndicator();
        mMediaStreamingManager.resume();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mMediaStreamingManager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

//        mIsReady = false;
//        mShutterButtonPressed = false;
//        mHandler.removeCallbacksAndMessages(null);
//        if(!isFinished){
//            mMediaStreamingManager.pause();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaStreamingManager.stopStreaming();
        mMediaStreamingManager.destroy();
    }


    private void initUI(final View view) {
        tvStreamStatus = (TextView) view.findViewById(R.id.tv_stream_status);
    }

    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }

    //根据CameraStreamingSetting来返回CAMERA_FACING_ID
    private CameraStreamingSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

    /**
     * 给屏幕设置一个 框框（对焦用）
     */
    protected void setFocusAreaIndicator() {
//        if (mRotateLayout == null) {
//            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
//            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,
//                    mRotateLayout.findViewById(R.id.focus_indicator));
//        }
    }

    /**
     * 通过控制handler来对stream进行管理
     */
    protected void startStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
    }

    /* package */ void stopStreaming() {
        mMediaStreamingManager.pause();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
        LiveAgent.postClose(streamId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                // do nothing
            }
        });
    }
        void stop(){
            mHandler.removeCallbacksAndMessages(null);
            LiveAgent.postClose(streamId, new SimpleJsonResponseHandler() {
                @Override
                public void success() {
                    // do nothing
                }
            });
        }
    /* package */ void switchCamera() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SWITCH_CAMERA), 50);
    }

    // region 各种接口...

//    @Override
//    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, boolean b) {
//
//    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());

        if (mIsReady) {
//            setFocusAreaIndicator();
            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {//zoom：变焦
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);

            Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            if (!mHandler.hasMessages(MSG_SET_ZOOM)) {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ZOOM), ZOOM_MINIMUM_WAIT_MILLIS);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Log.i(TAG, "view!!!!:" + v);
    }

    @Override
    public void notifyStreamStatusChanged(final StreamingProfile.StreamStatus streamStatus) {

    }

//    @Override
//    public void onPreviewFrame(byte[] bytes, Camera camera) {
//    }
//
//    @Override
//    public boolean onPreviewFrame(byte[] bytes, int i, int i1) {
//        return true;
//    }

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        mMediaStreamingManager.updateEncodingType(AVCodecType.SW_VIDEO_CODEC);
        mMediaStreamingManager.startStreaming();
        return true;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        Log.i(TAG, "onRestartStreamingHandled");
        return mMediaStreamingManager.startStreaming();
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            for (Camera.Size s : list) {
                if (s.height >= 480) {
                    size = s;
                    break;
                }
            }
        }
//        Log.e(TAG, "selected size :" + size.width + "x" + size.height);
        return size;
    }
boolean isFinished = false;
    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        if (isDetached()) {
            return;
        }
        Log.e("Cary", "state:"+streamingState);
        switch (streamingState) {
            case PREPARING:
                mStatusMsgContent = getString(R.string.string_state_preparing);
                break;
            case READY:
                mIsReady = true;
                mMaxZoom = mMediaStreamingManager.getMaxZoom();
                mStatusMsgContent = getString(R.string.string_state_ready);
                // start streaming when READY
                if (!isFinished) {
                    startStreaming();
                } else {
                    mMediaStreamingManager.stopStreaming();
                    stopStreaming();
                    Toast.makeText(getActivity(),"直播已经停止，请重新发起",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            case CONNECTING:
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case STREAMING:
                mStatusMsgContent = getString(R.string.string_state_streaming);

                break;
            case SHUTDOWN:
                mStatusMsgContent = "READY";
                isFinished = true;
//                if (mOrientationChanged) {
//                    mOrientationChanged = false;
//                    startStreaming();
//                }
                break;
            case IOERROR:
                mLogContent += "IOERROR\n";
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case UNKNOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "Open Camera Fail. id:" + extra);
                break;
            case DISCONNECTED:
                mLogContent += "DISCONNECTED\n";
                break;
            case INVALID_STREAMING_URL:
                Log.e(TAG, "Invalid streaming url:" + extra);
                break;
            case UNAUTHORIZED_STREAMING_URL:
                Log.e(TAG, "Unauthorized streaming url:" + extra);
                mLogContent += "Unauthorized Url\n";
                break;
            case CAMERA_SWITCHED:
//                mShutterButtonPressed = false;
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer) extra);
                }
                Log.i(TAG, "camera switched");
                break;
            default:
                break;
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLogTextView != null) {
                        mLogTextView.setText(mLogContent);
                    }
//                tvStreamStatus.setText(mStatusMsgContent);
                }
            });
        }

    }

    @Override
    public void onSurfaceCreated() {
        //初始化美颜的效果
        Log.i(TAG, "onSurfaceCreated");
        mFBO.initialize(getActivity());
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
        mFBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mFBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] floats) {
        int newTexId = mFBO.drawFrame(texId, texWidth, texHeight);
//        Log.i(TAG, "onDrawFrame texId:" + texId + ",newTexId:" + newTexId + ",texWidth:" + texWidth + ",texHeight:" + texHeight);
        return newTexId;
    }


    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, boolean b) {

    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    @Override
    public boolean onPreviewFrame(byte[] bytes, int i, int i1) {
        return false;
    }
    // endregion


    //用这个进程来进行摄像头的切换
    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();

            CameraStreamingSetting.CAMERA_FACING_ID facingId;
            if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
            } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
            } else {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
            }
            Log.i(TAG, "switchCamera:" + facingId);
            mMediaStreamingManager.switchCamera(facingId);
        }
    }


}
