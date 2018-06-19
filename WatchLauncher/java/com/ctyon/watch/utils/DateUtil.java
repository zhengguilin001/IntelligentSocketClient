package com.ctyon.watch.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/29.
 */

public class DateUtil {

    public static long getAlarmDateFormat(String time){
        String[] split = time.split(":");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        return h*60*60*1000+m*60*1000;
    }
    public static String getHour(String time){
        String[] split = time.split(":");
        if(split != null)
            return split[0];
        return "";
    }
    public static String getMinute(String time){
        String[] split = time.split(":");
        if(split != null)
            return split[1];
        return "";
    }
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
    public static String get24HourTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String curTime = hour+":"+(minute < 10 ? "0"+minute:minute);
        return curTime;
    }
}
