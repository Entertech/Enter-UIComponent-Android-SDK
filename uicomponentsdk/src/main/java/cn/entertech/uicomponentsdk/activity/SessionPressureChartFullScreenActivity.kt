package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.activity_session_pressure_chart_full_screen.*

class SessionPressureChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_pressure_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var lineData = intent.getDoubleArrayExtra("lineData")
        var lineWidth = intent.getFloatExtra("lineWidth",1.5f)
        var pointCount = intent.getIntExtra("pointCount",100)
        var timeUnit = intent.getIntExtra("timeUnit",800)
        var highlightLineColor = intent.getIntExtra("highlightLineColor", Color.parseColor("#11152E"))
        var highlightLineWidth = intent.getFloatExtra("highlightLineWidth",1.5f)
        var markViewBgColor = intent.getIntExtra("markViewBgColor", Color.parseColor("#F1F5F6"))
        var markViewTitle = intent.getStringExtra("markViewTitle")
        var markViewTitleColor = intent.getIntExtra("markViewTitleColor", Color.parseColor("#8F11152E"))
        var markViewValueColor = intent.getIntExtra("markViewValueColor", Color.parseColor("#8F11152E"))
        var gridLineColor = intent.getIntExtra("gridLineColor", Color.parseColor("#E9EBF1"))
        var xAxisUnit = intent.getStringExtra("xAxisUnit")
        var textColor = intent.getIntExtra("textColor", Color.parseColor("#333333"))
        var mainColor = intent.getIntExtra("mainColor", Color.parseColor("#333333"))
        var bgColor = intent.getIntExtra("bgColor", Color.WHITE)
        var averageLineColor = intent.getIntExtra("averageLineColor", Color.parseColor("#11152E"))
        var labelColor = intent.getIntExtra("labelColor", Color.parseColor("#9AA1A9"))
        var cohTime = intent.getStringExtra("cohTime")
        var averageBgColor = intent.getIntExtra("averageBgColor",0)
        var lineColor = intent.getIntExtra("lineColor", Color.RED)
        var secondLineColor = intent.getIntExtra("secondLineColor", Color.GREEN)
        var bgLineColor = intent.getIntExtra("bgLineColor", Color.GREEN)
//        var titleDescription = intent.getStringExtra("titleDescription")
        var fillStartGradientColor = intent.getIntExtra("fillStartGradientColor", Color.GREEN)
        var fillEndGradientColor = intent.getIntExtra("fillEndGradientColor", Color.GREEN)
        var startTime = intent.getStringExtra("startTime")
        var dataAverage = intent.getIntExtra("dataAverage",0)
        session_pressure_chart.setLineWidth(lineWidth)
        session_pressure_chart.setPointCount(pointCount)
        session_pressure_chart.setTimeUnit(timeUnit)
        session_pressure_chart.isFullScreen = true
        session_pressure_chart.mHighlightLineColor = highlightLineColor
        session_pressure_chart.mHighlightLineWidth = highlightLineWidth
        session_pressure_chart.mMarkViewBgColor = markViewBgColor
        session_pressure_chart.mMarkViewTitleColor = markViewTitleColor
        session_pressure_chart.mMarkViewValueColor = markViewValueColor
        session_pressure_chart.mMarkViewTitle = markViewTitle
        session_pressure_chart.mAverageLabelBgColor = averageBgColor
        session_pressure_chart.setGridLineColor(gridLineColor)
        session_pressure_chart.setXAxisUnit(xAxisUnit)
        session_pressure_chart.setTextColor(textColor)
        session_pressure_chart.bgColor = bgColor
        session_pressure_chart.setAverageLineColor(averageLineColor)
        session_pressure_chart.setLabelColor(labelColor)
        session_pressure_chart.setCohTime(cohTime?:"")
        session_pressure_chart.setLineColor(lineColor)
        session_pressure_chart.setSecondLineColor(secondLineColor)
        session_pressure_chart.isShowDetail = false
        session_pressure_chart.setBgLineColor(bgLineColor)
//        session_pressure_chart.setTitleDescription(titleDescription)
        session_pressure_chart.setFillStartGradientColorColor(fillStartGradientColor)
        session_pressure_chart.setFillEndGradientColorColor(fillEndGradientColor)
        session_pressure_chart.setMainColor(mainColor)
        session_pressure_chart.setData(lineData?.toList(),dataAverage.toDouble(),true)
        session_pressure_chart.setStartTime(startTime)
    }
}