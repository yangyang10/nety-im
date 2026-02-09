package com.xiaotimel.im.netty.decode

import com.xiaotimel.im.netty.config.ProtocolConfig
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * 标题：
 * 描述：
 * 作者：hhy
 * 创建时间：2017/9/19 14:15
 */
class WlwFrameDecoder @JvmOverloads constructor(protocolConfig: ProtocolConfig? = null) :
    ByteToMessageDecoder() {
    private val protocolConfig: ProtocolConfig = protocolConfig ?: ProtocolConfig.DEFAULT

    /**
     * 带协议配置的构造函数
     *
     * @param protocolConfig 协议配置对象，null时使用默认配置
     */

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext?, byteBuf: ByteBuf, out: MutableList<Any?>) {
        val decoded = decode(ctx, byteBuf)
        if (decoded != null) {
            out.add(decoded)
        }
    }

    /**
     * Create a frame out of the [ByteBuf] and return it.
     *
     * @param   ctx   the [ChannelHandlerContext] which this [ByteToMessageDecoder] belongs to
     * @param   in    the [ByteBuf] from which to read data
     * @return  frame the [ByteBuf] which represent the frame or `null` if no frame could
     * be created.
     */
    @Throws(Exception::class)
    protected fun decode(ctx: ChannelHandlerContext?, byteBuf: ByteBuf): Any? {
        val readbyteCount = byteBuf.readableBytes()

        if (readbyteCount == 0) {
            return null
        }
        val basedata = ByteArray(readbyteCount)
        byteBuf.markReaderIndex()
        byteBuf.readBytes(basedata, 0, readbyteCount)
        byteBuf.resetReaderIndex()
        //如果接收消息长度大于消息体的长度
        if (readbyteCount > protocolConfig.headLength) {
            val data = ByteArray(protocolConfig.headLength)
            byteBuf.markReaderIndex()
            byteBuf.readBytes(data, 0, protocolConfig.headLength)
            byteBuf.resetReaderIndex()
            //如果消息一消息头部开始
            if (BytesUtil.isIndexOfSubBytes(data, protocolConfig.headRpcBytes)) {
                //截取消息头部消息内容长度的字节数组
                val bytes = BytesUtil.subByte(
                    data, protocolConfig.messageLengIndex, protocolConfig.messageLengInfoLength
                )
                try {
                    val msgLength = kotlin.text.String(bytes!!).toInt()
                    //如果消息头部长度和消息内容一样长，那么是一个完整的包，不进行粘包处理
                    if (readbyteCount >= msgLength) {
                        return byteBuf.readSlice(msgLength).retain()
                    } else {
                        return null
                    }
                } catch (e: Exception) {
                    return null
                }
            } else { //开头非法字符处理过滤
                val headIndex = BytesUtil.headStarIndex(basedata, protocolConfig.headRpcBytes)
                if (headIndex > 0) {
                    return byteBuf.readSlice(headIndex).retain()
                }
            }
        } else {
            val shotdata = ByteArray(readbyteCount)
            byteBuf.markReaderIndex()
            byteBuf.readBytes(shotdata, 0, readbyteCount)
            byteBuf.resetReaderIndex()
            if (BytesUtil.isHeadSame(protocolConfig.headRpcBytes, shotdata)) {
                return null
            }
            return byteBuf.readSlice(readbyteCount).retain()
        }
        return null
    }
}
