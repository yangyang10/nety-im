package com.xiaotimel.im.netty.message

import com.xiaotimel.im.netty.base.BaseMsgHandler
import com.xiaotimel.im.netty.manager.CalculationHeartbeatManager
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame

/**
 * Created by HHY on 2021/3/3 4:20 PM
 * Desc: 仅处理 维持netty通道消息
 **/
class ChannelMsgHandler : BaseMsgHandler<Any>() {

    override fun doHandler(message: Any) {
        if (message is PongWebSocketFrame) {
            CalculationHeartbeatManager.instance.calculationTakeTime()
        } else if (message is PingWebSocketFrame) {
            CalculationHeartbeatManager.instance.resetCurrentTime()
        }

        getImHandler()?.doHandler(message)
    }
}