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

class ReportRelaxationAndAttentionCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mTitleMenuIcon: Drawable? = null
    private var mShowTitleMenuIcon: Boolean
    private var mTitleIcon: Drawable? = null
    private var mShowTitleIcon: Boolean = false
    private var mTitleText: String=""
    private var mTopBg: Drawable? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_attention_card, null)

    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mTextColor: Int = Color.parseColor("#FDF1EA")
    private var mBg: Drawable? = null
    private var mBarColor: Int = Color.parseColor("#5B6DD9")
    private var mLevelBg: Int = Color.parseColor("#2B2E40")
    private var mLevelTextColor: Int = Color.parseColor("#324039")
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

        mTopBg =
            typeArray.getDrawable(R.styleable.ReportRelaxationAndAttentionCard_rcraa_topBackground)

        mArrowColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_arrowColor,
                mArrowColor
            )
        mBarColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_barColor,
                mBarColor
            )
        mLevelBg =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_levelBgColor,
                mLevelBg
            )
        mLineColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_lineColor,
                mLineColor
            )
        mLevelTextColor =
            typeArray.getColor(
                R.styleable.ReportRelaxationAndAttentionCard_rcraa_levelTextColor,
                mLevelTextColor
            )
        mTitleText = typeArray.getString(R.styleable.ReportRelaxationAndAttentionCard_rcraa_titleText)?:""
        mShowTitleIcon = typeArray.getBoolean(R.styleable.ReportRelaxationAndAttentionCard_rcraa_showTitleIcon,false)
        mTitleIcon = typeArray.getDrawable(R.styleable.ReportRelaxationAndAttentionCard_rcraa_titleIcon)
        mShowTitleMenuIcon = typeArray.getBoolean(R.styleable.ReportRelaxationAndAttentionCard_rcraa_showTitleMenuIcon,false)
        mTitleMenuIcon = typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_titleMenuIcon)
        initView()

    }

    fun initView() {
        initBg()
        initTitle()
        initValueView()
        initBarColor()
    }

    fun initBarColor(){
        bar_attention.setBarText(mTitleText)
        bar_attention.setBarColor(mBarColor)
        bar_attention.setBarBgColor(mLineColor)
        bar_attention.setBarTextColor(mTextColor)
    }

    fun initValueView() {
        tv_attention.setTextColor(mTextColor)
        tv_attention_level.setTextColor(mLevelTextColor)
        (tv_attention_level.background as GradientDrawable).setColor(mLevelBg)
    }

    fun initTitle() {
        if (mShowTitleIcon && mTitleIcon != null){
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTitleIcon!!)
        }else{
            iv_icon.visibility = View.GONE
        }
        iv_menu.visibility = View.GONE
        if (mShowTitleMenuIcon ){
            iv_arrow.visibility = View.VISIBLE
            if (mTitleMenuIcon != null){
                iv_arrow.setImageDrawable(mTitleMenuIcon)
            }
            iv_arrow.setColorFilter(mArrowColor)
        }else{
            iv_arrow.visibility = View.GONE
        }
        tv_title.text = mTitleText
        tv_title.setTextColor(mTextColor)
        if (mTopBg != null) {
            rl_corner_icon_bg.visibility = View.VISIBLE
            iv_corner_icon_bg.setImageDrawable(mTopBg)
        } else {
            rl_corner_icon_bg.visibility = View.GONE
        }
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

    fun setValue(value: Int) {
        tv_attention.text = "$value"
        bar_attention.setValue(value)
        when (value) {
            in 0..29 -> tv_attention_level.text = context.getString(R.string.sdk_report_low)
            in 30..69 -> tv_attention_level.text = context.getString(R.string.sdk_report_nor)
            else -> tv_attention_level.text = context.getString(R.string.sdk_report_high)
        }
    }
}