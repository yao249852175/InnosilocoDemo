package innosiloco.demo;

import android.app.Application;
import android.os.Process;

import innosiloco.demo.service.MySocket;

/**
 * Created by Administrator on 2017/2/26.
 */
public class MyApp extends Application
{
    private static MyApp myapp;
    public static MyApp getSingleApp()
    {
        return myapp;
    }

    public MySocket mySocket;

    @Override
    public void onCreate() {
        super.onCreate();
        this.myapp = this;
    }

    public void exitApp()
    {
        Process.killProcess(Process.myPid());  //获取PID
        System.exit(0);
    }
}
