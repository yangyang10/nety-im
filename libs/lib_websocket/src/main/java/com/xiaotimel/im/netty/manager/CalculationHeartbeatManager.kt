package com.xiaotimel.im.netty.manager

import com.xiaotimel.im.util.LogUtils


/**
 * Created by HHY on 2021/3/3 6:46 PM
 * Desc: 计算消息响应时间。  第一版测试业务
 **/
class CalculationHeartbeatManager private constructor() {

    companion object {
        val instance: CalculationHeartbeatManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CalculationHeartbeatManager()
        }

        const val TAG = "WebSocketClient"
    }

    /**
     * 重置时间
     */
    fun resetCurrentTime() {
        sendMsgCurrentTime = System.currentTimeMillis()
    }

    @Volatile
    var mHeartbeatUsedTime: Long = 0
        private set

    private var sendMsgCurrentTime: Long = 0

    /**
     * 计算响应的时间
     */
    fun calculationTakeTime() {
        mHeartbeatUsedTime = System.currentTimeMillis() - sendMsgCurrentTime
        LogUtils.i(TAG, "心跳响应间隔时间 【$mHeartbeatUsedTime】毫秒")
    }
}