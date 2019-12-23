package cn.entertech.uicomponentsdk.report

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
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
//import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.layout_card_hrv.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import java.util.ArrayList

class ReportHeartRateVariabilityView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_hrv, null)

    private var mIsShowMin: Boolean = true
    private var mIsShowMax: Boolean = true
    private var mIsShowAvg: Boolean = true
    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mLineColor: Int = Color.parseColor("#ff4852")
    private var mIsAbsoluteTime: Boolean = false
    private var mSample: Int = 3
    private var mBg: Drawable? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
        const val SPECTRUM_COLORS = "#0921dd,#5167f8,#858aff,#bfadff,#f6e6ff"
    }

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportHeartRateVariabilityView
        )
        mMainColor =
            typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportHeartRateVariabilityView_hrv_background)
        mIsShowInfoIcon = typeArray.getBoolean(
            R.styleable.ReportHeartRateVariabilityView_hrv_isShowInfoIcon,
            true
        )
        mInfoUrl = typeArray.getString(R.styleable.ReportHeartRateVariabilityView_hrv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = ReportHeartRateView.INFO_URL
        }

        mIsShowAvg =
            typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowAvg, true)
        mIsShowMax =
            typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowMax, true)
        mIsShowMin =
            typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowMin, true)
        mIsAbsoluteTime = typeArray.getBoolean(
            R.styleable.ReportHeartRateVariabilityView_hrv_isAbsoluteTimeAxis,
            false
        )
        mLineColor =
            typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_lineColor, mLineColor)
        initView()

    }

    fun setData(data: List<Double>?) {
        if (data == null) {
            return
        }

        var maxValue = data.max()
        var minValue = data.min()
        val values = ArrayList<Entry>()
        var yAxisMax = (maxValue!! / 0.9f)
        var yAxisMix = (minValue!! * 0.9f)
        hrv_chart.axisLeft.axisMaximum = yAxisMax.toFloat()
        hrv_chart.axisLeft.axisMinimum = yAxisMix.toFloat()
        for (i in data.indices) {
            values.add(Entry(i.toFloat(), data[i].toFloat()))
        }

        val set1: LineDataSet

        if (hrv_chart.data != null && hrv_chart.data.dataSetCount > 0) {
            set1 = hrv_chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            hrv_chart.data.notifyDataChanged()
            hrv_chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")

//            set1.setDrawIcons(false)
            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set1.color = Color.RED
//            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
//            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f
            set1.setDrawValues(false)
            // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            set1.setDrawHighlightIndicators(false)
            set1.setDrawFilled(false)
            set1.setDrawCircles(false)
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER)
            // set color of filled area
//            if (Utils.getSDKInt() >= 18) {
//                // drawables only supported on api level 18 and above
//                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
//                set1.fillDrawable = drawable
//            } else {
//                set1.fillColor = Color.BLACK
//            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            hrv_chart.data = data

            hrv_chart.notifyDataSetChanged()
        }
    }

    fun initChart() {
        hrv_chart.setBackgroundColor(Color.parseColor("#ffffff"))

        // disable description text
        hrv_chart.getDescription().setEnabled(false)
        hrv_chart.legend.isEnabled = false

        // enable touch gestures
        hrv_chart.setTouchEnabled(true)

        hrv_chart.setDrawGridBackground(false)
        // enable scaling and dragging
        hrv_chart.setDragEnabled(true)
//        chart.setScaleEnabled(true)
        hrv_chart.setScaleXEnabled(true)
        hrv_chart.setScaleYEnabled(false)
        // force pinch zoom along both axis
        hrv_chart.setPinchZoom(true)
//        hrv_chart.viewPortHandler.setMaximumScaleX(5f)
        val xAxis: XAxis = hrv_chart.xAxis
        // vertical grid lines
        xAxis.setDrawAxisLine(false)
        xAxis.gridColor = Color.parseColor("#999999")
//        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis: YAxis = hrv_chart.axisLeft
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float, base: AxisBase
            ): String? {
                Log.d("####", "min is : ${(value * 0.8f / 60).toInt()}")
                return "${(value * 0.4f / 60).toInt()}"
            }
        }
        xAxis.setLabelCount(7, true)
//        xAxis.spaceMax = 300f
        // disable dual axis (only use LEFT axis)
        hrv_chart.axisRight.isEnabled = false
        // horizontal grid lines
//        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.setDrawGridLines(false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        // axis range
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 20f
        yAxis.labelCount = 3
        // // Create Limit Lines // //
        val llXAxis = LimitLine(9f, "")
        llXAxis.lineWidth = 2f
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
        llXAxis.textSize = 10f

        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.addLimitLine(ll1)
//        xAxis.addLimitLine(llXAxis);
    }

    fun initView() {
        initChart()
        tv_title.text = "心率变异性"
        tv_title.setTextColor(mMainColor)
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
        hrv_chart.setBackgroundColor(bgColor)
        if (mIsShowInfoIcon) {
            iv_info.visibility = View.VISIBLE
        } else {
            iv_info.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        tv_time_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
//        hrv_chart.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
//        hrv_chart.setYAxisTextColor(getOpacityColor(mTextColor, 0.7f))
//        hrv_chart.setGridLineColor(getOpacityColor(mTextColor, 0.7f))
        tv_heart_avg.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_heart_max.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_heart_min.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_avg_unit.setTextColor(getOpacityColor(mTextColor, 0.5f))
        tv_max_unit.setTextColor(getOpacityColor(mTextColor, 0.5f))
        tv_min_unit.setTextColor(getOpacityColor(mTextColor, 0.5f))
        vertical_text.setTextColor(getOpacityColor(mTextColor, 0.7f))
        if (mIsShowAvg) {
            ll_avg.visibility = View.VISIBLE
        } else {
            ll_avg.visibility = View.GONE
        }
        if (mIsShowMax) {
            ll_max.visibility = View.VISIBLE
        } else {
            ll_max.visibility = View.GONE
        }
        if (mIsShowMin) {
            ll_min.visibility = View.VISIBLE
        } else {
            ll_min.visibility = View.GONE
        }

        if (mIsAbsoluteTime) {
            tv_time_unit.visibility = View.GONE
        } else {
            tv_time_unit.visibility = View.VISIBLE
        }
//        hrv_chart.setLineColor(mLineColor)
    }

//    fun setData(startTime: Long, data: List<Double>?, avg: Double?) {
//        if (data == null) {
//            return
//        }
//        hrv_chart.setValues(data)
//        tv_heart_avg.text = "${context.getString(R.string.avg)}${avg?.toInt()}"
//        if (mIsAbsoluteTime) {
//            hrv_chart.isAbsoluteTime(true, startTime)
//        }
//    }

    fun isDataNull(flag: Boolean) {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_no_data_cover).visibility =
            if (flag) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}