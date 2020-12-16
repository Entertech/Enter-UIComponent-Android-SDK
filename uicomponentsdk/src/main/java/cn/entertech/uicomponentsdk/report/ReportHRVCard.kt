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
import kotlinx.android.synthetic.main.layout_report_hrv_card.view.*

class ReportHRVCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIsShortCard: Boolean = false
    private var mArrowColor: Int = Color.parseColor("#ffffff")
    private var mLevelBgColor: Int = Color.parseColor("#40392F")
    private var mIndicatorTriangleColor: Int = Color.parseColor("#FFE4BB")
    private var mIndicatorTextColor: Int = Color.parseColor("#FDF1EA")
    private var mUnitTextColor: Int = Color.parseColor("#99FFFFFF")
    private var mTextColor: Int = Color.parseColor("#FDF1EA")
    private var mIndicatorColor: Int = Color.parseColor("#FFCB7D")
    private var mBg: Drawable? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_hrv_card, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT,MATCH_PARENT)
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
        if (mIsShortCard){
            eiv_hrv.visibility = View.GONE
            return
        }
        var indicateItems = ArrayList<EmotionIndicatorView.IndicateItem>()
        var item1 = EmotionIndicatorView.IndicateItem(0.14f, getOpacityColor(mIndicatorColor, 0.2f))
        var item2 = EmotionIndicatorView.IndicateItem(0.26f, getOpacityColor(mIndicatorColor, 0.6f))
        var item3 = EmotionIndicatorView.IndicateItem(0.6f, mIndicatorColor)
        indicateItems.add(item1)
        indicateItems.add(item2)
        indicateItems.add(item3)
        eiv_hrv.setIndicatorItems(indicateItems)
        eiv_hrv.setScales(arrayOf(0, 7, 20, 50))
        eiv_hrv.setIndicatorColor(mIndicatorTriangleColor)
        eiv_hrv.setScaleTextColor(mIndicatorTextColor)
    }

    fun initTitle() {
        iv_arrow.setColorFilter(mArrowColor)
        if (!mIsShortCard) {
            iv_icon.visibility = View.VISIBLE
        } else {
            iv_icon.visibility = View.GONE
        }
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_hrv)
        tv_title.text = context.getString(R.string.sdk_hrv)
        tv_title.setTextColor(mTextColor)
    }

    private fun initValueText() {
        tv_unit.setTextColor(mUnitTextColor)
        tv_hrv.setTextColor(mTextColor)
        tv_level.setTextColor(mTextColor)
        tv_level_2.setTextColor(mTextColor)
        if (mLevelBgColor != null) {
            var bg = tv_level.background as GradientDrawable
            bg.setColor(mLevelBgColor)
        }
        if (!mIsShortCard) {
            ll_value_tip_1.visibility = View.VISIBLE
            ll_value_tip_2.visibility = View.GONE
        } else {
            ll_value_tip_1.visibility = View.GONE
            ll_value_tip_2.visibility = View.VISIBLE
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

    fun setValue(value: Float) {
        tv_hrv.text = "$value"
        eiv_hrv.setValue(value)
        when (value) {
            in 0..7 -> tv_level.text = context.getString(R.string.sdk_report_low)
            in 20..50 -> tv_level.text = context.getString(R.string.sdk_report_high)
            else -> tv_level.text = context.getString(R.string.sdk_report_nor)
        }
    }

    fun setIsShortCard(isShow: Boolean) {
        mIsShortCard = isShow
        initView()
    }


}