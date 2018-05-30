package com.xmaihh.phone.support.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Administrator on 2018/3/13.
 */

public class PhoneUtils {
    /**
     * 发送短信，需要SEND_SMS权限
     *
     * @param context        上下文
     * @param number         电话号码
     * @param messageContent 短信内容，如果长度过长将会发多条发送
     */
    public static void sendSms(Context context, String number, String messageContent) {
        SmsManager smsManager = SmsManager.getDefault();
        List<String> contentList = smsManager.divideMessage(messageContent);
        for (String content : contentList) {
            smsManager.sendTextMessage(number, null, content, null, null);
        }
    }

    /**
     * 获取所有联系人的姓名和电话号码，需要READ_CONTACTS权限
     *
     * @param context 上下文
     * @return Cursor。姓名：CommonDataKinds.Phone.DISPLAY_NAME；号码：CommonDataKinds.Phone.NUMBER
     */
    public static Cursor getContactsNameAndNumber(Context context) {
        return context.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI, new String[]{
                CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NUMBER}, null, null, CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    /**
     * 获取手机号码
     *
     * @param context 上下文
     * @return 手机号码，手机号码不一定能获取到
     */
    @SuppressLint("MissingPermission")
    public static String getMobilePhoneNumber(Context context) {
        return ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getLine1Number();
    }

}
