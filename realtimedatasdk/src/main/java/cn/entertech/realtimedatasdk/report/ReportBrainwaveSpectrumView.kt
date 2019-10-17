package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.entertech.realtimedatasdk.R
import kotlinx.android.synthetic.main.layout_card_brain_spectrum.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*

class ReportBrainwaveSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_brain_spectrum, null)

    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mIsAbsoluteTime: Boolean = false
    private var mSample: Int = 3
    private var mBg: Drawable? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
    }

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportBrainwaveSpectrumView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportBrainwaveSpectrumView_rbs_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportBrainwaveSpectrumView_rbs_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportBrainwaveSpectrumView_rbs_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportBrainwaveSpectrumView_rbs_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mSample = typeArray.getInteger(R.styleable.ReportBrainwaveSpectrumView_rbs_sample, 3)
        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportBrainwaveSpectrumView_rbs_isAbsoluteTimeAxis, false)
        initView()

    }

    fun initView() {
        iv_info.setOnClickListener {
        }
    }

    fun setData(stackItems: ArrayList<StackedAreaChart.StackItem>) {
        sac_brain_chart.setStackItems(stackItems)
    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}