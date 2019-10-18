package cn.entertech.realtimedatasdk.realtime

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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.getOpacityColor
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_meditation_brainwave.view.*

class RealtimeBrainwaveView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mInfoIconRes: Int? = null
    private var mLeftBrainwaveColor: Int = Color.parseColor("#ff4852")
    private var mRightBrainwaveColor: Int = Color.parseColor("#0064ff")
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_brainwave, null)
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mTextFont: String? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/EEG-b3a44e9eb01549c29da1d8b2cc7bc08d"
    }

    private var mIsShowInfoIcon: Boolean = true

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.RealtimeBrainwaveView
        )
        mMainColor = typeArray.getColor(R.styleable.RealtimeBrainwaveView_rbv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.RealtimeBrainwaveView_rbv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeBrainwaveView_rbv_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.RealtimeBrainwaveView_rbv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeBrainwaveView_rbv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeBrainwaveView_rbv_textFont)
        mLeftBrainwaveColor =
            typeArray.getColor(R.styleable.RealtimeBrainwaveView_rbv_leftBrainwaveColor, mLeftBrainwaveColor)
        mRightBrainwaveColor =
            typeArray.getColor(R.styleable.RealtimeBrainwaveView_rbv_rightBrainwaveColor, mRightBrainwaveColor)
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
        tv_legend_right.setTextColor(getOpacityColor(mTextColor, 0.5f))
        tv_legend_left.setTextColor(getOpacityColor(mTextColor, 0.5f))
        var bgColor = Color.WHITE
        if (mBg != null) {
            rl_bg.background = mBg
        } else {
            mBg = rl_bg.background
        }
        if (mBg is ColorDrawable) {
            bgColor = (mBg as ColorDrawable).color
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bgColor = (mBg as GradientDrawable).color.defaultColor
            }
        }
        bsv_brainwave_left.setBackgroundColor(bgColor)
        bsv_brainwave_right.setBackgroundColor(bgColor)
        bsv_brainwave_left.setLineColor(mLeftBrainwaveColor)
        bsv_brainwave_right.setLineColor(mRightBrainwaveColor)
        var rightLegendIconBg = tv_right_legend_icon.background as GradientDrawable
        rightLegendIconBg.setColor(mRightBrainwaveColor)
        var leftLegendIconBg = tv_left_legend_icon.background as GradientDrawable
        leftLegendIconBg.setColor(mLeftBrainwaveColor)
        tv_right_legend_icon.background = rightLegendIconBg
        tv_left_legend_icon.background = leftLegendIconBg
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
        tv_legend_right.typeface = typeface
        tv_legend_left.typeface = typeface
    }

    fun setLeftBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_left).setData(data)
    }

    fun setRightBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_right).setData(data)
    }

    fun showLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
    }

    fun hindLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
    }

    fun showDisconnectTip() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        var sampleBrainData = ArrayList<Double>()
        for (i in 0..150) {
            sampleBrainData.add(java.util.Random().nextDouble() * 100.0 - 50)
        }
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_left).setSampleData(sampleBrainData)
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_right).setSampleData(sampleBrainData)
    }

    fun setLeftBrainwaveLineColor(color: Int) {
        this.mLeftBrainwaveColor = color
        bsv_brainwave_left.setLineColor(mLeftBrainwaveColor)
    }

    fun setRightBrainwaveLineColor(color: Int) {
        this.mRightBrainwaveColor = color
        bsv_brainwave_right.setLineColor(mRightBrainwaveColor)
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
}