package com.ctyon.socketclient.project.support;

import android.util.Log;

import com.ctyon.socketclient.project.util.MyBytesUtils;
import com.xuhao.android.libsocket.sdk.protocol.IHeaderProtocol;
import com.xuhao.android.libsocket.utils.BytesUtils;

import java.nio.ByteOrder;

/**
 * Created by Administrator on 2018/3/12.
 * | Type(2 byte) | Length(2 byte) | json marshal data
 * 前2 字节为指令类型字段, 为大端序的无符号短整型, 标识该指令类型(指令json 数据中的
 * "type" 字段与类型值相同)
 * 后2 字节为长度标识, 为大端序的无符号短整型, 标识后面所带数据的长度(字节数),
 */

public class MyHeaderProtocol implements IHeaderProtocol {
    private static final String TAG = MyHeaderProtocol.class.getSimpleName();

    @Override
    public int getHeaderLength() {
        //Returns a custom header length that automatically parses the length of the head
        return 4;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        //The length of the body is parsed from the header, byteOrder is the sequence of bytes that you configured in the parameter, which can be easily parsed using ByteBuffer
        if (header == null || header.length == 0) {
            return 0;
        }
        if (ByteOrder.BIG_ENDIAN.toString().equals(byteOrder.toString())) {
            return MyBytesUtils.bytesToInt2(header, 0);
        } else {
            return MyBytesUtils.bytesToInt(header, 0);
        }
    }
}
