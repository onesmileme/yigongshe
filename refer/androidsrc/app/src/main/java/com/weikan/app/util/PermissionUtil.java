package com.weikan.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;

/**
 * Created by ylp on 2016/10/21.
 */

public class PermissionUtil {
    public static boolean isCameraPermission() {

        try {
            Camera.open().release();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 作用：用户是否同意录音权限
     *
     * @return true 同意 false 拒绝
     */
    public static boolean isVoicePermission() {

        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if(recordingState == AudioRecord.RECORDSTATE_STOPPED){
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
