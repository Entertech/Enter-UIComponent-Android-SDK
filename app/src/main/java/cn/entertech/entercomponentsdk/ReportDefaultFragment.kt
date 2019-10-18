package cn.entertech.entercomponentsdk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_report_default.*

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

//        affective view
        var attentionDatas = ArrayList<Double>()
        for (i in 0 until 500) {
            attentionDatas.add(java.util.Random().nextDouble() * 100)
        }
        report_attention.setData(System.currentTimeMillis() / 1000, attentionDatas)

//        brainwave spectrum view
        var gamma = ArrayList<Double>()
        var beta = ArrayList<Double>()
        var alpha = ArrayList<Double>()
        var theta = ArrayList<Double>()
        var delta = ArrayList<Double>()

        for (i in 0 until 1000) {
            gamma.add(0.2)
            beta.add(0.2)
            alpha.add(0.2)
            theta.add(0.2)
            delta.add(0.2)
        }
        report_brainwave_spectrum.setBrainwaveSpectrums(
            System.currentTimeMillis() / 1000,
            gamma,
            beta,
            alpha,
            theta,
            delta
        )
        var heartRateData = ArrayList<Double>()
        for (i in 0 until 1000) {
            heartRateData.add(java.util.Random().nextDouble() * 120)
        }
        report_heart_rate.setData(System.currentTimeMillis(),heartRateData,100f,79f,90f)
    }
}
