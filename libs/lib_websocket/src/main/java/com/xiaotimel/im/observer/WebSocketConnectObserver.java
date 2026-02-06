package com.xiaotimel.im.observer;

import java.util.Map;

/**
 * Created by HHY on 2021/3/2 4:19 PM
 * Desc: webSocket链接
 **/
public interface WebSocketConnectObserver {
    void connectSlsReport(String eventId, String eventName, Map<String, String> params);
}