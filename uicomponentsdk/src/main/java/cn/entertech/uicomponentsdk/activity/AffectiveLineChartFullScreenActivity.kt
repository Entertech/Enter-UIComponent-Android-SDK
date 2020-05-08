package cn.entertech.uicomponentsdk.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.activity_affective_line_chart_full_screen.*

class AffectiveLineChartFullScreenActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affective_line_chart_full_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var lineWidth = intent.getFloatExtra("lineWidth",1.5f)
        var pointCount = intent.getIntExtra("pointCount",100)
        var timeUnit = intent.getIntExtra("timeUnit",800)
        var highlightLineColor = intent.getIntExtra("highlightLineColor", Color.parseColor("#11152E"))
        var highlightLineWidth = intent.getFloatExtra("highlightLineWidth",1.5f)
        var markViewBgColor = intent.getIntExtra("markViewBgColor", Color.parseColor("#F1F5F6"))
        var markViewTitle1 = intent.getStringExtra("markViewTitle1")
        var markViewTitle2 = intent.getStringExtra("markViewTitle2")
        var markViewTitleColor = intent.getIntExtra("markViewTitleColor", Color.parseColor("#8F11152E"))
        var markViewValueColor = intent.getIntExtra("markViewValueColor", Color.parseColor("#8F11152E"))
        var gridLineColor = intent.getIntExtra("gridLineColor", Color.parseColor("#E9EBF1"))
        var xAxisUnit = intent.getStringExtra("xAxisUnit")
        var textColor = intent.getIntExtra("textColor", Color.parseColor("#333333"))
        var bgColor = intent.getIntExtra("bgColor", Color.WHITE)
        var averageLineColor = intent.getIntExtra("averageLineColor", Color.parseColor("#11152E"))
        var labelColor = intent.getIntExtra("labelColor", Color.parseColor("#9AA1A9"))
        var attentionAverage = intent.getIntExtra("attentionAverage",0)
        var relaxationAverage = intent.getIntExtra("relaxationAverage",0)
        var attentionLineColor = intent.getIntExtra("attentionLineColor", Color.RED)
        var relaxationLineColor = intent.getIntExtra("relaxationLineColor", Color.RED)
        var attentionData = intent.getDoubleArrayExtra("attentionData")
        var relaxationData = intent.getDoubleArrayExtra("relaxationData")
        line_affective_chart?.setLineWidth(lineWidth)
        line_affective_chart?.setRelaxationLineColor(relaxationLineColor)
        line_affective_chart?.setAttentionLineColor(attentionLineColor)
        line_affective_chart?.setTimeUnit(timeUnit)
        line_affective_chart?.setPointCount(pointCount)
        line_affective_chart?.setXAxisUnit(xAxisUnit)
        line_affective_chart?.setGridLineColor(gridLineColor)
        line_affective_chart?.setTextColor(textColor)
        line_affective_chart?.isFullScreen = true
        line_affective_chart?.bgColor = bgColor
        line_affective_chart?.mHighlightLineColor = highlightLineColor
        line_affective_chart?.mHighlightLineWidth = highlightLineWidth
        line_affective_chart?.mMarkViewBgColor = markViewBgColor
        line_affective_chart?.mMarkViewTitle1 = markViewTitle1
        line_affective_chart?.mMarkViewTitle2 = markViewTitle2
        line_affective_chart?.mMarkViewTitleColor = markViewTitleColor
        line_affective_chart?.mMarkViewValueColor = markViewValueColor
        line_affective_chart?.setAverageLineColor(averageLineColor)
        line_affective_chart?.setLabelColor(labelColor)
        line_affective_chart?.setAttentionAverage(attentionAverage)
        line_affective_chart?.setRelaxationAverage(relaxationAverage)
        line_affective_chart?.setData(attentionData.toList(), relaxationData.toList(), true)
    }
}
