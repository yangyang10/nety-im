package com.xiaotimel.im.netty.config

/**
 * 协议配置类
 * 用于自定义协议头标识、版本号等可配置字段
 * 其他计算字段（消息长度、序列号等）作为SDK封装细节
 *
 * Created by Claude on 2026/02/09
 */
data class ProtocolConfig(
    /**
     * 协议头标识，默认 "#XIAOTIMEL#"
     */
    val headRpc: String = "#XIAOTIMEL#",
    /**
     * 协议主版本号，默认 "01"
     */
    val version: String = "01",
    /**
     * 协议子版本号，默认 "01"
     */
    val subVersion: String = "01"
) {
    // ============ SDK封装的计算字段 ============

    /**
     * 协议头标识字节数组
     */
    val headRpcBytes: ByteArray
        get() = headRpc.toByteArray()

    /**
     * 协议头标识长度
     */
    val headRpcLength: Int
        get() = headRpcBytes.size

    /**
     * 主版本号长度
     */
    val versionLength: Int
        get() = version.toByteArray().size

    /**
     * 子版本号长度
     */
    val subVersionLength: Int
        get() = subVersion.toByteArray().size

    /**
     * 消息长度信息字段长度（固定6位数字）
     */
    val messageLengInfoLength: Int = 6

    /**
     * 随机序列号长度（固定10位数字）
     */
    val randomSeqLength: Int = 10

    /**
     * TCP协议包体长度在协议头中的索引位置
     */
    val messageLengIndex: Int
        get() = headRpcLength + versionLength + subVersionLength

    /**
     * TCP协议包头总长度
     */
    val headLength: Int
        get() = headRpcLength + versionLength + subVersionLength +
                messageLengInfoLength + randomSeqLength

    companion object {
        /**
         * 默认协议配置
         */
        val DEFAULT = ProtocolConfig(
            headRpc = "#XIAOTIMEL#",
            version = "01",
            subVersion = "01"
        )
    }
}
