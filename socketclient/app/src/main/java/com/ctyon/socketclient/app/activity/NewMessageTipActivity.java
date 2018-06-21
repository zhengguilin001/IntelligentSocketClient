package com.ctyon.socketclient.app.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ctyon.socketclient.R;
import com.ctyon.socketclient.project.util.ScreenUtils;

/**
 * Created by shipeixian on 18-6-1.
 */

public class NewMessageTipActivity extends Activity {

    private Button showMsg, cancelTip;
    private PowerManager.WakeLock mWakelock;
    private PowerManager powerManager;
    private MediaPlayer mediaPlayer;
    //private KeyguardManager km;
    //private KeyguardManager.KeyguardLock kl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 4444) {
                if (powerManager.isScreenOn()) {
                    sendKeyEvent(26);
                }
            }
            if (msg.what == 5555) {
                NewMessageTipActivity.this.finish();
            }
        }
    };

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                NewMessageTipActivity.this.finish();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //sendTouchEvent();
            }
        }
    }

    private ScreenReceiver screenReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        //just for test
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_newmessagetip);

        showMsg = (Button) findViewById(R.id.showMsg);
        cancelTip = (Button) findViewById(R.id.cancelTip);

        showMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.CTYON_WXCHAT");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NewMessageTipActivity.this.finish();
            }
        });

        cancelTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewMessageTipActivity.this.finish();
            }
        });



        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
           // mWakelock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"SimpleTimer");
        }

        /*km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");*/

        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);

        //just for test
        ScreenUtils.wakeUpAndUnlock(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            handler.removeMessages(4444);
        } catch (Exception e) {

        }
        if (!powerManager.isScreenOn()) {
            //sendKeyEvent(26);
            handler.sendEmptyMessageDelayed(4444, 10000);
            //sendTouchEvent();

            /*handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    //这里参数”unLock”作为调试时LogCat中的Tag
                    KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
                    kl.reenableKeyguard();
                }
            }, 9800);*/
        }
        /*if (mWakelock != null) {
            mWakelock.acquire();
            sendTouchEvent();
            handler.sendEmptyMessageDelayed(4444, 10000);
            //键盘锁管理器对象
            *//*KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            //这里参数”unLock”作为调试时LogCat中的Tag
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();  //解锁
            handler.sendEmptyMessageDelayed(4444, 10000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    kl.reenableKeyguard();
                }
            }, 9500);*//*
        }*/
        /*if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
            mediaPlayer.start();
        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
                mediaPlayer.start();
            }
        }*/
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {


        if (mWakelock != null) {
            mWakelock.release();
        }
        super.onDestroy();
        if (mediaPlayer != null) {
            //释放mp
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
    }

    private void sendKeyEvent(final int keyCode) {
        final Instrumentation instrumentation = new Instrumentation();
        new Thread(new Runnable() {

            @Override
            public void run () {
                instrumentation.sendKeyDownUpSync(keyCode);
            }
        }).start();
    }

    private void sendTouchEvent() {
        /*final Instrumentation instrumentation = new Instrumentation();
        instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100, 120, 0,0,0,0,0,0,0));// OK
        instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 150, 120, 0,0,0,0,0,0,0));*/
        try {
            Runtime.getRuntime().exec("input swipe 50 50 100 50");
        }catch (Exception e) {

        }
        /*Instrumentation inst = new Instrumentation();
        long dowTime = SystemClock.uptimeMillis();
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime,
                MotionEvent.ACTION_DOWN, 50, 100,0));
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime,
                MotionEvent.ACTION_MOVE, 50, 100,0));
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime+20,
                MotionEvent.ACTION_MOVE, 50+20, 100,0));
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime+30,
                MotionEvent.ACTION_MOVE, 50+40, 100,0));
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime+40,
                MotionEvent.ACTION_MOVE, 50+60, 100,0));
        inst.sendPointerSync(MotionEvent.obtain(dowTime,dowTime+40,
                MotionEvent.ACTION_UP, 50+60, 100,0));*/
        /*new Thread(new Runnable() {

            @Override
            public void run () {
                instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 50, 50, 50));
            }
        }).start();*/
    }

}
