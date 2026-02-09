package com.xiaotimel.im.netty.handler

import com.xiaotimel.im.netty.constant.NettyConfig
import com.xiaotimel.im.netty.decode.WlwFrameDecoder
import com.xiaotimel.im.netty.interfaces.NettyClientInterface
import com.xiaotimel.im.util.LogUtils
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.timeout.IdleStateHandler
import java.util.concurrent.TimeUnit


/**
 * Created by HHY on 2021/3/1 2:05 PM
 * Desc: netty 编解码、心跳配置
 **/
class NettySocketInitializer : ChannelInitializer<SocketChannel> {

    private val mClientInterface: NettyClientInterface

    constructor(client: NettyClientInterface) {
        this.mClientInterface = client
    }

    override fun initChannel(socketChannel: SocketChannel?) {
        socketChannel?.let {
            val pipeline: ChannelPipeline = socketChannel.pipeline()

            if(mClientInterface.getSslHandler(socketChannel) != null){
                pipeline.addLast("ssl",mClientInterface.getSslHandler(socketChannel))
                LogUtils.d(TAG,"添加sslHandler")
            }

            pipeline.addLast(IdleStateHandler::class.java.simpleName, IdleStateHandler(
                    NettyConfig.READER_IDLE_TIME,
                    NettyConfig.WRITER_IDLE_TIME, NettyConfig.ALL_IDLE_TIME, TimeUnit.SECONDS))

            pipeline.addLast("HttpClientCodec", HttpClientCodec())
            pipeline.addLast("HttpObjectAggregator", HttpObjectAggregator(1024 * 10));

            pipeline.addLast("framer", WlwFrameDecoder())
            pipeline.addLast("decoder", ByteArrayDecoder())
            pipeline.addLast("encoder", ByteArrayEncoder())
            pipeline.addLast(NettySocketReadHandler::class.java.simpleName, NettySocketReadHandler(mClientInterface))
        }
    }

    companion object {
        private const val TAG = "WebSocketClient"
    }
}