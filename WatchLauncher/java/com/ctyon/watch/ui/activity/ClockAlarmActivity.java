package com.ctyon.watch.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.model.CommonConstants;
import com.ctyon.watch.utils.DateUtil;
import com.ctyon.watch.utils.LogUtils;
import com.ctyon.watch.utils.ScreenUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;

/**
 * 闹钟响铃界面
 */
public class ClockAlarmActivity extends BaseActivity {

    private MediaPlayer player;
    public static boolean isRing = false;
    private AudioManager audioManager;
    private Vibrator mVibrator;
    private ScheduledExecutorService executorService;
    private TextView nowTime;
    private TextView keepSleep;
    private int ringModel = 0;
    private ScheduledExecutorService pool;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    nowTime.setText(DateUtil.getTime(new Date()));
                    break;
            }
        }
    };
    private TextView mTitle;
    private TextView tvPm;
    private TextView mCancel;
    private Intent alarmIntent;


    @Override
    protected void setContentView() {
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_clock_alarm);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        ScreenUtils.wakeUpAndUnlock(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void loadData() {
        alarmIntent = getIntent();
        pool = Executors.newScheduledThreadPool(1);
        long alarm_id = alarmIntent.getLongExtra("alarm_id",2);

        AlarmManager manager = new AlarmManager(this);
        AlarmModel model = manager.queryIsRepeatById(alarm_id);
        ringModel = alarmIntent.getIntExtra("soundOrVibrator", 2);
        switch(ringModel){
            case 0:
                //震动
                startVibrator();
                break;
            case 1:
                //铃声
                playRingtone();
                break;
            case 2:
                //铃声和震动
                playRingtone();
                startVibrator();
                break;
        }
        CommonConstants.IS_ALARM_RING = true;
    }

    @Override
    protected void initComponentEvent() {

        String title = alarmIntent.getStringExtra("alarm_title");
        if (title != null) {
            LogUtils.e("title" + title);
            mTitle.setText(title);
        }
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRingtone();
                finish();
            }
        });
        Calendar c = Calendar.getInstance();
        int apm = c.get(Calendar.AM_PM);
        if (DateFormat.is24HourFormat(this))
            tvPm.setVisibility(View.GONE);
        else
            tvPm.setText(apm == 0 ? getResources().getString(R.string.alarm_am) : getResources().getString(R.string.alarm_pm));
        nowTime.setText(DateUtil.getTime(new Date()));

        if (keepSleep.isFocusableInTouchMode())
            keepSleep.requestFocusFromTouch();
        else
            keepSleep.requestFocus();

        //keep sleep
        keepSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                continueSleep();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    @Override
    protected void initComponentView() {
        mTitle = (TextView) findViewById(R.id.tv_alarm_title);
        mCancel = (TextView) findViewById(R.id.btn_alarm_cancel);
        nowTime = (TextView) findViewById(R.id.alarm_now_time);
        keepSleep = (TextView) findViewById(R.id.alarm_little_sleep);
        tvPm = (TextView) findViewById(R.id.alarm_is_pm);
    }

    /**
     * 小睡一会
     */
    private void continueSleep() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (CommonConstants.IS_ALARM_RING)
                    return;
                Intent intent1 = new Intent(ClockAlarmActivity.this, ClockAlarmActivity.class);
                intent1.putExtra("title", mTitle.getText());
                startActivity(intent1);
            }
        };
        pool.schedule(task, 10, TimeUnit.MINUTES);
    }

    @Override
    public void onBackPressed() {
        stopRingtone();
        continueSleep();
        super.onBackPressed();

    }

    //取消铃声
    public void stopRingtone() {
        LogUtils.e("stopRingtone: music and vibrator");
        stopVibrator();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
                player = null;
            }
        }
        isRing = false;
        audioManager.abandonAudioFocus(afChangeListener);
        CommonConstants.IS_ALARM_RING = false;
    }

    //播放铃声
    private void playRingtone() {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                if (player == null) {
                    Uri uri = RingtoneManager.getActualDefaultRingtoneUri(ClockAlarmActivity.this, RingtoneManager.TYPE_ALARM);
                    LogUtils.e("uri" + uri);
                    try {
                        player = new MediaPlayer();
                        if (null == uri)
                            player.setDataSource("file:///android_asset/zero.mp3");
                        else
                            player.setDataSource(ClockAlarmActivity.this, uri);
                        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            player.setAudioStreamType(AudioManager.STREAM_ALARM);
                            player.setLooping(true);
                            player.prepare();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!player.isPlaying()) {
                    player.start();

                    isRing = true;
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            stopRingtone();
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            stopRingtone();
            finish();
            continueSleep();
            return true;
        } else if (keyCode == 124) {
            stopRingtone();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
        if (executorService != null) {
            executorService.shutdown();
            isRing = false;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
        stopRingtone();
        handler.removeCallbacksAndMessages(null);
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                LogUtils.e("暂时失去焦点");

                //if (player.isPlaying()) {
                stopRingtone();
                //}

            } else if (focusChange == AUDIOFOCUS_GAIN) {
                LogUtils.e("获得焦点");

                if (player == null) {
                    //playRingtone();
                } else if (!player.isPlaying()) {
                    //playRingtone();
                }
                // Resume playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                LogUtils.e("长期失去焦点");

                if (player.isPlaying()) {
                    stopRingtone();

                }
                audioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                LogUtils.e("申请成功");

                if (player.isPlaying()) {
                    stopRingtone();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                LogUtils.e("申请失败");

                if (player.isPlaying()) {
                    stopRingtone();
                }
            }
        }
    };

    /**
     * 开启震动
     */
    private void startVibrator() {
        if(!phoneIsInUse(this)){
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] patten = {400, 200, 400, 200};
            mVibrator.vibrate(patten, 0);
        }
    }

    /**
     * 判断是否在接电话
     * @param context
     * @return
     */
    public boolean phoneIsInUse(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return tm.isInCall();
    }

    /**
     * 停止震动
     */
    private void stopVibrator(){
        if(mVibrator != null){
            mVibrator.cancel();
        }
    }
}
