package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R

class ChartIconView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    var view = LayoutInflater.from(context).inflate(R.layout.view_chart_highlight_icon,null)
    var color:Int = Color.parseColor("#ff0000")
    set(value) {
        var bgCenter = view.findViewById<TextView>(R.id.tv_center_icon).background as GradientDrawable
        var bgOuter = view.findViewById<TextView>(R.id.tv_outer_icon).background as GradientDrawable
        bgCenter.setColor(value)
        bgOuter.setColor(Color.WHITE)
    }

    init {
        var layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        addView(view)
    }
}