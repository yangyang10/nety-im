package com.xiaotimel.im.netty.handler;

import com.xiaotimel.im.netty.decode.BytesUtil;
import com.xiaotimel.im.netty.decode.ProtocalHeadInfo;
import com.xiaotimel.im.netty.interfaces.NettyClientInterface;
import com.xiaotimel.im.netty.message.ChannelMsgHandler;
import com.xiaotimel.im.netty.message.SystemMsgHandler;
import com.xiaotimel.im.util.LogUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by HHY on 2021/3/3 4:20 PM
 * Desc: netty自定义消息解析器，接收到的消息会在这里统一分发
 **/
public class NettySocketReadHandler extends ChannelInboundHandlerAdapter {

    private final String TAG = "WebSocketClient";

    private NettyClientInterface mClient;
    private ChannelMsgHandler channelMsgHandler;
    private SystemMsgHandler systemMsgHandler;

    public NettySocketReadHandler(NettyClientInterface client) {

        this.mClient = client;
        channelMsgHandler = new ChannelMsgHandler();
        channelMsgHandler.bindNettyClient(client);
        systemMsgHandler = new SystemMsgHandler();
        systemMsgHandler.bindNettyClient(client);

        channelMsgHandler.setImHandler(systemMsgHandler);

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (mClient.getChannel() == ctx.channel() && evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
                case READER_IDLE:
                    LogUtils.i(TAG, "读取超时，进行重连", true);
                    clearAndConnect(ctx.channel());
                    break;

                case WRITER_IDLE:
                    LogUtils.i(TAG, "写数据超时，进行重连", true);
                    clearAndConnect(ctx.channel());
                    break;
                case ALL_IDLE:
                    LogUtils.i(TAG, "发送心跳", true);
                    if (mBeatTask == null) {
                        mBeatTask = new HeartbeatTask(ctx);
                    }
                    mClient.getLoopGroup().execWorkTask(mBeatTask);
                    break;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (this.handshaker != null && !this.handshaker.isHandshakeComplete() && msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            try {
                this.handshaker.finishHandshake(ctx.channel(), response);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "finishHandshake 403异常", true);
            }
            this.handshakeFuture.setSuccess();
            LogUtils.i(TAG, "【握手成功】", true);
            return;
        }

        if (mClient.getChannel() == ctx.channel()) {
            analyticMessage(msg);
        } else {
            LogUtils.e(TAG, "[服务下发channel错误,关闭channel]", true);
            removeHandlerAndClose(ctx.channel());
        }

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LogUtils.e(TAG, "channelInactive 通道未激活", true);
        clearAndConnect(ctx.channel());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // channel 通道异常，关闭重新链接服务器
        clearAndConnect(ctx.channel());
    }



    private void clearAndConnect(Channel channel){
        if(channel == null) return;
        boolean isSampleChannel = mClient.getChannel() == channel;
        removeHandlerAndClose(channel);
        if (isSampleChannel) {
            mClient.resetConnect();
        }
    }



    private void removeHandlerAndClose(Channel exceptionChannel) {
        mBeatTask = null;
        if (exceptionChannel.pipeline().get(IdleStateHandler.class.getSimpleName()) != null) {
            exceptionChannel.pipeline().remove(IdleStateHandler.class.getSimpleName());
        }
        if (exceptionChannel.pipeline().get(NettySocketReadHandler.class.getSimpleName()) != null) {
            exceptionChannel.pipeline().remove(NettySocketReadHandler.class.getSimpleName());
        }
        exceptionChannel.close();
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    private WebSocketClientHandshaker handshaker = null;
    private ChannelPromise handshakeFuture = null;

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    /**
     * 消息解析
     *
     * @param message
     */
    private void analyticMessage(Object message) {
        if(message instanceof byte[]){
            byte[] data = (byte[]) message;
            //如果接收消息长度大于消息体的长度
            if (data.length > ProtocalHeadInfo.HEAD_LENGTH) {
                //如果消息一消息头部开始
                if (BytesUtil.isIndexOfSubBytes(data, ProtocalHeadInfo.HEAD_RPC_BYTES)) {
                    //截取消息头部消息内容长度的字节数组
                    byte[] bytes = BytesUtil.subByte(data,
                            ProtocalHeadInfo.MESSAGE_LENG_INDEX,
                            ProtocalHeadInfo.MESSAGE_LENG_INFO_LENGTH);
                    try {
                        int msgLength = Integer.parseInt(new String(bytes));
                        //如果消息头部长度和消息内容一样长，那么是一个完整的包，不进行粘包处理
                        if (msgLength == data.length) {
                            // 完整消息包进行处理
                            String messageStr = BytesUtil.getString(BytesUtil.subByte(data, ProtocalHeadInfo.HEAD_LENGTH, msgLength - ProtocalHeadInfo.HEAD_LENGTH), "UTF-8");
                            LogUtils.d("","---接收到的消息--"+messageStr);
                            // 消息转发
                            channelMsgHandler.doHandler(messageStr);
                        }
                    } catch (Exception e) {
                        //如何处理
                        return;
                    }
                }
            }
        }

    }

    private HeartbeatTask mBeatTask;

    private class HeartbeatTask implements Runnable {
        private ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (ctx != null && ctx.channel().isActive()) {
                Object heartbeatMsg = mClient.getHeartbeatMsg();
                if (heartbeatMsg == null) {
                    return;
                }
                mClient.sendMsg(heartbeatMsg, false);
            }
        }
    }
}