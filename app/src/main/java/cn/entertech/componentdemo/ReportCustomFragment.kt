package cn.entertech.componentdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.uicomponentsdk.report.file.ReportFileHelper
import kotlinx.android.synthetic.main.fragment_report_custom.*


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
        var startTime = reportData.startTime
//        affective view

        report_attention.setData(startTime!!, reportData.reportAttentionEnitty?.attentionRec)

        report_relaxation.setData(startTime!!, reportData.reportRelaxationEnitty?.relaxationRec)

//        brainwave spectrum view

        report_brainwave_spectrum.setBrainwaveSpectrums(
            startTime!!,
            reportData.reportEEGDataEntity?.gammaCurve,
            reportData.reportEEGDataEntity?.betaCurve,
            reportData.reportEEGDataEntity?.alphaCurve,
            reportData.reportEEGDataEntity?.thetaCurve,
            reportData.reportEEGDataEntity?.deltaCurve
        )

        report_heart_rate.setData(startTime!!, reportData.reportHRDataEntity?.hrRec, reportData.reportHRDataEntity?.hrMax, reportData.reportHRDataEntity?.hrMin, reportData.reportHRDataEntity?.hrAvg)

        report_hrv.setData(startTime!!, reportData.reportHRDataEntity?.hrvRec, reportData.reportHRDataEntity?.hrvAvg)

        report_pressure.setData(startTime!!, reportData.reportPressureEnitty?.pressureRec)
    }
}
