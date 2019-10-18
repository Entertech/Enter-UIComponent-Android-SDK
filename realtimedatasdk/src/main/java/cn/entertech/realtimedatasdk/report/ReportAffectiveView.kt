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
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.getOpacityColor
import cn.entertech.realtimedatasdk.utils.removeZeroData
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_card_attention.view.*

class ReportAffectiveView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var mIsAbsoluteTime: Boolean = false
    private var mEmotionType: Int = 0
    private var mFillColor: Int = Color.parseColor("#0000ff")
    private var mIsShowMin: Boolean
    private var mIsShowMax: Boolean
    private var mIsShowAvg: Boolean = true
    private var mSample: Int = 3
    private var mBg: Drawable? = null

    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
    }

    private var mInfoIconRes: Int? = null
    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_attention, null)

    init {
        var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportAffectiveView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportAffectiveView_ra_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportAffectiveView_ra_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mSample = typeArray.getInteger(R.styleable.ReportAffectiveView_ra_sample, 3)
        mIsShowAvg = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowAvg, true)
        mIsShowMax = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowMax, true)
        mIsShowMin = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isShowMin, true)
        mFillColor = typeArray.getColor(R.styleable.ReportAffectiveView_ra_fillColor, mFillColor)
        mEmotionType = typeArray.getInteger(R.styleable.ReportAffectiveView_ra_emotionType, 0)
        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportAffectiveView_ra_isAbsoluteTimeAxis, false)
        initView()
    }

    fun initView() {
        tv_title.setTextColor(mMainColor)
        when (mEmotionType) {
            0 -> {
                tv_title.text = "专注度"
                tv_vertical.setText("专注度")
            }
            1 -> {
                tv_title.text = "放松度"
                tv_vertical.setText("放松度")
            }
        }
        tv_time_unit_des.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_avg.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_max.setTextColor(getOpacityColor(mTextColor, 0.7f))
        tv_min.setTextColor(getOpacityColor(mTextColor, 0.7f))

        tv_vertical.setTextColor(getOpacityColor(mTextColor, 0.7f))
        chart_attention.setYAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        chart_attention.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        chart_attention.setGridLineColor(getOpacityColor(mTextColor, 0.1f))

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
        chart_attention.setBackgroundColor(bgColor)
        if (mIsShowInfoIcon) {
            iv_info.visibility = View.VISIBLE
        } else {
            iv_info.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        chart_attention.setSample(mSample)
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
        var fillColors = intArrayOf(mFillColor, getOpacityColor(mFillColor, 0.7f))
        chart_attention.setColors(fillColors)
    }

    fun setData(startTime: Long, data: List<Double>?) {
        if (startTime == null || data == null){
            return
        }
        chart_attention.setValues(data)
        var removeZeroAttentionRec = removeZeroData(data)
        var attentionMax =
            java.util.Collections.max(removeZeroAttentionRec).toFloat()
        var attentionMin =
            java.util.Collections.min(removeZeroAttentionRec).toFloat()
        var sum = 0.0
        for (value in removeZeroAttentionRec) {
            sum += value
        }
        var avg = sum / removeZeroAttentionRec.size
        tv_avg.text = "${context.getString(R.string.avg)}${avg.toInt()}"
        tv_max.text = "${context.getString(R.string.max)}${attentionMax.toInt()}"
        tv_min.text = "${context.getString(R.string.min)}${attentionMin.toInt()}"
        if (mIsAbsoluteTime){
            chart_attention.isAbsoluteTime(true,startTime)
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