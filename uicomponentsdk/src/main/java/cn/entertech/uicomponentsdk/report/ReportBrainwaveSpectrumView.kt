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
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
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
import kotlinx.android.synthetic.main.layout_card_attention.view.*
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.*
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.chart
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.legend_alpha
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.legend_delta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.legend_gamma
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.legend_theta
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.ll_bg
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import java.util.*

class ReportBrainwaveSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mXAxisUnit: String? = "Time(min)"
    private var mSelfView: View? = null
    private var mBrainwaveSpectrums: List<List<Double>>? = null
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

    /*数据时间间隔：单位毫秒*/
    var mTimeUnit: Int = 400
    var mPointCount: Int = 100
    var mTimeOfTwoPoint: Int = 0

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
        const val SPECTRUM_COLORS = "#FF6682,#5E75FF,#F7C77E,#5FC695,#FB9C98"
    }

    init {
        if (layoutId == null) {
            mSelfView =
                LayoutInflater.from(context).inflate(R.layout.layout_card_brain_spectrum, null)
            var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            mSelfView?.layoutParams = layoutParams
        } else {
            mSelfView = LayoutInflater.from(context).inflate(R.layout.pop_card_brain_spectrum, null)
            var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            mSelfView?.layoutParams = layoutParams
        }
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportBrainwaveSpectrumView
        )
        mTitleText = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_title)
        mXAxisUnit = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_xAxisUnit)
        mMainColor =
            typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumView_rbs_background)
        mTitleIcon = typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumView_rbs_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumView_rbs_titleMenuIcon)
        mIsShowTitleIcon =
            typeArray.getBoolean(R.styleable.ReportBrainwaveSpectrumView_rbs_isTitleIconShow, true)
        mIsShowTitleMenuIcon = typeArray.getBoolean(
            R.styleable.ReportBrainwaveSpectrumView_rbs_isTitleMenuIconShow,
            true
        )
        mLabelColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_labelColor, mLabelColor)
        mGridLineColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_gridLineColor, mGridLineColor)

        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTimeUnit =
            typeArray.getInteger(R.styleable.ReportBrainwaveSpectrumView_rbs_timeUnit, mTimeUnit)

        mPointCount =
            typeArray.getInteger(R.styleable.ReportBrainwaveSpectrumView_rbs_pointCount, 100)
        var color = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_spectrumColors)
        if (color == null) {
            color = SPECTRUM_COLORS
        }
        mSpectrumColors = color.split(",")
            .map { Color.parseColor(it) }
        initView()
        initChart()
    }

    fun initView() {
        tv_title.text = mTitleText
        tv_title.setTextColor(mTextColor)
        if (mIsShowTitleIcon) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTitleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsShowTitleMenuIcon) {
            iv_menu.visibility = View.VISIBLE
            iv_menu.setImageDrawable(mTitleMenuIcon)
        } else {
            iv_menu.visibility = View.GONE
        }
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
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        tv_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
        if (mIsAbsoluteTime) {
            tv_unit.visibility = View.GONE
        } else {
            tv_unit.visibility = View.VISIBLE
        }

        if (mSpectrumColors != null) {
            legend_gamma.setLegendIconColor(mSpectrumColors!![0])
            legend_theta.setLegendIconColor(mSpectrumColors!![1])
            legend_alpha.setLegendIconColor(mSpectrumColors!![2])
            legend_theta.setLegendIconColor(mSpectrumColors!![3])
            legend_delta.setLegendIconColor(mSpectrumColors!![4])
        }

        iv_menu.setOnClickListener {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            var affectiveView =
                ReportBrainwaveSpectrumView(context, null, 0, R.layout.pop_card_brain_spectrum)
            affectiveView.setData(mBrainwaveSpectrums, true)
            affectiveView.setTimeUnit(mTimeUnit)
            affectiveView.setPointCount(mPointCount)
            affectiveView.setGridLineColor(mGridLineColor)
            affectiveView.setXAxisUnit(mXAxisUnit)
            affectiveView.setLabelColor(mLabelColor)
            var popWindow = PopupWindow(affectiveView, MATCH_PARENT, MATCH_PARENT)
            affectiveView.findViewById<TextView>(R.id.tv_title).text = "Zoom in on the curve and slide to view it."
            affectiveView.findViewById<ImageView>(R.id.iv_menu)
                .setImageResource(R.drawable.vector_drawable_screen_shrink)
            affectiveView.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                popWindow.dismiss()
            }
            popWindow.showAtLocation(iv_menu, Gravity.CENTER, 0, 0)
        }

        tv_unit.text = mXAxisUnit
    }

    fun setData(brainwaveSpectrums: List<List<Double>>?, isShowAllData: Boolean = false) {
        if (brainwaveSpectrums == null) {
            return
        }
        this.mBrainwaveSpectrums = brainwaveSpectrums
        var sample = brainwaveSpectrums[0]!!.size / mPointCount
        if (isShowAllData || sample <= 1) {
            sample = 1
        }
        var sampleData = ArrayList<Double>()
        for (i in brainwaveSpectrums[0]!!.indices) {
            if (i % sample == 0) {
                sampleData.add(brainwaveSpectrums[0]!![i])
            }
        }

        mTimeOfTwoPoint = mTimeUnit * sample
        var totalMin = brainwaveSpectrums[0]!!.size * mTimeUnit / 1000F / 60F
        var minOffset = (totalMin / 8).toInt() + 1
        var currentMin = 0
        while (currentMin < totalMin) {
            var limitX = currentMin * 60f * 1000 / mTimeOfTwoPoint
            val llXAxis = LimitLine(limitX, "$currentMin")
            llXAxis.lineWidth = 1f
            llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            llXAxis.textSize = 8f
            llXAxis.yOffset = -12f
            llXAxis.lineColor = mGridLineColor
            llXAxis.textColor = mLabelColor
            if (currentMin == 0) {
                llXAxis.xOffset = -3f
            } else {
                llXAxis.xOffset = 0f
            }
            chart.xAxis.addLimitLine(llXAxis)
            currentMin += minOffset
        }

        val dataSets = ArrayList<ILineDataSet>()
        for (i in 0..4) {
            val values = ArrayList<Entry>()
            for (j in sampleData.indices) {
                if (i == 0) {
                    values.add(Entry(j.toFloat(), 100f))
                } else if (i == 1) {
                    values.add(Entry(j.toFloat(), (100 * (1 - brainwaveSpectrums[0][j])).toFloat()))
                } else if (i == 2) {
                    values.add(
                        Entry(
                            j.toFloat(),
                            (100 * (1 - brainwaveSpectrums[0][j] - brainwaveSpectrums[1][j])).toFloat()
                        )
                    )
                } else if (i == 3) {
                    values.add(
                        Entry(
                            j.toFloat(),
                            (100 * (1 - brainwaveSpectrums[0][j] - brainwaveSpectrums[1][j] - brainwaveSpectrums[2][j])).toFloat()
                        )
                    )
                } else if (i == 4) {
                    values.add(
                        Entry(
                            j.toFloat(),
                            (100 * (1 - brainwaveSpectrums[0][j] - brainwaveSpectrums[1][j] - brainwaveSpectrums[2][j] - brainwaveSpectrums[3][j])).toFloat()
                        )
                    )
                }
            }

            val set1: LineDataSet
            set1 = LineDataSet(values, "")
            set1.lineWidth = 0f
            set1.setDrawCircleHole(false)
            set1.formLineWidth = 0f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            // text size of values
            set1.valueTextSize = 9f
            set1.setDrawValues(false)
            set1.lineWidth = 0f
            set1.setDrawHighlightIndicators(false)
            set1.setDrawFilled(true)
            set1.setDrawCircles(false)
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
            set1.fillAlpha = 255
            set1.fillColor = mSpectrumColors!![i]
            dataSets.add(set1) // add the data sets
        }

        // create a data object with the data sets
        val data = LineData(dataSets)
        // set data
        chart.data = data
        chart.notifyDataSetChanged()

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
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        // axis range
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.labelCount = 3
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {
                return ""
            }
        }

        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(false)
        // add limit lines
        yAxis.addLimitLine(ll1)
    }

    fun setSpectrumColors(colors: List<Int>) {
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


}