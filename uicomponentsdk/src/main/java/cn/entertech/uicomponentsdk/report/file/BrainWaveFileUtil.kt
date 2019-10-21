package cn.entertech.uicomponentsdk.report.file

import android.content.Context
import cn.entertech.naptime.file.DeviceHelper
import cn.entertech.naptime.file.DeviceHelperV3
import cn.entertech.uicomponentsdk.utils.HexDump
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 * Created by EnterTech on 2017/11/13.
 */
class BrainWaveFileUtil ()  {

    constructor(type: Type) : this() {
        brainFileHelper = if (type == Type.V2) {
            DeviceHelperV2()
        } else {
            DeviceHelperV3()
        }
    }

    private val VERSION = "0200"
    private val HEADER_LEN = "20"
    private val DATA_VERSION = "0.0.0.1"

    enum class FileType(val value: String) {
        RAW("01"),
        ANALYZED("02"),
        NAP_REPORT("03"),
        SLEEP_REPORT("05"),
        MEDITATION_REPORT("06")
    }

    enum class Type{V2, V3}
    private lateinit var brainFileHelper: DeviceHelper

    private fun writeFile(fileName: String, byteArray: ByteArray, header: ByteArray) {
        val file = File(fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file, true)
            if (0L == file.length()) {
                fos.write(header)
            }
            // 修改数据内容长度
            updateDataLength(file, byteArray)
            fos.write(byteArray)
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun writeReportFile(context: Context, fileName: String, byteArray: ByteArray, sleepType: String){
        val meditationReport = File(getReportDir(context))
        if (!meditationReport.exists()) {
            meditationReport.mkdirs()
        }
        writeFile(getReportDir(context) + "/" + fileName + getReportExtention(), byteArray,
                HexDump.hexStringToByteArray(getReportHeader(byteArray,sleepType)))
    }
    /**
     * 写文件(新文件协议);
     */
    fun <T: BrainDataUnit> writeFragment(context: Context,fileName: String, fragment: FileFragment<T>) {
        if (fragment.content.content.isEmpty())
            return

        val brainUnit = fragment.content.content.get(0)
        when {
            brainUnit is MeditationReportDataAnalyzed -> {
                writeReportFile(context,fileName, fragment.getWriteData(),"meditation")
            }
            else -> {}
        }
    }

    fun getReportDir(context:Context): String {
        return FileUtil.getFilesDir(context).toString() + "/" + "report"
    }

    fun getReportExtention(): String {
        return ".report"
    }


    fun getReportHeader(data: ByteArray? = null,sleepType:String): String {
        if ("sleep".equals(sleepType)){
            return getHeaderByType(FileType.SLEEP_REPORT,data)
        }else{
            return getHeaderByType(FileType.MEDITATION_REPORT,data)
        }
    }

    private fun getHeaderByType(type: FileType,data: ByteArray?=null): String {
        val calendar = Calendar.getInstance()
        val utcTick = calendar.timeInMillis
        return VERSION + HEADER_LEN + type.value + getDataVersion() + "000000000000"+
                HexDump.getValidateVode(data) + java.lang.Long.toHexString(utcTick / 1000) + "000000000000000000000000"
    }

    private fun getDataVersion():String{
        val codes = DATA_VERSION.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val version = StringBuilder()
        for (code in codes) {
            version.append(String.format("%02x", Integer.valueOf(code)))
        }
        return version.toString()
    }

    fun updateDataLength(file: File, byteArray: ByteArray) {
        try {
            val rf = RandomAccessFile(file, "rw")
            rf.seek(8)
            val cur = ByteArray(6)
            rf.read(cur, 0, 6)
            //            Logger.d("read long = "+HexDump.toHexString(cur));
            var curLen = java.lang.Long.valueOf(HexDump.toHexString(cur), 16)
            curLen += byteArray.size.toLong()

            //            Logger.d("read curLen = "+curLen);
            rf.seek(8)
            rf.write(HexDump.hexStringToByteArray(String.format("%012x", curLen)))
            rf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}