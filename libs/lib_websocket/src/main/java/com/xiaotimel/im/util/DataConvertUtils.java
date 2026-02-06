package com.xiaotimel.im.util;

/**
 * 将基本数据类型转换为byte数组，以及反向转换的方法
 *
 * Created on 2017/6/8.
 */

public class DataConvertUtils {

    /**
     * 将16位的short转换成byte数组
     *
     * @param s
     *            short
     * @return byte[] 长度为2
     * */
    public static byte[] shortToByteArray(short s) {
        byte[] b = new byte[2];
        b[0] = (byte) (s & 0xff);
        b[1] = (byte) (s >> 8 & 0xff);
        return b;
    }

    /**
     * 低字节数组到short的转换
     * @param b byte[]
     * @return short
     */
    public static short bytesToShort(byte[] b) {
        int s = 0;
        if (b[1] >= 0) {
            s = s + b[1];
        } else {
            s = s + 256 + b[1];
        }
        s = s * 256;
        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        short result = (short)s;
        return result;
    }


    /**
     * 将int类型的数据转换为byte数组
     * 原理：将int数据中的四个byte取出，分别存储
     * @param n int数据
     * @return 生成的byte数组
     */
    public static byte[] intToBytes(int n){
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }


    /**
     * 将低字节数组转换为int
     * @param b byte[]
     * @return int
     */
    public static int bytesToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[3-i] >= 0) {
                s = s + b[3-i];
            } else {
                s = s + 256 + b[3-i];
            }
            s = s * 256;
        }
        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        return s;
    }
}