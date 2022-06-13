package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.ReportBarChartCard
import cn.entertech.uicomponentsdk.report.ReportPressureTrendCard
import kotlinx.android.synthetic.main.activity_pressure_trend_chart_full_screen.*

class PressureTrendChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pressure_trend_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var lineData = intent.getSerializableExtra("lineData") as ArrayList<ReportPressureTrendCard.LineSourceData>
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
        var unit = intent.getStringExtra("unit")
        var showLevel = intent.getBooleanExtra("showLevel",false)
        var levelBgColor = intent.getIntExtra("levelBgColor",Color.RED)
        var levelTextColor = intent.getIntExtra("levelTextColor",Color.RED)
        var xAxisLineColor = intent.getIntExtra("xAxisLineColor",Color.RED)
        var fillGradientStartColor = intent.getIntExtra("fillGradientStartColor",Color.RED)
        var fillGradientEndColor = intent.getIntExtra("fillGradientEndColor",Color.RED)

        chart_pressure_trend.setLineWidth(lineWidth)
        chart_pressure_trend.isFullScreen = true
        chart_pressure_trend.mHighlightLineColor = highlightLineColor
        chart_pressure_trend.mHighlightLineWidth = highlightLineWidth
        chart_pressure_trend.mMarkViewBgColor = markViewBgColor
        chart_pressure_trend.mMarkViewTitleColor = markViewTitleColor
        chart_pressure_trend.mMarkViewValueColor = markViewValueColor
        chart_pressure_trend.mMarkViewTitle = markViewTitle
        chart_pressure_trend.mAverageLabelBgColor = averageBgColor
        chart_pressure_trend.setGridLineColor(gridLineColor)
        chart_pressure_trend.setXAxisUnit(xAxisUnit)
        chart_pressure_trend.setTextColor(textColor)
        chart_pressure_trend.bgColor = bgColor
        chart_pressure_trend.setAverageLineColor(averageLineColor)
        chart_pressure_trend.setLabelColor(labelColor)
        chart_pressure_trend.setAverage(average?:"")
        chart_pressure_trend.setMainColor(mainColor)
        chart_pressure_trend.setUnit(unit)
        chart_pressure_trend.setShowLevel(showLevel)
        chart_pressure_trend.setLevelBgColor(levelBgColor)
        chart_pressure_trend.setLevelTextColor(levelTextColor)
        chart_pressure_trend.setXAxisLineColor(xAxisLineColor)
        chart_pressure_trend.setData(lineData,cycle)
        chart_pressure_trend.setFillGradientStartColor(fillGradientStartColor)
        chart_pressure_trend.setFillGradientEndColor(fillGradientEndColor)
    }
}
