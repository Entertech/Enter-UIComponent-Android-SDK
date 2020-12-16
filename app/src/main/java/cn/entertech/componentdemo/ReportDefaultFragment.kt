package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        var spectrumList = listOf<ArrayList<Double>>(
            reportData.reportEEGDataEntity!!.gammaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.betaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.alphaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.thetaCurve!!.map { it * 100.0 } as ArrayList<Double>,
            reportData.reportEEGDataEntity!!.deltaCurve!!.map { it * 100.0 } as ArrayList<Double>
        )
        chart_brainwave.setLegendShowList(listOf(true,true,true,true,true))
        chart_brainwave.setData(
            spectrumList
        )
        chart_hr.setAverage("99")
        chart_hr.setData(reportData.reportHRDataEntity?.hrRec)

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
        chart_hrv.isShowYAxisLabels(false)
        chart_hrv.isShowLegend(false)
        chart_hrv.isShowDetail = false
        chart_hrv.setData(reportData.reportHRDataEntity?.hrRec, hrvSecondLine, false)
        chart_hrv.setOnClickListener {
            Toast.makeText(activity!!, "sfs", Toast.LENGTH_SHORT).show()
        }
        report_pressure.setValue(reportData.reportPressureEnitty?.pressureAvg!!.toInt())

    }
}
