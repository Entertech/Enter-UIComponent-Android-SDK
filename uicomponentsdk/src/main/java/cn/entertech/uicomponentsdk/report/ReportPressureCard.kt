package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.realtime.EmotionIndicatorView
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.tv_title
import kotlinx.android.synthetic.main.layout_report_attention_card.view.*
import kotlinx.android.synthetic.main.layout_report_hr_card.view.*
import kotlinx.android.synthetic.main.layout_report_hrv_card.view.*
import kotlinx.android.synthetic.main.layout_report_pressure_card.view.*
import kotlinx.android.synthetic.main.view_meditation_emotion.view.*

class ReportPressureCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_pressure_card, null)

    private var mMainColor: Int = Color.parseColor("#333333")

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, ScreenUtil.dip2px(context,218f))
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()

    }

    fun initView() {
        initTitle()
    }

    fun initTitle() {
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_pressure)
        tv_title.text = "Pressure"
        tv_title.setTextColor(mMainColor)
    }

    fun setValue(pressure: Int) {
        bar_pressure.setValue(pressure)
        when (pressure) {
            in 0..19 -> tv_pressure_level.text = "low"
            in 20..69 -> tv_pressure_level.text = "nor"
            else -> tv_pressure_level.text = "high"
        }
    }
}