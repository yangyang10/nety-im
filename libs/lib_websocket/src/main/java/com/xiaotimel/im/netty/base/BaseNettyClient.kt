package com.xiaotimel.im.netty.base

import com.xiaotimel.im.netty.ExecutorServiceFactory
import com.xiaotimel.im.netty.constant.NettyConfig
import com.xiaotimel.im.netty.handler.NettySocketReadHandler
import com.xiaotimel.im.netty.interfaces.NettyClientInterface
import com.xiaotimel.im.netty.interfaces.NettyConnectStatusCallback
import com.xiaotimel.im.netty.interfaces.OnNettyConfigListener
import com.xiaotimel.im.netty.manager.CalculationHeartbeatManager
import com.xiaotimel.im.netty.message.HeartbeatMsgBuilder
import com.xiaotimel.im.service.WebSocketService
import com.xiaotimel.im.util.LogUtils
import com.xiaotimel.im.util.NetworkUtils
import io.netty.channel.Channel
import io.netty.channel.socket.SocketChannel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslHandler
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.IdleStateHandler

/**
 * Created by HHY on 2021/3/6 10:20 AM
 * Desc: netty client 基类
 **/
open class BaseNettyClient : NettyClientInterface {

    protected val mTag = "WebSocketClient"
    protected var currentHost: String? = null // 当前连接host
    protected var currentPort = -1 // 当前连接port

    @Volatile
    protected var mIsClose: Boolean = false       //是否已关闭

    @Volatile
    protected var mConnectUrl: String? = ""
    protected var mConnectStatus: Int = NettyConfig.CONNECT_STATE_FAILURE // 连接状态，初始化为连接失败
    protected var mExecutorLoop: ExecutorServiceFactory? = null // 线程池
    protected var mConfigListener: OnNettyConfigListener? = null
    protected var mConnectStatusCallback: NettyConnectStatusCallback? = null// 连接状态回调监听器

    protected var mChannel: Channel? = null

    override fun init(connectUrl:String?, callback: NettyConnectStatusCallback, configListener: OnNettyConfigListener?) {
        this.mConnectStatusCallback = callback
        this.mConfigListener = configListener
        mExecutorLoop = ExecutorServiceFactory()
        mExecutorLoop?.initBossLoopGroup()
    }

    override fun resetConnect() {

    }

    override fun close() {

    }

    override fun sendMsg(msg: Any) {
        sendMsg(msg, true)
    }

    override fun sendMsg(msg: Any, isJoinTimeoutManager: Boolean) {
        if (mChannel == null) {
            LogUtils.e(mTag, "发送消息失败，channel为空\tmessage=$msg", true)
        }
        try {
            mChannel!!.writeAndFlush(msg)
            CalculationHeartbeatManager.instance.resetCurrentTime()
        } catch (ex: Exception) {
            LogUtils.e(mTag, "发送消息失败，reason:" + ex.message + "\tmessage=" + msg, true)
        }
    }

    override fun getHeartbeatMsg(): Any? {
        return HeartbeatMsgBuilder.heartbeatMsg()
    }

    override fun getHandshakeMsg(): Any? {
        return HeartbeatMsgBuilder.heartbeatMsg()
    }

    override fun getChannel(): Channel? {
        return mChannel
    }

    override fun getHeartbeatInterval(): Long {
        if (mConfigListener != null && mConfigListener!!.getHeartbeatInterval() > 0) {
            return mConfigListener!!.getHeartbeatInterval()
        }
        return NettyConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND
    }

    override fun getReconnectInterval(): Long {
        if (mConfigListener != null && mConfigListener!!.getReconnectInterval() > 0) {
            return mConfigListener!!.getReconnectInterval()
        }
        return NettyConfig.DEFAULT_RECONNECT_INTERVAL
    }

    override fun getLoopGroup(): ExecutorServiceFactory? {
        return mExecutorLoop
    }

    override fun isClose(): Boolean {
        return mIsClose
    }

    override fun getSslHandler(socketChannel: SocketChannel?): SslHandler? {
        if (socketChannel == null) return null
        if (mConnectUrl != null && mConnectUrl!!.startsWith("wss")) {

            val sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build()
            return sslCtx.newHandler(socketChannel.alloc(), currentHost, currentPort)
        }
        return null
    }


    /**
     * 移除指定handler
     * @param handlerName
     */
    private fun removeHandler(handlerName: String) {
        try {
            if (mChannel?.pipeline()?.get(handlerName) != null) {
                mChannel?.pipeline()?.remove(handlerName)
            } else {
                LogUtils.d(mTag, "移除找不到handler [$handlerName]", true)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogUtils.e(mTag, "移除handler失败，handlerName=$handlerName", true)
        }
    }

    /**
     * bootstrap链接超时时间
     */
    protected fun getConnectTimeout(): Int {
        if (mConfigListener != null && mConfigListener!!.getConnectTimeout() > 0) {
            return mConfigListener!!.getConnectTimeout()
        }
        return NettyConfig.DEFAULT_CONNECT_TIMEOUT
    }

    protected fun closeChannel() {
        try {
            if (mChannel != null) {
                removeHandler(IdleStateHandler::class.java.simpleName)
                removeHandler(NettySocketReadHandler::class.java.simpleName)
                mChannel?.close()
                mChannel?.eventLoop()?.shutdownGracefully()
            }
        } catch (ex: Exception) {
            LogUtils.e(mTag, "关闭channel异常，reason:" + ex.message, true)
        } finally {
            mChannel = null
        }
    }

    /**
     * 回调连接状态
     *
     * @param connectStatus
     */
    protected fun onConnectStatusCallback(connectStatus: Int) {
        this.mConnectStatus = connectStatus
        when (connectStatus) {
            NettyConfig.CONNECT_STATE_CONNECTING -> {
                LogUtils.d(mTag, "webSocket连接中...", true)
                if (mConnectStatusCallback != null) {
                    mConnectStatusCallback?.onConnecting()
                }

            }
            NettyConfig.CONNECT_STATE_SUCCESSFUL -> {
                LogUtils.d(mTag, "连接成功", true)
                if (mConnectStatusCallback != null) {
                    mConnectStatusCallback?.onConnected()
                }
                // 连接成功，发送握手消息
                val handshakeMsg = getHandshakeMsg()
                if (handshakeMsg != null) {
                    LogUtils.d(mTag, "发送握手消息", true)
                    sendMsg(handshakeMsg)
                }
            }
            NettyConfig.CONNECT_STATE_FAILURE -> {
                LogUtils.e(mTag, "连接失败", true)
                if (mConnectStatusCallback != null) {
                    mConnectStatusCallback?.onConnectFailed()
                }
            }
            else -> {
                LogUtils.e(mTag, "im连接失败", true)
                if (mConnectStatusCallback != null) {
                    mConnectStatusCallback?.onConnectFailed()
                }
            }
        }
    }

    /**
     * 网络是否可用
     * @return
     */
    protected fun isNetworkAvailable(): Boolean {
        if(WebSocketService.applicationContext == null){
            LogUtils.e(mTag, "applicationContext 为空请初始化")
            return false
        }
        return NetworkUtils.isConnected(WebSocketService.applicationContext)
    }

    override fun removeCallBack() {
        mConnectStatusCallback = null
    }

    override fun getConnectUrl(): String? {
        return mConnectUrl
    }

}