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
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.*
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.ll_bg
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.rl_no_data_cover
import kotlinx.android.synthetic.main.layout_common_card_title.view.*

class ReportBrainwaveSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mSpectrumColors: List<Int>? = null
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_brain_spectrum, null)

    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
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
            R.styleable.ReportBrainwaveSpectrumView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumView_rbs_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportBrainwaveSpectrumView_rbs_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mSample = typeArray.getInteger(R.styleable.ReportBrainwaveSpectrumView_rbs_sample, 3)
        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportBrainwaveSpectrumView_rbs_isAbsoluteTimeAxis, false)
        var color = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_spectrumColors)
        if (color == null) {
            color = SPECTRUM_COLORS
        }
        mSpectrumColors = color.split(",")
            .map { Color.parseColor(it) }
        initView()

    }

    fun initView() {
        tv_title.text = context.getString(R.string.brainwave_spectrum)
        tv_title.setTextColor(mMainColor)
        tv_vertical.setTextColor(getOpacityColor(mTextColor, 0.7f))
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
        sac_brain_chart.setBackgroundColor(bgColor)
        if (mIsShowInfoIcon) {
            iv_info.visibility = View.VISIBLE
        } else {
            iv_info.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        sac_brain_chart.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        sac_brain_chart.setGridLineColor(getOpacityColor(mTextColor, 0.1f))
        legend_gamma.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_beta.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_alpha.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_theta.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_delta.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
        legend_gamma.setLegendIconColor(mSpectrumColors!![0])
        legend_theta.setLegendIconColor(mSpectrumColors!![1])
        legend_alpha.setLegendIconColor(mSpectrumColors!![2])
        legend_theta.setLegendIconColor(mSpectrumColors!![3])
        legend_delta.setLegendIconColor(mSpectrumColors!![4])

        if (mIsAbsoluteTime) {
            tv_unit.visibility = View.GONE
        } else {
            tv_unit.visibility = View.VISIBLE
        }
    }

    fun setSpectrumColors(colors: List<Int>) {
        this.mSpectrumColors = colors
        initView()
    }

    fun setData(startTime: Long?, stackItems: ArrayList<StackedAreaChart.StackItem>) {
        sac_brain_chart.setStackItems(stackItems)
        if (mIsAbsoluteTime) {
            sac_brain_chart.isAbsoluteTime(true, startTime)
        }
    }

    fun setBrainwaveSpectrums(
        startTime: Long?,
        gamma: List<Double>?,
        beta: List<Double>?,
        alpha: List<Double>?,
        theta: List<Double>?,
        delta: List<Double>?
    ) {
        var items = ArrayList<StackedAreaChart.StackItem>()
        var gammaItem = StackedAreaChart.StackItem()
        var betaItem = StackedAreaChart.StackItem()
        var alphaItem = StackedAreaChart.StackItem()
        var thetaItem = StackedAreaChart.StackItem()
        var deltaItem = StackedAreaChart.StackItem()
        gammaItem.stackColor = mSpectrumColors!![0]
        gammaItem.stackData = gamma
        items.add(gammaItem)
        betaItem.stackColor = mSpectrumColors!![1]
        betaItem.stackData = beta
        items.add(betaItem)
        alphaItem.stackColor = mSpectrumColors!![2]
        alphaItem.stackData = alpha
        items.add(alphaItem)
        thetaItem.stackColor = mSpectrumColors!![3]
        thetaItem.stackData = theta
        items.add(thetaItem)
        deltaItem.stackColor = mSpectrumColors!![4]
        deltaItem.stackData = delta
        items.add(deltaItem)
        setData(startTime, items)
    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}