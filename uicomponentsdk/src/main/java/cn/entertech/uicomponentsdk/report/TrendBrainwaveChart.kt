package cn.entertech.uicomponentsdk.report

import android.animation.Animator
import android.animation.ValueAnimator
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
import cn.entertech.uicomponentsdk.activity.BrainwaveTrendChartFullScreenActivity
import cn.entertech.uicomponentsdk.fragment.TrendChartDateSelectFragment
import cn.entertech.uicomponentsdk.report.TrendCommonCandleChart.Companion.CYCLE_MONTH
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
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.chart
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_alpha
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_beta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_delta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_gamma
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_theta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.ll_legend_parent
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.ll_title
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.*
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.iv_menu
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.iv_more
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.rl_date_container
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.tv_date
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.tv_date_fullscreen
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.tv_title
import java.io.Serializable

class TrendBrainwaveChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mBottomSheetBg: Int = Color.parseColor("#ffffff")
    private var mIconColor: Int = Color.parseColor("#cccccc")
    private var mDateBgColor: Int = Color.parseColor("#cccccc")
    private var bottomSheetDialog: TrendChartDateSelectFragment? = null
    private var mLegendBgColor: Int = Color.parseColor("#F6F7FA")
    private var mLegendUnselectTextColor: Int = Color.parseColor("#878894")
    private lateinit var checkIndexList: java.util.ArrayList<Int>
    private var mFillColorArray: List<String>? = null
    private var mFillColors: String? = ""
    private var mDeltaAverage: Int = 0
    private var mThetaAverage: Int = 0
    private var mAlphaAverage: Int = 0
    private var mBetaAverage: Int = 0
    private var mGammaAverage: Int = 0
    private var mUnit: String? = ""
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
    private var mChartVisibleXRangeMaximum: Int = 0
    private lateinit var highestVisibleData: BrainwaveLineSourceData
    private lateinit var lowestVisibleData: BrainwaveLineSourceData
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

    var legendIsCheckList = listOf(true, true, true, true, true)

    //    var yLabels  =  mutableListOf("δ","θ","α","β","γ")
    val yLabels = mutableListOf("γ", "β", "α", "θ", "δ")
    var currentYLabels = mutableListOf("γ", "δ", "θ", "α", "β")
    private lateinit var drawableIcon: Drawable
    private var mAverageLineColor: Int = Color.parseColor("#11152E")
    private var mAverageValue: String = "0"
    private var mXAxisUnit: String? = "Time(min)"
    private var mLineWidth: Float = 1.5f
    private var mLineColor: Int = Color.RED
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
    private var mIsTitleMenuIconShow: Boolean = true
    private var mData = ArrayList<BrainwaveLineSourceData>()
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
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_card_brainwave_trend_chart, null)
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
            R.styleable.TrendBrainwaveChart
        )
        mMainColor =
            typeArray.getColor(R.styleable.TrendBrainwaveChart_tbc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.TrendBrainwaveChart_tbc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.TrendBrainwaveChart_tbc_background)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.TrendBrainwaveChart_tbc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.TrendBrainwaveChart_tbc_titleMenuIcon)

        mGridLineColor =
            typeArray.getColor(
                R.styleable.TrendBrainwaveChart_tbc_gridLineColor,
                mGridLineColor
            )
        mLineWidth =
            typeArray.getDimension(R.styleable.TrendBrainwaveChart_tbc_lineWidth, mLineWidth)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.TrendBrainwaveChart_tbc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.TrendBrainwaveChart_tbc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_markViewValueColor,
            mMarkViewValueColor
        )
        mXAxisLineColor =
            typeArray.getColor(
                R.styleable.TrendBrainwaveChart_tbc_xAxisLineColor,
                mXAxisLineColor
            )
        mFillColors = typeArray.getString(R.styleable.TrendBrainwaveChart_tbc_fillColors)
        mFillColorArray = mFillColors?.split(",")
        mLegendBgColor =
            typeArray.getColor(R.styleable.TrendBrainwaveChart_tbc_legendBgColor, mLegendBgColor)
        mLegendUnselectTextColor = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_legendUnselectTextColor,
            mLegendUnselectTextColor
        )
        mBottomSheetBg = typeArray.getColor(
            R.styleable.TrendBrainwaveChart_tbc_bottomSheetBgColor,
            mBottomSheetBg
        )
        mIconColor = typeArray.getColor(R.styleable.TrendBrainwaveChart_tbc_iconColor, mIconColor)
        mDateBgColor =
            typeArray.getColor(R.styleable.TrendBrainwaveChart_tbc_dateBgColor, mDateBgColor)

        typeArray.recycle()
        initView()
    }


    fun initView() {
        initBg()
        initTitle()
        initChart()
        initChartIcon()
        initLegend()
    }

    fun initLegend() {
        if (mFillColorArray != null) {
            legend_gamma.setSelectTextColor(Color.parseColor(mFillColorArray!![0]))
            legend_beta.setSelectTextColor(Color.parseColor(mFillColorArray!![1]))
            legend_alpha.setSelectTextColor(Color.parseColor(mFillColorArray!![2]))
            legend_theta.setSelectTextColor(Color.parseColor(mFillColorArray!![3]))
            legend_delta.setSelectTextColor(Color.parseColor(mFillColorArray!![4]))
            legend_gamma.setBgColor(mLegendBgColor)
            legend_beta.setBgColor(mLegendBgColor)
            legend_alpha.setBgColor(mLegendBgColor)
            legend_theta.setBgColor(mLegendBgColor)
            legend_delta.setBgColor(mLegendBgColor)
            legend_gamma.setUnselectTextColor(mLegendUnselectTextColor)
            legend_beta.setUnselectTextColor(mLegendUnselectTextColor)
            legend_alpha.setUnselectTextColor(mLegendUnselectTextColor)
            legend_theta.setUnselectTextColor(mLegendUnselectTextColor)
            legend_delta.setUnselectTextColor(mLegendUnselectTextColor)
        }
        for (i in legendIsCheckList.indices) {
            (ll_legend_parent.getChildAt(i) as OptionalBrainChartLegendView).setCheck(
                legendIsCheckList[i]
            )
        }
        legend_gamma.setOnClickListener {
            if (isLegendClickable() || !legend_gamma.mIsChecked) {
                legend_gamma.setCheck(!legend_gamma.mIsChecked)
                initChartIsShowList()
            }
        }
        legend_beta.setOnClickListener {
            if (isLegendClickable() || !legend_beta.mIsChecked) {
                legend_beta.setCheck(!legend_beta.mIsChecked)
                initChartIsShowList()
            }
        }
        legend_alpha.setOnClickListener {
            if (isLegendClickable() || !legend_alpha.mIsChecked) {
                legend_alpha.setCheck(!legend_alpha.mIsChecked)
                initChartIsShowList()
            }
        }
        legend_theta.setOnClickListener {
            if (isLegendClickable() || !legend_theta.mIsChecked) {
                legend_theta.setCheck(!legend_theta.mIsChecked)
                initChartIsShowList()
            }
        }
        legend_delta.setOnClickListener {
            if (isLegendClickable() || !legend_delta.mIsChecked) {
                legend_delta.setCheck(!legend_delta.mIsChecked)
                initChartIsShowList()
            }
        }
    }

    fun isLegendClickable(): Boolean {
        var isCheckCount = 0
        for (isChecked in legendIsCheckList) {
            if (isChecked) {
                isCheckCount++
            }
        }
        return isCheckCount > 1
    }

    fun initChartIsShowList() {
        legendIsCheckList = listOf(
            legend_gamma.mIsChecked,
            legend_beta.mIsChecked,
            legend_alpha.mIsChecked,
            legend_theta.mIsChecked,
            legend_delta.mIsChecked
        )
        refreshData()
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
//        tv_value.setTextColor(mMainColor)
        tv_date.setTextColor(mTextColor)
        tv_date_fullscreen.setTextColor(mTextColor)
        tv_title.setTextColor(mTextColor)
        if (isFullScreen){
            iv_menu.visibility = View.VISIBLE
        }else{
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

    fun fullScreen(){
        var intent = Intent(context, BrainwaveTrendChartFullScreenActivity::class.java)
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
        intent.putExtra("xAxisLineColor", mXAxisLineColor)
        intent.putExtra("fillColors", mFillColors)
        intent.putExtra("curMonth", curMonth)
        intent.putExtra("curYear", curYear)
        context.startActivity(intent)
    }

    fun fillPreDataWhenZero(
        sourceData: ArrayList<BrainwaveLineSourceData>
    ): ArrayList<BrainwaveLineSourceData> {
        if (sourceData.isEmpty()) {
            return sourceData
        }
        var firstValidValue: BrainwaveLineSourceData? = null
        var preValidValue: BrainwaveLineSourceData? = null
        var validValueList = sourceData.filter { it.alpha != 0f }
        if (validValueList.isNullOrEmpty()) {
            return sourceData
        }
        if (validValueList.isNotEmpty()) {
            firstValidValue = validValueList[0]
        }
        for (i in sourceData.indices) {
            if (sourceData[i].alpha == 0f) {
                if (i == 0) {
                    sourceData[i].gamma = firstValidValue!!.gamma
                    sourceData[i].alpha = firstValidValue.alpha
                    sourceData[i].beta = firstValidValue.beta
                    sourceData[i].delta = firstValidValue.delta
                    sourceData[i].theta = firstValidValue.theta
                    preValidValue = firstValidValue
                } else {
                    sourceData[i].gamma = preValidValue!!.gamma
                    sourceData[i].alpha = preValidValue.alpha
                    sourceData[i].beta = preValidValue.beta
                    sourceData[i].delta = preValidValue.delta
                    sourceData[i].theta = preValidValue.theta
                    preValidValue = sourceData[i]
                }
            } else {
                preValidValue = sourceData[i]
            }
        }
        return sourceData
    }

    fun completeSourceData(
        sourceData: ArrayList<BrainwaveLineSourceData>,
        cycle: String
    ): ArrayList<BrainwaveLineSourceData> {
        var firstData = sourceData[0]
        when (cycle) {
            TrendCommonCandleChart.CYCLE_MONTH -> {
                var date = firstData.date
                val days = date.split("-")
                var day = days[2]
                if (day != "01") {
                    var preData = ArrayList<BrainwaveLineSourceData>()
                    var dayIntValue = Integer.parseInt(day)
                    for (j in 1 until dayIntValue) {
                        var curDayString = String.format("%02d", j)
                        var barSourceData = BrainwaveLineSourceData()
                        barSourceData.gamma = 0f
                        barSourceData.beta = 0f
                        barSourceData.alpha = 0f
                        barSourceData.theta = 0f
                        barSourceData.delta = 0f
                        barSourceData.date = "${days[0]}-${days[1]}-${curDayString}"
                        barSourceData.xLabel = curDayString
                        preData.add(barSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
            TrendCommonCandleChart.CYCLE_YEAR -> {
                var date = firstData.date
                var dates = date.split("-")
                var month = dates[1]
                if (month != "01") {
                    var preData = ArrayList<BrainwaveLineSourceData>()
                    var monthIntValue = Integer.parseInt(month)
                    for (j in 1 until monthIntValue) {
                        var curMonthString = String.format("%02d", j)
                        var barSourceData = BrainwaveLineSourceData()
                        barSourceData.gamma = 0f
                        barSourceData.beta = 0f
                        barSourceData.alpha = 0f
                        barSourceData.theta = 0f
                        barSourceData.delta = 0f
                        barSourceData.date = "${dates[0]}-${curMonthString}"
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

    var mValues = ArrayList<ArrayList<Entry>>()
    private var mCycle: String = ""
    var firstIn = true

    fun initPages(data: ArrayList<BrainwaveLineSourceData>, cycle: String): ArrayList<ChartPage> {
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

    fun initChartValues(data: ArrayList<BrainwaveLineSourceData>): ArrayList<ArrayList<Entry>> {
        val gammaDataList = ArrayList<Double>()
        val betaDataList = ArrayList<Double>()
        val alphaDataList = ArrayList<Double>()
        val thetaDataList = ArrayList<Double>()
        val deltaDataList = ArrayList<Double>()
        val brainwaveDataList = ArrayList<ArrayList<Double>>()
        val valuesList = ArrayList<ArrayList<Entry>>()
        for (i in data.indices) {
            gammaDataList.add(data[i].gamma.toDouble())
            betaDataList.add(data[i].beta.toDouble())
            alphaDataList.add(data[i].alpha.toDouble())
            thetaDataList.add(data[i].theta.toDouble())
            deltaDataList.add(data[i].delta.toDouble())
        }
        brainwaveDataList.add(gammaDataList)
        brainwaveDataList.add(betaDataList)
        brainwaveDataList.add(alphaDataList)
        brainwaveDataList.add(thetaDataList)
        brainwaveDataList.add(deltaDataList)
        checkIndexList = ArrayList<Int>()
        for (j in legendIsCheckList.indices) {
            if (legendIsCheckList[j]) {
                checkIndexList.add(j)
                valuesList.add(ArrayList())
            }
        }

        for (i in data.indices) {
            var sum = 0.0
            for (j in checkIndexList.indices) {
                val brainwaveData = formatData(brainwaveDataList[checkIndexList[j]])
                sum += brainwaveData[i]
            }

            for (j in checkIndexList.indices) {
                var temSum = sum
                if (j != 0) {
                    for (z in 0 until j) {
                        temSum -= formatData(brainwaveDataList[checkIndexList[z]])[i]
                    }
                }
                valuesList[j].add(
                    Entry(
                        i.toFloat(),
                        temSum.toFloat(), data[i]
                    )
                )
            }
        }
        return valuesList
    }

    fun initChartXLabel(data: ArrayList<BrainwaveLineSourceData>) {
        var xLabelOffset = 0
        if (mCycle == CYCLE_MONTH) {
            xLabelOffset = 7
        } else {
            xLabelOffset = 1
        }
        chart.xAxis.removeAllLimitLines()
        for (i in data.indices) {
            if (((i + 1) % xLabelOffset == 0 && i + 1 < data.size)) {
                val llXAxis = LimitLine(i.toFloat() + 0.5f, "${data[i].xLabel}")
                llXAxis.lineWidth = 0.5f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = mGridLineColor
                llXAxis.textColor = mLabelColor
                chart.xAxis.addLimitLine(llXAxis)
            }
            if (i == 0 && mCycle == TrendCommonCandleChart.CYCLE_YEAR) {
                val llXAxis = LimitLine(i.toFloat() - 0.5f, "${data[i].xLabel}")
                llXAxis.lineWidth = 0.5f
                llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                llXAxis.textSize = 12f
                llXAxis.yOffset = -15f
                llXAxis.enableDashedLine(10f, 10f, 0f)
                llXAxis.lineColor = mGridLineColor
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
                TrendCommonBarChart.CYCLE_MONTH -> {
                    if (dates.size > 1) {
                        setCurMonthData(dates[0], dates[1])
                    }
                }
                TrendCommonBarChart.CYCLE_YEAR -> {
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
        val lp = rl_chart.layoutParams as RelativeLayout.LayoutParams
        if (isFullScreen) {
            lp.topMargin = 64f.dp().toInt()
            rl_date_container.visibility = View.GONE
            tv_date_fullscreen.visibility = View.VISIBLE
        } else {
            lp.topMargin = 32f.dp().toInt()
            rl_date_container.visibility = View.VISIBLE
            tv_date_fullscreen.visibility = View.GONE
        }
        rl_chart.layoutParams = lp
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
        val curMonthTotalDays = TimeUtils.getMonthLastDay(yearInt, monthInt)
        val deltaDays = curMonthTotalDays - curMonthData.size
        if (deltaDays != 0) {
            val lastDate = curMonthData[curMonthData.size - 1].date
            val dates = lastDate.split("-")
            if (dates.size > 2) {
                var lastDay = Integer.parseInt(dates[2])
                for (i in 0 until deltaDays) {
                    var curDayString = String.format("%02d", ++lastDay)
                    var barSourceData = BrainwaveLineSourceData()
                    barSourceData.gamma = 0f
                    barSourceData.beta = 0f
                    barSourceData.alpha = 0f
                    barSourceData.theta = 0f
                    barSourceData.delta = 0f
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
        val deltaMonths = TrendCommonBarChart.CHART_X_MAX_COUNT_YEAR - curYearData.size
        if (deltaMonths != 0) {
            val lastDate = curYearData[curYearData.size - 1].date
            val dates = lastDate.split("-")
            if (dates.size > 1) {
                var lastDay = Integer.parseInt(dates[1])
                for (i in 0 until deltaMonths) {
                    var curMonthString = String.format("%02d", ++lastDay)
                    var barSourceData = BrainwaveLineSourceData()
                    barSourceData.gamma = 0f
                    barSourceData.beta = 0f
                    barSourceData.alpha = 0f
                    barSourceData.theta = 0f
                    barSourceData.delta = 0f
                    barSourceData.date = "${dates[0]}-${curMonthString}"
                    barSourceData.xLabel = curMonthString
                    curYearData.add(barSourceData)
                }
            }
        }
        setCurData(curYearData)
    }

    fun setCurData(data: ArrayList<BrainwaveLineSourceData>) {
        this.mGammaAverage = data.map { it.gamma }.filter { it != 0f }.average().toInt()
        this.mBetaAverage = data.map { it.beta }.filter { it != 0f }.average().toInt()
        this.mAlphaAverage = data.map { it.alpha }.filter { it != 0f }.average().toInt()
        this.mThetaAverage = data.map { it.theta }.filter { it != 0f }.average().toInt()
        this.mDeltaAverage = data.map { it.delta }.filter { it != 0f }.average().toInt()
        setBrainwaveText(mGammaAverage, mBetaAverage, mAlphaAverage, mThetaAverage)
        refreshChart(data)
    }

    fun setBrainwaveText(gamma: Int, beta: Int, alpha: Int, theta: Int) {
        if ((gamma == 0 || beta == 0) || (gamma == 20 || beta == 20)) {
            tv_value_gamma.text = "--"
            tv_value_beta.text = "--"
            tv_value_alpha.text = "--"
            tv_value_theta.text = "--"
            tv_value_delta.text = "--"
        } else {
            tv_value_gamma.text = "$gamma"
            tv_value_beta.text = "$beta"
            tv_value_alpha.text = "$alpha"
            tv_value_theta.text = "$theta"
            var delta = 100 - gamma - beta - alpha - theta
            tv_value_delta.text = "$delta"
        }
    }

    var yLimitLineValues = listOf(25f, 50f, 75f)
    fun setData(data: ArrayList<BrainwaveLineSourceData>?, cycle: String) {
        if (data == null) {
            return
        }
        this.mData = completeSourceData(data, cycle)
        this.mCycle = cycle
        this.mPages = initPages(mData!!, cycle)
        this.mChartVisibleXRangeMaximum = initChartVisibleXRangeMaximum(cycle)
        when (mCycle) {
            "month" -> {
                tv_title.text = context.getString(R.string.chart_daily_average_percentage)
            }
            "year" -> {
                tv_title.text = context.getString(R.string.chart_month_average_percentage)
            }
        }
        initChartDateSelectView()
        setInitData()
    }
    fun setInitData(){
        if (isFullScreen){
            refreshData()
        }else{
            setCurData(getLastCycleData())
        }
    }

    fun refreshData(){
        if (curYear == null){
            setCurData(getLastCycleData())
        }else{
            if (curMonth != null){
                setCurMonthData(curYear!!,curMonth!!)
            }else{
                setCurYearData(curYear!!)
            }
        }
    }

    fun getLastCycleData(): ArrayList<BrainwaveLineSourceData> {
        var newListSize = mData.size
        val newList = ArrayList<BrainwaveLineSourceData>()
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

    fun refreshChart(data: ArrayList<BrainwaveLineSourceData>) {
        this.mValues = initChartValues(data)
        initChartXLabel(data)
        for (i in yLimitLineValues.indices) {
            val ll = LimitLine(yLimitLineValues[i], "")
            ll.lineWidth = 0.5f
            ll.enableDashedLine(10f, 10f, 0f)
            ll.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll.textSize = 12f
            ll.xOffset = 10f
            ll.yOffset = 8f
            ll.textColor = mTextColor
            ll.lineColor = mGridLineColor
            chart.axisLeft.addLimitLine(ll)
        }

        var lineData = LineData()
        var lineStartIndex = 0
        var entry = 0f
        val curYEntries = mutableListOf<Float>()
        currentYLabels.clear()
        checkIndexList.reverse()

        for (i in legendIsCheckList.indices) {
            if (legendIsCheckList[i]) {
                currentYLabels!!.add(yLabels[i])
                curYEntries.add(entry)
                entry += 25f
                var set = LineDataSet(mValues[lineStartIndex++], "$i")
                set.setDrawIcons(false)
                // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
                // black lines and points
                set.color = Color.parseColor(mFillColorArray!![i])
                set.highLightColor = mGridLineColor
                set.highlightLineWidth = 2f
                set.setDrawFilled(true)
                set.setDrawHorizontalHighlightIndicator(false)
                set.setDrawVerticalHighlightIndicator(true)
                if (mFillColorArray != null) {
                    set.lineWidth = 2f
                    if (Utils.getSDKInt() >= 18) {
                        var gradientDrawable = GradientDrawable()
                        gradientDrawable.setColor(Color.parseColor(mFillColorArray!![i]))
                        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
                        gradientDrawable.shape = GradientDrawable.RECTANGLE
                        set.fillDrawable = gradientDrawable
                    } else {
                        set.fillColor = Color.parseColor(mFillColorArray!![i])
                    }
                }
                // customize legend entry
                set.formLineWidth = 1f
                set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
                set.formSize = 15f
                set.isHighlightEnabled = true
                // text size of values
                set.valueTextSize = 9f
                set.setDrawCircles(false)
                set.setDrawValues(false)
                lineData.addDataSet(set)

            }
        }
        curYEntries.reverse()
        chart.axisLeft.mEntries = curYEntries.toFloatArray()
        chart.axisLeft.mEntryCount = legendIsCheckList.filter { it }.size
        chart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = curYEntries.indexOf(value)
                if (index == -1) {
                    return ""
                } else {
                    val label = "${curYEntries[index].toInt()}%"
                    return label
                }
            }
        }
//        if (mFillColorArray != null) {
//            chart.setDrawGridBackground(true)
//            chart.setGridBackgroundColor((Color.parseColor(mFillColorArray!![0])))
//        }
//         // set data
        chart.data = lineData
        initChart()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(mChartVisibleXRangeMaximum.toFloat())
        chart.viewTreeObserver.addOnGlobalLayoutListener {
            if (firstIn) {
//                initLowestAndHighestVisibleData()
                translateChartX(chart, -Float.MAX_VALUE)
            }
        }
        updateDateRange(data)
    }

//    fun initDateRange() {
//        if (mData?.size ?: 0 >= mChartVisibleXRangeMaximum) {
//            updateDateRange((mData?.size ?: 0) - mChartVisibleXRangeMaximum)
//            tv_date.visibility = View.VISIBLE
//        } else {
//            tv_date.visibility = View.INVISIBLE
//        }
//    }


    private fun initChartVisibleXRangeMaximum(cycle: String): Int {
        return if (cycle == CYCLE_MONTH) {
            31
        } else {
            12
        }

    }

    fun initChart() {
        chart.setBackgroundColor(Color.TRANSPARENT)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
//        chart.setTouchEnabled(true)
//        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
//        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = false
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        var markViewTitle = if (mCycle == "month") {
            context.getString(R.string.chart_daily_average_percentage)
        } else {
            context.getString(R.string.chart_month_average_percentage)
        }
        val marker = BrainwaveTrendChartMarkView(
            context,
            mFillColorArray?.map { Color.parseColor(it) }?.toIntArray(),
            mGridLineColor,
            markViewTitle, mCycle
        )
        marker.chartView = chart
        marker.setTextColor(mTextColor)
        marker.setMarkViewBgColor(mMarkViewBgColor)
        if (chart.data != null) {
            marker.setDataSets(chart.data.dataSets)
        }
        chart.marker = marker
        chart.extraTopOffset = 72f
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = mXAxisLineColor
        xAxis.axisLineWidth = 0.5f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
        yAxis.setLabelCount(5, false)
        yAxis.gridColor = mGridLineColor
        yAxis.gridLineWidth = 0.5f
        yAxis.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 10f), 0f))
        yAxis.textSize = 12f
        yAxis.textColor = mTextColor
        xAxis.setDrawLimitLinesBehindData(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawLimitLinesBehindData(false)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.mEntries = floatArrayOf(0f, 25f, 50f, 75f, 100f)
        yAxis.mEntryCount = 5
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setDrawGridLines(false)
        chart.setNoDataText("")
        chart.setNoDataTextColor(Color.TRANSPARENT);
        chart.invalidate();
        setChartListener()
    }

    fun cancelHighlight() {
        ll_title.visibility = View.VISIBLE
        chart.highlightValue(null)
        chart?.data?.dataSets?.forEach {
            it.setDrawIcons(false)
        }
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
        lowestVisibleData =
            chart?.data?.dataSets?.get(0)
                ?.getEntryForXValue(chart.lowestVisibleX, 0f)?.data as BrainwaveLineSourceData
        highestVisibleData =
            chart?.data?.dataSets?.get(0)
                ?.getEntryForXValue(chart.highestVisibleX, 0f)?.data as BrainwaveLineSourceData
    }

    var downX = 0f
    var downY = 0f
    var moveX = -1f
    var moveY = -1f
    val mainHandler = Handler(Looper.getMainLooper())


    fun updateDateRange(data: ArrayList<BrainwaveLineSourceData>) {
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
        val curGammaAverage =
            data.map { it.gamma }.filter { it != 0f }.average().toInt()
        val curBetaAverage =
            data.map { it.beta }.filter { it != 0f }.average().toInt()
        val curAlphaAverage =
            data.map { it.alpha }.filter { it != 0f }.average().toInt()
        val curThetaAverage =
            data.map { it.theta }.filter { it != 0f }.average().toInt()
        setBrainwaveText(curGammaAverage, curBetaAverage, curAlphaAverage, curThetaAverage)
    }


    fun setChartListener() {
        chart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
//                chart.isDragEnabled = true
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
//                chart.isDragEnabled = true
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
////                            chart.isDragEnabled = true
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
                chart.data.dataSets.forEach {
                    it.setDrawIcons(true)
                    it.iconsOffset = MPPointF(0f, 3f)
                    (it as LineDataSet).values.forEach {
                        it.icon = null
                    }
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

    fun setXAxisLineColor(color: Int) {
        this.mXAxisLineColor = color
        initView()
    }

    fun setFillColors(colors: String) {
        this.mFillColors = colors
        mFillColorArray = mFillColors?.split(",")
        initView()
    }

    class BrainwaveLineSourceData : Serializable {
        var gamma: Float = 0f
        var beta: Float = 0f
        var alpha: Float = 0f
        var theta: Float = 0f
        var delta: Float = 0f
        var date: String = ""
        var xLabel: String = ""
    }
}