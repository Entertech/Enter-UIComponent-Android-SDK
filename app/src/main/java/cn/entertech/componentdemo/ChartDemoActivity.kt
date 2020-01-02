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
        var mData1 = ArrayList<Double>()
        var mData2 = ArrayList<Double>()
        var mData3 = ArrayList<Double>()
        var mData4 = ArrayList<Double>()
        var mData5 = ArrayList<Double>()
        for (i in 0..1900) {
            mData1.add(0.2)
            mData2.add(0.2)
            mData3.add(0.2)
            mData4.add(0.2)
            mData5.add(0.2)
        }
        line_chart.setData(mData1)
    }
}
