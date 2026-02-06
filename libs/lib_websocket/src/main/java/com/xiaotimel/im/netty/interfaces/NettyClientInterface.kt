package com.xiaotimel.im.netty.interfaces

import com.xiaotimel.im.netty.ExecutorServiceFactory
import io.netty.channel.Channel
import io.netty.channel.socket.SocketChannel
import io.netty.handler.ssl.SslHandler

/**
 * Created by HHY on 2021/3/2 10:49 AM
 * Desc: netty 链接操作
 **/
interface NettyClientInterface {

    /**
     * 初始化
     */
    fun init(connectUrl:String?, callback: NettyConnectStatusCallback,configListener: OnNettyConfigListener?)

    /**
     * 重新连接
     */
    fun resetConnect()

    /**
     * 关闭channel 整个通道
     */
    fun close()

    fun sendMsg(msg: Any)
    /**
     * 发送消息
     * @param msg 消息体
     * @param isJoinTimeoutManager true添加到任务管理器
     */
    fun sendMsg(msg: Any, isJoinTimeoutManager: Boolean)

    /**
     * 心跳包
     */
    fun getHeartbeatMsg(): Any?

    /**
     * 登录包
     */
    fun getHandshakeMsg():Any?

    /**
     * 获取当前通道
     */
    fun getChannel(): Channel?

    /**
     * 心跳间隔时间
     */
    fun getHeartbeatInterval():Long

    /**
     * 重连时间间隔
     */
    fun getReconnectInterval():Long

    /**
     * 获取线程池
     */
    fun getLoopGroup(): ExecutorServiceFactory?

    /**
     * 链接是否关闭
     */
    fun isClose():Boolean

    /**
     * 添加ssl
     */
    fun getSslHandler(socketChannel: SocketChannel?): SslHandler?

    /**
     * 移除监听
     */
    fun removeCallBack()

    /**
     * 链接地址
     */
    fun getConnectUrl():String?

}