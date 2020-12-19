package cn.entertech.componentdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attention_tip1.showDisconnectTip()
        attention_tip2.showLoading()
        realtime_brainwave_spectrum.setDeltaWavePercent(0.2f)
        realtime_brainwave_spectrum.setAlphaWavePercent(0.5f)
        realtime_brainwave_spectrum.setThetaWavePercent(0.1f)
        realtime_brainwave_spectrum.setGammaWavePercent(0.02f)
        realtime_brainwave_spectrum.setBetaWavePercent(0.18f)
        var timer = Timer()
        var timerTask:TimerTask = object: TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    var leftBrainwave = ArrayList<Double>()
                    var rightBrainwave = ArrayList<Double>()
                    for (i in 0..99) {
                        leftBrainwave.add(Random().nextDouble() * 100)
                        rightBrainwave.add(Random().nextDouble() * 100)
                    }
                    var hrvList = listOf(Random().nextDouble()*10+85.0,Random().nextDouble()*10+85.0)
                    realtime_hrv.appendHrv(hrvList)
                    brainwave_view.setRightBrainwave(rightBrainwave)
                    brainwave_view.setLeftBrainwave(leftBrainwave)
                }
            }

        }
        timer.schedule(timerTask,0,400)
    }
}
