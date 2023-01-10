package cn.entertech.uicomponentsdk.report

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
import android.util.AttributeSet
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.ViewCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.activity.SessionCommonChartFullScreenActivity
import cn.entertech.uicomponentsdk.utils.*
import cn.entertech.uicomponentsdk.utils.ScreenUtil.isPad
import cn.entertech.uicomponentsdk.widget.ChartIconView
import cn.entertech.uicomponentsdk.widget.ChartMoreListAdapter
import cn.entertech.uicomponentsdk.widget.SessionCommonChartMarkView
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
import kotlinx.android.synthetic.main.layout_card_attention.view.chart
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_card_bar_chart.view.*
import kotlinx.android.synthetic.main.layout_session_common_chart.view.*
import kotlinx.android.synthetic.main.layout_session_common_chart.view.ll_chart
import kotlinx.android.synthetic.main.layout_session_common_chart.view.ll_title
import kotlinx.android.synthetic.main.layout_session_common_chart.view.tv_date_fullscreen
import kotlinx.android.synthetic.main.layout_session_common_chart.view.tv_level
import kotlinx.android.synthetic.main.layout_session_common_chart.view.tv_unit
import kotlinx.android.synthetic.main.layout_session_common_chart.view.tv_value
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class SessionCommonChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var onDateSelectListener: ((String) -> Unit)? = null
    private var mIsDataAverageInt: Boolean = true
    private var lineFlagTotalTime: Int? = 0
    private var mDataType: Int = 0
    private var mSourceDataList: List<Double>? = null
    private var mStartTime: String = ""
    private var mLevelBgColor: Int = Color.parseColor("#E5EAF7")
    private var mLevelTextColor: Int = Color.parseColor("#2B2E40")
    private var mIsShowLevel: Boolean = false
    private var dataTotalTimeMs: Int = 0
    private var mDataAverage: Double = 0.0
    private var mXAxisLineColor: Int = Color.parseColor("#9AA1A9")
    private var mIsShowXAxisUnit: Boolean = false
    private var mTitleUnit: String? = ""
    private var mTitleDescription: String? = ""
    private var mBgLineColor: Int = Color.parseColor("#DDE1EB")
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
    private var mBadQualityLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")
    private var mIsTitleMenuIconShow: Boolean = true
    private var mIsTitleMenuIconBgShow: Boolean = false
    private var mFirstData: List<Double>? = null
    private var mLineFlagData: List<Double>? = null
    private var mSampleFlagData: List<Double>? = null
    private var mBg: Drawable? = null

    private var mTitleMenuIcon: Drawable?
    private var mTitleMenuIconBg: Drawable?

    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#333333")
    private var mIconColor: Int = Color.parseColor("#ff0000")
    private var mDateBgColor: Int = Color.parseColor("#F2F2F7")
    var mSelfView: View? = null

    companion object {
        const val SECOND_LINE_START_OFFSET_FACTOR = 0.25F
    }

    var secondLineStartIndexOfFirstLine = -1

    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 600
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
                LayoutInflater.from(context).inflate(R.layout.layout_session_common_chart, null)
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
            R.styleable.SessionCommonChart
        )
        mMainColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_mainColor, mMainColor)
        mSecondLineColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_secondLineColor, mSecondLineColor)
        mLineColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_lineColor, mLineColor)
        mTextColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.SessionCommonChart_scc_background)

        mIsTitleMenuIconShow = typeArray.getBoolean(
            R.styleable.SessionCommonChart_scc_isTitleMenuIconShow,
            mIsTitleMenuIconShow
        )
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.SessionCommonChart_scc_titleMenuIcon)
        mIsTitleMenuIconBgShow = typeArray.getBoolean(
            R.styleable.SessionCommonChart_scc_isShowMenuIconBg,
            mIsTitleMenuIconBgShow
        )
        mTitleMenuIconBg =
            typeArray.getDrawable(R.styleable.SessionCommonChart_scc_menuIconBg)

        mPointCount =
            typeArray.getInteger(
                R.styleable.SessionCommonChart_scc_pointCount,
                mPointCount
            )
        mBgLineColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_bgLineColor,
            mBgLineColor
        )
        mBadQualityLineColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_badQualityLineColor,
            mBadQualityLineColor
        )
        mLabelColor =
            typeArray.getColor(
                R.styleable.SessionCommonChart_scc_labelColor,
                mLabelColor
            )
        mGridLineColor =
            typeArray.getColor(
                R.styleable.SessionCommonChart_scc_gridLineColor,
                mGridLineColor
            )
        mTimeUnit =
            typeArray.getInteger(R.styleable.SessionCommonChart_scc_timeUnit, mTimeUnit)
        mLineWidth = ScreenUtil.px2dip(
            context,
            typeArray.getDimension(
                R.styleable.SessionCommonChart_scc_lineWidth,
                mLineWidth
            )
        ).toFloat()
        mXAxisUnit = typeArray.getString(R.styleable.SessionCommonChart_scc_xAxisUnit)
        mIsDrawFill =
            typeArray.getBoolean(R.styleable.SessionCommonChart_scc_isDrawFill, false)

        mHighlightLineColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_highlightLineColor,
            mHighlightLineColor
        )
        mHighlightLineWidth = typeArray.getFloat(
            R.styleable.SessionCommonChart_scc_highlightLineWidth,
            mHighlightLineWidth
        )
        mMarkViewBgColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_markViewBgColor,
            mMarkViewBgColor
        )
        mMarkViewTitle =
            typeArray.getString(R.styleable.SessionCommonChart_scc_markViewTitle)
        mMarkViewTitleColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_markViewTitleColor,
            mMarkViewTitleColor
        )
        mMarkViewValueColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_markViewValueColor,
            mMarkViewValueColor
        )
        mAverageLabelBgColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_averageLabelBgColor,
            mAverageLabelBgColor
        )
        mTitleDescription =
            typeArray.getString(R.styleable.SessionCommonChart_scc_title_description)
        mTitleUnit = typeArray.getString(R.styleable.SessionCommonChart_scc_title_unit)
        mIsShowXAxisUnit =
            typeArray.getBoolean(R.styleable.SessionCommonChart_scc_showXAxisUnit, false)
        mXAxisLineColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_xAxisLineColor, mXAxisLineColor)
        mIsShowLevel = typeArray.getBoolean(R.styleable.SessionCommonChart_scc_isShowLevel, false)
        mLevelBgColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_valueLevelBgColor, mLevelBgColor)
        mLevelTextColor = typeArray.getColor(
            R.styleable.SessionCommonChart_scc_valueLevelTextColor,
            mLevelTextColor
        )
        mIsDataAverageInt =
            typeArray.getBoolean(R.styleable.SessionCommonChart_scc_isDataAverageInt, true)
        mDataType = typeArray.getInt(R.styleable.SessionCommonChart_scc_dataType, 0)
        mIconColor = typeArray.getColor(R.styleable.SessionCommonChart_scc_iconColor, mIconColor)
        mDateBgColor =
            typeArray.getColor(R.styleable.SessionCommonChart_scc_dateBgColor, mDateBgColor)
        typeArray.recycle()
        initView()
    }


    fun initView() {
        initBg()
        initTitle()
        initTimeUnit()
        initChart()
        initChartIcon()
        initDateSelector()
    }

    fun setOnDateSelectListener(listener: (String) -> Unit) {
        this.onDateSelectListener = listener
    }

    fun initDateSelector() {
        val lp = ll_chart.layoutParams as RelativeLayout.LayoutParams
        if (isFullScreen) {
            lp.topMargin = 26f.dp().toInt()
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
        tv_date_fullscreen.setTextColor(mTextColor)
        tv_date.iconTint = ColorStateList.valueOf(mTextColor)
        tv_date.setOnClickListener {
            onDateSelectListener?.invoke(mStartTime)
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
        if (mIsTitleMenuIconShow && isFullScreen) {
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
        tv_value.setTextColor(mMainColor)
        tv_description.setTextColor(mTextColor)
        tv_unit.setTextColor(mTextColor)
        if (mIsShowLevel) {
            tv_level.setTextColor(mLevelTextColor)
            var bg = tv_level.background as GradientDrawable
            bg.setColor(mLevelBgColor)
        } else {
            tv_unit.text = mTitleUnit
        }
        tv_date.setTextColor(mTextColor)
        tv_date_fullscreen.setTextColor(mTextColor)

        if (!mIsTitleMenuIconBgShow) {
            iv_menu_icon.setOnClickListener {
                if (isFullScreen) {
                    (context as Activity).finish()
                } else {
                    fullScreen()
                }

            }
        }

    }

    fun fullScreen() {
        var intent = Intent(context, SessionCommonChartFullScreenActivity::class.java)
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
        intent.putExtra("secondLineColor", mSecondLineColor)
        intent.putExtra("lineData", mSourceDataList?.toDoubleArray())
        intent.putExtra("lineDataAverage", mDataAverage)
        intent.putExtra("secondLineData", mLineFlagData?.toDoubleArray())
        intent.putExtra("qualityData", mSourceQualityData?.toDoubleArray())
        intent.putExtra("badQualityLineColor", mBadQualityLineColor)
        intent.putExtra("bgLineColor", mBgLineColor)
        intent.putExtra("titleDescription", mTitleDescription)
        intent.putExtra("titleUnit", mTitleUnit)
        intent.putExtra("isShowLevel", mIsShowLevel)
        intent.putExtra("levelBgColor", mLevelBgColor)
        intent.putExtra("levelTextColor", mLevelTextColor)
        intent.putExtra("startTime", mStartTime)
        intent.putExtra("dataTotalTime", dataTotalTimeMs)
        intent.putExtra("lineFlagTotalTime", lineFlagTotalTime)
        intent.putExtra("isDataAverageInt", mIsDataAverageInt)
        context.startActivity(intent)
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

    var drawByQuality = false
    private var mSourceQualityData: List<Double>? = null
    private var mSampleQualityData: ArrayList<Double>? = null
    fun setQualityRec(qualityRec: List<Double>?) {
        this.mSourceQualityData = qualityRec
    }

    fun fillQualityDataBySourceData(sourceData:List<Double>,qualityData:List<Double>):ArrayList<Double>{
        var newQualityData = ArrayList<Double>()
        val qualityRecSize = qualityData.size
        val sourceDataSize = sourceData.size
        if (qualityRecSize < sourceDataSize){
            newQualityData.addAll(qualityData)
            val deltaSize = sourceDataSize - qualityRecSize
            for (i in 0 until  deltaSize){
                newQualityData.add(2.0)
            }
        }else{
            for (i in 0 until sourceDataSize){
                newQualityData.add(qualityData[i])
            }
        }
        return newQualityData
    }

    @SuppressLint("SetTextI18n")
    fun setData(
        data: List<Double>?, dataAverage: Double? = null,
        lineFlagData: List<Double>? = null,
        lineFlagTotalTime: Int? = null,
        isShowAllData: Boolean = false
    ) {
        if (data == null) {
            return
        }
        this.mSourceDataList = data
        this.dataTotalTimeMs = data.size * mTimeUnit
        this.lineFlagTotalTime = lineFlagTotalTime
        if (dataAverage == null) {
            this.mDataAverage = data.average()
        } else {
            this.mDataAverage = dataAverage
        }
        this.mFirstData = formatData(data)
        this.mLineFlagData = lineFlagData
        var sample = mFirstData!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        setHeadView(data, sample)
        if (mSourceQualityData == null){
            drawByQuality = false
            mSourceQualityData = mSourceDataList!!.map { 2.0 }
        }else{
            drawByQuality = true
            mSourceQualityData = fillQualityDataBySourceData(mSourceDataList!!,mSourceQualityData!!)
        }
        mSampleQualityData = sampleData(mSourceQualityData!!, sample)
        val qualityFlagRec = curveByQuality(mSampleQualityData!!)
        mSampleData = sampleData(mFirstData, sample)
        setXLimitLine(sample)
        drawAverageLine()
        val dataSets = ArrayList<ILineDataSet>()
        val bgLineValues = getBgLineValues()
        if (drawByQuality){
            set1 = initDataSet(bgLineValues, mBadQualityLineColor)
        }else{
            set1 = initDataSet(bgLineValues, mLineColor)
        }
        if (set1 != null) {
            dataSets.add(set1!!) // add the data sets
        }
        if (drawByQuality){
            val qualitySets = getSetsByQuality(mSampleData!!,qualityFlagRec,mLineColor)
            for (set in qualitySets) {
                dataSets.add(set)
            }
        }
        if (mLineFlagData != null){
            mSampleFlagData = sampleData(mLineFlagData, sample)
            val flagSets = getSetByQualityAndFlag(mSampleData!!,qualityFlagRec!!,mSampleFlagData!!,mSecondLineColor)
            for (set in flagSets){
                dataSets.add(set)
            }
        }
        // create a data object with the data sets
        val lineData = LineData(dataSets)
//
        chart.data = lineData
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
        initChart()
        chart.notifyDataSetChanged()
    }

    fun setHeadView(data: List<Double>, sample: Int) {
        if (mLineFlagData != null) {
            var secondLineData1 = processSecondLineDataByFirstLine(data, mLineFlagData)
            mSampleSecondData = sampleData(secondLineData1, sample)
            if (lineFlagTotalTime != null) {
                tv_value.text = "$lineFlagTotalTime"
                tv_description.text = context.getString(R.string.chart_title_total)
            }
        } else {
            var average = if (mIsDataAverageInt) {
                "${ceil(mDataAverage).toInt()}"
            } else {
                var decimalFormat = DecimalFormat("0.0")
                decimalFormat.format(mDataAverage)
            }
            tv_value.text = average
            tv_description.text = context.getString(R.string.chart_title_average)
        }
        if (mIsShowLevel) {
            when (mDataAverage) {
                in 0.0..29.99 -> tv_unit.text = "(${context.getString(R.string.sdk_report_low)})"
                in 30.0..69.99 -> tv_unit.text = "(${context.getString(R.string.sdk_report_nor)})"
                else -> tv_unit.text = "(${context.getString(R.string.sdk_report_high)})"
            }
        }
    }

    fun setXLimitLine(sample: Int) {
        mTimeOfTwoPoint = mTimeUnit * sample
        chart.xAxis.removeAllLimitLines()
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
    }

    fun drawAverageLine() {
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
    }

    fun getBgLineValues(): ArrayList<Entry> {
        val values = ArrayList<Entry>()
        for (i in mSampleData!!.indices) {
            if (mSampleSecondData != null) {
                values.add(Entry(i.toFloat(), mSampleData!![i].toFloat(), mSampleSecondData!![i]))
            } else {
                values.add(Entry(i.toFloat(), mSampleData!![i].toFloat()))
            }
        }
        return values
    }

    fun getFlagSets(): ArrayList<LineDataSet> {
        val flagSets = ArrayList<LineDataSet>()
        var tempLineValues: ArrayList<Entry>? = null
        var isFindFirstPointInSecondLine = false
        if (mSampleSecondData != null) {
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
                                flagSets.add(set)
                            }
                        }
                    }
                } else {
                    if (i - 1 >= 0 && mSampleSecondData!![i - 1] != 0.0) {
                        if (tempLineValues?.size ?: 0 > 5) {
                            var set = initDataSet(tempLineValues!!, mSecondLineColor)
                            if (set != null) {
                                flagSets.add(set)
                            }
                        }
                    }
                }
            }
        }
        return flagSets
    }


    fun getSetByQualityAndFlag(
        data: ArrayList<Double>,
        qualityRec: List<Double>,
        flagRec: List<Double>,
        color: Int
    ): ArrayList<LineDataSet> {
        val flagSets = ArrayList<LineDataSet>()
        var tempLineValues: ArrayList<Entry>? = null
        var isFindFirstPointInSecondLine = false
        for (i in data.indices) {
            if (qualityRec!![i] != 0.0 && flagRec[i] != 0.0) {
                if (i == 0 || (i - 1 >= 0 && (qualityRec[i - 1] == 0.0) || flagRec[i - 1] == 0.0)) {
                    if (!isFindFirstPointInSecondLine) {
                        isFindFirstPointInSecondLine = true
                    }
                    tempLineValues = ArrayList()
                }
                tempLineValues?.add(Entry(i.toFloat(), data[i].toFloat()))
                if (i == data.size - 1) {
//                    if (tempLineValues?.size ?: 0 > 5) {
                    var set = initDataSet(tempLineValues!!, color)
                    if (set != null) {
                        flagSets.add(set)
                    }
//                    }
                }
            } else {
                if (i - 1 >= 0 && qualityRec[i - 1] != 0.0 && flagRec[i - 1] != 0.0) {
//                    if (tempLineValues?.size ?: 0 > 5) {
                        var set = initDataSet(tempLineValues!!, color)
                        if (set != null) {
                            flagSets.add(set)
                        }
//                    }
                }
            }
        }
        return flagSets
    }

    fun getSetsByQuality(
        data: ArrayList<Double>,
        qualityRec: List<Double>?,
        color: Int
    ): ArrayList<LineDataSet> {
        val flagSets = ArrayList<LineDataSet>()
        var tempLineValues: ArrayList<Entry>? = null
        var isFindFirstPointInSecondLine = false
        if (qualityRec != null) {
            for (i in data!!.indices) {
                if (qualityRec!![i] != 0.0) {
                    if (i == 0 || (i - 1 >= 0 && qualityRec[i - 1] == 0.0)) {
                        if (!isFindFirstPointInSecondLine) {
                            isFindFirstPointInSecondLine = true
                        }
                        tempLineValues = ArrayList()
                    }
                    tempLineValues?.add(Entry(i.toFloat(), data[i].toFloat()))
                    if (i == data.size - 1) {
//                        if (tempLineValues?.size ?: 0 > 5) {
                            var set = initDataSet(tempLineValues!!, color)
                            if (set != null) {
                                flagSets.add(set)
                            }
//                        }
                    }
                } else {
                    if (i - 1 >= 0 && qualityRec!![i - 1] != 0.0) {
//                        if (tempLineValues?.size ?: 0 > 5) {
                            var set = initDataSet(tempLineValues!!, color)
                            if (set != null) {
                                flagSets.add(set)
                            }
//                        }
                    }
                }
            }
        }
        return flagSets
    }

    fun initDataSet(values: ArrayList<Entry>, lineColor: Int): LineDataSet? {
        if (values.isEmpty()) {
            return null
        }
        var set: LineDataSet
//        if (chart.data != null && chart.data.dataSetCount > 0) {
//            set = chart.data.getDataSetByIndex(0) as LineDataSet
//            set.values = values
//            set.notifyDataSetChanged()
//            chart.data.notifyDataChanged()
//            chart.notifyDataSetChanged()
//        } else {
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
                            mLineColor,
                            getOpacityColor(mLineColor, 0.5F)
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
//        }
        return set

    }

    private fun calNiceLabel(data: List<Double>) {
        var min = data.minOrNull()
        var max = data.maxOrNull()
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
        var interval: Int
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
        val marker = SessionCommonChartMarkView(
            context,
            mMarkViewTitle,
            mStartTime,
            mSampleSecondData != null
        )
        marker.chartView = chart
        marker.setMainColor(mMainColor)
        marker.setTextColor(mTextColor)
        marker.setShowLevel(mIsShowLevel, mLevelTextColor, mLevelBgColor)
        marker.setUnit(mTitleUnit)
        marker.setYOffset(10f.dp())
        marker.setMarkViewBgColor(mMarkViewBgColor)
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
        chart.setMaxVisibleValueCount(100000)
        yAxis.setDrawLabels(mIsDrawYAxisLabels)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.setLabelCount(5, false)
        yAxis.setDrawGridLines(true)
        yAxis.gridColor = mBgLineColor
        yAxis.gridLineWidth = 0.5f
        yAxis.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 10f), 0f))
        yAxis.textSize = 12f
        yAxis.textColor = mTextColor
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
        var startTimeHourMin = TimeUtils.getFormatTime(startTimestamp, "hh:mm a")
        var endTimeHourMin = TimeUtils.getFormatTime(endTimestamp, "hh:mm a")
        if (startTimeDay == endTimeDay) {
            tv_date.text = "$startTimeDay ${startTimeHourMin}-${endTimeHourMin}"
            tv_date_fullscreen.text = "$startTimeDay ${startTimeHourMin}-${endTimeHourMin}"
        } else {
            tv_date.text = "$startTimeDay ${startTimeHourMin}-$endTimeDay ${endTimeHourMin} "
            tv_date_fullscreen.text =
                "$startTimeDay ${startTimeHourMin}-$endTimeDay ${endTimeHourMin} "
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
    fun setBadQualityLineColor(color: Int) {
        this.mBadQualityLineColor = color
        initView()
    }
    fun setTitleDescription(title: String) {
        this.mTitleDescription = title
        initView()
    }

    fun setTitleUnit(title: String) {
        this.mTitleUnit = title
        initView()
    }

    fun setShowLevel(showLevel: Boolean) {
        this.mIsShowLevel = showLevel
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

    fun setDataTotalTimeMs(time: Int) {
        this.dataTotalTimeMs = time
        initView()
    }

    fun setMainColor(mainColor: Int) {
        this.mMainColor = mainColor
        initView()
    }

    fun setDataType(dataType: Int) {
        this.mDataType = dataType
        initView()
    }

    fun setDataAverageInt(flag: Boolean) {
        this.mIsDataAverageInt = flag
        initView()
    }

    fun setLineFlagTotalTime(lineFlagTotalTime: Int) {
        this.lineFlagTotalTime = lineFlagTotalTime
        initView()
    }

}