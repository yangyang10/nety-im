package com.xiaotimel.im.netty

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

        fun build(): NettyClientInterface {
            val nettyClient = NettyConnectManager.INSTANCE
            if(connectUrl != null && callback != null) {
                nettyClient.init(connectUrl, callback!!, configListener)
            }
            return nettyClient
        }
    }
}