package com.xiaotimel.im.util;

import android.util.Log;

/**
 * Desc:日志打印
 */
public class LogUtils {

    public final static int MAX_LENGTH = 4000;
    public static boolean isOpenLog = true;
    public static void v(String tag, String msg) {
        v(tag, msg, true);
    }

    public static void v(String tag, final String format, final Object... obj) {
        final String log = obj == null ? format : String.format(format, obj);
        v(tag, log, true);
    }

    public static void v(String tag, String msg, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.v(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.v(tag, msg.substring(i, end));
                }
            }
        }
    }

    public static void d(String tag, String msg) {
        d(tag, msg, true);
    }

    public static void d(String tag, final String format, final Object... obj) {
        final String log = obj == null ? format : String.format(format, obj);
        d(tag, log, true);
    }

    public static void d(String tag, String msg, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.d(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.d(tag, msg.substring(i, end));
                }
            }
        }
    }

    public static void i(String tag, String msg) {
        i(tag, msg, true);
    }

    public static void i(String tag, final String format, final Object... obj) {
        final String log = obj == null ? format : String.format(format, obj);
        i(tag, log, true);
    }

    public static void i(String tag, String msg, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.i(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.i(tag, msg.substring(i, end));
                }
            }
        }
    }

    public static void w(String tag, String msg) {
        w(tag, msg, true);
    }

    public static void w(String tag, final String format, final Object... obj) {
        final String log = obj == null ? format : String.format(format, obj);
        w(tag, log, true);
    }

    public static void w(String tag, String msg, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.w(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.w(tag, msg.substring(i, end));
                }
            }
        }
    }

    public static void e(String tag, String msg) {
        e(tag, msg, true);
    }

    public static void e(String tag, final String format, final Object... obj) {
        final String log = obj == null ? format : String.format(format, obj);
        e(tag, log, true);
    }

    public static void e(String tag, String msg, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.e(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.e(tag, msg.substring(i, end));
                }
            }
        }
    }

    public static void e(String tag, Throwable e, boolean writeFile) {
        e(tag, "", e, true, writeFile);
    }

    /**
     * 异常写入
     */
    public static void e(String tag, Throwable e, boolean isReport, boolean writeFile) {
        e(tag, "", e, isReport, writeFile);
    }

    public static void e(String tag, String message, Throwable e, boolean writeFile) {
        e(tag, message, e, true, writeFile);
    }

    /**
     * 异常写入
     */
    public static void e(String tag, String msg, Throwable e, boolean isReport, boolean writeFile) {
        if (isOpenLog) {
            if(msg == null){
                return;
            }
            int length = msg.length();
            if(length < MAX_LENGTH){
                Log.e(tag, msg);
            }else{
                for(int i = 0; i < length; i += MAX_LENGTH){
                    int end = i + MAX_LENGTH;
                    if(end > length){
                        end = length;
                    }
                    Log.e(tag, msg.substring(i, end));
                }
            }
        }
    }
}
