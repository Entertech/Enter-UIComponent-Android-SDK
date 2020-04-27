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
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.Utils
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
import kotlin.math.*

class ReportLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mAverageLineColor: Int = Color.parseColor("#9AA1A9")
    private var mIsDrawFill: Boolean = false
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

    var bgColor = Color.WHITE

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
        mIsDrawFill = typeArray.getBoolean(R.styleable.ReportLineChartCard_rlcc_isDrawFill, false)
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
            affectiveView.setTextColor(mTextColor)
            affectiveView.setBg(mBg)
            affectiveView.setAverageLineColor(mAverageLineColor)
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

    fun setData(data: List<Double>?, isShowAllData: Boolean = false) {
        if (data == null) {
            return
        }
        this.mData = formatData(data)
        var maxValue = mData!!.max()
        var minValue = mData!!.min()
        var yAxisMax = (maxValue!! / 0.9f).toFloat()
        var yAxisMin = (minValue!! * 0.9f).toFloat()
        if (yAxisMax - yAxisMin < 5) {
            chart.axisLeft.axisMaximum = yAxisMax + 1
            chart.axisLeft.axisMinimum = if (yAxisMin - 1 <= 0) 0f else { yAxisMin - 1 }
        } else {
            chart.axisLeft.axisMaximum = yAxisMax
            chart.axisLeft.axisMinimum = yAxisMin
        }
        val values = ArrayList<Entry>()

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
            ll1.textColor = mTextColor
            ll1.lineColor = mAverageLineColor
            chart.axisLeft.addLimitLine(ll1)
        }

        for (i in sampleData.indices) {
            values.add(Entry(i.toFloat(), sampleData[i].toFloat()))
        }

        var set1: LineDataSet

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
            set1.setDrawHighlightIndicators(true)
            set1.setDrawFilled(mIsDrawFill)
            set1.fillAlpha = 255
            set1.setDrawCircles(false)
            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            if (mIsDrawFill) {
                // set color of filled area
                if (Utils.getSDKInt() >= 18) {
                    // drawables only supported on api level 18 and above
                    var gradientDrawable = GradientDrawable()

                    // Set the color array to draw gradient
                    gradientDrawable.setColors(
                        intArrayOf(
                            mLineColor,
                            getOpacityColor(mLineColor, 0.5F)
                        )
                    )

                    // Set the GradientDrawable gradient type linear gradient
                    gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT)

                    // Set GradientDrawable shape is a rectangle
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE)
                    set1.fillDrawable = gradientDrawable
                } else {
                    set1.fillColor = mLineColor
                }
                set1.lineWidth = 0f
                set1.setDrawFilled(true)
            } else {
                set1.setDrawFilled(false)
                set1.lineWidth = mLineWidth
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data

            chart.onChartGestureListener = object:OnChartGestureListener{
                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {

                }

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {

                }

                override fun onChartSingleTapped(me: MotionEvent?) {
                    if (set1.isDrawCirclesEnabled){
                        set1.setDrawCircles(false)
                    }else{
                        set1.setDrawCircles(true)
                    }
                }

                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                }

                override fun onChartLongPressed(me: MotionEvent?) {
                }

                override fun onChartDoubleTapped(me: MotionEvent?) {
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                }

            }
            chart.notifyDataSetChanged()
        }
    }

    fun initChart() {
        chart.setBackgroundColor(bgColor)

        // disable description text
        chart.getDescription().setEnabled(false)
        chart.legend.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(true)
        chart.animateX(500)
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
        yAxis.setLabelCount(5, false)
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

    fun setAverage(value: Int) {
        this.mAverageValue = value
        initView()
    }

    fun setAverageLineColor(color: Int) {
        this.mAverageLineColor = color
        initView()
    }

    fun setBg(bg: Drawable?) {
        this.mBg = bg
        initView()
    }

    fun setTextColor(color: Int) {
        this.mTextColor = color
        initView()
    }
}