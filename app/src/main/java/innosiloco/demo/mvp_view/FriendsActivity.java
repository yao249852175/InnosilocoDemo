package innosiloco.demo.mvp_view;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EncodeKeyEvent;
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.KeyBean;
import innosiloco.demo.beans.KeyCheckEvent;
import innosiloco.demo.beans.QuestionBean;
import innosiloco.demo.beans.SecretKeyBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.mvp_presenter.DataKeyUtil;
import innosiloco.demo.service.ParseDataHelper;
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AESUtil;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.CheckKeyUIUtil;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.ShowKeyUtil;
import innosiloco.demo.utils.TalkHelper;

public class FriendsActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    /*************
     * 查看聊天记录
     */
    public final int LookTalks = 1;

    private TextView titleView;

    private ListView listView;

    private UsbManager manager;

    private UsbDevice device;

    private UsbDeviceConnection connection;

    private Button btn_Bottom;

    byte[] mybuffer=new byte[1024];
    boolean threadcontrol_ct=false;
    boolean threadcontrol_mt=false;
    boolean threadsenddata=false;

    UsbInterface[] usbinterface=null;
    UsbEndpoint[][] endpoint=new UsbEndpoint[5][5];

    private CheckKeyUIUtil uiUtil;


    private ListView listView_log;

    private Button  log_clear;

    /*******************
     * 显示不同key的utils
     */
    private ShowKeyUtil showKeyUtil;

    @Override
    public void findViews() {
        listView = (ListView) findViewById(R.id.list_friends);
        titleView= (TextView)findViewById(R.id.tv_head_title);
        btn_Bottom = (Button) findViewById(R.id.tv_bottom);
        listView_log = (ListView)findViewById(R.id.list_log);
        log_clear =(Button) findViewById(R.id.btn_clearLog);
         arrayA = getResources().getStringArray(R.array.CheckQuest_list_A);
         arrayB = getResources().getStringArray(R.array.CheckQuest_list_B);
    }

    private final static String ACTION ="android.hardware.usb.action.USB_STATE";
    private final static String ACTION1 ="android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private PendingIntent mPermissionIntent;
    @Override
    public void initViews()
    {
        if(AppConfig.isServce)
        {
            btn_Bottom.setText(R.string.Label_RegisterDevice);
        }else
        {
            btn_Bottom.setText(R.string.Label_ReqeustMatch);
        }
        uiUtil = new CheckKeyUIUtil(this,listView_log,log_clear,btn_Bottom);
        showKeyUtil =  new ShowKeyUtil(uiUtil);

        //clear while lose the key
        TalkHelper.getSingle().clearTalk(AppConfig.clientId);
        friendListAdapter.notifyDataSetChanged();

        listView.setAdapter(friendListAdapter);
        setTitleAndColor(true);
        IntentFilter filter = new IntentFilter(ACTION1);
        filter.addAction(ACTION_USB_PERMISSION);
//        filter.addAction(ACTION1);
        registerReceiver(mUsbReceiver, filter);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        beginCheckDevice();
    }

    /***********************
     * 检测当前的USB是否已经连接
     * @return
     */
    private boolean checkUSBDevice()
    {

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            if(device.getVendorId()== AESKeyUitl.getSingleton().myvid&&device.getProductId()== AESKeyUitl.getSingleton().mypid)
            {
                return true;
            }
        }
        return false;

    }


    class ConnectedThread extends Thread{
        @Override
        public void destroy() {
            super.destroy();
        }
        public ConnectedThread()
        {
            if(connection!=null){
                connection.close();
            }
            usbinterface=new UsbInterface[device.getInterfaceCount()];
            for (int i = 0 ; i < usbinterface.length; i ++)
            {
                usbinterface[i] = device.getInterface(i);
                for(int j=0;j<usbinterface[i].getEndpointCount();j++) {
                    endpoint[i][j] = usbinterface[i].getEndpoint(j);
                }
            }
            connection = manager.openDevice(device);
            connection.claimInterface(usbinterface[1], true);
        }
        int a = 0;
        @Override
        public void run() {
            int datalength;
            while(threadcontrol_ct){
                /*if(threadsenddata){
                    threadsenddata=false;
                    //TODO 发送的数据
                    byte[] mytmpbyte= "helloworld".getBytes();
                    connection.bulkTransfer(endpoint[1][0], mytmpbyte, mytmpbyte.length, 30);
                }*/
                datalength=connection.bulkTransfer(endpoint[1][1], mybuffer, 1024, 30);
//                mydatatransfer.AddData(mybuffer, datalength);
                if(datalength>=0)
                {
                    handler.obtainMessage(1, AESUtil.toHex(mybuffer,datalength).trim()).sendToTarget();
                }
            }
        }
    }
    private ConnectedThread connectedThread;


    private void beginCheckDevice()
    {
        //检查权限
        setTitleAndColor(true);
        new Thread(runnable).start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run()
        {

            while (true)
            {
                if(checkUSBDevice())
                {
                    //判断权限
                    if(manager.hasPermission(device)){
                        connectedThread =  new ConnectedThread();
                        connectedThread.start();
                    }else
                    {
                        manager.requestPermission(device, mPermissionIntent);
                    }
//                    handler.obtainMessage(1,"检测到设备了").sendToTarget();
                    threadcontrol_ct=true;
                    break;
                }else
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action))
            {
                connectedThread =  new ConnectedThread();
                connectedThread.start();
                showKeyUtil.firstInsert();
                return;
            }else if(ACTION1.equals(action))
            {
                if(!AppConfig.isTest)
                {//收到了，设备发来的key
                    AESKeyUitl.getSingleton().setDecode_key("");
                    AESKeyUitl.getSingleton().setEncode_key("");
                }
                key = "";
                uiUtil.onDestory();
                threadcontrol_ct = false;
                beginCheckDevice();
//                Toast.makeText(FriendsActivity.this, action +":" ,Toast.LENGTH_LONG).show();
            }

        };
    };

    @Override
    public void initLisenter() {
        listView.setOnItemClickListener(this);
    }

    /************
     * 底部按钮的点击事件
     * @param view
     */
    public void onBottomBtnClick(View view)
    {
       if(TextUtils.isEmpty(key))
       {
            Toast.makeText(this,R.string.Label_InsertKeyPlease,Toast.LENGTH_LONG).show();
            return;
       }
        ((Button)view).setEnabled(false);
        showKeyUtil.setBegin(true,(Button)view);
        handler.sendEmptyMessageDelayed(998,5000);
       /*if(!AppConfig.isServce)
       {
           getKeyFromUsb(key);
       }*/
    }

    @Override
    public int getContentView() {
        return R.layout.activity_friends;
    }

    private BaseAdapter friendListAdapter = new BaseAdapter() {
        @Override
        public int getCount()
        {
            int size =  ParseDataHelper.onlineUser.size();
            return size;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            FriendViewHolder friendViewHolder = null;
            UserBean userBean = ParseDataHelper.onlineUser.get(position);
            if(convertView == null )
            {
                convertView = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.item_friend,null);
                friendViewHolder = new FriendViewHolder();
                friendViewHolder.head = (ImageView)convertView.findViewById(R.id.img_friend_head);
                friendViewHolder.name = (TextView) convertView.findViewById(R.id.tv_friend_name);
                friendViewHolder.speedNum = (TextView) convertView.findViewById(R.id.tv_friend_speedNum);
                friendViewHolder.lastSpeed = (TextView) convertView.findViewById(R.id.tv_friend_lastSpeed);
                friendViewHolder.bg = convertView.findViewById(R.id.ll_friend_item);
                convertView.setTag(friendViewHolder);
            }else
            {
                friendViewHolder = (FriendViewHolder) convertView.getTag();
            }
            if(userBean.userID == AppConfig.clientId)
            {//TODO  -----修改本机item背景颜色
                friendViewHolder.name.setText(userBean.userNike + "(本机)");
                friendViewHolder.bg.setBackgroundColor(Color.GRAY);
            }else
            {
                friendViewHolder.name.setText(userBean.userNike );
            }
            TalkBean talkBean = TalkHelper.getSingle().getLastTalk(userBean.userID);
            if(talkBean != null )
            {
                RonLog.LogE("lastContent:"+ talkBean.talkContent);
                friendViewHolder.speedNum.setVisibility(View.VISIBLE);


                switch (talkBean.fileType)
                {
                    case FileBean.isMp3:
                    case FileBean.isAAC:
                        friendViewHolder.lastSpeed.setText(R.string.Label_ACC);
                        break;
                    case FileBean.isJPE:
                    case FileBean.isPNG:
                        friendViewHolder.lastSpeed.setText(R.string.Label_Image);
                        break;
                    default:
                       /* if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
                        {
                            try {
                                String c = new String(AESUtil.toByte(AESUtil.encrypt("ron",talkBean.talkContent)));
                                friendViewHolder.lastSpeed.setText(c);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }else
                        friendViewHolder.lastSpeed.setText(talkBean.talkContent);*/
                        friendViewHolder.lastSpeed.setText("");
                        break;
                }
                friendViewHolder.speedNum.setText(""+ TalkHelper.getSingle().getOnceTalk(userBean.userID).talks.size());
            }else
            {
                friendViewHolder.lastSpeed.setText("");
                friendViewHolder.speedNum.setVisibility(View.GONE);
            }
            return convertView;
        }
    };


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void encodeKey(EncodeKeyEvent talkBean)
    {
        friendListAdapter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setUserInfoOver(EventFriendListUpdate eventFriendListUpdate)
    {
        /*for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.clientIp.equals(AppConfig.clientIp))
            {
                AppConfig.clientId = userBean.userID;
            }
        }*/
        friendListAdapter.notifyDataSetChanged();

    }

    /*******************
     * 有新的聊天记录
     * @param talkBean
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void talkUpdate(TalkBean talkBean)
    {

        friendListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(ParseDataHelper.onlineUser.get(position).userID == AppConfig.clientId)
        {
            Toast.makeText(this,"不能自己和自己聊天",Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 未检测到可用的key--- ron
        if(AppConfig.isServce )
        {
            RonLog.LogE( "" + ParseDataHelper.onlineUser.get(position).key);
            if(TextUtils.isEmpty(ParseDataHelper.onlineUser.get(position).key) || TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
            {
                if(uiUtil.getIsSuccess()) {
                    dialogCreatUtil.showSingleBtnDialog(null, getString(R.string.keyIsloss), this);
                    return;
                }
                else
                {
                    dialogCreatUtil.showSingleBtnDialog(null, getString(R.string.nomatch), this);
                    return;
                }
            }
        }


       else if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
        {
            if(uiUtil.getIsSuccess()) {
                dialogCreatUtil.showSingleBtnDialog(null, getString(R.string.keyIsloss), this);
                return;
            }
            else
            {
                dialogCreatUtil.showSingleBtnDialog(null, getString(R.string.nomatch), this);
                return;
            }
        }

        Intent intent = new Intent(this,SpeedActivity.class);
        intent.putExtra(SpeedActivity.TalkFromNick, ParseDataHelper.onlineUser.get(position).userNike);
        intent.putExtra(SpeedActivity.TalkFromID, ParseDataHelper.onlineUser.get(position).userID);
        startActivityForResult(intent,LookTalks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        friendListAdapter.notifyDataSetChanged();
    }

    private void setTitleAndColor(boolean isWarn)
    {
        EventBus.getDefault().post(new SecretKeyBean(!isWarn));
        if(isWarn)
        {
            showKeyUtil.onDestory();
            titleView.setText(R.string.keyIsloss);
            titleView.setTextColor(Color.RED);
        }else
        {
            titleView.setText(R.string.Label_onLine);
            titleView.setTextColor(Color.BLACK);
        }
    }
    private String key;

    private DataKeyUtil dataKeyUtil;

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void hadCheckKey(KeyCheckEvent keyCheckEvent)
    {
        RonLog.LogE("检查结果:" + keyCheckEvent.isSuccess);
     /*   if(keyCheckEvent.type == KeyCheckEvent.CheckKeyResult && !AppConfig.isServce)
        {
            uiUtil.setCheckResult(keyCheckEvent.isSuccess);
        }else
            uiUtil.beginCheck(keyCheckEvent.isSuccess,key);*/

       /* if(keyCheckEvent.isSuccess) {
            AESKeyUitl.getSingleton().setEncode_key(keyCheckEvent.key);
        }*/
        uiUtil.setCheckResult(keyCheckEvent.isSuccess);

       /* if(keyCheckEvent.type == KeyCheckEvent.CheckKeyResult && !AppConfig.isServce)
        {
            uiUtil.setCheckResult(keyCheckEvent.isSuccess);
            if(keyCheckEvent.isSuccess) {
                AESKeyUitl.getSingleton().setEncode_key(keyCheckEvent.key);
            }
        }else if(keyCheckEvent.type == KeyCheckEvent.CheckKeyBegin) {
            uiUtil.beginCheck(keyCheckEvent.isSuccess, keyCheckEvent.key);
        }*/
    }

    private void getKeyFromUsb(String key)
    {
        if(AppConfig.isServce )
        {
            AESKeyUitl.getSingleton().setEncode_key(key);

            if(dataKeyUtil == null )
            {
                dataKeyUtil = new DataKeyUtil(this);
            }
            dataKeyUtil.insert(key);
            //Toast.makeText(this,R.string.Label_HadInsertSqlite,Toast.LENGTH_LONG).show();
        }else
        {
            KeyBean keyBean = new KeyBean();
            keyBean.clientID = AppConfig.clientId;
            keyBean.key = key;
            MyApp.getSingleApp().mySocket.sendKey2ServerCheckKey(keyBean);
//            uiUtil.beginCheck(false,key);
            EventBus.getDefault().post(new KeyCheckEvent(KeyCheckEvent.CheckKeyBegin,false,key));
        }
    }
    String[] arrayA ;
    String[] arrayB ;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            QuestionBean questionBean = null;
            switch (msg.what)
            {
                case 0:
                    friendListAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    if(!AppConfig.isTest)
                    {//收到了，设备发来的key
//                        AESKeyUitl.getSingleton().setDecode_key("ron");
                        if(TextUtils.isEmpty(key))
                        {//刚刚才得到key
                            key = msg.obj.toString().trim();
                            setTitleAndColor(false);
//                             getKeyFromUsb(key);,
                        }
                        showKeyUtil.setKeyValue(msg.obj.toString().trim());



                        /*boolean setSuccess = AESKeyUitl.getSingleton().setEncode_key(msg.obj.toString());
                        if(setSuccess)
                        {
                            setTitleAndColor(false);
                        }*/
                    }
//                    textView.setText(msg.obj.toString());
                    break;
                case 999:
                    MyApp.getSingleApp().exitApp();
                    break;
                case 998:
                    btn_Bottom.setEnabled(true);
                    break;
                case QuestionBean.QuestionStep_2:
                    questionBean = (QuestionBean) msg.obj;
                    if(AppConfig.isServce)
                    {
                        uiUtil.addNewLog(arrayA[2] +":" + questionBean.key);
                        if(dataKeyUtil == null)
                            dataKeyUtil = new DataKeyUtil(MyApp.getSingleApp());

                        if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()) || !dataKeyUtil.checkKeyIsExit(AESKeyUitl.getSingleton().getEncode_key()))
                        {
                            uiUtil.addNewLog(getString(R.string.Label_ServerKeyNoPuf));
                            QuestionBean questionBean1 = new QuestionBean();
                            questionBean1.type = QuestionBean.QuestionResult;
                            questionBean1.key = "";
                            questionBean1.isSuccess = false;
                            MyApp.getSingleApp().mySocket.sendQuestion(questionBean1);
                            return;
                        } else  if(!uiUtil.getIsSuccess())
                        {//如果检查是失败，就显示 puf失败
                            uiUtil.addNewLog(getString(R.string.Label_CheckKey_ErrPuf));
                            QuestionBean questionBean1 = new QuestionBean();
                            questionBean1.type = QuestionBean.QuestionResult;
                            questionBean1.key = "";
                            questionBean1.isSuccess = false;
                            MyApp.getSingleApp().mySocket.sendQuestion(questionBean1);
                            return;
                        }else
                        {
                            uiUtil.addNewLog(arrayA[3]);
                        }
                        //step2 服务器 完成后，主动请求 ,客户端回复over
                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_3,"",false,true));

                    }else
                    {
                        uiUtil.addNewLog(arrayB[2] + ":" + showKeyUtil.cacheKey);
                        if(uiUtil.getIsSuccess())
                        //uiUtil.addNewLog(getString(R.string.Label_CheckKey_SuccessPuf));
                        //发送给服务器端 服务器第二次 请求要 客户端的key
                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_2,showKeyUtil.cacheKey,false,false));
                    }
                    break;
                case QuestionBean.QuestionStep_3:
                    questionBean = (QuestionBean) msg.obj;
                    if(!AppConfig.isServce)//客户端
                    {
                        if(questionBean.isQuestion)
                        {
                            uiUtil.addNewLog(arrayB[4]);
                            MyApp.getSingleApp().mySocket.sendQuestion(new
                                    QuestionBean(QuestionBean.QuestionStep_3,"",false,false));
                        }else
                        {
                            uiUtil.addNewLog(arrayB[5] + ":" + questionBean.key);
                            MyApp.getSingleApp().mySocket.sendQuestion(new
                                    QuestionBean(QuestionBean.QuestionStep_4,"",false,false));
                        }

                    }else
                    {//服务端
                        uiUtil.addNewLog(arrayA[4]);
                        uiUtil.addNewLog(arrayA[5] + showKeyUtil.cacheKey);

                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_3,showKeyUtil.cacheKey,false,false));
                    }
                    break;
                case QuestionBean.QuestionStep_4:
                    questionBean = (QuestionBean) msg.obj;
                    if(AppConfig.isServce)
                    {
                        AppConfig.setCacheKey(showKeyUtil.lastRealyKey,(byte)1);
                        uiUtil.addNewLog(arrayA[6] +":" + showKeyUtil.cacheKey);
                        uiUtil.addNewLog(arrayA[7]);
                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_5,showKeyUtil.cacheKey,false,false));
                    }else
                    {

                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( LookTalks == requestCode  && resultCode == RESULT_OK )
        {
            byte id = data.getByteExtra("clearId",(byte)-1);
            RonLog.LogE("清理的ID号："+ id);
            boolean clearSuccess = TalkHelper.getSingle().clearTalk(id);
            try
            {
                RonLog.LogE(TalkHelper.getSingle().getOnceTalk(id).talks.size()+",------------");
            }catch (Exception exception)
            {
                exception.printStackTrace();
            }
            friendListAdapter.notifyDataSetChanged();
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void checkQuestion(QuestionBean questionBean)
    {
        if(TextUtils.isEmpty(showKeyUtil.cacheKey))
        {
            uiUtil.addNewLog(getString(R.string.keyIsloss));
            MyApp.getSingleApp().mySocket.sendQuestion(new
                    QuestionBean(QuestionBean.QuestionResult,showKeyUtil.cacheKey,false,true));
            btn_Bottom.setEnabled(true);
            return;
        }

        String[] arrayA = getResources().getStringArray(R.array.CheckQuest_list_A);
        String[] arrayB = getResources().getStringArray(R.array.CheckQuest_list_B);
        switch (questionBean.type)
        {
            case QuestionBean.QuestionStep_1:
                if(AppConfig.isServce)
                {
                    if(questionBean.isQuestion)
                    {
                        uiUtil.addNewLog(getString(R.string.Label_Client_begin));
                        uiUtil.addNewLog(arrayA[0]);
                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_1,"",false,true));
                    }else
                    {
                        uiUtil.addNewLog(arrayA[1]+":" + questionBean.key);
                        MyApp.getSingleApp().mySocket.sendQuestion(new
                                QuestionBean(QuestionBean.QuestionStep_2,"",false,true));
                    }
                }else
                {
                    uiUtil.addNewLog(arrayB[0]);
                    uiUtil.addNewLog(arrayB[1] + ":" + showKeyUtil.cacheKey);
                    //发送回复服务器的请求
                    MyApp.getSingleApp().mySocket.sendQuestion(new
                            QuestionBean(QuestionBean.QuestionStep_1,showKeyUtil.cacheKey,false,false));
                }
                break;
            case QuestionBean.QuestionStep_2:
               Message msg_2 = handler.obtainMessage(QuestionBean.QuestionStep_2,questionBean);
                handler.sendMessageDelayed(msg_2,1000);
                break;
            case QuestionBean.QuestionStep_3:
                Message msg_3 = handler.obtainMessage(QuestionBean.QuestionStep_3,questionBean);
                handler.sendMessageDelayed(msg_3,1000);
                break;
            case QuestionBean.QuestionStep_4:

                Message msg_4 = handler.obtainMessage(QuestionBean.QuestionStep_4,questionBean);
                handler.sendMessageDelayed(msg_4,1000);
                break;
            case QuestionBean.QuestionResult:
                if(!questionBean.isSuccess)
                {
                    uiUtil.addNewLog(getString(R.string.Label_CheckKey_ErrPuf));
                 }
                break;
            case QuestionBean.QuestionStep_5:
                if(!AppConfig.isServce)
                {
                    if(dataKeyUtil == null )
                    {
                        dataKeyUtil = new DataKeyUtil(this);
                    }
                    dataKeyUtil.insert(showKeyUtil.lastRealyKey);
                    AppConfig.setCacheKey(showKeyUtil.lastRealyKey,(byte) 0);
                    AESKeyUitl.getSingleton().setEncode_key(showKeyUtil.lastRealyKey);
                }

                uiUtil.addNewLog(arrayB[5] + ":" + questionBean.key);
                uiUtil.addNewLog(arrayB[7]);
                uiUtil.addNewLog(arrayB[8]);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void userOnline(EventDownLine eventDownLine)
    {
        RonLog.LogE("列表页收到下线通知");
        if(!AppConfig.isServce)
        {
//            if(eventDownLine.clientId == AppConfig.clientId)
//            {//用户自己的客户端
            if(dialogCreatUtil != null )
            {
                dialogCreatUtil.showSingleBtnDialog("", "连接服务器失败", FriendsActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handler.sendEmptyMessageDelayed(999,2000);
                    }
                });
            }
//            }
        }
    }

    private class FriendViewHolder
    {
        ImageView head;
        TextView name;
        TextView speedNum;
        TextView lastSpeed;
        View bg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showKeyUtil.onDestory();
        uiUtil.onDestory();
        MyApp.getSingleApp().mySocket.stop();
        MyApp.getSingleApp().mySocket = null;
        unregisterReceiver(mUsbReceiver);
        MyApp.getSingleApp().exitApp();
        AppConfig.cacheKey_self="";
        AppConfig.cacheKey_other="";
    }
}
