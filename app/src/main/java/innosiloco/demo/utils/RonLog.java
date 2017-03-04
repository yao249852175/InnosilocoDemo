package innosiloco.demo.utils;

import android.util.Log;

/**
 * Created by ron on 2016/5/23.
 */
public class RonLog
{

    public static String RON = "<ron>";

    public static void LogE(String...args)
    {
        if(args.length >= 2)
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < args.length ; i ++)
            {
                sb.append(args[i]);
                sb.append(";");
            }
            Log.e(args[0],sb.toString());
        }else if( args.length == 1)
        {
            Log.e(RON,args[0]);
        }
    }


    public static void LogD(String...args)
    {
        if(args.length >= 2)
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < args.length ; i ++)
            {
                sb.append(args[i]);
                sb.append(";");
            }
            Log.d(args[0],sb.toString());
        }else if( args.length == 1)
        {
            Log.d(RON,args[0]);
        }
    }
}
