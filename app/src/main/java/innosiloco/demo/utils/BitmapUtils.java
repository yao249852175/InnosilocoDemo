package innosiloco.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/5.
 */
public class BitmapUtils
{
    public static String compressBitmap(Bitmap bitmap)
    {
        Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap,300,300);
        return saveBitmap(bitmap1);
    }

    /** 保存方法 */
    public static String saveBitmap(Bitmap bitmap) {
        File f = new File(AppConfig.BaseDirectory, System.currentTimeMillis()+".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return f.getPath();

    }
}
