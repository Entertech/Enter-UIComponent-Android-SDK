package cn.entertech.uicomponentsdk.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.BrainwaveTrendChartFullScreenActivity
import cn.entertech.uicomponentsdk.activity.SessionBrainwaveStackChartFullScreenActivity
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
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.tv_date
import kotlinx.android.synthetic.main.layout_card_brainwave_trend_chart.view.tv_title
import java.io.Serializable
import kotlin.math.roundToInt

class SessionBrainwaveStackChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mLegendBgColor: Int = Color.parseColor("#F6F7FA")
    private var mLegendUnselectTextColor: Int = Color.parseColor("#878894")
    private var mStartTime: String = ""
    private lateinit var marker: SessionBrainwaveChartMarkView
    private var sampleData: ArrayList<ArrayList<Double>>? = null
    private lateinit var mData: List<ArrayList<Double>>
    private var dataTotalTimeMs: Int = 0
    private lateinit var checkIndexList: java.util.ArrayList<Int>
    private var mFillColorArray: List<String>? = null
    private var mFillColors: String? = ""
    private var mUnit: String? = ""
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
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
    private var mBg: Drawable? = null

    private var mTitleMenuIcon: Drawable?

    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    var mSelfView: View? = null

    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 700
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
            R.styleable.SessionBrainwaveStackChart
        )
        mMainColor =
            typeArray.getColor(R.styleable.SessionBrainwaveStackChart_sbsc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.SessionBrainwaveStackChart_sbsc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.SessionBrainwaveStackChart_sbsc_background)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.SessionBrainwaveStackChart_sbsc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.SessionBrainwaveStackChart_sbsc_titleMenuIcon)

        mGridLineColor =
            typeArray.getColor(
                R.styleable.SessionBrainwaveStackChart_sbsc_gridLineColor,
                mGridLineColor
            )
        mLineWidth =
            typeArray.getDimension(
                R.styleable.SessionBrainwaveStackChart_sbsc_lineWidth,
                mLineWidth
            )

        mHighlightLineColor = typeArray.getColor(
            R.styleable.SessionBrainwaveStackChart_sbsc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.SessionBrainwaveStackChart_sbsc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.SessionBrainwaveStackChart_sbsc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.SessionBrainwaveStackChart_sbsc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.SessionBrainwaveStackChart_sbsc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.SessionBrainwaveStackChart_sbsc_markViewValueColor,
            mMarkViewValueColor
        )
        mXAxisLineColor =
            typeArray.getColor(
                R.styleable.SessionBrainwaveStackChart_sbsc_xAxisLineColor,
                mXAxisLineColor
            )
        mFillColors = typeArray.getString(R.styleable.SessionBrainwaveStackChart_sbsc_fillColors)
        mFillColorArray = mFillColors?.split(",")
        mLegendBgColor =
            typeArray.getColor(R.styleable.SessionBrainwaveStackChart_sbsc_legendBgColor, mLegendBgColor)
        mLegendUnselectTextColor = typeArray.getColor(
            R.styleable.SessionBrainwaveStackChart_sbsc_legendUnselectTextColor,
            mLegendUnselectTextColor
        )
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
        refreshChart()
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
        tv_title.setTextColor(mTextColor)
        iv_menu.setImageDrawable(mTitleMenuIcon)
        iv_menu.setOnClickListener {
            if (isFullScreen) {
                (context as Activity).finish()
            } else {
                var intent =
                    Intent(context, SessionBrainwaveStackChartFullScreenActivity::class.java)
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
                intent.putExtra("xAxisLineColor", mXAxisLineColor)
                intent.putExtra("fillColors", mFillColors)
                intent.putExtra("gammaData", mData!![0].toDoubleArray())
                intent.putExtra("betaData", mData!![1].toDoubleArray())
                intent.putExtra("alphaData", mData!![2].toDoubleArray())
                intent.putExtra("thetaData", mData!![3].toDoubleArray())
                intent.putExtra("deltaData", mData!![4].toDoubleArray())
                intent.putExtra("startTime", mStartTime)
                context.startActivity(intent)
            }
        }
    }

    fun completeSourceData(
        sourceData: ArrayList<BrainwaveLineSourceData>,
        cycle: String
    ): ArrayList<BrainwaveLineSourceData> {
        var firstData = sourceData[0]
        when (cycle) {
            TrendCommonCandleChart.CYCLE_MONTH -> {
                var date = firstData.date
                var day = date.split("-")[2]
                if (day != "01") {
                    var preData = ArrayList<BrainwaveLineSourceData>()
                    var dayIntValue = Integer.parseInt(day)
                    for (j in 1 until dayIntValue) {
                        var curDayString = String.format("%02d", j)
                        var barSourceData = BrainwaveLineSourceData()
                        barSourceData.gamma = 20f
                        barSourceData.beta = 20f
                        barSourceData.alpha = 20f
                        barSourceData.theta = 20f
                        barSourceData.delta = 20f
                        barSourceData.date = "${day[0]}-${day[1]}-${curDayString}"
                        barSourceData.xLabel = curDayString
                        preData.add(barSourceData)
                    }
                    sourceData.addAll(0, preData)
                }
            }
            TrendCommonCandleChart.CYCLE_YEAR -> {
                var date = firstData.date
                var month = date.split("-")[1]
                if (month != "01") {
                    var preData = ArrayList<BrainwaveLineSourceData>()
                    var monthIntValue = Integer.parseInt(month)
                    for (j in 1 until monthIntValue) {
                        var curMonthString = String.format("%02d", j)
                        var barSourceData = BrainwaveLineSourceData()
                        barSourceData.gamma = 20f
                        barSourceData.beta = 20f
                        barSourceData.alpha = 20f
                        barSourceData.theta = 20f
                        barSourceData.delta = 20f
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

    var mValues = ArrayList<ArrayList<Entry>>()
    private var mCycle: String = ""


    fun initChartValues(brainwaveDataList: ArrayList<ArrayList<Double>>): ArrayList<ArrayList<Entry>> {
        checkIndexList = ArrayList<Int>()
        val valuesList = ArrayList<ArrayList<Entry>>()
        for (j in legendIsCheckList.indices) {
            if (legendIsCheckList[j]) {
                checkIndexList.add(j)
                valuesList.add(ArrayList())
            }
        }

        for (i in brainwaveDataList[0].indices) {
            var sum = 0f
            for (j in checkIndexList.indices) {
                val brainwaveData = brainwaveDataList[checkIndexList[j]]
                sum += brainwaveData[i].toFloat()
            }

            for (j in checkIndexList.indices) {
                var temSum = sum
                if (j != 0) {
                    for (z in 0 until j) {
                        temSum -= brainwaveDataList[checkIndexList[z]][i].toFloat()
                    }
                }
                valuesList[j].add(
                    Entry(
                        i.toFloat(),
                        temSum
                    )
                )
            }
        }
        return valuesList
    }

    var yLimitLineValues = listOf(25f, 50f, 75f)
    fun setData(brainwaveSpectrums: List<ArrayList<Double>>?, isShowAllData: Boolean = false) {
        if (brainwaveSpectrums == null) {
            return
        }
        this.dataTotalTimeMs = brainwaveSpectrums[0].size * mTimeUnit
        this.mData = brainwaveSpectrums
        val gammaValueAverage = brainwaveSpectrums[0].average().roundToInt()
        val betaValueAverage = brainwaveSpectrums[1].average().roundToInt()
        val alphaValueAverage = brainwaveSpectrums[2].average().roundToInt()
        val thetaValueAverage = brainwaveSpectrums[3].average().roundToInt()
        tv_value_gamma.text = "$gammaValueAverage"
        tv_value_beta.text = "$betaValueAverage"
        tv_value_alpha.text = "$alphaValueAverage"
        tv_value_theta.text = "$thetaValueAverage"
        tv_value_delta.text =
            "${100 - gammaValueAverage - betaValueAverage - alphaValueAverage - thetaValueAverage}"
        fixData()
        var sample = brainwaveSpectrums[0].size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        sampleData = ArrayList<ArrayList<Double>>()
        var gammaAverage = ArrayList<Double>()
        var betaAverage = ArrayList<Double>()
        var alphaAverage = ArrayList<Double>()
        var thetaAverage = ArrayList<Double>()
        var deltaAverage = ArrayList<Double>()

        for (i in brainwaveSpectrums[0].indices) {

            if (i % sample == 0) {
                gammaAverage.add(brainwaveSpectrums[0][i])
                betaAverage.add(brainwaveSpectrums[1][i])
                alphaAverage.add(brainwaveSpectrums[2][i])
                thetaAverage.add(brainwaveSpectrums[3][i])
                deltaAverage.add(brainwaveSpectrums[4][i])
            }
        }
        sampleData?.add(gammaAverage)
        sampleData?.add(betaAverage)
        sampleData?.add(alphaAverage)
        sampleData?.add(thetaAverage)
        sampleData?.add(deltaAverage)

        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = brainwaveSpectrums[0].size * mTimeUnit / 1000F / 60F
        var minOffset = (totalMin / 8).toInt() + 1
        var currentMin = 0
        while (currentMin < totalMin) {
            var limitX = currentMin * 60f * 1000 / mTimeOfTwoPoint
            val llXAxis = LimitLine(limitX, "${currentMin}m")
            llXAxis.lineWidth = 0.5f
            llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            llXAxis.textSize = 12f
            llXAxis.yOffset = -15f
            llXAxis.enableDashedLine(10f, 10f, 0f)
            llXAxis.lineColor = mGridLineColor
            llXAxis.textColor = mTextColor
            if (currentMin == 0) {
                llXAxis.xOffset = -12f
            } else if (currentMin < totalMin && currentMin > totalMin * 7f / 8) {
                llXAxis.xOffset = 5f
            } else {
                llXAxis.xOffset = -1f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }
        refreshChart()
    }

    fun fixData() {
        if (this.mData != null && this.mData!!.isNotEmpty()) {
            for (i in this.mData!![0].indices) {
                if (this.mData!![0][i] + this.mData!![1][i] + this.mData!![2][i] + this.mData!![3][i] + this.mData!![4][i] < 0.9) {
                    if (i != 0) {
                        this.mData!![0][i] = this.mData!![0][i - 1]
                        this.mData!![1][i] = this.mData!![1][i - 1]
                        this.mData!![2][i] = this.mData!![2][i - 1]
                        this.mData!![3][i] = this.mData!![3][i - 1]
                        this.mData!![4][i] = this.mData!![4][i - 1]
                    } else {
                        for (j in this.mData!![0].indices) {
                            if (this.mData!![0][j] + this.mData!![1][j] + this.mData!![2][j] + this.mData!![3][j] + this.mData!![4][j] >= 0.9) {
                                this.mData!![0][0] = this.mData!![0][j]
                                this.mData!![1][0] = this.mData!![1][j]
                                this.mData!![2][0] = this.mData!![2][j]
                                this.mData!![3][0] = this.mData!![3][j]
                                this.mData!![4][0] = this.mData!![4][j]
                            }
                        }
                    }
                }
            }
        }
    }

    fun refreshChart() {
        this.mValues = initChartValues(sampleData!!)
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
                    var label = "${curYEntries[index].toInt()}%"
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
    }

    fun initChart() {
        chart.setBackgroundColor(Color.TRANSPARENT)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
//        chart.setTouchEnabled(true)
//        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
//        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = true
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        val colors = mFillColorArray?.map { Color.parseColor(it) }?.toIntArray()
        if (colors != null) {
            marker = SessionBrainwaveChartMarkView(
                context,
                colors,
                mGridLineColor,
                mStartTime
            )
            marker.chartView = chart
            marker.setTextColor(mTextColor)
            marker.setMarkViewBgColor(mMarkViewBgColor)
            if (chart.data != null) {
                marker.setDataSets(chart.data.dataSets)
            }
            chart.marker = marker
        }
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
        yAxis.gridLineWidth = 1f
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
//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                val index = yAxis.mEntries.indexOf(value)
//                if (index == -1) {
//                    return ""
//                } else {
//                    val label = currentYLabels[checkIndexList[index]]
//                    return label
//                }
//            }
//        }
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setDrawGridLines(false)
        setChartListener()
    }

    fun cancelHighlight() {
        ll_title.visibility = View.VISIBLE
        chart.highlightValue(null)
        chart?.data?.dataSets?.forEach {
            it.setDrawIcons(false)
        }
    }

    var isDrag = false
    fun setChartListener() {
        chart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                isDrag = false
                chart.isDragEnabled = true
                chart.isHighlightPerDragEnabled = false
                cancelHighlight()
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                isDrag = true
                cancelHighlight()
            }

            override fun onChartSingleTapped(me: MotionEvent) {
            }

            override fun onChartGestureStart(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                chart.disableScroll()
                marker.setDataSets(chart.data.dataSets)
                chart.data.dataSets.map {
                    it as LineDataSet
                }.forEach {
                    it.setDrawHorizontalHighlightIndicator(false)
                    it.setDrawVerticalHighlightIndicator(true)
                }
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartLongPressed(me: MotionEvent) {
                if (isDrag) {
                    return
                }
                chart.isDragEnabled = false
                chart.isHighlightPerDragEnabled = true
                val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
                chart.highlightValue(highlightByTouchPoint, true)
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            }
        }

        var iconGamma = ChartIconView(context)
        var iconBeta = ChartIconView(context)
        var iconAlpha = ChartIconView(context)
        var icontheta = ChartIconView(context)
        var iconDelta = ChartIconView(context)
        var iconList = listOf<ChartIconView>(iconGamma, iconBeta, iconAlpha, icontheta, iconDelta)
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                cancelHighlight()
            }

            override fun onValueSelected(e: Entry, h: Highlight?) {
                chart.highlightValue(null, false)
                ll_title.visibility = View.GONE
                for (i in iconList.indices) {
                    iconList[i].color =
                        mFillColorArray!!.map { Color.parseColor(it) }.toIntArray()[i]
                }
                var iconDrawables = iconList.map { it.toDrawable(context) }
                for (i in chart.data.dataSets.indices) {
                    chart.data.dataSets[i].setDrawIcons(true)
                    chart.data.dataSets[i].iconsOffset = MPPointF(0f, 0f)
                    (chart.data.dataSets[i] as LineDataSet).values.forEach {
                        if (it.x == e.x) {
                            it.icon = iconDrawables[i]
                        } else {
                            it.icon = null
                        }
                    }
                }
                chart.highlightValue(h, false)
            }

        })
    }

    fun setStartTime(startTime: String) {
        mStartTime = startTime
        var startTimestamp = TimeUtils.getStringToDate(startTime, "yyyy-MM-dd HH:mm:ss")
        var endTimestamp = startTimestamp + dataTotalTimeMs
        var startTimeDay = TimeUtils.getFormatTime(startTimestamp, "MMM dd, yyyy")
        var endTimeDay = TimeUtils.getFormatTime(endTimestamp, "MMM dd, yyyy")
        var startTimeHourMin = TimeUtils.getFormatTime(startTimestamp, "HH:mm a")
        var endTimeHourMin = TimeUtils.getFormatTime(endTimestamp, "HH:mm a")
        if (startTimeDay == endTimeDay) {
            tv_date.text = "$startTimeDay ${startTimeHourMin}-${endTimeHourMin}"
        } else {
            tv_date.text = "$startTimeDay ${startTimeHourMin}-$endTimeDay ${endTimeHourMin} "
        }
        initView()
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

    fun setLegendShowList(lists: List<Boolean>) {
        this.legendIsCheckList = lists
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