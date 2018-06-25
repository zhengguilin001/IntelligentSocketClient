package com.ctyon.watch.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ctyon.watch.R;
import com.ctyon.watch.utils.ContactsUtil;
import com.ctyon.watch.ui.activity.contact.ContactsManager;
import android.os.Message;
import android.content.Context;
import android.content.IntentFilter;

import android.telephony.TelephonyManager;
import java.lang.reflect.Method;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


/**
 * Created by zx
 * On 2018/2/27
 */

public class GuideActivity extends Activity {
    private static final String TAG = "GuideActivity";
    private ImageView mIVGuide;

    private boolean hasSendBatterySMS = false;

    private int phoneNumIndex = 1;
    private int callCounts = 0;
    private int sosNumberCounts = 0;

    private String outgoingPhoneNumber = "";

    //add by shipeixian for sos call handle begin
    private java.util.Random random;
    private PhoneReceiver phoneReceiver;
    private TelListner telListner;
    //add by shipeixian for sos call handle end

    //add by shipeixian for keep alive begin
    public android.os.PowerManager.WakeLock wakeLock;

    private void acquireWakeLock() {
        if (null == wakeLock) {
            android.os.PowerManager pm = (android.os.PowerManager) getSystemService(android.content.Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK
                    | android.os.PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                android.util.Log.d("shipeixian","call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            android.util.Log.d("shipeixian", "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }
    //add by shipeixian for keep alive end

    //add by shipeixian for touch out the activity begin
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(Math.abs(y1-y2) > 50 || Math.abs(x1-x2) > 50) {
                if (isForbidUse()) {
                    startActivity(new Intent(this, ForbidUseActivity.class));
                    overridePendingTransition(0, 0);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    //add by shipeixian for touch out the activity end

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initData();

        //add by shipeixian for sos call handle begin
        random = new java.util.Random();
        registReceiver();
        //add by shipeixian for sos call handle end

        mHandler.sendEmptyMessageDelayed(5555, 3*60*1000);

        //for alarm
        mHandler.sendEmptyMessageDelayed(6666, 5*1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContactsUtil.hasSimCard(this)) {
            if (isForbidUse()) {
                startActivity(new Intent(this, ForbidUseActivity.class));
                overridePendingTransition(0, 0);
            } else {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
        } else {
            Glide.with(this).load(R.mipmap.guide).into(mIVGuide);
        }
    }

    private void initView() {
        mIVGuide = (ImageView) findViewById(R.id.IV_guide);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG," BackPressed");
    }

    //add by shipeixian for forbid use when lesson begin
 
    private void initData() {
        try {
             //android.provider.Settings.Global.putInt(getContentResolver(),"socket_client_callstrategy",0);
             //android.provider.Settings.Global.putInt(getContentResolver(),"socket_client_listen",0);
             /*String jsonString = "{\n" +
                    "    \"type\" : 28,\n" +
                    "    \"ident\" : 9,\n" +
                    "    \"disturb\" : [\n" +
                    "        {\n" +
                    "            \"start\" : \"08:00\",\n" +
                    "            \"end\" : \"18:00\",\n" +
                    "            \"week\" : \"1111100\",\n" +
                    "            \"status\" : 0\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
             android.provider.Settings.Global.putString(getContentResolver(),"socket_client_disturb",jsonString);*/
             //android.provider.Settings.Global.putString(getContentResolver(),"socket_client_sos_number","0");
             int isOpen = android.provider.Settings.Global.getInt(getContentResolver(), "socket_client_callstrategy");
             android.util.Log.d("shipeixian", "socket_client_callstrategy == " + isOpen);
             android.util.Log.d("shipeixian", "socket_client_disturb == " + android.provider.Settings.Global.getString(getContentResolver(), "socket_client_disturb"));
        } catch (Exception e) {
       
        }
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
            com.ctyon.watch.model.ForbidUseModel forbidUseModel = gson.fromJson(jsonString, com.ctyon.watch.model.ForbidUseModel.class);
            String start = forbidUseModel.getDisturb().get(0).getStart();
            String end = forbidUseModel.getDisturb().get(0).getEnd();
            String week = forbidUseModel.getDisturb().get(0).getWeek();
            int status = forbidUseModel.getDisturb().get(0).getStatus();
            String nowTime = com.ctyon.watch.utils.DateUtil.getTime(new java.util.Date());

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
    //add by shipeixian for forbid use when lesson end 

    //add by shipeixian for sos call handle begin
    private class PhoneReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            //拨打电话
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                android.util.Log.d("shipeixian", "GuideActivity 拨打电话了");
                int sosStatus = android.provider.Settings.Global.getInt(getContentResolver(),"socket_client_sos_status", 0);
                if (sosStatus == 1) {
                    //dangerous
                    String number = getResultData();
                    outgoingPhoneNumber = number;
                    android.util.Log.d("shipeixian", "sos 发短信给"+outgoingPhoneNumber);
                    sendSMS(number, "您关注的宝贝发出sos紧急求救信息，请尽快确认宝贝的安全！");
                    mHandler.removeMessages(4400);
                    callCounts += 1;
                }

            }
            if (intent.getAction().equals("com.ctyon.shawn.STEAL_CALL")) {
                final String number = intent.getStringExtra("number");
                mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            android.os.SystemProperties.set("persist.sys.monitor", "0");
                            callPhone(number);
                        }
                }, 3*1000);
            }
            if(intent.getAction().equals("android.intent.action.BATTERY_LOW")) {
                android.util.Log.d("shipeixian", "收到低电量广播");
                if((int)(getBattery(GuideActivity.this)*100) < 20 && !hasSendBatterySMS) {
                    try {
                        String sosString = android.provider.Settings.Global.getString(getContentResolver(),"socket_client_sos_number");
                        String[] sosNumbers  = sosString.split("/");
                        sendSMS(sosNumbers[0], "您关联的手表电量低，请及时充电！");
                        android.util.Log.d("shipeixian", "低电量，发短信给"+sosNumbers[0]);
                        hasSendBatterySMS = true;
                        mHandler.sendEmptyMessageDelayed(7777, 30*1000);
                    } catch (Exception e) {

                    }
                } 
                
            }
            if(intent.getAction().equals("com.ctyon.shawn.ADD_CONTACT")) {
                android.util.Log.d("shipeixian", "添加联系人广播");
                String contactStr = intent.getStringExtra("contacts");
                android.util.Log.d("shipeixian", "添加联系人广播--"+contactStr);
                startService(new Intent(GuideActivity.this, com.ctyon.watch.service.ContactsService.class).putExtra("contacts", contactStr));
            }
            if(intent.getAction().equals("com.ctyon.shawn.CONFIRM_DIAL_SOS")) {
                startActivity(new Intent(GuideActivity.this, ConfirmSosActivity.class));
                overridePendingTransition(0,0);
            }
        }
    }

    private class TelListner extends android.telephony.PhoneStateListener {
       
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            int sosStatus = 0;
            switch (state) {
                case android.telephony.TelephonyManager.CALL_STATE_IDLE:/* 无任何状态 */
                    android.os.SystemProperties.set("persist.sys.monitor", "1");
                    mHandler.removeMessages(4409);
                    
                    sosStatus = android.provider.Settings.Global.getInt(getContentResolver(),"socket_client_sos_status", 0);
                    if (sosStatus == 1) {
                        //dangerous
                        android.util.Log.d("shipeixian", "GuideActivity 发送轮询");
                        mHandler.sendEmptyMessageDelayed(4400, 5*1000);
                    } else {
                        android.util.Log.d("shipeixian", "sos 已经被接听");
                        phoneNumIndex = 1;
                        callCounts = 0;
                        sosNumberCounts = 0;
                        mHandler.removeMessages(4400);
                    }
                    break;
                case android.telephony.TelephonyManager.CALL_STATE_OFFHOOK:/* 接起电话 */
                     if(getRecordState()) {
                         android.provider.Settings.Global.putInt(getContentResolver(),"socket_client_sos_status", 0);
                         phoneNumIndex = 1;
                         callCounts = 0;
                         sosNumberCounts = 0;
                         mHandler.removeMessages(4400);
                     }
                    break;
                case android.telephony.TelephonyManager.CALL_STATE_RINGING:/* 电话进来 */
                    //上课禁用，挂断电话
                    if (isForbidUse()) {
                        try{
                            Log.d("shipeixian", "上课禁用，挂断电话");
			    TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			    Class<TelephonyManager> c = TelephonyManager.class;
			    // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
		            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
			    //允许访问私有方法
			    mthEndCall.setAccessible(true);
			    final Object obj = mthEndCall.invoke(telMag, (Object[]) null);
			    // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
			    Method mt = obj.getClass().getMethod("endCall");
			    //允许访问私有方法
			    mt.setAccessible(true);
			    mt.invoke(obj);
		        } catch (Exception e){
			    e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(final android.os.Message msg) {
            switch (msg.what) {
                case 4400:
                    //每5秒钟发一次求救
                    try {
                        String sosString = android.provider.Settings.Global.getString(getContentResolver(),"socket_client_sos_number");
                        String[] sosNumbers  = sosString.split("/");
                        sosNumberCounts = sosNumbers.length;
                        int index = random.nextInt(sosNumbers.length);
                        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", android.net.Uri.parse("tel:"+sosNumbers[phoneNumIndex]));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //sendSMS(sosNumbers[phoneNumIndex], "宝贝发出sos求救信息，快去营救！");
                        if(phoneNumIndex < sosNumbers.length - 1){
                            phoneNumIndex += 1;
                        } else {
                            phoneNumIndex = 0;
                        }
                        //拨打两轮，1个号码，拨打2次；2个号码，拨打4次；3个号码，拨打6次
                        if((callCounts == 6 && sosNumberCounts == 3) || (callCounts == 4 && sosNumberCounts == 2) || (callCounts == 2 && sosNumberCounts == 1)) {
                            android.provider.Settings.Global.putInt(getContentResolver(),"socket_client_sos_status", 0);
                            phoneNumIndex = 1;
                            callCounts = 0;
                            sosNumberCounts = 0;
                            mHandler.removeMessages(4400);
                            break;
                        }
                        
                    } catch (Exception e) {

                    }
                    android.util.Log.d("shipeixian", "GuideActivity 时间到了发送轮询");
                    break;
                case 4409:
                    /*try{
			    android.telephony.TelephonyManager telMag = (android.telephony.TelephonyManager) getSystemService(android.content.Context.TELEPHONY_SERVICE);
			    Class<android.telephony.TelephonyManager> c = android.telephony.TelephonyManager.class;
			    // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
			    java.lang.reflect.Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
			    //允许访问私有方法
			    mthEndCall.setAccessible(true);
			    final Object obj = mthEndCall.invoke(telMag, (Object[]) null);
			    // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
			    java.lang.reflect.Method mt = obj.getClass().getMethod("endCall");
			    //允许访问私有方法
			    mt.setAccessible(true);
			    mt.invoke(obj);
			    android.util.Log.d("---SHIPEIXIAN---", "通话10分钟，挂断电话");
		    } catch (Exception e){
			    e.printStackTrace();
		    }*/
                    break;
                 case 5555:
                    if(!isApplicationRunning("com.ctyon.socketclient")) {
                        android.util.Log.d("shipeixian", "socket进程挂掉，拉起");
                        startService(new android.content.Intent().setClassName("com.ctyon.socketclient", "com.ctyon.socketclient.app.server.SocketService"));
                    }
                    mHandler.sendEmptyMessageDelayed(5555, 60*1000);
                    break;
                 case 6666:
                    if((int)(getBattery(GuideActivity.this)*100) > 20) {
                        hasSendBatterySMS = false;
                    }
                    //如果没有上课禁用，才开启闹钟服务
                    if (!isForbidUse()) {
                        startService(new Intent(GuideActivity.this, com.ctyon.watch.service.AlarmService.class));
                    }
                    mHandler.sendEmptyMessageDelayed(6666, 60*1000);
                    break;
                 case 7777:
                    try {
                        String sosString = android.provider.Settings.Global.getString(getContentResolver(),"socket_client_sos_number");
                        String[] sosNumbers  = sosString.split("/");
                        sendSMS(sosNumbers[0], "您关联的手表电量低，请及时充电！");
                        android.util.Log.d("shipeixian", "低电量，发短信给"+sosNumbers[0]);
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    };

    public void sendSMS(String phoneNumber, String message) {
        android.telephony.SmsManager manager = android.telephony.SmsManager.getDefault();
        Intent intent1 =  new Intent("com.ctyon.shawn.SMS.SENT");
        Intent intent2 = new Intent("com.ctyon.shawn.SMS.DELIVERY");
        android.app.PendingIntent pi1 = android.app.PendingIntent.getBroadcast(this, 0, intent1 , 0);
        android.app.PendingIntent pi2 = android.app.PendingIntent.getBroadcast(this, 0, intent2 , 0);
        manager.sendTextMessage(phoneNumber, null, message, pi1, pi2);
    }

    public void registReceiver() {
        phoneReceiver = new PhoneReceiver();
        android.content.IntentFilter phoneFilter = new android.content.IntentFilter();
        phoneFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        phoneFilter.addAction("android.intent.action.PHONE_STATE");
        phoneFilter.addAction("com.ctyon.shawn.STEAL_CALL");
        phoneFilter.addAction("android.intent.action.BATTERY_LOW");
        phoneFilter.addAction("com.ctyon.shawn.ADD_CONTACT");
        phoneFilter.addAction("com.ctyon.shawn.CONFIRM_DIAL_SOS");
        registerReceiver(phoneReceiver, phoneFilter);

        android.telephony.TelephonyManager telManager = (android.telephony.TelephonyManager) getSystemService(android.content.Context.TELEPHONY_SERVICE);
        telListner = new TelListner() ;
        telManager.listen(telListner, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (phoneReceiver != null) {
            unregisterReceiver(phoneReceiver);
        }
    }

    //add by shipeixian for sos call handle end   

    //add by shipeixian for start socket begin
    private void forceStopPackage(String packageName){
        try {
            android.app.ActivityManager mActivityManager = (android.app.ActivityManager)getSystemService(android.content.Context.ACTIVITY_SERVICE);
            java.lang.reflect.Method forceStopPackageMethod = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            forceStopPackageMethod.setAccessible(true);
            forceStopPackageMethod.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            android.util.Log.d("shipeixian", "GuideActivity forceStopPackage " + e);
        }
    }
    //add by shipeixian for start socket end

   public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        android.net.Uri data = android.net.Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean getCallLogState(String phoneNumber) {
        boolean isLink = false;
        android.content.ContentResolver cr = getContentResolver();
        final android.database.Cursor cursor = cr.query(android.provider.CallLog.Calls.CONTENT_URI,
                new String[]{android.provider.CallLog.Calls.NUMBER,android.provider.CallLog.Calls.TYPE,android.provider.CallLog.Calls.DURATION},
                android.provider.CallLog.Calls.NUMBER +"=? and "+android.provider.CallLog.Calls.TYPE +"= ?",
                new String[]{phoneNumber,"2"},null);
        while(cursor.moveToNext()){
            int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
            long durationTime = cursor.getLong(durationIndex);
            if(durationTime > 0){
                isLink = true;
            } else {
                isLink = false;
            }
        }
        return isLink;
    }

    public boolean isApplicationRunning(String packagename) {
        android.app.ActivityManager am = (android.app.ActivityManager)getSystemService(android.content.Context.ACTIVITY_SERVICE);
        java.util.List<android.app.ActivityManager.RunningAppProcessInfo> mRunningProcess = am.getRunningAppProcesses();
        for (android.app.ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess){
            if(amProcess.processName.equals(packagename)){
                 return true;
            }
        }
        return false;
    }

    /**
     * 获取电池电量,0~1
     *
     * @param context
     * @return
     */
    public float getBattery(Context context) {
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryInfoIntent.getIntExtra("status", 0);
        int health = batteryInfoIntent.getIntExtra("health", 1);
        boolean present = batteryInfoIntent.getBooleanExtra("present", false);
        int level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 0);
        int plugged = batteryInfoIntent.getIntExtra("plugged", 0);
        int voltage = batteryInfoIntent.getIntExtra("voltage", 0);
        int temperature = batteryInfoIntent.getIntExtra("temperature", 0);
        String technology = batteryInfoIntent.getStringExtra("technology");
        return ((float) level) / scale;
    }

    public boolean getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
                Log.i("shipeixian","startRecording():" + e);
            }
            return false;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            Log.d("shipeixian","audio input has been occupied");
            return false;
        } else {
            readSize = audioRecord.read(point, 0, point.length);
            if (readSize <= 0) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                Log.d("shipeixian","result of audiorecord is empty");
                return false;
            } else {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                Log.d("shipeixian","result of audiorecord is not empty");
                return true;
            }
        }
    }

}
