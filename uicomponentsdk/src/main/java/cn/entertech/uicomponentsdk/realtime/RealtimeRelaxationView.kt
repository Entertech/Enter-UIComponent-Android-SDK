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
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.view_meditation_emotion.view.*
import kotlinx.android.synthetic.main.view_meditation_emotion.view.icon_loading
import kotlinx.android.synthetic.main.view_meditation_emotion.view.ll_bg
import kotlinx.android.synthetic.main.view_meditation_emotion.view.tv_title


class RealtimeRelaxationView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mInfoIconRes: Int? = null
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_emotion, null)
    private var mTextFont: String? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Relaxation-c9e3b39634a14d2fa47eaed1d55d872b"
    }

    private var mIsShowInfoIcon: Boolean = true

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.RealtimeRelaxationView
        )
        mMainColor = typeArray.getColor(R.styleable.RealtimeRelaxationView_rrv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.RealtimeRelaxationView_rrv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeRelaxationView_rrv_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.RealtimeRelaxationView_rrv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeRelaxationView_rrv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeRelaxationView_rrv_textFont)
        initView()
    }

    fun initView() {
        icon_loading.loadGif("loading.gif")
        var relaxationScale = arrayOf(0, 60, 80, 100)
        var relaxationIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
        relaxationIndicatorItems.add(
            EmotionIndicatorView.IndicateItem(
                0.6f,
                getOpacityColor(mMainColor, 0.3f)
            )
        )
        relaxationIndicatorItems.add(
            EmotionIndicatorView.IndicateItem(
                0.2f,
                getOpacityColor(mMainColor, 0.5f)
            )
        )
        relaxationIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, mMainColor))
        eiv_emotion.setScales(relaxationScale)
        eiv_emotion.setIndicatorItems(relaxationIndicatorItems)
        eiv_emotion.setIndicatorColor(mMainColor)
        eiv_emotion
            .setScaleTextColor(getOpacityColor(mMainColor, 0.7f))
        iv_emotion_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        if (mInfoIconRes != null) {
            iv_emotion_real_time_info.setImageResource(mInfoIconRes!!)
        }
        if (mIsShowInfoIcon) {
            iv_emotion_real_time_info.visibility = View.VISIBLE
        } else {
            iv_emotion_real_time_info.visibility = View.GONE
        }
        iv_emotion_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        if (mBg != null) {
            ll_bg.background = mBg
        }
        tv_emotion_value.setTextColor(mTextColor)
        tv_title.setTextColor(mMainColor)
        tv_title.text = context.getString(R.string.sdk_relaxation)
        tv_emotion_level.setTextColor(mMainColor)
        tv_emotion_level.background = ContextCompat.getDrawable(context,R.drawable.shape_emotion_level_bg)
        val myGrad = tv_emotion_level.background as GradientDrawable
        myGrad.setColor(getOpacityColor(mMainColor, 0.2f))
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
        tv_emotion_value.typeface = typeface
        tv_emotion_level.typeface = typeface
        tv_disconnect_text_1.typeface = typeface
    }

    fun setRelaxation(value: Float?) {
        if (value == null) {
            return
        }
        var valueLevel = if (value >= 0 && value < 60) {
            context.getString(R.string.sdk_low)
        } else if (value >= 60 && value < 80) {
            context.getString(R.string.sdk_normal)
        } else {
            context.getString(R.string.sdk_high)
        }
        tv_emotion_level.text = valueLevel
        eiv_emotion.setValue(value)
        tv_emotion_value.text = "${value.toInt()}"
    }

    fun showDisconnectTip() {
        rl_loading_cover_1.visibility = View.VISIBLE
        icon_loading.visibility = View.GONE
        tv_disconnect_text_1.visibility = View.VISIBLE
        setRelaxation(39f)
    }


    fun showLoading() {
        rl_loading_cover_1.visibility = View.VISIBLE
        icon_loading.visibility = View.VISIBLE
        tv_disconnect_text_1.visibility = View.GONE
    }

    fun hideLoading() {
        rl_loading_cover_1.visibility = View.GONE
    }


    override fun setBackgroundColor(color: Int) {
        ll_bg.setBackgroundColor(color)
    }

    override fun setBackground(background: Drawable?) {
        ll_bg.background = background
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