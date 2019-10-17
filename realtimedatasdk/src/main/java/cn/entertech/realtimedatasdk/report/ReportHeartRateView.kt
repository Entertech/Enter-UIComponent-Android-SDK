package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.report.HeartRateChart

class ReportHeartRateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_card_heart_rate, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        mSelfView.findViewById<TextView>(R.id.tv_title).text = "Heart Rate"
        mSelfView.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.vector_drawable_heart)
        mSelfView.findViewById<ImageView>(R.id.iv_info).setOnClickListener {
        }
    }

    fun setData(values: List<Double>, max: Float, min: Float, avg: Float) {
        mSelfView.findViewById<HeartRateChart>(R.id.sac_brain_chart).setValues(values)
        mSelfView.findViewById<TextView>(R.id.tv_heart_avg).text = "AVG:${avg.toInt()}"
        mSelfView.findViewById<TextView>(R.id.tv_heart_max).text = "MAX:${max.toInt()}"
        mSelfView.findViewById<TextView>(R.id.tv_heart_min).text = "MIN:${min.toInt()}"
    }

    fun isDataNull(flag: Boolean) {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_no_data_cover).visibility = if (flag) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}