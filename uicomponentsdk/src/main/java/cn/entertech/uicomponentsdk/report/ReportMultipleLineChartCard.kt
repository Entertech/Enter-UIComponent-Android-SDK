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
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.LineChartFullScreenActivity
import cn.entertech.uicomponentsdk.activity.MultipleLineChartFullScreenActivity
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
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class ReportMultipleLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

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
    private var mFirstData: List<Double>? = null
    private var mSecondData: List<Double>? = null
    private var mBg: Drawable? = null

    private var mTiltleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?

    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    var mSelfView: View? = null
    companion object{
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
            mSelfView = LayoutInflater.from(context).inflate(R.layout.layout_card_attention, null)
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
            R.styleable.ReportMultipleLineChartCard
        )
        mMainColor = typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportMultipleLineChartCard_rmlcc_background)
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportMultipleLineChartCard_rmlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mSmallTitle = typeArray.getString(R.styleable.ReportMultipleLineChartCard_rmlcc_smallTitle)
        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.ReportMultipleLineChartCard_rmlcc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportMultipleLineChartCard_rmlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mIsTitleIconShow = typeArray.getBoolean(
            R.styleable.ReportMultipleLineChartCard_rmlcc_isTitleIconShow,
            mIsTitleIconShow
        )
        mTitle = typeArray.getString(R.styleable.ReportMultipleLineChartCard_rmlcc_title)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportMultipleLineChartCard_rmlcc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportMultipleLineChartCard_rmlcc_titleMenuIcon)

        mPointCount =
            typeArray.getInteger(R.styleable.ReportMultipleLineChartCard_rmlcc_pointCount, mPointCount)
        mLineColor = typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_lineColor, mLineColor)
        mSecondLineColor = typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_secondLineColor, mSecondLineColor)
        mLabelColor =
            typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_labelColor, mLabelColor)
        mGridLineColor =
            typeArray.getColor(R.styleable.ReportMultipleLineChartCard_rmlcc_gridLineColor, mGridLineColor)
        mTimeUnit = typeArray.getInteger(R.styleable.ReportMultipleLineChartCard_rmlcc_timeUnit, mTimeUnit)
        mLineWidth =
            typeArray.getDimension(R.styleable.ReportMultipleLineChartCard_rmlcc_lineWidth, mLineWidth)
        mXAxisUnit = typeArray.getString(R.styleable.ReportMultipleLineChartCard_rmlcc_xAxisUnit)
        mIsDrawFill = typeArray.getBoolean(R.styleable.ReportMultipleLineChartCard_rmlcc_isDrawFill, false)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.ReportMultipleLineChartCard_rmlcc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.ReportMultipleLineChartCard_rmlcc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.ReportMultipleLineChartCard_rmlcc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle = typeArray.getString(R.styleable.ReportMultipleLineChartCard_rmlcc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.ReportMultipleLineChartCard_rmlcc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.ReportMultipleLineChartCard_rmlcc_markViewValueColor,
            mMarkViewValueColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.ReportMultipleLineChartCard_rmlcc_averageLabelBgColor,
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
                    bgColor = (mBg as GradientDrawable).color.defaultColor
                }
            }
        }
        if (bgColor != null) {
            rl_bg.setBackgroundColor(bgColor)
        }
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
            iv_icon.setImageDrawable(mTiltleIcon)
        } else {
            iv_icon.visibility = View.GONE
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
                var intent = Intent(context, MultipleLineChartFullScreenActivity::class.java)
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
                context.startActivity(intent)
            }
        }
    }

    fun sampleData(data: List<Double>?,sample: Int, isShowAllData: Boolean): ArrayList<Double> {
        var sampleData = ArrayList<Double>()
        for (i in data!!.indices) {
            if (i % sample == 0) {
                sampleData.add(data!![i])
            }
        }
        return sampleData
    }

    fun processSecondLineDataByFirstLine(firstLineData: List<Double>?,secondLineData:List<Double>?):List<Double>?{
        if(firstLineData == null || secondLineData == null){
            return null
        }
        var newSecondLineData = ArrayList<Double>()
        for (data in secondLineData){
            for (i in 0..8){
                newSecondLineData.add(data)
            }
        }
        if (newSecondLineData.size > firstLineData.size){
            newSecondLineData = newSecondLineData.subList(0,firstLineData.size) as ArrayList<Double>
        }else if (newSecondLineData.size < firstLineData.size){
            var deltaSize = firstLineData.size- newSecondLineData.size
            for (i in 0 until deltaSize){
                newSecondLineData.add(newSecondLineData[newSecondLineData.size-1])
            }
        }
        for (i in newSecondLineData.indices){
            if (newSecondLineData[i] == 1.0){
                newSecondLineData[i] = firstLineData[i]
            }
        }
        return newSecondLineData
    }

    fun setData(data: List<Double>?,secondLineData:List<Double>?, isShowAllData: Boolean = false) {
        if (data == null) {
            return
        }
        this.mFirstData = formatData(data)
        this.mSecondData = secondLineData
        var secondLineData = processSecondLineDataByFirstLine(data,mSecondData)
        var sample = mFirstData!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        mSampleData = sampleData(mFirstData,sample, isShowAllData)
        mSampleSecondData = sampleData(secondLineData,sample, isShowAllData)
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
            } else {
                llXAxis.xOffset = -1f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }

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

        val values = ArrayList<Entry>()
        val secondLineValues = ArrayList<Entry>()
        var isFindFirstPointInSecondLine = false
        for (i in mSampleData!!.indices) {
            values.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
            if (mSampleSecondData!![i] != 0.0){
                if (!isFindFirstPointInSecondLine){
                    secondLineStartIndexOfFirstLine = i
                    isFindFirstPointInSecondLine = true
                }
                secondLineValues.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
            }
        }

        val dataSets = ArrayList<ILineDataSet>()
        set1 = initDataSet(values, mLineColor)
        set2 = initDataSet(secondLineValues, mSecondLineColor)
        if (set1 != null){
            dataSets.add(set1!!) // add the data sets
        }
        if (set2 != null){
            dataSets.add(set2!!) // add the data sets
        }

        // create a data object with the data sets
        val data = LineData(dataSets)

//         // set data
        chart.data = data
        calNiceLabel(mSampleData!!)
        if (isShowDetail){
            if (set2 == null){
                secondLineStartIndexOfFirstLine = 0
            }
            chart.zoom(mSampleData!!.size / mPointCount*1f, 1f, 0f, 0f)
            chart.viewTreeObserver.addOnGlobalLayoutListener {
                if (ViewCompat.isLaidOut(chart) && isFirstIn) {
                    var deltaX = chart.viewPortHandler.contentWidth() / mPointCount
                    if (mSampleData!!.size - secondLineStartIndexOfFirstLine < mPointCount){
                        secondLineStartIndexOfFirstLine = mSampleData!!.size - mPointCount *5/4
                    }
                    var translateNegX = -deltaX * secondLineStartIndexOfFirstLine
                    var translatePosX = if (secondLineStartIndexOfFirstLine > mPointCount * SECOND_LINE_START_OFFSET_FACTOR) {
                        mPointCount * SECOND_LINE_START_OFFSET_FACTOR * deltaX
                    } else {
                        -translateNegX
                    }
                    chart.viewPortHandler.matrixTouch.postTranslate(translateNegX + translatePosX, 0f)
                    isFirstIn = false
                }
            }
        }
        chart.notifyDataSetChanged()
    }

    fun initDataSet(values: ArrayList<Entry>, lineColor: Int): LineDataSet? {
        if (values == null || values.isEmpty()){
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
            set1.setDrawIcons(true)
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
            } else {
                yAxisMax = min + 10
                if (yAxisMax > 100) {
                    yAxisMax = 100.0
                }
                yAxisMin = min - 10
                if (yAxisMin < 0) {
                    yAxisMin = 0.0
                }
            }
        }
        var interval = calNiceInterval(yAxisMin, yAxisMax)
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
        chart.setTouchEnabled(true)
        chart.setYLimitLabelBgColor(mAverageLabelBgColor)
        chart.animateX(500)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = false
        chart.isDragEnabled = true
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = false
        val marker = LineChartMarkView(context, mLineColor, mMarkViewTitle)
        marker.chartView = chart
        marker.setMarkTitleColor(mMarkViewTitleColor)
        marker.setMarkViewBgColor(mMarkViewBgColor)
        marker.setMarkViewValueColor(mMarkViewValueColor)
        chart.marker = marker
        chart.extraTopOffset = 48f
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
        set1?.setDrawIcons(false)
        set2?.setDrawIcons(false)
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
                set1?.setDrawVerticalHighlightIndicator(true)
                set1?.setDrawHorizontalHighlightIndicator(false)
                set2?.setDrawVerticalHighlightIndicator(true)
                set2?.setDrawHorizontalHighlightIndicator(false)
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
                e?.icon = drawableIcon
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
    fun setSecondLineColor(secondLineColor:Int){
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