package com.ctyon.socketclient.app.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
//import android.service.notification.ZenModeConfig;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.BuildConfig;
import com.ctyon.socketclient.R;
import com.ctyon.socketclient.app.server.SocketService;
import com.ctyon.socketclient.project.data.LogBean;
import com.ctyon.socketclient.project.senddata.RedirectException;
import com.ctyon.socketclient.project.senddata.adapter.LogAdapter;
import com.ctyon.socketclient.project.senddata.publish.PulseData;
import com.ctyon.socketclient.project.senddata.publish.SendData;
import com.ctyon.socketclient.project.support.MyHeaderProtocol;
import com.ctyon.socketclient.project.util.IToast;
import com.example.camera.CameraActivity;
import com.example.location.AMapLocationImp;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wx.common.support.utils.SafeHandler;
import com.wx.common.support.utils.ThreadManager;
import com.wx.common.support.utils.lang.PreferencesUtils;
import com.xmaihh.phone.project.model.Modem;
import com.xmaihh.phone.support.hardware.CallUtils;
import com.xmaihh.phone.support.hardware.DeviceUtils;
import com.xmaihh.phone.support.hardware.Modem2Utils;
import com.xmaihh.phone.support.hardware.PowerUtils;
import com.xmaihh.phone.support.utils.ContactUtils;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.example.wxchat.app.WxchatActivity;
import me.example.wxchat.support.model.WeChatMessage;
import me.example.wxchat.support.ui.Injection;
import me.xmai.global.config.Constants;

public class MainActivity extends AppCompatActivity implements SafeHandler.HandlerContainer {

    @BindView(R.id.connect)
    Button connect;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.clear_log)
    Button clearLog;
    @BindView(R.id.send_list)
    RecyclerView sendList;
    @BindView(R.id.rece_list)
    RecyclerView receList;


    private static final String TAG = MainActivity.class.getSimpleName();

    private OkSocketOptions mOkOptions;
    private IConnectionManager mManager;
    private ConnectionInfo info;
    private LogAdapter mSendLogAdapter = new LogAdapter();
    private LogAdapter mReceLogAdapter = new LogAdapter();
    private SafeHandler<MainActivity> mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //initData();
//        initializeSocket();
        startService(new Intent(this, SocketService.class));
        //mHandler = new SafeHandler<MainActivity>(this);
        Log.i("shipeixian", "com.ctyon.socketclient comming");
    }

    private void initData() {
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        sendList.setLayoutManager(manager1);
        sendList.setAdapter(mSendLogAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        receList.setLayoutManager(manager2);
        receList.setAdapter(mReceLogAdapter);
    }

    private void initializeSocket() {
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
        if (mManager.isConnect()) {
            connect.setText("断开连接");
        }
    }

    @OnClick({R.id.connect,
            R.id.clear_log,
            R.id.login,
            R.id.heartbeat_c,
            R.id.clocksync,
            R.id.newmessage,
            R.id.getweather,
            R.id.electricity,
            R.id.setpcount,
            R.id.sendvoicess,
            R.id.shutdown,
            R.id.getlocation,
            R.id.getmodem
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.connect:
                if (mManager == null) {
                    return;
                }
                if (mManager.isConnect()) {
                    connect.setText("连接");
                    mManager.disConnect();
                }
                break;
            case R.id.clear_log:
                mReceLogAdapter.getDataList().clear();
                mSendLogAdapter.getDataList().clear();
                mReceLogAdapter.notifyDataSetChanged();
                mSendLogAdapter.notifyDataSetChanged();
                break;
            case R.id.login:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_LOGIN));
                }
                break;
            case R.id.heartbeat_c:
                if (mManager != null) {
//                    mManager.send(new TestSendData(Constants.COMMON.TYPE.TYPE_PULSE_C));
                    if (mManager != null) {
                        //手动触发一次心跳
                        mManager.getPulseManager().trigger();
                    }
                }
                break;
            case R.id.clocksync:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_CLOCK_SYNC));
                }
                break;
            case R.id.newmessage:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_NEW_MESSAGE));
                }
                break;
            case R.id.getweather:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                }
                break;
            case R.id.electricity:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_ELECTRICITY));
                }
                break;
            case R.id.setpcount:
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_STEP_COUNT));
                }
                break;
            case R.id.sendvoicess:
//                startActivity(new Intent(this, Main2Activity.class));
                break;
            case R.id.shutdown:
                PowerUtils.shutdown();
                break;
            case R.id.getlocation:
                if (AMapLocationImp.buildLocationClient(this, mHandler).isStarted()) {
                    AMapLocationImp.buildLocationClient(this, mHandler).stopLocation();
                }
                AMapLocationImp.buildLocationClient(this, mHandler).startLocation();
                logRece(PreferencesUtils.getString(this, Constants.MODEL.PRENAME.PRE_AMP));
                if (mManager != null) {
                    mManager.send(new SendData(Constants.COMMON.TYPE.TYPE_GET_WEATHER));
                }
                break;
            case R.id.getmodem:

                break;
        }
    }

    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketIOThreadStart(Context context, String action) {
            logRece(action);
        }

        @Override
        public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
            logRece(action + "Exception：" + e.toString());
        }

        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            if (str.length() != 0) {
                Log.d(TAG, str.length() + "onSocketReadResponse: " + str);
                logRece(str);
                //打印日志
                byte[] body = data.getBodyBytes();
                String bodyStr = new String(body);
                JsonElement jsonElement = new JsonParser().parse(bodyStr);
                if (jsonElement instanceof JsonObject) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    int type = jsonObject.get("type").getAsInt();
                    switch (type) {
                        case Constants.COMMON.TYPE.TYPE_LOGIN:
                            //给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
                            mManager.getPulseManager()
                                    .setPulseSendable(new PulseData())
                                    .pulse();
                            IToast.showLong("登录成功");
                            login.setText("登录成功，已在线");
                            break;
                        case Constants.COMMON.TYPE.TYPE_PULSE_C:
                            if (mManager != null) {   //心跳喂狗操作
                                mManager.getPulseManager().feed();
                            }
                            IToast.showLong("终端发送心跳成功");
                            break;
                        case Constants.COMMON.TYPE.TYPE_CLOCK_SYNC:
                            IToast.showLong("时间同步");
                            break;
                        case Constants.COMMON.TYPE.TYPE_NEW_MESSAGE:
                            break;
                    }
                }
            }
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d(TAG, "onSocketWriteResponse: " + str);
            logSend(str);
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d(TAG, "onPulseSend: " + str);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                if (e instanceof RedirectException) {
                    logSend("正在重定向连接...");
                    mManager.switchConnectionInfo(((RedirectException) e).redirectInfo);
                    mManager.connect();
                } else {
                    logSend("异常断开:" + e.getMessage());
                }
            } else {
                IToast.show("正常断开");
                logSend("正常断开");
                connect.setTextColor(getResources().getColor(Color.GRAY));
            }
        }

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            String str = info.getIp() + info.getPort() + action;
            Log.d(TAG, "onSocketConnectionSuccess: 服务器连接成功" + str);
            connect.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            connect.setText("已连接");
        }

        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            String str = info.getIp() + info.getPort() + action + e.toString();
            Log.d(TAG, "onSocketConnectionFailed: 服务器连接失败" + str);
            connect.setTextColor(getResources().getColor(R.color.colorAccent));
            connect.setText("未连接");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disConnect();
        }
        AMapLocationImp.buildLocationClient(this, mHandler).onDestroy();
    }

    private void logSend(String log) {
        LogBean logBean = new LogBean(System.currentTimeMillis(), log);
        mSendLogAdapter.getDataList().add(0, logBean);
        mSendLogAdapter.notifyDataSetChanged();
    }

    private void logRece(String log) {
        LogBean logBean = new LogBean(System.currentTimeMillis(), log);
        mReceLogAdapter.getDataList().add(0, logBean);
        mReceLogAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //创建Menu菜单
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.machineinfo:
                String imei = DeviceUtils.getIMEI(this);
                String imsi = DeviceUtils.getIMSI(this);
                int versioncode = DeviceUtils.getLocalVersion(this);
                String modules = DeviceUtils.hasModuleSupport(this);
                logRece("获取设备信息成功：" + "\n" +
                        "imei:" + imei + "\n" + "imsi:" + imsi +
                        "\n" + "versionCode:" + versioncode +
                        "\n" + "modules:" + modules);

                Settings.Global.putString(getContentResolver(), "sss", "123456");
                break;
            case R.id.modeminfo:
                Modem modem = Modem2Utils.getModemCell(this);
                logRece("获取基站信息" + "\n" + modem.toString());
                break;
            case R.id.callphone:
                CallUtils.callPhone(App.getsContext(), "13724971541");
                break;
            case R.id.weather:
                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_GET_WEATHER).sendToTarget();
                break;
            case R.id.intent_weather:
//                try {
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.CTYON_WEATHER");
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                startActivity(new Intent(this, com.xmaihh.phone.app.activity.MainActivity.class));
                break;
            case R.id.insert_contact:
                ContactUtils.addContact(App.getsContext(), "lililili", "11011011012");
                break;
            case R.id.battery:
                float battery = PowerUtils.getBattery(App.getsContext());
                IToast.show((int) (battery * 100) + "");
                break;
            case R.id.shutdown:
                Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.take_photo:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.wipe_data:
                PowerUtils.wipeAlldata(App.getsContext());
                break;
            case R.id.wechat:
                startActivity(new Intent(this, WxchatActivity.class));
                break;
            case R.id.insert_wechat:
                WeChatMessage bean = new WeChatMessage();
                bean.setMessageSendStatus(0);
                bean.setDuringTime(12);
                bean.setLogoUrl("123312");
                bean.setShowMeaageTime(true);
                ThreadManager.getThreadPollProxy().execute(() ->
                        Injection.provideMsgDatasource(App.getsContext())
                                .insertOrUpdateMsg(bean));
                break;
            case R.id.set_zen_mode:
                NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if(n.isNotificationPolicyAccessGranted()) {
                    /*AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    n.setZenMode(Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS,null,null);
                    ZenModeConfig conf = n.getZenModeConfig();
//                    n.updateAutomaticZenRule(new AutomaticZenRule());
                    Log.d(TAG, "onOptionsItemSelected: "+conf.toString());*/
                }else{
                    // Ask the user to grant access
                    Intent i = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(i);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleMessage(Message message) {

    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public boolean isServiceRunning(String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查网络是否可用
     *
     * @return
     */
    public boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

}
