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
        var data = ArrayList<Double>()
        var data1 = ArrayList<Double>()
        for (i in 0..1000) {
            data.add(0.2)
            data1.add(Random().nextDouble() * 50+20)
        }
         var brainwaves = ArrayList<ArrayList<Double>>()
        brainwaves.add(data)
        brainwaves.add(data)
        brainwaves.add(data)
        brainwaves.add(data)
        brainwaves.add(data)
        report_brainwave_spectrum_pie.setData(brainwaves)
        line_chart.setAttentionAverage(77)
        line_chart.setRelaxationAverage(60)
        line_chart1.setAverage(45)
        line_chart1.setData(data1)
        line_chart.setData(data,data1)
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
//        hrv.showSampleData()
//        var data = ArrayList<Double>()
//        for (i in 0..1000){
//            data.add(0.0)
//        }
//        chart_relaxation_and_attention.setRelaxationAverage(0)
//        chart_relaxation_and_attention.setAttentionAverage(0)
//        chart_relaxation_and_attention.setData(data,data)
    }
}
