package com.weikan.app.util;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.weikan.app.R;

/**
 * 对话框工具类
 * 
 * @author Patrick.Li
 * 
 */
public class DialogUtils extends Activity {

	/**
	 * 显示Loading对话框
	 * 
	 * @param context
	 * @return
	 */
	public static ProgressDialog showProgress(Context context) {
		return showProgress(context, R.string.common_loading);
	}

	/**
	 * 显示Loading对话框
	 * 
	 * @param context
	 * @param msgResId
	 * @return
	 */
	public static ProgressDialog showProgress(Context context, int msgResId) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("");
		progressDialog.setMessage(context.getResources().getString(msgResId));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;
	}

	/**
	 * 显示带OK按钮的对话框
	 * 
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showOk(Context context, String title, String message) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
		localBuilder.setTitle(title).setMessage(message)
				.setPositiveButton(context.getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface mDialogInterface, int mInt) {
						mDialogInterface.cancel();
					}
				}).create().show();
	}

	/**
	 * 显示带OK按钮的对话框
	 * 
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showOk(Context context, String title, String message, DialogInterface.OnClickListener okClickListener) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
		localBuilder.setTitle(title).setMessage(message)
				.setPositiveButton(context.getResources().getString(R.string.common_ok), okClickListener).create().show();
	}

	/**
	 * 显示带OK和Cancel按钮的对话框
	 * 
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showOkCancel(Context context, String title, String message, DialogInterface.OnClickListener okClickListener) {
		showOkCancel(context, title, message, okClickListener, null);
	}

	/**
	 * 显示带OK和Cancel按钮的对话框
	 * 
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showOkCancel(Context context, String title, String message,
			DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener cancelClickListener) {
		
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
		DialogInterface.OnClickListener cancelListener = cancelClickListener;
		if (cancelListener == null) {
			cancelListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface mDialogInterface, int mInt) {
					mDialogInterface.cancel();
				}
			};
		}
		localBuilder.setTitle(title).setMessage(message)
				.setPositiveButton(context.getResources().getString(R.string.common_ok), okClickListener)
				.setNegativeButton(context.getResources().getString(R.string.common_cancel), cancelListener).create().show();
	}

	/**
	 * 显示日期选择对话框
	 * 
	 * @param context
	 * @param listener
	 * @param defaultDate
	 */
	public static void showDatePicker(Context context, OnDateSetListener listener, Date defaultDate) {
		final Calendar calendar = Calendar.getInstance();
		Date currentDate = defaultDate == null ? new Date() : defaultDate;
		calendar.setTime(currentDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog datePicker = new DatePickerDialog(context, listener, year, month, day);
		datePicker.show();
	}

	/**
	 * 显示时间选择对话框
	 * 
	 * @param context
	 * @param listener
	 */
	public static void showTimePicker(Context context, OnTimeSetListener listener) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		showTimePicker(context, listener, hour, minute);
	}

	/**
	 * 显示时间选择对话框
	 * 
	 * @param context
	 * @param listener
	 * @param hourOfDay
	 * @param minute
	 */
	public static void showTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute) {
		TimePickerDialog timePicker = new TimePickerDialog(context, listener, hourOfDay, minute, true);
		timePicker.show();
	}

	/**
	 * 显示弹出列表
	 * 
	 * @param context
	 * @param adapter
	 * @param listener
	 */
	public static void showList(Context context, ListAdapter adapter, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(context).setTitle("").setAdapter(adapter, listener).show();
	}

	/**
	 * 显示弹出自定义列表
	 * 
	 * @param context
	 * @param adapter
	 * @param listener
	 */
	public static AlertDialog showCustomizeList(Context context, ListAdapter adapter, OnItemClickListener listener) {
		View customizeView = View.inflate(context, R.layout.dialog_listview, null);
		ListView listview = (ListView) customizeView.findViewById(R.id.listview);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(listener);

		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("").setView(customizeView).create();
		dialog.show();
		return dialog;
	}
}
