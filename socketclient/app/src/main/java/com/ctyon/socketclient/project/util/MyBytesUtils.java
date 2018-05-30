package com.ctyon.socketclient.project.util;

/**
 * Created by Administrator on 2018/3/10.
 */

public class MyBytesUtils {
    /**
     * 小字节序
     * <p>
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 大字节序
     * <p>
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * 小字节序
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) (
                (src[offset] & 0xFF)
                        | ((src[offset + 1] & 0xFF) << 8)
//                | ((src[offset + 2] & 0xFF) << 16)
//                | ((src[offset + 3] & 0xFF) << 24)
        );
        return value;
    }

    /**
     * 大字节序
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (
//                (
//                        (src[offset] & 0xFF) << 24)
//                | ((src[offset + 1] & 0xFF) << 16)
//                |
                ((src[offset + 2] & 0xFF) << 8)
                        | (src[offset + 3] & 0xFF)
        );
        return value;
    }

    /**
     * 通过byte数组取到short
     *
     * @param b
     * @param index 第几位开始取
     * @return
     */
    public static short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }


    /**
     * 生成打印16进制日志所需的字符串
     *
     * @param data 数据源
     * @return 字符串给日志使用
     */
    public static String toHexStringForLog(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                String tempHexStr = Integer.toHexString(data[i] & 0xff) + " ";
                tempHexStr = tempHexStr.length() == 2 ? "0" + tempHexStr : tempHexStr;
                sb.append(tempHexStr);
            }
        }
        return sb.toString();
    }
}

