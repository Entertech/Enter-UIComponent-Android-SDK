package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        chart_relaxation_and_attention.setAttentionAverage( reportData.reportAttentionEnitty?.attentionRec!!.average().toInt())
        chart_relaxation_and_attention.setRelaxationAverage( reportData.reportRelaxationEnitty?.relaxationRec!!.average().toInt())
        chart_relaxation_and_attention.setData( reportData.reportAttentionEnitty?.attentionRec, reportData.reportRelaxationEnitty?.relaxationRec)
        chart_relaxation.setAverage(reportData.reportRelaxationEnitty?.relaxationRec!!.average().toInt())
        chart_attention.setAverage(reportData.reportAttentionEnitty?.attentionRec!!.average().toInt())
        chart_relaxation.setData(reportData.reportRelaxationEnitty?.relaxationRec)
        chart_attention.setData(reportData.reportAttentionEnitty?.attentionRec)
        var spectrumList = listOf<List<Double>>(
            reportData.reportEEGDataEntity!!.gammaCurve!!,
            reportData.reportEEGDataEntity!!.betaCurve!!,
            reportData.reportEEGDataEntity!!.alphaCurve!!,
            reportData.reportEEGDataEntity!!.thetaCurve!!,
            reportData.reportEEGDataEntity!!.deltaCurve!!
        )
//        chart_brainwave.setData(
//            spectrumList
//        )
        chart_hr.setAverage(reportData.reportHRDataEntity?.hrAvg!!.toInt())
        chart_hr.setData(reportData.reportHRDataEntity?.hrRec)

        chart_hrv.setAverage(reportData.reportHRDataEntity?.hrvAvg!!.toInt())
        chart_hrv.setData(reportData.reportHRDataEntity?.hrvRec)

        report_pressure.setValue(reportData.reportPressureEnitty?.pressureAvg!!.toInt())
    }
}
