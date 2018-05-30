package com.ctyon.socketclient.project.util;

import android.content.ContentResolver;
import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * GPS类，用来判断GPS是否开启，切换GPS状态
 * 在 AndroidManifest.xml中需要添加权限：
 * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 * <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
 * 在 AndroidManifest.xml中添加系统权限： android:sharedUserId="android.uid.system"
 * 例如：
 * <manifest xmlns:android="http://schemas.android.com/apk/res/android"
 * android:sharedUserId="android.uid.system" >
 *
 * @author ljp 2014-1-11
 */
public class GpsTool {
    /**
     * Gets the state of GpsTool location.
     *
     * @param context
     * @return true if enabled.
     */
    public static boolean getGpsState(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean open = Settings.Secure.isLocationProviderEnabled(resolver,
                LocationManager.GPS_PROVIDER);
        System.out.println("getGpsState:" + open);
        return open;
    }

    /**
     * Toggles the state of GpsTool.
     *
     * @param context
     */
    public static void toggleGps(Context context, boolean enable) {
        ContentResolver resolver = context.getContentResolver();
        //boolean enabled = getGpsState(context);
        Settings.Secure.setLocationProviderEnabled(resolver,
                LocationManager.GPS_PROVIDER, enable);
    }


}