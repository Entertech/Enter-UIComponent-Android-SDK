package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_attention_card.view.*
import kotlinx.android.synthetic.main.layout_report_attention_card.view.iv_arrow
import kotlinx.android.synthetic.main.layout_report_attention_card.view.rl_bg
import kotlinx.android.synthetic.main.layout_report_hrv_card.view.*

class ReportRelaxationAndAttentionCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_attention_card, null)

    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mTextColor: Int = Color.parseColor("#FDF1EA")
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#333333")
    private var mAttentionColor: Int = Color.parseColor("#5B6DD9")
    private var mRelaxationColor: Int = Color.parseColor("#6DD1A1")
    private var mAttentionLevelBg: Int = Color.parseColor("#2B2E40")
    private var mRelaxationLevelBg: Int = Color.parseColor("#324039")
    private var mAttentionLevelTextColor: Int = Color.parseColor("#324039")
    private var mRelaxationLevelTextColor: Int = Color.parseColor("#324039")
    private var mLineColor: Int = Color.parseColor("#3A3A42")

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportRelaxationAndAttentionCard
        )
        mBg = typeArray.getDrawable(R.styleable.ReportRelaxationAndAttentionCard_rcraa_background)
        mTextColor = typeArray.getColor(
            R.styleable.ReportRelaxationAndAttentionCard_rcraa_textColor,
            mTextColor
        )
        mArrowColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_arrowColor,
                mArrowColor
            )
        mAttentionColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_attentionColor,
                mAttentionColor
            )
        mRelaxationColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_relaxationColor,
                mRelaxationColor
            )
        mAttentionLevelBg =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_attentionLevelBgColor,
                mAttentionLevelBg
            )
        mRelaxationLevelBg =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_relaxationLevelBgColor,
                mRelaxationLevelBg
            )
        mLineColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_lineColor,
                mLineColor
            )
        mAttentionLevelTextColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_attentionLevelTextColor,
                mAttentionLevelTextColor
            )
        mRelaxationLevelTextColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_relaxationLevelTextColor,
                mRelaxationLevelTextColor
            )

        initView()

    }

    fun initView() {
        initBg()
        initTitle()
        initValueView()
        initBarColor()
    }

    fun initBarColor(){
        bar_relaxation.setBarColor(mRelaxationColor)
        bar_relaxation.setBarBgColor(mLineColor)
        bar_relaxation.setBarTextColor(mTextColor)
        bar_attention.setBarColor(mAttentionColor)
        bar_attention.setBarBgColor(mLineColor)
        bar_attention.setBarTextColor(mTextColor)
    }

    fun initValueView() {
        tv_relaxation.setTextColor(mTextColor)
        tv_attention.setTextColor(mTextColor)
        tv_relaxation_level.setTextColor(mRelaxationLevelTextColor)
        tv_attention_level.setTextColor(mAttentionLevelTextColor)
        (tv_relaxation_level.background as GradientDrawable).setColor(mRelaxationLevelBg)
        (tv_attention_level.background as GradientDrawable).setColor(mAttentionLevelBg)
    }

    fun initTitle() {
        iv_arrow.setColorFilter(mArrowColor)
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_relaxtion)
        tv_title.text = context.getString(R.string.sdk_relaxation_and_attention)
        tv_title.setTextColor(mTextColor)
    }

    fun initBg() {
        if (mBg != null) {
            if (mBg is ColorDrawable) {
                rl_bg.setBackgroundColor((mBg as ColorDrawable).color)
            } else {
                rl_bg.background = mBg
            }
        }
    }

    fun setValue(relaxation: Int, attention: Int) {
        tv_relaxation.text = "$relaxation"
        bar_relaxation.setValue(relaxation)
        tv_attention.text = "$attention"
        bar_attention.setValue(attention)
        when (relaxation) {
            in 0..59 -> tv_relaxation_level.text = context.getString(R.string.sdk_report_low)
            in 60..79 -> tv_relaxation_level.text = context.getString(R.string.sdk_report_nor)
            else -> tv_relaxation_level.text = context.getString(R.string.sdk_report_high)
        }
        when (attention) {
            in 0..59 -> tv_attention_level.text = context.getString(R.string.sdk_report_low)
            in 60..79 -> tv_attention_level.text = context.getString(R.string.sdk_report_nor)
            else -> tv_attention_level.text = context.getString(R.string.sdk_report_high)
        }
    }
}