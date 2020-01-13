package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_pie.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlin.math.roundToInt

class ReportBrainwaveSpectrumPieView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIsShowTitleIcon: Boolean = true
    private var mIsShowTitleMenuIcon: Boolean = true
    private var mTitle: String? = null
    private var mSpectrumColors: List<Int>? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_card_brain_spectrum_pie, null)

    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mBg: Drawable? = null
    private var mTitleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?
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
            R.styleable.ReportBrainwaveSpectrumPieView
        )
        mMainColor = typeArray.getColor(
            R.styleable.ReportBrainwaveSpectrumPieView_rbsp_mainColor,
            mMainColor
        )
        mTextColor = typeArray.getColor(
            R.styleable.ReportBrainwaveSpectrumPieView_rbsp_textColor,
            mTextColor
        )
        mBg = typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumPieView_rbsp_background)
        mTitleIcon =
            typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumPieView_rbsp_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumPieView_rbsp_titleMenuIcon)

        mIsShowTitleMenuIcon =
            typeArray.getBoolean(
                R.styleable.ReportBrainwaveSpectrumPieView_rbsp_isTitleMenuIconShow,
                true
            )
        mIsShowTitleIcon = typeArray.getBoolean(
            R.styleable.ReportBrainwaveSpectrumPieView_rbsp_isTitleIconShow,
            true
        )
        var color =
            typeArray.getString(R.styleable.ReportBrainwaveSpectrumPieView_rbsp_spectrumColors)

        if (color == null) {
            color = SPECTRUM_COLORS
        }
        mSpectrumColors = color.split(",")
            .map { Color.parseColor(it) }
        mTitle =
            typeArray.getString(R.styleable.ReportBrainwaveSpectrumPieView_rbsp_title)
        initView()

    }

    fun initView() {
        tv_title.text = mTitle
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
        pie_chart.setBackgroundColor(bgColor)
        legend_gamma.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_beta.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_alpha.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_theta.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_delta.setTextColor(getOpacityColor(mTextColor, 0.7f))

        if (mSpectrumColors != null) {
            legend_gamma.setLegendIconColor(mSpectrumColors!![0])
            legend_beta.setLegendIconColor(mSpectrumColors!![1])
            legend_alpha.setLegendIconColor(mSpectrumColors!![2])
            legend_theta.setLegendIconColor(mSpectrumColors!![3])
            legend_delta.setLegendIconColor(mSpectrumColors!![4])
        }

        initChart()
    }

    fun initChart() {
        pie_chart.setUsePercentValues(true)
        pie_chart.getDescription().setEnabled(false)
        pie_chart.setExtraOffsets(5f, 10f, 5f, 5f)
        pie_chart.setDrawEntryLabels(false)
        pie_chart.setDragDecelerationFrictionCoef(0.95f)
        pie_chart.setDrawHoleEnabled(true)
        pie_chart.setHoleColor(Color.WHITE)
        pie_chart.setUsePercentValues(false)
        pie_chart.setTransparentCircleColor(Color.WHITE)
        pie_chart.setTransparentCircleAlpha(110)
        pie_chart.setHoleRadius(58f)
        pie_chart.setTransparentCircleRadius(61f)

        pie_chart.setDrawCenterText(true)

        pie_chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        pie_chart.setRotationEnabled(false)
        pie_chart.setHighlightPerTapEnabled(false)
        pie_chart.legend.isEnabled = false

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

    }

    fun setData(percents: List<Float>) {
        var gammaPercent = (percents[0] * 100).roundToInt()
        var betaPercent = (percents[1] * 100).roundToInt()
        var alphaPercent = (percents[2] * 100).roundToInt()
        var thetaPercent = (percents[3] * 100).roundToInt()
        var deltaPercent = 0
        var sum = gammaPercent + betaPercent + alphaPercent + thetaPercent
        if (sum != 0) {
            deltaPercent = 100 - sum
        }
        legend_gamma.setText("γ wave ${gammaPercent}%")
        legend_beta.setText("β wave ${betaPercent}%")
        legend_alpha.setText("α wave ${alphaPercent}%")
        legend_theta.setText("θ wave ${thetaPercent}%")
        legend_delta.setText("δ wave ${deltaPercent}%")
        val entries = java.util.ArrayList<PieEntry>()
        for (percent in percents) {
            entries.add(PieEntry(percent))
        }

        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.setDrawValues(false)
        dataSet.setColors(mSpectrumColors)
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pie_chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pie_chart.setData(data)
        // undo all highlights
        pie_chart.highlightValues(null)
        pie_chart.invalidate()
    }

    fun setSpectrumColors(colors: List<Int>) {
        this.mSpectrumColors = colors
        initView()
    }
}