package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.uicomponentsdk.report.file.ReportFileHelper
import kotlinx.android.synthetic.main.fragment_report_custom.*
import java.util.*
import kotlin.collections.ArrayList


class ReportCustomFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_custom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var reportFileHelper = ReportFileHelper.getInstance(activity!!)
        var reportData = reportFileHelper.readReportFile("sample")
//        affective view

        var testData = ArrayList<Double>()
        for (i in 0..500) {
            testData.add(Random().nextDouble() * 100)
        }
//        report_attention.setData( reportData.reportAttentionEnitty?.attentionRec)
        report_attention.setData(testData, testData)
        Log.d("####", "attention rec size:" + reportData.reportAttentionEnitty?.attentionRec?.size)
//        report_relaxation.setData(reportData.reportRelaxationEnitty?.relaxationRec)

//        brainwave spectrum view
//

        report_brainwave_spectrum.setData(
            listOf(
                reportData.reportEEGDataEntity?.gammaCurve as ArrayList<Double>,
                reportData.reportEEGDataEntity?.betaCurve as ArrayList<Double>,
                reportData.reportEEGDataEntity?.alphaCurve as ArrayList<Double>,
                reportData.reportEEGDataEntity?.thetaCurve as ArrayList<Double>,
                reportData.reportEEGDataEntity?.deltaCurve as ArrayList<Double>
            )
        )

        report_heart_rate.setData(reportData.reportHRDataEntity?.hrRec)
//
//        report_hrv.setData(startTime!!, reportData.reportHRDataEntity?.hrvRec, reportData.reportHRDataEntity?.hrvAvg)
        report_hrv.setData(reportData.reportHRDataEntity?.hrvRec)

        report_pressure.setData(reportData.reportPressureEnitty?.pressureRec)
    }
}
