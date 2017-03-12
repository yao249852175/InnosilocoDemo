package innosiloco.demo.beans;

import java.security.Key;

/**
 * Created by ronya on 2017/3/12.
 */

public class KeyCheckEvent
{

    public KeyCheckEvent(int type,boolean isSuccess,String key)
    {
        this.type = type;
        this.isSuccess = isSuccess;
        this.key = key;
    }
    /************
     * 正在匹配对中
     */
    public static final int CheckKeyING = 0x01;

    /*************
     *
     */
    public static final int CheckKeyResult = 0x02;

    public static final int CheckKeyBegin = 0x03;

    public int type;

    public boolean isSuccess;

    public String key;
}
