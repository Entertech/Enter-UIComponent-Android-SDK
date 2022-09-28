package cn.entertech.uicomponentsdk.report

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.CandleChartFullScreenActivity
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.widget.CandleChartMarkView
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.CustomCombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.chart
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.iv_menu
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_date
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_level
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_title
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_unit
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_value
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class TrendCommonCandleChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mXAxisLineColor: Int = Color.GRAY
    private var mDataAverage: Double = 0.0
    private var mUnit: String? = ""
    private var mLevelTextColor: Int = Color.RED
    private var mLevelBgColor: Int = Color.RED
    private var mIsShowLevel: Boolean = false
    private var mTitleMenuIcon: Drawable? = null
    private lateinit var highestVisibleData: CandleSourceData
    private lateinit var lowestVisibleData: CandleSourceData
    private var mCycle: String = ""
    var mAverageLabelBgColor: Int = Color.parseColor("#ffffff")

    var mMarkViewTitleColor: Int = Color.parseColor("#8F11152E")
        set(value) {
            field = value
            initView()
        }

    var mMarkViewValueColor: Int = Color.parseColor("#11152E")
        set(value) {
            field = value
            initView()
        }
    var mMarkViewTitle: String? = "--"
        set(value) {
            field = value
            initView()
        }
    var mMarkViewBgColor: Int = Color.parseColor("#F1F5F6")
        set(value) {
            field = value
            initView()
        }
    var mHighlightLineWidth: Float = 1.5f
        set(value) {
            field = value
            initView()
        }
    var mHighlightLineColor: Int = Color.parseColor("#11152E")
        set(value) {
            field = value
            initView()
        }

    private lateinit var drawableIcon: Drawable
    private var mAverageLineColor: Int = Color.parseColor("#11152E")
    private var mIsDrawFill: Boolean = false
    private var mAverageValue: String = "0"
    private var mXAxisUnit: String? = "Time(min)"
    private var mLineWidth: Float = 1.5f
    private var mLineColor: Int = Color.RED
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
    private var mData = ArrayList<CandleSourceData>()
    private var mBg: Drawable? = null


    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    var mSelfView: View? = null

    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 800
    var mPointCount: Int = 100
    var mTimeOfTwoPoint: Int = 0
    var isFullScreen = false
        set(value) {
            field = value
            initView()
        }

    var bgColor = Color.WHITE

    companion object {
        const val CYCLE_MONTH = "month"
        const val CYCLE_YEAR = "year"
    }

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_card_candlestick_chart, null)
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
            R.styleable.TrendCommonCandleChart
        )
        mMainColor =
            typeArray.getColor(R.styleable.TrendCommonCandleChart_tccc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.TrendCommonCandleChart_tccc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.TrendCommonCandleChart_tccc_background)

        mGridLineColor =
            typeArray.getColor(
                R.styleable.TrendCommonCandleChart_tccc_gridLineColor,
                mGridLineColor
            )
        mLineWidth =
            typeArray.getDimension(
                R.styleable.TrendCommonCandleChart_tccc_lineWidth,
                mLineWidth
            )
        mIsDrawFill =
            typeArray.getBoolean(R.styleable.TrendCommonCandleChart_tccc_isDrawFill, false)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.TrendCommonCandleChart_tccc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.TrendCommonCandleChart_tccc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_markViewValueColor,
            mMarkViewValueColor
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.TrendCommonCandleChart_tccc_titleMenuIcon)
        mIsShowLevel =
            typeArray.getBoolean(R.styleable.TrendCommonCandleChart_tccc_isShowLevel, false)
        mLevelBgColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_valueLevelBgColor,
            mLevelBgColor
        )
        mLevelTextColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_valueLevelTextColor,
            mLevelTextColor
        )
        mUnit = typeArray.getString(R.styleable.TrendCommonCandleChart_tccc_unit)
        mXAxisLineColor = typeArray.getColor(
            R.styleable.TrendCommonCandleChart_tccc_xAxisLineColor,
            mXAxisLineColor
        )
        typeArray.recycle()
        initView()
    }


    fun initView() {
        initBg()
        initTitle()
        initChart()
        initChartIcon()
    }

    fun initBg() {
        if (mBg != null) {
            if (mBg is ColorDrawable) {
                bgColor = (mBg as ColorDrawable).color
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bgColor = (mBg as GradientDrawable).color!!.defaultColor
                }
            }
        }
        rl_bg.setBackgroundColor(bgColor)
    }

    fun initChartIcon() {
        var iconView = ChartIconView(context)
        iconView.color = mLineColor
        drawableIcon = iconView.toDrawable(context)
    }

    fun initTitle() {
        if (mIsShowLevel) {
            tv_unit.visibility = View.GONE
            tv_level.visibility = View.VISIBLE
            tv_level.setTextColor(mLevelTextColor)
            var bg = tv_level.background as GradientDrawable
            bg.setColor(mLevelBgColor)
        } else {
            tv_unit.visibility = View.VISIBLE
            tv_level.visibility = View.GONE
            tv_unit.setTextColor(mTextColor)
            tv_unit.text = mUnit
        }
        tv_value.setTextColor(mMainColor)
        tv_date.setTextColor(mTextColor)
        tv_title.setTextColor(mTextColor)
        tv_unit.setTextColor(mTextColor)
        iv_menu.setImageDrawable(mTitleMenuIcon)
        iv_menu.setOnClickListener {
            if (isFullScreen) {
                (context as Activity).finish()
            } else {
                var intent = Intent(context, CandleChartFullScreenActivity::class.java)
                intent.putExtra("lineWidth", mLineWidth)
                intent.putExtra("highlightLineColor", mHighlightLineColor)
                intent.putExtra("highlightLineWidth", mHighlightLineWidth)
                intent.putExtra("markViewBgColor", mMarkViewBgColor)
                intent.putExtra("markViewTitle", mMarkViewTitle)
                intent.putExtra("markViewTitleColor", mMarkViewTitleColor)
                intent.putExtra("markViewValueColor", mMarkViewValueColor)
                intent.putExtra("gridLineColor", mGridLineColor)
                intent.putExtra("xAxisLineColor", mXAxisLineColor)
                intent.putExtra("xAxisUnit", mXAxisUnit)
                intent.putExtra("textColor", mTextColor)
                intent.putExtra("mainColor", mMainColor)
                intent.putExtra("bgColor", bgColor)
                intent.putExtra("averageLineColor", mAverageLineColor)
                intent.putExtra("labelColor", mLabelColor)
                intent.putExtra("average", mAverageValue)
                intent.putExtra("averageBgColor", mAverageLabelBgColor)
                intent.putExtra("lineColor", mLineColor)
                intent.putExtra("lineData", mData as Serializable)
                intent.putExtra("cycle", mCycle)
                context.startActivity(intent)
            }
        }
    }

    fun completeSourceData(
        sourceData: ArrayList<CandleSourceData>,
        cycle: String
    ): ArrayList<CandleSourceData> {
        var firstData = sourceData[0]
        when (cycle) {
            CYCLE_MONTH -> {
                var date = firstData.date
                var day = date.split("-")[2]
                if (day != "01") {
                    var preData = ArrayList<CandleSourceData>()
                    var dayIntValue = Integer.parseInt(day)
                    for (j in 1 until dayIntValue) {
                        var curDayString = String.format("%02d", j)
                        var candleSourceData = CandleSourceData()
                        candleSourceData.average = 0f
                        candleSourceData.max = 0f
                        candleSourceData.min = 0f
                        candleSourceData.date = "${day[0]}-${day[1]}-${curDayString}"
                        candleSourceData.xLabel = curDayString
                        preData.add(candleSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
            CYCLE_YEAR -> {
                var date = firstData.date
                var month = date.split("-")[1]
                if (month != "01") {
                    var preData = ArrayList<CandleSourceData>()
                    var monthIntValue = Integer.parseInt(month)
                    for (j in 1 until monthIntValue) {
                        var curMonthString = String.format("%02d", j)
                        var candleSourceData = CandleSourceData()
                        candleSourceData.average = -200f
                        candleSourceData.max = -200f
                        candleSourceData.min = -200f
                        candleSourceData.date = "${month[0]}-${curMonthString}"
                        candleSourceData.xLabel = curMonthString
                        preData.add(candleSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
        }
        return sourceData
    }

    fun initChartXLabel(data: ArrayList<CandleSourceData>) {
        var xLabelOffset = 0
        if (mCycle == CYCLE_MONTH) {
            xLabelOffset = 7
        } else {
            xLabelOffset = 1
        }
        for (i in data.indices) {
            if (((i + 1) % xLabelOffset == 0 && i + 1 < data.size)) {
                val llXAxis = LimitLine(i.toFloat() + 0.5f, "${data[i + 1].xLabel}")
                llXAxis.lineWidth = 0.5f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = getOpacityColor(mTextColor, 0.2f)
                llXAxis.textColor = mLabelColor
                chart.xAxis.addLimitLine(llXAxis)
            }
            if (i == 0 && mCycle == CYCLE_YEAR) {
                val llXAxis = LimitLine(i.toFloat() - 0.5f, "${data[0].xLabel}")
                llXAxis.lineWidth = 0.5f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = getOpacityColor(mTextColor, 0.2f)
                llXAxis.textColor = mLabelColor
                chart.xAxis.addLimitLine(llXAxis)
            }
        }
    }

    private fun initChartVisibleXRangeMaximum(cycle: String): Float {
        return if (cycle == CYCLE_MONTH) {
            31f
        } else {
            12f
        }

    }

    fun initChartCandleValues(data: ArrayList<CandleSourceData>): ArrayList<CandleEntry> {
        val values = ArrayList<CandleEntry>()
        for (i in data.indices) {
            values.add(
                CandleEntry(
                    i.toFloat(),
                    mData[i].max,
                    mData[i].min,
                    mData[i].max,
                    mData[i].min, mData[i]
                )
            )
        }
        return values
    }

    fun initChartLineValues(data: ArrayList<CandleSourceData>): ArrayList<Entry> {
        val lineValues = ArrayList<Entry>()
        for (i in data.indices) {
            if (data[i].average == 0f) {
                continue
            }
            lineValues.add(Entry(i.toFloat(), (data[i].max + data[i].min) / 2f, data[i]))
        }
        return lineValues
    }

    fun initPages(data: ArrayList<CandleSourceData>, cycle: String): ArrayList<ChartPage> {
        var curPage = 0
        var lastMonth = ""
        var lastYear = ""
        var pages = ArrayList<ChartPage>()
        for (i in data!!.indices) {
            when (cycle) {
                CYCLE_MONTH -> {
                    var date = data[i].date
                    var dateSplit = date.split("-")
                    if (dateSplit.size > 1) {
                        val curMonth = dateSplit[1]
                        if (curMonth != lastMonth) {
                            var page = ChartPage()
                            page.cycle = cycle
                            page.curPageIndex = curPage++
                            page.firstDataIndex = i
                            page.date = data[i].date
                            pages.add(page)
                        }
                        lastMonth = curMonth
                    }

                }
                CYCLE_YEAR -> {
                    var curYear = data[i].date.split("-")[0]
                    if (curYear != lastYear) {
                        var page = ChartPage()
                        page.cycle = cycle
                        page.curPageIndex = curPage++
                        page.firstDataIndex = i
                        page.date = data[i].date
                        pages.add(page)
                    }
                    lastYear = curYear
                }
            }
        }
        return pages
    }

    var firstIn = true
    lateinit var set1: CandleDataSet
    lateinit var set2: LineDataSet
    var mPages = ArrayList<ChartPage>()
    var curPage = -1
    var mCandleValues = ArrayList<CandleEntry>()
    var mLineValues = ArrayList<Entry>()
    private var mChartVisibleXRangeMaximum: Float = 0f
    fun setData(data: ArrayList<CandleSourceData>?, cycle: String) {
        when (cycle) {
            "month" -> {
                tv_title.text = "DAILY AVERAGE"
            }
            "year" -> {
                tv_title.text = "MONTHLY AVERAGE"
            }
        }
        if (data == null || data.isEmpty()) {
            return
        }
        this.mDataAverage = data.map { it.average }.filter { it != -200f && it != 0f }.average()
        this.mCycle = cycle
        this.mData = completeSourceData(data, cycle)
        this.mChartVisibleXRangeMaximum = initChartVisibleXRangeMaximum(cycle)
        this.mCandleValues = initChartCandleValues(mData!!)
        this.mLineValues = initChartLineValues(mData!!)
        this.mPages = initPages(data, cycle)
        initChartXLabel(data)

        when (mDataAverage) {
            in 0.0..29.0 -> tv_level.text = context.getString(R.string.sdk_report_low)
            in 30.0..69.0 -> tv_level.text = context.getString(R.string.sdk_report_nor)
            else -> tv_level.text = context.getString(R.string.sdk_report_high)
        }
        // create a dataset and give it a type
        set1 = CandleDataSet(mCandleValues, "")
        set1.setDrawIcons(false)
        set1.setDrawValues(false)
        set1.axisDependency = AxisDependency.LEFT
//        set1.setColor(Color.rgb(80, 80, 80));
        //        set1.setColor(Color.rgb(80, 80, 80));
        set1.shadowColor = mGridLineColor
        set1.shadowWidth = 4f
        set1.decreasingColor = mGridLineColor
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = mGridLineColor
        set1.increasingPaintStyle = Paint.Style.STROKE
        set1.neutralColor = mGridLineColor
//        set1.barSpace = 0.3f
        val dataSets = ArrayList<ICandleDataSet>()
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        val candleData = CandleData(dataSets)

        set2 = LineDataSet(mLineValues, "")
        set2.setDrawIcons(true)
        // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set2.color = mMainColor
        set2.lineWidth = ScreenUtil.px2dip(context, mLineWidth).toFloat()
        // customize legend entry
        set2.formLineWidth = 0.5f
        set2.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        set2.formSize = 15f
        // text size of values
        set2.valueTextSize = 9f
        set2.setDrawValues(false)
        set2.circleColors = listOf(mMainColor)
        set2.circleHoleColor = Color.parseColor("#ffffff")
        set2.highLightColor = mGridLineColor
        set2.highlightLineWidth = mHighlightLineWidth
        set2.setDrawHorizontalHighlightIndicator(false)
        set2.setDrawVerticalHighlightIndicator(true)

        var lineData = LineData()
        lineData.addDataSet(set2)
        val combinedData = CombinedData()
        combinedData.setData(lineData)
        combinedData.setData(candleData)
//         // set data

        initChart()
        chart.data = combinedData
        chart.isHighlightPerDragEnabled = false
        calNiceLabel(mData!!)
        chart.setVisibleXRangeMaximum(mChartVisibleXRangeMaximum)
        chart.xAxis.axisMinimum = -0.5f
        chart.xAxis.axisMaximum = chart.data.xMax + 0.5f
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            if (firstIn) {
                translateChartX(chart, -Float.MAX_VALUE)
            }
        }
        initDateRange()
        chart.notifyDataSetChanged()
    }

    private fun calNiceLabel(data: List<CandleSourceData>) {
        var min = data.map { it.min }.filter { it > 0 }.minOrNull() ?: 5 - 5f
        var max = data.map { it.max }.maxOrNull() ?: 99 + 5f
        var yAxisMax = (max / 1f)
        var yAxisMin = (min * 1f)
        if (min == max) {
            if (min == 0f) {
                chart.axisLeft.axisMaximum = 100f
                chart.axisLeft.axisMinimum = 0f
                chart.axisLeft.mEntries = floatArrayOf(0f, 25f, 50f, 75f, 100f)
                chart.axisLeft.mEntryCount = 5
                return
            } else {
                yAxisMax = min + 10
                if (yAxisMax > 100) {
                    yAxisMax = 100f
                }
                yAxisMin = min - 10
                if (yAxisMin < 0) {
                    yAxisMin = 0f
                }
            }
        }
        var interval = calNiceInterval(yAxisMin.toDouble(), yAxisMax.toDouble())
        var firstLabel = floor(yAxisMin / interval) * interval
        var lastLabel = ceil(yAxisMax / interval) * interval
        var labels = ArrayList<Float>()
        var i = firstLabel
        while (i <= lastLabel) {
            labels.add(i)
            i += interval
        }
        chart.axisLeft.axisMaximum = lastLabel
        chart.axisLeft.axisMinimum = firstLabel
        chart.axisLeft.mEntries = labels.toFloatArray()
        chart.axisLeft.mEntryCount = labels.size
    }


    fun initChart() {
        // draw bars behind lines
        chart.drawOrder = arrayOf(
            DrawOrder.CANDLE, DrawOrder.LINE
        )
        chart.renderer = CustomCombinedChartRenderer(chart, chart.animator, chart.viewPortHandler)
//        chart.setBackgroundColor(bgColor)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
//        chart.setTouchEnabled(true)
//        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(false)
//        chart.isHighlightPerDragEnabled = false

        chart.isDragEnabled = false
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        val markViewTitle = if (mCycle == "month") {
            "DAILY AVERAGE"
        } else {
            "MONTHLY AVERAGE"
        }
        val marker = CandleChartMarkView(context, markViewTitle, mCycle)
        marker.chartView = chart
        marker.setMainColor(mMainColor)
        marker.setTextColor(mTextColor)
        marker.setShowLevel(mIsShowLevel, mLevelTextColor, mLevelBgColor)
        marker.setUnit(mUnit)
        marker.setYOffset(10f.dp())
        chart.marker = marker
        chart.extraTopOffset = 84f
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.gridColor = mGridLineColor
        xAxis.axisLineColor = mXAxisLineColor
        xAxis.axisLineWidth = 0.5f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setLabelCount(5, false)
        yAxis.setDrawGridLines(true)
        yAxis.gridColor = mGridLineColor
        yAxis.gridLineWidth = 0.5f
        yAxis.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 10f), 0f))
        yAxis.textSize = 12f
        yAxis.textColor = mTextColor
        xAxis.setDrawLimitLinesBehindData(true)
        yAxis.setDrawAxisLine(false)
        chart.setNoDataText("")
        chart.setNoDataTextColor(Color.TRANSPARENT);
        chart.invalidate();
        setChartListener()
    }

    fun cancelHighlight() {
        ll_title.visibility = View.VISIBLE
        chart.highlightValue(null)
        set1.setDrawIcons(false)
        set2.setDrawIcons(false)
    }

    fun translateChartX(chart: CustomCombinedChart, translateX: Float) {
        var matrix = chart.viewPortHandler.matrixTouch
        matrix.postTranslate(translateX, 0f)
        chart.viewPortHandler.refresh(matrix, chart, true)
    }

    fun startChartTranslateAnim(offset: Float) {
        var currentOffset = 0f
        var valueAnimator = ValueAnimator.ofFloat(0f, offset)
        valueAnimator.addUpdateListener {
            var translateX = it.animatedValue as Float - currentOffset
            translateChartX(chart, translateX)
            currentOffset = it.animatedValue as Float
        }
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.duration = 500
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                lowestVisibleData =
                    set1.getEntryForXValue(chart.lowestVisibleX, 0f).data as CandleSourceData
                highestVisibleData =
                    set1.getEntryForXValue(chart.highestVisibleX, 0f).data as CandleSourceData
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        valueAnimator.start()
    }

    var downX = 0f
    var downY = 0f
    var moveX = -1f
    var moveY = -1f
    val mainHandler = Handler(Looper.getMainLooper())


    fun moveToPrePage() {
        if (curPage == 0) {
            return
        }
        curPage--
        var prePageIndex = mPages[curPage].firstDataIndex
        updateDateRange(prePageIndex)
        chart.moveViewToAnimated(prePageIndex - 0.5f, 0f, YAxis.AxisDependency.LEFT, 500)
    }

    fun moveToNextPage() {
        if (curPage == mPages.size - 1) {
            return
        }
        curPage++
        var nextPageIndex = mPages[curPage].firstDataIndex
        updateDateRange(nextPageIndex)
        chart.moveViewToAnimated(nextPageIndex - 0.5f, 0f, YAxis.AxisDependency.LEFT, 500)
    }

    fun updateDateRange(startIndex: Int) {
        var finalStartIndex = startIndex
        if (startIndex + mChartVisibleXRangeMaximum.toInt() - 1 >= mData.size) {
            finalStartIndex = mData.size - mChartVisibleXRangeMaximum.toInt()
        }
        lowestVisibleData = mData[finalStartIndex]
        highestVisibleData =
            mData[finalStartIndex + mChartVisibleXRangeMaximum.toInt() - 1]
        if (mCycle == "month") {
            tv_date.text = "${
                lowestVisibleData.date.formatTime(
                    "yyyy-MM-dd",
                    "MMM dd, yyyy"
                )
            }-${
                highestVisibleData.date.formatTime(
                    "yyyy-MM-dd",
                    "MMM dd, yyyy"
                )
            }"
        } else {
            tv_date.text = "${
                lowestVisibleData.date.formatTime(
                    "yyyy-MM",
                    "MMM yyyy"
                )
            }-${
                highestVisibleData.date.formatTime(
                    "yyyy-MM",
                    "MMM yyyy"
                )
            }"
        }
        var showDataAverage =
            mData?.subList(finalStartIndex, finalStartIndex + mChartVisibleXRangeMaximum.toInt())
                ?.filter { it.average != 0f && it.average != -200f }?.map { it.average }?.average() ?: 0.0
        tv_value.text = "${ceil(showDataAverage).toInt()}"
    }

    fun initDateRange() {
        if (mData.size >= mChartVisibleXRangeMaximum) {
            updateDateRange(mData.size - mChartVisibleXRangeMaximum.toInt())
            tv_date.visibility = View.VISIBLE
        } else {
            tv_date.visibility = View.INVISIBLE
        }
    }

    fun setChartListener() {
        chart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                chart.isDragEnabled = false
                if (chart.isHighlightPerDragEnabled) {
                    chart.isHighlightPerDragEnabled = false
                    cancelHighlight()
                } else {
//                    var chartWidth = chart.viewPortHandler.contentWidth()
//                    var deltaX = me.x - downX
//                    if (abs(deltaX) >= chartWidth / 3) {
//                        if (curPage == -1) {
//                            curPage = mPages.size - 1
//                        }
//                        if (curPage == -1) {
//                            curPage = mPages.size - 1
//                        }
//                        if (deltaX > 0) {
//                            moveToPrePage()
//                        } else {
//                            moveToNextPage()
//                        }
//                    } else {
//                        startChartTranslateAnim(-deltaX)
//                    }
                }
            }


            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                cancelHighlight()
                chart.isDragEnabled = false
            }

            override fun onChartSingleTapped(me: MotionEvent) {
            }

            override fun onChartGestureStart(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                firstIn = false
                moveX = -1f
                moveY = -1f
                downX = me.x
                downY = me.y
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartLongPressed(me: MotionEvent) {
                chart.isDragEnabled = false
                chart.isHighlightPerDragEnabled = true
                val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
                chart.highlightValue(highlightByTouchPoint, true)
//                mainHandler.postDelayed({
//                    if (moveX == -1f && moveY == -1f) {
//                        chart.isDragEnabled = false
//                        chart.isHighlightPerDragEnabled = true
//                        val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
//                        chart.highlightValue(highlightByTouchPoint, true)
//                    } else {
//                        var deltaX = moveX - downX
//                        var deltaY = moveY - downY
//                        if (abs(deltaX) < ViewConfiguration.get(context).scaledTouchSlop && abs(
//                                deltaY
//                            ) < ViewConfiguration.get(
//                                context
//                            ).scaledTouchSlop
//                        ) {
//                            chart.isDragEnabled = false
//                            chart.isHighlightPerDragEnabled = true
//                            val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
//                            chart.highlightValue(highlightByTouchPoint, true)
//                        } else {
//                            chart.isDragEnabled = false
//                            chart.isHighlightPerDragEnabled = false
//                            cancelHighlight()
//                        }
//                    }
//                }, 500)
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }


            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                moveX = me.x
                moveY = me.y
            }
        }
        chart.setOnChartValueSelectedListener(
            object : OnChartValueSelectedListener {
                override fun onNothingSelected() {
                    cancelHighlight()
                }

                override fun onValueSelected(e: Entry, h: Highlight?) {
                    ll_title.visibility = View.INVISIBLE
                    chart.highlightValue(null, false)
                    set2.setDrawIcons(false)
                    set2.iconsOffset = MPPointF(0f, 3f)
                    set2.values.forEach {
                        it.icon = null
                    }
                    set1.setDrawIcons(false)
                    set1.iconsOffset = MPPointF(0f, 3f)
                    set1.values.forEach {
                        it.icon = null
                    }
                    e.icon = drawableIcon
                    chart.highlightValue(h, false)
                }
            })
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

    fun setAverage(value: String) {
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

    fun setMainColor(color: Int) {
        this.mMainColor = color
        initView()
    }

    fun setXAxisLineColor(color: Int) {
        this.mXAxisLineColor = color
        initView()
    }

    class CandleSourceData : Serializable {
        var average: Float = 0f
        var max: Float = 0f
        var min: Float = 0f
        var date: String = ""
        var xLabel: String = ""
    }
}