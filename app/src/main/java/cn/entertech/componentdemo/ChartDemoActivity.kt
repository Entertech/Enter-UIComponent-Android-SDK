package cn.entertech.componentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_chart_demo.*

class ChartDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_demo)
        average_card.setValues(listOf(56,64,25,46,46,45,120))
    }
}
