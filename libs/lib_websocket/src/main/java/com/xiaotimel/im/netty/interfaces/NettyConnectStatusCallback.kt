package com.xiaotimel.im.netty.interfaces

/**
 * Created by HHY on 2021/3/1 6:35 PM
 * Desc: tcp 链接状态监听
 **/
interface NettyConnectStatusCallback {

    /**
     * 连接中
     */
    fun onConnecting()

    /**
     * 连接成功
     */
    fun onConnected()

    /**
     * 连接失败
     */
    fun onConnectFailed()
}