package com.xiaotimel.im.netty.manager

import android.text.TextUtils
import com.xiaotimel.im.netty.base.BaseNettyClient
import com.xiaotimel.im.netty.constant.NettyConfig
import com.xiaotimel.im.netty.handler.NettySocketInitializer
import com.xiaotimel.im.netty.handler.NettySocketReadHandler
import com.xiaotimel.im.netty.interfaces.NettyConnectStatusCallback
import com.xiaotimel.im.netty.interfaces.OnNettyConfigListener
import com.xiaotimel.im.observer.WebSocketConnectObservable
import com.xiaotimel.im.util.LogUtils
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.util.concurrent.GenericFutureListener
import java.net.URI

/**
 * Created by HHY on 2021/3/1 5:48 PM
 * Desc: netty 链接管理
 *
 * netty 服务器 使用IDE打开，直接运行可以自行调试
 * https://github.com/no-today/netty-websocket-protobuf
 *
 */
class NettyConnectManager private constructor() : BaseNettyClient() {


    companion object {
        val INSTANCE: NettyConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NettyConnectManager()
        }
        private const val CONNECT_STATE_EVENT = "websocket:connect_state"
    }

    private var mBootstrap: Bootstrap? = null

    @Volatile
    private var mIsConnecting: Boolean = false   //是否在链接中

    private fun initBootstrap() {
        val loopGroup: EventLoopGroup = NioEventLoopGroup(1)
        mBootstrap = Bootstrap()
        mBootstrap?.group(loopGroup)?.channel(NioSocketChannel::class.java)
        // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
        mBootstrap?.option(ChannelOption.SO_KEEPALIVE, true)
        // 设置禁用nagle算法
        mBootstrap?.option(ChannelOption.TCP_NODELAY, true)
        // 设置连接超时时长
        mBootstrap?.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout())
        // 设置初始化Channel
        mBootstrap?.handler(NettySocketInitializer(this))
    }

    override fun init(
        connectUrl: String?,
        callback: NettyConnectStatusCallback,
        configListener: OnNettyConfigListener?
    ) {
        super.init(connectUrl, callback, configListener)
        close()
        this.mConnectUrl = connectUrl
        mIsClose = false
        resetConnect()
    }

    override fun resetConnect() {

        if (!mIsClose && !mIsConnecting) {
            mIsConnecting = true
            closeChannel()
            // 回调连接状态
            onConnectStatusCallback(NettyConfig.CONNECT_STATE_CONNECTING)
            mExecutorLoop?.execBossTask(ResetConnectRunnable())
        }

    }

    override fun close() {
        super.close()
        if (mIsClose) {
            return
        }
        mIsClose = true
        try {
            //关闭通道
            closeChannel()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        try {
            //关闭通道容器
            if (mBootstrap != null) {
                mBootstrap?.group()?.shutdownGracefully()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        try {
            // 释放线程池
            mExecutorLoop?.destroy()
        } catch (ex: Exception) {
            ex.printStackTrace()
            LogUtils.e(mTag, "释放线程池失败", true)
        } finally {
            mIsConnecting = false
            mChannel = null
            mBootstrap = null
        }
    }


    /**
     * 重连任务
     */
    private inner class ResetConnectRunnable() : Runnable {
        override fun run() {

            try {
                mExecutorLoop?.destroyWorkLoopGroup()
                while (!mIsClose) {
                    if (!isNetworkAvailable()) {
                        try {
                            LogUtils.i(mTag, "当前网络状态【不可用】", true)
                            WebSocketConnectObservable.getInstance().connectSlsReport(CONNECT_STATE_EVENT,"当前网络状态【不可用】",null)
                            Thread.sleep(getReconnectInterval())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        continue
                    }
                    // 网络可用才进行连接
                    var status: Int
                    if (reConnect().also { status = it } == NettyConfig.CONNECT_STATE_SUCCESSFUL) {
                        onConnectStatusCallback(status)
                        break
                    }
                    if (status == NettyConfig.CONNECT_STATE_FAILURE) {
                        onConnectStatusCallback(status)
                    }
                }
            } finally {
                mIsConnecting = false
            }
        }

        /**
         * 重连，首次连接也认为是第一次重连
         * @return
         */
        private fun reConnect(): Int {
            if (!mIsClose) {
                try {
                    if (mBootstrap != null) {
                        mBootstrap?.group()?.shutdownGracefully()
                    }
                } finally {
                    mBootstrap = null
                }
                initBootstrap()
                return connectServer()
            }
            return NettyConfig.CONNECT_STATE_FAILURE
        }

        /**
         * 连接服务器
         * @return
         */
        private fun connectServer(): Int {
            // 如果服务器地址无效，直接回调连接状态，不再进行连接
            if (mConnectUrl.isNullOrBlank()) {
                mIsClose = true
                WebSocketConnectObservable.getInstance().connectSlsReport(CONNECT_STATE_EVENT,"webSocket链接地址为空",null)
                return NettyConfig.CONNECT_STATE_FAILURE
            }

            while (!mIsClose) {
                // 如果服务器地址无效，直接回调连接状态，不再进行连接
                if (TextUtils.isEmpty(mConnectUrl)) {
                    LogUtils.e(mTag, "webSocket [链接地址为空，无效]", true)
                    mIsClose = true
                    close()
                    WebSocketConnectObservable.getInstance().connectSlsReport(CONNECT_STATE_EVENT,"webSocket链接地址 无效",null)
                    return NettyConfig.CONNECT_STATE_FAILURE
                }
                //获取地址的链接和端口
                val webSocketURI = URI(mConnectUrl)
                val scheme = if (webSocketURI.scheme == null) "ws" else webSocketURI.scheme
                currentHost = if (webSocketURI.host == null) "127.0.0.1" else webSocketURI.host
                currentPort = webSocketURI.port
                if (currentPort == -1) {
                    LogUtils.e(mTag, "[获取端口失败 port = -1]")
                    mConnectUrl?.let {
                        if (scheme == "ws") {
                            currentPort = 80
                            LogUtils.e(mTag, "ws 默认80端口")
                        } else if (scheme == "wss") {
                            currentPort = 433
                            LogUtils.e(mTag, "wss 默认433端口")
                        }
                    }
                }
                LogUtils.i(mTag, "currentHost $currentHost  currentPort= $currentPort")

                //链接失败一个周期进行5次重试链接
                for (j in 0..NettyConfig.DEFAULT_RECONNECT_COUNT) {
                    if (mIsClose || !isNetworkAvailable()) {
                        return NettyConfig.CONNECT_STATE_FAILURE
                    }
                    if (mConnectStatus != NettyConfig.CONNECT_STATE_CONNECTING) {
                        onConnectStatusCallback(NettyConfig.CONNECT_STATE_CONNECTING)
                    }
                    val reportLogStr = "正在进行第『$j』次连接，当前重连延时时长为『${(j * 10) * 1000 + getReconnectInterval()}』"
                    LogUtils.d(mTag, reportLogStr)

                    WebSocketConnectObservable.getInstance().connectSlsReport(CONNECT_STATE_EVENT,reportLogStr,null)
                    try {
                        try {
                            val cf = mBootstrap?.connect(currentHost, currentPort)?.sync()
                            cf?.addListener(object : GenericFutureListener<ChannelFuture> {
                                override fun operationComplete(channelFuture: ChannelFuture?) {
                                    channelFuture?.let {
                                        if (it.isSuccess) {
                                            //http、https链接成功，添加webSocket协议
                                            mChannel = channelFuture.channel()
                                            val handler = mChannel?.pipeline()
                                                ?.get(NettySocketReadHandler::class.java.simpleName)
                                            if (handler is NettySocketReadHandler) {
                                                val handShaker =
                                                    WebSocketClientHandshakerFactory.newHandshaker(
                                                        webSocketURI,
                                                        WebSocketVersion.V13,
                                                        null,
                                                        true,
                                                        DefaultHttpHeaders()
                                                    )
                                                handler.setHandshaker(handShaker)
                                                handShaker.handshake(mChannel)
                                                handler.handshakeFuture()
                                            }
                                        }
                                    }
                                }
                            })
                            mChannel = cf?.channel()
                        } catch (e: Exception) {
                            mChannel = null
                            LogUtils.e(mTag, "连接webSocket失败")
                            WebSocketConnectObservable.getInstance().connectSlsReport(CONNECT_STATE_EVENT,"连接异常 $e",null)
                        }
                        if (mChannel != null) {
                            return NettyConfig.CONNECT_STATE_SUCCESSFUL
                        } else {
                            // 连接失败，则线程休眠(n + 2) * 1000 + 重连间隔时长 阶梯式增长
                            Thread.sleep((j * 10) * 1000 + getReconnectInterval())
                            if (mChannel != null) {
                                return NettyConfig.CONNECT_STATE_SUCCESSFUL
                            }
                        }
                    } catch (e: Exception) {
                        close()
                        break
                    }
                }
            }
            return NettyConfig.CONNECT_STATE_FAILURE
        }
    }
}