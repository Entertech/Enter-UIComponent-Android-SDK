package cn.entertech.componentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_chart_demo.*
import java.util.*
import kotlin.collections.ArrayList

class ChartDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_demo)
        var mData = ArrayList<Double>()
        for (i in 0..999) {
            mData.add(Random().nextDouble() * 100)
        }
        report_affective_chart.setData(mData)
    }
}
