package com.weikan.app.common.manager;

import android.media.MediaPlayer;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.weikan.app.widget.VoiceRecordView;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * 声音播放的管理器
 * 不弄个管理器管起来很麻烦就是了
 * @author kailun on 16/3/26
 */
public class VoicePlayManager {

    public static VoicePlayManager sManager = null;

    public static VoicePlayManager getInstance() {
        if (sManager == null) {
            synchronized (VoicePlayManager.class) {
                if (sManager == null) {
                    sManager = new VoicePlayManager();
                }
            }
        }
        return sManager;
    }

    // 唯一的Player
    private MediaPlayer player;

    // 对应的View
    private WeakReference<VoiceRecordView> view;

    /**
     * 状态的枚举
     */
    public enum State {
        NotPlaying, Playing
    }

    @NonNull
    private State state = State.NotPlaying;

    @NonNull
    public State getState() {
        return state;
    }

    private VoicePlayManager() {
        // 单例模式
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onEnd();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onEnd();
                return true;
            }
        });
    }

    /**
     * 设置当前的 VoiceRecordView
     * @param view view
     */
    public void bindView(final VoiceRecordView view) {
        this.view = new WeakReference<>(view);
    }

    /**
     * 播放
     * 只能在主线程执行
     * @param url url
     */
    public void start(@NonNull final String url) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("start can only be call from main thread");
        }

        // 先停止，后播放
        if (state == State.Playing) {
            stop();
        }

        try {
            initPlayer();
            player.setDataSource(url);
            player.prepare();
            player.start();

            onBegin();
        } catch (IOException | IllegalStateException e) {
            onEnd();
        }
    }

    /**
     * 停止
     * 只能在主线程执行
     */
    public void stop() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("stop can only be call from main thread");
        }

        try {
            player.stop();
        } catch (IllegalStateException e) {
            // 结束的时候的异常先不管
        } finally {
            onEnd();
        }
    }

    private void onBegin() {
        state = State.Playing;
        if (view != null) {
            VoiceRecordView v = view.get();
            if (v != null) {
                v.setPlayState(State.Playing);
            }
        }
    }

    private void onEnd() {
        state = State.NotPlaying;
        if (player != null) {
            player.release();
            player = null;
        }

        if (view != null) {
            VoiceRecordView v = view.get();
            if (v != null) {
                v.setPlayState(State.NotPlaying);
            }
            view = null;
        }
    }

}
