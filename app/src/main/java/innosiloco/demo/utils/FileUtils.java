package innosiloco.demo.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;

import innosiloco.demo.MyApp;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.FileInfo;

/**
 * Created by Administrator on 2017/3/5.
 */
public class FileUtils
{
    private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";
    private static String[] SysFileDirs = new String[] { "miren_browser/imagecaches" };
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
        }else if(path.toLowerCase().endsWith(".aac"))
        {
            return FileBean.isAAC;
        }
        return 0;
    }
    /**
     * 检测Sdcard是否存在
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    public static boolean isNormalFile(String fullName) {
        return !fullName.equals(ANDROID_SECURE);
    }
    public static boolean shouldShowFile(String path) {
        return shouldShowFile(new File(path));
    }
    /**
     * 过滤文件类型，判断是否应该显示
     */
    public static boolean shouldShowFile(File file) {
        if (file.isHidden())
            return false;

        if (file.getName().startsWith("."))
            return false;

        String sdFolder = getSdDirectory();
        for (String s : SysFileDirs) {
            if (file.getPath().startsWith(makePath(sdFolder, s)))
                return false;
        }
        return true;
    }
    public static String getSdDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }
    public static String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator))
            return path1 + path2;

        return path1 + File.separator + path2;
    }
    /**
     * 获取文件信息
     */
    public static FileInfo GetFileInfo(File f, FilenameFilter filter, boolean showHidden) {
        FileInfo lFileInfo = new FileInfo();
        String filePath = f.getPath();
        File lFile = new File(filePath);
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = lFile.isHidden();
        lFileInfo.fileName = f.getName();
        lFileInfo.ModifiedDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        if (lFileInfo.IsDir) {
            int lCount = 0;
            File[] files = lFile.listFiles(filter);
            if (files == null) {
                return null;
            }

            for (File child : files) {
                if ((!child.isHidden() || showHidden) && isNormalFile(child.getAbsolutePath())) {
                    lCount++;
                }
            }
            lFileInfo.Count = lCount;

        } else {

            lFileInfo.fileSize = lFile.length();

        }
        return lFileInfo;
    }
    // 计算文件的大小
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String getVoicePath() {
        final String voiceDir = getExternalCacheDir(MyApp.getSingleApp()) + "/voice/";
        File dir = new File(voiceDir);
        if (!dir.exists())
            dir.mkdirs();
        return voiceDir;
    }
    /**
     * 获取存储路径
     */
    public static String getExternalCacheDir(Context context) {
        if (hasExternalCacheDir() && context.getExternalCacheDir() != null) {
            // 获取 SDCard/Android/data/你的应用包名/cache/目录
            return context.getExternalCacheDir().getAbsolutePath();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache";
        return cacheDir;
    }
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}
