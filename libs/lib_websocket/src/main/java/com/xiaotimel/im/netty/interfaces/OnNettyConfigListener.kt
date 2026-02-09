package com.xiaotimel.im.netty.interfaces

import com.xiaotimel.im.netty.config.ProtocolConfig

/**
 * Created by HHY on 2021/3/2 3:11 PM
 * Desc: webSocket(netty) 配置连接信息
 **/
interface OnNettyConfigListener {

    /**
     * 消息分发
     *
     * @param msg
     */
//    fun dispatchMsg(msg: ProtobufMessageModule.Message?)

    /**
     * 获取重连间隔时长
     *
     * @return
     */
    fun getReconnectInterval(): Long

    /**
     * 获取心跳时间
     */
    fun getHeartbeatInterval():Long

    /**
     * 获取连接超时时长
     *
     * @return
     */
    fun getConnectTimeout(): Int

    /**
     * 消息发送超时重发次数
     *
     * @return
     */
    fun getResendCount(): Int

    /**
     * 消息发送超时重发间隔
     * @return
     */
    fun getResendInterval(): Int

    /**
     * 获取协议配置
     * 返回null时使用默认配置
     *
     * @return 协议配置对象
     */
    fun getProtocolConfig(): ProtocolConfig? = null
}