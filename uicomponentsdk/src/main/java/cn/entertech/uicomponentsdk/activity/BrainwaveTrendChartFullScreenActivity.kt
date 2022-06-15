package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.ReportBrainwaveTrendCard
import kotlinx.android.synthetic.main.activity_brainwave_trend_chart_full_screen.*

class BrainwaveTrendChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brainwave_trend_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var lineData = intent.getSerializableExtra("lineData") as ArrayList<ReportBrainwaveTrendCard.BrainwaveLineSourceData>
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
        var cycle = intent.getStringExtra("cycle")
        var xAxisLineColor = intent.getIntExtra("xAxisLineColor",Color.RED)
        var fillColors = intent.getStringExtra("fillColors")

        chart_brainwave_trend.setLineWidth(lineWidth)
        chart_brainwave_trend.isFullScreen = true
        chart_brainwave_trend.mHighlightLineColor = highlightLineColor
        chart_brainwave_trend.mHighlightLineWidth = highlightLineWidth
        chart_brainwave_trend.mMarkViewBgColor = markViewBgColor
        chart_brainwave_trend.mMarkViewTitleColor = markViewTitleColor
        chart_brainwave_trend.mMarkViewValueColor = markViewValueColor
        chart_brainwave_trend.mMarkViewTitle = markViewTitle
        chart_brainwave_trend.mAverageLabelBgColor = averageBgColor
        chart_brainwave_trend.setGridLineColor(gridLineColor)
        chart_brainwave_trend.setXAxisUnit(xAxisUnit)
        chart_brainwave_trend.setTextColor(textColor)
        chart_brainwave_trend.bgColor = bgColor
        chart_brainwave_trend.setAverageLineColor(averageLineColor)
        chart_brainwave_trend.setLabelColor(labelColor)
        chart_brainwave_trend.setAverage(average?:"")
        chart_brainwave_trend.setMainColor(mainColor)
        chart_brainwave_trend.setXAxisLineColor(xAxisLineColor)
        chart_brainwave_trend.setFillColors(fillColors)
        chart_brainwave_trend.setData(lineData,cycle)
    }
}
