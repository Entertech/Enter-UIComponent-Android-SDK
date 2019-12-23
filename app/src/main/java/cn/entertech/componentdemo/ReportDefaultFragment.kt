package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.uicomponentsdk.report.file.ReportFileHelper
import kotlinx.android.synthetic.main.fragment_report_custom.*
import kotlinx.android.synthetic.main.fragment_report_default.*
import kotlinx.android.synthetic.main.fragment_report_default.report_attention
import kotlinx.android.synthetic.main.fragment_report_default.report_brainwave_spectrum
import kotlinx.android.synthetic.main.fragment_report_default.report_heart_rate
import kotlinx.android.synthetic.main.fragment_report_default.report_hrv
import kotlinx.android.synthetic.main.fragment_report_default.report_pressure
import kotlinx.android.synthetic.main.fragment_report_default.report_relaxation
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
        var startTime = reportData.startTime
//        affective view

//        report_attention.setData(reportData.reportAttentionEnitty?.attentionRec)


        var testData = ArrayList<Double>()
        for (i in 0 .. 500){
            testData.add(Random().nextDouble()*100)
        }
//        report_attention.setData( reportData.reportAttentionEnitty?.attentionRec)
        report_attention.setData(testData)
        Log.d("####","attention rec size:"+reportData.reportAttentionEnitty?.attentionRec?.size)
//        report_relaxation.setData(reportData.reportRelaxationEnitty?.relaxationRec)

//        brainwave spectrum view

//        report_brainwave_spectrum.setBrainwaveSpectrums(
//            startTime!!,
//            reportData.reportEEGDataEntity?.gammaCurve,
//            reportData.reportEEGDataEntity?.betaCurve,
//            reportData.reportEEGDataEntity?.alphaCurve,
//            reportData.reportEEGDataEntity?.thetaCurve,
//            reportData.reportEEGDataEntity?.deltaCurve
//        )

        report_heart_rate.setData(reportData.reportHRDataEntity?.hrRec)

//        report_hrv.setData(startTime!!, reportData.reportHRDataEntity?.hrvRec, reportData.reportHRDataEntity?.hrvAvg)
        report_hrv.setData(reportData.reportHRDataEntity?.hrvRec)

        report_pressure.setData(startTime!!, reportData.reportPressureEnitty?.pressureRec)
        Log.d("###","attention size:"+reportData.reportAttentionEnitty?.attentionRec!!.size+"::heart size is "+reportData.reportHRDataEntity?.hrRec!!.size)
    }
}
