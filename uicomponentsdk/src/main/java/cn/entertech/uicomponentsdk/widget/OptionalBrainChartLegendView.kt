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
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import kotlinx.android.synthetic.main.layout_optional_brain_chart_legend.view.*


class OptionalBrainChartLegendView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    LinearLayout(context, attributeSet, def) {

    private var mOnCheckListener: ((Boolean) -> Unit)? = null
    var mIsChecked = true
    var mColor = Color.parseColor("#5167f8")
    var mLegend = ""
    var self: View =
        LayoutInflater.from(context).inflate(R.layout.layout_optional_brain_chart_legend, null)

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ChartLegendView)
        mColor = typeArray.getColor(R.styleable.ChartLegendView_clv_color, mColor)
        mLegend = typeArray.getString(R.styleable.ChartLegendView_clv_legend)
        var layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        self.layoutParams = layoutParams
        initView()
        addView(self)
    }

    fun initView() {
        self.findViewById<TextView>(R.id.tv_text).text = mLegend
        val gradientDrawable =
            self.findViewById<LinearLayout>(R.id.ll_bg).background as GradientDrawable
        gradientDrawable.setColor(getOpacityColor(mColor, 0.2f))
        self.findViewById<LinearLayout>(R.id.ll_bg).background = gradientDrawable
        self.findViewById<TextView>(R.id.tv_text).setTextColor(mColor)
        self.findViewById<ImageView>(R.id.iv_icon).imageTintList =
            ColorStateList.valueOf(mColor)
        if (mIsChecked) {
            self.findViewById<TextView>(R.id.tv_legend_bg).visibility = View.VISIBLE
            self.findViewById<ImageView>(R.id.iv_icon).setImageDrawable(ContextCompat.getDrawable(context,R.drawable.vector_drawable_brain_legend_select))
        } else {
            self.findViewById<ImageView>(R.id.iv_icon).setImageDrawable(ContextCompat.getDrawable(context,R.drawable.vector_drawable_brain_legend_unselect))
            self.findViewById<TextView>(R.id.tv_legend_bg).visibility = View.GONE
        }
//        self.findViewById<LinearLayout>(R.id.ll_bg).setOnClickListener {
//            mIsChecked = !mIsChecked
//            setCheck(mIsChecked)
//            mOnCheckListener?.invoke(mIsChecked)
//        }
    }

    fun setTextColor(color: Int) {
        tv_text.setTextColor(color)
    }

    fun setLegendIconColor(color: Int) {
        mColor = color
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