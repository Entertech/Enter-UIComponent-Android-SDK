package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.TrendCommonBarChart
import kotlinx.android.synthetic.main.activity_bar_chart_full_screen.*

class BarChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var lineData = intent.getSerializableExtra("lineData") as ArrayList<TrendCommonBarChart.BarSourceData>?
        var lineWidth = intent.getFloatExtra("lineWidth",1.5f)
        var highlightLineColor = intent.getIntExtra("highlightLineColor",Color.parseColor("#11152E"))
        var highlightLineWidth = intent.getFloatExtra("highlightLineWidth",1.5f)
        var markViewBgColor = intent.getIntExtra("markViewBgColor",Color.parseColor("#F1F5F6"))
        var markViewTitle = intent.getStringExtra("markViewTitle")
        var markViewTitleColor = intent.getIntExtra("markViewTitleColor",Color.parseColor("#8F11152E"))
        var markViewValueColor = intent.getIntExtra("markViewValueColor",Color.parseColor("#8F11152E"))
        var gridLineColor = intent.getIntExtra("gridLineColor",Color.parseColor("#E9EBF1"))
        var xAxisUnit = intent.getStringExtra("xAxisUnit")
        var textColor = intent.getIntExtra("textColor",Color.parseColor("#333333"))
        var bgColor = intent.getIntExtra("bgColor",Color.WHITE)
        var averageLineColor = intent.getIntExtra("averageLineColor",Color.parseColor("#11152E"))
        var labelColor = intent.getIntExtra("labelColor",Color.parseColor("#9AA1A9"))
        var average = intent.getStringExtra("average")
        var averageBgColor = intent.getIntExtra("averageBgColor",0)
        var mainColor = intent.getIntExtra("mainColor",Color.RED)
        var cycle = intent.getStringExtra("cycle")?:""
        var unit = intent.getStringExtra("unit")?:""
        var showLevel = intent.getBooleanExtra("showLevel",false)
        var levelBgColor = intent.getIntExtra("levelBgColor",Color.RED)
        var levelTextColor = intent.getIntExtra("levelTextColor",Color.RED)
        var xAxisLineColor = intent.getIntExtra("xAxisLineColor",Color.RED)
        var isDataTime = intent.getBooleanExtra("isDataTime",false)
        var curYear = intent.getStringExtra("curYear")
        var curMonth = intent.getStringExtra("curMonth")
        chart_bar.setLineWidth(lineWidth)
        chart_bar.curYear = curYear
        chart_bar.curMonth = curMonth
        chart_bar.isFullScreen = true
        chart_bar.mHighlightLineColor = highlightLineColor
        chart_bar.mHighlightLineWidth = highlightLineWidth
        chart_bar.mMarkViewBgColor = markViewBgColor
        chart_bar.mMarkViewTitleColor = markViewTitleColor
        chart_bar.mMarkViewValueColor = markViewValueColor
        chart_bar.mMarkViewTitle = markViewTitle
        chart_bar.mAverageLabelBgColor = averageBgColor
        chart_bar.setGridLineColor(gridLineColor)
        chart_bar.setXAxisUnit(xAxisUnit)
        chart_bar.setTextColor(textColor)
        chart_bar.bgColor = bgColor
        chart_bar.setAverageLineColor(averageLineColor)
        chart_bar.setLabelColor(labelColor)
        chart_bar.setAverage(average?:"")
        chart_bar.setMainColor(mainColor)
        chart_bar.setUnit(unit)
        chart_bar.setShowLevel(showLevel)
        chart_bar.setLevelBgColor(levelBgColor)
        chart_bar.setLevelTextColor(levelTextColor)
        chart_bar.setXAxisLineColor(xAxisLineColor)
        chart_bar.mIsDataTime = isDataTime
        chart_bar.setData(lineData,cycle)
    }
}
