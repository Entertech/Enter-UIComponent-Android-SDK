package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.realtime.EmotionIndicatorView
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_hrv_card.view.*

class ReportHRVCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_hrv_card, null)

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
        var item1 = EmotionIndicatorView.IndicateItem(0.43f, Color.parseColor("#FFE4BB"))
        var item2 = EmotionIndicatorView.IndicateItem(0.28f, Color.parseColor("#FFC56F"))
        var item3 = EmotionIndicatorView.IndicateItem(0.29f, Color.parseColor("#7F725E"))
        indicateItems.add(item1)
        indicateItems.add(item2)
        indicateItems.add(item3)
        eiv_hrv.setIndicatorItems(indicateItems)
        eiv_hrv.setScales(arrayOf(0, 30, 50, 70))
        eiv_hrv.setIndicatorColor(Color.parseColor("#FFC56F"))
    }

    fun initTitle() {
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_hrv)
        tv_title.text = "Heart Rate Variability"
        tv_title.setTextColor(mMainColor)
    }

    fun setValue(value: Int) {
        tv_hrv.text = "$value"
        eiv_hrv.setValue(value.toFloat())
        when (value) {
            in 0..29 -> tv_level.text = "low"
            in 50..70 -> tv_level.text = "high"
            else -> tv_level.text = "nor"
        }
    }
}