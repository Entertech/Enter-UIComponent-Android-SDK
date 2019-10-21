package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R
import kotlinx.android.synthetic.main.layout_chart_legend.view.*


class ChartLegendView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    LinearLayout(context, attributeSet, def) {
    var mColor = Color.parseColor("#5167f8")
    var mLegend = ""
    var self: View = LayoutInflater.from(context).inflate(R.layout.layout_chart_legend, null)

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ChartLegendView)
        mColor = typeArray.getColor(R.styleable.ChartLegendView_clv_color, mColor)
        mLegend = typeArray.getString(R.styleable.ChartLegendView_clv_legend)
        var layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        self.layoutParams = layoutParams
        initView()
        addView(self)
    }

    fun initView() {
        self.findViewById<TextView>(R.id.tv_text).text = mLegend
        val gradientDrawable = self.findViewById<TextView>(R.id.tv_icon).background as GradientDrawable
        gradientDrawable.setColor(mColor)
        self.findViewById<TextView>(R.id.tv_icon).background = gradientDrawable
    }

    fun setTextColor(color: Int) {
        tv_text.setTextColor(color)
    }

    fun setLegendIconColor(color: Int) {
        mColor = color
        initView()
    }
}