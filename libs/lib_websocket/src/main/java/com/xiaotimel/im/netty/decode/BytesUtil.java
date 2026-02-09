package com.xiaotimel.im.netty.decode;

import android.annotation.SuppressLint;

import java.nio.charset.Charset;

/**
 * 标题：
 * 描述：TCP字节码
 * 作者：hhy
 * 创建时间：2017/9/19 14:20
 */

public class BytesUtil {

    public static byte[] getBytes(byte data) {
        byte[] bytes = new byte[1];
        bytes[0] = data;
        return bytes;
    }

    /**
     * 网络字节 java to c++ char 转byte
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (data);
        return bytes;
    }

    @SuppressLint("NewApi")
    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    /**
     * 网络字节java to c++ long 转byte
     *
     * @param n
     * @return
     */
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);

        return b;
    }

    /**
     * 网络字节java to c++ int转byte
     *
     * @param n
     * @return
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    /**
     * 网络字节 java to c++ short转byte
     *
     * @param n
     * @return
     */
    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    /**
     * 网络字节 c++ to java byte[]转long
     *
     * @param array
     * @return
     */
    public static long bytesToLong(byte[] array) {
        return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40) | (((long) array[3] & 0xff) << 32)
                | (((long) array[4] & 0xff) << 24) | (((long) array[5] & 0xff) << 16) | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
    }

    /**
     * 网络字节 c++ to java byte[]转int
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte b[]) {

        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }

    /**
     * 网络字节 c++ to java byte[]转short
     *
     * @param b
     * @return
     */
    public static short bytesToShort(byte[] b) {

        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

	/* ---------------------------- merger ----------------- */


    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    /* ---------------------------- merger -----------------*/
    public static byte[] mergeBytes(byte[] pByteA, byte[] pByteB) {
        int aCount = pByteA.length;
        int bCount = pByteB.length;
        byte[] b = new byte[aCount + bCount];
        for (int i = 0; i < aCount; i++) {
            b[i] = pByteA[i];
        }
        for (int i = 0; i < bCount; i++) {
            b[aCount + i] = pByteB[i];
        }
        return b;
    }

    public static byte[] subByte(byte[] b, int start, int length) {
        if (b.length == 0 || length == 0 || start + length > b.length) {
            return null;
        }
        byte[] bjq = new byte[length];
        for (int i = 0; i < length; i++) {
            bjq[i] = b[start + i];
        }
        return bjq;
    }

    public static boolean isIndexOfSubBytes(byte[] desBytes, byte[] subBytes) {
        if (desBytes.length >= subBytes.length) {
            int count = 0;
            int length = subBytes.length;
            for (int i = 0; i < subBytes.length; i++) {
                if (desBytes[i] == subBytes[i]) {
                    count++;
                }
            }
            if (count == length) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断头部是否相等
     *
     * @param bytes1
     * @param bytes2
     * @return
     */
    public static boolean isHeadSame(byte[] bytes1, byte[] bytes2) {
        int length;
        if (bytes1.length < bytes2.length) {
            length = bytes1.length;
        } else {
            length = bytes2.length;
        }
        if (length > 0) {
            int count = 0;
            for (int i = 0; i < length; i++) {
                if (bytes1[i] == bytes2[i]) {
                    count++;
                }
            }
            if (count == length) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断头部的开始位置
     *
     * @param desBytes
     * @param subBytes
     * @return
     */
    public static int headStarIndex(byte[] desBytes, byte[] subBytes) {
        int desBytesLength = desBytes.length;
        int subBytesLength = subBytes.length;
        if (desBytesLength >= subBytesLength) {
            for (int i = 0; i < desBytesLength; i++) {
                if (desBytes[i] == subBytes[0] && desBytesLength >= i + subBytesLength) {
                    int count = 0;
                    for (int j = 0; j < subBytes.length; j++) {
                        if (desBytes[i + j] == subBytes[j]) {
                            count++;
                        }
                    }
                    if (count == subBytesLength) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }
}
