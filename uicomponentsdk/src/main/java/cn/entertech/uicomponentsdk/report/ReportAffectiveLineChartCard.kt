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
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.formatData
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.chart
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_avg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_bg
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_max
import kotlinx.android.synthetic.main.layout_card_attention.view.ll_min
import kotlinx.android.synthetic.main.layout_card_attention.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_card_attention.view.tv_time_unit_des
import kotlinx.android.synthetic.main.layout_report_affective_card.view.*
import kotlinx.android.synthetic.main.layout_report_affective_card.view.legend_attention
import kotlinx.android.synthetic.main.layout_report_affective_card.view.legend_relaxation
import kotlinx.android.synthetic.main.pop_card_attention.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

class ReportAffectiveLineChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mAttentionAverage: Int = 0
    private var mRelaxationAverage: Int = 0
    private var mXAxisUnit: String? = "Time(min)"
    private var mGridLineColor: Int = Color.parseColor("#E9EBF1")
    private var mLabelColor: Int = Color.parseColor("#9AA1A9")

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
        initView()
    }

    fun initView() {
        initTitle()
        iv_menu.setOnClickListener {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            var view = LayoutInflater.from(context).inflate(R.layout.pop_card_attention,null)
            var affectiveView =
                ReportAffectiveLineChartCard(context, null, 0, R.layout.pop_card_attention)
            affectiveView.setLineWidth(mLineWidth)
            affectiveView.setRelaxationLineColor(mRelaxationLineColor)
            affectiveView.setAttentionLineColor(mAttentionLineColor)
            affectiveView.setTimeUnit(mTimeUnit)
            affectiveView.setPointCount(mPointCount)
            affectiveView.setXAxisUnit(mXAxisUnit)
            affectiveView.setGridLineColor(mGridLineColor)
            affectiveView.setLabelColor(mLabelColor)
            affectiveView.setAttentionAverage(mAttentionAverage)
            affectiveView.setRelaxationAverage(mRelaxationAverage)
            affectiveView.setData(mAttentionData, mRelaxationData, true)
            var popWindow = PopupWindow(affectiveView, MATCH_PARENT, MATCH_PARENT)
            popWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
            affectiveView.findViewById<TextView>(R.id.tv_title).text =
                context.getString(R.string.chart_full_screen_tip)
            affectiveView.findViewById<ImageView>(R.id.iv_menu)
                .setImageResource(R.drawable.vector_drawable_screen_shrink)
            affectiveView.findViewById<LinearLayout>(R.id.legend).visibility = View.VISIBLE
            affectiveView.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                popWindow.dismiss()
            }
        }
        initChart()
        tv_time_unit_des.setTextColor(getOpacityColor(mTextColor, 0.7f))

        var bgColor = Color.WHITE
        if (mBg != null) {
            ll_bg.background = mBg
        } else {
            mBg = ll_bg.background
        }
        if (mBg is ColorDrawable) {
            bgColor = (mBg as ColorDrawable).color
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bgColor = (mBg as GradientDrawable).color.defaultColor
            }
        }

        legend_attention.setLegendIconColor(mAttentionLineColor)
        legend_relaxation.setLegendIconColor(mRelaxationLineColor)

        tv_time_unit_des.text = mXAxisUnit
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
        if (mIsTitleMenuIconShow) {
            iv_menu.setImageDrawable(mTitleMenuIcon)
            iv_menu.visibility = View.VISIBLE
        } else {
            iv_menu.visibility = View.GONE
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
            llXAxis.lineColor = mGridLineColor
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
                average.toFloat(), "AVG:${if (average >= 100) {
                    mRelaxationAverage
                } else {
                    mAttentionAverage
                }}"
            )
            ll1.lineWidth = 1f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 14f
            ll1.lineColor = Color.parseColor("#9AA1A9")
            chart.axisLeft.addLimitLine(ll1)
        }
        val zeroLimitLine = LimitLine(100f, "")
        zeroLimitLine.lineWidth = 1f
        zeroLimitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        zeroLimitLine.textSize = 10f
        zeroLimitLine.lineColor = Color.parseColor("#9AA1A9")
        chart.axisLeft.addLimitLine(zeroLimitLine)
        val values = ArrayList<Entry>()

        for (i in sampleData.indices) {
            values.add(Entry(i.toFloat(), sampleData!![i].toFloat()))
        }

        val set1: LineDataSet
        set1 = LineDataSet(values, "")
        set1.color = lineColor
        set1.lineWidth = mLineWidth
        // draw points as solid circles
        set1.setDrawCircleHole(false)
        // customize legend entry
        set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f
        // text size of values
        set1.valueTextSize = 9f
        set1.setDrawValues(false)
        set1.setDrawHighlightIndicators(false)
        set1.setDrawFilled(false)
        set1.setDrawCircles(false)
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        val data = LineData(dataSets)
        // set data
        chart.data = data
        chart.notifyDataSetChanged()

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
        drawLine(mAttentionData!!, mAttentionAverage, mAttentionLineColor, isShowAllData)
        drawLine(
            mRelaxationData!!.map { it + 100 },
            mRelaxationAverage + 100,
            mRelaxationLineColor,
            isShowAllData
        )
//        chart.xAxis.setLabelCount(mData!!.size,true)
    }

    fun initChart() {
        chart.setBackgroundColor(Color.parseColor("#ffffff"))

        // disable description text
        chart.getDescription().setEnabled(false)
        chart.legend.isEnabled = false
        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.setDrawGridBackground(false)
        // enable scaling and dragging
        chart.setDragEnabled(true)
//        chart.setScaleEnabled(true)
        chart.setScaleXEnabled(true)
        chart.setScaleYEnabled(false)
        // force pinch zoom along both axis
        chart.setPinchZoom(true)
        val xAxis: XAxis = chart.xAxis
        // vertical grid lines
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = chart.axisLeft
        xAxis.setDrawLabels(false)
        chart.axisRight.isEnabled = false
        yAxis.mAxisRange
        yAxis.axisMaximum = 200f
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
        yAxis.setDrawLimitLinesBehindData(false)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
    }
    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
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


}