package cn.entertech.realtimedatasdk.realtime

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil

class EmotionIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private lateinit var mBarPaint: Paint
    private lateinit var mTextPaint: Paint
    private lateinit var mIndicatorPaint: Paint

    private var mTextColor: Int = Color.parseColor("#000000")
    private var mIndicatorColor: Int = Color.parseColor("#ff6682")
    private var mTextSize: Int = 24
    private var mTextBottomPadding: Int = 10
    private var mIndicatorTopPadding: Int = 0
    private var mLeftPadding: Int = 0
    private var mRightPadding: Int = 0
    private var mBarWidth: Int = 30
    private var mWidth: Int = 0
    private var mHight: Int = 0
    private var mValue: Float = 0f

    private var scales = arrayOf(0, 60, 80, 100)
    private var indicateItems = arrayListOf<IndicateItem>()

    init {
        init(context, attrs)
        initPaint()
        initIndicateItem()
    }


    fun init(context: Context, attrs: AttributeSet?) {
        var typeArray = context.obtainStyledAttributes(attrs,
            R.styleable.EmotionIndicatorView
        )
        mTextColor = typeArray.getColor(R.styleable.EmotionIndicatorView_eiv_textColor, mTextColor)
        mTextSize = typeArray.getDimensionPixelSize(
            R.styleable.EmotionIndicatorView_eiv_textSize,
            ScreenUtil.dip2px(context, 24f)
        )
        mTextBottomPadding = typeArray.getDimensionPixelSize(
            R.styleable.EmotionIndicatorView_eiv_textBottomPadding,
            ScreenUtil.dip2px(context, 10f)
        )
        mBarWidth = typeArray.getDimensionPixelSize(
            R.styleable.EmotionIndicatorView_eiv_barWidth,
            ScreenUtil.dip2px(context, 10f)
        )

        mIndicatorColor = typeArray.getColor(R.styleable.EmotionIndicatorView_eiv_indicatorColor, mIndicatorColor)
        mIndicatorTopPadding =
            typeArray.getDimensionPixelSize(R.styleable.EmotionIndicatorView_eiv_indicatorTopPadding, 0)
        mLeftPadding =
            typeArray.getDimensionPixelSize(R.styleable.EmotionIndicatorView_eiv_leftPadding, 0)
        mRightPadding =
            typeArray.getDimensionPixelSize(R.styleable.EmotionIndicatorView_eiv_rightPadding, 0)
        typeArray?.recycle()
    }


    fun initIndicateItem() {
        indicateItems.add(
            IndicateItem(
                0.2f,
                Color.parseColor("#ffccd2")
            )
        )
        indicateItems.add(
            IndicateItem(
                0.5f,
                Color.parseColor("#ff98a9")
            )
        )
        indicateItems.add(
            IndicateItem(
                0.3f,
                Color.parseColor("#cc5268")
            )
        )
    }


    fun initPaint() {
        mBarPaint = Paint()
        mBarPaint.strokeWidth = mBarWidth.toFloat()
        mTextPaint = Paint()
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER

        mIndicatorPaint = Paint()
        mIndicatorPaint.color = mIndicatorColor
        val pathEffect = CornerPathEffect(10f)
        mIndicatorPaint.pathEffect = pathEffect
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHight = h
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(0f, mHight / 2f)
        onDrawBar(canvas)
        onDrawScale(canvas)
        onDrawIndicator(canvas)
    }

    private fun onDrawBar(canvas: Canvas?) {
        mBarPaint.color = indicateItems[0].indicateColor
        canvas?.drawCircle(mBarWidth / 2f + mLeftPadding, 0f, mBarWidth / 2f, mBarPaint)
        var startX = mBarWidth / 2f + mLeftPadding
        for (i in 0 until indicateItems.size) {
            mBarPaint.color = indicateItems[i].indicateColor
            var endX = startX + indicateItems[i].indicatePercent * (mWidth - (mBarWidth + mLeftPadding + mRightPadding))
            canvas?.drawLine(startX, 0f, endX, 0f, mBarPaint)
            startX = endX
        }
        mBarPaint.color = indicateItems[indicateItems.size - 1].indicateColor
        canvas?.drawCircle(mWidth - (mBarWidth / 2f + mRightPadding), 0f, mBarWidth / 2f, mBarPaint)
    }


    private fun onDrawScale(canvas: Canvas?) {
        for (i in 0 until scales.size) {
            var offset =
                (scales[i] * 1f - scales[0]) / (scales[scales.size - 1] - scales[0]) * (mWidth - (mBarWidth + mLeftPadding + mRightPadding))
            canvas?.drawText(
                "${scales[i]}",
                offset + (mBarWidth + mLeftPadding + mRightPadding) / 2,
                -(mBarWidth / 2f + mTextBottomPadding),
                mTextPaint
            )
        }
    }


    private fun onDrawIndicator(canvas: Canvas?) {
        var path = Path()
        var curX =
            (mValue - (scales[0])) / (scales[scales.size - 1] - (scales[0])) * (mWidth - (mBarWidth + mLeftPadding + mRightPadding)) + (mBarWidth + mLeftPadding + mRightPadding) / 2
        path.moveTo(curX, mBarWidth / 2f + mIndicatorTopPadding)
        path.lineTo(curX + mBarWidth * 2 / 3, mBarWidth / 2f + mIndicatorTopPadding + mBarWidth)
        path.lineTo(curX - mBarWidth * 2 / 3, mBarWidth / 2f + mIndicatorTopPadding + mBarWidth)
        path.close()
        canvas?.drawPath(path, mIndicatorPaint)
    }

    fun setValue(value: Float) {
        mValue = value
        invalidate()
    }

    fun setScales(array: Array<Int>) {
        this.scales = array
        invalidate()
    }

    fun setIndicatorItems(items: ArrayList<IndicateItem>) {
        this.indicateItems = items
        invalidate()
    }

    fun setIndicatorColor(color:Int){
        this.mIndicatorColor = color
        mIndicatorPaint.color = mIndicatorColor
        invalidate()
    }

    fun setScaleTextColor(color:Int){
        this.mTextColor = color
        mTextPaint.color = mTextColor
        invalidate()
    }

    data class IndicateItem(val indicatePercent: Float, val indicateColor: Int)
}