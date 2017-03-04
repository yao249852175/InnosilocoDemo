package innosiloco.demo.utils;

/**
 * Created by Administrator on 2017/2/25.
 */
public class AppConfig
{
    /************************
     * 端口号
     */
    public static final int PORT = 4022;

    /**********************
     * 服务器的ip地址
     */
    public static final String ServerIP = "192.168.1.107";


    public static String clientIp ;

    /********************
     * <p>解析的类型</p>
     * <p>在线列表</p>
     */
    public static final byte FriendCode = 0x01;


    /********************
     * <p>解析的类型</p>
     * <p>聊天内容</p>
     */
    public static final byte TalkCode = 0x02;

    /********************
     * <p>解析的类型</p>
     * <p>设置用户信息</p>
     */
    public static final byte UserInfoCode = 0x03;

    /*********************
     * socket的心跳包
     */
    public static final byte SocketHeart = 0x04;

    /*******************
     * <p>客户端的ID号</p>
     */
    public static byte clientId = -1;

    /***************
     * 是否是服务器
     */
    public static boolean isServce = false;


    /**
     * <p>结尾分隔符</p>
     *	endWith = "$_@#";
     */
    public  static String endWith = "$_@-";
}
