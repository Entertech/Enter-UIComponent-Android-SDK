package cn.entertech.uicomponentsdk.realtime

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import kotlinx.android.synthetic.main.view_meditation_brainwave.view.tv_title
import kotlinx.android.synthetic.main.view_meditation_brainwave_spectrum.view.*
import kotlinx.android.synthetic.main.view_meditation_brainwave_spectrum.view.icon_loading

class RealtimeBrainwaveSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mTitleIcon: Drawable? = null
    private var mInfoIconRes: Int? = null
    private var mProcessBarColors: String? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.view_meditation_brainwave_spectrum, null)
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mTextFont: String? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL =
            "https://www.notion.so/Brainwave-Power-4cdadda14a69424790c2d7913ad775ff"
        const val PROCESSBAR_DEFAULT_COLORS = "#FFC200,#FF4852,#00D993,#0064FF,#0064FF"
    }

    private var mIsShowInfoIcon: Boolean = true

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.RealtimeBrainwaveSpectrumView
        )
        mMainColor =
            typeArray.getColor(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_mainColor, mMainColor)
        mTextColor =
            typeArray.getColor(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_background)
        mTitleIcon = typeArray.getDrawable(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_titleIcon)
        mIsShowInfoIcon = typeArray.getBoolean(
            R.styleable.RealtimeBrainwaveSpectrumView_rbsv_isShowInfoIcon,
            true
        )
        mInfoUrl = typeArray.getString(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_textFont)
        mProcessBarColors =
            typeArray.getString(R.styleable.RealtimeBrainwaveSpectrumView_rbsv_processBarColors)
        if (mProcessBarColors == null) {
            mProcessBarColors =
                PROCESSBAR_DEFAULT_COLORS
        }
        initView()
    }

    fun initView() {
//        icon_loading.loadGif("loading.gif")
        if (mInfoIconRes != null) {
            iv_spectrum_real_time_info.setImageResource(mInfoIconRes!!)
        }
        iv_spectrum_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        if (mBg != null) {
            ll_bg.background = mBg
        }
        if (mTitleIcon != null) {
            iv_title_icon.setImageDrawable(mTitleIcon)
        }
        tv_title.setTextColor(mMainColor)
        ppb_one.setLabelTextColor(mTextColor)
        ppb_two.setLabelTextColor(mTextColor)
        ppb_three.setLabelTextColor(mTextColor)
        ppb_four.setLabelTextColor(mTextColor)
        ppb_five.setLabelTextColor(mTextColor)

        var percentTextColor = getOpacityColor(mTextColor, 0.5f)
        ppb_one.setPercentTextColor(percentTextColor)
        ppb_two.setPercentTextColor(percentTextColor)
        ppb_three.setPercentTextColor(percentTextColor)
        ppb_four.setPercentTextColor(percentTextColor)
        ppb_five.setPercentTextColor(percentTextColor)

        var barColors = mProcessBarColors!!.split(",")
        ppb_one.setBarColor(Color.parseColor(barColors[0]))
        ppb_two.setBarColor(Color.parseColor(barColors[1]))
        ppb_three.setBarColor(Color.parseColor(barColors[2]))
        ppb_four.setBarColor(Color.parseColor(barColors[3]))
        ppb_five.setBarColor(Color.parseColor(barColors[4]))
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
    }


    fun setGammaWavePercent(percent: Float?) {
        ppb_one.setPercent(percent)
    }

    fun setBetaWavePercent(percent: Float?) {
        ppb_two.setPercent(percent)
    }

    fun setAlphaWavePercent(percent: Float?) {
        ppb_three.setPercent(percent)
    }

    fun setThetaWavePercent(percent: Float?) {
        ppb_four.setPercent(percent)
    }

    fun setDeltaWavePercent(percent: Float?) {
        ppb_five.setPercent(percent)
    }

    fun setGammaWaveDBValue(value: Float?) {
        ppb_one.setDBValue(value)
    }

    fun setBetaWaveDBValue(value: Float?) {
        ppb_two.setDBValue(value)
    }

    fun setAlphaWaveDBValue(value: Float?) {
        ppb_three.setDBValue(value)
    }

    fun setThetaWaveDBValue(value: Float?) {
        ppb_four.setDBValue(value)
    }

    fun setDeltaWaveDBValue(value: Float?) {
        ppb_five.setDBValue(value)
    }

    fun showErrorMessage(error: String) {
        rl_loading_cover_2.visibility = View.VISIBLE
        icon_loading.visibility = View.GONE
        tv_disconnect_text_2.visibility = View.VISIBLE
        tv_disconnect_text_2.text = error
    }

    fun showLoading() {
        rl_loading_cover_2.visibility = View.VISIBLE
        icon_loading.visibility = View.VISIBLE
        tv_disconnect_text_2.visibility = View.GONE
    }

    fun hideLoading() {
        rl_loading_cover_2.visibility = View.GONE
    }

    fun showDisconnectTip() {
        rl_loading_cover_2.visibility = View.VISIBLE
        icon_loading.visibility = View.GONE
        tv_disconnect_text_2.visibility = View.VISIBLE
        setAlphaWavePercent(0.2f)
        setBetaWavePercent(0.6f)
        setDeltaWavePercent(0.06f)
        setGammaWavePercent(0.04f)
        setThetaWavePercent(0.1f)
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

    fun setBarColors(colors: String) {
        this.mProcessBarColors = colors
        initView()
    }

    fun setPowerMode(mode:String){
        ppb_one.setPowerMode(mode)
        ppb_two.setPowerMode(mode)
        ppb_three.setPowerMode(mode)
        ppb_four.setPowerMode(mode)
        ppb_five.setPowerMode(mode)
        initView()
    }

}