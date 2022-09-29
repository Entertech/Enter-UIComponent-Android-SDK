package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.dp
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_optional_brain_chart_legend.view.*
import org.w3c.dom.Text


class OptionalBrainChartLegendView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    LinearLayout(context, attributeSet, def) {

    private var mUnSelectTextColor: Int = Color.parseColor("#878894")
    private var mSelectTextColor: Int = Color.parseColor("#4B5DCC")
    private var mBgColor: Int = Color.parseColor("#F6F7FA")
    private var mOnCheckListener: ((Boolean) -> Unit)? = null
    var mIsChecked = true
    var mColor = Color.parseColor("#5167f8")
    var mLegend:String? = ""
    var self: View =
        LayoutInflater.from(context).inflate(R.layout.layout_optional_brain_chart_legend, null)

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ChartLegendView)
        mBgColor = typeArray.getColor(R.styleable.ChartLegendView_clv_bgColor, mBgColor)
        mUnSelectTextColor = typeArray.getColor(R.styleable.ChartLegendView_clv_unselectTextColor, mUnSelectTextColor)
        mSelectTextColor = typeArray.getColor(R.styleable.ChartLegendView_clv_selectTextColor, mSelectTextColor)
        mLegend = typeArray.getString(R.styleable.ChartLegendView_clv_legend)
        var layoutParams =
            LayoutParams(
                49f.dp().toInt(),
                23f.dp().toInt()
            )
        self.layoutParams = layoutParams
        initView()
        addView(self)
    }

    fun initView() {
        val bg = self.findViewById<TextView>(R.id.tv_legend).background as GradientDrawable
        bg.setColor(mBgColor)
        self.findViewById<TextView>(R.id.tv_legend).text = mLegend
        if (mIsChecked) {
            self.findViewById<TextView>(R.id.tv_legend).setTextColor(mSelectTextColor)
        } else {
            self.findViewById<TextView>(R.id.tv_legend).setTextColor(mUnSelectTextColor)
        }
    }

    fun setUnselectTextColor(color: Int) {
        this.mUnSelectTextColor = color
        initView()
    }

    fun setSelectTextColor(color:Int){
        this.mSelectTextColor = color
        initView()
    }

    fun setBgColor(color: Int){
        this.mBgColor = color
        initView()
    }

    fun setText(text: String) {
        this.mLegend = text
        initView()
    }

    fun addOnCheckListener(listener: ((Boolean) -> Unit)) {
        this.mOnCheckListener = listener
    }

    fun setCheck(isChecked: Boolean) {
        this.mIsChecked = isChecked
        initView()
    }
}