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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.widget.GifMovieView
import kotlinx.android.synthetic.main.view_meditation_hrv.view.*

class RealtimeHRVView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mInfoIconRes: Int? = null
    private var mLineColor: Int = Color.parseColor("#ff4852")
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_hrv, null)
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
            R.styleable.RealtimeHRVView
        )
        mMainColor = typeArray.getColor(R.styleable.RealtimeHRVView_rhrvv_mainColor, mMainColor)
        mTextColor = typeArray.getColor(R.styleable.RealtimeHRVView_rhrvv_textColor, mTextColor)
        mBg = typeArray.getDrawable(R.styleable.RealtimeHRVView_rhrvv_background)
        mIsShowInfoIcon = typeArray.getBoolean(R.styleable.RealtimeHRVView_rhrvv_isShowInfoIcon, true)
        mInfoUrl = typeArray.getString(R.styleable.RealtimeHRVView_rhrvv_infoUrl)
        if (mInfoUrl == null) {
            mInfoUrl = INFO_URL
        }
        mTextFont = typeArray.getString(R.styleable.RealtimeHRVView_rhrvv_textFont)
        mLineColor =
            typeArray.getColor(R.styleable.RealtimeHRVView_rhrvv_lineColor, mLineColor)
        initView()
    }

    fun initView() {
        icon_loading.loadGif("loading.gif")
        if (mInfoIconRes != null) {
            iv_brain_real_time_info.setImageResource(mInfoIconRes!!)
        }
        iv_brain_real_time_info.setOnClickListener {
            var uri = Uri.parse(mInfoUrl)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        tv_title.setTextColor(mMainColor)
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
        sf_hrv.setBackgroundColor(bgColor)
        sf_hrv.setLineColor(mLineColor)
        setTextFont()
    }

    private fun setTextFont() {
        if (mTextFont == null) {
            return
        }
        var typeface = Typeface.createFromAsset(context.assets, mTextFont)
        tv_title.typeface = typeface
    }

    fun appendHrv(data: Double?) {
        if (data == null){
            return
        }
        mSelfView.findViewById<HRVSurfaceView>(R.id.sf_hrv).setData(data)
    }

    fun showLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<GifMovieView>(R.id.icon_loading).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
    }

    fun hideLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
    }

    fun showDisconnectTip() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<GifMovieView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        var sampleBrainData = ArrayList<Double>()
        for (i in 0..150) {
            sampleBrainData.add(java.util.Random().nextDouble() * 50)
        }
        mSelfView.findViewById<HRVSurfaceView>(R.id.sf_hrv).setSampleData(sampleBrainData)
    }

    fun hideDisconnectTip(){
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
        mSelfView.findViewById<GifMovieView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
        mSelfView.findViewById<HRVSurfaceView>(R.id.sf_hrv).hideSampleData()
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
}