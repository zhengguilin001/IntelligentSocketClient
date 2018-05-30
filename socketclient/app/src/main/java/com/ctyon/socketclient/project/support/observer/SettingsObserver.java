package com.ctyon.socketclient.project.support.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

/**
 * Created by xmai on 18-3-28.
 * 监听所需Settings.Global
 */

public class SettingsObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private String mUrl;
    private int mMsg;
    private UTYPE mType;

    public SettingsObserver(Context mContext, Handler mHandler, String url,int msg,UTYPE type) {
        super(mHandler);
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mUrl = url;
        this.mMsg = msg;
        this.mType = type;
    }

    /**
     * 当所监听的Uri发生改变时，就会回调此方法
     *
     * @param selfChange 此值意义不大 一般情况下该回调值false
     */
    @Override
    public void onChange(boolean selfChange) {
        Object ob = null;
        if (mType.equals(UTYPE.STRING)){
            String to1 = Settings.Global.getString(mContext.getContentResolver(), mUrl);
            ob = to1;
        }else if (mType.equals(UTYPE.FLOAT)){
            float to2 = Settings.Global.getFloat(mContext.getContentResolver(),mUrl,0);
            ob = to2;
        }else if (mType.equals(UTYPE.INT)){
            int to3 = Settings.Global.getInt(mContext.getContentResolver(),mUrl,0);
            ob = to3;
        }
        mHandler.obtainMessage(mMsg, ob).sendToTarget();
    }
    public enum UTYPE {
        STRING,
        FLOAT,
        INT;
    }
}
