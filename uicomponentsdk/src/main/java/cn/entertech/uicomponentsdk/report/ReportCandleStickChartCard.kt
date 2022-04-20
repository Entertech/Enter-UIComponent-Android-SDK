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
import cn.entertech.uicomponentsdk.widget.*
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
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.*
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.*
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.chart
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.iv_menu
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_date
import kotlinx.android.synthetic.main.layout_card_candlestick_chart.view.tv_time_unit_des
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class ReportCandleStickChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private lateinit var highestVisibleData: CandleSourceData
    private lateinit var lowestVisibleData: CandleSourceData
    private var mCycle: String = ""
    private var mSmallTitle: String? = ""
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
    private var mTitle: String? = "Changes During Meditation"
    private var mIsTitleIconShow: Boolean = false
    private var mIsTitleMenuIconShow: Boolean = true
    private var mData: List<CandleSourceData>? = null
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
            R.styleable.ReportLineChartCard
        )
        mMainColor = typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportLineChartCard_rlcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportLineChartCard_rlcc_background)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportLineChartCard_rlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mSmallTitle = typeArray.getString(R.styleable.ReportLineChartCard_rlcc_smallTitle)
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

        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportLineChartCard_rlcc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportLineChartCard_rlcc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportLineChartCard_rlcc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle = typeArray.getString(R.styleable.ReportLineChartCard_rlcc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportLineChartCard_rlcc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportLineChartCard_rlcc_markViewValueColor,
            mMarkViewValueColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.ReportLineChartCard_rlcc_averageLabelBgColor,
            mAverageLabelBgColor
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
//        tv_title.visibility = View.VISIBLE
//        tv_title.text = mTitle
//        tv_title.setTextColor(mTextColor)
//        if (mSmallTitle != null) {
//            tv_small_title.visibility = View.VISIBLE
//            tv_small_title.text = mSmallTitle
//            tv_small_title.setTextColor(mTextColor)
//        }
//        if (mIsTitleIconShow) {
//            iv_icon.visibility = View.VISIBLE
//            iv_icon.setImageDrawable(mTiltleIcon)
//        } else {
//            iv_icon.visibility = View.GONE
//        }
        if (mIsTitleMenuIconShow) {
            iv_menu.setImageDrawable(mTitleMenuIcon)
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
        }
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
                intent.putExtra("xAxisUnit", mXAxisUnit)
                intent.putExtra("textColor", mTextColor)
                intent.putExtra("bgColor", bgColor)
                intent.putExtra("averageLineColor", mAverageLineColor)
                intent.putExtra("labelColor", mLabelColor)
                intent.putExtra("average", mAverageValue)
                intent.putExtra("averageBgColor", mAverageLabelBgColor)
                intent.putExtra("lineColor", mLineColor)
                intent.putExtra("lineData", mData!! as Serializable)
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
                        candleSourceData.average = 0f
                        candleSourceData.max = 0f
                        candleSourceData.min = 0f
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

    var firstIn = true
    lateinit var set1: CandleDataSet
    lateinit var set2: LineDataSet
    val pages = ArrayList<Page>()
    var curPage = -1
    val values = ArrayList<CandleEntry>()
    fun setData(data: ArrayList<CandleSourceData>?, cycle: String) {
        if (data == null || data.isEmpty()) {
            return
        }
        this.mCycle = cycle
        this.mData = completeSourceData(data, cycle)
        for (i in mData!!.indices) {
            if ((i + 1) % 7 == 0) {
                val llXAxis = LimitLine(i.toFloat() + 0.5f, "${mData!![i + 1].xLabel}")
                llXAxis.lineWidth = 1f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = getOpacityColor(mTextColor, 0.2f)
                llXAxis.textColor = mLabelColor
                chart.xAxis.addLimitLine(llXAxis)
            }
        }

        val lineValues = ArrayList<Entry>()
        var curPage = 0
        var lastMonth = ""
        var lastYear = ""
        for (i in mData!!.indices) {
            when (cycle) {
                CYCLE_MONTH -> {
                    var date = data[i].date
                    var dateSplit = date.split("-")
                    if (dateSplit.size > 1) {
                        val curMonth = dateSplit[1]
                        if (curMonth != lastMonth) {
                            var page = Page()
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
                        var page = Page()
                        page.cycle = cycle
                        page.curPageIndex = curPage++
                        page.firstDataIndex = i
                        page.date = data[i].date
                        pages.add(page)
                    }
                    lastYear = curYear
                }
            }
            values.add(
                CandleEntry(
                    i.toFloat(),
                    mData!![i].max,
                    mData!![i].min,
                    mData!![i].max,
                    mData!![i].min, mData!![i]
                )
            )
            lineValues.add(Entry(i.toFloat(), mData!![i].average, mData!![i]))
        }

        // create a dataset and give it a type
        set1 = CandleDataSet(values, "")
        set1.setDrawIcons(false)
        set1.setDrawValues(false)
        set1.axisDependency = AxisDependency.LEFT
//        set1.setColor(Color.rgb(80, 80, 80));
        //        set1.setColor(Color.rgb(80, 80, 80));
        set1.shadowColor = Color.DKGRAY
        set1.shadowWidth = 0.7f
        set1.decreasingColor = Color.RED
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(122, 242, 84)
        set1.increasingPaintStyle = Paint.Style.STROKE
        set1.neutralColor = Color.BLUE
//        set1.barSpace = 0.3f
        val dataSets = ArrayList<ICandleDataSet>()
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        val candleData = CandleData(dataSets)

        set2 = LineDataSet(lineValues, "")
        set2.setDrawIcons(true)
        // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set2.color = mLineColor
        // customize legend entry
        set2.formLineWidth = 1f
        set2.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        set2.formSize = 15f
        // text size of values
        set2.valueTextSize = 9f
        set2.setDrawValues(false)
        set2.highLightColor = mHighlightLineColor
        set2.highlightLineWidth = mHighlightLineWidth
        set2.setDrawHorizontalHighlightIndicator(false)
        set2.setDrawVerticalHighlightIndicator(true)
        var lineData = LineData()
        lineData.addDataSet(set2)
        val combinedData = CombinedData()
        combinedData.setData(candleData)
        combinedData.setData(lineData)
//         // set data
        chart.data = combinedData
        chart.isHighlightPerDragEnabled = false
        calNiceLabel(mData!!)
        chart.notifyDataSetChanged()
        setChartVisibleXRangeMaximum(chart, cycle)
//        chart.xAxis.spaceMax = 1f
//        chart.xAxis.spaceMin = 1f
        chart.xAxis.axisMinimum = -0.5f
        chart.xAxis.axisMaximum = chart.data.xMax+0.5f
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            Log.d("#####", "addOnGlobalLayoutListener")
            if (firstIn) {
                translateChartX(chart, -Float.MAX_VALUE)
            }
        }
    }

    private fun setChartVisibleXRangeMaximum(chart: CustomCombinedChart, cycle: String) {
        when (cycle) {
            CYCLE_MONTH -> {
                chart.setVisibleXRangeMaximum(31f)
            }
            CYCLE_YEAR -> {
                chart.setVisibleXRangeMaximum(12f)
            }
        }
    }

    private fun calNiceLabel(data: List<CandleSourceData>) {
        var min = data.map { it.min }.min() ?: 5 - 5f
        var max = data.map { it.max }.max() ?: 99 + 5f
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
        chart.renderer = CustomCombinedChartRenderer(chart, chart.animator, chart.viewPortHandler)
//        chart.setBackgroundColor(bgColor)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
//        chart.setTouchEnabled(true)
//        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(false)
//        chart.isHighlightPerDragEnabled = false

        chart.isDragEnabled = true
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        val marker = CandleChartMarkView(context, mLineColor, mMarkViewTitle)
        marker.chartView = chart
//        marker.setMarkTitleColor(mMarkViewTitleColor)
//        marker.setMarkViewBgColor(mMarkViewBgColor)
//        marker.setMarkViewValueColor(mMarkViewValueColor)
        chart.marker = marker
        chart.extraTopOffset = 28f.dp()
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = getOpacityColor(mTextColor, 0.6f)
        xAxis.axisLineWidth = 1f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
        chart.setMaxVisibleValueCount(100000)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setLabelCount(5, false)
        yAxis.setDrawGridLines(true)
        yAxis.gridColor = getOpacityColor(mTextColor, 0.2f)
        yAxis.gridLineWidth = 1f
        yAxis.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 10f), 0f))
        yAxis.textSize = 12f
        yAxis.textColor = Color.parseColor("#9AA1A9")
        xAxis.setDrawLimitLinesBehindData(true)
        yAxis.setDrawAxisLine(false)
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
                val lowestVisibleData =
                    set1.getEntryForXValue(chart.lowestVisibleX, 0f).data as CandleSourceData
                val highestVisibleData =
                    set1.getEntryForXValue(chart.highestVisibleX, 0f).data as CandleSourceData
                tv_date.text = "${lowestVisibleData.date}-${highestVisibleData.date}"
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
        var prePageIndex = pages[curPage].firstDataIndex
        updateDateDuration(prePageIndex)
        chart.moveViewToAnimated(prePageIndex - 0.5f, 0f, YAxis.AxisDependency.LEFT, 500)
    }

    fun updateDateDuration(startIndex: Int) {
        lowestVisibleData = set2.getEntryForIndex(startIndex).data as CandleSourceData
        highestVisibleData = set2.getEntryForIndex(startIndex+30).data as CandleSourceData
        tv_date.text = "${lowestVisibleData.date}-${highestVisibleData.date}"
    }

    fun moveToNextPage() {
        if (curPage == pages.size - 1) {
            return
        }
        curPage++
        var nextPageIndex = pages[curPage].firstDataIndex
        updateDateDuration(nextPageIndex)
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
                        if (curPage == -1){
                            curPage = pages.size-1
                        }
                        if (curPage == -1) {
                            curPage = pages.size - 1
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
//                var entry = set1.getEntryForXValue(me.x,0f)
                var entry = chart.getEntryByTouchPoint(me.x, me.y)
                var xValue = chart.getPixelForValues(entry.x, entry.y, AxisDependency.LEFT)
                Log.d(
                    "#####",
                    "downx is ${me.x},entry x ${entry.x},xvalue is ${xValue},chart width ${chart.width},viewholer width ${chart.viewPortHandler.chartWidth}"
                )
//                set1.setDrawVerticalHighlightIndicator(true)
//                set1.setDrawHorizontalHighlightIndicator(false)
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
        chart.setOnChartValueSelectedListener(
            object : OnChartValueSelectedListener {
                override fun onNothingSelected() {
                    cancelHighlight()
                }

                override fun onValueSelected(e: Entry, h: Highlight?) {
                    ll_title.visibility = View.INVISIBLE
                    chart.highlightValue(null, false)
                    set1.setDrawIcons(true)
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

    class CandleSourceData : Serializable {
        var average: Float = 0f
        var max: Float = 0f
        var min: Float = 0f
        var date: String = ""
        var xLabel: String = ""
    }

    class Page {
        var cycle: String = ""
        var curPageIndex: Int = 0
        var firstDataIndex: Int = 0
        var date: String = ""
    }
}