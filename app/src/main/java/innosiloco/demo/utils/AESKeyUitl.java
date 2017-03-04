package innosiloco.demo.utils;

/**
 * Created by Administrator on 2017/3/1.
 */
public class AESKeyUitl
{
    private static  AESKeyUitl aesKeyUitl;

    private AESKeyUitl()
    {

    }

    public static AESKeyUitl getSingleton()
    {
        if(aesKeyUitl == null )
        {
            aesKeyUitl = new AESKeyUitl();
        }
        return aesKeyUitl;
    }

    /**********************
     * 获取加密的key
     * @return
     */
    public String getAESKey()
    {
        return "ron";
    }
}
