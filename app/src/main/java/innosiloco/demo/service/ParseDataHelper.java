package innosiloco.demo.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import innosiloco.demo.beans.FrameBean;
import innosiloco.demo.beans.KeyBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AESUtil;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;

/**
 * Created by ron on 2017/2/26.
 * [F0,(Send2ID),01(CODEIDNEx),content,CRC8,END]
 */
public class ParseDataHelper
{
    /*******************
     * <p>保存聊天内容的全局变量</p>
     */
    public static LinkedBlockingQueue<FrameBean> talks = new LinkedBlockingQueue<>();

    public static List<UserBean> onlineUser = new ArrayList<>();

    /***************
     * 添加add key,从客户端来的，
     */
    public static boolean addKey(KeyBean keyBean)
    {
        for (UserBean userBean: onlineUser)
        {
            if(userBean.userID == keyBean.clientID)
            {
                userBean.key = keyBean.key;
                return true;
            }
        }
        return false;
    }

    /*****************************88
     * 使用第三方工具gson
     * @param talkBean
     * @return
     */
    public static String talkBean2Json(TalkBean talkBean)
    {
        try {
            RonLog.LogE("机密前:" + talkBean.talkContent);
//            byte[] content = AESUtil.encryptArgByte(
//                    AESKeyUitl.getSingleton().getEncode_key().getBytes(),talkBean.talkContent.getBytes(utf_8));
//            talkBean.talkContent = new String(content,utf_8);
            TalkBean talkBean1 = new TalkBean();
            talkBean1.toID = talkBean.toID;
            talkBean1.sendID = talkBean.sendID;


            talkBean1.talkContent= AESUtil.encrypt(AESKeyUitl.getSingleton().getEncode_key(),talkBean.talkContent);
            RonLog.LogE("加密后:" + talkBean1.talkContent);
            Gson gson = new Gson();
            String json = gson.toJson(talkBean1);
            return json;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static String utf_8 = "UTF-8";

    /****************************
     * json转TalkBean
     * @param json
     * @return
     */
    public static TalkBean json2TalkBean(String json)
    {
        RonLog.LogE(json);
        Gson gson = new Gson();
        try{
            TalkBean talkBean =  gson.fromJson(json,TalkBean.class);
            RonLog.LogE("解密前:" + talkBean.talkContent);

            try {
               /* byte[] content =AESUtil.decryptArgByte(
                        AESKeyUitl.getSingleton().getDecode_key().getBytes(),talkBean.talkContent.getBytes(utf_8));
                talkBean.talkContent = new String(content,utf_8);*/
                String decodeKey = AESKeyUitl.getSingleton().getDecode_key(talkBean.sendID);
                RonLog.LogE("sendID:" + talkBean.sendID + ",decodeKey:" + decodeKey + ",encode:" + AESKeyUitl.getSingleton().getEncode_key());
                if(!TextUtils.isEmpty(decodeKey) &&
                        !TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
                talkBean.talkContent = AESUtil.decrypt(decodeKey,talkBean.talkContent);
                else
                {//乱码显示
                    String c = new String(AESUtil.toByte(talkBean.talkContent));
                    talkBean.talkContent = c;
                }
                RonLog.LogE("解密后:" + talkBean.talkContent);
            }catch (Exception e)
            {
                e.printStackTrace();

            }
            return talkBean;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }



    /*****************************88
     * 使用第三方工具gson
     * @param friendBean 用户的信息
     * @return
     */
    public static String friendBean2Json(UserBean friendBean)
    {
        Gson gson = new Gson();
        return gson.toJson(friendBean);
    }

    public static String keyBean2Json(KeyBean keyBean)
    {
        Gson gson = new Gson();
        return gson.toJson(keyBean);
    }

    public static KeyBean json2KeyBean(String json)
    {
        Gson gson = new Gson();
        KeyBean f =  gson.fromJson(json,KeyBean.class);
        return f;
    }

    /****************************
     * json转Friend
     * @param json
     * @return
     */
    public static UserBean json2Friend(String json)
    {
        Gson gson = new Gson();
        UserBean f =  gson.fromJson(json,UserBean.class);
        return f;
    }

    /***************************************
     * <p></p>
     * @param beans
     * @return
     */
    public static String friendList2Json(List<UserBean> beans)
    {
        Gson gson = new Gson();
        return  gson.toJson(beans);
    }


    public static List<UserBean> jsonFriendList(String json)
    {
        Gson gson = new Gson();
        List<UserBean> beans = gson.fromJson(json, new TypeToken<List<UserBean>>(){}.getType());
        return beans;
    }

    /**************
     * 一帧数据转换为byte[]
     * frameBean:必须指定 frameBean.send2ID，frameBean.content  frameBean.cmdIndex
     * @param frameBean
     * @return
     */
    public static byte[] frame2Btye(FrameBean frameBean)
    {
        //content设计到加密
      /*  byte[] SRCData= frameBean.content;
        if(frameBean.cmdIndex == AppConfig.TalkCode)
        {
            try {
                frameBean.content = AESUtil.encryptArgByte(
                        AESKeyUitl.getSingleton().getEncode_key().getBytes(),SRCData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }*/

        byte[] data = new byte[frameBean.content.length + 8 ];
        int index = 0;
        data[index++] = (byte) 0xF0;
        data[index++] = frameBean.send2ID;
        data[index++] = frameBean.cmdIndex;
        System.arraycopy(frameBean.content,0,data,index,frameBean.content.length);
        index += frameBean.content.length;
        data[index ++] = CRC.CRC8(data,0,data.length -5);
        System.arraycopy(AppConfig.endWith.getBytes(),0,data,index,4);
        return data;
    }

    /**********************
     * data数组转换为 一帧数据
     * 注意：里面没有做CRC校验，data必须是经过校验的数据
     * @param data
     * @return
     */
    public static FrameBean byte2Frame(byte[] data, int pos, int length)
    {
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID= data[1 + pos];
        frameBean.cmdIndex = data[2 + pos];
        byte[] enSrcData = new byte[length - 8];
        System.arraycopy(data,3+pos,enSrcData,0,enSrcData.length);
        frameBean.content = enSrcData;
        //解密
        /*if(frameBean.cmdIndex == AppConfig.TalkCode)
        {
            try {
                frameBean.content = AESUtil.decryptArgByte(
                        AESKeyUitl.getSingleton().getDecode_key().getBytes(),enSrcData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }*/

        frameBean.crc8 = data[length-5+ pos];
        return frameBean;
    }

    public static boolean checkCRC(byte[] data,int start,int length)
    {
        byte crc = CRC.CRC8(data,start,length -5);
        if(crc == data[start+  length - 5])
        {
            return true;
        }else
        {
            return false;
        }
    }

    /*************
     * <p>校验数据的结尾</p>
     * @param data 数据
     * @param begin 数据结尾开始的Index
     * @return
     */
    public static boolean checkEnd(byte[] data,int begin)
    {
        byte[] ends = AppConfig.endWith.getBytes();

        for (int i = 0;i < 4; i ++ )
        {
            if(data[begin + i ] != ends[i])
            {
                return false;
            }
        }
        RonLog.LogE("找到尾部");
        return true;
    }
}
