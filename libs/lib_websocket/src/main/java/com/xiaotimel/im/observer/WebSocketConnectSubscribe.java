package com.xiaotimel.im.observer;

import java.util.Map;

/**
 * Created by HHY on 2021/3/2 4:19 PM
 * Desc: webSocket链接
 **/
public interface WebSocketConnectSubscribe {

    //注册一个观察者
    void attach(WebSocketConnectObserver observer);

    //移除已注册的观察者
    void detach(WebSocketConnectObserver observer);

    //链接配置异常
    void connectSlsReport(String eventId, String eventName, Map<String,String> value);

}