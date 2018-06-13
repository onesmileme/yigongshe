package com.weikan.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Intent跳转管理
 * 
 * @author Patrick.Li
 * 
 */
public class IntentUtils {

	public static void to(Activity activity, Intent intent) {
		activity.startActivity(intent);
	}

	public static void to(Activity activity, Class<?> clasz) {
		Intent intent = new Intent(activity, clasz);
		activity.startActivity(intent);
	}

	public static void to(Activity activity, Class<?> clasz, Bundle bundle) {
		Intent intent = new Intent(activity, clasz);
		intent.putExtras(bundle);
		activity.startActivity(intent);

	}

	public static void toForResult(Activity activity, Class<?> clasz, int requestCode) {
		Intent intent = new Intent(activity, clasz);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void toForResult(Activity activity, Class<?> clasz, Bundle bundle, int requestCode) {
		Intent intent = new Intent(activity, clasz);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void toForResult(Activity activity, Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
	}

	public static void toFlags(Context context, Class<?> clasz, Bundle bundle, int flags) {
		Intent intent = new Intent(context, clasz);
		intent.putExtras(bundle);
		intent.setFlags(flags);
		context.startActivity(intent);
	}
	// public static void toLogin(Activity activity) {
	// Intent intent = new Intent(activity, LoginActivity.class);
	// activity.startActivity(intent);
	// AnimUtils.leftToRight(activity);
	// }
	//
	// public static void toLoginForResult(Activity activity, int requestCode) {
	// toLoginForResult(activity, requestCode, Constants.COME_FROM_FOR_RESULT);
	// }
	//
	// public static void toLoginForResult(Activity activity, int requestCode,
	// int flag) {
	// Intent intent = new Intent(activity, LoginActivity.class);
	// intent.putExtra(LoginActivity.PARAM_COME_FROM, flag);
	// toForResult(activity, intent, requestCode);
	// }
	//
	// public static void logout(Activity activity) {
	// Session.clearUp();
	// Intent intent = new Intent(activity, LoginActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// activity.startActivity(intent);
	// }
	//
	// public static void toHome(Activity activity) {
	// Intent intent = new Intent(activity, HomeActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// activity.startActivity(intent);
	// }

	public static void toCall(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	public static void toSystemBroswer(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}

	// public static void toBroswer(Activity activity, String url) {
	// Bundle bundle = new Bundle();
	// bundle.putString("url", url);
	// to(activity, BroswerActivity.class, bundle);
	// }

	public static void toEmail(Context context, String email, String title, String text) {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse("mailto:" + email));
		data.putExtra(Intent.EXTRA_SUBJECT, title);
		data.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(data);
	}
}
