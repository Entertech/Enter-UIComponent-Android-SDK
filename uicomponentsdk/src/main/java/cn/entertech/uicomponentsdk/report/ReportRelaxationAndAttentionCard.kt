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
import kotlinx.android.synthetic.main.view_meditation_emotion.view.*

class ReportRelaxationAndAttentionCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_report_attention_card, null)

    private var mMainColor: Int = Color.parseColor("#333333")

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()

    }

    fun initView() {
        initTitle()
    }

    fun initTitle() {
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_title_icon_relaxtion)
        tv_title.text = context.getString(R.string.sdk_relaxation_and_attention)
        tv_title.setTextColor(mMainColor)
    }

    fun setValue(relaxation: Int, attention: Int) {
        tv_relaxation.text = "$relaxation"
        bar_relaxation.setValue(relaxation)
        tv_attention.text = "$attention"
        bar_attention.setValue(attention)
        when (relaxation) {
            in 0..59 -> tv_relaxation_level.text = context.getString(R.string.sdk_low)
            in 60..79 -> tv_relaxation_level.text = context.getString(R.string.sdk_normal)
            else -> tv_relaxation_level.text = context.getString(R.string.sdk_high)
        }
        when (attention) {
            in 0..59 -> tv_attention_level.text = context.getString(R.string.sdk_low)
            in 60..79 -> tv_attention_level.text = context.getString(R.string.sdk_normal)
            else -> tv_attention_level.text = context.getString(R.string.sdk_high)
        }
    }
}