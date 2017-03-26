package innosiloco.demo.utils;

import android.app.Activity;
import android.text.TextUtils;

import de.greenrobot.event.EventBus;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EncodeKeyEvent;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.service.ParseDataHelper;

/**
 * Created by Administrator on 2017/3/1.
 */
public class AESKeyUitl
{
    private static AESKeyUitl aesKeyUitl;

    public final int myvid=1155,mypid=22336;

    private String decode_key = "";

    private String encode_key = "";

    public String getDecode_key(byte fromClientID)
    {
        for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.userID == fromClientID)
            {
                return userBean.key;
            }
        }
        return "";
    }

    /********
     * 设置解码key
     * <p></p>
     * @param decode_key
     */
    public void setDecode_key(String decode_key)
    {
        this.decode_key = decode_key;
    }

    public String getEncode_key()
    {
        return encode_key;
    }

    /*********
     * 设置加密key
     * @return
     */
    public boolean setEncode_key(String encode_key)
    {
        if(encode_key.equals(this.encode_key))
        {
            return false;
        }else
        {
            this.encode_key = encode_key;
            if(!TextUtils.isEmpty(encode_key))
            {
                UserBean userBean = new UserBean();
                userBean.key = encode_key;
                userBean.userNike = AppConfig.userNick;
                userBean.userID = AppConfig.clientId;
                MyApp.getSingleApp().mySocket.updateUser(userBean);
                EventBus.getDefault().post(new EncodeKeyEvent(true));
            }else {
                EventBus.getDefault().post(new EncodeKeyEvent(false));
            }
            return true;
        }
    }



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


}
