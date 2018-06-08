package com.ctyon.socketclient;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.ctyon.socketclient.app.receive.BootBroadcastReceiver;
import com.ctyon.socketclient.app.receive.SocketReceiverB;
import com.ctyon.socketclient.app.server.SocketService;
import com.ctyon.socketclient.app.server.SocketServiceB;
import com.ctyon.socketclient.project.support.observer.SettingsObserver;
import com.ctyon.socketclient.project.util.CrashHandler;
import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.OkUpload;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.tencent.bugly.Bugly;
import com.wx.common.support.utils.SafeHandler;
import com.wx.common.support.utils.timer.RxTimeUtil;
import com.xuhao.android.libsocket.sdk.OkSocket;

import me.xmai.global.config.Constants;

/**
 * Created by Administrator on 2018/3/9.
 */

public class App extends Application {
    private static Context sContext;
    private OkUpload okUpload;
    private OkDownload okDownload;
    public App() {
        sContext = this;
    }

    public static Context getsContext() {
        return sContext;
    }

    /**
     * you can override this method instead of {@link android.app.Application attachBaseContext}
     *
     * @param base
     */
   /* @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化一个socket
        OkSocket.initialize(this, true);
        //初始化Buglly上报
        //Bugly.init(getApplicationContext(), BuildConfig.BUGLLYAPPID, false);
        //初始化Stetho数据库
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        //崩溃日志抓取
//        CrashHandler.getInstance().init(getsContext());
        //初始化文件上传\下载
        OkGo.getInstance().init(this);
        okDownload = OkDownload.getInstance()
                .setFolder(Constants.MODEL.CACHEPATH.CACHE);
        //定义最大支持下载并发
        okDownload.getThreadPool().setCorePoolSize(1);
        okUpload = OkUpload.getInstance();
        //定义最大支持上传并发
        okUpload.getThreadPool().setCorePoolSize(1);
        Log.i("shipeixian", "SocketClient Application oncreate");
    }


    /**
     * give the configuration to lib in this callback
     *
     * @return
     * 进程保护相关,当socket进程１挂掉后,进程2监测并拉起
     */
   /* @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        Log.i("shipeixian", "socket进程１挂掉后,进程2监测并拉起");
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.ctyon.socketclient:process1",
                SocketService.class.getCanonicalName(),
                BootBroadcastReceiver.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.ctyon.socketclient:process2",
                SocketServiceB.class.getCanonicalName(),
                SocketReceiverB.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }*/

    //低内存时执行
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //System.gc();
    }
}
