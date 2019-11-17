package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_card_pressure.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*

class ReportPressureView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_pressure, null)

    private var mIsShowInfoIcon: Boolean = true
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mColor: Int = Color.parseColor("#ff6783")
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
            R.styleable.ReportPressureView
        )
        mMainColor = typeArray.getColor(R.styleable.ReportPressureView_rp_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.ReportPressureView_rp_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.ReportPressureView_rp_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.ReportPressureView_rp_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.ReportPressureView_rp_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = ReportHeartRateView.INFO_URL
        }

        mIsAbsoluteTime = typeArray.getBoolean(R.styleable.ReportPressureView_rp_isAbsoluteTimeAxis, false)
        mColor = typeArray.getColor(R.styleable.ReportPressureView_rp_color, mColor)
        initView()
    }

    fun initView() {
        tv_title.text = context.getString(R.string.pressure)
        tv_title.setTextColor(mMainColor)
        if (mBg != null) {
            ll_bg.background = mBg
        }
        if (mIsShowInfoIcon) {
            iv_info.visibility = View.VISIBLE
        } else {
            iv_info.visibility = View.GONE
        }
        iv_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        tv_time_unit.setTextColor(getOpacityColor(mTextColor, 0.7f))
        chart_pressure.setXAxisTextColor(getOpacityColor(mTextColor, 0.7f))
        chart_pressure.setGridLineColor(getOpacityColor(mTextColor, 0.3f))

        if (mIsAbsoluteTime) {
            tv_time_unit.visibility = View.GONE
        } else {
            tv_time_unit.visibility = View.VISIBLE
        }
        chart_pressure.setColor(mColor)
    }

    fun setData(startTime: Long, data: List<Double>?) {
        if (data == null) {
            return
        }
        chart_pressure.setValue(data)
        if (mIsAbsoluteTime) {
            chart_pressure.isAbsoluteTime(true, startTime)
        }
    }

    fun isDataNull(flag: Boolean) {
        rl_no_data_cover.visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}