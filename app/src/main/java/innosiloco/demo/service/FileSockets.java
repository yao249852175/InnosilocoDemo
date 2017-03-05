package innosiloco.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import innosiloco.demo.beans.FileBean;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;

/**
 * Created by Administrator on 2017/3/4.
 * 弃用
 */
public class FileSockets
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

    private LinkedBlockingQueue<FileBean> sendMsgs;

    /**********************************
     * <p>标示 读写线程，是否可以使用</p>
     */
    private boolean  writeAndReadAble;

    /***************
     * 当前的通讯是否 正常
     */
    public boolean isAlive;

    public FileSockets(Socket socket)
    {
        this.socket = socket;
        try {
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }

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

        RonLog.LogE("开始断开连接");
    }

    /***********
     * <p>添加发送的消息</p>
     * @param data
     */
    public void addMsg(FileBean data)
    {
        this.sendMsgs.offer(data);
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
            byte[] recData = new byte[102400];
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
     * 解析数据[F0,(Send2ID),01(CODEIDNEx),content,CRC8,END]
     * @param data
     * @param length
     */
    private void parseData(byte[] data ,int length)
    {
        if(ParseDataHelper.checkCRC(data,0,length))
        {
            switch (data[2])
            {
                case AppConfig.ResponseFile:

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
            /*while (writeAndReadAble)
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
            }*/
        }
    };
}
