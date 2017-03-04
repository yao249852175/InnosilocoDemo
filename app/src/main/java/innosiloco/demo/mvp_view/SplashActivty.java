package innosiloco.demo.mvp_view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.ICallback;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.service.MyClientSocket;
import innosiloco.demo.service.MyServerSocket;
import innosiloco.demo.service.ParseDataHelper;
import innosiloco.demo.utils.AESUtil;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.DialogCreatUtil;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.WifiUtil;

/**
 * Created by Administrator on 2017/2/25.
 */
public class SplashActivty extends BaseActivity
{

    @Override
    public void findViews() {

    }

    @Override
    public void initViews() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK},
                    0);
        }else
        {
           init();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0 )
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            }
        }
    }

    private void createConn()
    {
        if(dialogCreatUtil != null )
        {
            dialogCreatUtil.showDialogWith2Btn(R.string.selectServer_title,
                    R.string.selectServer_content,
                    R.string.selectServer_cilent, R.string.selectServer_server,
                    "ron", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          switch (v.getId())
                          {
                              case R.id.tv_close:

                                  break;
                              case R.id.tv_ok:
                                  boolean a =WifiUtil.setWifiApEnabled(SplashActivty.this,true);
                                  Toast.makeText(SplashActivty.this,"a:"+a,Toast.LENGTH_SHORT).show();
                                  break;
                          }
                        }
                    },this);
        }
    }

    private void init()
    {
        if(true)
        {
            createConn();
            return;
        }

        AppConfig.clientIp = getLocalIpAddress();
        RonLog.LogD("获取到IP:" + AppConfig.clientIp);
        RonLog.LogE("服务器Ip:" + getDpcpInfo());

        String text = "xiedfsadfasdfsadfsa";
        String pwd= "ron";
        String result = "";
        try {
            result = AESUtil.encrypt(pwd,text);
            RonLog.LogE("加密的数据:" + result);

            RonLog.LogE("解密数据:" + AESUtil.decrypt(pwd,result) );
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        if(AppConfig.clientIp.equals("0.0.0.0"))
        {//服务器
            MyApp.getSingleApp().mySocket = new MyServerSocket();

        }else
        {
            MyApp.getSingleApp().mySocket = new MyClientSocket(getDpcpInfo());
        }
        MyApp.getSingleApp().mySocket.start(iCallback);

    }

    private ICallback iCallback = new ICallback() {
        @Override
        public void callBack(int result, Object oj) {
            if(result == 0 )
            {
                handler.sendEmptyMessageDelayed(0,3000);
            }else
            {
                handler.sendEmptyMessage(1);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setUserInfoOver(EventFriendListUpdate eventFriendListUpdate)
    {
        for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.clientIp.equals(AppConfig.clientIp))
            {
                AppConfig.clientId = userBean.userID;
                break;
            }
        }
    }

    /*****************************
     * 获取服务器的 ip地址
     * @return
     */
    public String getDpcpInfo()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info=wifiManager.getDhcpInfo();
        System.out.println(info.serverAddress);
        return intToIp(info.serverAddress);
    }

    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public String getLocalIpAddress()
    {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }
    @Override
    public void initLisenter() {

    }


    @Override
    public int getContentView() {
        return R.layout.activity_splash;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    Intent intent = new Intent(SplashActivty.this,UserInfoActivity.class);
                    startActivity(intent);
                    SplashActivty.this.finish();
                    break;
                case 1:
                    if(dialogCreatUtil != null )
                    {
                        dialogCreatUtil.showSingleBtnDialog(null,"连接失败，请检查服务器",SplashActivty.this);
                    }
//                    Toast.makeText(SplashActivty.this,"连接失败，请坚持服务器",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
