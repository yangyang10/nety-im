package com.xiaotimel.im.netty

import com.xiaotimel.im.netty.config.ProtocolConfig
import com.xiaotimel.im.netty.constant.NettyConfig
import com.xiaotimel.im.netty.interfaces.NettyClientInterface
import com.xiaotimel.im.netty.interfaces.NettyConnectStatusCallback
import com.xiaotimel.im.netty.interfaces.OnNettyConfigListener
import com.xiaotimel.im.netty.manager.NettyConnectManager

/**
 * Created by HHY on 2021/3/2 4:19 PM
 * Desc: 构建webSocket链接
 **/
class WebSocketClient private constructor() {

    companion object {
        val INSTANCE: WebSocketClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            WebSocketClient()
        }
    }

    val mBuilder: Builder by lazy { Builder() }

    class Builder {

        private var connectUrl: String? = null
        private var callback: NettyConnectStatusCallback? = null
        private var configListener: OnNettyConfigListener? = null
        private var protocolConfig: ProtocolConfig? = null

        /**
         * 服务器地址
         */
        fun setConnectUrl(url: String?): Builder {
            this.connectUrl = url
            return this
        }

        /**
         * 链接状态
         */
        fun setConnectCallback(callback: NettyConnectStatusCallback?): Builder {
            this.callback = callback
            return this
        }

        /**
         * 不配置使用默认的配置
         */
        fun setConfigListener(configListener: OnNettyConfigListener): Builder {
            this.configListener = configListener
            return this
        }

        /**
         * 设置协议配置
         * 不配置时使用默认协议配置
         *
         * @param config 协议配置对象
         * @return Builder实例，支持链式调用
         */
        fun setProtocolConfig(config: ProtocolConfig?): Builder {
            this.protocolConfig = config
            return this
        }

        fun build(): NettyClientInterface {
            val nettyClient = NettyConnectManager.INSTANCE
            if(connectUrl != null && callback != null) {
                // 创建组合的配置监听器，优先使用Builder中的protocolConfig
                val combinedConfigListener = if (protocolConfig != null) {
                    val builderProtocolConfig = protocolConfig!!
                    // 如果有configListener，包装它以提供protocolConfig
                    if (configListener != null) {
                        val originalListener = configListener!!
                        object : OnNettyConfigListener {
                            override fun getReconnectInterval(): Long = originalListener.getReconnectInterval()
                            override fun getHeartbeatInterval(): Long = originalListener.getHeartbeatInterval()
                            override fun getConnectTimeout(): Int = originalListener.getConnectTimeout()
                            override fun getResendCount(): Int = originalListener.getResendCount()
                            override fun getResendInterval(): Int = originalListener.getResendInterval()
                            override fun getProtocolConfig(): ProtocolConfig = builderProtocolConfig
                        }
                    } else {
                        // 没有configListener，创建一个只提供protocolConfig的监听器
                        object : OnNettyConfigListener {
                            override fun getReconnectInterval(): Long = NettyConfig.DEFAULT_RECONNECT_INTERVAL
                            override fun getHeartbeatInterval(): Long = NettyConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND
                            override fun getConnectTimeout(): Int = NettyConfig.DEFAULT_CONNECT_TIMEOUT
                            override fun getResendCount(): Int = NettyConfig.DEFAULT_RESEND_COUNT
                            override fun getResendInterval(): Int = NettyConfig.DEFAULT_RECONNECT_BASE_DELAY_TIME
                            override fun getProtocolConfig(): ProtocolConfig = builderProtocolConfig
                        }
                    }
                } else {
                    // protocolConfig为null，使用原始的configListener
                    configListener
                }
                nettyClient.init(connectUrl, callback!!, combinedConfigListener)
            }
            return nettyClient
        }
    }
}