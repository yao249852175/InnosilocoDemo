package innosiloco.demo.service;

import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.FrameBean;
import innosiloco.demo.beans.ICallback;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.BitmapUtils;
import innosiloco.demo.utils.FileUtils;

/**
 * Created by ron on 2017/2/26.
 */
public class MyClientSocket implements Runnable,MySocket
{
    private Socket socket;
    private SocketThread socketThread;

    private String serverIP;

    public MyClientSocket(String ip)
    {
        this.serverIP = ip;
    }

    private ICallback iCallback;
    @Override
    public void run()
    {
        try {
            socket = new Socket(serverIP, AppConfig.PORT);
            socketThread = new SocketThread(socket,(byte)-1,null);
            socketThread.begin();
            if(iCallback != null )
            {
                iCallback.callBack(0,"");
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(iCallback != null )
            {
                iCallback.callBack(1,"");
            }
        }
    }

    @Override
    public void start(ICallback iCallback)
    {
        this.iCallback = iCallback;
        new Thread(this).start();
        AppConfig.isServce = false;
    }

    @Override
    public void stop()
    {
        if(socketThread != null )
             socketThread.stop();
    }

    /*************
     * <p>上传重新设置的用户信息</p>
     * @param bean
     */
    public void updateUser(UserBean bean)
    {
        if(socketThread == null )
        {
            Toast.makeText(MyApp.getSingleApp(),"连接服务器失败",Toast.LENGTH_SHORT).show();
            return;
        }
        FrameBean frameBean = new FrameBean();
        frameBean.send2ID = 0;
        frameBean.cmdIndex = AppConfig.UserInfoCode;
        frameBean.content = ParseDataHelper.friendBean2Json(bean).getBytes();
        socketThread.addMsg(ParseDataHelper.frame2Btye(frameBean));
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
        frameBean.content = ParseDataHelper.talkBean2Json(talkBean).getBytes();
        socketThread.addMsg(ParseDataHelper.frame2Btye(frameBean));
    }

    @Override
    public void sendFileTalk(TalkBean talkBean) {
        byte type = FileUtils.fliePath2Type(talkBean.talkContent);
        if(type == FileBean.isMp3 )
        {
            File file = new File(talkBean.talkContent);
            if(file.isFile() && file.length() > AppConfig.maxSendFIleLength)
            {
                Toast.makeText(MyApp.getSingleApp(),MyApp.getSingleApp().getString(R.string.uploadFileIsBig),Toast.LENGTH_SHORT).show();
            }else
            {
                socketThread.addFileMsg(talkBean.talkContent,talkBean.sendID,talkBean.toID);
            }
        }else
        if(type == FileBean.isJPE|| type == FileBean.isPNG )
        {
            File file = new File(talkBean.talkContent);
            if(file.isFile() && file.length() > AppConfig.maxSendFIleLength)
            {
                talkBean.talkContent = BitmapUtils.compressBitmap(BitmapFactory.decodeFile(file.getPath()));
            }

            socketThread.addFileMsg(talkBean.talkContent,talkBean.sendID,talkBean.toID);
        }
    }
}
