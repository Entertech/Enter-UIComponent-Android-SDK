package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil

class GradientSweepBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {
    private var mMarginBottomDegree: Int = 4
    private lateinit var mScaleLinePaint: Paint
    private var mScaleLineColor: Int = Color.parseColor("#FF6682")
    private var mScaleLineWidth: Float = ScreenUtil.dip2px(context, 2f).toFloat()
    private var mScaleLineLength: Float = ScreenUtil.dip2px(context, 56f).toFloat()
    private var mTextBottomMargin: Float = ScreenUtil.dip2px(context, 10f).toFloat()
    private lateinit var mTextPaint: Paint
    private lateinit var mBarPaint: Paint
    private lateinit var mBgBarPaint: Paint
    private var mBgBarColor: Int = Color.parseColor("#F1F5F6")
    private var mStartColor: Int = Color.parseColor("#5E75FF")
    private var mEndColor: Int = Color.parseColor("#FB9C98")
    private var mTextSize: Float = ScreenUtil.dip2px(context, 32f).toFloat()
    private var mTextColor: Int = Color.BLACK
    private var mBarWidth: Float = ScreenUtil.dip2px(context, 48f).toFloat()

    init {
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.GradientSweepBar
        )
        mBarWidth = typeArray.getDimension(R.styleable.GradientSweepBar_gsv_barWidth, mBarWidth)
        mTextColor = typeArray.getColor(R.styleable.GradientSweepBar_gsv_textColor, mTextColor)
        mTextSize = typeArray.getDimension(R.styleable.GradientSweepBar_gsv_textSize, mTextSize)
        mStartColor = typeArray.getColor(R.styleable.GradientSweepBar_gsv_startColor, mStartColor)
        mEndColor = typeArray.getColor(R.styleable.GradientSweepBar_gsv_endColor, mEndColor)
        mBgBarColor = typeArray.getColor(R.styleable.GradientSweepBar_gsv_bgBarColor, mBgBarColor)
        mMarginBottomDegree =
            typeArray.getInteger(R.styleable.GradientSweepBar_gsv_marginBottomDegree, 4)
        mScaleLineLength = typeArray.getDimension(
            R.styleable.GradientSweepBar_gsv_scaleLineLength,
            mScaleLineLength
        )
        mScaleLineWidth = typeArray.getDimension(
            R.styleable.GradientSweepBar_gsv_scaleLineWidth,
            mScaleLineWidth
        )
        mScaleLineColor =
            typeArray.getColor(R.styleable.GradientSweepBar_gsv_scaleLineColor, mScaleLineColor)
        mTextBottomMargin =
            typeArray.getDimension(
                R.styleable.GradientSweepBar_gsv_textBottomMargin,
                mTextBottomMargin
            )

        typeArray?.recycle()

        initPaint()
    }

    private fun initPaint() {
        mBgBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgBarPaint.style = Paint.Style.STROKE
        mBgBarPaint.color = mBgBarColor
        mBgBarPaint.strokeWidth = mBarWidth
        mBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBarPaint.style = Paint.Style.STROKE
        mBarPaint.strokeWidth = mBarWidth
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = mTextSize
        mTextPaint.color = mTextColor
        mScaleLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mScaleLinePaint.color = mScaleLineColor
        mScaleLinePaint.strokeWidth = mScaleLineWidth
        mScaleLinePaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        onDrawBgBar(canvas)
        onDrawBar(canvas, 0.8f)
        onDrawScaleLine(canvas, 0.8f)
        onDrawText(canvas)
    }

    private fun onDrawBar(canvas: Canvas?, percent: Float, paint: Paint) {
        canvas?.save()
        canvas?.translate(width / 2f, height.toFloat())
        var radius = (width / 2f).coerceAtMost(height.toFloat())
        canvas?.drawArc(
            -radius + mBarWidth / 2,
            -radius + mBarWidth / 2,
            radius - mBarWidth / 2,
            radius - mBarWidth / 2,
            -180f + mMarginBottomDegree,
            percent * (180 - mMarginBottomDegree * 2),
            false,
            paint
        )
        canvas?.restore()
    }

    private fun onDrawBgBar(canvas: Canvas?) {
        onDrawBar(canvas, 1f, mBgBarPaint)
    }

    private fun onDrawBar(canvas: Canvas?, percent: Float) {
        mBarPaint.shader = LinearGradient(
            -width.toFloat() / 2f,
            0f,
            width.toFloat() / 2f,
            0f, mStartColor, mEndColor,
            Shader.TileMode.MIRROR
        )
        onDrawBar(canvas, percent, mBarPaint)
    }


    private fun onDrawScaleLine(canvas: Canvas?, percent: Float) {
        var radius = (width / 2f).coerceAtMost(height.toFloat())
        var rotationDegree = 90 - mMarginBottomDegree - percent * (180 - mMarginBottomDegree * 2)
        canvas?.save()
        canvas?.translate(width / 2f, height.toFloat())
        canvas?.rotate(-rotationDegree)
        var startY = radius - mBarWidth / 2f - mScaleLineLength / 2f
        var endY = radius - mBarWidth / 2f + mScaleLineLength / 2f
        canvas?.drawLine(0f, -startY, 0f, -endY, mScaleLinePaint)
        canvas?.restore()
    }

    private fun onDrawText(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(width / 2f, height.toFloat())
        canvas?.drawText("79", 0f, -mTextBottomMargin, mTextPaint)
        canvas?.restore()
    }
}