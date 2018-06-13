package com.weikan.app.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.bean.Voice;
import com.weikan.app.common.manager.VoicePlayManager;

import java.lang.ref.SoftReference;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/21
 */
public class VoiceRecordView extends RelativeLayout {

    ImageView ivVoicePlay;
    TextView tvVoice;

    double density = 0;


    private OnClickListener outerClickListener;

    @Nullable
    Voice voice;

    @NonNull
    VoicePlayManager.State state = VoicePlayManager.State.NotPlaying;

    public VoiceRecordView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.original_widget_voice_record, this);
        ivVoicePlay = (ImageView) findViewById(R.id.iv_voice_play);
        tvVoice = (TextView) findViewById(R.id.tv_voice);

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceClick();
                if (outerClickListener != null) {
                    outerClickListener.onClick(VoiceRecordView.this);
                }
            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        outerClickListener = l;
    }

    public void setVoice(@Nullable Voice voice) {
        this.voice = voice;

        if (voice == null) {
            tvVoice.setText("");
            setVisibility(View.GONE);
        } else {
            tvVoice.setText(voice.duration + "″");

            if (density == 0) {
                density = getResources().getDisplayMetrics().density;
            }
            int paddingLeft = (int) ((voice.duration + 30) * density);
            tvVoice.setPadding(paddingLeft, 0, tvVoice.getPaddingRight(), 0);

            setVisibility(View.VISIBLE);
        }

        setPlayState(VoicePlayManager.State.NotPlaying);
    }

    @Nullable
    public Voice getVoice() {
        return this.voice;
    }


    @SuppressWarnings("ResourceType")
    public void setPlayState(@NonNull final VoicePlayManager.State state) {
        this.state = state;

        if (state == VoicePlayManager.State.Playing) {
            ivVoicePlay.setImageResource(R.anim.anim_voice_play);
            AnimationDrawable drawable = (AnimationDrawable) ivVoicePlay.getDrawable();
            drawable.start();
        } else {
            ivVoicePlay.setImageResource(R.drawable.anim_voice_play_3);
        }
    }

    private void voiceClick() {
        Voice voice = getVoice();
        if (voice == null) {
            return;
        }

        VoicePlayManager manager = VoicePlayManager.getInstance();

        // 如果当前View正在播放，那么点击停止
        if (state == VoicePlayManager.State.Playing) {
            manager.stop();
            return;
        }

        // 如果有其他View正在播放，那么先停止他们的，然后播放当前View的
        if (manager.getState() == VoicePlayManager.State.Playing) {
            manager.stop();
        }

        manager.bindView(this);
        manager.start(voice.url);
    }
}
