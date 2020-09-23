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
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.AffectiveLineChartFullScreenActivity
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.widget.AAndRLineChartMarkView
import cn.entertech.uicomponentsdk.widget.ChartIconView
import com.github.mikephil.charting.components.*
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
import kotlinx.android.synthetic.main.layout_card_attention.view.chart
import kotlinx.android.synthetic.main.layout_card_attention.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_report_affective_card.view.*

class ReportAffectiveLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mSmallTitle: String? = ""
    var mAverageLabelBgColor: Int = Color.parseColor("#ffffff")
    private var popWindow: PopupWindow? = null
    private var fullScreenChart: ReportAffectiveLineChartCard? = null
    private lateinit var marker: AAndRLineChartMarkView
    private lateinit var attentionDrawableIcon: Drawable
    private lateinit var relaxationDrawableIcon: Drawable
    private var mAttentionAverage: Int = 0
    private var mRelaxationAverage: Int = 0
    private var mXAxisUnit: String? = "Time(min)"
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")

    private var mAverageLineColor: Int = Color.parseColor("#11152E")
    private var mLineWidth: Float = 1.5f
    private var mAttentionData: List<Double> = ArrayList()
    private var mRelaxationData: List<Double> = ArrayList()

    //    private var mAttentionData: List<Double>? = null
    private var mAttentionLineColor: Int = Color.RED
    private var mRelaxationLineColor: Int = Color.BLUE
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

    val dataSets = ArrayList<ILineDataSet>()

    var bgColor = Color.WHITE
    val TICK_COUNT = 5

    companion object {
        val ATTENTION_Y_OFFSET = 120
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
    var mMarkViewDivideLineColor: Int = Color.parseColor("#9AA1A9")
        set(value) {
            field = value
            initView()
        }
    var mMarkViewTitle1: String? = "--"
        set(value) {
            field = value
            initView()
        }
    var mMarkViewTitle2: String? = "--"
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

    var sets = HashMap<List<Double>, LineDataSet>()

    var isFullScreen = false

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_report_affective_card, null)
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
            R.styleable.ReportAffectiveLineChartCard
        )
        mMainColor =
            typeArray.getColor(R.styleable.ReportAffectiveLineChartCard_ralcc_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportAffectiveLineChartCard_ralcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_background)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mSmallTitle = typeArray.getString(
            R.styleable.ReportAffectiveLineChartCard_ralcc_smallTitle
        )
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportAffectiveLineChartCard_ralcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mTitle = typeArray.getString(R.styleable.ReportAffectiveLineChartCard_ralcc_title)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportAffectiveLineChartCard_ralcc_titleMenuIcon)

        mPointCount =
            typeArray.getInteger(
                R.styleable.ReportAffectiveLineChartCard_ralcc_pointCount,
                mPointCount
            )
        mAttentionLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_attentionLineColor,
            mAttentionLineColor
        )
        mRelaxationLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_relaxationLineColor,
            mRelaxationLineColor
        )
        mLabelColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_labelColor,
            mLabelColor
        )
        mGridLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_gridLineColor,
            mGridLineColor
        )

        mTimeUnit =
            typeArray.getInteger(R.styleable.ReportAffectiveLineChartCard_ralcc_timeUnit, mTimeUnit)
        mLineWidth = typeArray.getDimension(
            R.styleable.ReportAffectiveLineChartCard_ralcc_lineWidth,
            mLineWidth
        )
        mXAxisUnit = typeArray.getString(R.styleable.ReportAffectiveLineChartCard_ralcc_xAxisUnit)
        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportAffectiveLineChartCard_ralcc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle1 =
            typeArray.getString(R.styleable.ReportAffectiveLineChartCard_ralcc_markViewTitle1)
        mMarkViewTitle2 =
            typeArray.getString(R.styleable.ReportAffectiveLineChartCard_ralcc_markViewTitle2)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_markViewValueColor,
            mMarkViewValueColor
        )
        mMarkViewDivideLineColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_markViewDivideLineColor,
            mMarkViewDivideLineColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.ReportAffectiveLineChartCard_ralcc_averageLabelBgColor,
            mAverageLabelBgColor
        )
        typeArray.recycle()
        initView()
    }

    fun initView() {
        initTitle()
        initChart()
        initBg()
        initTimeUnit()
        initLegend()
        initChartIcon()
    }

    fun initChartIcon() {
//        var iconViewAttention = ChartIconView(context)
//        var iconViewRelaxation = ChartIconView(context)
//        iconViewAttention.color = mAttentionLineColor
//        iconViewRelaxation.color = mRelaxationLineColor
//        attentionDrawableIcon = iconViewAttention.toDrawable(context)
//        relaxationDrawableIcon = iconViewRelaxation.toDrawable(context)
    }

    fun initLegend() {
        legend_attention.setLegendIconColor(mAttentionLineColor)
        legend_relaxation.setLegendIconColor(mRelaxationLineColor)
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
                    bgColor = (mBg as GradientDrawable).color.defaultColor
                }
            }
        }
        if (bgColor != null) {
            rl_bg.setBackgroundColor(bgColor)
        }
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

        if (mSmallTitle != null) {
            tv_small_title.visibility = View.VISIBLE
            tv_small_title.text = mSmallTitle
            tv_small_title.setTextColor(mTextColor)
        }
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
                var intent = Intent(context, AffectiveLineChartFullScreenActivity::class.java)
                intent.putExtra("lineWidth", mLineWidth)
                intent.putExtra("pointCount", mPointCount)
                intent.putExtra("timeUnit", mTimeUnit)
                intent.putExtra("highlightLineColor", mHighlightLineColor)
                intent.putExtra("highlightLineWidth", mHighlightLineWidth)
                intent.putExtra("markViewBgColor", mMarkViewBgColor)
                intent.putExtra("markViewTitle1", mMarkViewTitle1)
                intent.putExtra("markViewTitle2", mMarkViewTitle2)
                intent.putExtra("markViewTitleColor", mMarkViewTitleColor)
                intent.putExtra("markViewValueColor", mMarkViewValueColor)
                intent.putExtra("gridLineColor", mGridLineColor)
                intent.putExtra("xAxisUnit", mXAxisUnit)
                intent.putExtra("textColor", mTextColor)
                intent.putExtra("bgColor", bgColor)
                intent.putExtra("averageLineColor", mAverageLineColor)
                intent.putExtra("averageLabelBgColor", mAverageLabelBgColor)
                intent.putExtra("labelColor", mLabelColor)
                intent.putExtra("attentionAverage", mAttentionAverage)
                intent.putExtra("relaxationAverage", mRelaxationAverage)
                intent.putExtra("attentionLineColor", mAttentionLineColor)
                intent.putExtra("relaxationLineColor", mRelaxationLineColor)
                intent.putExtra("attentionData", mAttentionData?.toDoubleArray())
                intent.putExtra("relaxationData", mRelaxationData?.toDoubleArray())
                context.startActivity(intent)
            }
        }
    }

    fun setOnMenuIconClickListener(onClickListener: View.OnClickListener) {
        iv_menu.setOnClickListener(onClickListener)
    }

    fun drawLine(
        mData: List<Double>,
        average: Int,
        lineColor: Int,
        isShowAllData: Boolean = false
    ) {
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

        if (mData != null && mData!!.isNotEmpty()) {
            val ll1 = LimitLine(
                average.toFloat(), "${context.getString(R.string.sdk_report_average)}${if (average >= ATTENTION_Y_OFFSET) {
                    mAttentionAverage
                } else {
                    mRelaxationAverage
                }}"
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
        var yLimitLineValues = listOf<Float>(0f, 120f, 30f, 70f, 100f, 150f, 190f, 220f)
        yLimitLineValues.forEach {
            var limitLine: LimitLine? = null
            if (it == 0f || it == 120f) {
                limitLine = LimitLine(it, "0")
                limitLine?.lineColor = getOpacityColor(mTextColor, 0.6f)
            } else {
                var label = if (it > ATTENTION_Y_OFFSET) {
                    it - ATTENTION_Y_OFFSET
                } else {
                    it
                }
                limitLine = LimitLine(it, "${label.toInt()}")
                limitLine?.enableDashedLine(10f, 10f, 0f)
                limitLine?.lineColor = getOpacityColor(mAverageLineColor, 0.2f)
            }
            limitLine?.lineWidth = 1f
            limitLine?.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            limitLine?.textSize = 11f
            limitLine?.xOffset = -20f
            limitLine?.yOffset = -4f
            limitLine?.textColor = mLabelColor
            chart.axisLeft.addLimitLine(limitLine)
        }
        val values = ArrayList<Entry>()
        for (i in sampleData.indices) {
            values.add(Entry(i.toFloat(), sampleData!![i].toFloat()))
        }

        val set1: LineDataSet
        set1 = LineDataSet(values, "")
        sets[mData] = set1
        set1.color = lineColor
        set1.lineWidth = mLineWidth
        // draw points as solid circles
        set1.setDrawHighlightIndicators(false)
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.setDrawVerticalHighlightIndicator(true)
        set1.setDrawCircleHole(false)
        set1.highLightColor = mHighlightLineColor
        set1.highlightLineWidth = mHighlightLineWidth
        // customize legend entry
        set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f
        // text size of values
        set1.valueTextSize = 9f
        set1.setDrawValues(false)
        set1.setDrawCircles(false);
        set1.setDrawHighlightIndicators(true)
        set1.setDrawFilled(false)
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        marker.setDataSets(dataSets)
        val data = LineData(dataSets)
        // set data
        chart.data = data
//        chart.extraTopOffset = 64f
//        chart.extraLeftOffset = -10f
        chart.notifyDataSetChanged()
        setChartListener()
    }

    fun setData(
        attentionData: List<Double>?,
        relaxationData: List<Double>?,
        isShowAllData: Boolean = false
    ) {
        if (attentionData == null || relaxationData == null) {
            return
        }

        this.mAttentionData = formatData(attentionData)
        this.mRelaxationData = formatData(relaxationData)

        drawLine(
            mAttentionData!!.map { it + ATTENTION_Y_OFFSET },
            mAttentionAverage + ATTENTION_Y_OFFSET,
            mAttentionLineColor,
            isShowAllData
        )
        drawLine(mRelaxationData!!, mRelaxationAverage, mRelaxationLineColor, isShowAllData)

//        chart.xAxis.setLabelCount(mData!!.size,true)
    }

    fun initChart() {
//        chart.setBackgroundColor(bgColor)
        marker = AAndRLineChartMarkView(
            context,
            mAttentionLineColor,
            mRelaxationLineColor,
            getOpacityColor(mMarkViewDivideLineColor, 0.3f),
            mMarkViewTitle1,
            mMarkViewTitle2
        )
        marker.chartView = chart
        marker.setMarkTitleColor(mMarkViewTitleColor)
        marker.setMarkViewBgColor(mMarkViewBgColor)
        marker.setMarkViewValueColor(mMarkViewValueColor)
        chart.animateX(500)

        chart.isHighlightPerDragEnabled = false
        chart.marker = marker
        chart.extraTopOffset = 64f
        chart.extraLeftOffset = 30f
        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        // disable description text
        chart.getDescription().setEnabled(false)
        chart.legend.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(true)
        chart.setDrawGridBackground(false)
        // enable scaling and dragging
        chart.setDragEnabled(true)
        chart.setMaxVisibleValueCount(100000)
//        chart.setScaleEnabled(true)
        chart.setScaleXEnabled(true)
        chart.setScaleYEnabled(false)
        // force pinch zoom along both axis
        chart.setPinchZoom(true)
        val xAxis: XAxis = chart.xAxis
        // vertical grid lines
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
        xAxis.setDrawAxisLine(false)
        xAxis.axisLineColor = getOpacityColor(mTextColor, 0.6f)
        xAxis.axisLineWidth = 1f
        yAxis.mAxisRange
        yAxis.axisMaximum = 225f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        // axis range
        yAxis.labelCount = 3
        yAxis.textSize = 12f
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {
                var yLabel = ""
                if (value == 0f) {
                    yLabel = "0"
                } else if (value == 100f) {
                    yLabel = "100"
                } else if (value == 200f) {
                    yLabel = "100"
                }
                return yLabel
            }
        }
        // // Create Limit Lines // //


        // draw limit lines behind data instead of on top

        // add limit lines
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
                }
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartLongPressed(me: MotionEvent) {
                chart.isHighlightPerDragEnabled = true
                chart.isDragEnabled = false
                val highlightByTouchPoint = chart.getHighlightByTouchPoint(me.x, me.y)
                chart.highlightValue(highlightByTouchPoint, true)
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            }
        }

        var iconViewAttention = ChartIconView(context)
        var iconViewRelaxation = ChartIconView(context)
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                cancelHighlight()
            }

            override fun onValueSelected(e: Entry, h: Highlight?) {
                chart.highlightValue(null, false)
                iconViewAttention.color = mAttentionLineColor
                iconViewRelaxation.color = mRelaxationLineColor
                attentionDrawableIcon = iconViewAttention.toDrawable(context)
                relaxationDrawableIcon = iconViewRelaxation.toDrawable(context)
                ll_title.visibility = View.INVISIBLE
                dataSets[0]?.setDrawIcons(true)
                dataSets[0]?.iconsOffset = MPPointF(0f, 3f)
                (dataSets[0]!! as LineDataSet)?.values.forEach {
                    if (it.x == e.x) {
                        it.icon = attentionDrawableIcon
                    } else {
                        it.icon = null
                    }
                }
                dataSets[1]?.setDrawIcons(true)
                dataSets[1]?.iconsOffset = MPPointF(0f, 3f)
                (dataSets[1]!! as LineDataSet)?.values?.forEach {
                    if (it.x == e.x) {
                        it.icon = relaxationDrawableIcon
                    } else {
                        it.icon = null
                    }
                }
                chart.highlightValue(h, false)
            }

        })
    }

    fun setLineWidth(lineWidth: Float) {
        this.mLineWidth = lineWidth
        initView()
    }

    fun setRelaxationLineColor(relaxationLineColor: Int) {
        this.mRelaxationLineColor = relaxationLineColor
        initView()
    }

    fun setAttentionLineColor(attentionLineColor: Int) {
        this.mAttentionLineColor = attentionLineColor
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

    fun setAttentionAverage(attentionValue: Int) {
        this.mAttentionAverage = attentionValue
        initView()
    }

    fun setRelaxationAverage(relaxationValue: Int) {
        this.mRelaxationAverage = relaxationValue
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