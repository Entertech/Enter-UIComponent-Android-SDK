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
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.dp
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_pressure_card.view.*
import kotlinx.android.synthetic.main.layout_report_pressure_card.view.iv_arrow
import kotlinx.android.synthetic.main.layout_report_pressure_card.view.rl_bg

class ReportPressureCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIsShowTitleIcon: Boolean = false
    private var mBarWidth: Float = 18f.dp()
    private var mBarScaleLength: Float = 24f.dp()
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_pressure_card, null)

    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mLevelBgColor: Int = Color.parseColor("#402D30")
    private var mLevelTextColor: Int = Color.parseColor("#FFE7E6")
    private var mTextColor: Int = Color.parseColor("#ffffff")
    private var mBarBgColor: Int = Color.parseColor("#3A3A42")
    private var mBg: Drawable? = null

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)


        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportPressureCard
        )
        mBg = typeArray.getDrawable(R.styleable.ReportPressureCard_rpc_background)
        mTextColor = typeArray.getColor(R.styleable.ReportPressureCard_rpc_textColor, mTextColor)
        mLevelBgColor =
            typeArray.getColor(R.styleable.ReportPressureCard_rpc_levelBgColor, mLevelBgColor)
        mLevelTextColor =
            typeArray.getColor(R.styleable.ReportPressureCard_rpc_levelTextColor, mLevelTextColor)
        mArrowColor =
            typeArray.getColor(R.styleable.ReportPressureCard_rpc_arrowColor, mArrowColor)
        mBarBgColor =
            typeArray.getColor(R.styleable.ReportPressureCard_rpc_barBgColor, mBarBgColor)
        mBarWidth = typeArray.getDimension(R.styleable.ReportPressureCard_rpc_barWidth, mBarWidth)
        mBarScaleLength = typeArray.getDimension(
            R.styleable.ReportPressureCard_rpc_barScaleLength,
            mBarScaleLength
        )
        mIsShowTitleIcon = typeArray.getBoolean(
            R.styleable.ReportPressureCard_rpc_isShowTitleIcon,
            mIsShowTitleIcon
        )
        initView()

    }

    fun initView() {
        initBg()
        initTitle()
        initBar()
    }

    fun initBar() {
        bar_pressure.setBarWidth(mBarWidth)
        bar_pressure.setScaleLineLength(mBarScaleLength)
        bar_pressure.setValueTextColor(mTextColor)
        bar_pressure.setBarBgColor(mBarBgColor)
        tv_pressure_level.setTextColor(mLevelTextColor)
        (tv_pressure_level.background as GradientDrawable).setColor(mLevelBgColor)
    }

    private fun initTitle() {
        iv_arrow.setColorFilter(mArrowColor)
        if (!mIsShowTitleIcon) {
            iv_icon.visibility = View.GONE
        } else {
            iv_icon.visibility = View.VISIBLE
        }
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_pressure)
        tv_title.text = context.getString(R.string.sdk_pressure)
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

    fun setValue(pressure: Int) {
        bar_pressure.setValue(pressure)
        when (pressure) {
            in 0..19 -> tv_pressure_level.text = context.getString(R.string.sdk_report_low)
            in 20..69 -> tv_pressure_level.text = context.getString(R.string.sdk_report_nor)
            else -> tv_pressure_level.text = context.getString(R.string.sdk_report_high)
        }
    }

    fun setShowTitleIcon(isShow: Boolean) {
        iv_icon.visibility = View.GONE
    }

    fun setBarWidth(width: Float) {
        bar_pressure.setBarWidth(width)
    }

    fun setScaleLineLength(width: Float) {
        bar_pressure.setScaleLineLength(width)
    }
}