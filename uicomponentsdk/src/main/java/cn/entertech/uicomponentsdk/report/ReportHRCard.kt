package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.realtime.EmotionIndicatorView
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_hr_card.view.*

class ReportHRCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_hr_card, null)

    private var mMainColor: Int = Color.parseColor("#333333")

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, ScreenUtil.dip2px(context, 136f))
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()

    }

    fun initView() {
        initTitle()
        var indicateItems = ArrayList<EmotionIndicatorView.IndicateItem>()
        var item1 = EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#FFB2C0"))
        var item2 = EmotionIndicatorView.IndicateItem(0.6f, Color.parseColor("#FF6682"))
        var item3 = EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#7F5960"))
        indicateItems.add(item1)
        indicateItems.add(item2)
        indicateItems.add(item3)
        eiv_hr.setIndicatorItems(indicateItems)
        eiv_hr.setScales(arrayOf(0, 20,40,60,80,100))
        eiv_hr.setIndicatorColor(Color.parseColor("#FFC56F"))
    }

    fun initTitle() {
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_hr)
        tv_title.text = context.getString(R.string.sdk_heart_rate)
        tv_title.setTextColor(mMainColor)
    }

    fun setValue(value: Int) {
        tv_hr.text = "$value"
        eiv_hr.setValue(value.toFloat())
        when (value) {
            in 0..19 -> tv_hr_level.text = context.getString(R.string.sdk_low)
            in 80..100 -> tv_hr_level.text = context.getString(R.string.sdk_high)
            else -> tv_hr_level.text = context.getString(R.string.sdk_normal)
        }
    }
}