package cn.entertech.realtimedatasdk.report.file;

import android.content.Context;
import android.os.Environment;
import cn.entertech.realtimedatasdk.utils.HexDump;

import java.io.*;


/**
 * Created by EnterTech on 2017/1/12.
 */

public class FileUtil {

    /**
     * 获取私有文件存储路径，如脑波文件
     *
     * @return 私有文件存储路径
     */
    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取普通文件存储路径，如音乐
     *
     * @return 普通文件存储路径
     */
    private static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }

    /**
     * 获取截图路径
     *
     * @return 截图路径
     */
    public static File getScreenShotDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/Naptime/screenshot/");
    }

    /**
     * 获取缓存文件存储路径，如音乐缓存
     *
     * @return 缓存文件存储路径
     */
    private static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 读取app内部文件
     *
     * @param
     * @return
     * @throws IOException
     */
    public static String readRaw(Context context, int resId) {
        try {
            InputStream is = context.getResources().openRawResource(resId);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String res = HexDump.toHexString(buffer);
            is.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) {
        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            if (!file.exists()) return null;
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            String res = HexDump.toHexString(buffer);
            fis.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写文件
     *
     * @param fileName
     * @param str
     * @throws IOException
     */
    public static void writeFile(String fileName, String str, byte[] header) {
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            if (0 == file.length()) {
                fos.write(header);
            }
            // 修改数据内容长度
            FileHelper.updateDataLength(file, str);
            byte[] bytes = HexDump.hexStringToByteArray(str);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getFolderSize(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        } else {
            for (int i = 0; i < file.listFiles().length; i++) {
                size = size + file.listFiles()[i].length();
            }
        }
        return size;
    }

    public static void deleteEarlyFile(File folder) {
        if (!folder.exists()) {
            return;
        } else {
            File[] files = folder.listFiles();
            if (files.length <= 0) {
                return;
            }
            File earlyFile = files[0];
            for (int i = 0; i < files.length; i++) {
                if (files[i].lastModified() < earlyFile.lastModified()) {
                    earlyFile = files[i];
                }
            }
            earlyFile.delete();
        }
    }

    public interface OnProgressListener {
        void onProgress(int percent);
    }
}
