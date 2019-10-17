package cn.entertech.realtimedatasdk.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil

class CustomVerticalTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private var mText: String? = ""

    private var mTextColor: Int = Color.parseColor("#666666")

    private var mTextSize: Float = ScreenUtil.dip2px(context, 12f).toFloat()

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomVerticalTextView)
        mText = typeArray.getString(R.styleable.CustomVerticalTextView_cvtv_text)
        mTextColor = typeArray.getColor(R.styleable.CustomVerticalTextView_cvtv_textColor, mTextColor)
        mTextSize = typeArray.getDimension(R.styleable.CustomVerticalTextView_cvtv_textSize, mTextSize)
        typeArray?.recycle()
        initPaint()
    }

    private lateinit var mPaint: Paint

    private var mTextBound: Rect? = null

    private var mTextTop: Float = 0f
    private var mTextHeight: Float = 0f

    fun initPaint() {
        mPaint = Paint()
        mPaint.textSize = mTextSize
        mPaint.color = mTextColor
        mPaint.textAlign = Paint.Align.CENTER
        mTextTop = Math.abs(mPaint.fontMetrics.top)
        mTextHeight = Math.abs(mPaint.fontMetrics.top)+Math.abs(mPaint.fontMetrics.bottom)
        mTextBound = Rect()
        if (mText != null) {
            mPaint.getTextBounds(mText, 0, mText!!.length, mTextBound)
        }
    }

    private fun measureWidth(measureSpec: Int): Int {
        var result: Int
        val specSize = View.MeasureSpec.getSize(measureSpec)
        val specMode = View.MeasureSpec.getMode(measureSpec)
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = ScreenUtil.dip2px(context, 200f)
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mTextBound != null && mTextHeight != null) {
            setMeasuredDimension(mTextHeight.toInt(), mTextBound!!.width())
        } else {
            setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(heightMeasureSpec))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(mTextTop, measuredHeight / 2f)
        canvas?.rotate(-90f)
        canvas?.drawText(mText, 0f, 0f, mPaint)
    }

    fun setText(text:String){
        this.mText = text
        invalidate()
    }

    fun setTextColor(color:Int){
        this.mTextColor = color
        mPaint.color = mTextColor
        invalidate()
    }
}