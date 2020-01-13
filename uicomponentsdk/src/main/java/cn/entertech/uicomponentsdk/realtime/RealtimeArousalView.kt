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
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.view_meditation_emotion.view.*
import kotlinx.android.synthetic.main.view_meditation_emotion.view.icon_loading
import kotlinx.android.synthetic.main.view_meditation_emotion.view.ll_bg
import kotlinx.android.synthetic.main.view_meditation_emotion.view.tv_title


class RealtimeArousalView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mInfoIconRes: Int? = null
    private var mBg: Drawable? = null
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.view_meditation_emotion, null)
    private var mTextFont: String? = null
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Arousal-ee57f4590373442b9107b7ce665e1253"
    }

    private var mIsShowInfoIcon: Boolean = true

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.RealtimeArousalView
        )
        mMainColor = typeArray.getColor(R.styleable.RealtimeArousalView_rarv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.RealtimeArousalView_rarv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeArousalView_rarv_background)
        mIsShowInfoIcon =
            typeArray.getBoolean(R.styleable.RealtimeArousalView_rarv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeArousalView_rarv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeArousalView_rarv_textFont)
        initView()
    }

    fun initView() {
        icon_loading.loadGif("loading.gif")
        var stressScale = arrayOf(-2, -1, 0, 1, 2)
        var stressIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
        stressIndicatorItems.add(
            EmotionIndicatorView.IndicateItem(
                0.5f,
                getOpacityColor(mMainColor, 0.3f)
            )
        )
        stressIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.5f, mMainColor))
        eiv_emotion.setScales(stressScale)
        eiv_emotion.setIndicatorItems(stressIndicatorItems)
        eiv_emotion.setIndicatorColor(mMainColor)
        eiv_emotion.setScaleTextColor(getOpacityColor(mMainColor, 0.7f))
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
        tv_title.text = context.getString(R.string.sdk_arousal)
        tv_emotion_level.setTextColor(mMainColor)
        tv_emotion_level.background =
            context.getDrawable(R.drawable.shape_emotion_level_bg)
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


    fun setArousal(value: Float?) {
        if (value == null) {
            return
        }
        var arousalValue = String.format("%.1f", value / 25f - 2f).toFloat()
        var valueLevel = if (value >= 0 && value < 2) {
            context.getString(R.string.sdk_high)
        } else {
            context.getString(R.string.sdk_low)
        }
        tv_emotion_level.text = valueLevel
        eiv_emotion.setValue(arousalValue)
        tv_emotion_value.text = "$arousalValue"
    }

    fun showDisconnectTip() {
        rl_loading_cover_1.visibility = View.VISIBLE
        icon_loading.visibility = View.GONE
        tv_disconnect_text_1.visibility = View.VISIBLE
        setArousal(1.2f)
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