package cn.entertech.componentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chart_demo.*
import java.util.*
import kotlin.collections.ArrayList

class ChartDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_demo)
        btn.setOnClickListener {
        }

//        line_chart.setData(listOf(77.0,77.0,77.0,77.0,77.0,78.0,77.0),listOf(7.0,10.0,10.0,10.0,10.0,10.0,10.0))
//        line_chart.setOnClickListener {
//            Toast.makeText(this,"sfdsfs",Toast.LENGTH_SHORT).show()
//        }
//        realtime_coherence.setCoherence(50f)
//        average_bar.setValues(listOf(100,20,30,40,50,30,90))
        var timer = Timer()
        var timerTask:TimerTask = object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    hrv.appendHrv(Random().nextDouble()*50)
                }
            }

        }
        timer.schedule(timerTask,0,400)
        hrv.showSampleData()
//        var data = ArrayList<Double>()
//        for (i in 0..1000){
//            data.add(0.0)
//        }
//        chart_relaxation_and_attention.setRelaxationAverage(0)
//        chart_relaxation_and_attention.setAttentionAverage(0)
//        chart_relaxation_and_attention.setData(data,data)
    }
}
