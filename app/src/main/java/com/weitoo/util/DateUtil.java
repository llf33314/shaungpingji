package com.weitoo.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String SHORT_YEAR_MONTH_FORMAT = "yyyyMM";
    public static final String SHORT_TERSE_FORMAT = "yyyyMMdd";
    public static final String MONTH_DAY_FORMAT = "MM-dd";
    public static final String TIME = "HH:mm";
    public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五",
            "周六" };

    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDays() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int[] getPerviousWeekSunday() {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -getWeekDay());
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH)+1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;
    }

    public static int[] getWeekSunday(int year, int month, int day, int pervious) {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.DAY_OF_MONTH, pervious);
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH )+1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;

    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromString(year, month));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static String formatDateWithZone(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }

    public static String getCurDateTime() {
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        String curTime = formatter.format(new Date());
        return curTime;
    }

    public static String getStandardDateTime() {
        DateFormat formatter = new SimpleDateFormat(LONG_DATE_FORMAT);
        String curTime = formatter.format(new Date());
        return curTime;
    }

    public static Date stringToLongDate(String value) {
        DateFormat formatter = new SimpleDateFormat(LONG_DATE_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToShortDate(String value) {
        DateFormat formatter = new SimpleDateFormat(SHORT_DATE_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToSimpleDate(String value) {
        DateFormat formatter = new SimpleDateFormat(SHORT_TERSE_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToLongString(Date value) {
        return dateToFormatString(value, LONG_DATE_FORMAT);
    }

    public static String dateToShortString(Date value) {
        return dateToFormatString(value, SHORT_DATE_FORMAT);
    }

    public static String dateToMonthDayString(Date value) {
        return dateToFormatString(value, MONTH_DAY_FORMAT);
    }

    public static String dateToTimeString(Date value) {
        return dateToFormatString(value, TIME);
    }

    public static String dateToSimpleString(Date value) {
        return dateToFormatString(value, SHORT_TERSE_FORMAT);
    }

    public static String dateToFormatString(Date value, String format) {
        if (value == null)
            return "";
        DateFormat formatter = new SimpleDateFormat(format);
        String shortDate = formatter.format(value);
        return shortDate;
    }

    public static String dateToLocaleLongString(Date value) {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG);
        String longDate = formatter.format(value);
        return longDate;
    }

    public static String dateToLocaleShortString(Date value) {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        String longDate = formatter.format(value);
        return longDate;
    }

    public static String formatToYearAndMonth(Date value) {
        DateFormat formatter = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        String date = formatter.format(value);
        return date;
    }

    public static Date firstDayOfYearMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date lastDayOfYearMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    public static Date nextYearAndMonth(String value) {
        Calendar calendar = Calendar.getInstance();

        DateFormat formatter = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(value);
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }

    public static Date nextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date[] calculateWeekRange(Date begin, Date end) {
        Calendar calendar = Calendar.getInstance();
        Date[] weekDate = new Date[2];
        calendar.setTime(begin);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (calendar.getTime().after(begin))
            calendar.add(Calendar.DATE, -7);
        weekDate[0] = calendar.getTime();

        calendar.setTime(end);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if (calendar.getTime().before(end))
            calendar.add(Calendar.DATE, 7);
        weekDate[1] = calendar.getTime();

        return weekDate;
    }

    /**
     * @return
     */
    public static int getCurrentTime() {
        return new Long(System.currentTimeMillis() / 1000).intValue();
    }

    /**
     * @return return the start of today, which means the hour, minute and
     *         second are all set to 0.
     */
    public static Date getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date generateDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }
    /**
     * @param date
     *            Date to be added such as 2008-06-17 00:00:00
     * @param days
     *            how days to be added
     * @return the date added the specified days.
     */
    public static Date incrementDateByDay(Date date, int days) {
        if (date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days == 0 ? 1 : days);
        return calendar.getTime();
    }

    /**
     * @param date
     *            Date to be added such as 2008-06-17 00:00:00
     *
     *            how minutes to be added
     * @return the date added the specified minutes.
     */
    public static Date incrementDateByMinute(Date date, int minute) {
        if (date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute == 0 ? 1 : minute);
        return calendar.getTime();
    }

    /**
     * @param date
     *            Date to be added such as 2008-06-17 00:00:00
     *  days how seconds to be added
     * @return the date added the specified seconds.
     */
    public static Date incrementDateBySecond(Date date, int second) {
        if (date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second == 0 ? 1 : second);
        return calendar.getTime();
    }

    public static int calculateAge(Date birthday){
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        calendar.setTime(birthday);
        int birthdayYear = calendar.get(Calendar.YEAR);
        return nowYear - birthdayYear;
    }

    public static Date incrementDateByMonth(Date date, int month) {
        if (date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month == 0 ? 1 : month);
        return calendar.getTime();
    }

    public static Integer[] generateMonthCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Integer[] result = null;
        if (dayOfWeek + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 <= 35) {
            result = new Integer[35];
        }else{
            result = new Integer[42];
        }
        int lastIndex = dayOfWeek + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
        for(int i = dayOfWeek -1 ;i< lastIndex;i++){
            result[i] = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }

    /**
     * 判断时间差
     */
    public static String differTime(Date now, Date create){
        String tips = "";
        long diff = now.getTime() - create.getTime();
        long day = diff/(24*60*60*1000);
        long hour = (diff/(60*60*1000)-day*24);
        long min = ((diff/(60*1000))-day*24*60-hour*60);

        if (day == 0 && hour == 0 && min < 2){
            tips = "刚刚";
        }else if (day == 0 && hour == 0 && min >= 2){
            tips = min + "分钟前";
        }else if (day == 0 && hour >= 1){
            tips = hour + "小时前";
        }else{
            tips = day + "天前";
        }

        return tips;
    }

    /**
     * 判断时间差
     */
    public static int[] differTime(String create, int seconds) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(LONG_DATE_FORMAT);
        long diff = formatter.parse(create).getTime() + seconds*1000 - new Date().getTime();
        long day = diff/(24*60*60*1000);
        long hour = (diff/(60*60*1000)-day*24);
        long min = ((diff/(60*1000))-day*24*60-hour*60);
        long second = diff/1000 - day*24*60 - hour*60 - min*60;

        return new int[]{(int) min, (int) second};
    }

    /**
     * 判断时间大小
     * @return
     */
    public static boolean checkTime(String s1, String s2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            c1.setTime(df.parse(s1));
            c2.setTime(df.parse(s2));
        } catch (ParseException e) {
            Log.e("DateUtil", "格式不正确");
        }

        int result = c1.compareTo(c2);
        if (result <= 0) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断同一天
     * @return
     */
    public static boolean isSameDay(String s1, String s2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            c1.setTime(df.parse(s1));
            c2.setTime(df.parse(s2));
        } catch (ParseException e) {
            Log.e("DateUtil", "格式不正确");
        }

        int result = c1.compareTo(c2);
        if (result == 0) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 根据日期获取星期几
     * String pTime = "2012-03-12";
     * @param date
     * @return
     */
    public static String getStringWeek(Date date) {
        String Week = "";
        String pTime = dateToShortString(date);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "星期六";
        }

        return Week;
    }
}
