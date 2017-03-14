package innosiloco.demo.utils;

import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/14.
 */
public class ShowKeyUtil
{
    public TextView showKey;

    public String lastKey;

    public ShowKeyUtil(TextView textView)
    {
        this.showKey = textView;
    }

    public void setKeyValue(String key)
    {
        if(TextUtils.isEmpty(lastKey))
        {
            showKey.setText(key);
        }else
        {
            compareKey(key);
        }

        lastKey = key;
    }

    private void  compareKey(String key)
    {
        String[] keyList = key.split("");
        String[] lastKeyList = lastKey.split("");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0 ; i > keyList.length; i ++ )
        {
            if(lastKeyList[i].equals(keyList[i]))
            {
                stringBuffer.append(lastKeyList[i]);
            }else
            {
                stringBuffer.append("<font color=\"#ff0000\">");
                stringBuffer.append(lastKeyList[i]);
                stringBuffer.append("</font>");
            }
        }

        showKey.setText(Html.fromHtml(stringBuffer.toString()));
    }

}
