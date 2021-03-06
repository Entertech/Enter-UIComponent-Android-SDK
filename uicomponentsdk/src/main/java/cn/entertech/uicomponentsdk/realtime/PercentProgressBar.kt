package cn.entertech.uicomponentsdk.realtime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil

class PercentProgressBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {
    var mWidth: Int = 0
    var mHeight: Int = 0
    lateinit var mLabelTextPaint: Paint
    lateinit var mPercentTextPaint: Paint
    lateinit var mBarPaint: Paint
    var mLabelText: String? = null
    var mLabelTextColor: Int = Color.parseColor("#666666")
    var mPercentTextColor: Int = Color.parseColor("#999999")
    var mBarColor: Int = Color.parseColor("#FF0000")
    var mLabelTextSize: Float = ScreenUtil.dip2px(context, 24f).toFloat()
    var mPercentTextSize: Float = ScreenUtil.dip2px(context, 24f).toFloat()
    var mBarHeight: Float = 30f
    var mBarLeftPadding: Float = 16f
    var mBarRightPadding: Float = 16f
    var mLeftPadding: Float = 16f
    var mRightPadding: Float = 16f
    var mPercent: Float = 0f
    var mDBValue: Float = 0f

    companion object {
        const val MAX_VALUE = 120
        const val POWER_MODE_DB = "db"
        const val POWER_MODE_RATE = "rate"
    }

    var mPowerMode: String = POWER_MODE_DB

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.PercentProgressBar)
        mLabelText = typeArray.getString(R.styleable.PercentProgressBar_ppb_labelText)
        mLabelTextColor =
            typeArray.getColor(R.styleable.PercentProgressBar_ppb_labelTextColor, mLabelTextColor)
        mLabelTextSize =
            typeArray.getDimension(R.styleable.PercentProgressBar_ppb_labelTextSize, mLabelTextSize)

        mPercent = typeArray.getFloat(R.styleable.PercentProgressBar_ppb_percent, 0f)
        mPercentTextColor = typeArray.getColor(
            R.styleable.PercentProgressBar_ppb_percentTextColor,
            mPercentTextColor
        )
        mPercentTextSize = typeArray.getDimension(
            R.styleable.PercentProgressBar_ppb_percentTextSize,
            mPercentTextSize
        )

        mBarColor = typeArray.getColor(R.styleable.PercentProgressBar_ppb_barColor, mBarColor)
        mBarHeight = typeArray.getDimension(R.styleable.PercentProgressBar_ppb_barWidth, mBarHeight)
        mBarLeftPadding = typeArray.getDimension(
            R.styleable.PercentProgressBar_ppb_barLeftPadding,
            mBarLeftPadding
        )
        mBarRightPadding = typeArray.getDimension(
            R.styleable.PercentProgressBar_ppb_barRightPadding,
            mBarRightPadding
        )
        mLeftPadding =
            typeArray.getDimension(R.styleable.PercentProgressBar_ppb_leftPadding, mLeftPadding)
        mRightPadding =
            typeArray.getDimension(R.styleable.PercentProgressBar_ppb_rightPadding, mRightPadding)
        typeArray.recycle()
        initPaint()
    }

    fun initPaint() {
        mLabelTextPaint = Paint()
        mLabelTextPaint.color = mLabelTextColor
        mLabelTextPaint.textSize = mLabelTextSize

        mPercentTextPaint = Paint()
        mPercentTextPaint.color = mPercentTextColor
        mPercentTextPaint.textSize = mPercentTextSize

        mBarPaint = Paint()
        mBarPaint.color = mBarColor
        mBarPaint.strokeWidth = mBarHeight

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    var labelTextWidth: Float = 0f
    var percentTextWidth: Float = 0f
    var barMaxWidth: Float = 0f

    fun transformDB2Rate(value:Float):Float{
        var rate = 0f
        if (value in 0f..70f) {
            rate = value / 70 * 1f / 10
        } else if (value in 70f..100f) {
            rate = (value - 70) / 30 * 4 / 5 + 1f / 10
        } else if (value > 100f){
            rate = (value - 100) / 20 * 1 / 10 + 9f / 10
        }
        if (rate > 1f){
            rate = 1f
        }
        return rate
    }

    fun formatNum(number: Float): Float? {
        var value = 0f
        try {
            value = String.format("%.1f", number).toFloat()
        } catch (e: Exception) {

        } finally {
            return value
        }
    }
    override fun onDraw(canvas: Canvas) {
        var percentText = ""
        if (mPowerMode == POWER_MODE_DB) {
            mPercent = transformDB2Rate(mDBValue)
        } else {
            percentText = "${formatNum(mPercent * 100)}%"
        }
        labelTextWidth = mLabelTextPaint.measureText(mLabelText)
        percentTextWidth = mLabelTextPaint.measureText(percentText)
        barMaxWidth =
            mWidth - labelTextWidth - percentTextWidth - mLeftPadding - mRightPadding - mBarLeftPadding - mBarRightPadding
        var labelTextBaseline =
            (Math.abs(mLabelTextPaint.fontMetrics.descent) + Math.abs(mLabelTextPaint.fontMetrics.ascent)) / 2 - Math.abs(
                mLabelTextPaint.fontMetrics.descent
            )

        var percentTextBaseline =
            (Math.abs(mPercentTextPaint.ascent()) + Math.abs(mPercentTextPaint.descent())) / 2 - Math.abs(
                mPercentTextPaint.descent()
            )
        canvas.translate(0f, mHeight / 2f)
        canvas.drawText(mLabelText!!, mLeftPadding, labelTextBaseline, mLabelTextPaint)
        drawBar(canvas)
        canvas.drawText(
            percentText,
            labelTextWidth + mBarLeftPadding + barMaxWidth * mPercent + mBarRightPadding + mLeftPadding,
            percentTextBaseline,
            mPercentTextPaint
        )
    }

    private fun drawBar(canvas: Canvas) {
        canvas.drawCircle(
            labelTextWidth + mBarHeight / 2 + mBarLeftPadding + mLeftPadding,
            0f,
            mBarHeight / 2,
            mBarPaint
        )
        canvas.drawLine(
            labelTextWidth + mBarHeight / 2 + mBarLeftPadding + mLeftPadding,
            0f,
            labelTextWidth + mBarLeftPadding + barMaxWidth * mPercent - mBarHeight / 2 + mLeftPadding,
            0f,
            mBarPaint
        )
        canvas.drawCircle(
            labelTextWidth + mBarLeftPadding + barMaxWidth * mPercent - mBarHeight / 2 + mLeftPadding,
            0f,
            mBarHeight / 2,
            mBarPaint
        )
    }

    fun setPercent(value: Float?) {
        if (value == null) {
            return
        }
        mPercent = value
        invalidate()
    }

    fun setDBValue(db: Float?) {
        if (db == null) {
            return
        }
        this.mDBValue = db
        invalidate()
    }
    fun setBarColor(color:Int){
        this.mBarColor = color
        this.mBarPaint.color = mBarColor
        invalidate()
    }
    fun setPowerMode(mode: String) {
        this.mPowerMode = mode
        invalidate()
    }
    fun setLabelTextColor(color:Int) {
        this.mLabelTextColor = color
        this.mLabelTextPaint.color = mLabelTextColor
        invalidate()
    }

    fun setPercentTextColor(color:Int){
        this.mPercentTextColor = color
        this.mPercentTextPaint.color = mPercentTextColor
        invalidate()
    }

//    fun setBackgroundColor(color:Int){
//
//    }

}