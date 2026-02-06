package com.xiaotimel.im.netty.message

import com.xiaotimel.im.netty.base.BaseMsgHandler

/**
 * Created by HHY on 2021/3/3 4:20 PM
 * Desc: 仅处理 系统级别消息
 **/
class SystemMsgHandler : BaseMsgHandler<Any>() {

    override fun doHandler(message: Any) {
        //
    }
}