package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.ReportCandleStickChartCard
import kotlinx.android.synthetic.main.activity_candle_chart_full_screen.*

class CandleChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candle_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var lineData = intent.getSerializableExtra("lineData") as ArrayList<ReportCandleStickChartCard.CandleSourceData>
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
        var lineColor = intent.getIntExtra("lineColor",Color.RED)
        var cycle = intent.getStringExtra("cycle")
        chart_candle_stick.setLineWidth(lineWidth)
        chart_candle_stick.isFullScreen = true
        chart_candle_stick.mHighlightLineColor = highlightLineColor
        chart_candle_stick.mHighlightLineWidth = highlightLineWidth
        chart_candle_stick.mMarkViewBgColor = markViewBgColor
        chart_candle_stick.mMarkViewTitleColor = markViewTitleColor
        chart_candle_stick.mMarkViewValueColor = markViewValueColor
        chart_candle_stick.mMarkViewTitle = markViewTitle
        chart_candle_stick.mAverageLabelBgColor = averageBgColor
        chart_candle_stick.setGridLineColor(gridLineColor)
        chart_candle_stick.setXAxisUnit(xAxisUnit)
        chart_candle_stick.setTextColor(textColor)
        chart_candle_stick.bgColor = bgColor
        chart_candle_stick.setAverageLineColor(averageLineColor)
        chart_candle_stick.setLabelColor(labelColor)
        chart_candle_stick.setAverage(average?:"")
        chart_candle_stick.setLineColor(lineColor)
        chart_candle_stick.setData(lineData,cycle)
    }
}
