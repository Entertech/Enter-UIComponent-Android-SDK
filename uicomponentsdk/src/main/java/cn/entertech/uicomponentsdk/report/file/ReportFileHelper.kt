package cn.entertech.uicomponentsdk.report.file

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import cn.entertech.naptime.model.*
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.file.entity.*

class ReportFileHelper(var context: Context) {
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


    companion object {
        var mInstance: ReportFileHelper? = null
        fun getInstance(context: Context): ReportFileHelper {
            if (mInstance == null) {
                synchronized(ReportFileHelper::class.java) {
                    if (mInstance == null) {
                        mInstance = ReportFileHelper(context)
                    }
                }
            }
            return mInstance!!
        }
    }

    //写文件后清除
    var meditationReportFileFragment: FileFragment<MeditationReportDataAnalyzed>? = null

    fun storeReportFile(
        fileName: String,
        reportMeditationDataEntity: ReportData,
        interruptTimeStamps: ArrayList<Interruption>
    ) {
        this.fileName = fileName
        var state = FileFragment.Status.NORMAL

        if (null == meditationReportFileFragment) {
            meditationReportFileFragment = FileFragment(state, FileFragmentContent())
        }

        reportMeditationDataEntity.let {
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
    fun readReportFile(filePath: String?): ReportData {
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

        val fileProtocol = FileProtocol<MeditationReportDataAnalyzed>(
            protocolVersion, headLength,
            fileType, dataVersion, dataLength, checkSum, tick
        )
        fileProtocol.add(FileParser.parseMeditationReport(string))
        var reportData = ReportData()
        if (fileProtocol.list.size > 0) {
            var reportPleasureEnitty = ReportPleasureEnitty()
            var reportAttentionEnitty = ReportAttentionEnitty()
            var reportPressureEnitty = ReportPressureEnitty()
            var reportRelaxationEnitty = ReportRelaxationEnitty()
            var reportHRDataEntity = ReportHRDataEntity()
            var reportEEGDataEntity = ReportEEGDataEntity()
            var result = fileProtocol.list[0]
            reportPleasureEnitty.pleasureAvg = result.pleasureAvg.toDouble()
            reportPleasureEnitty.pleasureRec = result.pleasureRec
            reportAttentionEnitty.attentionAvg = result.attentionAvg.toDouble()
            reportAttentionEnitty.attentionRec = result.attentionRec
            reportPressureEnitty.pressureAvg = result.pressureAvg.toDouble()
            reportPressureEnitty.pressureRec = result.pressureRec
            reportRelaxationEnitty.relaxationAvg = result.relaxationAvg.toDouble()
            reportRelaxationEnitty.relaxationRec = result.relaxationRec
            reportHRDataEntity.hrAvg = result.hrAvg.toDouble()
            reportHRDataEntity.hrMax = result.hrMax.toDouble()
            reportHRDataEntity.hrMax = result.hrMax.toDouble()
            reportHRDataEntity.hrRec = result.hrRec
            reportHRDataEntity.hrvAvg = result.hrvAvg.toDouble()
            reportHRDataEntity.hrvRec = result.hrvRec
            reportEEGDataEntity.alphaCurve = result.alphaCurve
            reportEEGDataEntity.betaCurve = result.betaCurve
            reportEEGDataEntity.thetaCurve = result.thetaCurve
            reportEEGDataEntity.gammaCurve = result.gammaCurve
            reportEEGDataEntity.deltaCurve = result.deltaCurve
            reportData.reportAttentionEnitty = reportAttentionEnitty
            reportData.reportRelaxationEnitty = reportRelaxationEnitty
            reportData.reportEEGDataEntity = reportEEGDataEntity
            reportData.reportHRDataEntity = reportHRDataEntity
            reportData.reportPleasureEnitty = reportPleasureEnitty
            reportData.reportPressureEnitty = reportPressureEnitty
            reportData.startTime = result.startTime
        }
        return reportData
    }


    @Synchronized
    private fun writeMeditationReport() {
        meditationReportFileFragment?.let {
            //            Logger.d(it.content.content.size)
            brainWaveFileUtil.writeFragment(context, fileName!!, it)
        }
        meditationReportFileFragment = null
    }
}