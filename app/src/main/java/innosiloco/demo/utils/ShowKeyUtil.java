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
        hadGetRealKey = false;
        this.begin = true;
        this.button = button;
        this.button.setEnabled(!begin);
        if(AppConfig.isServce)
            checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Register_step1));
//        else
//            checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Client_begin));
    }

    public void setKeyValue(String key)
    {
        if(!begin)
            return;
        if(TextUtils.isEmpty(lastKey))
        {
            if(AppConfig.isServce)
            this.checkKeyUIUtil.addNewLog(key);
        }else
        {
            compareKey(key);
        }
        checkKeyIsRealy(key);
        lastKey = key;
        boolean hadExit = false;
        for (String key1:getKeys)
        {
            if(key1.equals(key))
            {
                hadExit = true;
                break;
            }
        }
        if(!hadExit)
            getKeys.add(key);
        if(getKeys.size() >= 3 )
        {
            button.setEnabled(true);
            begin = false;
            if(AppConfig.isServce)
            {
                checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Register_step2));
                checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_HadInsertSqlite));
            }

        }

    }

    /***********
     * 拿到真正的key然后使用
     * @param key
     * @return
     */
    private boolean checkKeyIsRealy(String key)
    {
        if(hadGetRealKey) return false;
        String sub = key.substring(key.length() - 4, key.length() - 2);
        if(sub.equals("3C") || sub.equals("3c") )
        {
            hadGetRealKey =true;
            AESKeyUitl.getSingleton().setEncode_key(key);
            if(AppConfig.isServce)
            {
                dataKeyUtil.insert(key);
            }else
            {
                KeyBean keyBean = new KeyBean();
                keyBean.clientID = AppConfig.clientId;
                keyBean.key = key;
                MyApp.getSingleApp().mySocket.sendKey2ServerCheckKey(keyBean);
                EventBus.getDefault().post(new KeyCheckEvent(KeyCheckEvent.CheckKeyBegin,false,key));
                checkKeyUIUtil.addNewLog(MyApp.getSingleApp().getString(R.string.Label_Client_begin));
            }

            Toast.makeText(MyApp.getSingleApp(),R.string.Label_HadInsertSqlite,Toast.LENGTH_LONG).show();
        }
        return true;
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
        if(AppConfig.isServce)
        checkKeyUIUtil.addNewLog(builder);
    }

    public void onDestory()
    {
        getKeys.clear();
        lastKey = "";
        if(button != null)
        button.setEnabled(true);
    }
}
