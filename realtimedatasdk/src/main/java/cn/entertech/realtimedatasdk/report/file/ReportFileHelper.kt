package cn.entertech.realtimedatasdk.report.file

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import cn.entertech.naptime.model.*
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.report.file.entity.Interruption
import cn.entertech.realtimedatasdk.report.file.entity.ReportData

class ReportFileHelper(var context:Context) {
    private val handlerThread: HandlerThread
    private val handler: Handler
    private val brainWaveFileUtil: BrainWaveFileUtil
    var fileName: String? = null


    init {
        brainWaveFileUtil = BrainWaveFileUtil()
        handlerThread = HandlerThread("file_fragment_buffer")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }



    //写文件后清除
    var meditationReportFileFragment: FileFragment<MeditationReportDataAnalyzed>? = null

    fun storeReportFile(fileName:String,
        reportMeditationDataEntity: ReportData,
        interruptTimeStamps: ArrayList<Interruption>
    ) {
        this.fileName = fileName
        var state = FileFragment.Status.NORMAL

        if (null == meditationReportFileFragment) {
            meditationReportFileFragment = FileFragment(state, FileFragmentContent())
        }

        reportMeditationDataEntity?.let {
            meditationReportFileFragment?.content?.append(
                MeditationReportDataAnalyzed(
                    reportMeditationDataEntity,
                    interruptTimeStamps
                )
            )
        }

        handler.post {
            writeMeditationReport()
        }
    }

    /**
     * 解析分析后文件
     */
    //todo 临时只简单的连接各个片段，暂时不考虑中断等情况
    fun readReportFile(context: Context, filePath: String): FileProtocol<BrainDataUnit> {
        var string = FileUtil.readFile(filePath)

        if (null == string || string == "") {
            string = FileUtil.readRaw(context, R.raw.sample)
        }

        val protocolVersion = Integer.valueOf(StringUtil.substring(string, 0, 4), 16)
        val headLength = Integer.valueOf(StringUtil.substring(string, 4, 6), 16)
        val fileType = Integer.valueOf(StringUtil.substring(string, 6, 8), 16)
        val dataVersion = java.lang.Long.valueOf(StringUtil.substring(string, 8, 16), 16)
        val dataLength = java.lang.Long.valueOf(StringUtil.substring(string, 16, 28), 16)
        val checkSum = Integer.valueOf(StringUtil.substring(string, 28, 32), 16)
        val tick = java.lang.Long.valueOf(StringUtil.substring(string, 32, 40), 16)

        val fileProtocol = FileProtocol<BrainDataUnit>(
            protocolVersion, headLength,
            fileType, dataVersion, dataLength, checkSum, tick
        )
        fileProtocol.add(FileParser.parseMeditationReport(string))
        return fileProtocol
    }


    @Synchronized
    private fun writeMeditationReport() {
        meditationReportFileFragment?.let {
            //            Logger.d(it.content.content.size)
            brainWaveFileUtil.writeFragment(context,fileName!!, it)
        }
        meditationReportFileFragment = null
    }
}