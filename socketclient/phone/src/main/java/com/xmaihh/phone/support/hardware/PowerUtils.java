package com.xmaihh.phone.support.hardware;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/3/13.
 */

public class PowerUtils {
    public static final String TAG = PowerUtils.class.getSimpleName();

    /**
     * 关机操作
     */
    public static void shutdown() {
        try {
            //获得ServiceManager类
            Class<?> ServiceManager = Class
                    .forName("android.os.ServiceManager");
            //获得ServiceManager的getService方法
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
            //调用getService获取RemoteService
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
            //获得IPowerManager.Stub类
            Class<?> cStub = Class
                    .forName("android.os.IPowerManager$Stub");
            //获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            //调用asInterface方法获取IPowerManager对象
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            //获得shutdown()方法
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
            //调用shutdown()方法
            shutdown.invoke(oIPowerManager, false, true);
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * 获取电池电量,0~1
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unused")
    public static float getBattery(Context context) {
        Intent batteryInfoIntent = context.getApplicationContext()
                .registerReceiver(null,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryInfoIntent.getIntExtra("status", 0);
        int health = batteryInfoIntent.getIntExtra("health", 1);
        boolean present = batteryInfoIntent.getBooleanExtra("present", false);
        int level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 0);
        int plugged = batteryInfoIntent.getIntExtra("plugged", 0);
        int voltage = batteryInfoIntent.getIntExtra("voltage", 0);
        int temperature = batteryInfoIntent.getIntExtra("temperature", 0); // 温度的单位是10�?
        String technology = batteryInfoIntent.getStringExtra("technology");
        return ((float) level) / scale;
    }

    public static void wipeAlldata(Context ctx){
//        Intent intent = new Intent(Intent.ACTION_MASTER_CLEAR);
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
//        intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, true);//清除数据
//        ctx.sendBroadcast(intent);
        Intent intent=new Intent(Settings.ACTION_PRIVACY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
