package com.weikan.app.util;

import android.content.Context;
import android.content.Intent;
import com.weikan.app.MainApplication;
import com.weikan.app.MainActivity;
import com.weikan.app.WebshellActivity;

/**
 * Created by liujian on 15/3/20.
 */
public class NavigateUtil {

	public static void gotoWebshell(Context context, String url) {
		Intent intent = new Intent();
		intent.setClass(context, WebshellActivity.class);
		if (context.equals(MainApplication.getInstance().getApplicationContext())) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.putExtra(BundleParamKey.URL, url);
		context.startActivity(intent);

	}

	public static void gotoMainActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);
		context.startActivity(intent);

	}

}
