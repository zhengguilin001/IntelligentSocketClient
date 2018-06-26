package com.ctyon.socketclient.app.server;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.BuildConfig;
import com.ctyon.socketclient.R;
import com.ctyon.socketclient.app.activity.ConfirmUnbindActivity;
import com.ctyon.socketclient.app.activity.ShowImedActivity;
import com.ctyon.socketclient.app.activity.ShowTokenActivity;
import com.ctyon.socketclient.app.activity.XCameraActivity;
import com.ctyon.socketclient.app.location.GPSLocationListener;
import com.ctyon.socketclient.app.location.GPSLocationManager;
import com.ctyon.socketclient.app.location.GPSProviderStatus;
import com.ctyon.socketclient.app.network.NetBroadcastReceiver;
import com.ctyon.socketclient.app.network.NetEvent;
import com.ctyon.socketclient.app.network.NetUtil;
import com.ctyon.socketclient.app.receive.ScreenReceiver;
import com.ctyon.socketclient.project.model.AlarmModel;
import com.ctyon.socketclient.project.model.ContactJson;
import com.ctyon.socketclient.project.model.ForbidUseModel;
import com.ctyon.socketclient.project.senddata.RedirectException;
import com.ctyon.socketclient.project.senddata.publish.PulseData;
import com.ctyon.socketclient.project.senddata.publish.SendData;
import com.ctyon.socketclient.project.support.MyHeaderProtocol;
import com.ctyon.socketclient.project.support.observer.SettingsObserver;
import com.ctyon.socketclient.project.util.DateUtil;
import com.ctyon.socketclient.project.util.GpsTool;
import com.ctyon.socketclient.project.util.IToast;
import com.example.location.AMapLocationImp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.sdsmdg.tastytoast.TastyToast;
import com.wx.common.support.utils.FileUtils;
import com.wx.common.support.utils.SafeHandler;
import com.wx.common.support.utils.timer.RxTimeUtil;
import com.wx.common.support.utils.timer.TimeUtils;
import com.xmaihh.phone.support.hardware.DeviceUtils;
import com.xmaihh.phone.support.hardware.PowerUtils;
import com.xmaihh.phone.support.provider.SettingsUtils;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.example.wxchat.support.model.WeChatMessage;
import me.example.wxchat.support.model.WxchatMessageBean;
import me.example.wxchat.support.persistence.DbUtils;
import me.xmai.global.config.Constants;

public class SocketService extends Service implements SafeHandler.HandlerContainer, NetEvent {
    //private static final String TAG = SocketService.class.getSimpleName();
    private static final String TAG = "shipeixian";
    private OkSocketOptions mOkOptions;
    private IConnectionManager mManager;
    private ConnectionInfo info;
    private SafeHandler<SocketService> mHandler;
    private SettingsObserver mPicpathObserver;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    String file = "/weather.json";

    private int cycleLocationTime = 0;

    private int ident = 999999;

    //add by shipeixian on 2018-05-24 begin
    private boolean isWaitingServerResponse = false;
    private boolean isGpsLocatioinSuccess = false;
    private int connectFailedCount = 0;
    private int resendCodeCount = 0;
    private LocationManager locationManager;
    private double longtitude = 0;
    private double lantitude = 0;
    private String wifiListInfo = "";

    private long stealPhotoTime = 0;
    private long stealCallTime = 0;
    private long uploadLocationTime = 0;

    private ActivityManager activityManager;

    private MediaPlayer mediaPlayer;

    private String addContactDataStr = "";

    private LocationManager locationmanager;

    private GPSLocationManager gpsLocationManager;


    /**
     * 监控网络的广播
     */
    private NetBroadcastReceiver netBroadcastReceiver;

    /**
     * 监控亮灭屏的广播
     */
    private ScreenReceiver screenReceiver;

    //add by shipeixian on 2018-05-24 end


    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "SocketService oncreate() begin");
        if (isApplicationDoubleRunning("com.ctyon.socketclient")) {
            Log.i(TAG, "SocketService重复运行 stopself "+Process.myPid());
            stopSelf();
            return;
        }
        mHandler = new SafeHandler<SocketService>(this);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //registerContentObserver();

        //add by shipeixian on 2018-05-24 begin
        registerReceivers();
        //add by shipeixian on 2018-05-24 end

        try {
            //关闭GPS
            //GpsTool.toggleGps(getApplicationContext(), false);
            //关闭wifi
            /*WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
            if (!wifiManager.isWifiEnabled()) {
                Log.i(TAG, "wifi是关闭的");
            }*/
            //还未登录，取消登录通知图标
            cancelNotification();
        } catch (Exception e) {

        }

        //初始化LocationManager
        initGpsService();

        Log.i(TAG, "SocketService oncreate(...) end");

    }

    private void registerContentObserver() {
        //注册Settings.Global的监听
        mPicpathObserver = new SettingsObserver(this, mHandler,
                Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH,
                Constants.COMMON.MSG.MSG_UPLOAD_PHOTO, SettingsObserver.UTYPE.STRING);
        getContentResolver().registerContentObserver(Settings.Global
                        .getUriFor(Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH),
                true, mPicpathObserver);
    }

    private void registerReceivers() {
        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction("com.ctyon.shawn.UPLOAD_PHOTO");
            filter.addAction("unbind_watch");
            filter.addAction("show_imed");
            filter.addAction("com.ctyon.shawn.REALLY_UNBIND_WATCH");
            registerReceiver(netBroadcastReceiver, filter);
            //设置监听
            netBroadcastReceiver.setNetEvent(this);
        }

        if (screenReceiver == null) {
            screenReceiver = new ScreenReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, filter);
        }

    }

    private void resetSocket() {

        cancelNotification();
        //add by shipeixian on 2018-05-24 begin
        if (mHandler != null) {
            mHandler.removeMessages(4400);
            mHandler.removeMessages(4401);
            mHandler.removeMessages(4402);
            mHandler.removeMessages(4403);
            mHandler.removeMessages(4404);
            mHandler.removeMessages(4405);
        }
        //add by shipeixian on 2018-05-24 end

        //没写imei号，则return
        /*String imei = DeviceUtils.getIMEI(App.getsContext());
        if (imei.startsWith("0")&&imei.endsWith("0")){
            return;
        }*/
        //如果没有网络,return
        if (NetUtil.getNetWorkState(getApplicationContext()) == -1) {
            return;
        }
        //判断网络信号强度,如果没有信号return
        /*try {
            if (Settings.Global.getInt(getContentResolver(), "socket_signal_level", 0) != 1) {
                mHandler.sendEmptyMessageDelayed(4405, 10000);
                return;
            }
        } catch (Exception e) {

        }*/

        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        info = new ConnectionInfo(BuildConfig.HOST, BuildConfig.PORT);
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        mManager = OkSocket.open(info, OkSocketOptions.getDefault());
        //获得当前连接通道的参配对象
        mOkOptions = mManager.getOption();
        //基于当前参配对象构建一个参配建造者类
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(mOkOptions);
        //修改参配设置
        builder.setPulseFrequency(60 * 1000); // 默认心跳 60s
        builder.setHeaderProtocol(new MyHeaderProtocol());
        //建造一个新的参配对象并且付给通道
        mManager.option(builder.build());
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        mManager.registerReceiver(adapter);
        //调用通道进行连接
        mManager.connect();
    }


    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketIOThreadStart(Context context, String action) {
            Log.i(TAG, "onSocketIOThreadStart");
        }

        @Override
        public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
            Log.i(TAG, "onSocketIOThreadShutdown");
        }

        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            int length = data.getBodyBytes().length;
            if (length != 0 && length < 65535) {
//            if (str.length() != 0 && str.length() < 65535) {
                //打印日志
                Log.i(TAG, str.length() + "onSocketReadResponse: " + str);
                isWaitingServerResponse = false;
                resendCodeCount = 0;
                byte[] body = data.getBodyBytes();
                String bodyStr = new String(body);
                JsonElement jsonElement = new JsonParser().parse(bodyStr);
                if (jsonElement instanceof JsonObject) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    int type = jsonObject.get(Constants.MODEL.DATA.DATA_TYPE).getAsInt();
                    ident = jsonObject.get("ident").getAsInt();
                    switch (type) {
                        case Constants.COMMON.TYPE.TYPE_LOGIN:
                            String token = jsonObject.get(Constants.MODEL.DATA.DATA_TOKEN).getAsString();
                            Log.i("shipeixian", "登录成功, token = " + token);
                            //给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
                            mManager.getPulseManager()
                                    .setPulseSendable(new PulseData())
                                    .pulse();
                            //上传电量，服务器要求的
                            mHandler.sendEmptyMessageDelayed(4401, 20 * 1000);
                            //上传定位，服务器要求的
                            mHandler.sendEmptyMessageDelayed(4403, 40 * 1000);
                            //请求天气
                            mHandler.sendEmptyMessageDelayed(4402, 2 * 60 * 1000);
                            //挂通知
                            sendNotification();
                            Settings.Global.putString(getContentResolver(), Constants.MODEL.SETTINGS.GLOBAL_TOKEN, token);
                            //IToast.show("登录成功");
//                            mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_SYNC_CLOCK, 1000);
                            break;
                        case Constants.COMMON.TYPE.TYPE_PULSE_C:
                            if (mManager != null) {   //心跳喂狗操作
                                mManager.getPulseManager().feed();
                            }
                            //IToast.show("终端发送心跳成功");
                            Log.i("shipeixian", "终端发送心跳成功");
                            break;
                        case Constants.COMMON.TYPE.TYPE_CLOCK_SYNC:
                            //IToast.show("时间同步成功");
                            Log.i("shipeixian", "时间同步成功");
                            mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_GET_WEATHER, 5000);
                            break;
                        case Constants.COMMON.TYPE.TYPE_NEW_MESSAGE:
                            break;
                        //服务器主动下发
                        case Constants.COMMON.TYPE.TYPE_PULSE_S:
                            //服务器下发心跳
                            Log.i("shipeixian", "服务器下发心跳");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_PULSE_S, ident + ""));
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_LOCATION_S:
                            //服务器返回定位结果
                            Log.i("shipeixian", "服务器返回定位结果");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_S, ident + ""));
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_LOCATION_C:
                            //终端上传定位
                            Log.i("shipeixian", "终端上传定位");
                            if (mManager != null && mManager.isConnect()) {
                                //mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_LOCK_S:
                            //服务器请求定位
                            Log.i("shipeixian", "服务器请求定位");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCK_S, ident + ""));
                            }
                            /*if (AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).isStarted()) {
                                AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).stopLocation();
                            }
                            AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).startLocation();*/
                            //10秒限制，防止无限下发
                            if (System.currentTimeMillis() - uploadLocationTime > 10000) {
                                startLocationWithNetwork();
                                uploadLocationTime = System.currentTimeMillis();
                            } else {
                                Log.i("shipeixian", "服务器请求定位，10秒前已经上传过，稍后重试");
                            }
                            //IToast.show("服务器请求当前位置");
                            break;
                        case Constants.COMMON.TYPE.TYPE_SET_CONTACT_S:
//                        {"type":16,"ident":463124,"index":0,"last":0,"contact":[{"name":"qqq","phone":"1234","t":1521689556.594874}]}
                            Log.i("shipeixian", "通讯录号码设置");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_SET_CONTACT_S, ident + ""));
                            }
                            Gson gson = new Gson();
                            ContactJson contactJson = gson.fromJson(str, ContactJson.class);
                            List<ContactJson.ContactBean> contactBeans = contactJson.getContact();
                            if (contactBeans != null && contactBeans.size() > 0) {
                                String contactsResult = "";
                                for (ContactJson.ContactBean bean : contactBeans) {
                                    contactsResult += bean.getName()+","+bean.getPhone()+"/";
                                }
                                Intent addContactIntent = new Intent();
                                addContactDataStr += contactsResult;
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        addContactIntent.putExtra("contacts", addContactDataStr);
                                        addContactIntent.setAction("com.ctyon.shawn.ADD_CONTACT");
                                        sendBroadcast(addContactIntent);
                                        addContactDataStr = "";
                                        Log.i(TAG, addContactDataStr);
                                    }
                                }, 2000);
                            } else {
                                Intent addContactIntent = new Intent();
                                addContactDataStr = "";
                                addContactIntent.putExtra("contacts", "");
                                addContactIntent.setAction("com.ctyon.shawn.ADD_CONTACT");
                                sendBroadcast(addContactIntent);
                            }
                            /*int index = jsonObject.get(Constants.MODEL.DATA.DATA_INEDX).getAsInt();
                            int last = jsonObject.get(Constants.MODEL.DATA.DATA_LAST).getAsInt();
                            JsonArray jsonArray = jsonObject.get(Constants.MODEL.DATA.DATA_CONTACT).getAsJsonArray();
                            JsonObject cj = jsonArray.get(index).getAsJsonObject();
                            String cj_name = cj.get(Constants.MODEL.DATA.DATA_NAME).getAsString();
                            String cj_phone = cj.get(Constants.MODEL.DATA.DATA_PHONE).getAsString();
                            //ContactUtils.addContact(App.getsContext(),cj_name,cj_phone);
                            Intent addContactIntent = new Intent();
                            //addContactIntent.putExtra("contact_name", cj_name);
                            //addContactIntent.putExtra("contact_number", cj_phone);
                            addContactIntent.putExtra("contact_json", str);
                            addContactIntent.setAction("com.ctyon.shawn.ADD_CONTACT");
                            sendBroadcast(addContactIntent);
                            //IToast.show("通讯录号码设置");*/
                            break;
                        case Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE:
//                        {"type":10,"ident":226201,"id":"5ab49367c493a9055a9344dc","url":"http://devicetest.iot08.com/static/message/fe801c53132108ecc9781e9e.amr","duration":8,"size":14022}
                            //IToast.show("您有一条新消息(语音)");
                            Log.i("shipeixian", "您有一条新消息(语音)");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE, ident + ""));
                            }
                            //如果不是上课禁用，才播放声音
                            if (!isForbidUse()) {
                                if (mediaPlayer == null) {
                                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
                                    mediaPlayer.start();
                                } else {
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
                                        mediaPlayer.start();
                                    }
                                }
                            }

                            WeChatMessage bean = new WeChatMessage();
                            bean.setMessageType(WxchatMessageBean.MessageType.Voice.ordinal());
                            bean.setMessageSenderType(WxchatMessageBean.MessageSenderType.Parents.ordinal());
                            bean.setMessageReadStatus(WxchatMessageBean.MessageReadStatus.UnRead.ordinal());
                            bean.setSecid(jsonObject.get(Constants.MODEL.DATA.DATA_ID).getAsString());
                            bean.setMessageTime(System.currentTimeMillis());
                            bean.setShowMeaageTime(true);
                            bean.setValue1(jsonObject.get(Constants.MODEL.DATA.DATA_URL).getAsString());
                            bean.setDuringTime(jsonObject.get(Constants.MODEL.DATA.DATA_DURATION).getAsInt());
                            bean.setValue2(jsonObject.get(Constants.MODEL.DATA.DATA_SIZE).getAsInt() + "");
                            DbUtils.insertMsg(App.getsContext(), bean);
                            //如果不是上课禁用，才弹出消息框
                            if (!isForbidUse()) {
                                mHandler.sendEmptyMessage(4406);
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_SET_VOLUME:
//                             {"type":26,"ident":706373,"level":5}
                            //设置音量级别
                            Log.i("shipeixian", "设置音量级别");
                            int volume_level = jsonObject.get(Constants.MODEL.DATA.DATA_LEVEL).getAsInt();
                            if (mManager != null && SettingsUtils.setMediaVolume(App.getsContext(), volume_level * 2)) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_SET_VOLUME, ident + ""));
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_SHUT_DOWN:
                            //远程关机
                            Log.i("shipeixian", "远程关机");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_SHUT_DOWN, ident + ""));
                            }
                            mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_SHUT_DOWN, 5000);
                            break;
                        case Constants.COMMON.TYPE.TYPE_WATCH_MACTION:
                            //监听手机
//                        {"type":34,"ident":381291,"phone":"13678989878"}
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_WATCH_MACTION, ident + ""));
                            }
                            //30秒限制，防止无限下发
                            if (System.currentTimeMillis() - stealCallTime > 30000) {
                                final String phone = jsonObject.get(Constants.MODEL.DATA.DATA_PHONE).getAsString();
                                sendBroadcast(new Intent().setAction("com.ctyon.shawn.STEAL_CALL").putExtra("number", phone));
                                stealCallTime = System.currentTimeMillis();
                                Log.i("shipeixian", "来自" + phone + "的监听");
                            } else {
                                final String phone = jsonObject.get(Constants.MODEL.DATA.DATA_PHONE).getAsString();
                                Log.i("shipeixian", "来自" + phone + "的监听, 30秒前已在拨打监听号码，请稍后重试");
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_SET_SOS_NUMBER:
//                             {"type":20,"ident":523220,"sos":["11122233311"]}
                            //设置sos号码
                            JsonArray sosArray = jsonObject.get(Constants.MODEL.DATA.DATA_SOS).getAsJsonArray();
                            String[] sosNumbers = new String[sosArray.size()];
                            Log.i("shipeixian", "服务器设置紧急联系人" + sosNumbers.toString());
                            //ContactUtils.addContact(App.getsContext(),"sos紧急联系人",sos);
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_SET_SOS_NUMBER, ident + ""));
                            }
                            for (int i = 0; i < sosArray.size(); i++) {
                                sosNumbers[i] = sosArray.get(i).getAsString();
                            }
                            setSosNumber(sosNumbers);
                            //String sos = sosArray.get(0).getAsString();
                            IToast.show("紧急联系人设置成功");
                            break;
                        case Constants.COMMON.TYPE.TYPE_NO_DISTURB:
                            //免打扰时间段设置
                            //IToast.show("上课禁用指令下达");
                            String disturb = jsonObject.toString();
                            Log.i("shipeixian", "上课禁用指令下达:" + disturb);
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_NO_DISTURB, ident + ""));
                            }
                            Settings.Global.putString(getContentResolver(), Constants.MODEL.SETTINGS.GLOBAL_DISTURB, disturb);
                            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchIntent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(launchIntent);
                            break;
                        case Constants.COMMON.TYPE.TYPE_TIMER_REMIND:
                            //定时提醒设置
                            Log.i("shipeixian", "定时提醒设置");
                            String alarm = jsonObject.toString();
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_TIMER_REMIND, ident + ""));
                                //mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_TIMER_REMIND));
                            }
                            Gson gson2 = new Gson();
                            String alarmArgs = "";
                            Intent alarmIntent = new Intent();
                            alarmIntent.setAction("com.ctyon.shawn.SET_ALARM");
                            try {
                                AlarmModel alarmModel = gson2.fromJson(alarm, AlarmModel.class);
                                List<AlarmModel.AlarmBean> alarmBeans = alarmModel.getAlarm();
                                if (alarmBeans == null || alarmBeans.size() == 0) {
                                    Settings.Global.putInt(getContentResolver(), "isAlarmSet", 0);
                                }
                                if (alarmBeans != null) {
                                    if(alarmBeans.size() > 0) {
                                        Settings.Global.putInt(getContentResolver(), "isAlarmSet", 1);
                                    }
                                    for (AlarmModel.AlarmBean itemBean : alarmBeans) {
                                        alarmArgs += itemBean.getStart() + "," + getAlarmCycle(itemBean.getWeek()) + "/";
                                    }
                                }
                                Log.i(TAG, "解析参数 == " + alarmArgs);
                            } catch (Exception e) {
                                Log.i(TAG, "socketclient 闹钟json解析错误：" + e);
                            }
                            alarmIntent.putExtra("alarmArgs", alarmArgs);
                            sendBroadcast(alarmIntent);
                            //Settings.Global.putString(getContentResolver(),Constants.MODEL.SETTINGS.GLOBAL_ALARM,alarmArgs);
                            break;
                        case Constants.COMMON.TYPE.TYPE_TEXT_MESSAGE:
                            //新文字消息
//                            {"type":38,"ident":535087,"id":"5ab492fcc493a9055a9344db","content":"flank an jar"}
                            //IToast.show("您有一条新消息(文字)");
                            Log.i("shipeixian", "您有一条新消息(文字)");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_TEXT_MESSAGE, ident + ""));
                            }
                            //如果不是上课禁用，才播放声音
                            if (!isForbidUse()) {
                                if (mediaPlayer == null) {
                                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
                                    mediaPlayer.start();
                                } else {
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
                                        mediaPlayer.start();
                                    }
                                }
                            }
                            WeChatMessage wcm = new WeChatMessage();
                            wcm.setSecid(jsonObject.get(Constants.MODEL.DATA.DATA_ID).getAsString());
                            wcm.setMessageType(WxchatMessageBean.MessageType.Text.ordinal());
                            wcm.setMessageTime(System.currentTimeMillis());
                            wcm.setMessageText(jsonObject.get(Constants.MODEL.DATA.DATA_CONTENT).getAsString());
                            wcm.setMessageSenderType(WxchatMessageBean.MessageSenderType.Parents.ordinal());
                            wcm.setMessageReadStatus(WxchatMessageBean.MessageReadStatus.UnRead.ordinal());
                            wcm.setShowMeaageTime(true);
                            DbUtils.insertMsg(App.getsContext(), wcm);
                            //如果不是上课禁用，才弹出消息框
                            if (!isForbidUse()) {
                                mHandler.sendEmptyMessage(4406);
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_REMOTE_PHOTO:
                            //远程拍照
                            Log.i("shipeixian", "远程拍照");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_REMOTE_PHOTO, ident + ""));
                            }
                            //记录远程拍照指令下达的次数
                            int photoCodeCount = Settings.Global.getInt(getContentResolver(),"photo_code_count", 0);
                            Settings.Global.putInt(getContentResolver(),"photo_code_count", photoCodeCount+1);
                            //20秒限制，防止无限下发
                            if (System.currentTimeMillis() - stealPhotoTime > 20000) {
                                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_REMOTE_PHOTO).sendToTarget();
                                stealPhotoTime = System.currentTimeMillis();
                            } else {
                                Log.i("shipeixian", "远程拍照， 20秒前已远程拍照");
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_TIMER_LOCK:
                            //设置终端定时上传定位
                            Log.i("shipeixian", "设置终端定时上传定位");
                            int timer = jsonObject.get(Constants.MODEL.DATA.DATA_SECOND).getAsInt();
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_TIMER_LOCK, ident + ""));
                            }
                            Settings.Global.putInt(getContentResolver(),
                                    Constants.MODEL.SETTINGS.GLOBAL_TIMER_LOCK, timer);
                            mHandler.removeMessages(Constants.COMMON.MSG.MSG_TIMER_LOCK);
                            mHandler.obtainMessage(Constants.COMMON.MSG.MSG_TIMER_LOCK).sendToTarget();
                            break;
                        case Constants.COMMON.TYPE.TYPE_CALL_STRATEGY:
                            //拒绝陌生人来电设置
                            //IToast.show("拒绝陌生人来电指令下达");
                            Log.i("shipeixian", "拒绝陌生人来电指令下达");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_CALL_STRATEGY, ident + ""));
                            }
                            int strategy = jsonObject.get("on").getAsInt();
                            Settings.Global.putInt(getContentResolver(),
                                    Constants.MODEL.SETTINGS.GLOBAL_CALLSTRATEGY, strategy);
                            break;
                        case Constants.COMMON.TYPE.TYPE_GET_WEATHER:
                            //查询终端所在地天气信息
                            Log.i("shipeixian", "查询终端所在地天气信息");
                            String weather = jsonObject.get("weather").getAsString();
                            Settings.Global.putString(getContentResolver(),
                                    Constants.MODEL.SETTINGS.GLOBAL_WEATHER, weather);
                            try {
                                FileUtils.saveData(App.getsContext(), weather, "weather.json");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case Constants.COMMON.TYPE.TYPE_MASTER_CLEAR:
                            //恢复手表出厂设置
                            Log.i("shipeixian", "恢复手表出厂设置");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_MASTER_CLEAR, ident + ""));
                            }
                            FileUtils.cleanInternalCache(App.getsContext());
                            FileUtils.deleteFilesByDirectory(new File(Constants.MODEL.CACHEPATH.CACHE));
                            //恢复出厂设置核心代码
                            Intent clearintent = new Intent("android.intent.action.MASTER_CLEAR");
                            clearintent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                            clearintent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
                            clearintent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
                            sendBroadcast(clearintent);
                            // Intent handling is asynchronous -- assume it will happen soon.
                            break;
                        case Constants.COMMON.TYPE.TYPE_DEVICE_TOKEN:
                            //服务器下发设备验证码
                            //IToast.show("恢复出厂设置指令下达");
                            Log.i("shipeixian", "服务器下发设备验证码");
                            String deviceToken = jsonObject.get("token").getAsString();
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_DEVICE_TOKEN, ident + ""));
                            }
                            Settings.Global.putString(getContentResolver(), "socket_client_shawn_token", deviceToken);
                            //startActivity
                            startActivity(new Intent(SocketService.this, ShowTokenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case Constants.COMMON.TYPE.TYPE_MASTER_UNTIE:
                            Log.i("shipeixian", "服务器响应，解绑手表成功");
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.i(TAG, "onSocketWriteResponse: " + str);
            //add by shipeixian for record the response time begin
            try {
                String[] splitString = str.split("\"type\":");
                String[] subSplitString = splitString[1].split(",");
                int typeValue = 0;
                if (subSplitString[0].contains("}")) {
                    String rawStr = subSplitString[0].replace("}", "");
                    typeValue = Integer.parseInt(rawStr);
                } else {
                    typeValue = Integer.parseInt(subSplitString[0]);
                }
                if (typeValue % 2 != 0) {
                    mHandler.removeMessages(4404);
                    isWaitingServerResponse = true;
                    final int typeArgsValue = typeValue;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Message message = mHandler.obtainMessage();
                            message.what = 4404;
                            message.arg1 = typeArgsValue;
                            mHandler.sendMessage(message);
                        }
                    }, 10 * 1000);
                }
                Log.i(TAG, "type = " + typeValue);
            } catch (Exception e) {

            }
            //add by shipeixian for record the response time end
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.i(TAG, "onPulseSend: " + str);

        }


        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            mHandler.removeMessages(4400);
            mHandler.removeMessages(4401);
            mHandler.removeMessages(4402);
            mHandler.removeMessages(4403);
            mHandler.removeMessages(4404);
            mHandler.removeMessages(4405);
            mHandler.removeMessages(4406);
            if (e != null) {
                if (e instanceof RedirectException) {
                    Log.i(TAG, "正在重定向连接...");
                    mManager.switchConnectionInfo(((RedirectException) e).redirectInfo);
                    mManager.connect();
                } else {
                    Log.i(TAG, "异常断开:" + e.getMessage());
                    reconnectSocket();
                    /*if (e.getMessage().equals("environmental disconnect")) {
                        //nager.disConnect();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mManager.connect();
                            }
                        }, 5000);
                    }
                    if (e.getMessage().contains("feed dog on time")) {
                        Log.i(TAG, "异常断开:feed dog on time resetsocket");
                        reconnectSocket();
                    }*/
                    /*if (e.getMessage().equals("environmental disconnect") || e.getMessage().equals("java.net.SocketException: sendto failed: EPIPE (Broken pipe)")) {
                        mManager.disConnect();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mManager.connect();
                            }
                        }, 5000);
                    }*/
                }
            } else {
                //IToast.show("正常断开");
                Log.i(TAG, "正常断开");
            }
            //取消通知
            cancelNotification();
        }

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            String str = info.getIp() + info.getPort() + action;
            Log.i(TAG, "onSocketConnectionSuccess: 服务器连接成功" + str);
            mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_LOGIN, 1500);
            //失败重连次数值为0
            connectFailedCount = 0;
        }

        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            String str = info.getIp() + info.getPort() + action + e.toString();
            Log.i(TAG, "onSocketConnectionFailed: 服务器连接失败" + str);
            reconnectSocket();
            /*if (str.contains("Socket Closed")) {
                reconnectSocket();
            }
            //失败重连次数自增，当重连失败3次，则重置socket，10分钟后尝试再连接
            connectFailedCount++;
            if (connectFailedCount == 3) {
                if (mManager != null) {
                    mManager.unRegisterReceiver(adapter);
                    mManager.disConnect();
                    mManager = null;
                }
                connectFailedCount = 0;
                mHandler.sendEmptyMessageDelayed(4405, 60 * 1000);
            }*/
        }
    };

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.COMMON.MSG.MSG_LOGIN:
                //连接成功，自动登入
                if (mManager != null && mManager.isConnect()) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOGIN));
                }
                break;
            case Constants.COMMON.MSG.MSG_SYNC_TOKEN:
                String token = (String) msg.obj;
                IToast.show("新token：" + token);
                break;
            case Constants.COMMON.MSG.MSG_SYNC_CLOCK:
                // 连接成功,自动对时
                if (mManager != null && mManager.isConnect()) {
                    IToast.show("开始对时");
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_CLOCK_SYNC));
                }
                break;
            case Constants.COMMON.MSG.MSG_LOCATION_SUC:
                //上传定位数据
                if (mManager != null && mManager.isConnect()) {
                    IToast.show("上传定位");
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_ELECTRICITY));
                }
                break;
            case Constants.COMMON.MSG.MSG_GET_WEATHER:
                //获取天气
                if (mManager != null && mManager.isConnect()) {
                    IToast.show("获取天气");
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                }
                break;
            case Constants.COMMON.MSG.MSG_SHUT_DOWN:
                PowerUtils.shutdown();
                break;
            case Constants.COMMON.MSG.MSG_REMOTE_PHOTO:
                if (getTopActivity() != null && !getTopActivity().contains("XCameraActivity")) {
                    startActivity(new Intent(this, XCameraActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                break;
            case Constants.COMMON.MSG.MSG_UPLOAD_PHOTO:
                if (msg.obj instanceof String) {
                    String path = (String) msg.obj;
                    PostRequest<String> postRequest = OkGo.<String>post(Constants.COMMON.Url.sendImage)
                            .params("imei", DeviceUtils.getIMEI(this))
                            //.params("imei", "C5B20180200030")
                            .params("token", Settings.Global.getString(getContentResolver(),
                                    Constants.MODEL.SETTINGS.GLOBAL_TOKEN))
                            .params("content", new File(path))
                            .converter(new StringConvert());
                    postRequest.execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.i(TAG, "照片上传成功");
                            int photoCodeCount = Settings.Global.getInt(getContentResolver(),"photo_code_count", 0);
                            if (photoCodeCount > 0) {
                                Settings.Global.putInt(getContentResolver(),"photo_code_count", photoCodeCount - 1);
                                if (photoCodeCount - 1 == 0) {
                                    return;
                                }
                                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_REMOTE_PHOTO).sendToTarget();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "照片上传失败");
                            int photoCodeCount = Settings.Global.getInt(getContentResolver(),"photo_code_count", 0);
                            if (photoCodeCount > 0) {
                                Settings.Global.putInt(getContentResolver(),"photo_code_count", photoCodeCount - 1);
                                if (photoCodeCount - 1 == 0) {
                                    return;
                                }
                                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_REMOTE_PHOTO).sendToTarget();
                            }
                        }
                    });

                }
                break;
            case Constants.COMMON.MSG.MSG_TIMER_LOCK:
                mHandler.removeMessages(4403);
                int cycleTime = Settings.Global.getInt(getContentResolver(), Constants.MODEL.SETTINGS.GLOBAL_TIMER_LOCK, 180);
                if (mManager != null && mManager.isConnect()) {
                    startLocationWithSuperMethod();
                }
                mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_TIMER_LOCK, 1000*cycleTime);
                /*RxTimeUtil.cancel();
                int time = (int) msg.obj;
                //重新设置定时上传定位的时间
                RxTimeUtil.interval(1000 * time, number -> {
                            if (AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).isStarted()) {
                                AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).stopLocation();
                            }
                            AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).startLocation();

                            /*//***每天更新天气信息
                            SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
                            boolean isInTime = TimeUtils.isInTime("02:00-10:00", df.format(new Date()));
                            //如果当天未更新天气信息，则将自动从服务器获取一次
                            if (isInTime && (!FileUtils.inSameDay(
                                    FileUtils.getFileLastModifiedTime(path + file),
                                    new Date()))) {
                                if (mManager != null && mManager.isConnect()) {
                                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                                }
                            }
                        }
                );*/
                break;
            case 4400:
                if (msg.obj == null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                    Log.i("shipeixian", "handler 上传基站定位数据包");
                    break;
                }
                String argStr = (String) msg.obj;
                String[] args = argStr.split("/");
                if (args.length == 2) {
                    if (mManager != null && mManager.isConnect()) {
                        mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C, args[0], args[1]));
                        Log.i("shipeixian", "handler 上传定位数据包");
                    }
                } else {
                    //mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                    Log.i("shipeixian", "handler 没有获得经纬度");
                }
                break;
            case 4401:
                //每一个小时上传一次电量，如此可减少功耗
                if (mManager != null && mManager.isConnect()) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_ELECTRICITY));
                    mHandler.sendEmptyMessageDelayed(4401, 13 * 60 * 1000);
                }
                break;
            case 4402:
                if (mManager != null && mManager.isConnect()) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                }
                break;
            case 4403:
                //默认正常模式，每10分钟上传一次定位
                startLocationWithNetwork();
                mHandler.sendEmptyMessageDelayed(Constants.COMMON.MSG.MSG_TIMER_LOCK, 3*60*1000);

                break;
            case 4404:
                if (isWaitingServerResponse) {
                    Log.i(TAG, "终端发了奇数指令，10秒内未得到服务器响应");
                    if (msg.arg1 == 7) {
                        break;
                    }
                    //如果申请登录，10秒内还没有登录，则重新连接
                    if (msg.arg1 == 1) {
                        reconnectSocket();
                    } else {
                        if (mManager != null && mManager.isConnect()) {
                            resendCodeCount++;
                            //如果重发了三次还不行，则重连
                            if (resendCodeCount == 3) {
                                reconnectSocket();
                            } else {
                                try {
                                    mManager.send(new SendData(msg.arg1));
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }
                break;
            case 4405:
                reconnectSocket();
                break;
            case 4406:
                if (getTopActivity() != null && !getTopActivity().contains("WxchatActivity")) {
                    startActivity(new Intent().setAction("com.ctyon.shawn.MESSAGE_TIP").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mPicpathObserver);
        mHandler.removeCallbacksAndMessages(null);
        mPicpathObserver = null;
        RxTimeUtil.cancel();
        //add by shipeixian for close manager begin
        if (mManager != null) {
            mManager.disConnect();
        }
        if (netBroadcastReceiver != null) {
            //注销广播
            unregisterReceiver(netBroadcastReceiver);
        }
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
        //add by shipeixian for close manager end

        cancelNotification();
        Log.i(TAG, "SocketService onDestroy");
    }

    //add by shipeixian for add sos number begin
    private void setSosNumber(String[] sosNumbers) {
        //"socket_client_sos_number"初始化为“0”,需要在WatchLauncher里面初始化一下
        try {
            String sosString = "";
            if (sosNumbers != null && sosNumbers.length > 0) {
                for (String item : sosNumbers) {
                    sosString += item + "/";
                }
            }
            Settings.Global.putString(getContentResolver(), "socket_client_sos_number", sosString);
        } catch (Exception e) {

        }
    }
    //add by shipeixian for add sos number end

    //add by shipeixian for location begin

    private void startLocationWithNetwork() {

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                if (AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).isStarted()) {
                    AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).stopLocation();
                }
                AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).startLocation();
            }
        }).start();*/

        startLocationWithSuperMethod();
    }

    /**
     * 定位方法，优先GPS经纬度，次之WiFi列表，最后基站信息
     */
    private void startLocationWithSuperMethod() {

        //发送基站信息
        if (mManager != null && mManager.isConnect()) {
            mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
            Log.i("shipeixian", "handler 上传基站定位数据包");
        }

        /*try {
            //打开GPS开关
            GpsTool.toggleGps(getApplicationContext(), true);
            //打开wifi开关
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        } catch (Exception e) {

        }
        //获取WiFi列表
        getWifiListInfo();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(wifiListInfo)) {
                    //开启GPS服务
                    startGpsService();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (longtitude != 0 && lantitude != 0) {
                                if (mManager != null && mManager.isConnect()) {
                                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C, longtitude+"", lantitude+""));
                                    Log.i("shipeixian", "handler 上传GPS定位数据包");
                                    closeGpsAndWifi();
                                }
                            } else {
                                //发送基站信息
                                if (mManager != null && mManager.isConnect()) {
                                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                                    Log.i("shipeixian", "handler 上传基站定位数据包");
                                    closeGpsAndWifi();
                                }
                            }
                        }
                    }, 10000);
                } else {
                    String[] wifiArray = wifiListInfo.split("/");
                    if (wifiArray.length >= 6) {
                        if (mManager != null && mManager.isConnect()) {
                            if (wifiArray.length >= 9) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C, wifiArray[0], wifiArray[1], wifiArray[2], wifiArray[3], wifiArray[4], wifiArray[5], wifiArray[6], wifiArray[7], wifiArray[8]));
                            } else if (wifiArray.length == 6) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C, wifiArray[0], wifiArray[1], wifiArray[2], wifiArray[3], wifiArray[4], wifiArray[5]));
                            }
                            Log.i("shipeixian", "handler 上传wifi定位数据包");
                            closeGpsAndWifi();
                        }
                    } else {
                        if (mManager != null && mManager.isConnect()) {
                            mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOCATION_C));
                            Log.i("shipeixian", "handler 上传基站定位数据包");
                            closeGpsAndWifi();
                        }
                    }
                }
            }
        }, 8000);*/

    }

    private void closeGpsAndWifi() {
        longtitude = 0;
        lantitude = 0;
        wifiListInfo = "";
        try {
            //关闭GPS开关
            //GpsTool.toggleGps(getApplicationContext(), false);
            stopGpsService();
            //关闭wifi开关
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
        } catch (Exception e) {

        }
    }

    private void getWifiListInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //显示扫描到的所有wifi信息
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    StringBuilder listinfo = new StringBuilder();
                    //搜索到的wifi列表信息
                    List<ScanResult> scanResults = wm.getScanResults();

                    Random random = new Random();
                    for (ScanResult sr:scanResults){
                        wifiListInfo += sr.BSSID+"/"+sr.level+"/"+ random.nextInt(999999) +"/";

                        listinfo.append("wifi网络ID：");
                        listinfo.append(sr.SSID);
                        listinfo.append("\nwifi MAC地址：");
                        listinfo.append(sr.BSSID);
                        listinfo.append("\nwifi信号强度：");
                        listinfo.append(sr.level+"\n\n");
                    }
                }
            }
        }).start();
    }

    //add by shipeixian for location end

    //add by shipeixian for force stop self begin
    private void forceStopPackage(String packageName) {
        try {
            android.app.ActivityManager mActivityManager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            java.lang.reflect.Method forceStopPackageMethod = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            forceStopPackageMethod.setAccessible(true);
            forceStopPackageMethod.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            Log.i("shipeixian", "GuideActivity forceStopPackage " + e);
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        switch (netMobile) {
            case 1://wifi
                if (mManager == null) {
                    resetSocket();
                    Log.i(TAG, "网络状态WiFi，开始初始化socket");
                }
                break;
            case 0://移动数据
                if (mManager == null) {
                    resetSocket();
                    Log.i(TAG, "网络状态移动数据，开始初始化socket");
                }
                break;
            case -1://没有网络
                try {
                    if (mManager != null ) {
                        mManager.unRegisterReceiver(adapter);
                        mManager.disConnect();
                        mManager = null;
                        mHandler.removeCallbacksAndMessages(null);
                    }
                } catch (Exception e) {

                }
                Log.i(TAG, "网络状态无网络");
                cancelNotification();
                break;
        }
    }

    @Override
    public void onUploadPhoto(boolean isTackPictureSucess) {
        if (isTackPictureSucess) {
            String picPath = Settings.Global.getString(getContentResolver(), Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH);
            if (!TextUtils.isEmpty(picPath)) {
                Message message = new Message();
                message.what = Constants.COMMON.MSG.MSG_UPLOAD_PHOTO;
                message.obj = picPath;
                mHandler.sendMessage(message);
            }
        } else {
            int photoCodeCount = Settings.Global.getInt(getContentResolver(),"photo_code_count", 0);
            if (photoCodeCount > 0) {
                Settings.Global.putInt(getContentResolver(),"photo_code_count", photoCodeCount - 1);
                if (photoCodeCount - 1 == 0) {
                    return;
                }
                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_REMOTE_PHOTO).sendToTarget();
            }
        }
    }

    @Override
    public void onUnbindWatch() {
        startActivity(new Intent(this, ConfirmUnbindActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onReallyUnbindWatch() {
        if (mManager != null && mManager.isConnect()) {
            mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_MASTER_UNTIE));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reboot();
                }
            }, 2500);
        }
    }

    @Override
    public void onShowImed() {
        startActivity(new Intent(this, ShowImedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    //add by shipeixian for force stop self end

    //add by shipeixian for gps tool begin

    private void initGpsService() {
        gpsLocationManager = GPSLocationManager.getInstances(getApplicationContext());
    }

    private void startGpsService() {
        gpsLocationManager.start(new MyListener());
    }

    private void stopGpsService() {
        gpsLocationManager.stop();
    }

    class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                longtitude = location.getLongitude();
                lantitude = location.getLatitude();
            } else {
                longtitude = 0;
                lantitude = 0;
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps" == provider) {
                Log.i(TAG, "定位类型：" + provider);
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    Log.i(TAG, "GPS开启");
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    Log.i(TAG, "GPS关闭");
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    Log.i(TAG, "GPS不可用");
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "GPS暂时不可用");
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    Log.i(TAG, "GPS可用啦");
                    break;
            }
        }
    }

    /*private void initGpsService() {
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(1);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(1);
        String provider = locationmanager.getBestProvider(criteria, true);
        updateWithNewLocation(locationmanager.getLastKnownLocation(provider));
        locationmanager.requestLocationUpdates(provider, 1000, 0.0f, locationlistener);
    }

    private final LocationListener locationlistener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double jingdu = location.getLongitude();
            double weidu = location.getLatitude();
            longtitude = jingdu;
            lantitude = weidu;
            return;
        }
        longtitude = 0;
        lantitude = 0;
        Log.i(TAG, "GPS开关关了，经纬度复原");
    }*/
    //add by shipeixian for gps tool end


    private int getAlarmCycle(String week) {
        int result = 0;
        if (week.equals("0000000")) {
            return -1;
        }
        if (week.equals("1111111")) {
            return 0;
        }
        for (int i = 0; i < week.length(); i++) {
            if (week.charAt(i) == '1') {
                result += Math.pow(2, i);
            }
        }
        return result;
    }

    private void sendNotification() {
        try {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //"isAlarmSet"
                    Settings.Global.putInt(getContentResolver(), "isSocketLogin", 1);
                }
            }, 3000);
        } catch (Exception e) {

        }
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.setContentTitle("")
                .setDefaults(Notification.DEFAULT_LIGHTS).setSmallIcon(R.mipmap.icon_notification)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(4444, notification);*/
    }

    private void cancelNotification() {
        try {
            Settings.Global.putInt(getContentResolver(), "isSocketLogin", 0);
        } catch (Exception e) {

        }
       /* NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(4444);*/
    }

    private void showMessageToast() {
        if (getTopActivity() != null && !getTopActivity().contains("WxchatActivity")) {
            TastyToast.makeText(getApplicationContext(), "您有新的微聊消息!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).setGravity(Gravity.CENTER, 0, 0);
        }
    }

    private String getTopActivity() {
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
        if(runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString() ;
        else
            return null ;
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public boolean isServiceRunning(String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isApplicationDoubleRunning(String packagename) {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess){
            if(amProcess.processName.equals(packagename)){
                if (Process.myPid() != amProcess.pid) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重连socket
     */
    private void reconnectSocket() {
        try {
            if (mManager != null ) {
                mManager.unRegisterReceiver(adapter);
                mManager.disConnect();
                mManager = null;
                mHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e2) {

        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetSocket();
            }
        }, 5000);
    }


    private boolean isForbidUse() {
        try {
            /*String jsonString = "{\n" +
                    "    \"type\" : 28,\n" +
                    "    \"ident\" : 9,\n" +
                    "    \"disturb\" : [\n" +
                    "        {\n" +
                    "            \"start\" : \"08:00\",\n" +
                    "            \"end\" : \"18:00\",\n" +
                    "            \"week\" : \"1111100\",\n" +
                    "            \"status\" : 1\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";*/
            String jsonString = android.provider.Settings.Global.getString(getContentResolver(), "socket_client_disturb");
            com.google.gson.Gson gson = new com.google.gson.Gson();
            ForbidUseModel forbidUseModel = gson.fromJson(jsonString, ForbidUseModel.class);
            String start = forbidUseModel.getDisturb().get(0).getStart();
            String end = forbidUseModel.getDisturb().get(0).getEnd();
            String week = forbidUseModel.getDisturb().get(0).getWeek();
            int status = forbidUseModel.getDisturb().get(0).getStatus();
            String nowTime = DateUtil.getTime(new java.util.Date());

            String[] timeZoneArray = {start, end, nowTime};
            java.util.Arrays.sort(timeZoneArray);

            //上课禁用1:开启 0：未开启
            if (status == 1 && timeZoneArray[1].equals(nowTime) && week.charAt(getWeek()) == '1') {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /*获取星期几*/
    private int getWeek() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int i = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int week = 0;
        switch (i) {
            case 1:
                //"星期日"
                week = 6;
                break;
            case 2:
                //"星期一"
                week = 0;
                break;
            case 3:
                //"星期二"
                week = 1;
            case 4:
                //"星期三"
                week = 2;
                break;
            case 5:
                //"星期四"
                week = 3;
                break;
            case 6:
                //"星期五"
                week = 4;
                break;
            case 7:
                //"星期六"
                week = 5;
                break;
            default:
                break;
        }
        return week;
    }

    private void reboot() {
        PowerManager pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        try {
            pManager.reboot("unbind_watch");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
