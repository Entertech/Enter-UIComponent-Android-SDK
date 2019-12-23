package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil

class HalfCircleProgressBar @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {
    private lateinit var mTextPaint: Paint
    private var mBarWidth: Float = ScreenUtil.dip2px(context, 8f).toFloat()
    private var mBarColor: Int = Color.BLUE
    private var mTextSize: Float = ScreenUtil.dip2px(context, 13f).toFloat()
    private var mTextColor: Int = Color.BLACK
    private var mText: String? = ""
    private lateinit var mBarPaint: Paint

    init {
        var typeArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.HalfCircleProgressBar)
        mText = typeArray.getString(R.styleable.HalfCircleProgressBar_hcpb_text)
        mTextColor =
            typeArray.getColor(R.styleable.HalfCircleProgressBar_hcpb_textColor, mTextColor)
        mTextSize =
            typeArray.getDimension(R.styleable.HalfCircleProgressBar_hcpb_textSize, mTextSize)
        mBarColor = typeArray.getColor(R.styleable.HalfCircleProgressBar_hcpb_barColor, mBarColor)
        mBarWidth =
            typeArray.getDimension(R.styleable.HalfCircleProgressBar_hcpb_barWidth, mBarWidth)
        typeArray?.recycle()
        initPaint()
    }

    private fun initPaint() {
        mBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBarPaint.color = mBarColor
        mBarPaint.strokeWidth = mBarWidth
        mBarPaint.style = Paint.Style.STROKE

        mTextPaint = Paint()
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize

    }

    override fun onDraw(canvas: Canvas?) {
        onDrawBgBar(canvas)
        onDrawBar(canvas, 0.7f)
        onDrawText(canvas)
    }

    private fun onDrawBar(canvas: Canvas?, percent: Float, barColor: Int) {
        canvas?.save()
        mBarPaint.color = barColor
        canvas?.translate(width / 2f, height.toFloat())
        var radius = (width / 2f).coerceAtMost(height.toFloat())
        canvas?.drawArc(
            -radius + mBarWidth / 2,
            -radius + mBarWidth / 2,
            radius - mBarWidth / 2,
            radius - mBarWidth / 2,
            -176f,
            percent * 180 - 8,
            false,
            mBarPaint
        )
        mBarPaint.style = Paint.Style.FILL
        canvas?.rotate(-86f)
        canvas?.drawCircle(0f, -height + mBarWidth / 2f, mBarWidth / 2f, mBarPaint)
        canvas?.rotate(percent * 180 - 8, 0f, 0f)
        canvas?.drawCircle(0f, -height + mBarWidth / 2f, mBarWidth / 2f, mBarPaint)
        mBarPaint.style = Paint.Style.STROKE
        canvas?.restore()
    }

    private fun onDrawBgBar(canvas: Canvas?) {
        onDrawBar(canvas, 1f, Color.parseColor("#F1F5F6"))
    }

    private fun onDrawBar(canvas: Canvas?, percent: Float) {
        onDrawBar(canvas, percent, Color.BLUE)
    }

    private fun onDrawText(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(width / 2f, height.toFloat())
        mTextPaint.textAlign = Paint.Align.CENTER
        canvas?.drawText(
            mText,
            0f,
            0f - (width / 2f).coerceAtMost(height.toFloat()) / 10f,
            mTextPaint
        )
        canvas?.restore()
    }
}