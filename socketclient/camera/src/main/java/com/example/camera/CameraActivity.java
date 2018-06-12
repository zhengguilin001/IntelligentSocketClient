package com.example.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import me.xmai.global.config.Constants;

public class CameraActivity extends Activity implements InitTimetoTakePic.takePicCallback{
    private final static String TAG = "shipeixian";
    FrameLayout fl_preview;
    private InitTimetoTakePic timetoTakePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        setContentView(R.layout.activity_camera);
        // 创建预览类，并与Camera关联，最后添加到界面布局中

        fl_preview = (FrameLayout) findViewById(R.id.camera_preview);

        //创建定时拍照任务
        InitTimetoTakePic itt = InitTimetoTakePic.getInstance(CameraActivity.this,this);
        itt.initView(fl_preview);
    }

    @Override
    protected void onDestroy() {
        // 回收Camera资源
        super.onDestroy();
        InitTimetoTakePic.getInstance(CameraActivity.this,this).releaseCarema();
    }

    @Override
    public void takePicSuccess(String path) {
        Settings.Global.putString(getContentResolver(),
                Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH,path);
        sendBroadcast(new Intent("com.ctyon.shawn.UPLOAD_PHOTO"));
        Log.d(TAG, "takePicSuccess: "+path);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3*1000);
    }

    @Override
    public void takePicFailed(String msg) {
        Log.d(TAG, "takePicFailed: "+msg);
        finish();
    }
}
