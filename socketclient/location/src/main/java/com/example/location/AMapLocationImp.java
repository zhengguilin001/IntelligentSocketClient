package com.example.location;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wx.common.support.utils.lang.PreferencesUtils;

import java.util.HashMap;
import java.util.Map;

import me.xmai.global.config.Constants;

/**
 * Created by Administrator on 2018/3/13.
 */

public class AMapLocationImp {
    //private static final String TAG = AMapLocationImp.class.getSimpleName();
    private static final String TAG = "shipeixian";
    //声明AMapLocationClient类对象
    private static AMapLocationClient mLocationClient = null;
    private static Context mContext;
    private static Handler mHandler;
    //声明定位回调监听器
    static AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (aMapLocation.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + aMapLocation.getLocationType() + "\n");
                    sb.append("经    度    : " + aMapLocation.getLongitude() + "\n");
                    sb.append("纬    度    : " + aMapLocation.getLatitude() + "\n");
                    sb.append("精    度    : " + aMapLocation.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + aMapLocation.getProvider() + "\n");

                    sb.append("速    度    : " + aMapLocation.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + aMapLocation.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + aMapLocation.getSatellites() + "\n");
                    sb.append("国    家    : " + aMapLocation.getCountry() + "\n");
                    sb.append("省            : " + aMapLocation.getProvince() + "\n");
                    sb.append("市            : " + aMapLocation.getCity() + "\n");
                    sb.append("城市编码 : " + aMapLocation.getCityCode() + "\n");
                    sb.append("区            : " + aMapLocation.getDistrict() + "\n");
                    sb.append("区域 码   : " + aMapLocation.getAdCode() + "\n");
                    sb.append("地    址    : " + aMapLocation.getAddress() + "\n");
                    sb.append("兴趣点    : " + aMapLocation.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + LocationUtils.formatUTC(aMapLocation.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + aMapLocation.getErrorCode() + "\n");
                    sb.append("错误信息:" + aMapLocation.getErrorInfo() + "\n");
                    sb.append("错误描述:" + aMapLocation.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(aMapLocation.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(LocationUtils.getGPSStatusString(aMapLocation.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(aMapLocation.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + LocationUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
                Log.d(TAG, "定位成功: " + result);
                mLocationClient.stopLocation();
                PreferencesUtils.putString(mContext, Constants.MODEL.PRENAME.PRE_AMP, result);
                PreferencesUtils.putString(mContext, Constants.MODEL.PRENAME.PRE_AMPLON, aMapLocation.getLongitude() + "");
                PreferencesUtils.putString(mContext, Constants.MODEL.PRENAME.PRE_AMPLAT, aMapLocation.getLatitude() + "");
                //mHandler.obtainMessage(Constants.COMMON.MSG.MSG_LOCATION_SUC).sendToTarget();
                String[] gpsData = gcj2wgs(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                Log.d(TAG, "gcj2转换为gps坐标: 经度=" + gpsData[0]+",纬度="+gpsData[1]);
                Message message = new Message();
                message.obj = gpsData[0] + "/" + gpsData[1];
                //message.obj = aMapLocation.getLongitude() + "/" + aMapLocation.getLatitude();
                message.what = 4400;
                mHandler.sendMessage(message);
                //mHandler.obtainMessage(4400).sendToTarget();
                Settings.Global.putString(mContext.getContentResolver(),
                        Constants.MODEL.SETTINGS.GLOBAL_CITY, aMapLocation.getCity());
            } else {
                mLocationClient.stopLocation();
                Log.d(TAG, "定位失败，loc is null");
                Message message = new Message();
                message.what = 4400;
                mHandler.sendMessage(message);
                //mHandler.obtainMessage(Constants.COMMON.MSG.MSG_LOCATION_SUC).sendToTarget();
            }
            try {
                //关闭wifi
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);
                if (!wifiManager.isWifiEnabled()) {
                    Log.i(TAG, "wifi是关闭的");
                }
            } catch (Exception e) {

            }
        }
    };

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private static AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    public synchronized static AMapLocationClient buildLocationClient(Context context, Handler handler) {
        if (mLocationClient == null) {
            //初始化client
            mLocationClient = new AMapLocationClient(context.getApplicationContext());
            //设置定位参数
            mLocationClient.setLocationOption(getDefaultOption());
            //设置定位监听
            mLocationClient.setLocationListener(mLocationListener);
        }
        mContext = context.getApplicationContext();
        mHandler = handler;
        return mLocationClient;
    }


    private void initLocation(Context context) {
        //初始化client
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
    }


    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void startLocation() {
        // 设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void stopLocation() {
        // 停止定位
        mLocationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void destroyLocation() {
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private static double pi = 3.14159265358979324D;// 圆周率
    private static double a = 6378245.0D;// WGS 长轴半径
    private static double ee = 0.00669342162296594323D;// WGS 偏心率的平方

    /**
     * 中国坐标内
     *
     * @param lat
     * @param lon
     * @return
     */
    public static boolean outofChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    // 84->gcj02
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static Map<String, Double> transform(double lon, double lat) {
        HashMap<String, Double> localHashMap = new HashMap<String, Double>();
        if (outofChina(lat, lon)) {
            localHashMap.put("lon", Double.valueOf(lon));
            localHashMap.put("lat", Double.valueOf(lat));
            return localHashMap;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        localHashMap.put("lon", mgLon);
        localHashMap.put("lat", mgLat);
        return localHashMap;
    }

    // gcj02-84
    public static String[] gcj2wgs(double lon, double lat) {
        String[] gpsData = new String[2];
        //Map<String, Double> localHashMap = new HashMap<String, Double>();
        double lontitude = lon
                - (((Double) transform(lon, lat).get("lon")).doubleValue() - lon);
        double latitude = (lat - (((Double) (transform(lon, lat))
                .get("lat")).doubleValue() - lat));
        //localHashMap.put("lon", lontitude);
        //localHashMap.put("lat", latitude);
        gpsData[0] = lontitude+"";
        gpsData[1] = latitude+"";
        return gpsData;
    }

}
