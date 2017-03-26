package innosiloco.demo.service;

import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.FrameBean;
import innosiloco.demo.beans.ICallback;
import innosiloco.demo.beans.KeyBean;
import innosiloco.demo.beans.QuestionBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.BitmapUtils;
import innosiloco.demo.utils.FileUtils;
import innosiloco.demo.utils.RonLog;

/**
 * Created by ron on 2017/2/25.
 */
public class MyServerSocket implements Runnable,MySocket
{
   private List<SocketThread> socketThreadList;

    private ICallback iCallback;
    private boolean isAlive;

    private Thread thread;

    private ServerSocket serverSocket;

    public MyServerSocket()
    {
        init();
    }

    /*****************
     * 服务器启动
     */
    public void start(ICallback iCallback)
    {
        this.iCallback = iCallback;
        isAlive = true;
        thread = new Thread(this);
        thread.start();
        AppConfig.clientId = 0;
    }

    /*****************
     * 关闭服务器
     */
    public void stop()
    {
        isAlive = false;
        if(thread != null && thread.isAlive())
        {
            thread.interrupt();
            thread = null;
        }
    }

    /******************
     * 初始化工作
     */
    private void init()
    {
        socketThreadList = new ArrayList<>();
        ParseDataHelper.talks.clear();
        ParseDataHelper.onlineUser.clear();
        AppConfig.isServce = true;
    }

    /**********************
     * <p>给客户端分配id号</p>
     * @return
     */
    private int getClientId()
    {
        //先清理掉，已经回收的Socket
        for(int i = 0 ; i < socketThreadList.size(); i++)
        {
            if(!socketThreadList.get(i).isAlive)
            {
                socketThreadList.remove(i);
                i--;
            }
        }

        /**************** 客户端的Id已经存在**********/
        boolean clientIsExit = false;

        for (int i = 1 ; i < 255; i ++)
        {
            for (int j =0 ; j < socketThreadList.size() ; j ++ )
            {
                if(socketThreadList.get(j).getClientId() == i)
                {
                    clientIsExit = true;
                    break;
                }
            }

            if(!clientIsExit)
            {
                return i;
            }
        }
        return  0;
    }

    @Override
    public void run()
    {
        try {
             serverSocket = new ServerSocket(AppConfig.PORT);
            RonLog.LogD("创建服务器");
            if(iCallback != null )
            {
                iCallback.callBack(0,"");
            }
            while (isAlive)
            {
                Socket socket = serverSocket.accept();
                RonLog.LogD("有客户端连接");
                /***************分配客户端************/
                SocketThread socketThread = new SocketThread(socket,(byte) getClientId(),clientIsAliveListener);
                socketThread.begin();
                socketThreadList.add(socketThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(iCallback != null )
            {
                iCallback.callBack(1,"");
            }
        }

    }

    /****************
     * 更新服务器的用户信息
     * @param userBean 服务器的用户信息
     */
    public void updateUser(UserBean userBean)
    {
//        RonLog.LogE("updateUser");
        clientIsAliveListener.onClientAlive(true,userBean);
//        EventBus.getDefault().post(new EventFriendListUpdate());
    }

    /**********************
     * 发送Talk到服务器
     * @param talkBean
     */
    public void sendTalk(TalkBean talkBean)
    {
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = 0;
        frameBean.cmdIndex = AppConfig.TalkCode;
        String content = ParseDataHelper.talkBean2Json(talkBean);
        sendMsg2Client(talkBean.toID, AppConfig.TalkCode,content);
    }



    /********************
     * 监听客户端的存活状态
     */
    private ClientIsAliveListener clientIsAliveListener = new ClientIsAliveListener() {
        @Override
        public void onClientAlive(boolean isAlive,UserBean userBean) {
            if(isAlive)
            {
                boolean hadExit = false;
                for (UserBean user: ParseDataHelper.onlineUser)
                {
                    if(user.userID == userBean.userID)
                    {
                        user.userNike = userBean.userNike;
                        user.key  = userBean.key;
                        hadExit =true;
                        break;
                    }
                }

                /**********已经存在************/
                if(!hadExit)
                {
                    ParseDataHelper.onlineUser.add(userBean);
                }
            }else
            {//用户下线通知
                for (UserBean bean: ParseDataHelper.onlineUser)
                {
                    if(bean.userID == userBean.userID)
                    {
                        ParseDataHelper.onlineUser.remove(bean);
                        break;
                    }
                }
            }
            //重新更新一个 用户列表
            sendMsg2All(AppConfig.FriendCode,
                    ParseDataHelper.friendList2Json(ParseDataHelper.onlineUser));
            EventBus.getDefault().post(new EventFriendListUpdate());
        }
    };


    /**************************
     * <p>发送消息到 所有的客户端</p>
     */
    private void sendMsg2All(byte cmd,String content)
    {
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = 0;
        frameBean.cmdIndex = cmd;
        frameBean.content = content.getBytes();
        byte[] data = ParseDataHelper.frame2Btye(frameBean);
        for (SocketThread socketThread:socketThreadList)
        {
            socketThread.addMsg(data);
        }
    }

    /**************
     * <p>发送消息到指定的客户端</p>
     */
    private void sendMsg2Client(byte send2ID,byte cmd,String content)
    {
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = send2ID;
        frameBean.cmdIndex = cmd;
        frameBean.content = content.getBytes();
        byte[] data = ParseDataHelper.frame2Btye(frameBean);
        for (SocketThread socketThread:socketThreadList)
        {
            if(socketThread.clientId == send2ID)
            {
                socketThread.addMsg(data);
                break;
            }
        }
    }

    /**************
     * <p>客户端存活状态发生变化</p>
     */
    public interface ClientIsAliveListener
    {
        public void onClientAlive(boolean isAlive, UserBean userBean);
    }

    @Override
    public void sendFileTalk(TalkBean talkBean)
    {
        SocketThread socketThread1 = null;
        for (SocketThread socketThread:socketThreadList)
        {
            if(socketThread.clientId == talkBean.toID)
            {
                socketThread1 = socketThread;
                break;
            }
        }
        if(socketThread1 == null )
           return;

        byte type = FileUtils.fliePath2Type(talkBean.talkContent);
        if(type == FileBean.isMp3 )
        {
            File file = new File(talkBean.talkContent);
            if(file.isFile() && file.length() > AppConfig.maxSendFIleLength)
            {
                Toast.makeText(MyApp.getSingleApp(),MyApp.getSingleApp().getString(R.string.uploadFileIsBig),Toast.LENGTH_SHORT).show();
            }else
            {
                socketThread1.addFileMsg(talkBean.talkContent,talkBean.sendID,talkBean.toID);
            }
        }else
        if(type == FileBean.isJPE|| type == FileBean.isPNG )
        {
            File file = new File(talkBean.talkContent);
            if(file.isFile() && file.length() > AppConfig.maxSendFIleLength)
            {
                talkBean.talkContent = BitmapUtils.compressBitmap(BitmapFactory.decodeFile(file.getPath()));
            }

            socketThread1.addFileMsg(talkBean.talkContent,talkBean.sendID,talkBean.toID);
        }
    }

    public void sendQuestion(QuestionBean questionBean)
    {
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = 0;
        frameBean.cmdIndex = AppConfig.QuestionCheck;
        frameBean.content = ParseDataHelper.Question2Json(questionBean).getBytes();
        for (SocketThread socketThread:socketThreadList)
        {
            socketThread.addMsg(ParseDataHelper.frame2Btye(frameBean));
        }

    }


    /********
     * 发生 key 到服务器 检测key
     * @param keyBean
     */
    public void sendKey2ServerCheckKey(KeyBean keyBean)
    {
        //TODO 无用

    }

    public void sendCheckKeyResult(KeyBean keyBean)
    {
        SocketThread socketThread1 = null;
        for (SocketThread socketThread:socketThreadList)
        {
            if(socketThread.clientId == keyBean.clientID)
            {
                socketThread1 = socketThread;
                break;
            }
        }
        if(socketThread1 == null )
            return;

        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = 0;
        frameBean.cmdIndex = AppConfig.CheckKeyResult;
        frameBean.content = ParseDataHelper.keyBean2Json(keyBean).getBytes();
        socketThread1.addMsg(ParseDataHelper.frame2Btye(frameBean));
    }
}
