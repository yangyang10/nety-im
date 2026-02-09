package com.xiaotimel.im.netty.decode;

/**
 * 标题：
 * 描述：TCP协议头
 * 作者：hhy
 * 创建时间：2017/9/19 10:06
 *
 * @deprecated 使用 {@link com.xiaotimel.im.netty.config.ProtocolConfig} 替代
 */
@Deprecated
public class ProtocalHeadInfo {
    private static final String HEAD_RPC = "#XIAOTIMEL#";//标识开始 （8个字符）
    private static final String VERSION = "01";//协议主版本号 （2个字符）
    private static final String SUB_VERSION = "01";//协议子版本号 （2个字符）
    private static final String MESSAGE_LENG_INFO = "000001";//包头+包体的长度 （6个字符）
    private static final String RANDOM_SEQ = "0000000001";//序列号 (随机数 10个字符)
    public static final byte[] HEAD_RPC_BYTES = HEAD_RPC.getBytes();

    private static final int HEAD_RPC_LENGTH = HEAD_RPC_BYTES.length;
    private static final int VERSION_LENGTH = VERSION.getBytes().length;
    private static final int SUB_VERSION_LENGTH = SUB_VERSION.getBytes().length;
    public static final int MESSAGE_LENG_INFO_LENGTH = MESSAGE_LENG_INFO.getBytes().length;
    private static final int RANDOM_SEQ_LENGTH = RANDOM_SEQ.getBytes().length;

    public static final int MESSAGE_LENG_INDEX = HEAD_RPC_LENGTH+VERSION_LENGTH+SUB_VERSION_LENGTH;//TCP协议包体长度
    public static final int HEAD_LENGTH = HEAD_RPC_LENGTH+VERSION_LENGTH+SUB_VERSION_LENGTH+MESSAGE_LENG_INFO_LENGTH+RANDOM_SEQ_LENGTH;//TCP协议包头长度
}
