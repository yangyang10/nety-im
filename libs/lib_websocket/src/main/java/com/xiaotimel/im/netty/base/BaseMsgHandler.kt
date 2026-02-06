package com.xiaotimel.im.netty.base

import com.xiaotimel.im.netty.interfaces.NettyClientInterface

/**
 * Created by HHY on 2021/3/3 4:18 PM
 * Desc: channel消息处理基类(消息分发)
 **/
abstract class BaseMsgHandler<T> {
    private var imHandler: BaseMsgHandler<T>? = null
    fun getImHandler(): BaseMsgHandler<T>? {
        return imHandler
    }

    fun setImHandler(imHandler: BaseMsgHandler<T>?) {
        this.imHandler = imHandler
    }

    /**消息处理 */
    abstract fun doHandler(message: T)

    var nettyClient: NettyClientInterface? = null
        private set

    fun bindNettyClient(client: NettyClientInterface){
        this.nettyClient = client
    }
}