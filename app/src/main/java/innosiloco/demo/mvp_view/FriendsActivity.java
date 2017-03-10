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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.SecretKeyBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.service.ParseDataHelper;
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;
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

    byte[] mybuffer=new byte[1024];
    boolean threadcontrol_ct=false;
    boolean threadcontrol_mt=false;
    boolean threadsenddata=false;

    TextView textView;

    UsbInterface[] usbinterface=null;
    UsbEndpoint[][] endpoint=new UsbEndpoint[5][5];


    @Override
    public void findViews() {
        listView = (ListView) findViewById(R.id.list_friends);
        textView = (TextView) findViewById(R.id.test);
        titleView= (TextView)findViewById(R.id.tv_head_title);
    }

    private final static String ACTION ="android.hardware.usb.action.USB_STATE";
    private final static String ACTION1 ="android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private PendingIntent mPermissionIntent;
    @Override
    public void initViews()
    {
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
            if(device.getVendorId()== AESKeyUitl.getSingleton().myvid&&device.getProductId()==AESKeyUitl.getSingleton().mypid)
            {
                return true;
            }
        }
        return false;

    }


    class ConnectedThread extends Thread{
        @Override
        public void destroy() {
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
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
                if(datalength>=0){
                    handler.obtainMessage(1,new String(mybuffer).trim() ).sendToTarget();
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
            if(!AppConfig.isTest)
            {//收到了，设备发来的key
                AESKeyUitl.getSingleton().setDecode_key("");
                AESKeyUitl.getSingleton().setEncode_key("");
            }
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
                return;
            }
            threadcontrol_ct = false;
            beginCheckDevice();
            Toast.makeText(FriendsActivity.this, action +":" ,Toast.LENGTH_LONG).show();
        };
    };

    @Override
    public void initLisenter() {
        listView.setOnItemClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_friends;
    }


    private BaseAdapter friendListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return ParseDataHelper.onlineUser.size();
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
                convertView.setTag(friendViewHolder);
            }else
            {
                friendViewHolder = (FriendViewHolder) convertView.getTag();
            }
            friendViewHolder.name.setText(userBean.userNike);
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
                        friendViewHolder.lastSpeed.setText(talkBean.talkContent);
                        break;
                }
                friendViewHolder.speedNum.setText(""+TalkHelper.getSingle().getOnceTalk(userBean.userID).talks.size());
            }else
            {
                friendViewHolder.lastSpeed.setText("");
                friendViewHolder.speedNum.setVisibility(View.GONE);
            }
            return convertView;
        }
    };


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
        Intent intent = new Intent(this,SpeedActivity.class);
        intent.putExtra(SpeedActivity.TalkFromNick,ParseDataHelper.onlineUser.get(position).userNike);
        intent.putExtra(SpeedActivity.TalkFromID,ParseDataHelper.onlineUser.get(position).userID);
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
            titleView.setText(R.string.keyIsloss);
            titleView.setTextColor(Color.RED);
        }else
        {
            titleView.setText(R.string.Label_onLine);
            titleView.setTextColor(Color.BLACK);
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    friendListAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    if(!AppConfig.isTest)
                    {//收到了，设备发来的key
//                        AESKeyUitl.getSingleton().setDecode_key("ron");
                        boolean setSuccess = AESKeyUitl.getSingleton().setEncode_key(msg.obj.toString());
                        if(setSuccess)
                        {
                            setTitleAndColor(false);
                        }
                    }
                    textView.setText(msg.obj.toString());
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
    public void userOnline(EventDownLine eventDownLine)
    {
        RonLog.LogE("列表页收到下线通知");
        if(!AppConfig.isServce)
        {
//            if(eventDownLine.clientId == AppConfig.clientId)
//            {//用户自己的客户端
            if(dialogCreatUtil != null )
            {
                dialogCreatUtil.showSingleBtnDialog("","连接服务器失败",FriendsActivity.this);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getSingleApp().mySocket.stop();
        MyApp.getSingleApp().mySocket = null;
        unregisterReceiver(mUsbReceiver);
        MyApp.getSingleApp().exitApp();
    }
}
