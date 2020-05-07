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
import cn.entertech.uicomponentsdk.widget.GifMovieView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.view_heart_rate.view.*
import java.io.IOException
import java.io.InputStream

class RealtimeHeartRateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mInfoIconRes: Int? = null
    private var mTextFont: String?
    private var mInfoUrl: String? = null

    companion object {
        const val INFO_URL = "https://www.notion.so/Heart-Rate-4d64215ac50f4520af7ff516c0f0e00b"
    }

    private var mIsShowInfoIcon: Boolean
    private var mHeartTextColor: Int = Color.parseColor("#171726")
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mIsShowExtremeValue: Boolean = true
    private var mBg: Drawable? = null
    var minHeart: Int = 0
    var maxHeart: Int = 0
    var isFirstLoad = true
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_heart_rate, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.RealtimeHeartRateView
        )
        mBg = typeArray.getDrawable(R.styleable.RealtimeHeartRateView_rhrv_background)
        mIsShowExtremeValue =
            typeArray.getBoolean(
                R.styleable.RealtimeHeartRateView_rhrv_isShowExtremeValue,
                mIsShowExtremeValue
            )
        mHeartTextColor =
            typeArray.getColor(R.styleable.RealtimeHeartRateView_rhrv_textColor, mHeartTextColor)
        mMainColor =
            typeArray.getColor(R.styleable.RealtimeHeartRateView_rhrv_mainColor, mMainColor)
        mIsShowInfoIcon =
            typeArray.getBoolean(R.styleable.RealtimeHeartRateView_rhrv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeHeartRateView_rhrv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeHeartRateView_rhrv_textFont)
        initView()
    }

    private fun initView() {
        if (mInfoIconRes != null) {
            iv_heart_real_time_info.setImageResource(mInfoIconRes!!)
        }
        if (mIsShowInfoIcon) {
            iv_heart_real_time_info.visibility = View.VISIBLE
        } else {
            iv_heart_real_time_info.visibility = View.GONE
        }
        iv_heart_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        if (mBg != null) {
            ll_bg.background = mBg
        }
        tv_heart_rate.setTextColor(mHeartTextColor)
        tv_title.setTextColor(mMainColor)
        var heartExtremeValueColor = getOpacityColor(mHeartTextColor, 0.7f)
        var heartExtremeValueUnitColor = getOpacityColor(mHeartTextColor, 0.5f)
        tv_max_heart.setTextColor(heartExtremeValueColor)
        tv_min_heart.setTextColor(heartExtremeValueColor)
        tv_min_heart_unit.setTextColor(heartExtremeValueUnitColor)
        tv_max_heart_unit.setTextColor(heartExtremeValueUnitColor)
        if (mIsShowExtremeValue) {
            ll_max_and_min_value.visibility = View.VISIBLE
        } else {
            ll_max_and_min_value.visibility = View.GONE
        }
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
        tv_heart_rate.typeface = typeface
        tv_max_heart.typeface = typeface
        tv_min_heart.typeface = typeface
        tv_unit.typeface = typeface
        tv_min_heart_unit.typeface = typeface
        tv_max_heart_unit.typeface = typeface
    }


    fun setHeartValue(heartRate: Int?) {
        if (heartRate == null) {
            return
        }
        if (isFirstLoad) {
            if (heartRate == 0) {
                return
            }
            minHeart = heartRate
            maxHeart = heartRate
            isFirstLoad = false
        } else {
            if (heartRate > maxHeart) {
                maxHeart = heartRate
            }
            if (heartRate != 0 && heartRate < minHeart) {
                minHeart = heartRate
            }
        }
        tv_heart_rate.text = "$heartRate"
        tv_max_heart.text = "${context.getString(R.string.sdk_max)}$maxHeart"
        tv_min_heart.text = "${context.getString(R.string.sdk_min)}$minHeart"
    }

    fun showLoading() {
        rl_loading_cover.visibility = View.VISIBLE
        icon_loading.visibility = View.VISIBLE
        tv_disconnect_text.visibility = View.GONE
    }

    fun hideLoading() {
        rl_loading_cover.visibility = View.GONE
    }

    fun showDisconnectTip() {
        icon_loading.visibility = View.GONE
        rl_loading_cover.visibility = View.VISIBLE
        tv_disconnect_text.visibility = View.VISIBLE
        setHeartValue(78)
    }

    fun hideDisconnectTip() {
        rl_loading_cover.visibility = View.GONE
    }

    override fun setBackgroundColor(color: Int) {
        ll_bg.setBackgroundColor(color)
    }

    override fun setBackground(background: Drawable?) {
        ll_bg.background = background
    }

    fun setIsShowExtremeValue(flag: Boolean) {
        this.mIsShowExtremeValue = flag
        initView()
    }

    fun setMainColor(color: Int) {
        this.mMainColor = color
        initView()
    }

    fun setTextColor(color: Int) {
        this.mHeartTextColor = color
        initView()
    }

    fun setTextFont(textFont: String) {
        this.mTextFont = textFont
        initView()
    }

    fun setIsShowInfoIcon(
        flag: Boolean,
        res: Int = R.drawable.vector_drawable_info_circle,
        url: String = INFO_URL
    ) {
        this.mIsShowInfoIcon = flag
        this.mInfoUrl = url
        this.mInfoIconRes = res
        initView()
    }
}