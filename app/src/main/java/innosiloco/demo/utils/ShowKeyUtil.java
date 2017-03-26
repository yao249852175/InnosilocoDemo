package innosiloco.demo.utils;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.KeyBean;
import innosiloco.demo.beans.KeyCheckEvent;
import innosiloco.demo.beans.QuestionBean;
import innosiloco.demo.mvp_presenter.DataKeyUtil;

/**
 * Created by Administrator on 2017/3/14.
 */
public class ShowKeyUtil
{

    public String lastKey;

    private CheckKeyUIUtil checkKeyUIUtil;




    private List<String> getKeys;

    private DataKeyUtil dataKeyUtil;
    private boolean begin =false;
    private boolean beginLog = false;

    private boolean hadGetRealKey = false;

    public ShowKeyUtil(CheckKeyUIUtil textView)
    {
        getKeys = new ArrayList<>();
        this.checkKeyUIUtil = textView;
        if(dataKeyUtil == null )
        {
            dataKeyUtil = new DataKeyUtil(MyApp.getSingleApp());
        }

    }
    private Button button;

    public void setBegin(boolean begin, Button button)
    {
        getKeys.clear();
        //hadGetRealKey = false;
        this.begin = true;
        this.beginLog = true;
        checkKeyUIUtil.isShowLog = true;
        this.button = button;
        this.button.setEnabled(!begin);
        if(AppConfig.isServce)
            checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Register_step1));
        else
            checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Client_begin));
    }

    public void firstInsert()
    {
        getKeys.clear();
//        this.begin = true;
        this.beginLog = false;
        checkKeyUIUtil.isShowLog = false;
    }

    public String cacheKey;

    public String lastRealyKey;

    public void setKeyValue(String key)
    {
        cacheKey = key;

        if(begin && !TextUtils.isEmpty(lastRealyKey))
        {
            if(AppConfig.isServce)
            {
                dataKeyUtil.insert(lastRealyKey);
            } else
            {
            KeyBean keyBean = new KeyBean();
            keyBean.clientID = AppConfig.clientId;
            keyBean.key = lastRealyKey;
            MyApp.getSingleApp().mySocket.sendKey2ServerCheckKey(keyBean);
            EventBus.getDefault().post(new KeyCheckEvent(KeyCheckEvent.CheckKeyBegin,false,lastRealyKey));
                RonLog.LogE("开始发送检测客户端的key");
        }
            begin = false;
        }

        if(checkKeyIsReally(key))
        {
            lastRealyKey =  key;
//            AESKeyUitl.getSingleton().setEncode_key(key);
            if(dataKeyUtil.checkKeyIsExit(key))
            {
                AESKeyUitl.getSingleton().setEncode_key(key);
            }
            //如果是正确的key，则服务器添加进数据库
            if(begin) {
                if (AppConfig.isServce) {
                    dataKeyUtil.insert(key);
                } else {
                    //如果使正确的key，则客户端将该key发送到服务端进行验证

                    {
                        KeyBean keyBean = new KeyBean();
                        keyBean.clientID = AppConfig.clientId;
                        keyBean.key = key;
                        MyApp.getSingleApp().mySocket.sendKey2ServerCheckKey(keyBean);
                        EventBus.getDefault().post(new KeyCheckEvent(KeyCheckEvent.CheckKeyBegin, false, key));
                    }

                    //checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Client_begin));
                }
            }
            begin = false;
        }
        RonLog.LogE("beginLog:" + beginLog);
        if(beginLog && AppConfig.isServce )
        {

            if (TextUtils.isEmpty(lastKey)) {
                if (AppConfig.isServce) this.checkKeyUIUtil.addNewLog(key); //如果第一次进入直接显示key（服务器）
            } else {
                compareKey(key);  //否则跟上一次的key进行比较，不一样的byte标红（服务器）
            }
            lastKey = key;
            //判断数据库中是否存在该key，若不存在，则添加
            boolean hadExit = false;
            for (String key1 : getKeys) {
                if (key1.equals(key)) {
                    hadExit = true;
                    break;
                }
            }
            if (!hadExit) getKeys.add(key);

            if (getKeys.size() >= 3)
            {
                if(button != null)
                button.setEnabled(true);
                beginLog = false;
                if (AppConfig.isServce) {
                    checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Register_step2));
                    checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_HadInsertSqlite));
                    AESKeyUitl.getSingleton().setEncode_key(key);
                }

            }
        }


    }

    /***********
     * 拿到真正的key然后使用
     * @param key
     * @return
     */
    private boolean checkKeyIsReally(String key)
    {
        //if(hadGetRealKey) return false;
        String sub = key.substring(key.length() - 6, key.length() - 4);
        if(sub.equals("3C") || sub.equals("3c") )
        {
            //checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.test));
            //hadGetRealKey =true;
//            AESKeyUitl.getSingleton().setEncode_key(key);
//            //如果是正确的key，则服务器添加进数据库
//            if(AppConfig.isServce)
//            {
//                dataKeyUtil.insert(key);
//            }else
//            {
//                //如果使正确的key，则客户端将该key发送到服务端进行验证
//                KeyBean keyBean = new KeyBean();
//                keyBean.clientID = AppConfig.clientId;
//                keyBean.key = key;
//                MyApp.getSingleApp().mySocket.sendKey2ServerCheckKey(keyBean);
//                EventBus.getDefault().post(new KeyCheckEvent(KeyCheckEvent.CheckKeyBegin,false,key));
//                //checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Client_begin));
//            }
//            Toast.makeText(MyApp.getSingleApp(),R.string.Label_HadInsertSqlite,Toast.LENGTH_LONG).show();
            return true;
        }

        return false;

    }


    private void  compareKey(String key)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(key);
        char[] keyList = key.toCharArray();
        char[] lastKeyList = lastKey.toCharArray();
        for (int i = 0 ; i < keyList.length; i ++ )
        {
            if(keyList[i] !=  lastKeyList[i])
            {
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                builder.setSpan(redSpan, i, 1+i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        if(AppConfig.isServce) checkKeyUIUtil.addNewLog(builder);
    }

    public void onDestory()
    {
        getKeys.clear();
        lastKey = "";
        cacheKey = "";
        lastRealyKey = "";
        this.beginLog = false;
        this.begin = false;
        if(button != null)
        button.setEnabled(true);
    }
}
