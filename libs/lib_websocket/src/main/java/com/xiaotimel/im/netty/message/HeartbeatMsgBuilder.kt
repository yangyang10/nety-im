package com.xiaotimel.im.netty.message

import com.xiaotimel.im.netty.manager.CalculationHeartbeatManager
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame

/**
 * Created by HHY on 2021/3/4 6:39 PM
 * Desc: 心跳构造器
 **/
object HeartbeatMsgBuilder {

    /**
     * 逗号分隔
     * id,时间戳,消耗的时间
     */
    fun heartbeatMsg(): PingWebSocketFrame {
        CalculationHeartbeatManager.instance.resetCurrentTime()
        val currentTime = System.currentTimeMillis()
        val id = "im_$currentTime"
        val takeTime = CalculationHeartbeatManager.instance.mHeartbeatUsedTime.toString()
        val stringBuilder = StringBuilder()
        stringBuilder.append(id).append(",").append(currentTime).append(",").append(takeTime)
        return PingWebSocketFrame(Unpooled.wrappedBuffer(stringBuilder.toString().toByteArray()))
    }

}