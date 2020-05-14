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
import cn.entertech.uicomponentsdk.realtime.EmotionIndicatorView
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_corherence_card.view.*

class ReportCoherenceCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mLevelBgColor: Int = Color.parseColor("#40392F")
    private var mIndicatorTriangleColor: Int = Color.parseColor("#FFE4BB")
    private var mIndicatorTextColor: Int = Color.parseColor("#FDF1EA")
    private var mUnitTextColor: Int = Color.parseColor("#99FFFFFF")
    private var mTextColor: Int = Color.parseColor("#FDF1EA")
    private var mIndicatorColor: Int = Color.parseColor("#FFCB7D")
    private var mBg: Drawable? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_corherence_card, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, ScreenUtil.dip2px(context, 136f))
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)

        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportHRVCard
        )
        mBg = typeArray.getDrawable(R.styleable.ReportHRVCard_rchrv_background)
        mIndicatorColor =
            typeArray.getColor(R.styleable.ReportHRVCard_rchrv_indicatorColor, mIndicatorColor)
        mTextColor = typeArray.getColor(R.styleable.ReportHRVCard_rchrv_textColor, mTextColor)
        mUnitTextColor =
            typeArray.getColor(R.styleable.ReportHRVCard_rchrv_unitTextColor, mUnitTextColor)
        mIndicatorTextColor = typeArray.getColor(
            R.styleable.ReportHRVCard_rchrv_indicatorTextColor,
            mIndicatorTextColor
        )
        mIndicatorTriangleColor = typeArray.getColor(
            R.styleable.ReportHRVCard_rchrv_indicatorTriangleColor,
            mIndicatorTriangleColor
        )
        mLevelBgColor =
            typeArray.getColor(R.styleable.ReportHRVCard_rchrv_levelBgColor, mLevelBgColor)
        mArrowColor =
            typeArray.getColor(R.styleable.ReportHRVCard_rchrv_arrowColor, mArrowColor)

        initView()
    }

    fun initView() {
        initBg()
        initTitle()
        initValueText()
        initIndicator()
    }
    fun initIndicator() {
        var indicateItems = ArrayList<EmotionIndicatorView.IndicateItem>()
        var item1 = EmotionIndicatorView.IndicateItem(0.2f, getOpacityColor(mIndicatorColor,0.2f))
        var item2 = EmotionIndicatorView.IndicateItem(0.4f, getOpacityColor(mIndicatorColor,0.6f))
        var item3 = EmotionIndicatorView.IndicateItem(0.4f, mIndicatorColor)
        indicateItems.add(item1)
        indicateItems.add(item2)
        indicateItems.add(item3)
        eiv_hrv.setIndicatorItems(indicateItems)
        eiv_hrv.setScales(arrayOf(0, 20, 60, 100))
        eiv_hrv.setIndicatorColor(mIndicatorTriangleColor)
        eiv_hrv.setScaleTextColor(mIndicatorTextColor)
    }
    fun initTitle() {
        iv_arrow.setColorFilter(mArrowColor)
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_hrv)
        tv_title.text = context.getString(R.string.sdk_coherence)
        tv_title.setTextColor(mTextColor)
    }

    private fun initValueText() {
        tv_unit.setTextColor(mUnitTextColor)
        tv_hrv.setTextColor(mTextColor)
        tv_level.setTextColor(mTextColor)
        if (mLevelBgColor != null) {
            var bg = tv_level.background as GradientDrawable
            bg.setColor(mLevelBgColor)
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
        tv_hrv.text = "$value"
        eiv_hrv.setValue(value.toFloat())
        when (value) {
            in 0..19 -> tv_level.text = context.getString(R.string.sdk_report_low)
            in 60..100 -> tv_level.text = context.getString(R.string.sdk_report_high)
            else -> tv_level.text = context.getString(R.string.sdk_report_nor)
        }
    }
}