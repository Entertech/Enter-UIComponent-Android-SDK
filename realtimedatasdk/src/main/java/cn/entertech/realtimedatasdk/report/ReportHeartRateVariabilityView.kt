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
import android.widget.RelativeLayout
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_card_hrv.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.view_meditation_brainwave.view.*

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
        mMainColor = typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportHeartRateVariabilityView_hrv_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportHeartRateVariabilityView_hrv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = ReportHeartRateView.INFO_URL
        }

        mIsShowAvg = typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowAvg, true)
        mIsShowMax = typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowMax, true)
        mIsShowMin = typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isShowMin, true)
        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportHeartRateVariabilityView_hrv_isAbsoluteTimeAxis, false)
        mLineColor = typeArray.getColor(R.styleable.ReportHeartRateVariabilityView_hrv_lineColor, mLineColor)
        initView()

    }

    fun initView() {
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
        hrv_chart.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        hrv_chart.setYAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        hrv_chart.setGridLineColor(getOpacityColor(mTextColor, 0.7f))
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
        hrv_chart.setLineColor(mLineColor)
    }

    fun setData(startTime: Long, data: List<Double>?, avg: Double?) {
        if (data == null){
            return
        }
        hrv_chart.setValues(data)
        tv_heart_avg.text = "${context.getString(R.string.avg)}${avg?.toInt()}"
        if (mIsAbsoluteTime) {
            hrv_chart.isAbsoluteTime(true, startTime)
        }
    }

    fun isDataNull(flag: Boolean) {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_no_data_cover).visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}