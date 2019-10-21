package cn.entertech.uicomponentsdk.report.file;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by EnterTech on 2017/3/28.
 */

public class FileProtocol<T> {

    private int mProtocolVersion;

    private int mHeadLength;

    private int mFileType;

    private long mDataVersion;

    private long mDataLength;

    private int mCheckSum;

    private long mTick;

    private List<T> mList;

    public FileProtocol(int protocolVersion, int headLength, int fileType, long dataVersion, long dataLength, int checkSum, long tick) {
        mList = new LinkedList<>();
        mProtocolVersion = protocolVersion;
        mHeadLength = headLength;
        mFileType = fileType;
        mDataVersion = dataVersion;
        mDataLength = dataLength;
        mCheckSum = checkSum;
        mTick = tick;
    }

    public int getProtocolVersion() {
        return mProtocolVersion;
    }

    public int getHeadLength() {
        return mHeadLength;
    }

    public int getFileType() {
        return mFileType;
    }

    public long getDataVersion() {
        return mDataVersion;
    }

    public long getDataLength() {
        return mDataLength;
    }

    public int getCheckSum() {
        return mCheckSum;
    }

    public long getTick() {
        return mTick;
    }

    public List<T> getList() {
        return mList;
    }

    public void add(T record) {
        mList.add(record);
    }

    @Override
    public String toString() {
        return "FileProtocol{" +
                "mProtocolVersion=" + mProtocolVersion +
                ", mHeadLength=" + mHeadLength +
                ", mFileType=" + mFileType +
                ", mDataVersion=" + mDataVersion +
                ", mDataLength=" + mDataLength +
                ", mCheckSum=" + mCheckSum +
                ", mTick=" + mTick +
                ", mList=" + mList +
                '}';
    }
}
