package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.realtime.EmotionIndicatorView
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_report_hr_card.view.*
import kotlinx.android.synthetic.main.layout_report_hr_card.view.iv_arrow
import kotlinx.android.synthetic.main.layout_report_hr_card.view.iv_corner_icon_bg
import kotlinx.android.synthetic.main.layout_report_hr_card.view.rl_bg
import kotlinx.android.synthetic.main.layout_report_hr_card.view.rl_corner_icon_bg
import kotlinx.android.synthetic.main.layout_report_hr_card.view.tv_unit
import kotlinx.android.synthetic.main.layout_report_hrv_card.view.*


class ReportHRCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mTopBg: Drawable?
    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mLevelBgColor: Int = Color.parseColor("#40392F")
    private var mIndicatorTriangleColor: Int = Color.parseColor("#FFE4BB")
    private var mIndicatorTextColor: Int = Color.parseColor("#FDF1EA")
    private var mUnitTextColor: Int = Color.parseColor("#99FFFFFF")
    private var mTextColor: Int = Color.parseColor("#FDF1EA")
    private var mIndicatorColor: Int = Color.parseColor("#FFCB7D")
    private var mBg: Drawable? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_hr_card, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, ScreenUtil.dip2px(context, 136f))
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)

        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportHRCard
        )

        mBg = typeArray.getDrawable(R.styleable.ReportHRCard_rchr_background)
        mTopBg = typeArray.getDrawable(R.styleable.ReportHRCard_rchr_topBackground)
        mIndicatorColor =
            typeArray.getColor(R.styleable.ReportHRCard_rchr_indicatorColor, mIndicatorColor)
        mTextColor = typeArray.getColor(R.styleable.ReportHRCard_rchr_textColor, mTextColor)
        mUnitTextColor =
            typeArray.getColor(R.styleable.ReportHRCard_rchr_unitTextColor, mUnitTextColor)
        mIndicatorTextColor = typeArray.getColor(
            R.styleable.ReportHRCard_rchr_indicatorTextColor,
            mIndicatorTextColor
        )
        mIndicatorTriangleColor = typeArray.getColor(
            R.styleable.ReportHRCard_rchr_indicatorTriangleColor,
            mIndicatorTriangleColor
        )
        mLevelBgColor =
            typeArray.getColor(R.styleable.ReportHRCard_rchr_levelBgColor, mLevelBgColor)
        mArrowColor =
            typeArray.getColor(R.styleable.ReportHRCard_rchr_arrowColor, mArrowColor)

        initView()
    }

    fun initView() {
        initBg()
        initTitle()
        initValueText()
        initIndicator()
    }

    private fun initIndicator() {
        var indicateItems = ArrayList<EmotionIndicatorView.IndicateItem>()
        var item1 = EmotionIndicatorView.IndicateItem(0.2f, getOpacityColor(mIndicatorColor, 0.2f))
        var item2 = EmotionIndicatorView.IndicateItem(0.4f, getOpacityColor(mIndicatorColor, 0.6f))
        var item3 = EmotionIndicatorView.IndicateItem(0.4f, mIndicatorColor)
        indicateItems.add(item1)
        indicateItems.add(item2)
        indicateItems.add(item3)
        eiv_hr.setIndicatorItems(indicateItems)
        eiv_hr.setScales(arrayOf(50, 60, 80, 100))
        eiv_hr.setIndicatorColor(mIndicatorTriangleColor)
        eiv_hr.setScaleTextColor(mIndicatorTextColor)
    }

    private fun initTitle() {
        iv_arrow.setColorFilter(mArrowColor)
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_hr)
        tv_title.text = context.getString(R.string.sdk_heart_rate)
        tv_title.setTextColor(mTextColor)
        if (mTopBg != null) {
            rl_corner_icon_bg.visibility = View.VISIBLE
            iv_corner_icon_bg.setImageDrawable(mTopBg)
        } else {
            rl_corner_icon_bg.visibility = View.GONE
        }
    }

    private fun initValueText() {
        tv_unit.setTextColor(mUnitTextColor)
        tv_hr.setTextColor(mTextColor)
        tv_hr_level.setTextColor(mTextColor)
        var bg = tv_hr_level.background as GradientDrawable
        bg.setColor(mLevelBgColor)
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
        tv_hr.text = "$value"
        eiv_hr.setValue(value.toFloat())
        when (value) {
            in 50..60 -> tv_hr_level.text = context.getString(R.string.sdk_report_low)
            in 80..100 -> tv_hr_level.text = context.getString(R.string.sdk_report_high)
            else -> tv_hr_level.text = context.getString(R.string.sdk_report_nor)
        }
    }
}