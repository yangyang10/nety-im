package com.xiaotimel.im.observer;

import java.util.Map;
import java.util.Vector;

/**
 * Created by HHY on 2021/3/2 4:19 PM
 * Desc: webSocket链接
 **/
public class WebSocketConnectObservable implements WebSocketConnectSubscribe {

    private static WebSocketConnectObservable observable;

    public static WebSocketConnectObservable getInstance() {
        if (observable == null) {
            synchronized (WebSocketConnectObservable.class) {
                if (observable == null) {
                    observable = new WebSocketConnectObservable();
                }
            }
        }
        return observable;
    }

    private WebSocketConnectObservable() {
    }

    private final Vector<WebSocketConnectObserver> observersVector = new Vector<>();

    @Override
    public void attach(WebSocketConnectObserver observer) {
        observersVector.add(observer);
    }

    @Override
    public void detach(WebSocketConnectObserver observer) {
        observersVector.remove(observer);
    }

    @Override
    public void connectSlsReport(String eventId, String eventName, Map<String, String> params) {
        if (observersVector.isEmpty()) {
            return;
        }
        WebSocketConnectObserver observer = observersVector.lastElement();
        if (observer != null) {
            observer.connectSlsReport(eventId, eventName,params);
        }
    }

    public void clearAll(){
        observersVector.clear();
    }
}
