package innosiloco.demo.utils;

import android.text.TextUtils;

import innosiloco.demo.beans.FileBean;

/**
 * Created by Administrator on 2017/3/5.
 */
public class FileUtils
{
   public static byte fliePath2Type(String path)
   {
       if(TextUtils.isEmpty(path))
           return 0;
       if(path.toLowerCase().endsWith(".mp3"))
       {
           return FileBean.isMp3;
       } else if(path.toLowerCase().endsWith(".jpg"))
       {
           return FileBean.isJPE;
       } else if(path.toLowerCase().endsWith(".png"))
       {
           return FileBean.isPNG;
       }
       return 0;
   }
}
