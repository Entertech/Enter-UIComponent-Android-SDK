package cn.entertech.componentdemo

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.entertech.uicomponentsdk.report.*
import cn.entertech.uicomponentsdk.report.file.ReportFileHelper
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
        report_relaxation.setValue(50)
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
        chart_session_brainwave.setData(
            spectrumList
        )
//        控制可展示的脑波曲线
        chart_brainwave_session_stack.setData(spectrumList)
        chart_brainwave_session_stack.setLegendShowList(listOf(true, true, true, true, true))
        chart_brainwave_session_stack.setStartTime("2022-06-13 22:19:13")
        chart_session_brainwave.setLegendShowList(listOf(true, true, true, true, true))
        chart_session_brainwave.setStartTime("2022-06-13 22:19:13")
        chart_brainwave.setLegendShowList(listOf(true, true, true, true, true))
        chart_brainwave.isChartEnable(true)
        chart_session_brainwave.isChartEnable(true)
        chart_hr.setAverage("99")
        chart_hr.setData(reportData.reportHRDataEntity?.hrRec)
        chart_pressure.setData(reportData.reportHRDataEntity?.hrRec)
        var candleStickValues = ArrayList<TrendCommonCandleChart.CandleSourceData>()
        var barValues = ArrayList<TrendCommonBarChart.BarSourceData>()
        var lineValues = ArrayList<TrendPressureChart.LineSourceData>()
        var brainwaveLineValues = ArrayList<TrendBrainwaveChart.BrainwaveLineSourceData>()
        var dates = getDateList()
        var yearDates = getYearDateList()
        for (i in yearDates.indices) {
            var candleSourceData = TrendCommonCandleChart.CandleSourceData()
            val value = Random().nextInt(30) + 60.0f
            if (i == yearDates.size - 2) {
                candleSourceData.average = 58f
                candleSourceData.max = 58f
                candleSourceData.min = 58f
            } else {
                candleSourceData.average = value
                candleSourceData.max = value + 10
                candleSourceData.min = value - 5
            }
            candleSourceData.date = "${yearDates[i]}"
            candleSourceData.xLabel = "${yearDates[i].split("-")[1]}"
            candleStickValues.add(candleSourceData)

            val brainwaveLineSourceData = TrendBrainwaveChart.BrainwaveLineSourceData()
            brainwaveLineSourceData.gamma = 20f
            brainwaveLineSourceData.alpha = 20f
            brainwaveLineSourceData.beta = 20f
            brainwaveLineSourceData.delta = 20f
            brainwaveLineSourceData.theta = 20f
            brainwaveLineSourceData.date = "${yearDates[i]}"
            brainwaveLineSourceData.xLabel = "${yearDates[i].split("-")[1]}"
            brainwaveLineValues.add(brainwaveLineSourceData)
        }
        for (i in dates.indices) {

            val value = Random().nextInt(30) + 60.0f
            var barSourceData = TrendCommonBarChart.BarSourceData()
            barSourceData.value = value
            barSourceData.date = "${dates[i]}"
            barSourceData.xLabel = "${dates[i].split("-")[2]}"
            barValues.add(barSourceData)
            var lineSourceData = TrendPressureChart.LineSourceData()
            if (i in dates.size - 15 until dates.size - 4) {
                lineSourceData.value = 0f
            } else {
                lineSourceData.value = value
            }
            lineSourceData.date = "${dates[i]}"
            lineSourceData.xLabel = "${dates[i].split("-")[2]}"
            lineValues.add(lineSourceData)


        }
        chart_candle_stick.setData(candleStickValues, "year")
        chart_bar.setData(barValues, "month")
        chart_pressure_trend.setData(lineValues, "month")
        chart_brainwave_trend.setData(brainwaveLineValues, "year")
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
        chart_session_common.setDataType(2)
        chart_session_common.setData(
            reportData.reportHRDataEntity?.hrRec,
            reportData.reportHRDataEntity?.hrAvg,
            reportData.reportHRDataEntity?.hrRec?.map { 0.0 },
            600,
            true
        )
        chart_session_common.setStartTime("2022-06-13 22:59:13")
        chart_session_pressure.setData(
            reportData.reportPressureEnitty?.pressureRec,
            reportData.reportPressureEnitty?.pressureAvg,
            true
        )
        chart_session_pressure.setStartTime("2022-06-13 23:59:13")
        chart_hrv.setOnClickListener {
            Toast.makeText(activity!!, "sfs", Toast.LENGTH_SHORT).show()
        }
        report_pressure.setValue(reportData.reportPressureEnitty?.pressureAvg!!.toInt())

        //flow
        val view =
            LayoutInflater.from(activity).inflate(R.layout.card_content_view_flow_curve, null)
        view.findViewById<ReportJournalFlowLineView>(R.id.line_view).setData(testData)
        card_flow.setContentView(view)

        var flags = ArrayList<Int>()
        var coherenceData = ArrayList<Double>()
        for (i in 0..50) {
            if (i in 30..45) {
                flags.add(1)
            } else if (i in 5..20) {
                flags.add(1)
            } else {
                flags.add(0)
            }
            coherenceData.add(Random().nextDouble() * 100)
        }
        report_coherence.setData(coherenceData, flags)

        var alpha = listOf<Float>(70f,70f,70f,70f,70f,70f,70f)
        var beta = listOf<Float>(70f,70f,70f,70f,70f,70f,70f)
        report_last_7_time.setValueLists(listOf(alpha,beta),
            listOf("alpha","beta"))
        var levels  = ArrayList<ReportAverageChartCard.Level>()
        levels.add(ReportAverageChartCard.Level(0.3f,"低"))
        levels.add(ReportAverageChartCard.Level(0.3f,"中"))
        levels.add(ReportAverageChartCard.Level(0.4f,"高"))
        report_last_7_time.setLevels(levels)
        report_last_7_time.showLevelOnly(false)
//        report_last_7_time.setValues(alpha)
    }

    fun getDateList(): ArrayList<String> {
        var dates = ArrayList<String>()
        for (i in 1..31) {
            dates.add("2022-01-${String.format("%02d", i)}")
        }
        for (i in 1..28) {
            dates.add("2022-02-${String.format("%02d", i)}")
        }
        for (i in 1..31) {
            dates.add("2022-03-${String.format("%02d", i)}")
        }
        for (i in 1..30) {
            dates.add("2022-04-${String.format("%02d", i)}")
        }
        for (i in 1..14) {
            dates.add("2022-05-${String.format("%02d", i)}")
        }
        return dates
    }

    fun getYearDateList(): ArrayList<String> {
        var dates = ArrayList<String>()
        dates.add("2021-08")
        dates.add("2021-09")
        dates.add("2021-10")
        dates.add("2021-11")
        dates.add("2021-12")
//        for (j in 1..5){
        for (i in 1..12) {
            dates.add("2022-${String.format("%02d", i)}")
        }
//        }
        return dates
    }
}
