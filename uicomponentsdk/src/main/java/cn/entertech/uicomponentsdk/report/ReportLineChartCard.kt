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
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.formatData
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
import kotlinx.android.synthetic.main.pop_card_attention.view.*
import java.util.*
import kotlin.math.sign

class ReportLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mAverageValue: Int = 0
    private var mXAxisUnit: String? = "Time(min)"
    private var mLineWidth: Float = 1.5f
    private var mLineColor: Int = Color.RED
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
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

    init {
        if (layoutId == null) {
            mSelfView = LayoutInflater.from(context).inflate(R.layout.layout_card_attention, null)
            var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            mSelfView?.layoutParams = layoutParams
        } else {
            mSelfView = LayoutInflater.from(context).inflate(layoutId, null)
            var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            mSelfView?.layoutParams = layoutParams
        }
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportLineChartCard
        )
        mMainColor = typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportLineChartCard_rlcc_background)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportLineChartCard_rlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportLineChartCard_rlcc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportLineChartCard_rlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportLineChartCard_rlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mTitle = typeArray.getString(R.styleable.ReportLineChartCard_rlcc_title)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportLineChartCard_rlcc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportLineChartCard_rlcc_titleMenuIcon)

        mPointCount =
            typeArray.getInteger(R.styleable.ReportLineChartCard_rlcc_pointCount, mPointCount)
        mLineColor = typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_lineColor, mLineColor)
        mLabelColor =
            typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_labelColor, mLabelColor)
        mGridLineColor =
            typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_gridLineColor, mGridLineColor)
        mTimeUnit = typeArray.getInteger(R.styleable.ReportLineChartCard_rlcc_timeUnit, mTimeUnit)
        mLineWidth =
            typeArray.getDimension(R.styleable.ReportLineChartCard_rlcc_lineWidth, mLineWidth)
        mXAxisUnit = typeArray.getString(R.styleable.ReportLineChartCard_rlcc_xAxisUnit)
        initView()
    }

    fun initView() {
        initTitle()
        iv_menu.setOnClickListener {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            var affectiveView = ReportLineChartCard(context, null, 0, R.layout.pop_card_attention)
            affectiveView.setLineColor(mLineColor)
            affectiveView.setLineWidth(mLineWidth)
            affectiveView.setPointCount(mPointCount)
            affectiveView.setTimeUnit(mTimeUnit)
            affectiveView.setGridLineColor(mGridLineColor)
            affectiveView.setXAxisUnit(mXAxisUnit)
            affectiveView.setLabelColor(mLabelColor)
            affectiveView.setAverage(mAverageValue)
            affectiveView.setData(mData, true)
            affectiveView.findViewById<TextView>(R.id.tv_title).text =
               context.getString(R.string.chart_full_screen_tip)
            var popWindow = PopupWindow(affectiveView, MATCH_PARENT, MATCH_PARENT)
            affectiveView.findViewById<ImageView>(R.id.iv_menu)
                .setImageResource(R.drawable.vector_drawable_screen_shrink)
            affectiveView.findViewById<LinearLayout>(R.id.ll_custom_yLabel).visibility = View.GONE
            affectiveView.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                popWindow.dismiss()
            }
            popWindow.showAtLocation(iv_menu, Gravity.CENTER, 0, 0)
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

        tv_time_unit_des.text = mXAxisUnit
    }

    fun initTitle() {
        tv_title.visibility = View.VISIBLE
        tv_title.text = mTitle
        tv_title.setTextColor(mTextColor)
        if (mIsTitleIconShow) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTiltleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsTitleMenuIconShow) {
            iv_menu.setImageDrawable(mTitleMenuIcon)
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
        }
    }

    fun setData(data: List<Double>?,isShowAllData: Boolean = false) {
        if (data == null) {
            return
        }
        this.mData = formatData(data)
        var maxValue = mData!!.max()
        var minValue = mData!!.min()
        val values = ArrayList<Entry>()
        var yAxisMax = (maxValue!! / 0.9f)
        var yAxisMix = (minValue!! * 0.9f)
        chart.axisLeft.axisMaximum = yAxisMax.toFloat()
        chart.axisLeft.axisMinimum = yAxisMix.toFloat()

        var sample = mData!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        var sampleData = ArrayList<Double>()
        for (i in mData!!.indices) {
            if (i % sample == 0) {
                sampleData.add(mData!![i])
            }
        }

        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = mData!!.size * mTimeUnit / 1000F / 60F
        var minOffset = (totalMin / 8).toInt() + 1
        var currentMin = 0
        while (currentMin < totalMin) {
            var limitX = currentMin * 60f * 1000 / mTimeOfTwoPoint
            val llXAxis = LimitLine(limitX, "$currentMin")
            llXAxis.lineWidth = 1f
            llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            llXAxis.textSize = 12f
            llXAxis.yOffset = -15f
            llXAxis.lineColor = mGridLineColor
            llXAxis.textColor = mLabelColor
            if (currentMin == 0) {
                llXAxis.xOffset = -3f
            } else {
                llXAxis.xOffset = -1f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }

        if (mData != null && mData!!.isNotEmpty()) {
            val ll1 = LimitLine(mAverageValue.toFloat(), "AVG:$mAverageValue")
            ll1.lineWidth = 1f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 14f
            ll1.lineColor = Color.parseColor("#9AA1A9")
            chart.axisLeft.addLimitLine(ll1)
        }

        for (i in sampleData.indices) {
            values.add(Entry(i.toFloat(), sampleData[i].toFloat()))
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
            set1.color = mLineColor
//            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = mLineWidth
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
            set1.setDrawFilled(false)
            set1.setDrawCircles(false)
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
            // set color of filled area
//            if (Utils.getSDKInt() >= 18) {
//                // drawables only supported on api level 18 and above
//                val drawable =
//                    ContextCompat.getDrawable(context, R.drawable.shape_affective_chart_fill)
//                set1.fillDrawable = drawable
//            } else {
//                set1.fillColor = Color.GREEN
//            }

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
        chart.axisRight.isEnabled = false
        // horizontal grid lines
//        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        // axis range
        yAxis.labelCount = 3
        yAxis.textSize = 12f
        yAxis.textColor = Color.parseColor("#9AA1A9")
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

    fun setLineWidth(lineWidth: Float) {
        this.mLineWidth = lineWidth
        initView()
    }

    fun setLineColor(lineColor: Int) {
        this.mLineColor = lineColor
        initView()
    }

    fun setTimeUnit(timeUnit: Int) {
        this.mTimeUnit = timeUnit
        initView()
    }

    fun setPointCount(pointCount: Int) {
        this.mPointCount = pointCount
        initView()
    }

    fun setGridLineColor(gridLineColor: Int) {
        this.mGridLineColor = gridLineColor
        initView()
    }

    fun setLabelColor(labelColor: Int) {
        this.mLabelColor = labelColor
        initView()
    }

    fun setXAxisUnit(axisUnit: String?) {
        this.mXAxisUnit = axisUnit
        initView()
    }

    fun setAverage(value:Int){
        this.mAverageValue = value
        initView()
    }

}