package cn.entertech.uicomponentsdk.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import cn.entertech.uicomponentsdk.utils.removeZeroData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_avg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_max
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_min
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_card_heart_rate.view.*
import java.util.*

class ReportAffectiveView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mData: List<Double>? = null
    private var mIsAbsoluteTime: Boolean = false
    private var mEmotionType: Int = 0
    private var mFillColor: Int = Color.parseColor("#0000ff")
    private var mIsShowMin: Boolean
    private var mIsShowMax: Boolean
    private var mIsShowAvg: Boolean = true
    private var mSample: Int = 3
    private var mBg: Drawable? = null

    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
    }

    private var mInfoIconRes: Int? = null
    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    var mSelfView: View? = null

    init {
        if (layoutId == null) {
            mSelfView = LayoutInflater.from(context).inflate(R.layout.layout_card_attention, null)
            var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            mSelfView?.layoutParams = layoutParams
        } else {
            mSelfView = LayoutInflater.from(context).inflate(layoutId, null)
            var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            mSelfView?.layoutParams = layoutParams
        }
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportAffectiveView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportAffectiveView_ra_background)
        mIsShowInfoIcon =
            typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportAffectiveView_ra_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mSample = typeArray.getInteger(R.styleable.ReportAffectiveView_ra_sample, 3)
        mIsShowAvg = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowAvg, true)
        mIsShowMax = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowMax, true)
        mIsShowMin = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowMin, true)
        mFillColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_fillColor, mFillColor)
        mEmotionType = typeArray.getInteger(R.styleable.ReportAffectiveView_ra_emotionType, 0)
        mIsAbsoluteTime =
            typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isAbsoluteTimeAxis, false)
        initView()
    }

    fun initView() {
        iv_fullscreen.setOnClickListener {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            var view = LayoutInflater.from(context).inflate(R.layout.pop_card_attention,null)
            var affectiveView = ReportAffectiveView(context, null, 0, R.layout.pop_card_attention)
            affectiveView.setData(mData)
            var popWindow = PopupWindow(affectiveView, MATCH_PARENT, MATCH_PARENT)
            popWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
            affectiveView.findViewById<ImageView>(R.id.iv_fullscreen).setOnClickListener {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                popWindow.dismiss()
            }
        }
        initChart()
        tv_title.setTextColor(mMainColor)
        when (mEmotionType) {
            0 -> {
                tv_title.text = "专注度"
                tv_vertical.setText("专注度")
            }
            1 -> {
                tv_title.text = "放松度"
                tv_vertical.setText("放松度")
            }
        }
        tv_time_unit_des.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_avg.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_max.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_min.setTextColor(getOpacityColor(mTextColor, 0.7f))

        tv_vertical.setTextColor(getOpacityColor(mTextColor, 0.7f))
//        chart_attention.setYAxisTextColor(getOpacityColor(mTextColor, 0.7f))
//        chart_attention.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
//        chart_attention.setGridLineColor(getOpacityColor(mTextColor, 0.1f))

        var bgColor = Color.WHITE
        if (mBg != null) {
            ll_bg.background = mBg
        } else {
            mBg = ll_bg.background
        }
        if (mBg is ColorDrawable) {
            bgColor = (mBg as ColorDrawable).color
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bgColor = (mBg as GradientDrawable).color.defaultColor
            }
        }
//        chart_attention.setBackgroundColor(bgColor)
        if (mIsShowInfoIcon) {
            iv_info.visibility = View.VISIBLE
        } else {
            iv_info.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
//        chart_attention.setSample(mSample)
        if (mIsShowAvg) {
            ll_avg.visibility = View.VISIBLE
        } else {
            ll_avg.visibility = View.GONE
        }
        if (mIsShowMax) {
            ll_max.visibility = View.VISIBLE
        } else {
            ll_max.visibility = View.GONE
        }
        if (mIsShowMin) {
            ll_min.visibility = View.VISIBLE
        } else {
            ll_min.visibility = View.GONE
        }
        var fillColors = intArrayOf(mFillColor, getOpacityColor(mFillColor, 0.7f))
//        chart_attention.setColors(fillColors)
    }


    fun setData(data: List<Double>?) {
        if (data == null) {
            return
        }
        this.mData = data
//        chart.xAxis.setLabelCount(mData!!.size,true)
        var scaleX = mData!!.size / 100f
        if (scaleX < 1f) {
            scaleX = 1f
        }
//        chart.viewPortHandler.setMaximumScaleX(scaleX)
        var maxValue = data.max()
        var minValue = data.min()
        val values = ArrayList<Entry>()
        var yAxisMax = (maxValue!! / 0.9f)
        var yAxisMix = (minValue!! * 0.9f)
        chart.axisLeft.axisMaximum = yAxisMax.toFloat()
        chart.axisLeft.axisMinimum = yAxisMix.toFloat()
        for(i in data.indices){
            if (i % 75 == 0){
                val llXAxis = LimitLine(i.toFloat(), "${i/75}")
                llXAxis.lineWidth = 1f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
                llXAxis.textSize = 8f
                llXAxis.yOffset = -12f
                llXAxis.lineColor = Color.parseColor("#999999")
                if (i == 0){
                    llXAxis.xOffset = -3f
                }
                chart.xAxis.addLimitLine(llXAxis)
            }
        }

        for (i in data.indices) {
            values.add(Entry(i.toFloat(), data[i].toFloat()))
        }

        val set1: LineDataSet

        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")

//            set1.setDrawIcons(false)
            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set1.color = Color.parseColor("#00d993")
//            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 0f
//            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f
            set1.setDrawValues(false)
            // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            set1.setDrawHighlightIndicators(false)
            set1.setDrawFilled(true)
            set1.setDrawCircles(false)
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable =
                    ContextCompat.getDrawable(context, R.drawable.shape_affective_chart_fill)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.GREEN
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data


            chart.notifyDataSetChanged()
        }
    }

    fun initChart() {
        chart.setBackgroundColor(Color.parseColor("#ffffff"))

        // disable description text
        chart.getDescription().setEnabled(false)
        chart.legend.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.setDrawGridBackground(false)
        // enable scaling and dragging
        chart.setDragEnabled(true)
//        chart.setScaleEnabled(true)
        chart.setScaleXEnabled(true)
        chart.setScaleYEnabled(false)
        // force pinch zoom along both axis
        chart.setPinchZoom(true)
        val xAxis: XAxis = chart.xAxis
        // vertical grid lines
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
//        xAxis.enableGridDashedLine(10f, 10f, 0f)
//        xAxis.setLabelCount(8)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {

//                xAxis.mEntries = floatArrayOf(75f,150f,225f,300f,375f,450f,525f)
                Log.d("####", "x entry is " + Arrays.toString(xAxis.mEntries))
//                if ((value * 0.8f) % 60 == 0f){
//                    return "${(value * 0.8f / 60).toInt()}"
//                }else{
//                    return ""
//                }
//                return "${value}"
//                Log.d("####", "min is : ${(value * 0.8f / 60).toInt()}")
                return "${String.format("%.1f", value * 0.8f / 60)}"
//                return "${(value * 0.8f / 60).toInt()}"
            }
        }
//        xAxis.setValueFormatter { value, axis ->
//            if ((value * 0.8f) % 60 == 0f){
//                "${(value * 0.8f / 60).toInt()}"
//            }else{
//                ""
//            }
//        }
//        xAxis.setLabelCount(7, true)
//        xAxis.spaceMax = 300f
        // disable dual axis (only use LEFT axis)
//        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("1","2","3","4"))
        chart.axisRight.isEnabled = false
        // horizontal grid lines
//        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        // axis range
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 20f
        yAxis.labelCount = 3
//        yAxis.setValueFormatter { value, axis ->
//            if (value == 0f){
//                ""
//            }else{
//                "${value.toInt()}"
//            }
//        }
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {
                if (value == 0f) {
                    return ""
                } else {
                    return "${value.toInt()}"
                }
            }
        }
        // // Create Limit Lines // //


        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)
        // add limit lines
        yAxis.addLimitLine(ll1)
    }

//    fun setData(startTime: Long, data: List<Double>?) {
//        if (startTime == null || data == null){
//            return
//        }
//        chart_attention.setValues(data)
//        var removeZeroAttentionRec = removeZeroData(data)
//        var attentionMax =
//            java.util.Collections.max(removeZeroAttentionRec).toFloat()
//        var attentionMin =
//            java.util.Collections.min(removeZeroAttentionRec).toFloat()
//        var sum = 0.0
//        for (value in removeZeroAttentionRec) {
//            sum += value
//        }
//        var avg = sum / removeZeroAttentionRec.size
//        tv_avg.text = "${context.getString(R.string.avg)}${avg.toInt()}"
//        tv_max.text = "${context.getString(R.string.max)}${attentionMax.toInt()}"
//        tv_min.text = "${context.getString(R.string.min)}${attentionMin.toInt()}"
//        if (mIsAbsoluteTime){
//            chart_attention.isAbsoluteTime(true,startTime)
//        }
//    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}