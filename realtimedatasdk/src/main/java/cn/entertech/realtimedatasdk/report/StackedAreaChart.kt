package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil
import cn.entertech.realtimedatasdk.utils.getChartAbsoluteTime

class StackedAreaChart @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    View(context, attributeSet, def) {

    private var mStartTime: Long? = null
    private var mIsAbsoluteTime: Boolean = false
    var mStackItems: ArrayList<StackItem>? = null
    var mStackPaints: ArrayList<Paint> = ArrayList()
    var mTimeStampLsit: ArrayList<String> = ArrayList()
    var mWidth: Int = 0
    var mHeight: Int = 0
    var lineOffset: Float = 0f
    lateinit var mGridPait: Paint
    lateinit var mTimestampPaint: Paint
    var mGridLineColor: Int = Color.parseColor("#ffffff")
    var mBackgroundColor: Int = Color.parseColor("#ffffff")
    var mTimeStampTextColor: Int = Color.parseColor("#9aa1a9")
    var mTimeStampTextSize: Float
    var mTimeStampPaddingTop: Float
    var mTimeStampTextHeight: Float = 0f

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.StackedAreaChart)
        mGridLineColor = typeArray.getColor(R.styleable.StackedAreaChart_sac_gridLineColor, mGridLineColor)
        mTimeStampTextColor =
            typeArray.getColor(R.styleable.StackedAreaChart_sac_timeStampTextColor, mTimeStampTextColor)
        mTimeStampTextSize = typeArray.getDimension(
            R.styleable.StackedAreaChart_sac_timeStampTextSize,
            ScreenUtil.dip2px(context, 24f).toFloat()
        )
        mTimeStampPaddingTop = typeArray.getDimension(
            R.styleable.StackedAreaChart_sac_timeStampTextPaddingTop,
            ScreenUtil.dip2px(context, 20f).toFloat()
        )
        mBackgroundColor = typeArray.getColor(R.styleable.StackedAreaChart_sac_backgroundColor, mBackgroundColor)
        typeArray?.recycle()
        initPaint()
        this.setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun initPaint() {
        mGridPait = Paint()
        mGridPait.color = mGridLineColor
        mGridPait.strokeWidth = 2f
        mTimestampPaint = Paint()
        mTimestampPaint.textAlign = Paint.Align.CENTER
        mTimestampPaint.color = mTimeStampTextColor
        mTimestampPaint.textSize = mTimeStampTextSize

        mTimeStampTextHeight = Math.abs(mTimestampPaint.fontMetrics.top)
        +Math.abs(mTimestampPaint.fontMetrics.bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("StackAreaChart", "onSizeChanged")
        mWidth = w
        mHeight = h
        setPaintStrokeWidth(mWidth)
    }

    fun setPaintStrokeWidth(width: Int) {
        mStackPaints.forEach {
            if (mStackItems != null && mStackItems!!.size > 0) {
                lineOffset = width * 1f / (mStackItems!![0].stackData!!.size)
                it.strokeWidth = lineOffset
            }
        }
    }

    fun initPaint(stackItems: ArrayList<StackItem>) {
        stackItems.forEach {
            var paint = Paint()
            paint.color = it.stackColor
            mStackPaints.add(paint)
        }
    }

    /**
     * set stack data items
     */
    fun setStackItems(stackItems: ArrayList<StackItem>) {
        mStackItems = stackItems
        initPaint(stackItems)
        initTimestamp(stackItems)
    }

    var timestampMaxCount = 8
    var timestampOffset: Float = 0f
    fun initTimestamp(stackItems: ArrayList<StackItem>) {
        if (stackItems != null && stackItems.size > 0) {
            if (stackItems[0].stackData != null && stackItems[0].stackData!!.size > 0) {
                var totalMin = stackItems[0].stackData!!.size * 400f / 1000f / 60f
                var minOffset = (totalMin / timestampMaxCount).toInt() + 1
                timestampOffset = 1000 * 60f / 400 * lineOffset * minOffset
                var timestampCount = mWidth / timestampOffset + 1
                for (i in 0 until timestampCount.toInt()) {
                    mTimeStampLsit.add("${i * minOffset}")
                }
                if (mIsAbsoluteTime && mStartTime != null) {
                    mTimeStampLsit.clear()
                    for (i in 0 until timestampCount.toInt()) {
                        var time = mStartTime!! + i * minOffset * 60
                        mTimeStampLsit.add(getChartAbsoluteTime(time))
                    }
                }
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        Log.d("StackAreaChart", "onDraw")
        if (!checkNotNull()) {
            return
        }
        onDrawBackground(canvas)
        onDrawData(canvas)
        onDrawTimestamp(canvas)
    }

    fun onDrawBackground(canvas: Canvas?) {
        canvas?.drawColor(mBackgroundColor)
    }

    fun onDrawData(canvas: Canvas?) {
        setPaintStrokeWidth(width)
        var lastData = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
        for (i in 0 until mStackItems!![0].stackData!!.size) {
            var startY = 0.0
            for (j in 0 until mStackPaints.size) {
                var drawData = 0.0
                if (mStackItems!![j].stackData!![i] == 0.0) {
                    drawData = lastData[j]
                } else {
                    drawData = mStackItems!![j].stackData!![i]
                    lastData[j] = mStackItems!![j].stackData!![i]
                }
                var endY = drawData * (height - mTimeStampTextHeight - mTimeStampPaddingTop) + startY
                canvas?.drawLine(i * lineOffset, startY.toFloat(), i * lineOffset, endY.toFloat(), mStackPaints[j])
                startY = endY
            }
        }
    }

    fun onDrawTimestamp(canvas: Canvas?) {
        initTimestamp(mStackItems!!)
        for (i in 0 until mTimeStampLsit.size) {
            if (i != 0) {
                canvas?.drawLine(
                    timestampOffset * i,
                    0f,
                    timestampOffset * i,
                    (height - mTimeStampTextHeight - mTimeStampPaddingTop),
                    mGridPait
                )
            }
            if (i == 0) {
                mTimestampPaint.textAlign = Paint.Align.LEFT
            } else {
                mTimestampPaint.textAlign = Paint.Align.CENTER
            }
            canvas?.drawText(
                mTimeStampLsit[i],
                timestampOffset * i,
                height - mTimeStampTextHeight - mTimeStampPaddingTop + Math.abs(mTimestampPaint.fontMetrics.ascent),
                mTimestampPaint
            )
        }
    }

    private fun checkNotNull(): Boolean {
        return mStackPaints.size != null && mStackItems != null
                && mStackItems!!.size != 0 && mStackItems!![0].stackData != null
                && mStackItems!![0].stackData!!.isNotEmpty()
    }


    class StackItem {
        var stackData: List<Double>? = null
        var stackColor: Int = Color.parseColor("#5167f8")
    }

    fun setXAxisTextColor(color: Int) {
        this.mTimeStampTextColor = color
        invalidate()
    }

    fun setGridLineColor(color: Int) {
        this.mGridPait.color = color
        invalidate()
    }

    fun isAbsoluteTime(flag: Boolean, startTime: Long?) {
        this.mIsAbsoluteTime = flag
        this.mStartTime = startTime
        invalidate()
    }

}