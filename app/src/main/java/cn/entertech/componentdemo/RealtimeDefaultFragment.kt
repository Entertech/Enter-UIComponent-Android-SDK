package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.uicomponentsdk.realtime.RealtimeAnimLineChartView
import kotlinx.android.synthetic.main.fragment_default.*
import java.util.*
import kotlin.collections.ArrayList


class RealtimeDefaultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default, container, false)
    }

    var isShowData = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attention_tip1.showDisconnectTip()
        attention_tip2.showLoading()
        btn_pause.setOnClickListener {
            isShowData = !isShowData
        }
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (isShowData) {
                        var leftBrainwave = ArrayList<Double>()
                        var rightBrainwave = ArrayList<Double>()
                        for (i in 0..99) {
                            leftBrainwave.add(Random().nextDouble() * 100)
                            rightBrainwave.add(Random().nextDouble() * 100)
                        }
                        var hrvList = listOf(
                            Random().nextDouble() * 10 + 85.0,
                            Random().nextDouble() * 10 + 85.0
                        )
                        var hrvList2 = listOf(
                            Random().nextDouble() * 10 + 45.0,
                            Random().nextDouble() * 10 + 45.0
                        )
                        var hrvList3 = listOf(
                            Random().nextDouble() * 10 + 45.0,
                            Random().nextDouble() * 10 + 45.0
                        )
                        var hrvList4 = listOf(
                            Random().nextDouble() * 10 + 45.0,
                            Random().nextDouble() * 10 + 45.0
                        )
                        var hrvList5 = listOf(
                            Random().nextDouble() * 10 + 45.0,
                            Random().nextDouble() * 10 + 45.0
                        )
//                    realtime_hrv.showSampleData()
                        realtime_hrv.appendData(0, hrvList)
                        realtime_hrv.appendData(1, hrvList2)
                        realtime_hrv.appendData(2, hrvList3)
                        realtime_hrv.appendData(3, hrvList4)
                        realtime_hrv.appendData(4, hrvList5)
                        brainwave_view.setRightBrainwave(rightBrainwave)
                        brainwave_view.setLeftBrainwave(leftBrainwave)
                    }
                }
            }

        }, 0, 600)

        realtime_hrv.setOnDrawLastValueListener(RealtimeAnimLineChartView.OnDrawLastValueListener { index, value ->
            when(index){
                0->{
                    realtime_brainwave_spectrum.setDeltaWavePercent(value.toFloat())
                }
                1->{
                    realtime_brainwave_spectrum.setAlphaWavePercent(value.toFloat())
                }
                2->{
                    realtime_brainwave_spectrum.setGammaWavePercent(value.toFloat())
                }
                3->{
                    realtime_brainwave_spectrum.setThetaWavePercent(value.toFloat())
                }
                4->{
                    realtime_brainwave_spectrum.setBetaWavePercent(value.toFloat())
                }
            }
        })
    }
}
