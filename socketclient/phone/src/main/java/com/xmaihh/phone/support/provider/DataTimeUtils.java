package com.xmaihh.phone.support.provider;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by xmai on 18-3-28.
 * 设置时间相关类
 */

public class DataTimeUtils {
    /**
     * 判断系统使用的是24小时制还是12小时制
     */
    public boolean is24Hour(Context ctx) {

        return DateFormat.is24HourFormat(ctx);
    }

    /**
     * 设置系统的小时制
     * 24小时制
     **/
    public void set24Hour(Context ctx) {


        android.provider.Settings.System.putString(ctx.getContentResolver(),
                android.provider.Settings.System.TIME_12_24, "24");
    }

    /**
     * 12小时制
     **/
    public void set12Hour(Context ctx) {


        android.provider.Settings.System.putString(ctx.getContentResolver(),
                android.provider.Settings.System.TIME_12_24,"12");
    }
    /**判断系统的时区是否是自动获取的**/

    public boolean isTimeZoneAuto(Context ctx){
        try {
            return  android.provider.Settings.Global.getInt(ctx.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**设置系统的时区是否自动获取*/

    public void setAutoTimeZone(Context ctx,int checked){
        android.provider.Settings.Global.putInt(ctx.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
    }
    /**判断系统的时间是否自动获取的**/

    public boolean isDateTimeAuto(Context ctx){
        try {
            return android.provider.Settings.Global.getInt(ctx.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**设置系统的时间是否需要自动获取**/

    public void setAutoDateTime(Context ctx,int checked){
        android.provider.Settings.Global.putInt(ctx.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME, checked);
    }
    /**设置系统日期

     参考系统Settings中的源码**/

    public void setSysDate(Context ctx,int year,int month,int day){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
    /**设置系统时间

     参考系统Settings中的源码
     **/
    public void setSysTime(Context ctx,int hour,int minute){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
    /**设置系统时区**/

    public void setTimeZone(String timeZone){
        final Calendar now = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        now.setTimeZone(tz);
    }
    /**获取系统当前的时区**/

    public String getDefaultTimeZone(){
        return TimeZone.getDefault().getDisplayName();
    }
}
