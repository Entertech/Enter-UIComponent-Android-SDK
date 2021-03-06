package cn.entertech.uicomponentsdk.report.file;

import cn.entertech.uicomponentsdk.utils.HexDump;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

/**
 * Created by EnterTech on 2017/3/20.
 * File Helper
 * 硬件读写文件帮助类
 */

public class FileHelper {
    private final static String VERSION = "0100";
    private final static String HEADER_LEN = "20";
    private final static String TYPE_SOUCE = "01";
    private final static String TYPE_ANALYZED = "02";
    private final static String DATA_VERSION = "03010000";


    public static String getRawHeader() {
        return getHeaderByType(TYPE_SOUCE);
    }


    public static String getAnalyzedHeader() {
        return getHeaderByType(TYPE_ANALYZED);
    }


    private static String getHeaderByType(String type) {
        Calendar calendar = Calendar.getInstance();
        long utcTick = calendar.getTimeInMillis();
        // TODO: 2017/3/21 校验和
        return VERSION + HEADER_LEN + type + DATA_VERSION + "0000000000000000"
                + Long.toHexString(utcTick / 1000) + "000000000000000000000000";
    }

    /**
     * update date length
     * 更新文件数据长度
     *
     * @param file
     * @param str
     */
    public static void updateDataLength(File file, String str) {
        try {
            RandomAccessFile rf = new RandomAccessFile(file, "rw");
            rf.seek(8);
            byte[] cur = new byte[6];
            rf.read(cur, 0, 6);
//            Logger.d("read long = "+HexDump.toHexString(cur));
            Long curLen = Long.valueOf(HexDump.toHexString(cur), 16);
            curLen += (str.length() / 2);
//            Logger.d("read curLen = "+curLen);
            rf.seek(8);
            rf.write(HexDump.hexStringToByteArray(String.format("%012x", curLen)));
            rf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
