package cn.entertech.componentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_chart_demo.*
import java.util.*
import kotlin.collections.ArrayList

class ChartDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_demo)
//        average_bar.setValues(listOf(100,20,30,40,50,30,90))
//        var timer = Timer()
//        var timerTask:TimerTask = object: TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    realtime_hrv.appendHrv(Random().nextDouble()*50)
//                }
//            }
//
//        }
//        timer.schedule(timerTask,0,400)

//        var data = ArrayList<Double>()
//        for (i in 0..1000){
//            data.add(0.0)
//        }
//        chart_relaxation_and_attention.setRelaxationAverage(0)
//        chart_relaxation_and_attention.setAttentionAverage(0)
//        chart_relaxation_and_attention.setData(data,data)
    }
}
