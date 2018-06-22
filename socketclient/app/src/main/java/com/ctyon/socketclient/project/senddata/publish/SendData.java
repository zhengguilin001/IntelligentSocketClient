package com.ctyon.socketclient.project.senddata.publish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.app.network.NetUtil;
import com.wx.common.support.utils.lang.PreferencesUtils;
import com.xmaihh.phone.project.model.Modem;
import com.xmaihh.phone.support.hardware.DeviceUtils;
import com.xmaihh.phone.support.hardware.Modem2Utils;
import com.xmaihh.phone.support.hardware.PowerUtils;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.sql.Time;
import java.util.Random;

import me.xmai.global.config.Constants;

/**
 * Created by Administrator on 2018/3/14.
 */

public class SendData implements ISendable {
    private static final String TAG = SendData.class.getSimpleName();
    private String str = "";
    private int type;

    public SendData(int type1, String ... args) {
        JSONObject jsonObject = new JSONObject();
        Random random = new Random();
        type = type1;
        switch (type) {
            case Constants.COMMON.TYPE.TYPE_LOGIN:
                // 构造登录请求
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    jsonObject.put(Constants.MODEL.DATA.DATA_VERSION, DeviceUtils.getLocalVersion(App.getsContext()));
                    String imei = DeviceUtils.getIMEI(App.getsContext());
                    if (imei.startsWith("0")&&imei.endsWith("0")){
                        imei = DeviceUtils.getIMSI(App.getsContext());
                    }
                    //记得改回去
                    jsonObject.put(Constants.MODEL.DATA.DATA_IMEI, imei);
                    //jsonObject.put(Constants.MODEL.DATA.DATA_IMEI, "C5B20180200030");
                    //jsonObject.put(Constants.MODEL.DATA.DATA_IMSI, "460110869360288");
                    jsonObject.put(Constants.MODEL.DATA.DATA_IMSI, DeviceUtils.getIMSI(App.getsContext()));
                    jsonObject.put(Constants.MODEL.DATA.DATA_MODULES, DeviceUtils.hasModuleSupport(App.getsContext()));
                    jsonObject.put(Constants.MODEL.DATA.DATA_HEARTBEAT, 60);
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_LOGIN);
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace(


                    );
                }
                break;
            case Constants.COMMON.TYPE.TYPE_PULSE_C:
                //构造终端发送心跳
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_PULSE_C);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_CLOCK_SYNC:
                //构造请求时间同步
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_CLOCK_SYNC);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    JSONArray jsonArray = new JSONArray();
                    JSONObject j1 = new JSONObject();
                    Modem m = Modem2Utils.getModemCell(App.getsContext());
                    j1.put(Constants.MODEL.DATA.DATA_MCC, m.getMcc());
                    j1.put(Constants.MODEL.DATA.DATA_MNC, m.getMnc());
                    j1.put(Constants.MODEL.DATA.DATA_LAC, m.getLac());
                    j1.put(Constants.MODEL.DATA.DATA_CI, m.getCi());
                    j1.put(Constants.MODEL.DATA.DATA_RXLEV, m.getRxlev());
                    jsonArray.put(j1);
                    jsonObject.put(Constants.MODEL.DATA.DATA_CELLS, jsonArray);
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_NEW_MESSAGE:
                //构造查询新消息
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_NEW_MESSAGE);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    jsonObject.put("last_id", "5ab49367c493a9055a9344dc");
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_GET_WEATHER:
                //构造查询所在地天气
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_GET_WEATHER);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    JSONArray jsonArray = new JSONArray();
                    JSONObject j1 = new JSONObject();
                    Modem m = Modem2Utils.getModemCell(App.getsContext());
                    j1.put(Constants.MODEL.DATA.DATA_MCC, m.getMcc());
                    j1.put(Constants.MODEL.DATA.DATA_MNC, m.getMnc());
                    j1.put(Constants.MODEL.DATA.DATA_LAC, m.getLac());
                    j1.put(Constants.MODEL.DATA.DATA_CI, m.getCi());
                    j1.put(Constants.MODEL.DATA.DATA_RXLEV, m.getRxlev());
                    jsonArray.put(j1);
                    jsonObject.put(Constants.MODEL.DATA.DATA_CELLS, jsonArray);
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_ELECTRICITY:
                //构造电量上传
                try {
                    int value = (int)(PowerUtils.getBattery(App.getsContext())*100);
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,
                            Constants.COMMON.TYPE.TYPE_ELECTRICITY);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    jsonObject.put(Constants.MODEL.DATA.DATA_BATTERY,
                            (int)(PowerUtils.getBattery(App.getsContext())*100));
                    jsonObject.put(Constants.MODEL.DATA.DATA_MAX_LEVEL, 4);
                    jsonObject.put(Constants.MODEL.DATA.DATA_CURR_LEVEL, getLevel(value));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_STEP_COUNT:
                //构造上传计步
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_STEP_COUNT);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    jsonObject.put(Constants.MODEL.DATA.DATA_STEP, 1256800);
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_PULSE_S:
                //构造返回服务端测试连接心跳
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_PULSE_S);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_LOCATION_S:
                //构造返回服务器请求定位响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_LOCATION_S);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_LOCATION_C:
                //构造上传定位
                try {
                    long timeStampSec = System.currentTimeMillis()/1000;
                    String timestamp = String.format("%010d", timeStampSec);

                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_LOCATION_C);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, random.nextInt(999999));
                    //gps
                    if (args != null && args.length == 2) {
                        JSONObject jgps = new JSONObject();
                        /*jgps.put(Constants.MODEL.DATA.DATA_LON, PreferencesUtils.getFloat(App.getsContext(),
                                Constants.MODEL.PRENAME.PRE_AMPLON, 113));
                        jgps.put(Constants.MODEL.DATA.DATA_LAT, PreferencesUtils.getFloat(App.getsContext(),
                                Constants.MODEL.PRENAME.PRE_AMPLAT, 22));*/
                        jgps.put(Constants.MODEL.DATA.DATA_LON, args[0]);
                        jgps.put(Constants.MODEL.DATA.DATA_LAT, args[1]);
                        //jgps.put(Constants.MODEL.DATA.DATA_LON, "113.917787");
                        //jgps.put(Constants.MODEL.DATA.DATA_LAT, "22.576198");
                        jgps.put(Constants.MODEL.DATA.DATA_HEIGHT, 0);
                        jgps.put(Constants.MODEL.DATA.DATA_SPEED, 0);
                        jgps.put(Constants.MODEL.DATA.DATA_DIRECTION, 0);
                        jsonObject.put(Constants.MODEL.DATA.DATA_GPS, jgps);
                    }
                    //cells
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jm = new JSONObject();
                    Modem m = Modem2Utils.getModemCell(App.getsContext());
                    jm.put(Constants.MODEL.DATA.DATA_MCC, m.getMcc());
                    jm.put(Constants.MODEL.DATA.DATA_MNC, m.getMnc());
                    jm.put(Constants.MODEL.DATA.DATA_LAC, m.getLac());
                    jm.put(Constants.MODEL.DATA.DATA_CI, m.getCi());
                    jm.put(Constants.MODEL.DATA.DATA_RXLEV, m.getRxlev());
                    jsonArray.put(jm);
                    jsonObject.put(Constants.MODEL.DATA.DATA_CELLS, jsonArray);

                    //wifis
                    if (args != null && args.length > 2) {
                        JSONArray wifiJsonArray = new JSONArray();
                        JSONObject wifiJsonObj1 = new JSONObject();
                        //wifiJsonObj.put("mac", "02:00:00:00:00:00");
                        //wifiJsonObj.put("signal", "-51");
                        //wifiJsonObj.put("ssid", "ctyon-test22");
                        wifiJsonObj1.put("mac", args[0]);
                        wifiJsonObj1.put("signal", args[1]);
                        wifiJsonObj1.put("ssid", args[2]);
                        wifiJsonArray.put(wifiJsonObj1);

                        JSONObject wifiJsonObj2 = new JSONObject();
                        //wifiJsonObj.put("mac", "02:00:00:00:00:00");
                        //wifiJsonObj.put("signal", "-51");
                        //wifiJsonObj.put("ssid", "ctyon-test22");
                        wifiJsonObj2.put("mac", args[3]);
                        wifiJsonObj2.put("signal", args[4]);
                        wifiJsonObj2.put("ssid", args[5]);
                        wifiJsonArray.put(wifiJsonObj2);

                        JSONObject wifiJsonObj3 = new JSONObject();
                        //wifiJsonObj.put("mac", "02:00:00:00:00:00");
                        //wifiJsonObj.put("signal", "-51");
                        //wifiJsonObj.put("ssid", "ctyon-test22");
                        wifiJsonObj3.put("mac", args[6]);
                        wifiJsonObj3.put("signal", args[7]);
                        wifiJsonObj3.put("ssid", args[8]);
                        wifiJsonArray.put(wifiJsonObj3);

                        jsonObject.put("wifis", wifiJsonArray);
                    }
                    jsonObject.put(Constants.MODEL.DATA.DATA_TIMESTAMP, Float.valueOf(timestamp));
                    jsonObject.put(Constants.MODEL.DATA.DATA_SOS,
                            PreferencesUtils.getInt(App.getsContext(), Constants.MODEL.PRENAME.PRE_SOS, 0));
                    jsonObject.put(Constants.MODEL.DATA.DATA_FEEDBACK,
                            1);
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_LOCK_S:
                //构造服务器请求定位响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_LOCK_S);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_SET_CONTACT_S:
                //构造通讯录号码设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_SET_CONTACT_S);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_SET_SOS_NUMBER:
                //构造SOS号码设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_SET_SOS_NUMBER);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE:
                //构造新消息提醒（语音）设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_SET_VOLUME:
                //构造音量级别设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_SET_VOLUME);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_SHUT_DOWN:
                //构造远程关机响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_SHUT_DOWN);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_WATCH_MACTION:
                //构造监听设备响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_WATCH_MACTION);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_NO_DISTURB:
                //构造免打扰时间段设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_NO_DISTURB);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_TIMER_REMIND:
                //构造免打扰时间段设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_TIMER_REMIND);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_TEXT_MESSAGE:
                //构造新消息提醒(文字)响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_TEXT_MESSAGE);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_TIMER_LOCK:
                //构造设置终端定时上传定位响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_TIMER_LOCK);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_CALL_STRATEGY:
                //构造拒绝陌生人来电设置响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_CALL_STRATEGY);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
                case Constants.COMMON.TYPE.TYPE_MASTER_CLEAR:
                    //构造恢复手表出厂设置响应
                    try {
                        jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_MASTER_CLEAR);
                        jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                        str = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            case Constants.COMMON.TYPE.TYPE_DEVICE_TOKEN:
                //构造设备验证的响应
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_DEVICE_TOKEN);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,Integer.parseInt(args[0]));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.COMMON.TYPE.TYPE_MASTER_UNTIE:
                //构造解绑数据
                try {
                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE,Constants.COMMON.TYPE.TYPE_MASTER_UNTIE);
                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT,random.nextInt(999999));
                    str = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
//                try {
//                    jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_PULSE_C);
//                    jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, 456873);
//                    str = jsonObject.toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

    @Override
    public byte[] parse() {
        //根据服务器的解析规则,构建byte数组
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putShort((short) type);
        bb.putShort((short) body.length);
        Log.d(TAG, "parse: " + (short) body.length);
        bb.put(body);
        return bb.array();
    }

    //add by shipeixian for get level begin
    private int getLevel(int value) {
        if (value > 0 && value <= 25) {
            return 0;
        } else if (value > 25 && value <= 50) {
            return 1;
        } else if (value > 50 && value <= 75) {
            return 2;
        } else if (value > 75 && value <= 100) {
            return 3;
        }
        return 2;
    }
    //add by shipeixian for get level end
}
