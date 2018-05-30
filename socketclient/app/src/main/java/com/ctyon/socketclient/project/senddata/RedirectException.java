package com.ctyon.socketclient.project.senddata;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;

/**
 * Created by Administrator on 2018/3/12.
 */

public class RedirectException extends RuntimeException {
    public ConnectionInfo redirectInfo;

    public RedirectException(ConnectionInfo redirectInfo) {
        this.redirectInfo = redirectInfo;
    }
}

