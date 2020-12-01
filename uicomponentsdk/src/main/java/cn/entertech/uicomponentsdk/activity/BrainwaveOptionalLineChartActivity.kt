package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.dp
import kotlinx.android.synthetic.main.activity_brainwave_optional_line_chart.*

class BrainwaveOptionalLineChartActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brainwave_optional_line_chart)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var pointCount = intent.getIntExtra("pointCount",100)
        var timeUnit = intent.getIntExtra("timeUnit",800)
        var highlightLineColor = intent.getIntExtra("highlightLineColor", Color.parseColor("#11152E"))
        var highlightLineWidth = intent.getFloatExtra("highlightLineWidth",1.5f)
        var lineWidth = intent.getFloatExtra("lineWidth",1f.dp())
        var markViewBgColor = intent.getIntExtra("markViewBgColor", Color.parseColor("#F1F5F6"))
        var markViewTitleColor = intent.getIntExtra("markViewTitleColor", Color.parseColor("#8F11152E"))
        var markViewValueColor = intent.getIntExtra("markViewValueColor", Color.parseColor("#8F11152E"))
        var gridLineColor = intent.getIntExtra("gridLineColor", Color.parseColor("#E9EBF1"))
        var xAxisUnit = intent.getStringExtra("xAxisUnit")
        var textColor = intent.getIntExtra("textColor", Color.parseColor("#333333"))
        var bgColor = intent.getIntExtra("bgColor", Color.WHITE)
        var labelColor = intent.getIntExtra("labelColor", Color.parseColor("#9AA1A9"))
        var gammaData = intent.getDoubleArrayExtra("gammaData").toList() as ArrayList
        var betaData = intent.getDoubleArrayExtra("betaData").toList() as ArrayList
        var alphaData = intent.getDoubleArrayExtra("alphaData").toList() as ArrayList
        var thetaData = intent.getDoubleArrayExtra("thetaData").toList() as ArrayList
        var deltaData = intent.getDoubleArrayExtra("deltaData").toList() as ArrayList
        var spectrumColors = intent.getIntArrayExtra("spectrumColors").toList() as ArrayList

        brainwave_chart.setPointCount(pointCount)
        brainwave_chart.setTimeUnit(timeUnit)
        brainwave_chart.isFullScreen = true
        brainwave_chart.mHighlightLineColor = highlightLineColor
        brainwave_chart.mHighlightLineWidth = highlightLineWidth
        brainwave_chart.setLineWidth(lineWidth)
        brainwave_chart.mMarkViewBgColor = markViewBgColor
        brainwave_chart.mMarkViewTitleColor = markViewTitleColor
        brainwave_chart.mMarkViewValueColor = markViewValueColor
        brainwave_chart.setGridLineColor(gridLineColor)
        brainwave_chart.setXAxisUnit(xAxisUnit)
        brainwave_chart.setTextColor(textColor)
        brainwave_chart.setSpectrumColors(spectrumColors)
        brainwave_chart.bgColor = bgColor
        brainwave_chart.setLabelColor(labelColor)
        brainwave_chart.setData(listOf(gammaData,betaData,alphaData,thetaData,deltaData),true)
    }
}