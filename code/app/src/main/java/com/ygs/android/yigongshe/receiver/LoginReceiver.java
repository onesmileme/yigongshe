package com.ygs.android.yigongshe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String token = intent.getStringExtra("token");

        Log.e("RECEIVER", "onReceive: "+token );
    }
}
