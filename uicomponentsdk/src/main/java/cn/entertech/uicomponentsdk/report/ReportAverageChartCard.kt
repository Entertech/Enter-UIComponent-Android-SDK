package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_average_bar_card.view.*
import kotlinx.android.synthetic.main.layout_card_brain_spectrum_pie.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*

class ReportAverageChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mBarHighLightColor: Int = Color.parseColor("#FFE4BB")
    private var mTag: String? = ""
    private var mUnit: String? = ""
    private var mInfoUrl: String? = ""
    private var mBarValueBgColor: Int = Color.parseColor("#FFE4BB")
    private var mBarColor: Int = Color.parseColor("#EAECF1")
    private var mIsShowTitleIcon: Boolean = true
    private var mIsShowTitleMenuIcon: Boolean = true
    private var mTitle: String? = null
    private var mSpectrumColors: List<Int>? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_average_bar_card, null)

    private var mIsMenuIconInfo: Boolean = false
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mBg: Drawable? = null
    private var mTiltleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportAverageChartCard
        )
        mMainColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_mainColor,
            mMainColor
        )
        mTextColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_textColor,
            mTextColor
        )
        mBg = typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_background)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_titleMenuIcon)

        mIsShowTitleMenuIcon =
            typeArray.getBoolean(
                R.styleable.ReportAverageChartCard_racc_isTitleMenuIconShow,
                true
            )
        mIsShowTitleIcon = typeArray.getBoolean(
            R.styleable.ReportAverageChartCard_racc_isTitleIconShow,
            true
        )
        mTitle =
            typeArray.getString(R.styleable.ReportAverageChartCard_racc_title)
        mTag =
            typeArray.getString(R.styleable.ReportAverageChartCard_racc_tag)
        mUnit = typeArray.getString(R.styleable.ReportAverageChartCard_racc_unit)
        mBarColor = typeArray.getColor(R.styleable.ReportAverageChartCard_racc_barColor, mBarColor)
        mBarHighLightColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_barHighLightColor,
            mBarHighLightColor
        )
        mBarValueBgColor =
            typeArray.getColor(R.styleable.ReportAverageChartCard_racc_barValueBgColor, mBarColor)
        mInfoUrl = typeArray.getString(R.styleable.ReportAverageChartCard_racc_infoUrl)
        mIsMenuIconInfo =
            typeArray.getBoolean(R.styleable.ReportAverageChartCard_racc_isMenuIconInfo, false)
        initView()

    }

    fun initView() {
        tv_title.text = mTitle
        tv_title.setTextColor(mTextColor)
        if (mIsShowTitleIcon) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTiltleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsShowTitleMenuIcon) {
            iv_menu.visibility = View.VISIBLE
            iv_menu.setImageDrawable(mTitleMenuIcon)
        } else {
            iv_menu.visibility = View.GONE
        }
        if (mIsMenuIconInfo) {
            iv_menu.setOnClickListener {
                var uri = Uri.parse(mInfoUrl)
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
        initChart()
    }

    fun initChart() {
        average_bar_chart.setUnit(mUnit)
        average_bar_chart.setBarColor(mBarColor)
        average_bar_chart.setBarHighLightColor(mBarHighLightColor)
        average_bar_chart.setBarValueBgColor(mBarValueBgColor)
    }

    fun setValues(values: List<Int>) {
        if (values == null || values.isEmpty()) {
            return
        }
        average_bar_chart.setValues(values)
        var average = values.average()
        var lastValue = values[values.size - 1]
        if (lastValue > average) {
            tv_tip.text = "The $mTag is higher than the average of last 7 times. "
            iv_arrow.setImageResource(R.drawable.vector_drawable_report_average_arrow_up)
        } else if (lastValue < average) {
            tv_tip.text = "The $mTag is low than the average of last 7 times. "
            iv_arrow.setImageResource(R.drawable.vector_drawable_report_average_arrow_down)
        } else {
            tv_tip.text = "The $mTag is the same as the average of last 7 times. "
            iv_arrow.setImageResource(R.mipmap.ic_average_equal)
        }

    }

}