package innosiloco.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/3/4.
 */
public class FileSockets
{
    private Socket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    private Thread readThread;

    private Thread writeThread;

    private boolean readMark;

    private boolean writeMark;

    public FileSockets(Socket  socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        readMark = true;
        readThread = new Thread(readRun);
        readThread.start();

        writeMark = true;
        writeThread = new Thread(writeRun);
        writeThread.start();
    }

    /****************
     * 停止
     */
    public void stop()
    {
        readMark = false;
        writeMark = false;

        if(readThread != null )
        {
            readThread.interrupt();
        }

        if(writeThread != null )
        {
            writeThread.interrupt();
        }
    }

    private Runnable readRun = new Runnable() {
        @Override
        public void run()
        {

        }
    };


    private Runnable writeRun = new Runnable() {
        @Override
        public void run() {

        }
    };

}
