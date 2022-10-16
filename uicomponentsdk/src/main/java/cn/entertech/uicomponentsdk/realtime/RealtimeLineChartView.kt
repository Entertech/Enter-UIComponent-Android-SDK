package cn.entertech.uicomponentsdk.realtime

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.realtime.RealtimeAnimLineChartView.OnDrawLastValueListener
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.dp
import cn.entertech.uicomponentsdk.utils.toWebView
import cn.entertech.uicomponentsdk.widget.RealtimeChartLegendView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_realtime_line_chart.view.*
import java.util.*
import kotlin.collections.ArrayList

class RealtimeLineChartView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mOnInfoClickListener: (() -> Unit)? = null
    private var mIsForPad: Boolean = false
    private var mVerticalPadding: Int = 1
    private var mOnDrawLastValueListener: OnDrawLastValueListener? = null
    private var mWebTitle: String? = ""
    private var mTextRectBgColor: Int = Color.WHITE
    private var mPointBgColor: Int = Color.parseColor("#11152E")
    private var mIsDrawValueText: Boolean = false
    private var mLineLegendText: String? = ""
    private var mScreenPointCount: Int = 100
    private var mIsShowXAxis: Boolean = false
    private var mMaxValue: Int = 50
    private var mRefreshTime: Int = 200
    private var mBuffer: Int = 2
    private var mInfoIconRes: Int? = null
    private var mLineColor: String? = "#ff0000"
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.view_realtime_line_chart, null)
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mAxisColor: Int = Color.parseColor("#9AA1A9")
    private var mGridLineColor: Int = Color.parseColor("#9AA1A9")
    private var mTextFont: String? = null
    private var mInfoUrl: String? = null
    private var mTitleText: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/EEG-b3a44e9eb01549c29da1d8b2cc7bc08d"
    }

    private var mIsShowInfoIcon: Boolean = true

    private var mLineWidth = ScreenUtil.dip2px(context, 1.5f).toFloat()

    init {
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.RealtimeLineChartView
        )
        mMainColor =
            typeArray.getColor(R.styleable.RealtimeLineChartView_rlcv_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.RealtimeLineChartView_rlcv_textColor, mTextColor)
        mAxisColor =
            typeArray.getColor(R.styleable.RealtimeLineChartView_rlcv_axisColor, mAxisColor)
        mGridLineColor =
            typeArray.getColor(R.styleable.RealtimeLineChartView_rlcv_gridLineColor, mGridLineColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeLineChartView_rlcv_background)
        mLineWidth =
            typeArray.getDimension(R.styleable.RealtimeLineChartView_rlcv_lineWidth, mLineWidth)
        mIsShowInfoIcon =
            typeArray.getBoolean(R.styleable.RealtimeLineChartView_rlcv_isShowInfoIcon, true)
        mTitleText = typeArray.getString(R.styleable.RealtimeLineChartView_rlcv_titleText)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeLineChartView_rlcv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeLineChartView_rlcv_textFont)
        mBuffer = typeArray.getInteger(R.styleable.RealtimeLineChartView_rlcv_buffer, mBuffer)
        mMaxValue = typeArray.getInteger(R.styleable.RealtimeLineChartView_rlcv_maxValue, mMaxValue)
        mRefreshTime =
            typeArray.getInteger(R.styleable.RealtimeLineChartView_rlcv_refreshTime, mRefreshTime)
        mLineColor =
            typeArray.getString(R.styleable.RealtimeLineChartView_rlcv_lineColor)
        mPointBgColor =
            typeArray.getColor(R.styleable.RealtimeLineChartView_rlcv_pointBgColor, mPointBgColor)
        mTextRectBgColor =
            typeArray.getColor(
                R.styleable.RealtimeLineChartView_rlcv_textRectBgColor,
                mTextRectBgColor
            )
        mLineLegendText =
            typeArray.getString(R.styleable.RealtimeLineChartView_rlcv_lineLegendText)
        mScreenPointCount = typeArray.getInteger(
            R.styleable.RealtimeLineChartView_rlcv_screenPointCount,
            mScreenPointCount
        )
        mIsDrawValueText =
            typeArray.getBoolean(R.styleable.RealtimeLineChartView_rlcv_isDrawValueText, false)
        mIsForPad = typeArray.getBoolean(R.styleable.RealtimeLineChartView_rlcv_isForPad, true)

        mSelfView = if (mIsForPad) {
            LayoutInflater.from(context).inflate(R.layout.view_realtime_line_chart_pad, null)
        } else {
            LayoutInflater.from(context).inflate(R.layout.view_realtime_line_chart, null)
        }
        var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()
    }

    fun initView() {
        if (!mIsForPad) {
            initLegendView()
        }
        if (mInfoIconRes != null) {
            iv_brain_real_time_info.setImageResource(mInfoIconRes!!)
        }
        if (mIsShowInfoIcon) {
            iv_brain_real_time_info.visibility = View.VISIBLE
        } else {
            iv_brain_real_time_info.visibility = View.GONE
        }

        iv_brain_real_time_info.setOnClickListener {
            mOnInfoClickListener?.invoke()
        }

        tv_title.setTextColor(mMainColor)
        tv_title.text = mTitleText
        var bgColor = Color.WHITE
        if (mBg != null) {
            ll_bg.background = mBg
        } else {
            mBg = ll_bg.background
        }
        if (mBg is ColorDrawable) {
            bgColor = (mBg as ColorDrawable).color
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bgColor = (mBg as GradientDrawable).color!!.defaultColor
            }
        }
        realtime_chart.setMaxValue(mMaxValue)
        realtime_chart.setRefreshTime(mRefreshTime)
        realtime_chart.isDrawXAxis(mIsShowXAxis)
        realtime_chart.setBuffer(mBuffer)
        realtime_chart.setBackgroundColor(bgColor)
        realtime_chart.setLineColor(mLineColor)
        realtime_chart.setLineWidth(mLineWidth)
        realtime_chart.setGridLineColor(mGridLineColor)
        realtime_chart.setBgPointColor(mPointBgColor)
        realtime_chart.setTextRectBgColor(mTextRectBgColor)
        realtime_chart.setAxisColor(mAxisColor)
        realtime_chart.setVerticalPadding(mVerticalPadding)
        realtime_chart.setOnDrawLastValueListener(mOnDrawLastValueListener)
        realtime_chart.setScreenPointCount(mScreenPointCount)
        realtime_chart.isDrawValueText = mIsDrawValueText
        realtime_chart.init()
        setTextFont()
    }

    var lineShowIndexs = ArrayList<Int>()
    fun initLegendView() {
        var colors = mLineColor?.split(",")
        if (colors == null || colors.size <= 1) {
            ll_legend_parent.visibility = View.GONE
        } else {
            ll_legend_parent.visibility = View.VISIBLE
            var legendTexts = mLineLegendText?.split(",")
            if (legendTexts != null && legendTexts.size == colors.size) {
                var layoutParams: LayoutParams?
                if (legendTexts.size >= 3) {
                    layoutParams = LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    )
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams.rightMargin = 4f.dp().toInt()
                    layoutParams.leftMargin = 4f.dp().toInt()
                } else {
                    layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams.rightMargin = 8f.dp().toInt()
                }
                ll_legend_parent.removeAllViews()
                for (i in colors.indices) {
                    var realtimeChartLegendView = RealtimeChartLegendView(context)
                    realtimeChartLegendView.layoutParams = layoutParams
                    realtimeChartLegendView.setLegendIconColor(Color.parseColor(colors[i]))
                    realtimeChartLegendView.setText(legendTexts[i])
                    realtimeChartLegendView.setCheck(true)
                    realtimeChartLegendView.addOnCheckListener {
                        lineShowIndexs.clear()
                        for (j in 0 until ll_legend_parent.childCount) {
                            var view = ll_legend_parent.getChildAt(j) as RealtimeChartLegendView
                            if (view.mIsChecked) {
                                lineShowIndexs.add(j)
                            }
                        }
                        realtime_chart.setLineShowIndexs(lineShowIndexs)
                    }
                    ll_legend_parent.addView(realtimeChartLegendView)
                }
            }
        }
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
    }

    fun appendData(index: Int, data: List<Double>?) {
        if (data == null) {
            return
        }
        mSelfView.findViewById<RealtimeAnimLineChartView>(R.id.realtime_chart).setData(index, data)
    }

    fun appendData(index: Int, data: Double?) {
        if (data == null) {
            return
        }
        mSelfView.findViewById<RealtimeAnimLineChartView>(R.id.realtime_chart).setData(index, data)
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.GONE
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.GONE
    }

    fun showSampleData(sampleData: List<List<Int>>) {
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.VISIBLE
        mSelfView.findViewById<RealtimeAnimLineChartView>(R.id.realtime_chart)
            .showSampleData(sampleData)
    }

    fun showErrorMessage(error: String) {
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).text = error
//        appendHrv(listOf(0.0))
    }

    fun hideSampleData() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.GONE
        realtime_chart.hideSampleData()
    }

    fun setLineColor(color: String?) {
        this.mLineColor = color
        realtime_chart.setLineColor(mLineColor)
    }

    fun setMainColor(color: Int) {
        this.mMainColor = color
        initView()
    }

    fun setTextColor(color: Int) {
        this.mTextColor = color
        initView()
    }

    fun setTextFont(textFont: String) {
        this.mTextFont = textFont
        initView()
    }


    fun setIsShowInfoIcon(
        flag: Boolean,
        res: Int = R.drawable.vector_drawable_info_circle,
        url: String = RealtimeHeartRateView.INFO_URL,
        webTitle: String = ""
    ) {
        this.mIsShowInfoIcon = flag
        this.mInfoUrl = url
        this.mInfoIconRes = res
        this.mWebTitle = webTitle
        initView()
    }

    fun setIsShowXAxis(flag: Boolean) {
        this.mIsShowXAxis = flag
        initView()
    }

    fun setOnDrawLastValueListener(lastValueListener: OnDrawLastValueListener) {
        this.mOnDrawLastValueListener = lastValueListener
        initView()
    }

    fun setVerticalPadding(padding: Int) {
        this.mVerticalPadding = padding
        initView()
    }

    fun setOnInfoClickListener(listener:()->Unit){
        this.mOnInfoClickListener = listener
    }
}