package com.xiaotimel.im.netty.constant;

/**
 * Created by HHY on 2021/3/1 6:21 PM
 * Desc: netty 默认连接配置
 **/
public interface NettyConfig {

    // 默认重连一个周期失败间隔时长
    long DEFAULT_RECONNECT_INTERVAL = 10 * 1000;
    // 默认应用在前台时心跳消息间隔时长
    long DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND = 20 * 1000;
    // 应用在前台标识
    int APP_STATUS_FOREGROUND = 0;
    // 应用在后台标识
    int APP_STATUS_BACKGROUND = -1;
    // 默认一个周期重连次数
    int DEFAULT_RECONNECT_COUNT = 5;
    //默认链接超时时间
    int DEFAULT_CONNECT_TIMEOUT = 5000;
    // 默认重连起始延时时长，重连规则：最大n次，每次延时n * 起始延时时长，重连次数达到n次后，重置
    int DEFAULT_RECONNECT_BASE_DELAY_TIME = 3 * 1000;
    // 默认消息发送失败重发次数
    int DEFAULT_RESEND_COUNT = 3;
    // ims连接状态：连接中
    int CONNECT_STATE_CONNECTING = 0;
    // ims连接状态：连接成功
    int CONNECT_STATE_SUCCESSFUL = 1;
    // ims连接状态：连接失败
    int CONNECT_STATE_FAILURE = -1;

    //检测读超时时间 单位秒
    long READER_IDLE_TIME = 45;
    //检测写超时时间 单位秒
    long WRITER_IDLE_TIME = 45;
    //检测读写超时时间 单位秒
    long ALL_IDLE_TIME = 20;
}
