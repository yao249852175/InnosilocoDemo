package innosiloco.demo.utils;

import android.os.Environment;

import innosiloco.demo.beans.UserBean;
import innosiloco.demo.service.ParseDataHelper;

/**
 * Created by Administrator on 2017/2/25.
 */
public class AppConfig
{
    /************************
     * 端口号
     */
    public static final int PORT = 4022;

    /**************
     * true表示 不需要 设备，可以进行交换
     * false表示，需要设备进行交换
     */
    public static boolean isTest = false;

    /**********************
     * 服务器的ip地址
     */
    public static final String ServerIP = "192.168.1.107";

    public static  String userNick;


    public static String clientIp ;

    /********************
     * <p>解析的类型</p>
     * <p>在线列表</p>
     */
    public static final byte FriendCode = 0x01;

    /************
     * 保存自己的上一次的匹配过的key
     */
    public static String cacheKey_self = "";

    /**************
     * 对方的匹配过的可以
     */
    public static String cacheKey_other = "";

    public static void  setCacheKey(String selfKey,byte otherId)
    {
        AppConfig.cacheKey_self = selfKey;
        for (UserBean userBean:ParseDataHelper.onlineUser)
        {
            if(userBean.userID == otherId )
            {
                AppConfig.cacheKey_other = userBean.key;
                break;
            }
        }

    }

    /*********
     * 保存cache的目录
     */
    public static String BaseDirectory = Environment
            .getExternalStorageDirectory() + "/inno/cache/";

    /*********
     * 保存cache的目录
     */
    public static String ErrIMGDirectory = Environment
            .getExternalStorageDirectory() + "/inno/err/img/";

    /*********
     * 保存cache的目录
     */
    public static String ErrVoiceDirectory = Environment
            .getExternalStorageDirectory() + "/inno/err/voice/";

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

    /***********
     * 请求文件
     */
    public static final byte RequestFile = 0x05;

    /**********
     * 发送文件
     */
    public static final byte ResponseFile = 0x06;

    /**********
     * 验证key
     */
    public static final byte CheckKey = 0x07;

    /**************
     * 验证key的结果
     */
    public static final byte CheckKeyResult = 0x08;

    /*********
     * 问题的校验过程
     */
    public static final byte QuestionCheck = 0x09;


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

    public static final int maxSendFIleLength = 90*1024;
}
