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
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.SessionPressureChartFullScreenActivity
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.utils.ScreenUtil.isPad
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.LineChartMarkView
import cn.entertech.uicomponentsdk.widget.SessionPressureChartMarkView
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
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_session_pressure_chart.view.*
import kotlinx.android.synthetic.main.layout_session_pressure_chart.view.chart
import kotlinx.android.synthetic.main.layout_session_pressure_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_session_pressure_chart.view.rl_bg
import kotlinx.android.synthetic.main.layout_session_pressure_chart.view.tv_time_unit_des

class SessionPressureChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mFillGradientStartColor: Int = Color.parseColor("#80FB9C98")
    private var mFillGradientEndColor: Int = Color.parseColor("#805F76FF")
    private var mSourceDataList: List<Double>? = null
    private var mStartTime: String = ""
    private var dataTotalTimeMs: Int = 0
    private var mDataAverage: Int = 0
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
    private var mIsShowXAxisUnit: Boolean = false
    private var mTitleDescription: String? = ""
    private var mBgLineColor: Int = Color.parseColor("#DDE1EB")
    private var mIsChartEnable: Boolean = true
    private var mIsShowLegend: Boolean = false
    private var mIsDrawYAxisLabels: Boolean = true
    private var mChartExtraTopOffset: Float = 22f.dp()
    private var mIsShowAverage: Boolean = false
    private var mCohTime: String = "--"
    private var set2: LineDataSet? = null
    private var mSampleData: ArrayList<Double>? = null
    var isFirstIn = true
    private var set1: LineDataSet? = null
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
    private var mIsTitleMenuIconShow: Boolean = true
    private var mIsTitleMenuIconBgShow: Boolean = false
    private var mFirstData: List<Double>? = null
    private var mBg: Drawable? = null

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
                LayoutInflater.from(context).inflate(R.layout.layout_session_pressure_chart, null)
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
            R.styleable.SessionPressureChart
        )
        mMainColor =
            typeArray.getColor(R.styleable.SessionPressureChart_spc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.SessionPressureChart_spc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.SessionPressureChart_spc_background)

        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.SessionPressureChart_spc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.SessionPressureChart_spc_titleMenuIcon)
        mIsTitleMenuIconBgShow = typeArray.getBoolean(
            R.styleable.SessionPressureChart_spc_isShowMenuIconBg,
            mIsTitleMenuIconBgShow
        )
        mTitleMenuIconBg =
            typeArray.getDrawable(R.styleable.SessionPressureChart_spc_menuIconBg)

        mPointCount =
            typeArray.getInteger(
                R.styleable.SessionPressureChart_spc_pointCount,
                mPointCount
            )
        mBgLineColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_bgLineColor,
            mBgLineColor
        )
        mLabelColor =
            typeArray.getColor(
                R.styleable.SessionPressureChart_spc_labelColor,
                mLabelColor
            )
        mGridLineColor =
            typeArray.getColor(
                R.styleable.SessionPressureChart_spc_gridLineColor,
                mGridLineColor
            )
        mTimeUnit =
            typeArray.getInteger(R.styleable.SessionPressureChart_spc_timeUnit, mTimeUnit)
        mLineWidth =
            ScreenUtil.px2dip(context,
                typeArray.getDimension(
                    R.styleable.SessionPressureChart_spc_lineWidth,
                    mLineWidth
                )).toFloat()
        mXAxisUnit = typeArray.getString(R.styleable.SessionPressureChart_spc_xAxisUnit)
        mIsDrawFill =
            typeArray.getBoolean(R.styleable.SessionPressureChart_spc_isDrawFill, false)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.SessionPressureChart_spc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.SessionPressureChart_spc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_markViewValueColor,
            mMarkViewValueColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_averageLabelBgColor,
            mAverageLabelBgColor
        )
        mTitleDescription =
            typeArray.getString(R.styleable.SessionPressureChart_spc_title_description)
        mIsShowXAxisUnit =
            typeArray.getBoolean(R.styleable.SessionPressureChart_spc_showXAxisUnit, false)
        mXAxisLineColor =
            typeArray.getColor(R.styleable.SessionPressureChart_spc_xAxisLineColor, mXAxisLineColor)
        mFillGradientStartColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_fillGradientStartColor,
            mFillGradientStartColor
        )
        mFillGradientEndColor = typeArray.getColor(
            R.styleable.SessionPressureChart_spc_fillGradientEndColor,
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
        if (mIsShowXAxisUnit) {
            tv_time_unit_des.visibility = View.VISIBLE
        } else {
            tv_time_unit_des.visibility = View.GONE
        }
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
        if (mIsTitleMenuIconShow) {
            iv_menu_icon.visibility = View.VISIBLE
            iv_menu_icon.setImageDrawable(mTitleMenuIcon)
        } else {
            iv_menu_icon.visibility = View.GONE
        }
        mChartExtraTopOffset = if (isPad(context)) {
            36f.dp()
        } else {
            26f.dp()
        }
        tv_description.text = "AVERAGE"
        tv_description.setTextColor(mTextColor)


        tv_date.setTextColor(mTextColor)

        if (!mIsTitleMenuIconBgShow) {
            iv_menu_icon.setOnClickListener {
                if (isFullScreen) {
                    (context as Activity).finish()
                } else {
                    var intent = Intent(context, SessionPressureChartFullScreenActivity::class.java)
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
                    intent.putExtra("mainColor", mMainColor)
                    intent.putExtra("bgColor", bgColor)
                    intent.putExtra("averageLineColor", mAverageLineColor)
                    intent.putExtra("labelColor", mLabelColor)
                    intent.putExtra("average", mAverageValue)
                    intent.putExtra("averageBgColor", mAverageLabelBgColor)
                    intent.putExtra("lineColor", mLineColor)
                    intent.putExtra("lineData", mSourceDataList?.toDoubleArray())
                    intent.putExtra("bgLineColor", mBgLineColor)
//                    intent.putExtra("titleDescription", mTitleDescription)
                    intent.putExtra("fillStartGradientColor", mFillGradientStartColor)
                    intent.putExtra("fillEndGradientColor", mFillGradientEndColor)
                    intent.putExtra("startTime", mStartTime)
                    intent.putExtra("dataAverage", mDataAverage)
                    context.startActivity(intent)
                }

            }
        }

    }

    fun sampleData(data: List<Double>?, sample: Int): ArrayList<Double> {
        var sampleData = ArrayList<Double>()
        for (i in data!!.indices) {
            if (i % sample == 0) {
                sampleData.add(data[i])
            }
        }
        return sampleData
    }

    var yLimitLineValues = listOf(25f, 50f, 75f)

    fun setData(
        data: List<Double>?,dataAverage:Double? = null,
        isShowAllData: Boolean = false
    ) {
        if (data == null) {
            return
        }
        this.mSourceDataList = data
        this.dataTotalTimeMs = data.size * mTimeUnit
        if (dataAverage == null){
            this.mDataAverage = data.average().toInt()
        }else{
            this.mDataAverage = dataAverage.toInt()
        }

        this.mFirstData = formatData(data)
        var sample = mFirstData!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        when (mDataAverage) {
            in 0..24 -> tv_level.text = context.getString(R.string.pressure_level_low)
            in 25..50 -> tv_level.text = context.getString(R.string.pressure_level_normal)
            in 50..75 -> tv_level.text = context.getString(R.string.pressure_level_elevated)
            else -> tv_level.text = context.getString(R.string.pressure_level_high)
        }
        mSampleData = sampleData(mFirstData, sample)
        mLineColor = mMainColor
        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = mFirstData!!.size * mTimeUnit / 1000F / 60F
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
            llXAxis.lineColor = mBgLineColor
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
                ll1.lineWidth = 0.5f
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
        for (i in yLimitLineValues.indices) {
            val ll = LimitLine(yLimitLineValues[i], "")
            ll.lineWidth = 0.5f
            ll.enableDashedLine(10f, 10f, 0f)
            ll.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll.textSize = 12f
            ll.xOffset = 10f
            ll.yOffset = 8f
            ll.textColor = mTextColor
            ll.lineColor = mBgLineColor
            chart.axisLeft.addLimitLine(ll)
        }
        val dataSets = ArrayList<ILineDataSet>()
        val values = ArrayList<Entry>()
        for (i in mSampleData!!.indices) {
            values.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
        }

        set1 = initDataSet(values, mLineColor)
        if (set1 != null) {
            dataSets.add(set1!!) // add the data sets
        }

        // create a data object with the data sets
        val lineData = LineData(dataSets)

//
        chart.data = lineData
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
        var set: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set = chart.data.getDataSetByIndex(0) as LineDataSet
            set.values = values
            set.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set = LineDataSet(values, "")
            set.setDrawIcons(false)
            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set.color = lineColor
            // line thickness and point size
            set.lineWidth = mLineWidth
//            set1.circleRadius = 3f
            // draw points as solid circles
            set.setDrawCircleHole(false)
            // customize legend entry
            set.formLineWidth = 0.5f
            set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
            set.formSize = 15f
            // text size of values
            set.valueTextSize = 9f
            set.setDrawValues(false)
            set.highLightColor = mBgLineColor
            set.highlightLineWidth = mHighlightLineWidth
            set.setDrawHorizontalHighlightIndicator(false)
            set.setDrawVerticalHighlightIndicator(true)
            set.setDrawFilled(mIsDrawFill)
            set.fillAlpha = 255
            set.setDrawCircles(false)
            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            if (mIsDrawFill) {
                if (Utils.getSDKInt() >= 18) {
                    var gradientDrawable = GradientDrawable()
                    gradientDrawable.setColors(
                        intArrayOf(
                            mFillGradientStartColor,
                            mFillGradientEndColor
                        )
                    )
                    gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT)
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE)
                    set.fillDrawable = gradientDrawable
                } else {
                    set.fillColor = mLineColor
                }
                set.lineWidth = 0f
                set.setDrawFilled(true)
            } else {
                set.setDrawFilled(false)
                set.lineWidth = mLineWidth
            }
        }
        return set

    }

    fun initChart() {
//        chart.setBackgroundColor(bgColor)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(mIsChartEnable)
        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(true)
        chart.setGridBackgroundColors(intArrayOf(mFillGradientStartColor, mFillGradientEndColor))
        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = mIsChartEnable
        chart.isScaleXEnabled = mIsChartEnable
        chart.isScaleYEnabled = false
        val marker = SessionPressureChartMarkView(context, mMarkViewTitle, mStartTime)
        marker.chartView = chart
        marker.setYOffset(10f.dp())
        marker.chartView = chart
        marker.setMainColor(mMainColor)
        marker.setTextColor(mTextColor)
        marker.setMarkViewBgColor(mMarkViewBgColor)
        chart.marker = marker
        chart.extraTopOffset = 66f
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = mXAxisLineColor
        xAxis.axisLineWidth = 0.5f
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
        yAxis.gridColor = mBgLineColor
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
//                e.icon = drawableIcon
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

    fun setBgLineColor(color: Int) {
        this.mBgLineColor = color
        initView()
    }

    fun setTitleDescription(title: String) {
        this.mTitleDescription = title
        initView()
    }

    fun setFillStartGradientColorColor(color: Int) {
        this.mFillGradientStartColor = color
        initView()
    }

    fun setFillEndGradientColorColor(color: Int) {
        this.mFillGradientEndColor = color
        initView()
    }

    fun setMainColor(mainColor: Int) {
        this.mMainColor = mainColor
        initView()
    }

}