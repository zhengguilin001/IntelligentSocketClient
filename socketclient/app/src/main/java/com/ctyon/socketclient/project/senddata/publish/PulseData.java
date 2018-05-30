package com.ctyon.socketclient.project.senddata.publish;

import android.util.Log;

import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import me.xmai.global.config.Constants;

/**
 * Created by Administrator on 2018/3/14.
 * ...定义心跳管理器需要的心跳数据类型...
 */

public class PulseData implements IPulseSendable {
    private static final String TAG = PulseData.class.getSimpleName();
    private String str = "";

    public PulseData() {
        JSONObject jsonObject = new JSONObject();
        //构造终端发送心跳
        try {
            jsonObject.put(Constants.MODEL.DATA.DATA_TYPE, Constants.COMMON.TYPE.TYPE_PULSE_C);
            jsonObject.put(Constants.MODEL.DATA.DATA_IDENT, 456873);
            str = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] parse() {
        //根据服务器的解析规则,构建byte数组
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putShort((short) Constants.COMMON.TYPE.TYPE_PULSE_C);
        bb.putShort((short) body.length);
        Log.d(TAG, "parse: " + (short) body.length);
        bb.put(body);
        return bb.array();
    }
}