package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.activity_session_common_chart_full_screen.*

class SessionCommonChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_common_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var lineData = intent.getDoubleArrayExtra("lineData")
        var secondLineData = intent.getDoubleArrayExtra("secondLineData")
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
        var average = intent.getStringExtra("average")
        var cohTime = intent.getStringExtra("cohTime")
        var averageBgColor = intent.getIntExtra("averageBgColor",0)
        var lineColor = intent.getIntExtra("lineColor", Color.RED)
        var secondLineColor = intent.getIntExtra("secondLineColor", Color.GREEN)
        var bgLineColor = intent.getIntExtra("bgLineColor", Color.GREEN)
        var titleDescription = intent.getStringExtra("titleDescription")?:""
        var titleUnit = intent.getStringExtra("titleUnit")?:""
        var isShowLevel = intent.getBooleanExtra("isShowLevel", false)
        var levelBgColor = intent.getIntExtra("levelBgColor", Color.GREEN)
        var levelTextColor = intent.getIntExtra("levelTextColor", Color.GREEN)
        var dataTotalTimeMs = intent.getIntExtra("dataTotalTime",0)
        var startTime = intent.getStringExtra("startTime")?:""
        var lineDataAverage = intent.getIntExtra("lineDataAverage",0)
        var lineFlagTotalTime = intent.getIntExtra("lineFlagTotalTime",0)
        var isDataAverageInt = intent.getBooleanExtra("isDataAverageInt",true)
        session_common_chart.setLineWidth(lineWidth)
        session_common_chart.setPointCount(pointCount)
        session_common_chart.setTimeUnit(timeUnit)
        session_common_chart.isFullScreen = true
        session_common_chart.mHighlightLineColor = highlightLineColor
        session_common_chart.mHighlightLineWidth = highlightLineWidth
        session_common_chart.mMarkViewBgColor = markViewBgColor
        session_common_chart.mMarkViewTitleColor = markViewTitleColor
        session_common_chart.mMarkViewValueColor = markViewValueColor
        session_common_chart.mMarkViewTitle = markViewTitle
        session_common_chart.mAverageLabelBgColor = averageBgColor
        session_common_chart.setGridLineColor(gridLineColor)
        session_common_chart.setXAxisUnit(xAxisUnit)
        session_common_chart.setTextColor(textColor)
        session_common_chart.bgColor = bgColor
        session_common_chart.setAverageLineColor(averageLineColor)
        session_common_chart.setLabelColor(labelColor)
        session_common_chart.setCohTime(cohTime?:"")
        session_common_chart.setLineColor(lineColor)
        session_common_chart.setSecondLineColor(secondLineColor)
        session_common_chart.isShowDetail = false
        session_common_chart.setBgLineColor(bgLineColor)
        session_common_chart.setTitleDescription(titleDescription)
        session_common_chart.setTitleUnit(titleUnit)
        session_common_chart.setDataAverageInt(isDataAverageInt)
        session_common_chart.setShowLevel(isShowLevel)
        session_common_chart.setDataTotalTimeMs(dataTotalTimeMs)
        session_common_chart.setLevelBgColor(levelBgColor)
        session_common_chart.setLevelTextColor(levelTextColor)
        session_common_chart.setMainColor(mainColor)
        session_common_chart.setData(lineData?.toList(),lineDataAverage.toDouble(),secondLineData?.toList(),lineFlagTotalTime,true)
        session_common_chart.setStartTime(startTime)
    }
}