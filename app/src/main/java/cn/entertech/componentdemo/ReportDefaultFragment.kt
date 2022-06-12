package cn.entertech.componentdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.entertech.uicomponentsdk.report.ReportBarChartCard
import cn.entertech.uicomponentsdk.report.ReportCandleStickChartCard
import cn.entertech.uicomponentsdk.report.StackedAreaChart
import cn.entertech.uicomponentsdk.report.file.ReportFileHelper
import cn.entertech.uicomponentsdk.utils.dp
import kotlinx.android.synthetic.main.fragment_report_default.*
import kotlinx.android.synthetic.main.fragment_report_default.report_pressure
import java.util.*
import kotlin.collections.ArrayList

class ReportDefaultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_default, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var reportFileHelper = ReportFileHelper.getInstance(activity!!)
        var reportData = reportFileHelper.readReportFile("sample")

        var testData = ArrayList<Double>()
        for (i in 0..500) {
            testData.add(Random().nextDouble() * 100)
        }
        chart_relaxation_and_attention.setAttentionAverage(
            reportData.reportAttentionEnitty?.attentionRec!!.average().toInt()
        )
        chart_relaxation_and_attention.setRelaxationAverage(
            reportData.reportRelaxationEnitty?.relaxationRec!!.average().toInt()
        )
        chart_relaxation_and_attention.setData(
            reportData.reportAttentionEnitty?.attentionRec,
            reportData.reportRelaxationEnitty?.relaxationRec
        )
        chart_relaxation.setAverage(
            "${reportData.reportRelaxationEnitty?.relaxationRec!!.average().toInt()}"
        )
        chart_attention.setAverage(
            "${reportData.reportAttentionEnitty?.attentionRec!!.average().toInt()}"
        )
        chart_relaxation.setData(reportData.reportRelaxationEnitty?.relaxationRec)
        chart_attention.setData(reportData.reportAttentionEnitty?.attentionRec)

//        脑波各个频率曲线
        var spectrumList = listOf(
            reportData.reportEEGDataEntity!!.gammaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.betaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.alphaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.thetaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.deltaCurve!!.map { it * 100.0 } as ArrayList<Double>
        )
        var stackItemAlpha = StackedAreaChart.StackItem().apply {
            stackColor = Color.parseColor("#ff0000")
            stackData = reportData.reportEEGDataEntity!!.alphaCurve
        }
        var stackItemBeta = StackedAreaChart.StackItem().apply {
            stackColor = Color.parseColor("#00ff00")
            stackData = reportData.reportEEGDataEntity!!.betaCurve
        }
        var stackItemTheta = StackedAreaChart.StackItem().apply {
            stackColor = Color.parseColor("#0000ff")
            stackData = reportData.reportEEGDataEntity!!.thetaCurve
        }
        var stackItemDelta = StackedAreaChart.StackItem().apply {
            stackColor = Color.parseColor("#ffff00")
            stackData = reportData.reportEEGDataEntity!!.deltaCurve
        }
        var stackItemGamma = StackedAreaChart.StackItem().apply {
            stackColor = Color.parseColor("#00ffff")
            stackData = reportData.reportEEGDataEntity!!.gammaCurve
        }
        var stackItemList = ArrayList<StackedAreaChart.StackItem>()
        stackItemList.add(stackItemAlpha)
        stackItemList.add(stackItemBeta)
        stackItemList.add(stackItemTheta)
        stackItemList.add(stackItemDelta)
        stackItemList.add(stackItemGamma)
        stack_chart.setStackItems(stackItemList)
//        设置脑波曲线
        chart_brainwave.setData(
            spectrumList
        )
//        控制可展示的脑波曲线
        chart_brainwave.setLegendShowList(listOf(true, true, true, true, true))
        chart_brainwave.isChartEnable(true)
        chart_hr.setAverage("99")
        chart_hr.setData(reportData.reportHRDataEntity?.hrRec)
        chart_pressure.setData(reportData.reportHRDataEntity?.hrRec)
        var candleStickValues = ArrayList<ReportCandleStickChartCard.CandleSourceData>()
        var barValues = ArrayList<ReportBarChartCard.BarSourceData>()
        var dates = getDateList()
        for (i in dates.indices) {
            var candleSourceData = ReportCandleStickChartCard.CandleSourceData()
            val value = Random().nextInt(30) + 60.0f
            candleSourceData.average = value
            candleSourceData.max = value + 5
            candleSourceData.min = value - 5
            candleSourceData.date = "${dates[i]}"
            candleSourceData.xLabel = "${dates[i].split("-")[2]}"
            candleStickValues.add(candleSourceData)
            var barSourceData = ReportBarChartCard.BarSourceData()
            barSourceData.value = value
            barSourceData.date =  "${dates[i]}"
            barSourceData.xLabel = "${dates[i].split("-")[2]}"
            barValues.add(barSourceData)
        }
        chart_candle_stick.setData(candleStickValues, "month")
        chart_bar.setData(barValues,"month")
//        chart_hrv.setAverage("${reportData.reportHRDataEntity?.hrvAvg!!.toInt()}")
        var hrvSecondLine = ArrayList<Double>()
        var hrvSecondLineSize = reportData.reportHRDataEntity!!.hrRec!!.size / 9f.toInt()
        for (i in 0 until hrvSecondLineSize) {
            if (i >= hrvSecondLineSize / 4 && i < hrvSecondLineSize / 2) {
                hrvSecondLine.add(1.0)
            } else {
                hrvSecondLine.add(0.0)
            }
        }
        chart_hrv.setCohTime("2min 2s")
        chart_hrv.isChartEnable(true)
        chart_hrv.isShowYAxisLabels(true)
        chart_hrv.isShowLegend(true)
        chart_hrv.isShowDetail = false
        chart_hrv.setData(reportData.reportHRDataEntity?.hrRec, hrvSecondLine, false)
        chart_session_common.setData(reportData.reportHRDataEntity?.hrRec, null, null,true)
        chart_session_common.setStartTime("2022-06-13 23:59:13")
        chart_hrv.setOnClickListener {
            Toast.makeText(activity!!, "sfs", Toast.LENGTH_SHORT).show()
        }
        report_pressure.setValue(reportData.reportPressureEnitty?.pressureAvg!!.toInt())

    }

    fun getDateList(): ArrayList<String> {
        var dates = ArrayList<String>()
        for (i in 1..31){
            dates.add("2022-01-${String.format("%02d", i)}")
        }
        for (i in 1..28){
            dates.add("2022-02-${String.format("%02d", i)}")
        }
        for (i in 1..31){
            dates.add("2022-03-${String.format("%02d", i)}")
        }
        for (i in 1..30){
            dates.add("2022-04-${String.format("%02d", i)}")
        }
        for (i in 1..31){
            dates.add("2022-05-${String.format("%02d", i)}")
        }
        return dates
    }
}
