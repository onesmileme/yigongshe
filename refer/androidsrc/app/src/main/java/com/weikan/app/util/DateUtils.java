package com.weikan.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;
import com.weikan.app.R;

/**
 * 日期操作工具类
 *
 * @author Patrick.Li
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
     * <p/>
     * return Date
     *
     * @return
     */
    public static Date getSystemDate() {
        return new Date();
    }

    /**
     * Example: getSystemDate("yyyyMMdd");
     * <p/>
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
     * <p/>
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
     * <p/>
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
     * <p/>
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

    public static String getStrTime(long cc_time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(cc_time * 1000));
            return date;
        } catch (Exception e) {
            return "";
        }
    }

    public static String passedTime(long timeMillis) {
        return new FriendlyDate(timeMillis).toFriendlyDate(false);
//        long passedTimeMillis = System.currentTimeMillis() - timeMillis;
//        if (passedTimeMillis < 5L * 60 * 1000) {
//            return "刚刚";
//        } else if (passedTimeMillis < 60L * 60 * 1000) {
//            return String.format(Locale.getDefault(), "%d分钟前", (int) (passedTimeMillis / 60 / 1000));
//        } else if (passedTimeMillis < 24L * 60 * 60 * 1000) {
//            return String.format(Locale.getDefault(), "%d小时前", (int) (passedTimeMillis / 60 / 60 / 1000));
//        } else if (passedTimeMillis < 30L * 24 * 60 * 60 * 1000) {
//            return String.format(Locale.getDefault(), "%d天前", (int) (passedTimeMillis / 24 / 60 / 60 / 1000));
//        } else if (passedTimeMillis < 12L * 30 * 24 * 60 * 60 * 1000) {
//            return String.format(Locale.getDefault(), "%d月前", (int) (passedTimeMillis / 30 / 24 / 60 / 60 / 1000));
//        } else {
//            return String.format(Locale.getDefault(), "%d年前", (int) (passedTimeMillis / 12 / 30 / 24 / 60 / 60 / 1000));
//        }
    }

    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
    private final static int[] consResWhiteArr = new int[] {R.drawable.cons_mojie_white, R.drawable.cons_shuiping_white,
            R.drawable.cons_shuangyu_white, R.drawable.cons_baiyang_white, R.drawable.cons_jinniu_white,
            R.drawable.cons_shuangzi_white, R.drawable.cons_juxie_white, R.drawable.cons_shizi_white,
            R.drawable.cons_chunv_white, R.drawable.cons_tianping_white, R.drawable.cons_tianxie_white,
            R.drawable.cons_sheshou_white, R.drawable.cons_mojie_white };

    /**
     * 计算星座
     */
    public static String getConstellation(long timemillis) {
        if (timemillis == 0) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timemillis);
        return getConstellation(calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 计算星座
     * @param month
     * @param day
     * @return
     */
    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    public static int getConstellationResWhite(long timemillis) {
        if (timemillis == 0) {
            return R.color.transparent;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timemillis);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day < dayArr[month - 1] ? consResWhiteArr[month - 1] : consResWhiteArr[month];
    }
}
