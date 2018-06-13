package com.weikan.app.news.utils;


import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期操作工具类
 * 
 * @author Patrick.Li
 * 
 */
public class DateUtils {
	// 年-月-日
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	// 年-月-日 时:分
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	// 年-月-日 时:分:秒
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	// 年月日
	public static final String YYYYMMDD = "yyyyMMdd";
	// 年月日 时分
	public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
	// 年月日 时分秒
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	// 年月日 时分秒毫秒
	public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmsssss";
	// 年月: 1/2014
	public static final String M_YYYY = "M/yyyy";
	// 年: 2014
	public static final String YYYY = "yyyy";
	// 月: 01
	public static final String MM = "MM";
	// 日: 1
	public static final String D = "d";
	// 星期几: 周一
	public static final String E = "E";
	// 月日 星期几: 01-26 周日
	public static final String MM_DD_E = "MM-dd E";
	// 年月日 星期几: 2014-01-26 周日
	public static final String YYYY_MM_DD_E = "yyyy-MM-dd E";
	// 时:分
	public static final String HH_MM = "HH:mm";

	/**
	 * Example: getSystemDate();
	 * 
	 * return Date
	 * 
	 * @param format
	 * @return
	 */
	public static Date getSystemDate() {
		return new Date();
	}

	/**
	 * Example: getSystemDate("yyyyMMdd");
	 * 
	 * return 20110820
	 * 
	 * @param format
	 * @return
	 */
	public static String getSystemDateString(String format) {
		return formatDate(new Date(), format);
	}

	/**
	 * Example: getCurrentTime();
	 * 
	 * return 20110820101010222
	 * 
	 * @param
	 * @return
	 */
	public static String getCurrentTime() {
		return getSystemDateString(YYYYMMDDHHMMSSSSS);
	}

	/**
	 * Example: formatDate(date, "yyyyMMdd");
	 * 
	 * date = 2011/01/02, return 20110102
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

		if (date == null)
			return null;

		return sdf.format(date);
	}

	/**
	 * Example: formatDate("2011/01/10", "yyyy/MM/dd", "yyyy.MM.dd");
	 * 
	 * return 2011.01.10
	 * 
	 * @param dateString
	 * @param srcFormat
	 * @param targetFormat
	 * @return
	 */
	public static String formatDate(String dateString, String srcFormat, String targetFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(srcFormat, Locale.getDefault());

		Date date = null;
		try {
			if (!TextUtils.isEmpty(dateString))
				date = sdf.parse(dateString);
		} catch (ParseException e) {
		}

		return formatDate(date, targetFormat);
	}

	/**
	 * Example: parseDate("2011/01/10", "yyyy/MM/dd")
	 * 
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static Date parseDate(String dateString, String format) {
		final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

		Date date = null;
		try {
			if (!TextUtils.isEmpty(dateString))
				date = sdf.parse(dateString);
		} catch (final ParseException e) {
		}

		return date;
	}

	/**
	 * 取得明天的日期
	 * 
	 * @return
	 */
	public static Date getTomorrow() {
		return addDays(new Date(), 1);
	}

	/**
	 * 日期的月份加法
	 * 
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonths(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 日期的天加法
	 * 
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addDays(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 日期的小时加法
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date addHours(Date date, int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}

	/**
	 * 日期相减
	 * 
	 * @param date1
	 * @param date2
	 * @param dateFormat
	 * @return
	 */
	public static long minusDate(String date1, String date2, String dateFormat) {
		return minusDate(parseDate(date1, dateFormat), parseDate(date1, dateFormat));
	}

	/**
	 * 日期相减
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long minusDate(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0;
		}
		return Math.abs(date1.getTime() - date2.getTime());
	}

}
