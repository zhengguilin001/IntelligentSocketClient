package com.ctyon.watch.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.ctyon.watch.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zx
 * On 2017/12/23
 */

public class ContactsUtil {
    /***
     * 判断有无SIM卡
     * @return
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        Log.d("ContactsUtil", result ? "有SIM卡" : "无SIM卡");
        return result;
    }

    /***
     * 判断系统时间是否是24小时制
     * @return
     */
    public static boolean is24Format(Context context){
        //获得系统时间制
        String timeFormat = android.provider.Settings.System.getString(context.getContentResolver(),android.provider.Settings.System.TIME_12_24);
        //判断时间制
        return timeFormat.equals("24");
    }

    /***
     * 判断系统语言
     * @return
     */
    public static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        if ("zh".equals(language)) {
            language = "zh";
        } else if ("en".equals(language)) {
            language = "en";
        }
        return language;
    }

    //格式化日期
    public static String parserDate(long date, Context context) {
        //系统当前时间，年月日
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH)+1;
        int cDay = calendar.get(Calendar.DATE);
        //Log.e("TAG",cYear + "-"+cMonth +"-"+cDay);

        //格式化时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        String callDate = format.format(date);
        int year = Integer.parseInt(callDate.substring(0,4));
        int month = Integer.parseInt(callDate.substring(5,7));
        int day = Integer.parseInt(callDate.substring(8,10));
        if (cYear != year){
            return format.format(date);
        }else if (cMonth != month || cDay != day){
            SimpleDateFormat format2 = new SimpleDateFormat("MM/dd", Locale.CHINA);
            return format2.format(date);
        }else if (is24Format(context)){
            SimpleDateFormat format3 = new SimpleDateFormat("H:mm", Locale.CHINA);
            return format3.format(date);
        }else {
            int hour = Integer.parseInt(new SimpleDateFormat("H", Locale.CHINA).format(date));
            if(hour < 13){
                //AM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (getLanguageEnv().equals("zh")){
                    return context.getString(R.string.am)+format3.format(date);
                }else {
                    return format3.format(date)+ context.getString(R.string.am);
                }
            }else {
                //PM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (getLanguageEnv().equals("zh")){
                    return context.getString(R.string.pm)+format3.format(date);
                }else {
                    return format3.format(date)+ context.getString(R.string.pm);
                }
            }
        }

    }

    /***
     * 将date转为时间 在短信中调用
     * @param date
     * @return
     */
    public static String convertToTime(long date, Context context){
        if (ContactsUtil.is24Format(context)){
            SimpleDateFormat format3 = new SimpleDateFormat("H:mm", Locale.CHINA);
            return format3.format(date);
        }else {
            int hour = Integer.parseInt(new SimpleDateFormat("H", Locale.CHINA).format(date));
            if(hour < 13){
                //AM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (ContactsUtil.getLanguageEnv().equals("zh")){
                    return context.getString(R.string.am)+format3.format(date);
                }else {
                    return format3.format(date)+ context.getString(R.string.am);
                }
            }else {
                //PM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (ContactsUtil.getLanguageEnv().equals("zh")){
                    return context.getString(R.string.pm)+format3.format(date);
                }else {
                    return format3.format(date)+ context.getString(R.string.pm);
                }
            }
        }
    }

    /***
     * 将date转为年月日
     * @param date
     * @return
     */
    public static String convertToDate(long date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(date);
    }

    /***
     * 通话记录号码是否保存
     * @param number 号码
     * @return 如果保存返回保存的名字否则返回空
     */
    public static String getName(String number, Context context){
        String name = null;
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup/" + number);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndex("display_name"));
            }
            cursor.close();
        }
        return name;
    }

}
