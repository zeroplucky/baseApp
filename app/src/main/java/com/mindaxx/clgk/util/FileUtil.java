package com.mindaxx.clgk.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 */
public class FileUtil {

    public static boolean isMountSdCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
                !Environment.isExternalStorageRemovable();
    }

    public static File createCacheFile(Context context, String dir, String fileName) {
        File diskCacheDir = createCacheDir(context, dir);
        return new File(diskCacheDir, fileName);
    }

    /**
     * 创建图片文件,在有些手机里，如果图片文件不放在这里将扫描不出来
     */
    public static File createPictureDiskFile(String dir, String fileName) {
        if (isMountSdCard()) {
            File dirFile;
            try {
                dirFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), dir);
                dirFile.mkdirs();
            } catch (Exception e) {
                dirFile = new File(Environment.getExternalStorageDirectory(), dir);
                dirFile.mkdirs();
            }
            return new File(dirFile, fileName);
        }
        return null;
    }

    private static String getCachePath(Context context) {
        return isMountSdCard() && context.getExternalCacheDir() != null
                ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath();
    }

    public static File createCacheFile(Context context, String uniqueName) {
        String cachePath = getCachePath(context);
        return new File(cachePath + File.separator + uniqueName);
    }

    public static File createCacheDir(Context context, String dir) {
        String cachePath = getCachePath(context);
        File dirFile = new File(cachePath + File.separator + dir);
        dirFile.mkdirs();
        return dirFile;
    }

    /**
     * 递归删除文件和文件夹
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }


    public static File getFromAssets(Context context, String src, String key) {
        try {
            InputStream open = context.getResources().getAssets().open(src);
            BufferedInputStream inBuff = new BufferedInputStream(open);
            File targetFile = createCacheFile(context, src);
            //SPUtilFactory.init(context).put(key, targetFile.getAbsolutePath());
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);

            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }

            outBuff.flush();
            inBuff.close();
            outBuff.close();
            output.close();
            open.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
