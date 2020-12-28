package cn.entertech.uicomponentsdk.realtime

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_meditation_hrv.view.*

class RealtimeBreathCoherenceView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIsShowXAxis: Boolean = false
    private var mMaxValue: Int = 50
    private var mRefreshTime: Int = 200
    private var mBuffer: Int = 2
    private var mInfoIconRes: Int? = null
    private var mLineColor: Int = Color.parseColor("#ff4852")
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_hrv, null)
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

    private var mLineWidth = ScreenUtil.dip2px(context,1.5f).toFloat()
    init {
        var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.RealtimeBreathCoherenceView
        )
        mMainColor = typeArray.getColor(R.styleable.RealtimeBreathCoherenceView_rhrvv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.RealtimeBreathCoherenceView_rhrvv_textColor, mTextColor)
        mAxisColor = typeArray.getColor(R.styleable.RealtimeBreathCoherenceView_rhrvv_axisColor, mAxisColor)
        mGridLineColor = typeArray.getColor(R.styleable.RealtimeBreathCoherenceView_rhrvv_gridLineColor, mGridLineColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeBreathCoherenceView_rhrvv_background)
        mLineWidth = typeArray.getDimension(R.styleable.RealtimeBreathCoherenceView_rhrvv_lineWidth,mLineWidth)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.RealtimeBreathCoherenceView_rhrvv_isShowInfoIcon, true)
        mTitleText = typeArray.getString(R.styleable.RealtimeBreathCoherenceView_rhrvv_titleText)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeBreathCoherenceView_rhrvv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeBreathCoherenceView_rhrvv_textFont)
        mBuffer = typeArray.getInteger(R.styleable.RealtimeBreathCoherenceView_rhrvv_buffer,mBuffer)
        mMaxValue = typeArray.getInteger(R.styleable.RealtimeBreathCoherenceView_rhrvv_maxValue,mMaxValue)
        mRefreshTime = typeArray.getInteger(R.styleable.RealtimeBreathCoherenceView_rhrvv_refreshTime,mRefreshTime)
        mLineColor =
            typeArray.getColor(R.styleable.RealtimeBreathCoherenceView_rhrvv_lineColor, mLineColor)
        initView()
    }

    fun initView() {
        if (mInfoIconRes != null) {
            iv_brain_real_time_info.setImageResource(mInfoIconRes!!)
        }
        iv_brain_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
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
                bgColor = (mBg as GradientDrawable).color.defaultColor
            }
        }
        sf_hrv.setMaxValue(mMaxValue)
        sf_hrv.setRefreshTime(mRefreshTime)
        sf_hrv.isDrawXAxis(mIsShowXAxis)
        sf_hrv.setBuffer(mBuffer)
        sf_hrv.setBackgroundColor(bgColor)
        sf_hrv.setLineColor(mLineColor)
        sf_hrv.setLineWidth(mLineWidth)
        sf_hrv.setGridLineColor(mGridLineColor)
        sf_hrv.setAxisColor(mAxisColor)
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
    }

    fun appendHrv(data: List<Double>?) {
        if (data == null){
            return
        }
        mSelfView.findViewById<BreathCoherenceSurfaceView>(R.id.sf_hrv).setData(data)
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.GONE
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.GONE
    }

    fun showSampleData() {
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.VISIBLE
        var sampleBrainData = ArrayList<Double>()
        for (i in 0..35) {
            sampleBrainData.add(java.util.Random().nextDouble() * 10+60)
        }
        mSelfView.findViewById<BreathCoherenceSurfaceView>(R.id.sf_hrv).setSampleData(sampleBrainData)
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
        sf_hrv.hideSampleData()
    }

    fun setLineColor(color: Int) {
        this.mLineColor = color
        sf_hrv.setLineColor(mLineColor)
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
        url: String = RealtimeHeartRateView.INFO_URL
    ) {
        this.mIsShowInfoIcon = flag
        this.mInfoUrl = url
        this.mInfoIconRes = res
        initView()
    }

    fun setIsShowXAxis(flag: Boolean){
        this.mIsShowXAxis = flag
        initView()
    }
}