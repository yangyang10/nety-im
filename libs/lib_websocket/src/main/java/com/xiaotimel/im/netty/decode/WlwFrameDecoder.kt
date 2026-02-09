package com.xiaotimel.im.netty.decode;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


/**
 * 标题：
 * 描述：
 * 作者：hhy
 * 创建时间：2017/9/19 14:15
 */

public class WlwFrameDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    /**
     * Create a frame out of the {@link ByteBuf} and return it.
     *
     * @param   ctx   the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param   in    the {@link ByteBuf} from which to read data
     * @return  frame the {@link ByteBuf} which represent the frame or {@code null} if no frame could
     *                          be created.
     */
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int readbyteCount= in.readableBytes();

        if(readbyteCount==0){
            return null;
        }
        byte [] basedata = new byte[readbyteCount];
        in.markReaderIndex();
        in.readBytes(basedata, 0, readbyteCount);
        in.resetReaderIndex();
        //如果接收消息长度大于消息体的长度
        if(readbyteCount> ProtocalHeadInfo.HEAD_LENGTH){
            byte[] data=new byte[ProtocalHeadInfo.HEAD_LENGTH];
            in.markReaderIndex();
            in.readBytes(data, 0, ProtocalHeadInfo.HEAD_LENGTH);
            in.resetReaderIndex();
            //如果消息一消息头部开始
            if(BytesUtil.isIndexOfSubBytes(data, ProtocalHeadInfo.HEAD_RPC_BYTES)){
                //截取消息头部消息内容长度的字节数组
                byte [] bytes=BytesUtil.subByte(data,
                        ProtocalHeadInfo.MESSAGE_LENG_INDEX,
                        ProtocalHeadInfo.MESSAGE_LENG_INFO_LENGTH);
                try{
                    int msgLength=Integer.parseInt(new String(bytes));
                    //如果消息头部长度和消息内容一样长，那么是一个完整的包，不进行粘包处理
                    if(readbyteCount>=msgLength){
                        return in.readSlice(msgLength).retain();
                    }else{
                        return null;

                    }
                }catch(Exception e){
                    return null;
                }
            }else{//开头非法字符处理过滤
                int headIndex=BytesUtil.headStarIndex(basedata, ProtocalHeadInfo.HEAD_RPC_BYTES);
                if(headIndex>0){
                    return in.readSlice(headIndex).retain();
                }
            }
        }else{
            byte [] shotdata = new byte[readbyteCount];
            in.markReaderIndex();
            in.readBytes(shotdata, 0, readbyteCount);
            in.resetReaderIndex();
            if(BytesUtil.isHeadSame(ProtocalHeadInfo.HEAD_RPC_BYTES,shotdata)){
                return null;
            }
            return in.readSlice(readbyteCount).retain();
        }
        return null;
    }
}
