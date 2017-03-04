package innosiloco.demo.dao;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/2/26.
 */
public class MyPreference
{
    private SharedPreferences sharedPreferences;

    public final String Label_UserNick = "Label_UserNick";

    public MyPreference(Context context)
    {
        sharedPreferences = context.getSharedPreferences("MyPreference",0);
    }

    /**
     * <p>保存String类型的变量</p>
     * @param key
     * @param value
     */
    public void saveStringValue(String key,String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * <p>获取 String类型的变量</p>
     * @param key
     * @return
     */
    public String getStringValue(String key)
    {
        return sharedPreferences.getString(key, "");
    }
}
