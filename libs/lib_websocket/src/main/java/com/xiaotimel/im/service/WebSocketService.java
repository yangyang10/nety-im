package com.xiaotimel.im.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.xiaotimel.im.netty.WebSocketClient;
import com.xiaotimel.im.netty.interfaces.NettyClientInterface;
import com.xiaotimel.im.netty.interfaces.NettyConnectStatusCallback;
import com.xiaotimel.im.observer.WebSocketConnectObservable;
import com.xiaotimel.im.util.LogUtils;
import com.xiaotimel.im.util.ServiceUtils;


/**
 * Created by HHJ on 2020/10/23 5:30 PM
 */
public class WebSocketService extends Service {

    private static final String TAG = "WebSocketClient";

    private static String mWebSocketUrl = "";
    public static Context applicationContext;

    public static void start(Context context, String webSocketUrl) {
        try {
            applicationContext = context.getApplicationContext();
            if (!ServiceUtils.isServiceRunning(applicationContext,WebSocketService.class)) {
                mWebSocketUrl = webSocketUrl;
                Intent starter = new Intent(context, WebSocketService.class);
                context.startService(starter);
            } else {
                if(socketClient == null || TextUtils.isEmpty(socketClient.getConnectUrl())){
                    stop(context);
                    LogUtils.d(TAG, "socketClient is null or url is null resetService");
                }else {
                    LogUtils.d(TAG, "[WebSocketService is running]");
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "[WebSocketService 启动异常]");
            WebSocketConnectObservable.getInstance().connectSlsReport("websocket:connect_state","WebSocketService 启动异常"+ e,null);
        }
    }

    public static void stop(Context context) {
        try {
            Intent stop = new Intent(context, WebSocketService.class);
            context.stopService(stop);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getImService() {
        return mWebSocketUrl;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "[WebSocketService start]");
        nettySocket();
        return START_NOT_STICKY;
    }


    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeNetty();
        LogUtils.d(TAG, "[WebSocketService stop]");
    }

    private void closeNetty(){
        if (socketClient != null) {
            socketClient.close();
            WebSocketClient.Companion.getINSTANCE()
                    .getMBuilder().setConnectCallback(null);
            socketClient.removeCallBack();
            socketClient = null;
        }
    }

    private static NettyClientInterface socketClient;

    private void nettySocket() {
        closeNetty();
        NettyConnectStatusCallback callBack = new NettyConnectStatusCallback() {

            @Override
            public void onConnectFailed() {

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onConnecting() {

            }
        };
        socketClient = WebSocketClient.Companion.getINSTANCE()
                .getMBuilder().setConnectUrl(getImService())
                .setConnectCallback(callBack)
                .build();


    }
}