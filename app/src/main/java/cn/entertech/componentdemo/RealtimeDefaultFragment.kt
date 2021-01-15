package cn.entertech.componentdemo

import android.os.Bundle
import android.util.Log
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
        realtime_brainwave_spectrum.setDeltaWavePercent(80f)
        realtime_brainwave_spectrum.setAlphaWavePercent(90f)
        realtime_brainwave_spectrum.setThetaWavePercent(80f)
        realtime_brainwave_spectrum.setGammaWavePercent(90f)
        realtime_brainwave_spectrum.setBetaWavePercent(100f)
        var timer = Timer()
        timer.schedule( object: TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    var leftBrainwave = ArrayList<Double>()
                    var rightBrainwave = ArrayList<Double>()
                    for (i in 0..99) {
                        leftBrainwave.add(Random().nextDouble() * 100)
                        rightBrainwave.add(Random().nextDouble() * 100)
                    }
                    Log.d("######","anim append data")
                    var hrvList = listOf(Random().nextDouble()*10+85.0,Random().nextDouble()*10+85.0)
                    var hrvList2 = listOf(Random().nextDouble()*10+45.0,Random().nextDouble()*10+45.0)
                    var hrvList3 = listOf(Random().nextDouble()*10+45.0,Random().nextDouble()*10+45.0)
                    realtime_hrv.appendData(0,hrvList)
                    realtime_hrv.appendData(1,hrvList2)
                    realtime_hrv.appendData(2,hrvList3)
                    brainwave_view.setRightBrainwave(rightBrainwave)
                    brainwave_view.setLeftBrainwave(leftBrainwave)
                }
            }

        },0,600)
    }
}
