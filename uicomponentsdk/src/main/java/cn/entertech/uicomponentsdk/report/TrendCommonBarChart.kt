package cn.entertech.uicomponentsdk.report

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.FragmentActivity
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.BarChartFullScreenActivity
import cn.entertech.uicomponentsdk.fragment.TrendChartDateSelectFragment
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.utils.TimeUtils.*
import cn.entertech.uicomponentsdk.widget.BarChartMarkView
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.ChartMoreListAdapter
import cn.entertech.uicomponentsdk.widget.CustomBarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.*
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.chart
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.ll_chart
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.tv_date_fullscreen
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.tv_level
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.tv_unit
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.tv_value
import kotlinx.android.synthetic.main.layout_chart_date_select.view.*
import kotlinx.android.synthetic.main.layout_chart_date_select.view.iv_more
import kotlinx.android.synthetic.main.layout_chart_date_select.view.rl_date_container
import kotlinx.android.synthetic.main.layout_chart_date_select.view.tv_date
import java.io.Serializable
import kotlin.math.ceil
import kotlin.math.floor

class TrendCommonBarChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mBottomSheetBg: Int = Color.parseColor("#ffffff")
    private var mIconColor: Int = Color.parseColor("#cccccc")
    private var mDateBgColor: Int = Color.parseColor("#cccccc")
    private var bottomSheetDialog: TrendChartDateSelectFragment? = null
    private var OnDataSelectListener: ((String, List<String>) -> Unit)? = null
    var mIsDataTime: Boolean = false
        set(value) {
            field = value
            initView()
        }
    private var mDataAverage: Double = 0.0
    private var mLevelTextColor: Int = Color.GRAY
    private var mLevelBgColor: Int = Color.GRAY
    private var mShowLevel: Boolean
    private var mUnit: String? = ""
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
    private var mChartVisibleXRangeMaximum: Float = 0f
    private lateinit var highestVisibleData: BarSourceData
    private lateinit var lowestVisibleData: BarSourceData
    private var mVisibleDataCount: Int = CHART_X_MAX_COUNT_YEAR
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
    private var mData = ArrayList<BarSourceData>()
    private var mBg: Drawable? = null

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
        const val CHART_X_MAX_COUNT_YEAR = 12
        const val CHART_X_MAX_COUNT_MONTH = 31
        const val CYCLE_MONTH = "month"
        const val CYCLE_YEAR = "year"
    }

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_card_bar_chart, null)
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
            R.styleable.TrendCommonBarChart
        )
        mMainColor = typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.TrendCommonBarChart_tcbc_background)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.TrendCommonBarChart_tcbc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.TrendCommonBarChart_tcbc_titleMenuIcon)

        mGridLineColor =
            typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_gridLineColor, mGridLineColor)
        mLineWidth =
            typeArray.getDimension(R.styleable.TrendCommonBarChart_tcbc_lineWidth, mLineWidth)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.TrendCommonBarChart_tcbc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle = typeArray.getString(R.styleable.TrendCommonBarChart_tcbc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_markViewValueColor,
            mMarkViewValueColor
        )
        mXAxisLineColor =
            typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_xAxisLineColor, mXAxisLineColor)
        mUnit = typeArray.getString(R.styleable.TrendCommonBarChart_tcbc_unit)
        mShowLevel = typeArray.getBoolean(R.styleable.TrendCommonBarChart_tcbc_isShowLevel, false)
        mLevelBgColor =
            typeArray.getColor(
                R.styleable.TrendCommonBarChart_tcbc_valueLevelBgColor,
                mLevelBgColor
            )
        mLevelTextColor = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_valueLevelTextColor,
            mLevelTextColor
        )
        mIsDataTime =
            typeArray.getBoolean(R.styleable.TrendCommonBarChart_tcbc_isDataTime, mIsDataTime)
        mBottomSheetBg = typeArray.getColor(
            R.styleable.TrendCommonBarChart_tcbc_bottomSheetBgColor,
            mBottomSheetBg
        )
        mIconColor = typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_iconColor, mIconColor)
        mDateBgColor =
            typeArray.getColor(R.styleable.TrendCommonBarChart_tcbc_dateBgColor, mDateBgColor)
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
        if (mShowLevel) {
            tv_unit.setTextColor(mTextColor)
            tv_unit.visibility = View.GONE
            tv_level.visibility = View.VISIBLE
            tv_level.setTextColor(mLevelTextColor)
            var bg = tv_level.background as GradientDrawable
            bg.setColor(mLevelBgColor)
        } else {
            tv_unit.visibility = View.VISIBLE
            tv_level.visibility = View.GONE
            tv_unit.text = mUnit
        }
        tv_value.setTextColor(mMainColor)
        tv_date.setTextColor(mTextColor)
        tv_date_fullscreen.setTextColor(mTextColor)
        tv_title.setTextColor(mTextColor)
        tv_unit.setTextColor(mTextColor)
        if (isFullScreen) {
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
        }
        iv_menu.setImageDrawable(mTitleMenuIcon)
        iv_menu.setOnClickListener {
            if (isFullScreen) {
                (context as Activity).finish()
            } else {
                fullScreen()
            }
        }
    }

    fun fullScreen() {
        var intent = Intent(context, BarChartFullScreenActivity::class.java)
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
        intent.putExtra("lineData", mData as Serializable)
        intent.putExtra("cycle", mCycle)
        intent.putExtra("unit", mUnit)
        intent.putExtra("showLevel", mShowLevel)
        intent.putExtra("levelBgColor", mLevelBgColor)
        intent.putExtra("levelTextColor", mLevelTextColor)
        intent.putExtra("xAxisLineColor", mXAxisLineColor)
        intent.putExtra("isDataTime", mIsDataTime)
        intent.putExtra("curYear", curYear)
        intent.putExtra("curMonth", curMonth)
        context.startActivity(intent)
    }
    fun completeSourceData(
        sourceData: ArrayList<BarSourceData>,
        cycle: String
    ): ArrayList<BarSourceData> {
        var firstData = sourceData[0]
        when (cycle) {
            TrendCommonCandleChart.CYCLE_MONTH -> {
                var date = firstData.date
                val days = date.split("-")
                var day = days[2]
                if (day != "01") {
                    var preData = ArrayList<BarSourceData>()
                    var dayIntValue = Integer.parseInt(day)
                    for (j in 1 until dayIntValue) {
                        var curDayString = String.format("%02d", j)
                        var barSourceData = BarSourceData()
                        barSourceData.value = 0f
                        barSourceData.date = "${days[0]}-${days[1]}-${curDayString}"
                        barSourceData.xLabel = curDayString
                        preData.add(barSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
            TrendCommonCandleChart.CYCLE_YEAR -> {
                var date = firstData.date
                val months = date.split("-")
                var month = months[1]
                if (month != "01") {
                    var preData = ArrayList<BarSourceData>()
                    var monthIntValue = Integer.parseInt(month)
                    for (j in 1 until monthIntValue) {
                        var curMonthString = String.format("%02d", j)
                        var barSourceData = BarSourceData()
                        barSourceData.value = 0f
                        barSourceData.date = "${months[0]}-${curMonthString}"
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

    var mValues = ArrayList<BarEntry>()
    private var mCycle: String = ""
    lateinit var set: BarDataSet
    var firstIn = true

    fun initPages(data: ArrayList<BarSourceData>, cycle: String): ArrayList<ChartPage> {
        var curPage = 0
        var lastMonth = ""
        var lastYear = ""
        var pages = ArrayList<ChartPage>()
        for (i in data.indices) {
            when (cycle) {
                TrendCommonCandleChart.CYCLE_MONTH -> {
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
                TrendCommonCandleChart.CYCLE_YEAR -> {
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

    fun initChartValues(data: ArrayList<BarSourceData>): ArrayList<BarEntry> {
        val values = ArrayList<BarEntry>()
        for (i in data.indices) {
            values.add(
                BarEntry(
                    i.toFloat(),
                    data[i].value, data[i]
                )
            )
        }
        return values
    }

    fun initChartXLabel(data: ArrayList<BarSourceData>) {
        var xLabelOffset = 0
        if (mCycle == CYCLE_MONTH) {
            xLabelOffset = 7
        } else {
            xLabelOffset = 1
        }
        chart.xAxis.removeAllLimitLines()
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
            if (i == 0 && mCycle == TrendCommonCandleChart.CYCLE_YEAR) {
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

    fun initDateSelectBottomSheetDialog() {
        bottomSheetDialog = TrendChartDateSelectFragment()
        bottomSheetDialog?.setBgColor(mBottomSheetBg)
        bottomSheetDialog?.setTextColor(mTextColor)
        bottomSheetDialog?.setDividerColor(mGridLineColor)
        val items = getDateSelectList()
        bottomSheetDialog?.setItems(items, fun(selectDate) {
            val dates = selectDate.split("-")
            when (mCycle) {
                CYCLE_MONTH -> {
                    if (dates.size > 1) {
                        setCurMonthData(dates[0], dates[1])
                    }
                }
                CYCLE_YEAR -> {
                    if (dates.isNotEmpty()) {
                        setCurYearData(dates[0])
                    }
                }
            }
        })
    }

    fun initChartDateSelectView() {
        initDateSelectBottomSheetDialog()
        tv_date.setOnClickListener {
            bottomSheetDialog?.show(
                (context as FragmentActivity).supportFragmentManager,
                TrendChartDateSelectFragment.TAG
            )
        }
        val lp = ll_chart.layoutParams as RelativeLayout.LayoutParams
        if (isFullScreen) {
            lp.topMargin = 16f.dp().toInt()
            rl_date_container.visibility = View.GONE
            tv_date_fullscreen.visibility = View.VISIBLE
        } else {
            lp.topMargin = 0f.dp().toInt()
            rl_date_container.visibility = View.VISIBLE
            tv_date_fullscreen.visibility = View.GONE
        }
        ll_chart.layoutParams = lp
        initPopupWindow()
        iv_more.imageTintList = ColorStateList.valueOf(mIconColor)
        tv_date.backgroundTintList = ColorStateList.valueOf(mDateBgColor)
        tv_date.setTextColor(mTextColor)
        tv_date.iconTint = ColorStateList.valueOf(mTextColor)
        tv_date_fullscreen.setTextColor(mTextColor)
    }

    fun initPopupWindow() {
        val listPopupWindowButton = iv_more
        listPopupWindowButton?.visibility = View.VISIBLE
        val listPopupWindow = ListPopupWindow(context!!, null)

        listPopupWindow.anchorView = listPopupWindowButton
        val adapter = ChartMoreListAdapter(context, getMenuListData())
        adapter.setTextColor(mTextColor)
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setContentWidth(150f.dp().toInt())

        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            if (position == 0) {
                listPopupWindow.dismiss()
                fullScreen()
            }
        }

        listPopupWindowButton?.setOnClickListener { v: View? ->
            listPopupWindow.show()
        }
    }


    fun getMenuListData(): ArrayList<ChartMoreListAdapter.MenuItem> {
        val lists = ArrayList<ChartMoreListAdapter.MenuItem>()
        val menuItem = ChartMoreListAdapter.MenuItem()
        menuItem.text = context.getString(R.string.expand)
        menuItem.iconRes = R.drawable.vector_drawable_full_screen
        lists.add(menuItem)
        return lists
    }

    fun getDateSelectList(): List<String> {
        val dateSelectList = ArrayList<String>()
        var lastDate: String? = null
        for (i in mData.indices) {
            val curDate = if (mCycle == CYCLE_MONTH) {
                val dates = mData[i].date.split("-")
                "${dates[0]}-${dates[1]}"
            } else {
                val dates = mData[i].date.split("-")
                "${dates[0]}"
            }
            if (curDate != lastDate) {
                dateSelectList.add(curDate)
            }
            lastDate = curDate
        }
        return dateSelectList
    }
    var curYear:String? = null
    var curMonth:String? = null
    fun setCurMonthData(year: String, month: String) {
        curYear = year
        curMonth = month
        val curMonthData =
            mData.filter {
                it.date.split("-")[1] == month &&
                        it.date.split(
                            "-"
                        )[0] == year
            } as ArrayList
        val yearInt = Integer.parseInt(year)
        val monthInt = Integer.parseInt(month)
        val curMonthTotalDays = getMonthLastDay(yearInt, monthInt)
        val deltaDays = curMonthTotalDays - curMonthData.size
        if (deltaDays != 0) {
            val lastDate = curMonthData[curMonthData.size - 1].date
            val dates = lastDate.split("-")
            if (dates.size > 2) {
                var lastDay = Integer.parseInt(dates[2])
                for (i in 0 until deltaDays) {
                    val barSourceData = BarSourceData()
                    var curDayString = String.format("%02d", ++lastDay)
                    barSourceData.value = 0f
                    barSourceData.date = "${dates[0]}-${dates[1]}-${curDayString}"
                    barSourceData.xLabel = curDayString
                    curMonthData.add(barSourceData)
                }
            }
        }
        setCurData(curMonthData)
    }


    fun setCurYearData(year: String) {
        curYear = year
        val curYearData =
            mData.filter { it.date.split("-")[0] == year } as ArrayList
        val deltaMonths = CHART_X_MAX_COUNT_YEAR - curYearData.size
        if (deltaMonths != 0) {
            val lastDate = curYearData[curYearData.size - 1].date
            val dates = lastDate.split("-")
            if (dates.size > 1) {
                var lastDay = Integer.parseInt(dates[1])
                for (i in 0 until deltaMonths) {
                    val barSourceData = BarSourceData()
                    var curMonthString = String.format("%02d", ++lastDay)
                    barSourceData.value = 0f
                    barSourceData.date = "${dates[0]}-${curMonthString}"
                    barSourceData.xLabel = curMonthString
                    curYearData.add(barSourceData)
                }
            }
        }
        setCurData(curYearData)
    }

    fun getDataLevel(average:Double):String{
        val level = when (average) {
            in 0.0..29.0 -> context.getString(R.string.sdk_report_low)
            in 30.0..69.0 -> context.getString(R.string.sdk_report_nor)
            else -> context.getString(R.string.sdk_report_high)
        }
        return level
    }
    fun setCurData(data: ArrayList<BarSourceData>) {
        this.mDataAverage = data.map { it.value }.average()
        this.mChartVisibleXRangeMaximum = initChartVisibleXRangeMaximum(mCycle)
        this.mValues = initChartValues(data!!)
        initChartXLabel(data)
        set = BarDataSet(mValues, "")
        set.setDrawIcons(true)
        // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set.color = mMainColor
        // customize legend entry
//        set.formLineWidth = 1f
//        set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
//        set.formSize = 15f
        // text size of values
        set.valueTextSize = 9f
        set.setDrawValues(false)
        set.isHighlightEnabled = true
        var barData = BarData()
        barData.addDataSet(set)
//         // set data
        chart.data = barData
        chart.setHighlightColor(mGridLineColor)
        calNiceLabel(data)
        initChart()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(mChartVisibleXRangeMaximum)
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            if (firstIn) {
//                initLowestAndHighestVisibleData()
                translateChartX(chart, -Float.MAX_VALUE)
            }
        }
        updateDateRange(data)
//        initDateRange()
    }

    fun setData(data: ArrayList<BarSourceData>?, cycle: String) {
        when (cycle) {
            CYCLE_MONTH -> {
                tv_title.text = context.getString(R.string.chart_daily_average)
            }
            CYCLE_YEAR -> {
                tv_title.text = context.getString(R.string.chart_monthly_average)
            }
        }
        if (data == null) {
            return
        }
        this.mData = completeSourceData(data, cycle)
        this.mCycle = cycle
        this.mPages = initPages(mData!!, cycle)
        initChartDateSelectView()
        if (isFullScreen){
            if (curYear == null){
                setCurData(getLastCycleData())
            }else{
                if (curMonth != null){
                    setCurMonthData(curYear!!,curMonth!!)
                }else{
                    setCurYearData(curYear!!)
                }
            }
        }else{
            setCurData(getLastCycleData())
        }
    }

    fun getLastCycleData(): ArrayList<BarSourceData> {
        var newListSize = mData.size
        val newList = ArrayList<BarSourceData>()
        when (mCycle) {
            CYCLE_MONTH -> {
                if (mData.size > CHART_X_MAX_COUNT_MONTH) {
                    newListSize = CHART_X_MAX_COUNT_MONTH
                }
            }
            CYCLE_YEAR -> {
                if (mData.size > CHART_X_MAX_COUNT_YEAR) {
                    newListSize = CHART_X_MAX_COUNT_YEAR
                }
            }
        }
        for (i in mData.indices) {
            if (i >= mData.size - newListSize) {
                newList.add(mData[i])
            }
        }
        return newList
    }

    private fun initChartVisibleXRangeMaximum(cycle: String): Float {
        return if (cycle == CYCLE_MONTH) {
            CHART_X_MAX_COUNT_MONTH.toFloat()
        } else {
            CHART_X_MAX_COUNT_YEAR.toFloat()
        }

    }

    private fun calNiceLabel(data: List<BarSourceData>) {
        var min = data.map { it.value }.minOrNull()!!
        var max = data.map { it.value }.maxOrNull()!!
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
        chart.setDrawGridBackground(false)
//        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = false
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        val markViewTitle = if (mCycle == CYCLE_MONTH) {
            if (mIsDataTime) {
                context.getString(R.string.chart_daily_total)
            } else {
                context.getString(R.string.chart_daily_average)
            }
        } else {
            if (mIsDataTime) {
                context.getString(R.string.chart_monthly_total)
            } else {
                context.getString(R.string.chart_monthly_average)
            }
        }
        val marker = BarChartMarkView(context, markViewTitle, mCycle)
        marker.chartView = chart
        marker.setMainColor(mMainColor)
        marker.setTextColor(mTextColor)
        marker.setShowLevel(mShowLevel, mLevelTextColor, mLevelBgColor)
        marker.setUnit(mUnit)
        marker.setYOffset(10f.dp())

        chart.marker = marker
        chart.extraTopOffset = 84f
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = mXAxisLineColor
        xAxis.axisLineWidth = 0.5f
        xAxis.gridColor = mGridLineColor
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
//        chart.setMaxVisibleValueCount(100000)
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
        set.setDrawIcons(false)
    }

    fun translateChartX(chart: CustomBarChart, translateX: Float) {
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
        lowestVisibleData = set.getEntryForXValue(chart.lowestVisibleX, 0f)?.data as BarSourceData
        highestVisibleData = set.getEntryForXValue(chart.highestVisibleX, 0f)?.data as BarSourceData
    }

    var downX = 0f
    var downY = 0f
    var moveX = -1f
    var moveY = -1f
    val mainHandler = Handler(Looper.getMainLooper())


//    fun moveToPrePage() {
//        if (curPage == 0) {
//            return
//        }
//        curPage--
//        var prePageIndex = mPages[curPage].firstDataIndex
//        updateDateRange(prePageIndex)
//        chart.moveViewToAnimated(prePageIndex - 0.5f, 0f, YAxis.AxisDependency.LEFT, 500)
//    }

    @SuppressLint("SetTextI18n")
    fun updateDateRange(data: ArrayList<BarSourceData>) {
        lowestVisibleData = data[0]
        highestVisibleData = data[data.size - 1]
        if (mCycle == CYCLE_MONTH) {
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
            tv_date_fullscreen.text = "${
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
            tv_date_fullscreen.text = "${
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
            data.filter { it.value != 0f }.map { it.value }.average() ?: 0.0
        tv_value.text = "${ceil(showDataAverage).toInt()}"
        tv_level.text = getDataLevel(showDataAverage)
    }

//    fun moveToNextPage() {
//        if (curPage == mPages.size - 1) {
//            return
//        }
//        curPage++
//        var nextPageIndex = mPages[curPage].firstDataIndex
//        updateDateRange(nextPageIndex)
//        chart.moveViewToAnimated(nextPageIndex - 0.5f, 0f, YAxis.AxisDependency.LEFT, 500)
//    }

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
//                set2.setDrawVerticalHighlightIndicator(true)
//                set2.setDrawHorizontalHighlightIndicator(false)
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
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                cancelHighlight()
            }

            override fun onValueSelected(e: Entry, h: Highlight?) {
                ll_title.visibility = View.INVISIBLE
                chart.highlightValue(null, false)
                set.setDrawIcons(false)
                set.iconsOffset = MPPointF(0f, 3f)
                set.values.forEach {
                    it.icon = null
                }
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

    class BarSourceData : Serializable {
        var value: Float = 0f
        var date: String = ""
        var xLabel: String = ""
    }
}