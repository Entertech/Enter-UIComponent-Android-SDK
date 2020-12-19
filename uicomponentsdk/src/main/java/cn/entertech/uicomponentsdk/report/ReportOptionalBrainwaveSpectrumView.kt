package cn.entertech.uicomponentsdk.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.BrainwaveOptionalLineChartActivity
import cn.entertech.uicomponentsdk.utils.dp
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import cn.entertech.uicomponentsdk.utils.toDrawable
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.OptionalBrainChartLegendView
import cn.entertech.uicomponentsdk.widget.OptionalBrainwaveSpectrumChartMarkView
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.*
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.chart
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_alpha
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_beta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_delta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_gamma
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.legend_theta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.ll_title
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_optional.view.tv_unit
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReportOptionalBrainwaveSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIsChartEnable: Boolean = true
    private var mLineWidth: Float = 1f.dp()
    private var sampleData: java.util.ArrayList<java.util.ArrayList<Double>>? = null
    private var mSmallTitle: String? = ""
    var isFullScreen: Boolean = false
    private lateinit var marker: OptionalBrainwaveSpectrumChartMarkView
    private var dataSets: ArrayList<ILineDataSet> = ArrayList()
    var bgColor: Int = Color.WHITE
    private var mXAxisUnit: String? = "Time(min)"
    private var mSelfView: View? = null
    private var mBrainwaveSpectrums: List<ArrayList<Double>>? = null
    private var mTitleText: String? = null
    private var mTitleIcon: Drawable? = null
    private var mTitleMenuIcon: Drawable? = null
    private var mSpectrumColors: List<Int>? = null
    private var mIsShowTitleIcon: Boolean = true
    private var mIsShowTitleMenuIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mIsAbsoluteTime: Boolean = false
    private var mBg: Drawable? = null
    private var mInfoUrl: String? = null
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")

    private var mTitleMenuIconBg: Drawable? = null
    private var mIsTitleMenuIconBgShow: Boolean = true

    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 400
    var mPointCount: Int = 100
    var mTimeOfTwoPoint: Int = 0

    var legendIsCheckList = listOf(true, false, true, false, true)

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
        const val SPECTRUM_COLORS = "#FF6682,#5E75FF,#F7C77E,#5FC695,#FB9C98"
    }

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
    var mHighlightLineColor: Int = Color.parseColor("#DDE1EB")
        set(value) {
            field = value
            initView()
        }
    var mMarkDivideLineColor: Int = Color.parseColor("#9AA1A9")
        set(value) {
            field = value
            initView()
        }


    init {
        mSelfView =
            LayoutInflater.from(context).inflate(R.layout.layout_card_brain_spectrum_optional, null)
        var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mSelfView?.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportOptionalBrainwaveSpectrumView
        )
        mTitleText = typeArray.getString(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_title)
        mXAxisUnit =
            typeArray.getString(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_xAxisUnit)
        mMainColor =
            typeArray.getColor(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_mainColor,
                mMainColor
            )
        mTextColor =
            typeArray.getColor(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_textColor,
                mTextColor
            )
        mBg = typeArray.getDrawable(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_background)
        mSmallTitle =
            typeArray.getString(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_smallTitle)
        mTitleIcon =
            typeArray.getDrawable(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_titleMenuIcon)
        mIsShowTitleIcon =
            typeArray.getBoolean(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_isTitleIconShow,
                true
            )
        mIsShowTitleMenuIcon = typeArray.getBoolean(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_isTitleMenuIconShow,
            true
        )
        mIsTitleMenuIconBgShow = typeArray.getBoolean(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_isShowMenuIconBg,
            mIsTitleMenuIconBgShow
        )
        mTitleMenuIconBg =
            typeArray.getDrawable(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_menuIconBg)

        mLabelColor =
            typeArray.getColor(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_labelColor,
                mLabelColor
            )
        mGridLineColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_gridLineColor,
            mGridLineColor
        )

        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTimeUnit =
            typeArray.getInteger(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_timeUnit,
                mTimeUnit
            )

        mPointCount =
            typeArray.getInteger(
                R.styleable.ReportOptionalBrainwaveSpectrumView_robs_pointCount,
                100
            )
        var color =
            typeArray.getString(R.styleable.ReportOptionalBrainwaveSpectrumView_robs_spectrumColors)
        if (color == null) {
            color = SPECTRUM_COLORS
        }
        mSpectrumColors = color.split(",")
            .map { Color.parseColor(it) }
        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_markViewValueColor,
            mMarkViewValueColor
        )
        mMarkDivideLineColor = typeArray.getColor(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_markViewDivideLineColor,
            mMarkDivideLineColor
        )
        mLineWidth = typeArray.getDimension(
            R.styleable.ReportOptionalBrainwaveSpectrumView_robs_lineWidth,
            mLineWidth
        )

        initView()
    }

    fun initTitle() {
        tv_title.text = mTitleText
        tv_title.setTextColor(mTextColor)
        if (mIsShowTitleIcon) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTitleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mSmallTitle != null) {
            tv_small_title.visibility = View.VISIBLE
            tv_small_title.text = mSmallTitle
            tv_small_title.setTextColor(mTextColor)
        }
        if (mIsShowTitleMenuIcon) {
            iv_menu.visibility = View.VISIBLE
            iv_menu.setImageDrawable(mTitleMenuIcon)
        } else {
            iv_menu.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        if (mIsTitleMenuIconBgShow) {
            rl_corner_icon_bg.visibility = View.VISIBLE
            iv_corner_icon_bg.setImageDrawable(mTitleMenuIconBg)
        } else {
            rl_corner_icon_bg.visibility = View.GONE
        }
        iv_menu.setOnClickListener {
            if (!mIsTitleMenuIconBgShow) {
                if (isFullScreen) {
                    (context as Activity).finish()
                } else {
                    var intent = Intent(context, BrainwaveOptionalLineChartActivity::class.java)
                    intent.putExtra("pointCount", mPointCount)
                    intent.putExtra("timeUnit", mTimeUnit)
                    intent.putExtra("highlightLineColor", mHighlightLineColor)
                    intent.putExtra("highlightLineWidth", mHighlightLineWidth)
                    intent.putExtra("lineWidth", mLineWidth)
                    intent.putExtra("markViewBgColor", mMarkViewBgColor)
                    intent.putExtra("markViewTitleColor", mMarkViewTitleColor)
                    intent.putExtra("markViewValueColor", mMarkViewValueColor)
                    intent.putExtra("spectrumColors", mSpectrumColors?.toIntArray())
                    intent.putExtra("gridLineColor", mGridLineColor)
                    intent.putExtra("xAxisUnit", mXAxisUnit)
                    intent.putExtra("textColor", mTextColor)
                    intent.putExtra("bgColor", bgColor)
                    intent.putExtra("labelColor", mLabelColor)
                    intent.putExtra("gammaData", mBrainwaveSpectrums!![0].toDoubleArray())
                    intent.putExtra("betaData", mBrainwaveSpectrums!![1].toDoubleArray())
                    intent.putExtra("alphaData", mBrainwaveSpectrums!![2].toDoubleArray())
                    intent.putExtra("thetaData", mBrainwaveSpectrums!![3].toDoubleArray())
                    intent.putExtra("deltaData", mBrainwaveSpectrums!![4].toDoubleArray())
                    intent.putExtra("isLegendShowList", legendIsCheckList.toBooleanArray())
                    context.startActivity(intent)
                }
            }
        }

    }

    fun initBg() {
        if (mBg != null) {
            if (mBg is ColorDrawable) {
                bgColor = (mBg as ColorDrawable).color
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bgColor = (mBg as GradientDrawable).color.defaultColor
                }
            }
        }
        if (bgColor != null) {
            rl_bg.setBackgroundColor(bgColor)
        }
    }

    fun initUnit() {
        tv_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_unit.text = mXAxisUnit
        if (mIsAbsoluteTime) {
            tv_unit.visibility = View.GONE
        } else {
            tv_unit.visibility = View.VISIBLE
        }
    }

    fun initLegend() {
        if (mSpectrumColors != null) {
            legend_gamma.setLegendIconColor(mSpectrumColors!![0])
            legend_beta.setLegendIconColor(mSpectrumColors!![1])
            legend_alpha.setLegendIconColor(mSpectrumColors!![2])
            legend_theta.setLegendIconColor(mSpectrumColors!![3])
            legend_delta.setLegendIconColor(mSpectrumColors!![4])
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
        for (isChecked in legendIsCheckList!!) {
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
        chartInvalidate()
    }

    fun initView() {
        initTitle()
        initBg()
        initUnit()
        initChart()
        initLegend()
    }

    fun fixData() {
        if (mBrainwaveSpectrums != null && mBrainwaveSpectrums!!.isNotEmpty()) {
            for (i in mBrainwaveSpectrums!![0]!!.indices) {
                if (mBrainwaveSpectrums!![0][i] + mBrainwaveSpectrums!![1][i] + mBrainwaveSpectrums!![2][i] + mBrainwaveSpectrums!![3][i] + mBrainwaveSpectrums!![4][i] < 0.9) {
                    if (i != 0) {
                        mBrainwaveSpectrums!![0]!![i] = mBrainwaveSpectrums!![0]!![i - 1]
                        mBrainwaveSpectrums!![1]!![i] = mBrainwaveSpectrums!![1]!![i - 1]
                        mBrainwaveSpectrums!![2]!![i] = mBrainwaveSpectrums!![2]!![i - 1]
                        mBrainwaveSpectrums!![3]!![i] = mBrainwaveSpectrums!![3]!![i - 1]
                        mBrainwaveSpectrums!![4]!![i] = mBrainwaveSpectrums!![4]!![i - 1]
                    } else {
                        for (j in mBrainwaveSpectrums!![0]!!.indices) {
                            if (mBrainwaveSpectrums!![0][j] + mBrainwaveSpectrums!![1][j] + mBrainwaveSpectrums!![2][j] + mBrainwaveSpectrums!![3][j] + mBrainwaveSpectrums!![4][j] >= 0.9) {
                                mBrainwaveSpectrums!![0]!![0] = mBrainwaveSpectrums!![0]!![j]
                                mBrainwaveSpectrums!![1]!![0] = mBrainwaveSpectrums!![1]!![j]
                                mBrainwaveSpectrums!![2]!![0] = mBrainwaveSpectrums!![2]!![j]
                                mBrainwaveSpectrums!![3]!![0] = mBrainwaveSpectrums!![3]!![j]
                                mBrainwaveSpectrums!![4]!![0] = mBrainwaveSpectrums!![4]!![j]
                            }
                        }
                    }
                }
            }
        }
    }

    fun setData(brainwaveSpectrums: List<ArrayList<Double>>?, isShowAllData: Boolean = false) {
        if (brainwaveSpectrums == null) {
            return
        }
        this.mBrainwaveSpectrums = brainwaveSpectrums
        fixData()
        var sample = brainwaveSpectrums[0]!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        sampleData = ArrayList<ArrayList<Double>>()
        var gammaAverage = ArrayList<Double>()
        var betaAverage = ArrayList<Double>()
        var alphaAverage = ArrayList<Double>()
        var thetaAverage = ArrayList<Double>()
        var deltaAverage = ArrayList<Double>()

        for (i in brainwaveSpectrums[0]!!.indices) {

            if (i % sample == 0) {
                gammaAverage.add(brainwaveSpectrums[0]!![i])
                betaAverage.add(brainwaveSpectrums[1]!![i])
                alphaAverage.add(brainwaveSpectrums[2]!![i])
                thetaAverage.add(brainwaveSpectrums[3]!![i])
                deltaAverage.add(brainwaveSpectrums[4]!![i])
            }
        }
        sampleData?.add(gammaAverage)
        sampleData?.add(betaAverage)
        sampleData?.add(alphaAverage)
        sampleData?.add(thetaAverage)
        sampleData?.add(deltaAverage)

        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = brainwaveSpectrums[0]!!.size * mTimeUnit / 1000F / 60F
        var minOffset = (totalMin / 8).toInt() + 1
        var currentMin = 0
        while (currentMin < totalMin) {
            var limitX = currentMin * 60f * 1000 / mTimeOfTwoPoint
            val llXAxis = LimitLine(limitX, "$currentMin")
            llXAxis.lineWidth = 1f
            llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            llXAxis.textSize = 12f
            llXAxis.yOffset = -15f
            llXAxis.lineColor = Color.parseColor("#00000000")
            llXAxis.textColor = mLabelColor
            if (currentMin == 0) {
                llXAxis.xOffset = -3f
            } else {
                llXAxis.xOffset = -1f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }

        chartInvalidate()
    }

    var dataSetsLabels = listOf<String>("γ", "β", "α", "θ", "δ")
    fun chartInvalidate() {
        dataSets.clear()
        chart.clear()
        var maxValue: Float? = null
        var minValue: Float? = null
        for (i in 0..4) {
            if (!legendIsCheckList?.get(i)) {
                continue
            }
            val values = ArrayList<Entry>()
            for (j in sampleData!![i].indices) {
                if (maxValue == null) {
                    maxValue = sampleData!![i][0].toFloat()
                }
                if (minValue == null) {
                    minValue = sampleData!![i][0].toFloat()
                }
                if (sampleData!![i][j].toFloat() > maxValue) {
                    maxValue = sampleData!![i][j].toFloat()
                }
                if (sampleData!![i][j].toFloat() < minValue) {
                    minValue = sampleData!![i][j].toFloat()
                }
                values.add(Entry(j.toFloat(), sampleData!![i][j].toFloat()))
            }

            val set1: LineDataSet
            set1 = LineDataSet(values, "$i")
            set1.lineWidth = 0f
            set1.setDrawCircleHole(false)

//            set1.setDrawHighlightIndicators(false)
//            set1.setDrawHorizontalHighlightIndicator(false)
            set1.setDrawVerticalHighlightIndicator(true)
            set1.color = mSpectrumColors!![i]
            set1.highLightColor = mHighlightLineColor
            set1.highlightLineWidth = mHighlightLineWidth
            set1.formLineWidth = 0f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            // text size of values
            set1.valueTextSize = 9f
            set1.setDrawValues(false)
            set1.lineWidth = mLineWidth
            set1.setDrawFilled(false)
            set1.setDrawCircles(false)
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
//            set1.fillAlpha = 255
//            set1.fillColor = mSpectrumColors!![i]
            dataSets.add(set1) // add the data sets
        }
        // create a data object with the data sets
        maxValue = maxValue ?: 100f + 10f
        minValue = minValue ?: 0f
        drawYLimit(maxValue, minValue)
        chart.axisLeft.axisMaximum = maxValue
        chart.axisLeft.axisMinimum = minValue
        val data = LineData(dataSets)
        marker.setDataSets(dataSets)
        // set data
        chart.data = data
        chart.notifyDataSetChanged()
    }

    fun drawYLimit(maxValue: Float, minValue: Float) {
        var yLimitLineDelta = ((maxValue!! - minValue!!) / 4f)

        var yLimitLineValues = listOf<Float>(
            minValue + yLimitLineDelta,
            minValue + 2 * yLimitLineDelta,
            minValue + 3 * yLimitLineDelta,
            minValue + 4 * yLimitLineDelta
        )
        chart.axisLeft.removeAllLimitLines()
        yLimitLineValues.forEach {
            var limitLine: LimitLine? = null
            limitLine = LimitLine(it, "")
            limitLine?.enableDashedLine(10f, 10f, 0f)
            limitLine?.lineWidth = 1f
            limitLine?.yOffset = -4f
            limitLine?.textColor = mLabelColor
            limitLine?.lineColor = mGridLineColor
            chart.axisLeft.addLimitLine(limitLine)
        }
    }

    fun cancelHighlight() {
        ll_title.visibility = View.VISIBLE
        chart.highlightValue(null)
        dataSets.map {
            it as LineDataSet
        }.forEach {
            it.setDrawIcons(false)
        }
    }

    fun setChartListener() {
        chart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
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
                cancelHighlight()
            }

            override fun onChartSingleTapped(me: MotionEvent) {
            }

            override fun onChartGestureStart(
                me: MotionEvent,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                chart.disableScroll()
                marker.setDataSets(dataSets)
                dataSets.map {
                    it as LineDataSet
                }.forEach {
                    it.setDrawHorizontalHighlightIndicator(false)
                    it.setDrawVerticalHighlightIndicator(true)
                    it.highLightColor = mHighlightLineColor
                    it.highlightLineWidth = mHighlightLineWidth
                }
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartLongPressed(me: MotionEvent) {
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
                    iconList[i].color = mSpectrumColors!![i]
                }
                var iconDrawables = iconList.map { it.toDrawable(context) }
                for (i in dataSets.indices) {
                    dataSets[i]?.setDrawIcons(true)
                    dataSets[i]?.iconsOffset = MPPointF(0f, 0f)
                    (dataSets[i]!! as LineDataSet)?.values.forEach {
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

    fun initChart() {
//        chart.setBackgroundColor(bgColor)
        // disable description text

        marker = OptionalBrainwaveSpectrumChartMarkView(
            context,
            mSpectrumColors!!.toIntArray(), mMarkViewValueColor,
            getOpacityColor(mMarkDivideLineColor, 0.3f), mMarkViewTitleColor,
            arrayOf("γ", "β", "α", "θ", "δ")
        )
        marker.setMarkViewBgColor(mMarkViewBgColor)
        marker.chartView = chart
        chart.marker = marker
        chart.getDescription().setEnabled(false)
        chart.legend.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(mIsChartEnable)

        chart.isHighlightPerDragEnabled = false
        chart.setMaxVisibleValueCount(100000)
        chart.setDrawGridBackground(false)
        // enable scaling and dragging
        chart.setDragEnabled(mIsChartEnable)
//        chart.setScaleEnabled(true)
        chart.setScaleXEnabled(mIsChartEnable)
        chart.setScaleYEnabled(mIsChartEnable)
        chart.extraTopOffset = 24f.dp()
        // force pinch zoom along both axis
        chart.setPinchZoom(true)
        val xAxis: XAxis = chart.xAxis
        // vertical grid lines
        xAxis.setDrawAxisLine(true)
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
                Log.d("####", "x entry is " + Arrays.toString(xAxis.mEntries))
                return "${String.format("%.1f", value * 0.8f / 60)}"
            }
        }
        chart.axisRight.isEnabled = false
        // horizontal grid lines
//        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawLabels(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        // axis range


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(false)
        xAxis.setDrawLimitLinesBehindData(false)
        // add limit lines
        setChartListener()
    }

    fun setSpectrumColors(colors: List<Int>?) {
        this.mSpectrumColors = colors
        initView()
    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
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

    fun setBg(bg: Drawable?) {
        this.mBg = bg
        initView()
    }

    fun setTextColor(color: Int) {
        this.mTextColor = color
        initView()
    }

    fun setLineWidth(lineWidth: Float) {
        this.mLineWidth = lineWidth
        initView()
    }

    fun setLegendShowList(lists: List<Boolean>) {
        this.legendIsCheckList = lists
        initView()
    }

    fun isChartEnable(isChartEnable: Boolean) {
        this.mIsChartEnable = isChartEnable
        initView()
    }
}