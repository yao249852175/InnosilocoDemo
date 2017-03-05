package innosiloco.demo.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;
import innosiloco.demo.MyApp;
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.FrameBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.TalkHelper;

/**
 * Created by ron on 2017/2/25.
 * 读写 线程怎么分开呢
 * 里面在套一个写的线程
 */
public class SocketThread
{
    public byte getClientId() {
        return clientId;
    }

    public byte clientId;

    private Socket socket;

    private Thread readThread;

    private Thread writeThread;

    private InputStream inputStream;

    private OutputStream outputStream;

    private LinkedBlockingQueue<byte[]> sendMsgs;

    /**********************************
     * <p>标示 读写线程，是否可以使用</p>
     */
    private boolean  writeAndReadAble;

    /***************
     * 当前的通讯是否 正常
     */
    public boolean isAlive;

    private MyServerSocket.ClientIsAliveListener clientIsAliveListener;


    public SocketThread(Socket socket, byte clientId, MyServerSocket.ClientIsAliveListener clientIsAliveListener)
    {
        this.socket = socket;
        this.clientId = clientId;
        RonLog.LogE("新连接的clientid：" + clientId);
        try {
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
        /*if(!AppConfig.isServce)
        {
            handler.sendEmptyMessageDelayed(1,1000);
            handler.sendEmptyMessageDelayed(0,5000);
        }*/


        this.clientIsAliveListener = clientIsAliveListener;
    }

    /*****************
     * <p>初始化</p>
     */
    private void init() throws IOException
    {
        readThread =   new Thread(readRun);
        writeThread =  new Thread(writeRun);
        inputStream =  socket.getInputStream();
        outputStream = socket.getOutputStream();
        sendMsgs = new LinkedBlockingQueue<>();
    }

    /**************
     * <p>开始启动</p>
     */
    public void begin()
    {
        writeAndReadAble = true;
        readThread.start();
        writeThread.start();
        isAlive = true;
    }

    /***************
     * <p>停止</p>
     */
    public void stop()
    {
        writeAndReadAble = false;
        if(readThread != null && readThread.isAlive())
        {
            readThread.interrupt();
            readThread = null;
        }

        if(writeThread != null && writeThread.isAlive() )
        {
            writeThread.interrupt();
            writeThread = null;
        }

        if(socket != null )
        {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isAlive = false;
        if(clientIsAliveListener != null )
        {
            UserBean userBean = new UserBean();
            userBean.userID = clientId;
            clientIsAliveListener.onClientAlive(false,userBean);
        }
        RonLog.LogE("开始断开连接");
        if(!AppConfig.isServce)
        {
            RonLog.LogE("发送Eventbus");
            EventBus.getDefault().post(new EventDownLine(true,clientId));
        }
    }

    /***********
     * <p>添加发送的消息</p>
     * @param data
     */
    public void addMsg(byte[] data)
    {
        this.sendMsgs.offer(data);
    }

    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0://没有收到数据,说明socket断开了
                    stop();

                    break;

                case 1://开始发送 心跳包
                      sendSocketHeart();
                    break;
            }
            return true;
        }
    });

    /***************
     * 发送心跳包
     */
    public void  sendSocketHeart()
    {
        FrameBean frameBean = new FrameBean();
        frameBean.content = new byte[]{0,0,0,0};
        frameBean.send2ID =clientId;
        frameBean.cmdIndex = AppConfig.SocketHeart;
        byte[] data= ParseDataHelper.frame2Btye(frameBean);
        addMsg(data);
    }

    int dataIsErrCount = 0;

    /**********************
     * <p>read的线程Run</p>
     */
    private Runnable readRun = new Runnable()
    {
        @Override
        public void run()
        {
            byte[] recData = new byte[5000];
            int index = 0;
            byte data = 0;
            try {
                while(writeAndReadAble)
                {
                    data = (byte) inputStream.read();
                    RonLog.LogE("data:" + data);
                    if(data == -1 )
                    {//socket异常关闭
                        dataIsErrCount ++;
                    }else
                    {
                        dataIsErrCount = 0;
                    }
                    if(dataIsErrCount >= 10 )
                    {
                        break;
                    }else
                    {
                        recData[index ++ ] = data;
                        if(index > 5&& ParseDataHelper.checkEnd(recData,index- 4))
                        {
                            parseData(recData,index);
                            index = 0;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
                stop();
        }
    };

    /**************************************
     * 解析数据
     * @param data
     * @param length
     */
    private void parseData(byte[] data ,int length)
    {
        if(ParseDataHelper.checkCRC(data,0,length))
        {
            FrameBean frameBean = ParseDataHelper.byte2Frame(data,0,length);
            if(frameBean == null )
            {
                RonLog.LogE("解密出问题了");
                return;
            }
            RonLog.LogE("cmdIndex:" + frameBean.cmdIndex);
                switch (frameBean.cmdIndex)
                {
                    case AppConfig.SocketHeart://收到心跳包，
                        handler.removeMessages(0);//清理 断开判断的延迟消息
                        handler.sendEmptyMessageDelayed(1,1000);
                        handler.sendEmptyMessageDelayed(0,5000);
                        break;
                    case AppConfig.FriendCode:
                        //有人 下线
                        ParseDataHelper.onlineUser = ParseDataHelper.jsonFriendList(new String(frameBean.content));
                        EventBus.getDefault().post(new EventFriendListUpdate());
                        break;
                    case AppConfig.TalkCode:
//                        ParseDataHelper.talks.offer(frameBean);
                        TalkBean talkBean = ParseDataHelper.
                                json2TalkBean(new String(frameBean.content)) ;
                        if(talkBean == null) return;
                        if(AppConfig.isServce)
                        {
                            if(talkBean.toID == AppConfig.clientId )
                            {
                                TalkHelper.getSingle().addTalk( talkBean );
                                EventBus.getDefault().post(talkBean);
                            }else
                            {
                                MyApp.getSingleApp().mySocket.sendTalk(talkBean);
                            }
                        }else
                        {
                            TalkHelper.getSingle().addTalk( talkBean );
                            EventBus.getDefault().post(talkBean);
                        }
                        break;
                    case AppConfig.UserInfoCode:
                        //用户上线 和 设置用信息
                        UserBean userBean = ParseDataHelper.json2Friend(new String(frameBean.content));
                        userBean.userID = clientId;
                        RonLog.LogE("userNick:" + userBean.userNike + ",ip:"+ userBean.clientIp);
                        if(clientIsAliveListener != null )
                        {
                            clientIsAliveListener.onClientAlive(true,userBean);
                        }

                        break;
                }

        }
    }

    /******************
     * <p>写的线程</p>
     */
    private Runnable writeRun = new Runnable() {
        @Override
        public void run()
        {
            while (writeAndReadAble)
            {
                try {
                   byte[] data = sendMsgs.take();
                    if(data != null)
                        outputStream.write(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
