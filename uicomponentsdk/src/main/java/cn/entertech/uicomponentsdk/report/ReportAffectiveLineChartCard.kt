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
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.chart
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_avg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_max
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_min
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_card_attention.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_report_affective_card.view.*
import kotlinx.android.synthetic.main.layout_report_affective_card.view.legend_attention
import kotlinx.android.synthetic.main.layout_report_affective_card.view.legend_relaxation
import kotlinx.android.synthetic.main.pop_card_attention.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

class ReportAffectiveLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mAttentionData: List<Double> = ArrayList()
    private var mRelaxationData: List<Double> = ArrayList()
    //    private var mAttentionData: List<Double>? = null
    private var mAttentionLineColor: Int = Color.RED
    private var mRelaxationLineColor: Int = Color.BLUE
    private var mTitle: String? = "Changes During Meditation"
    private var mIsTitleIconShow: Boolean = false
    private var mIsTitleMenuIconShow: Boolean = true
    private var mData: List<Double>? = null
    private var mBg: Drawable? = null

    private var mTiltleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?

    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    var mSelfView: View? = null
    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 800
    var mPointCount: Int = 100
    var mTimeOfTwoPoint: Int = 0

    val dataSets = ArrayList<ILineDataSet>()

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_report_affective_card, null)
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
            R.styleable.ReportAffectiveLineChartCard
        )
        mMainColor =
            typeArray.getColor(R.styleable.ReportAffectiveLineChartCard_ralcc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportAffectiveLineChartCard_ralcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_background)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mTitle = typeArray.getString(R.styleable.ReportAffectiveLineChartCard_ralcc_title)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_titleMenuIcon)

        mPointCount =
            typeArray.getInteger(
                R.styleable.ReportAffectiveLineChartCard_ralcc_pointCount,
                mPointCount
            )
        mAttentionLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_attentionLineColor,
            mAttentionLineColor
        )
        mRelaxationLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_relaxationLineColor,
            mRelaxationLineColor
        )
        mTimeUnit =
            typeArray.getInteger(R.styleable.ReportAffectiveLineChartCard_ralcc_timeUnit, mTimeUnit)
        initView()
    }

    fun initView() {
        initTitle()
        iv_menu.setOnClickListener {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            var view = LayoutInflater.from(context).inflate(R.layout.pop_card_attention,null)
            var affectiveView =
                ReportAffectiveLineChartCard(context, null, 0, R.layout.pop_card_attention)
            affectiveView.setData(mAttentionData, mRelaxationData, true)
            var popWindow = PopupWindow(affectiveView, MATCH_PARENT, MATCH_PARENT)
            popWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
            affectiveView.findViewById<ImageView>(R.id.iv_menu)
                .setImageResource(R.drawable.vector_drawable_screen_shrink)
            affectiveView.findViewById<LinearLayout>(R.id.legend).visibility = View.VISIBLE
            affectiveView.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                popWindow.dismiss()
            }
        }
        initChart()
        tv_time_unit_des.setTextColor(getOpacityColor(mTextColor, 0.7f))

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

        legend_attention.setLegendIconColor(mAttentionLineColor)
        legend_relaxation.setLegendIconColor(mRelaxationLineColor)
    }

    fun initTitle() {
        tv_title.visibility = View.VISIBLE
        tv_title.text = mTitle
        tv_title.setTextColor(mTextColor)
        if (mIsTitleIconShow) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.background = mTiltleIcon
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsTitleMenuIconShow) {
            iv_menu.background = mTitleMenuIcon
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
        }
    }

    fun setOnMenuIconClickListener(onClickListener: View.OnClickListener) {
        iv_menu.setOnClickListener(onClickListener)
    }

    fun drawLine(mData: List<Double>, lineColor: Int, isShowAllData: Boolean = false) {
        var sample = mData!!.size / mPointCount
        if (isShowAllData) {
            sample = 1
        }
        var sampleData = ArrayList<Double>()
        for (i in mData!!.indices) {
            if (i % sample == 0) {
                sampleData.add(mData!![i])
            }
        }

        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = (sampleData!!.size * mTimeOfTwoPoint / 1000F / 60F).toInt() + 1
        var minOffset = totalMin / 8 + 1

        for (i in sampleData!!.indices) {
            if ((i * mTimeOfTwoPoint / 1000F / 60F) % minOffset == 0f) {
                val llXAxis =
                    LimitLine(
                        i.toFloat(),
                        "${(i * mTimeOfTwoPoint / 1000F / 60F).toInt()}"
                    )
                llXAxis.lineWidth = 1f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
                llXAxis.textSize = 8f
                llXAxis.yOffset = -12f
                llXAxis.lineColor = Color.parseColor("#999999")
                if (i == 0) {
                    llXAxis.xOffset = -3f
                }
                chart.xAxis.addLimitLine(llXAxis)
            }
        }

        if (mData != null && mData!!.isNotEmpty()) {
            var average = mData!!.average().toInt() + 1
            val ll1 = LimitLine(
                average.toFloat(), "AVG:${if (average > 100) {
                    average - 100
                } else {
                    average
                }
                }"
            )
            ll1.lineWidth = 1f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 10f
            ll1.lineColor = Color.parseColor("#9AA1A9")
            chart.axisLeft.addLimitLine(ll1)
        }
        val zeroLimitLine = LimitLine(100f, "")
        zeroLimitLine.lineWidth = 1f
        zeroLimitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        zeroLimitLine.textSize = 10f
        zeroLimitLine.lineColor = Color.parseColor("#9AA1A9")
        chart.axisLeft.addLimitLine(zeroLimitLine)
        val values = ArrayList<Entry>()

        for (i in sampleData.indices) {
            values.add(Entry(i.toFloat(), mData!![i].toFloat()))
        }

        val set1: LineDataSet
        set1 = LineDataSet(values, "")
        set1.color = lineColor
        set1.lineWidth = 0f
        // draw points as solid circles
        set1.setDrawCircleHole(false)
        // customize legend entry
        set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f
        // text size of values
        set1.valueTextSize = 9f
        set1.setDrawValues(false)
        set1.setDrawHighlightIndicators(false)
        set1.setDrawFilled(false)
        set1.setDrawCircles(false)
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        val data = LineData(dataSets)
        // set data
        chart.data = data
        chart.notifyDataSetChanged()

    }

    fun setData(
        attentionData: List<Double>?,
        relaxationData: List<Double>?,
        isShowAllData: Boolean = false
    ) {
        if (attentionData == null || relaxationData == null) {
            return
        }
        this.mAttentionData = attentionData
        this.mRelaxationData = relaxationData
        drawLine(mAttentionData!!, mAttentionLineColor, isShowAllData)
        drawLine(mRelaxationData!!.map { it + 100 }, mRelaxationLineColor, isShowAllData)
//        chart.xAxis.setLabelCount(mData!!.size,true)
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
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        // axis range
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f
//        yAxis.labelCount = 3
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {
                var yLabel = ""
                if (value == 0f) {
                    yLabel =  "0"
                } else if (value == 100f){
                    yLabel = "100"
                }else if (value == 200f){
                    yLabel = "100"
                }
                return yLabel
            }
        }
        // // Create Limit Lines // //


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(false)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
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