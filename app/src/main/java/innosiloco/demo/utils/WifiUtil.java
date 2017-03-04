package innosiloco.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/3/4.
 */
public class WifiUtil
{
    // wifi热点开关
    public static boolean setWifiApEnabled(Activity activity,boolean enabled)
    {
        WifiManager wifiManager = (WifiManager)activity. getSystemService(Context.WIFI_SERVICE);
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "Ron_Test";
            //配置热点的密码
            apConfig.preSharedKey="11111111";
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
}
