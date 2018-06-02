package com.ctyon.socketclient.app.server;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.BuildConfig;
import com.ctyon.socketclient.R;
import com.ctyon.socketclient.app.network.NetBroadcastReceiver;
import com.ctyon.socketclient.app.network.NetEvent;
import com.ctyon.socketclient.project.model.AlarmModel;
import com.ctyon.socketclient.project.senddata.RedirectException;
import com.ctyon.socketclient.project.senddata.publish.PulseData;
import com.ctyon.socketclient.project.senddata.publish.SendData;
import com.ctyon.socketclient.project.support.MyHeaderProtocol;
import com.ctyon.socketclient.project.support.observer.SettingsObserver;
import com.ctyon.socketclient.project.util.IToast;
import com.example.camera.CameraActivity;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    private int ident = 999999;

    //add by shipeixian on 2018-05-24 begin
    private boolean isWaitingServerResponse = false;
    private boolean isGpsLocatioinSuccess = false;
    private int connectFailedCount = 0;
    private int resendCodeCount = 0;
    private LocationManager locationManager;
    private double longtitude = 0;
    private double lantitude = 0;

    private long stealPhotoTime = 0;
    private long stealCallTime = 0;
    private long uploadLocationTime = 0;

    private ActivityManager activityManager;


    /**
     * 监控网络的广播
     */
    private NetBroadcastReceiver netBroadcastReceiver;

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
        mHandler = new SafeHandler<SocketService>(this);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //registerContentObserver();

        //add by shipeixian on 2018-05-24 begin
        registerReceivers();
        //add by shipeixian on 2018-05-24 end
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
            registerReceiver(netBroadcastReceiver, filter);
            //设置监听
            netBroadcastReceiver.setNetEvent(this);
        }
    }

    private void resetSocket() {
        //add by shipeixian on 2018-05-24 begin
        if (mHandler != null) {
            mHandler.removeMessages(4400);
            mHandler.removeMessages(4401);
            mHandler.removeMessages(4402);
            mHandler.removeMessages(4403);
            mHandler.removeMessages(4405);
        }
        //add by shipeixian on 2018-05-24 end

        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        info = new ConnectionInfo(BuildConfig.HOST, BuildConfig.PORT);
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        mManager = OkSocket.open(info, OkSocketOptions.getDefault());
        //获得当前连接通道的参配对象
        mOkOptions = mManager.getOption();
        //基于当前参配对象构建一个参配建造者类
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(mOkOptions);
        //修改参配设置
        builder.setPulseFrequency(9 * 60 * 1000); // 默认心跳 60s
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
                            int index = jsonObject.get(Constants.MODEL.DATA.DATA_INEDX).getAsInt();
                            int last = jsonObject.get(Constants.MODEL.DATA.DATA_LAST).getAsInt();
                            JsonArray jsonArray = jsonObject.get(Constants.MODEL.DATA.DATA_CONTACT).getAsJsonArray();
                            JsonObject cj = jsonArray.get(index).getAsJsonObject();
                            String cj_name = cj.get(Constants.MODEL.DATA.DATA_NAME).getAsString();
                            String cj_phone = cj.get(Constants.MODEL.DATA.DATA_PHONE).getAsString();
                            //ContactUtils.addContact(App.getsContext(),cj_name,cj_phone);
                            Intent addContactIntent = new Intent();
                            addContactIntent.putExtra("contact_name", cj_name);
                            addContactIntent.putExtra("contact_number", cj_phone);
                            addContactIntent.setAction("com.ctyon.shawn.ADD_CONTACT");
                            sendBroadcast(addContactIntent);
                            //IToast.show("通讯录号码设置");
                            break;
                        case Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE:
//                        {"type":10,"ident":226201,"id":"5ab49367c493a9055a9344dc","url":"http://devicetest.iot08.com/static/message/fe801c53132108ecc9781e9e.amr","duration":8,"size":14022}
                            //IToast.show("您有一条新消息(语音)");
                            Log.i("shipeixian", "您有一条新消息(语音)");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_VOICE_MESSAGE, ident + ""));
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
                            mHandler.sendEmptyMessage(4406);
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
                            Gson gson = new Gson();
                            String alarmArgs = "";
                            Intent alarmIntent = new Intent();
                            alarmIntent.setAction("com.ctyon.shawn.SET_ALARM");
                            try {
                                AlarmModel alarmModel = gson.fromJson(alarm, AlarmModel.class);
                                List<AlarmModel.AlarmBean> alarmBeans = alarmModel.getAlarm();
                                if (alarmBeans != null) {
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
                            WeChatMessage wcm = new WeChatMessage();
                            wcm.setSecid(jsonObject.get(Constants.MODEL.DATA.DATA_ID).getAsString());
                            wcm.setMessageType(WxchatMessageBean.MessageType.Text.ordinal());
                            wcm.setMessageTime(System.currentTimeMillis());
                            wcm.setMessageText(jsonObject.get(Constants.MODEL.DATA.DATA_CONTENT).getAsString());
                            wcm.setMessageSenderType(WxchatMessageBean.MessageSenderType.Parents.ordinal());
                            wcm.setMessageReadStatus(WxchatMessageBean.MessageReadStatus.UnRead.ordinal());
                            wcm.setShowMeaageTime(true);
                            DbUtils.insertMsg(App.getsContext(), wcm);
                            mHandler.sendEmptyMessage(4406);
                            break;
                        case Constants.COMMON.TYPE.TYPE_REMOTE_PHOTO:
                            //远程拍照
                            Log.i("shipeixian", "远程拍照");
                            if (mManager != null && mManager.isConnect()) {
                                mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_REMOTE_PHOTO, ident + ""));
                            }
                            //20秒限制，防止无限下发
                            if (System.currentTimeMillis() - stealPhotoTime > 20000) {
                                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_REMOTE_PHOTO).sendToTarget();
                                stealPhotoTime = System.currentTimeMillis();
                            } else {
                                Log.i("shipeixian", "远程拍照， 20秒前已远程拍照，稍后重试");
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
                            Settings.Global.putString(getContentResolver(), "socket_client_token", deviceToken);
                            //startActivity
                            Intent qrcodeIntent = new Intent();
                            qrcodeIntent.setClassName("com.ctyon.watch", "com.ctyon.watch.ui.activity.QrcodeActivity");
                            startActivity(qrcodeIntent);
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
            //add by shipeixian for record the response time end
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.i(TAG, "onPulseSend: " + str);

        }


        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                if (e instanceof RedirectException) {
                    Log.i(TAG, "正在重定向连接...");
                    mManager.switchConnectionInfo(((RedirectException) e).redirectInfo);
                    mManager.connect();
                } else {
                    Log.i(TAG, "异常断开:" + e.getMessage());
                    mHandler.removeMessages(4400);
                    mHandler.removeMessages(4401);
                    mHandler.removeMessages(4402);
                    mHandler.removeMessages(4403);
                    if (e.getMessage().equals("environmental disconnect")) {
                        //nager.disConnect();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mManager.connect();
                            }
                        }, 5000);
                    }
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
            //失败重连次数自增，当重连失败3次，则重置socket，10分钟后尝试再连接
            connectFailedCount++;
            if (connectFailedCount == 3) {
                if (mManager != null) {
                    mManager.unRegisterReceiver(adapter);
                    mManager.disConnect();
                    mManager = null;
                }
                connectFailedCount = 0;
                mHandler.sendEmptyMessageDelayed(4405, 10 * 60 * 1000);
            }
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
                startActivity(new Intent(this, CameraActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "照片上传失败");
                        }
                    });

                }
                break;
            case Constants.COMMON.MSG.MSG_TIMER_LOCK:
                mHandler.removeMessages(4403);
                RxTimeUtil.cancel();
                int time = (int) msg.obj;
                //重新设置定时上传定位的时间
                RxTimeUtil.interval(1000 * time, number -> {
                            if (AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).isStarted()) {
                                AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).stopLocation();
                            }
                            AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).startLocation();

                            //***每天更新天气信息
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
                );
                break;
            case 4400:
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
                    mHandler.sendEmptyMessageDelayed(4401, 60 * 60 * 1000);
                }
                break;
            case 4402:
                if (mManager != null && mManager.isConnect()) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                }
                break;
            case 4403:
                //默认省电模式，每一小时上传一次定位
                startLocationWithNetwork();
                mHandler.sendEmptyMessageDelayed(4403, 60 * 60 * 1000);
                break;
            case 4404:
                if (isWaitingServerResponse) {
                    Log.i(TAG, "终端发了奇数指令，10秒内未得到服务器响应");
                    if (msg.arg1 == 7) {
                        break;
                    }
                    //如果申请登录，10秒内还没有登录，则重新连接
                    if (msg.arg1 == 1) {
                        mHandler.removeMessages(4400);
                        mHandler.removeMessages(4401);
                        mHandler.removeMessages(4402);
                        mHandler.removeMessages(4403);
                        mManager.disConnect();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mManager.connect();
                            }
                        }, 5000);
                    } else {
                        if (mManager != null && mManager.isConnect()) {
                            resendCodeCount++;
                            //如果重发了三次还不行，则重连
                            if (resendCodeCount == 4) {
                                mHandler.removeMessages(4400);
                                mHandler.removeMessages(4401);
                                mHandler.removeMessages(4402);
                                mHandler.removeMessages(4403);
                                mManager.disConnect();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mManager.connect();
                                    }
                                }, 5000);
                            } else {
                                try {
                                    mManager.send(new SendData(msg.arg1));
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                    Log.i(TAG, "socket的连接状态为isconnect = " + mManager.isConnect());
                }
                break;
            case 4405:
                resetSocket();
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
        //add by shipeixian for close manager end
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        Log.i(TAG, "SocketService onDestroy");
    }

    //add by shipeixian for add sos number begin
    private void setSosNumber(String[] sosNumbers) {
        //"socket_client_sos_number"初始化为“0”,需要在WatchLauncher里面初始化一下
        try {
            String sosString = "";
            for (String item : sosNumbers) {
                sosString += item + "/";
            }
            Settings.Global.putString(getContentResolver(), "socket_client_sos_number", sosString);
        } catch (Exception e) {

        }
    }
    //add by shipeixian for add sos number end

    //add by shipeixian for location begin

    private void startLocationWithNetwork() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).isStarted()) {
                    AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).stopLocation();
                }
                AMapLocationImp.buildLocationClient(App.getsContext(), mHandler).startLocation();
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
                if (mManager != null) {
                    mManager.unRegisterReceiver(adapter);
                    mManager.disConnect();
                    mManager = null;
                    mHandler.removeCallbacksAndMessages(null);
                }
                Log.i(TAG, "网络状态无网络");
                break;
        }
    }

    @Override
    public void onUploadPhoto() {
        String picPath = Settings.Global.getString(getContentResolver(), Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH);
        if (!TextUtils.isEmpty(picPath)) {
            Message message = new Message();
            message.what = Constants.COMMON.MSG.MSG_UPLOAD_PHOTO;
            message.obj = picPath;
            mHandler.sendMessage(message);
        }
    }

    //add by shipeixian for force stop self end

    //add by shipeixian for gps tool begin

    private void initGpsServer() {
        Log.i(TAG, "initGpsServer come in ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.i(TAG, "初始化locationmanager成功");
        // 判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 强制开启GPS
            //GpsTool.toggleGps(getApplicationContext(), true);
        }

        // 为获取地理位置信息时设置查询条件
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        //Location location = locationManager.getLastKnownLocation(bestProvider);
        /*if (location != null) {
            Log.i(TAG, "initGpsServer方法触发 "+location.getLatitude()+"-"+location.getLongitude());
        }*/
        //updateGpsData(location);
        // 监听状态
        locationManager.addGpsStatusListener(listener);
        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateGpsData(location);
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = locationManager.getLastKnownLocation(provider);
            Log.i(TAG, "GPS开启时触发 " + location.getLatitude() + "-" + location.getLongitude());
            updateGpsData(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateGpsData(null);
        }

    };

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //Log.i(TAG, "卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    //System.out.println("搜索到：" + count + "颗卫星");
                    //Log.i(TAG, "搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "GPS定位启动");

                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "GPS定位结束");
                    break;
            }
        }
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateGpsData(Location location) {
        if (location != null) {
            lantitude = location.getLatitude();
            longtitude = location.getLongitude();

        }
    }


    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
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
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.setContentTitle("")
                .setDefaults(Notification.DEFAULT_LIGHTS).setSmallIcon(R.mipmap.icon_notification)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        notificationManager.notify(4444, notification);*/
    }

    private void cancelNotification() {
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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

}
