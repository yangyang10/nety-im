package com.xiaotimel.im.netty.constant

/**
 * Created by HHY on 2021/3/3 4:40 PM
 * Desc: netty数据包 协议配置
 **/
object ProtocolConfig {

    /**协议版本**/
    const val PROTOCOL_VERSION = "01"


    /**指令 ping**/
    const val COMMAND_TYPE_PING = 1
    /**指令 pong**/
    const val COMMAND_TYPE_PONG = 2
    /**指令 login**/
    const val COMMAND_TYPE_LOGIN = 3

}