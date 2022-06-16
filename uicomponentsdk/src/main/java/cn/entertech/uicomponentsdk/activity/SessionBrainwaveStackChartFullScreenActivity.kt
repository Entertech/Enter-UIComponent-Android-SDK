package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.activity_session_brainwave_stack_chart_full_screen.*

class SessionBrainwaveStackChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_brainwave_stack_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

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
        var gammaData = intent.getDoubleArrayExtra("gammaData")?.toList() as ArrayList
        var betaData = intent.getDoubleArrayExtra("betaData")?.toList() as ArrayList
        var alphaData = intent.getDoubleArrayExtra("alphaData")?.toList() as ArrayList
        var thetaData = intent.getDoubleArrayExtra("thetaData")?.toList() as ArrayList
        var deltaData = intent.getDoubleArrayExtra("deltaData")?.toList() as ArrayList
        var xAxisLineColor = intent.getIntExtra("xAxisLineColor",Color.RED)
        var fillColors = intent.getStringExtra("fillColors")

        chart_brainwave_session_stack.setLineWidth(lineWidth)
        chart_brainwave_session_stack.isFullScreen = true
        chart_brainwave_session_stack.mHighlightLineColor = highlightLineColor
        chart_brainwave_session_stack.mHighlightLineWidth = highlightLineWidth
        chart_brainwave_session_stack.mMarkViewBgColor = markViewBgColor
        chart_brainwave_session_stack.mMarkViewTitleColor = markViewTitleColor
        chart_brainwave_session_stack.mMarkViewValueColor = markViewValueColor
        chart_brainwave_session_stack.mMarkViewTitle = markViewTitle
        chart_brainwave_session_stack.mAverageLabelBgColor = averageBgColor
        chart_brainwave_session_stack.setGridLineColor(gridLineColor)
        chart_brainwave_session_stack.setXAxisUnit(xAxisUnit)
        chart_brainwave_session_stack.setTextColor(textColor)
        chart_brainwave_session_stack.bgColor = bgColor
        chart_brainwave_session_stack.setAverageLineColor(averageLineColor)
        chart_brainwave_session_stack.setLabelColor(labelColor)
        chart_brainwave_session_stack.setAverage(average?:"")
        chart_brainwave_session_stack.setMainColor(mainColor)
        chart_brainwave_session_stack.setXAxisLineColor(xAxisLineColor)
        chart_brainwave_session_stack.setFillColors(fillColors)
        chart_brainwave_session_stack.setData(listOf(gammaData,betaData,alphaData,thetaData,deltaData),true)
    }
}
