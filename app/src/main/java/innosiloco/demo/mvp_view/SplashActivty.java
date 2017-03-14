package innosiloco.demo.mvp_view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.WifiUtil;

/**
 * Created by Administrator on 2017/2/25.
 */
public class SplashActivty extends BaseActivity
{
    private List<ScanResult> wifiList;
    private WifiManager wifiManager;
    private List<String> passableHotsPot;
    private WifiReceiver wifiReceiver;
    private boolean isConnected=false;

    /**************
     * 客户端连接成功
     */
    private final int What_WifiConnResult = 2;
    @Override
    public void findViews()
    {

    }

    @Override
    public void initViews()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WAKE_LOCK},
                    0);
        }else
        {
           init();
            new Thread(runnable).start();
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
                new Thread(runnable).start();
            }
        }
    }

    /**************
     * 创建socket
     * @param isServer
     */
    private void createSocket(boolean isServer)
    {
        if(isServer )
        {
            if(WifiUtil.isWifiApEnabled(this))
            {
                AppConfig.clientIp = getLocalIpAddress();
                MyApp.getSingleApp().mySocket = new MyServerSocket();
                MyApp.getSingleApp().mySocket.start(iCallback);
                return;
            }

            boolean isOpen = WifiUtil.setWifiApEnabled(SplashActivty.this,true);
            if( isOpen )
            {
                AppConfig.clientIp = getLocalIpAddress();
                MyApp.getSingleApp().mySocket = new MyServerSocket();
                MyApp.getSingleApp().mySocket.start(iCallback);
            }else
            {
                dialogCreatUtil.showSingleBtnDialog(null, "请先在设置里面打开wifi热点", this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                        startActivityForResult(intent,1);

                    }
                });
            }
        }else
        {
            if(WifiUtil.isWifiConnected(this))
            {
                isConnected = true;
                handler.sendEmptyMessage(What_WifiConnResult);
            }else
            {
                isConnected = false;
                handler.sendEmptyMessage(What_WifiConnResult);
            }

            //自动连接wifi的逻辑太复杂，先不弄
           /* wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiReceiver = new WifiReceiver();
            IntentFilter intentFilter = new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(wifiReceiver,intentFilter);

            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }

            wifiManager.startScan();*/
        }

    }

    private void init()
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
                                    dialogCreatUtil.cancel2BtnDialog();
                                    createSocket(false);
                                    break;
                                case R.id.tv_ok:
                                    dialogCreatUtil.cancel2BtnDialog();
                                    createSocket(true);
                                    break;
                            }
                        }
                    },this);
        }

//        AppConfig.clientIp = getLocalIpAddress();
        /*RonLog.LogD("获取到IP:" + AppConfig.clientIp);
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
        MyApp.getSingleApp().mySocket.start(iCallback);*/

    }

    private ICallback iCallback = new ICallback() {
        @Override
        public void callBack(int result, Object oj)
        {
            if(result == 0 )
            {
//                handler.sendEmptyMessageDelayed(0,3000);
                setUserName();
            }else
            {
                isConnected = false;
                handler.sendEmptyMessage(What_WifiConnResult);
            }
        }
    };

    /***************************
     * 告诉服务器 用户的名字
     */
    private void  setUserName()
    {
        UserBean userBean = new UserBean();
        userBean.clientIp = AppConfig.clientIp;
        if(AppConfig.isServce)
        userBean.userNike = getResources().getString(R.string.name_Server);
        else
            userBean.userNike = getResources().getString(R.string.name_Client);
        userBean.userID = AppConfig.clientId;
        AppConfig.userNick = userBean.userNike;
        if(AppConfig.isTest)
        {
            userBean.key = AESKeyUitl.getSingleton().getEncode_key();
        }
        MyApp.getSingleApp().mySocket.updateUser(userBean);;
    }

    @Override
    protected void onDestroy()
    {
        if(wifiReceiver != null )
        {
            unregisterReceiver(wifiReceiver);
        }
        super.onDestroy();

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setUserInfoOver(EventFriendListUpdate eventFriendListUpdate)
    {
        for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.clientIp.equals(AppConfig.clientIp))
            {
                AppConfig.clientId = userBean.userID;
                handler.sendEmptyMessageDelayed(0,2000);
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


    /* 监听热点变化 */
    private final class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            RonLog.LogE("开始扫描的结果");
            if(intent.getAction().equals(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                wifiList = wifiManager.getScanResults();
                if (wifiList == null || wifiList.size() == 0 || isConnected)
                {
                    handler.sendEmptyMessage(What_WifiConnResult);
                    return;
                }
                onReceiveNewNetworks(wifiList);
            }else
            {
                /*ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if(wifiInfo != null )
                {
                    isConnected = true;
                    handler.sendEmptyMessage(What_WifiConnResult);
                }*/
            }

        }
    }


    /*当搜索到新的wifi热点时判断该热点是否符合规格*/
    public void onReceiveNewNetworks(List<ScanResult> wifiList){
        passableHotsPot=new ArrayList<String>();
        for(ScanResult result:wifiList){
            System.out.println(result.SSID);
            if((result.SSID).contains(WifiUtil.WIFINAME))
                passableHotsPot.add(result.SSID);
        }
        synchronized (this) {
            connectToHotpot();
        }
    }

    /*连接到热点*/
    public void connectToHotpot(){
        if(passableHotsPot==null || passableHotsPot.size()==0)
        {
            handler.sendEmptyMessageDelayed(What_WifiConnResult,500);
            return;
        }
        WifiConfiguration wifiConfig=this.setWifiParams();
        int wcgID = wifiManager.addNetwork(wifiConfig);
        boolean flag=wifiManager.enableNetwork(wcgID, true);
        isConnected=flag;
        System.out.println("connect success? "+flag);
//        handler.sendEmptyMessage(2);
        handler.sendEmptyMessageDelayed(What_WifiConnResult,500);

    }

    /*设置要连接的热点的参数*/
    public WifiConfiguration setWifiParams(){
        WifiConfiguration apConfig=new WifiConfiguration();
        apConfig.SSID="\""+ WifiUtil.WIFINAME+"\"";
        apConfig.preSharedKey="\""+ WifiUtil.WIFIPWD+"\"";
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return apConfig;
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
                    Intent intent = new Intent(SplashActivty.this,FriendsActivity.class);
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
                case What_WifiConnResult://客户端wifi连接成功
                    if(isConnected)
                    {
                        AppConfig.clientIp = getLocalIpAddress();
                        MyApp.getSingleApp().mySocket = new MyClientSocket(getDpcpInfo());
                        MyApp.getSingleApp().mySocket.start(iCallback);
                    }else
                    {
                        dialogCreatUtil.showSingleBtnDialog(null,
                                getResources().getString(R.string.client_connWifi, WifiUtil.WIFINAME,
                                        WifiUtil.WIFIPWD), SplashActivty.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent1 =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                                        startActivityForResult(intent1,0);
                                    }
                                });

                    }

                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RonLog.LogE("反馈了：：："+ requestCode);
        init();
    }

    /***********************
     * 错误的文件写到文件中
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run()
        {
            File fileErrimg = new File(AppConfig.ErrIMGDirectory);
            if(!fileErrimg.exists() || fileErrimg.list().length < 3)
            {
                try {
                    writeDefaultVideo2SD("img_err",AppConfig.ErrIMGDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File fileErrVoice = new File(AppConfig.ErrVoiceDirectory);
            if(!fileErrVoice.exists() || fileErrVoice.list().length < 7)
            {
                try {
                    writeDefaultVideo2SD("voice_err",AppConfig.ErrVoiceDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * <p>默认的1个视频放到 手机内存中</p>
     *
     * @throws IOException
     */
    public void writeDefaultVideo2SD( String fileName ,String destPath) throws IOException {
        //检查文件路径
        //String tempFilePath = UavStaticVar.cStrVideoCapPath;
        File tempMusic = new File(destPath);
        if (!tempMusic.exists()) {
            tempMusic.mkdirs();
        }

        AssetManager aa = this.getAssets();
        String[] files = aa.list(fileName);

        for (int i = 0; i < files.length; i++) {
            InputStream is = aa.open(fileName + File.separator  + files[i]);
            FileOutputStream fos = new FileOutputStream(destPath + File.separator + files[i]);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
            fos.close();
            is.close();
        }
    }
}
