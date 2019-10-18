package cn.entertech.realtimedatasdk.report

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
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_card_heart_rate.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*

class ReportHeartRateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_heart_rate, null)

    private var mIsShowMin: Boolean = true
    private var mIsShowMax: Boolean = true
    private var mIsShowAvg: Boolean = true
    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mHearRateHighLineColor: Int = Color.parseColor("#ff4852")
    private var mHearRateNormalLineColor: Int = Color.parseColor("#ffc200")
    private var mHearRateLowLineColor: Int = Color.parseColor("#00d993")
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
            R.styleable.ReportHeartRateView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportHeartRateView_rhr_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportHeartRateView_rhr_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportHeartRateView_rhr_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportHeartRateView_rhr_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportHeartRateView_rhr_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }

        mIsShowAvg = typeArray.getBoolean(R.styleable.ReportHeartRateView_rhr_isShowAvg, true)
        mIsShowMax = typeArray.getBoolean(R.styleable.ReportHeartRateView_rhr_isShowMax, true)
        mIsShowMin = typeArray.getBoolean(R.styleable.ReportHeartRateView_rhr_isShowMin, true)
        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportHeartRateView_rhr_isAbsoluteTimeAxis, false)
        mHearRateHighLineColor =
            typeArray.getColor(R.styleable.ReportHeartRateView_rhr_heartRateHighLineColor, mHearRateHighLineColor)
        mHearRateNormalLineColor =
            typeArray.getColor(R.styleable.ReportHeartRateView_rhr_heartRateNormalLineColor, mHearRateNormalLineColor)
        mHearRateLowLineColor =
            typeArray.getColor(R.styleable.ReportHeartRateView_rhr_heartRateLowLineColor, mHearRateLowLineColor)
        initView()
    }

    fun initView() {
        tv_title.text = "心率"
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
        tv_time_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
        sac_brain_chart.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        sac_brain_chart.setYAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        sac_brain_chart.setGridLineColor(getOpacityColor(mTextColor, 0.7f))
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

        sac_brain_chart.setHeartRateHighLineColor(mHearRateHighLineColor)
        sac_brain_chart.setHeartRateLowLineColor(mHearRateLowLineColor)
        sac_brain_chart.setHeartRateNormalLineColor(mHearRateNormalLineColor)
        legend_low.setLegendIconColor(mHearRateLowLineColor)
        legend_normal.setLegendIconColor(mHearRateNormalLineColor)
        legend_high.setLegendIconColor(mHearRateHighLineColor)
        legend_low.setTextColor(mTextColor)
        legend_normal.setTextColor(mTextColor)
        legend_high.setTextColor(mTextColor)
    }

    fun setData(startTime: Long?, values: List<Double>?, max: Double?, min: Double?, avg: Double?) {
        if (values == null){
            return
        }
        sac_brain_chart.setValues(values)
        tv_heart_avg.text = "${context.getString(R.string.avg)}${avg?.toInt()}"
        tv_heart_max.text = "${context.getString(R.string.max)}${max?.toInt()}"
        tv_heart_min.text = "${context.getString(R.string.min)}${min?.toInt()}"
        if (mIsAbsoluteTime) {
            sac_brain_chart.isAbsoluteTime(true, startTime)
        }
    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}