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
import android.widget.TextView
import androidx.core.view.ViewCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.HRVLineChartFullScreenActivity
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.LineChartMarkView
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.chart
import kotlinx.android.synthetic.main.layout_card_attention.view.iv_corner_icon_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_title
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_corner_icon_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_card_hrv_line_chart.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class ReportHRLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mIsChartEnable: Boolean = true
    private var mIsShowLegend: Boolean = false
    private var mIsDrawYAxisLabels: Boolean = true
    private var mChartExtraTopOffset: Float = 22f.dp()
    private var mIsShowAverage: Boolean = false
    private var mCohTime: String = "--"
    private var set2: LineDataSet? = null
    private var mSampleSecondData: java.util.ArrayList<Double>? = null
    private var mSampleData: ArrayList<Double>? = null
    var isFirstIn = true
    private var set1: LineDataSet? = null
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
    private var mSecondLineColor: Int = Color.GREEN
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
    private var mTitle: String? = "Changes During Meditation"
    private var mIsTitleIconShow: Boolean = false
    private var mIsTitleMenuIconShow: Boolean = true
    private var mIsTitleMenuIconBgShow: Boolean = false
    private var mFirstData: List<Double>? = null
    private var mSecondData: List<Double>? = null
    private var mBg: Drawable? = null

    private var mTitleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?
    private var mTitleMenuIconBg: Drawable?

    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    var mSelfView: View? = null

    companion object {
        const val SECOND_LINE_START_OFFSET_FACTOR = 0.25F
    }

    var secondLineStartIndexOfFirstLine = -1

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
    var isShowDetail = false

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_card_hrv_line_chart, null)
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
            R.styleable.ReportHRLineChartCard
        )
        mMainColor =
            typeArray.getColor(R.styleable.ReportHRLineChartCard_rhlcc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportHRLineChartCard_rhlcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportHRLineChartCard_rhlcc_background)

        mSmallTitle = typeArray.getString(R.styleable.ReportHRLineChartCard_rhlcc_smallTitle)

        mTitle = typeArray.getString(R.styleable.ReportHRLineChartCard_rhlcc_title)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportHRLineChartCard_rhlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mTitleIcon =
            typeArray.getDrawable(R.styleable.ReportHRLineChartCard_rhlcc_titleIcon)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportHRLineChartCard_rhlcc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportHRLineChartCard_rhlcc_titleMenuIcon)
        mIsTitleMenuIconBgShow = typeArray.getBoolean(
            R.styleable.ReportHRLineChartCard_rhlcc_isShowMenuIconBg,
            mIsTitleMenuIconBgShow
        )
        mTitleMenuIconBg =
            typeArray.getDrawable(R.styleable.ReportHRLineChartCard_rhlcc_menuIconBg)

        mPointCount =
            typeArray.getInteger(
                R.styleable.ReportHRLineChartCard_rhlcc_pointCount,
                mPointCount
            )
        mLineColor =
            typeArray.getColor(R.styleable.ReportHRLineChartCard_rhlcc_lineColor, mLineColor)
        mSecondLineColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_secondLineColor,
            mSecondLineColor
        )
        mLabelColor =
            typeArray.getColor(
                R.styleable.ReportHRLineChartCard_rhlcc_labelColor,
                mLabelColor
            )
        mGridLineColor =
            typeArray.getColor(
                R.styleable.ReportHRLineChartCard_rhlcc_gridLineColor,
                mGridLineColor
            )
        mTimeUnit =
            typeArray.getInteger(R.styleable.ReportHRLineChartCard_rhlcc_timeUnit, mTimeUnit)
        mLineWidth =
            typeArray.getDimension(
                R.styleable.ReportHRLineChartCard_rhlcc_lineWidth,
                mLineWidth
            )
        mXAxisUnit = typeArray.getString(R.styleable.ReportHRLineChartCard_rhlcc_xAxisUnit)
        mIsDrawFill =
            typeArray.getBoolean(R.styleable.ReportHRLineChartCard_rhlcc_isDrawFill, false)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportHRLineChartCard_rhlcc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.ReportHRLineChartCard_rhlcc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_markViewValueColor,
            mMarkViewValueColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.ReportHRLineChartCard_rhlcc_averageLabelBgColor,
            mAverageLabelBgColor
        )
        typeArray.recycle()
        initView()
    }


    fun initView() {
        initBg()
        initTitle()
        initTimeUnit()
        initLegned()
        initChart()
        initChartIcon()
    }

    fun initLegned() {
        if (!mIsShowLegend) {
            mChartExtraTopOffset = 26f.dp()
            mSelfView?.findViewById<LinearLayout>(R.id.ll_legend)?.visibility = View.GONE
            mSelfView?.findViewById<TextView>(R.id.tv_coh_time_value)?.visibility = View.GONE
            mSelfView?.findViewById<TextView>(R.id.tv_coh_time_value_2)?.visibility = View.VISIBLE
        } else {
            mChartExtraTopOffset = 26f.dp()
            mSelfView?.findViewById<LinearLayout>(R.id.ll_legend)?.visibility = View.VISIBLE
            mSelfView?.findViewById<TextView>(R.id.tv_coh_time_value)?.visibility = View.VISIBLE
            mSelfView?.findViewById<TextView>(R.id.tv_coh_time_value_2)?.visibility = View.GONE
        }
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
        tv_title.visibility = View.VISIBLE
        tv_title.text = mTitle
        tv_title.setTextColor(mTextColor)
        if (mSmallTitle != null) {
            tv_small_title.visibility = View.VISIBLE
            tv_small_title.text = mSmallTitle
            tv_small_title.setTextColor(mTextColor)
        }
        if (mIsTitleIconShow) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTitleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsTitleMenuIconShow) {
            iv_menu.setImageDrawable(mTitleMenuIcon)
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
        }
        if (mIsTitleMenuIconBgShow) {
            rl_corner_icon_bg.visibility = View.VISIBLE
            iv_corner_icon_bg.setImageDrawable(mTitleMenuIconBg)
        } else {
            rl_corner_icon_bg.visibility = View.GONE
        }
        tv_coh_time_title.setTextColor(mTextColor)
        tv_coh_time_value.setTextColor(mSecondLineColor)
        tv_coh_time_value_2.setTextColor(mSecondLineColor)
        tv_coh_time_value.text = mCohTime
        tv_coh_time_value_2.text = mCohTime
        (tv_legend_icon.background as GradientDrawable).setColor(mSecondLineColor)
        tv_legend_text.setTextColor(mTextColor)
        if (!mIsTitleMenuIconBgShow) {
            iv_menu.setOnClickListener {
                if (isFullScreen) {
                    (context as Activity).finish()
                } else {
                    var intent = Intent(context, HRVLineChartFullScreenActivity::class.java)
                    intent.putExtra("lineWidth", mLineWidth)
                    intent.putExtra("pointCount", mPointCount)
                    intent.putExtra("timeUnit", mTimeUnit)
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
                    intent.putExtra("secondLineColor", mSecondLineColor)
                    intent.putExtra("lineData", mFirstData?.toDoubleArray())
                    intent.putExtra("secondLineData", mSecondData?.toDoubleArray())
                    intent.putExtra("cohTime", mCohTime)
                    context.startActivity(intent)
                }

            }
        }

    }

    fun sampleData(data: List<Double>?, sample: Int): ArrayList<Double> {
        var sampleData = ArrayList<Double>()
        for (i in data!!.indices) {
            if (i % sample == 0) {
                sampleData.add(data!![i])
            }
        }
        return sampleData
    }

    fun processSecondLineDataByFirstLine(
        firstLineData: List<Double>?,
        secondLineData: List<Double>?
    ): List<Double>? {
        if (firstLineData == null || secondLineData == null) {
            return null
        }
        var newSecondLineData = ArrayList<Double>()
//        for (data in secondLineData) {
//            for (i in 0..8) {
//                newSecondLineData.add(data)
//            }
//        }
        if (secondLineData.size >= firstLineData.size) {
            for (i in firstLineData.indices) {
                newSecondLineData.add(secondLineData[i])
            }
        } else if (secondLineData.size < firstLineData.size) {
            for (i in firstLineData.indices) {
                if (i < secondLineData.size) {
                    newSecondLineData.add(secondLineData[i])
                } else {
                    newSecondLineData.add(secondLineData[secondLineData.size - 1])
                }
            }
        }
        for (i in newSecondLineData.indices) {
            if (newSecondLineData[i] == 1.0) {
                newSecondLineData[i] = firstLineData[i]
            }
        }
        return newSecondLineData
    }

    fun setData(
        data: List<Double>?,
        secondLineData: List<Double>?,
        isShowAllData: Boolean = false
    ) {
        if (data == null) {
            return
        }
        this.mFirstData = formatData(data)
        this.mSecondData = secondLineData
        var secondLineData = processSecondLineDataByFirstLine(data, mSecondData)
        var sample = mFirstData!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        mSampleData = sampleData(mFirstData, sample)
        mSampleSecondData = sampleData(secondLineData, sample)
        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = mFirstData!!.size * mTimeUnit / 1000F / 60F
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
            } else if (currentMin < totalMin && currentMin > totalMin * 7f / 8) {
                llXAxis.xOffset = 5f
            } else {
                llXAxis.xOffset = -1f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }
        if (mIsShowAverage) {
            if (mFirstData != null && mFirstData!!.isNotEmpty()) {
                var average = 0f
                try {
                    average = java.lang.Float.parseFloat(mAverageValue)
                } catch (e: Exception) {
                }
                val ll1 = LimitLine(
                    average,
                    "${context.getString(R.string.sdk_report_average)}$mAverageValue"
                )
                ll1.lineWidth = 1f
                ll1.enableDashedLine(10f, 10f, 0f)
                ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                ll1.textSize = 14f
                ll1.xOffset = 10f
                ll1.yOffset = 8f
                ll1.textColor = mTextColor
                ll1.lineColor = mTextColor
                chart.axisLeft.addLimitLine(ll1)
            }
        }

        val dataSets = ArrayList<ILineDataSet>()
        val values = ArrayList<Entry>()
        var tempLineValues: ArrayList<Entry>? = null
        var isFindFirstPointInSecondLine = false
        for (i in mSampleData!!.indices) {
            values.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
        }

        set1 = initDataSet(values, mLineColor)
        if (set1 != null) {
            dataSets.add(set1!!) // add the data sets
        }
        for (i in mSampleData!!.indices) {
            if (mSampleSecondData!![i] != 0.0) {
                if (i == 0 || (i - 1 >= 0 && mSampleSecondData!![i - 1] == 0.0)) {
                    if (!isFindFirstPointInSecondLine) {
                        secondLineStartIndexOfFirstLine = i
                        isFindFirstPointInSecondLine = true
                    }
                    tempLineValues = ArrayList()
                }
                tempLineValues?.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
                if (i == mSampleData!!.size - 1) {
                    if (tempLineValues?.size ?: 0 > 5) {
                        var set = initDataSet(tempLineValues!!, mSecondLineColor)
                        if (set != null) {
                            dataSets.add(set)
                        }
                    }
                }
            } else {
                if (i - 1 >= 0 && mSampleSecondData!![i - 1] != 0.0) {
                    if (tempLineValues?.size ?: 0 > 5) {
                        var set = initDataSet(tempLineValues!!, mSecondLineColor)
                        if (set != null) {
                            dataSets.add(set)
                        }
                    }
                }
            }
        }

        // create a data object with the data sets
        val data = LineData(dataSets)

//         // set data
        chart.data = data
        calNiceLabel(mSampleData!!)
        if (isShowDetail) {
            if (dataSets.size <= 1) {
                secondLineStartIndexOfFirstLine = 0
            }
            chart.viewTreeObserver.addOnGlobalLayoutListener {
                if (ViewCompat.isLaidOut(chart) && isFirstIn) {
                    chart.zoom(mSampleData!!.size * 1f / mPointCount, 1f, 0f, 0f)
                    var deltaX = chart.viewPortHandler.contentWidth() / mPointCount
                    if (mSampleData!!.size - secondLineStartIndexOfFirstLine < mPointCount) {
                        secondLineStartIndexOfFirstLine = mSampleData!!.size - mPointCount * 5 / 4
                    }
                    var translateNegX = -deltaX * secondLineStartIndexOfFirstLine
                    var translatePosX =
                        if (secondLineStartIndexOfFirstLine > mPointCount * SECOND_LINE_START_OFFSET_FACTOR) {
                            mPointCount * SECOND_LINE_START_OFFSET_FACTOR * deltaX
                        } else {
                            -translateNegX
                        }
                    chart.viewPortHandler.matrixTouch.postTranslate(
                        translateNegX + translatePosX,
                        0f
                    )
                    isFirstIn = false
                }
            }
        }
        chart.notifyDataSetChanged()
    }

    fun initDataSet(values: ArrayList<Entry>, lineColor: Int): LineDataSet? {
        if (values.isEmpty()) {
            return null
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
            set1.setDrawIcons(false)
            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set1.color = lineColor
            // line thickness and point size
            set1.lineWidth = mLineWidth
//            set1.circleRadius = 3f
            // draw points as solid circles
            set1.setDrawCircleHole(false)
            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
            set1.formSize = 15f
            // text size of values
            set1.valueTextSize = 9f
            set1.setDrawValues(false)
            set1.highLightColor = mHighlightLineColor
            set1.highlightLineWidth = mHighlightLineWidth
            set1.setDrawHorizontalHighlightIndicator(false)
            set1.setDrawVerticalHighlightIndicator(true)
            set1.setDrawFilled(mIsDrawFill)
            set1.fillAlpha = 255
            set1.setDrawCircles(false)
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            if (mIsDrawFill) {
                if (Utils.getSDKInt() >= 18) {
                    var gradientDrawable = GradientDrawable()
                    gradientDrawable.setColors(
                        intArrayOf(
                            mLineColor,
                            getOpacityColor(mLineColor, 0.5F)
                        )
                    )
                    gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT)
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
        }
        return set1

    }

    private fun calNiceLabel(data: List<Double>) {
        var min = data.min()
        var max = data.max()
        var yAxisMax = (max!! / 1f)
        var yAxisMin = (min!! * 1f)
        if (min == max) {
            if (min == 0.0) {
                chart.axisLeft.axisMaximum = 100f
                chart.axisLeft.axisMinimum = 0f
                chart.axisLeft.mEntries = floatArrayOf(0f, 25f, 50f, 75f, 100f)
                chart.axisLeft.mEntryCount = 5
                return
            }
        } else {
            yAxisMax = max + (max - min) / 8f
//            if (yAxisMax > 100) {
//                yAxisMax = 100.0
//            }
            yAxisMin = min - (max - min) / 8f
            if (yAxisMin < 0) {
                yAxisMin = 0.0
            }
        }
        var interval = 0
        try {
            interval = calNiceInterval(yAxisMin, yAxisMax)
        } catch (e: Exception) {
            chart.axisLeft.axisMaximum = 100f
            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.mEntries = floatArrayOf(0f, 25f, 50f, 75f, 100f)
            chart.axisLeft.mEntryCount = 5
            return
        }
        var firstLabel = floor(yAxisMin / interval) * interval
        var lastLabel = ceil(yAxisMax / interval) * interval
        var labels = ArrayList<Float>()
        var i = firstLabel
        while (i <= lastLabel) {
            labels.add(i.toFloat())
            i += interval
        }
        chart.axisLeft.axisMaximum = lastLabel.toFloat()
        chart.axisLeft.axisMinimum = firstLabel.toFloat()
        chart.axisLeft.mEntries = labels.toFloatArray()
        chart.axisLeft.mEntryCount = labels.size
    }


    fun initChart() {
//        chart.setBackgroundColor(bgColor)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(mIsChartEnable)
        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = mIsChartEnable
        chart.isScaleXEnabled = mIsChartEnable
        chart.isScaleYEnabled = false
        val marker = LineChartMarkView(context, mLineColor, mMarkViewTitle)
        marker.chartView = chart
        marker.setYOffset(10f.dp())
        marker.setMarkTitleColor(mMarkViewTitleColor)
        marker.setMarkViewBgColor(mMarkViewBgColor)
        marker.setMarkViewValueColor(mMarkViewValueColor)
        chart.marker = marker
        chart.extraTopOffset = mChartExtraTopOffset
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
        yAxis.setDrawLabels(mIsDrawYAxisLabels)
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
        set1?.setDrawIcons(false)
        set2?.setDrawIcons(false)
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
                set1?.setDrawVerticalHighlightIndicator(true)
                set1?.setDrawHorizontalHighlightIndicator(false)
                set2?.setDrawVerticalHighlightIndicator(true)
                set2?.setDrawHorizontalHighlightIndicator(false)
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
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                cancelHighlight()
            }

            override fun onValueSelected(e: Entry, h: Highlight?) {
                ll_title.visibility = View.INVISIBLE
                chart.highlightValue(null, false)
                set1?.setDrawIcons(true)
                set1?.iconsOffset = MPPointF(0f, 3f)
                set1?.values?.forEach {
                    it.icon = null
                }
                set2?.setDrawIcons(true)
                set2?.iconsOffset = MPPointF(0f, 3f)
                set2?.values?.forEach {
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

    fun setSecondLineColor(secondLineColor: Int) {
        this.mSecondLineColor = secondLineColor
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
        this.mIsShowAverage = true
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

    fun setCohTime(time: String) {
        this.mCohTime = time
        initView()
    }

    fun isShowLegend(isShowLegend: Boolean) {
        this.mIsShowLegend = isShowLegend
        initView()
    }


    fun isShowYAxisLabels(flag: Boolean) {
        this.mIsDrawYAxisLabels = flag
        initView()
    }

    fun isChartEnable(isChartEnable: Boolean) {
        this.mIsChartEnable = isChartEnable
        initView()
    }
}