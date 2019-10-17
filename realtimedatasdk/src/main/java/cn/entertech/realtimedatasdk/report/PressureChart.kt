package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil
import cn.entertech.realtimedatasdk.utils.convertTransparentToHex
import java.util.*

class PressureChart @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    View(context, attributeSet, def) {
    var mBaseColor: String = "ff6682"
    var mValue: List<Double> = ArrayList()
    var mTimeStampLsit: ArrayList<String> = ArrayList()
    lateinit var curvePaint: Paint
    var mTimeStampTextHeight: Float = 0f
    var mTimeStampPaddingTop: Float = 10f
    var timestampMaxCount = 8
    var timestampOffset: Float = 0f
    lateinit var mGridPait: Paint
    lateinit var mTimestampPaint: Paint
    var mTimeStampTextColor: Int = Color.parseColor("#9aa1a9")
    var mGridLineLength: Float = 0f
    var mGridLineWidth: Float = 0f
    var mGridLineColor = mTimeStampTextColor
    var mTimestampTextSize = ScreenUtil.dip2px(context, 24f).toFloat()

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.PressureChart)
        mBaseColor = typeArray.getString(R.styleable.PressureChart_pc_baseColor)
        mBaseColor = mBaseColor?.replace("#", "")
        mGridLineLength = typeArray.getDimension(
            R.styleable.PressureChart_pc_gridLineLength,
            ScreenUtil.dip2px(context, 10f).toFloat()
        )
        mGridLineWidth =
            typeArray.getDimension(R.styleable.PressureChart_pc_gridLineWidth, ScreenUtil.dip2px(context, 1f).toFloat())
        mGridLineColor = typeArray.getColor(R.styleable.PressureChart_pc_gridLineColor, mGridLineColor)
        mTimestampTextSize = typeArray.getDimension(R.styleable.PressureChart_pc_timestampTextSize, mTimestampTextSize)
        mTimeStampTextColor = typeArray.getColor(R.styleable.PressureChart_pc_timestampTextColor, mTimeStampTextColor)
        mTimeStampPaddingTop =
            typeArray.getDimension(R.styleable.PressureChart_pc_timestampTextPaddingTop, mTimeStampPaddingTop)
        initPaint()
    }

    fun setValue(values: List<Double>) {
        this.mValue = values
    }

    fun initPaint() {
        curvePaint = Paint()
        curvePaint.style = Paint.Style.FILL
        mTimestampPaint = Paint()
        mTimestampPaint.textAlign = Paint.Align.CENTER
        mTimestampPaint.color = mTimeStampTextColor
        mTimestampPaint.textSize = mTimestampTextSize

        mGridPait = Paint()
        mGridPait.color = mGridLineColor
        mGridPait.strokeWidth = mGridLineWidth
        mTimeStampTextHeight = Math.abs(mTimestampPaint.fontMetrics.top)
        +Math.abs(mTimestampPaint.fontMetrics.bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        onDrawData(canvas)
        drawGridLineAndTimestamp(canvas)
    }


    fun drawGridLineAndTimestamp(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(0f, height - mTimeStampTextHeight - mTimeStampPaddingTop - mGridLineLength)
        if (mValue != null && mValue.size > 0) {
            mTimeStampLsit.clear()
            var lineOffset = width * 1f / mValue.size
            var totalMin = mValue.size * 800f / 1000f / 60f
            var minOffset = (totalMin / timestampMaxCount).toInt() + 1
            timestampOffset = 1000 * 60f / 800 * lineOffset * minOffset
            var timestampCount = width / timestampOffset + 1
            for (i in 0 until timestampCount.toInt()) {
                mTimeStampLsit.add("${i * minOffset}")
            }
        }
        for (i in 0 until mTimeStampLsit.size) {
            if (i != 0) {
                canvas?.drawLine(
                    timestampOffset * i,
                    0f,
                    timestampOffset * i,
                    mGridLineLength,
                    mGridPait
                )
            }
            when (i) {
                0 -> mTimestampPaint.textAlign = Paint.Align.LEFT
//                mTimeStampLsit.size - 1 -> mTimestampPaint.textAlign = Paint.Align.RIGHT
                else -> mTimestampPaint.textAlign = Paint.Align.CENTER
            }
            canvas?.drawText(
                mTimeStampLsit[i],
                timestampOffset * i,
                mGridLineLength + mTimeStampPaddingTop + Math.abs(mTimestampPaint.fontMetrics.ascent),
                mTimestampPaint
            )
        }
        canvas?.restore()
    }

    fun onDrawData(canvas: Canvas?) {
//        var isDataNormal = false
        var offset = width.toFloat() / mValue.size
        curvePaint.strokeWidth = offset
        for (i in 0 until mValue.size) {
            if (mValue[i] == 0.0) {
                curvePaint.color = Color.parseColor("#E9EBF1")
            } else {
//                isDataNormal = true
                curvePaint.color = Color.parseColor("#${convertTransparentToHex(mValue[i].toInt())}$mBaseColor")
            }
            canvas?.drawLine(
                i * offset + offset / 2,
                0f,
                i * offset,
                height - mTimeStampTextHeight - mTimeStampPaddingTop - mGridLineLength,
                curvePaint
            )
        }
    }
}