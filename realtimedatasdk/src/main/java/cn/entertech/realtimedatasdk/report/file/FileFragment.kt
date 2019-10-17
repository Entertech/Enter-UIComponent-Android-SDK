package cn.entertech.realtimedatasdk.report.file

import android.util.Log
import cn.entertech.naptime.model.FileFragmentContent
import cn.entertech.realtimedatasdk.utils.HexDump
import java.util.*

/**
 * Created by EnterTech on 2017/12/19.
 */
data class FileFragment<T: BrainDataUnit>(val status: Status, val content: FileFragmentContent<T>) {
    var tick: Long = Calendar.getInstance().timeInMillis / 1000

    enum class Status(val value: Byte){
        NORMAL(0), TIMEOUT(1), DISCONNECTED(2)
    }

    //获取要写文件的数据
    fun getWriteData(): ByteArray {
        val bytes = mutableListOf<Byte>()

        //时间戳
        bytes.addAll(HexDump.hexStringToByteArray(String.format("%08x", tick)).asList())

//        //校验位
//        for (i in 0..1) {
//            bytes.add(0)
//        }

        //状态
        bytes.add(status.value)

        //保留字段
        for (i in 0..4) {
            bytes.add(0)
        }

        //内容
        val contentBytes = content.toFileBytes()
        //校验位
        bytes.addAll(4,HexDump.hexSringToBytes(HexDump.getValidateVode(contentBytes.toByteArray())).asList())
        //数据
        bytes.addAll(contentBytes)
        //数据长度
        bytes.addAll(0, HexDump.hexStringToByteArray(String.format("%08x", contentBytes.size)).asList())
        return bytes.toByteArray()
    }


}