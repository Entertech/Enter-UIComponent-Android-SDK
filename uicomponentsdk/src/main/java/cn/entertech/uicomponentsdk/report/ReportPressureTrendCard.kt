package cn.entertech.uicomponentsdk.report

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.BarChartFullScreenActivity
import cn.entertech.uicomponentsdk.activity.PressureTrendChartFullScreenActivity
import cn.entertech.uicomponentsdk.report.ReportCandleStickChartCard.Companion.CYCLE_MONTH
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.widget.*
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.*
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.chart
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.tv_date
import kotlinx.android.synthetic.main.layout_card_pressure_trend_chart.view.tv_time_unit_des
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class ReportPressureTrendCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mDataAverage: Double = 0.0
    private var mLevelTextColor: Int = Color.GRAY
    private var mLevelBgColor: Int = Color.GRAY
    private var mShowLevel: Boolean
    private var mUnit: String? = ""
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
    private var mChartVisibleXRangeMaximum: Float = 0f
    private lateinit var highestVisibleData: LineSourceData
    private lateinit var lowestVisibleData: LineSourceData
    private var mVisibleDataCount: Int = 12
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
    private var mAverageValue: String = "0"
    private var mXAxisUnit: String? = "Time(min)"
    private var mLineWidth: Float = 1.5f
    private var mLineColor: Int = Color.RED
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
    private var mIsTitleMenuIconShow: Boolean = true
    private var mData: ArrayList<LineSourceData>? = null
    private var mBg: Drawable? = null
    private var mFillGradientStartColor: Int = Color.parseColor("#80FB9C98")
    private var mFillGradientEndColor: Int = Color.parseColor("#805F76FF")

    private var mTitleMenuIcon: Drawable?

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

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_card_pressure_trend_chart, null)
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
            R.styleable.ReportPressureTrendCard
        )
        mMainColor =
            typeArray.getColor(R.styleable.ReportPressureTrendCard_rptc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportPressureTrendCard_rptc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportPressureTrendCard_rptc_background)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportPressureTrendCard_rptc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportPressureTrendCard_rptc_titleMenuIcon)

        mGridLineColor =
            typeArray.getColor(
                R.styleable.ReportPressureTrendCard_rptc_gridLineColor,
                mGridLineColor
            )
        mLineWidth =
            typeArray.getDimension(R.styleable.ReportPressureTrendCard_rptc_lineWidth, mLineWidth)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportPressureTrendCard_rptc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle = typeArray.getString(R.styleable.ReportPressureTrendCard_rptc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_markViewValueColor,
            mMarkViewValueColor
        )
        mXAxisLineColor =
            typeArray.getColor(
                R.styleable.ReportPressureTrendCard_rptc_xAxisLineColor,
                mXAxisLineColor
            )
        mUnit = typeArray.getString(R.styleable.ReportPressureTrendCard_rptc_unit)
        mShowLevel =
            typeArray.getBoolean(R.styleable.ReportPressureTrendCard_rptc_isShowLevel, false)
        mLevelBgColor =
            typeArray.getColor(
                R.styleable.ReportPressureTrendCard_rptc_valueLevelBgColor,
                mLevelBgColor
            )
        mLevelTextColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_valueLevelTextColor,
            mLevelTextColor
        )
        mFillGradientStartColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_fillGradientStartColor,
            mFillGradientStartColor
        )
        mFillGradientEndColor = typeArray.getColor(
            R.styleable.ReportPressureTrendCard_rptc_fillGradientEndColor,
            mFillGradientEndColor
        )
        typeArray.recycle()
        initView()
    }


    fun initView() {
        initBg()
        initTitle()
        initTimeUnit()
        initChart()
        initChartIcon()
    }

    fun initTimeUnit() {
        tv_time_unit_des.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_time_unit_des.text = mXAxisUnit
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
        tv_value.setTextColor(mMainColor)
        tv_date.setTextColor(mTextColor)
        tv_title.setTextColor(mTextColor)
        iv_menu.setImageDrawable(mTitleMenuIcon)
        iv_menu.setOnClickListener {
            if (isFullScreen) {
                (context as Activity).finish()
            } else {
                var intent = Intent(context, PressureTrendChartFullScreenActivity::class.java)
                intent.putExtra("lineWidth", mLineWidth)
                intent.putExtra("highlightLineColor", mHighlightLineColor)
                intent.putExtra("highlightLineWidth", mHighlightLineWidth)
                intent.putExtra("markViewBgColor", mMarkViewBgColor)
                intent.putExtra("markViewTitle", mMarkViewTitle)
                intent.putExtra("markViewTitleColor", mMarkViewTitleColor)
                intent.putExtra("markViewValueColor", mMarkViewValueColor)
                intent.putExtra("gridLineColor", mGridLineColor)
                intent.putExtra("xAxisUnit", mXAxisUnit)
                intent.putExtra("textColor", mTextColor)
                intent.putExtra("bgColor", bgColor)
                intent.putExtra("averageLineColor", mAverageLineColor)
                intent.putExtra("labelColor", mLabelColor)
                intent.putExtra("average", mAverageValue)
                intent.putExtra("averageBgColor", mAverageLabelBgColor)
                intent.putExtra("mainColor", mMainColor)
                intent.putExtra("lineData", mData!! as Serializable)
                intent.putExtra("cycle", mCycle)
                intent.putExtra("unit", mUnit)
                intent.putExtra("showLevel", mShowLevel)
                intent.putExtra("levelBgColor", mLevelBgColor)
                intent.putExtra("levelTextColor", mLevelTextColor)
                intent.putExtra("xAxisLineColor", mXAxisLineColor)
                intent.putExtra("fillGradientStartColor", mFillGradientStartColor)
                intent.putExtra("fillGradientEndColor", mFillGradientEndColor)
                context.startActivity(intent)
            }
        }
    }

    fun completeSourceData(
        sourceData: ArrayList<LineSourceData>,
        cycle: String
    ): ArrayList<LineSourceData> {
        var firstData = sourceData[0]
        when (cycle) {
            ReportCandleStickChartCard.CYCLE_MONTH -> {
                var date = firstData.date
                var day = date.split("-")[2]
                if (day != "01") {
                    var preData = ArrayList<LineSourceData>()
                    var dayIntValue = Integer.parseInt(day)
                    for (j in 1 until dayIntValue) {
                        var curDayString = String.format("%02d", j)
                        var barSourceData = LineSourceData()
                        barSourceData.value = 0f
                        barSourceData.date = "${day[0]}-${day[1]}-${curDayString}"
                        barSourceData.xLabel = curDayString
                        preData.add(barSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
            ReportCandleStickChartCard.CYCLE_YEAR -> {
                var date = firstData.date
                var month = date.split("-")[1]
                if (month != "01") {
                    var preData = ArrayList<LineSourceData>()
                    var monthIntValue = Integer.parseInt(month)
                    for (j in 1 until monthIntValue) {
                        var curMonthString = String.format("%02d", j)
                        var barSourceData = LineSourceData()
                        barSourceData.value = 0f
                        barSourceData.date = "${month[0]}-${curMonthString}"
                        barSourceData.xLabel = curMonthString
                        preData.add(barSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
        }
        return sourceData
    }

    var mPages = ArrayList<ChartPage>()
    var curPage = -1

    var mValues = ArrayList<Entry>()
    private var mCycle: String = ""
    lateinit var set: LineDataSet
    var firstIn = true

    fun initPages(data: ArrayList<LineSourceData>, cycle: String): ArrayList<ChartPage> {
        var curPage = 0
        var lastMonth = ""
        var lastYear = ""
        var pages = ArrayList<ChartPage>()
        for (i in data.indices) {
            when (cycle) {
                ReportCandleStickChartCard.CYCLE_MONTH -> {
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
                ReportCandleStickChartCard.CYCLE_YEAR -> {
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

    fun initChartValues(data: ArrayList<LineSourceData>): ArrayList<Entry> {
        val values = ArrayList<Entry>()
        for (i in data.indices) {
            values.add(
                Entry(
                    i.toFloat(),
                    data[i].value, data[i]
                )
            )
        }
        return values
    }

    fun initChartXLabel(data: ArrayList<LineSourceData>) {
        for (i in data.indices) {
            if ((i + 1) % 7 == 0) {
                val llXAxis = LimitLine(i.toFloat() + 0.5f, "${data[i + 1].xLabel}")
                llXAxis.lineWidth = 1f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = mGridLineColor
                llXAxis.textColor = mTextColor
                chart.xAxis.addLimitLine(llXAxis)
            }
        }
    }

    var yLimitLineValues = listOf(25f, 50f, 75f)
    fun setData(data: ArrayList<LineSourceData>?, cycle: String) {
        if (data == null) {
            return
        }
        this.mData = completeSourceData(data, cycle)
        this.mDataAverage = mData!!.map { it.value }.average()
        this.mCycle = cycle
        this.mPages = initPages(mData!!, cycle)
        this.mChartVisibleXRangeMaximum = initChartVisibleXRangeMaximum(cycle)
        this.mValues = initChartValues(mData!!)
        initChartXLabel(mData!!)
        for (i in yLimitLineValues.indices) {
            val ll = LimitLine(yLimitLineValues[i], "")
            ll.lineWidth = 1f
            ll.enableDashedLine(10f, 10f, 0f)
            ll.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll.textSize = 12f
            ll.xOffset = 10f
            ll.yOffset = 8f
            ll.textColor = mTextColor
            ll.lineColor = mGridLineColor
            chart.axisLeft.addLimitLine(ll)
        }
        set = LineDataSet(mValues, "")
        set.setDrawIcons(true)
        // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set.color = mMainColor
        set.lineWidth = 2f
        // customize legend entry
        set.formLineWidth = 1f
        set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        set.formSize = 15f
        set.isHighlightEnabled = true
        // text size of values
        set.valueTextSize = 9f
        set.circleColors = listOf(mMainColor)
        set.circleHoleColor = Color.parseColor("#ffffff")
        set.setDrawValues(false)
        set.highLightColor = mHighlightLineColor
        var lineData = LineData()
        lineData.addDataSet(set)
//         // set data
        chart.data = lineData
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(mChartVisibleXRangeMaximum)
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            if (firstIn) {
                initLowestAndHighestVisibleData()
                translateChartX(chart, -Float.MAX_VALUE)
            }
        }
        initDateRange()
    }

    fun initDateRange() {
        if (mValues.size >= mChartVisibleXRangeMaximum) {
            updateDateRange(mValues.size - mChartVisibleXRangeMaximum.toInt())
            tv_date.visibility = View.VISIBLE
        } else {
            tv_date.visibility = View.INVISIBLE
        }
    }


    private fun initChartVisibleXRangeMaximum(cycle: String): Float {
        return if (cycle == CYCLE_MONTH) {
            31f
        } else {
            12f
        }

    }

    private fun calNiceLabel(data: List<LineSourceData>) {
        var min = data.map { it.value }.min()!!
        var max = data.map { it.value }.max()!!
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
        firstLabel = if (firstLabel - 10 < 0) {
            0f
        } else {
            floor(firstLabel - 10)
        }
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
//        chart.setBackgroundColor(bgColor)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
//        chart.setTouchEnabled(true)
//        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(true)
        chart.setGridBackgroundColors(intArrayOf(mFillGradientStartColor, mFillGradientEndColor))
//        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = true
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        val marker = PressureTrendChartMarkView(context, mLineColor, mMarkViewTitle)
        marker.chartView = chart
//        marker.setMarkTitleColor(mMarkViewTitleColor)
//        marker.setMarkViewBgColor(mMarkViewBgColor)
//        marker.setMarkViewValueColor(mMarkViewValueColor)

        chart.marker = marker
        chart.extraTopOffset = 28f.dp()
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = mXAxisLineColor
        xAxis.axisLineWidth = 1f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
//        chart.setMaxVisibleValueCount(100000)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setLabelCount(5, false)
        yAxis.gridColor = mGridLineColor
        yAxis.gridLineWidth = 1f
        yAxis.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 10f), 0f))
        yAxis.textSize = 12f
        yAxis.textColor = mTextColor
        xAxis.setDrawLimitLinesBehindData(true)
        yAxis.setDrawAxisLine(false)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.mEntries = floatArrayOf(12.5f, 37.5f, 62.5f, 87.5f)
        yAxis.mEntryCount = 4
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value == 12.5f) {
                    return context.getString(R.string.pressure_level_low)
                } else if (value == 37.5f) {
                    return context.getString(R.string.pressure_level_normal)
                } else if (value == 62.5f) {
                    return context.getString(R.string.pressure_level_elevated)
                } else if (value == 87.5f) {
                    return context.getString(R.string.pressure_level_high)
                } else {
                    return ""
                }
            }
        }
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxis.setDrawGridLines(false)
        setChartListener()
    }

    fun cancelHighlight() {
        ll_title.visibility = View.VISIBLE
        chart.highlightValue(null)
        set.setDrawIcons(false)
    }

    fun translateChartX(chart: CustomLineChart, translateX: Float) {
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
                initLowestAndHighestVisibleData()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        valueAnimator.start()
    }

    fun initLowestAndHighestVisibleData() {
        lowestVisibleData = set.getEntryForXValue(chart.lowestVisibleX, 0f).data as LineSourceData
        highestVisibleData = set.getEntryForXValue(chart.highestVisibleX, 0f).data as LineSourceData
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

    fun updateDateRange(startIndex: Int) {
        lowestVisibleData = set.getEntryForIndex(startIndex).data as LineSourceData
        highestVisibleData =
            set.getEntryForIndex(startIndex + mChartVisibleXRangeMaximum.toInt() - 1).data as LineSourceData
        tv_date.text = "${lowestVisibleData.date}-${highestVisibleData.date}"
        val curDataList  = mData!!.subList(startIndex,startIndex + mChartVisibleXRangeMaximum.toInt()).map { it.value }
        when(curDataList.average()){
            in 0.0..24.0->{
                tv_value.text = context.getString(R.string.pressure_level_low)
            }
            in 25.0..49.0->{
                tv_value.text = context.getString(R.string.pressure_level_normal)
            }
            in 50.0..74.0->{
                tv_value.text = context.getString(R.string.pressure_level_elevated)
            }
            else ->{
                tv_value.text = context.getString(R.string.pressure_level_high)
            }
        }
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

    fun setChartListener() {
        chart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                chart.isDragEnabled = true
                if (chart.isHighlightPerDragEnabled) {
                    chart.isHighlightPerDragEnabled = false
                    cancelHighlight()
                } else {
                    var chartWidth = chart.viewPortHandler.contentWidth()
                    var deltaX = me.x - downX
                    if (abs(deltaX) >= chartWidth / 3) {
                        if (curPage == -1) {
                            curPage = mPages.size - 1
                        }
                        if (deltaX > 0) {
                            moveToPrePage()
                        } else {
                            moveToNextPage()
                        }
                    } else {
                        startChartTranslateAnim(-deltaX)
                    }
                }
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                cancelHighlight()
                chart.isDragEnabled = true
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
//                set2.setDrawVerticalHighlightIndicator(true)
//                set2.setDrawHorizontalHighlightIndicator(false)
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartLongPressed(me: MotionEvent) {
                mainHandler.postDelayed({
                    if (moveX == -1f && moveY == -1f) {
                        chart.isDragEnabled = false
                        chart.isHighlightPerDragEnabled = true
                        val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
                        chart.highlightValue(highlightByTouchPoint, true)
                    } else {
                        var deltaX = moveX - downX
                        var deltaY = moveY - downY
                        if (abs(deltaX) < ViewConfiguration.get(context).scaledTouchSlop && abs(
                                deltaY
                            ) < ViewConfiguration.get(
                                context
                            ).scaledTouchSlop
                        ) {
                            chart.isDragEnabled = false
                            chart.isHighlightPerDragEnabled = true
                            val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
                            chart.highlightValue(highlightByTouchPoint, true)
                        } else {
                            chart.isDragEnabled = true
                            chart.isHighlightPerDragEnabled = false
                            cancelHighlight()
                        }
                    }
                }, 500)

            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }


            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                moveX = me.x
                moveY = me.y
            }
        }
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                cancelHighlight()
            }

            override fun onValueSelected(e: Entry, h: Highlight?) {
                ll_title.visibility = View.INVISIBLE
                chart.highlightValue(null, false)
                set.setDrawIcons(true)
                set.iconsOffset = MPPointF(0f, 3f)
                set.values.forEach {
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

    fun setVisibleDataCount(count: Int) {
        this.mVisibleDataCount = count
        initView()
    }

    fun setMainColor(color: Int) {
        this.mMainColor = color
        initView()
    }

    fun setUnit(unit: String) {
        this.mUnit = unit
        initView()
    }

    fun setShowLevel(showLevel: Boolean) {
        this.mShowLevel = showLevel
        initView()
    }

    fun setLevelBgColor(color: Int) {
        this.mLevelBgColor = color
        initView()
    }

    fun setLevelTextColor(color: Int) {
        this.mLevelTextColor = color
        initView()
    }

    fun setXAxisLineColor(color: Int) {
        this.mXAxisLineColor = color
        initView()
    }
    fun setFillGradientStartColor(color:Int){
        this.mFillGradientStartColor = color
        initView()
    }

    fun setFillGradientEndColor(color:Int){
        this.mFillGradientEndColor = color
        initView()
    }

    class LineSourceData : Serializable {
        var value: Float = 0f
        var date: String = ""
        var xLabel: String = ""
    }
}