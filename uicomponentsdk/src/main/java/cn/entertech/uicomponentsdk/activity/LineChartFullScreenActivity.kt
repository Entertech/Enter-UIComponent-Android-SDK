package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.activity_line_chart_full_screen.*

class LineChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var lineData = intent.getDoubleArrayExtra("lineData")
        var lineWidth = intent.getFloatExtra("lineWidth",1.5f)
        var pointCount = intent.getIntExtra("pointCount",100)
        var timeUnit = intent.getIntExtra("timeUnit",800)
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
        var average = intent.getIntExtra("average",0)
        var averageBgColor = intent.getIntExtra("averageBgColor",0)
        var lineColor = intent.getIntExtra("lineColor",Color.RED)
        line_chart.setLineWidth(lineWidth)
        line_chart.setPointCount(pointCount)
        line_chart.setTimeUnit(timeUnit)
        line_chart.isFullScreen = true
        line_chart.mHighlightLineColor = highlightLineColor
        line_chart.mHighlightLineWidth = highlightLineWidth
        line_chart.mMarkViewBgColor = markViewBgColor
        line_chart.mMarkViewTitleColor = markViewTitleColor
        line_chart.mMarkViewValueColor = markViewValueColor
        line_chart.mMarkViewTitle = markViewTitle
        line_chart.mAverageLabelBgColor = averageBgColor
        line_chart.setGridLineColor(gridLineColor)
        line_chart.setXAxisUnit(xAxisUnit)
        line_chart.setTextColor(textColor)
        line_chart.bgColor = bgColor
        line_chart.setAverageLineColor(averageLineColor)
        line_chart.setLabelColor(labelColor)
        line_chart.setAverage(average)
        line_chart.setLineColor(lineColor)
        line_chart.setData(lineData.toList(),true)
    }
}
