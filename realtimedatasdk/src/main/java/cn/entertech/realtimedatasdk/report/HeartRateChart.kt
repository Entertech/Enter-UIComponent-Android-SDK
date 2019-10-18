package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil
import cn.entertech.realtimedatasdk.utils.getChartAbsoluteTime
import java.util.*

class HeartRateChart @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    View(context, attributeSet, def) {

    lateinit var mCruvePaint: Paint
    lateinit var noDataPaint: Paint
    lateinit var mBarPaint: Paint
    lateinit var mBarTextPaint: Paint
    var mHeartRateValues: List<Double> = ArrayList()
    var mTimeStampLsit: ArrayList<String> = ArrayList()
    var mTimeStampTextColor: Int = Color.parseColor("#9aa1a9")
    lateinit var mTimestampPaint: Paint
    lateinit var mGridPait: Paint
    var mTimeStampTextHeight: Float = 0f
    var mTimeStampPaddingTop: Float = 10f
    var timestampMaxCount = 8
    var timestampOffset: Float = 0f
    var mTimeStampTextSize: Float
    var mGridLineColor: Int = Color.parseColor("#ffffff")
    var mLeftBarWidth: Float = 24f
    var mCurveLineWidth: Float = 24f

    var mLowHeartRateColor: Int = Color.parseColor("#ffe4bb")
    var mNeutralHeartRateColor: Int = Color.parseColor("#ffb952")
    var mHighHeartRateColor: Int = Color.parseColor("#f88883")
    var mLeftBarTextSize: Float = 50f
    var mLeftBarTextPadding: Float = 10f
    var mCurveHeight: Float = 10f
    var mBackgroundColor: Int = Color.parseColor("#ffffff")
    private var mMaxValue: Float = 120f

    private var mIsAbsoluteTime: Boolean = false
    private var mStartTime: Long? = null
    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.HeartRateChart)
        mGridLineColor = typeArray.getColor(R.styleable.HeartRateChart_hrc_gridLineColor, mGridLineColor)
        mTimeStampTextColor =
            typeArray.getColor(R.styleable.HeartRateChart_hrc_timeStampTextColor, mTimeStampTextColor)
        mTimeStampTextSize = typeArray.getDimension(
            R.styleable.HeartRateChart_hrc_timeStampTextSize,
            ScreenUtil.dip2px(context, 24f).toFloat()
        )
        mTimeStampPaddingTop = typeArray.getDimension(
            R.styleable.HeartRateChart_hrc_timeStampTextPaddingTop,
            ScreenUtil.dip2px(context, 20f).toFloat()
        )
        mLeftBarWidth = typeArray.getDimension(R.styleable.HeartRateChart_hrc_leftBarWidth, 24f)
        mLeftBarTextSize = typeArray.getDimension(R.styleable.HeartRateChart_hrc_leftBarTextSize, 50f)
        mLeftBarTextPadding = typeArray.getDimension(
            R.styleable.HeartRateChart_hrc_leftBarTextPadding,
            ScreenUtil.dip2px(context, 5f).toFloat()
        )
        mCurveLineWidth = typeArray.getDimension(R.styleable.HeartRateChart_hrc_curveLineWidth, 10f)

        mLowHeartRateColor = typeArray.getColor(R.styleable.HeartRateChart_hrc_lowHeartRateColor, mLowHeartRateColor)
        mNeutralHeartRateColor =
            typeArray.getColor(R.styleable.HeartRateChart_hrc_neutralHeartRateColor, mNeutralHeartRateColor)
        mHighHeartRateColor =
            typeArray.getColor(R.styleable.HeartRateChart_hrc_highHeartRateColor, mHighHeartRateColor)
        mBackgroundColor = typeArray.getColor(R.styleable.HeartRateChart_hrc_backgroundColor, mBackgroundColor)
        typeArray?.recycle()
        initPaint()
    }


    var firstEffectiveValueIndex = 0
    var firstEffectiveValue = 0f
    fun setValues(values: List<Double>) {
        this.mHeartRateValues = values
        for (i in 0 until mHeartRateValues.size) {
            if (i < 5) {
                continue
            }
            if (mHeartRateValues[i] != 0.0) {
                firstEffectiveValueIndex = i
                firstEffectiveValue = mHeartRateValues[i].toFloat()
                break
            }
        }
    }

    fun initPaint() {
        mCruvePaint = Paint()
        mCruvePaint.isDither = true
        mCruvePaint.style = Paint.Style.STROKE
        mCruvePaint.isAntiAlias = true
        mCruvePaint.strokeWidth = mCurveLineWidth
        val pathEffect = CornerPathEffect(25f)
        mCruvePaint.pathEffect = pathEffect
        mCruvePaint.color = mLowHeartRateColor


        noDataPaint = Paint()
        noDataPaint.color = Color.parseColor("#E9EBF1")
        noDataPaint.style = Paint.Style.FILL
        noDataPaint.strokeWidth = mCurveLineWidth
        mBarPaint = Paint()
        mBarPaint.style = Paint.Style.FILL
        mBarPaint.strokeWidth = mLeftBarWidth
        mBarTextPaint = Paint()
        mBarTextPaint.color = Color.parseColor("#9aa1a9")
        mBarTextPaint.textSize = mLeftBarTextSize

        mTimestampPaint = Paint()
        mTimestampPaint.textAlign = Paint.Align.CENTER
        mTimestampPaint.color = mTimeStampTextColor
        mTimestampPaint.textSize = mTimeStampTextSize

        mGridPait = Paint()
        mGridPait.color = Color.parseColor("#e9ebf1")
        mGridPait.strokeWidth = 2f
        mTimeStampTextHeight = Math.abs(mTimestampPaint.fontMetrics.top)
        +Math.abs(mTimestampPaint.fontMetrics.bottom)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        mCurveHeight = height.toFloat() - mTimeStampTextHeight - mTimeStampPaddingTop
        drawBackground(canvas)
        drawGridLineAndTimestamp(canvas)
        drawCurve(canvas)
        drawLeftBar(canvas)
    }

    fun drawBackground(canvas: Canvas?) {
        canvas?.drawColor(mBackgroundColor)
    }

    fun drawCurve(canvas: Canvas?) {
        canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mCruvePaint)
        canvas?.save()
        var mSrcBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var mSrcCanvas = Canvas(mSrcBitmap)
        var mDstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var mDstCanvas = Canvas(mDstBitmap)
        mDstCanvas?.translate(mLeftBarWidth / 2, mCurveHeight)
        var scale = mCurveHeight / mMaxValue
        var offset = width * 1f / mHeartRateValues.size
        var path = Path()
        path.moveTo(offset * firstEffectiveValueIndex.toFloat(), -firstEffectiveValue * scale)
        var lastData = 0.0
        for (i in firstEffectiveValueIndex until mHeartRateValues.size) {
            var drawData = 0.0
            if (mHeartRateValues[i] != 0.0) {
                lastData = mHeartRateValues[i]
            }
            drawData = lastData
            path.lineTo(offset * (i).toFloat(), -drawData.toFloat() * scale)
        }
        mDstCanvas?.drawPath(path, mCruvePaint)
        mDstCanvas?.drawLine(
            0f,
            -firstEffectiveValue * scale,
            firstEffectiveValueIndex * offset,
            -firstEffectiveValue * scale,
            noDataPaint
        )
        canvas?.drawBitmap(mDstBitmap, 0f, 0f, mCruvePaint)

        mSrcCanvas.translate(0f, mCurveHeight)
        mCruvePaint.color = mHighHeartRateColor
        var rect1 = Rect(0, -mCurveHeight.toInt(), width, -(mCurveHeight / 4 * 3).toInt())
        var rect2 = Rect(0, -(mCurveHeight / 4 * 3).toInt(), width, -(mCurveHeight / 2).toInt())
        mCruvePaint.style = Paint.Style.FILL
        mCruvePaint.pathEffect = null
//
        mSrcCanvas?.drawRect(rect1, mCruvePaint)

        mCruvePaint.color = mNeutralHeartRateColor
        mSrcCanvas?.drawRect(rect2, mCruvePaint)

        mCruvePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas?.drawBitmap(mSrcBitmap, 0f, 0f, mCruvePaint)
        canvas?.restore()
        canvas?.restoreToCount(canvas.saveCount)
    }

    fun drawLeftBar(canvas: Canvas?) {
        mBarPaint.color = mHighHeartRateColor
        canvas?.drawCircle(mLeftBarWidth / 2, mLeftBarWidth / 2, mLeftBarWidth / 2, mBarPaint)
        canvas?.drawLine(
            mLeftBarWidth / 2,
            mLeftBarWidth / 2,
            mLeftBarWidth / 2,
            mCurveHeight / 4f,
            mBarPaint
        )
        mBarPaint.color = mNeutralHeartRateColor
        canvas?.drawLine(
            mLeftBarWidth / 2,
            mCurveHeight / 4f,
            mLeftBarWidth / 2,
            mCurveHeight / 2f,
            mBarPaint
        )
        mBarPaint.color = mLowHeartRateColor
        canvas?.drawLine(
            mLeftBarWidth / 2,
            mCurveHeight / 2f,
            mLeftBarWidth / 2,
            mCurveHeight - mLeftBarWidth / 2,
            mBarPaint
        )
        canvas?.drawCircle(
            mLeftBarWidth / 2,
            mCurveHeight - mLeftBarWidth / 2,
            mLeftBarWidth / 2,
            mBarPaint
        )
        canvas?.drawText(
            "${(mMaxValue / 4 * 3).toInt()}",
            mLeftBarWidth / 2 + mLeftBarTextPadding,
            mCurveHeight / 4f + Math.abs(mBarTextPaint.fontMetrics.descent),
            mBarTextPaint
        )
        canvas?.drawText(
            "${(mMaxValue / 2).toInt()}",
            mLeftBarWidth / 2 + mLeftBarTextPadding,
            mCurveHeight / 2f + Math.abs(mBarTextPaint.fontMetrics.descent),
            mBarTextPaint
        )
    }

    fun drawGridLineAndTimestamp(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(mLeftBarWidth / 2, 0f)
        if (mHeartRateValues != null && mHeartRateValues.size > 0) {
            mTimeStampLsit.clear()
            var lineOffset = width * 1f / mHeartRateValues.size
            var totalMin = mHeartRateValues.size * 400f / 1000f / 60f
            var minOffset = (totalMin / timestampMaxCount).toInt() + 1
            timestampOffset = 1000 * 60f / 400 * lineOffset * minOffset
            var timestampCount = width / timestampOffset + 1
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
            if(i == 0){
                mTimestampPaint.textAlign = Paint.Align.LEFT
            }else{
                mTimestampPaint.textAlign = Paint.Align.CENTER
            }
            canvas?.drawText(
                mTimeStampLsit[i],
                timestampOffset * i,
                height - mTimeStampTextHeight - mTimeStampPaddingTop + Math.abs(mTimestampPaint.fontMetrics.ascent),
                mTimestampPaint
            )
        }

        canvas?.restore()
    }


    fun setMaxValue(max: Float) {
        this.mMaxValue = max
        invalidate()
    }

    fun setYAxisTextColor(color: Int) {
        this.mBarTextPaint.color = color
        invalidate()
    }

    fun setXAxisTextColor(color: Int) {
        this.mTimeStampTextColor = color
        invalidate()
    }

    fun setGridLineColor(color: Int) {
        this.mGridPait.color = color
        invalidate()
    }

    fun setHeartRateHighLineColor(color: Int) {
        this.mHighHeartRateColor = color
        invalidate()
    }

    fun setHeartRateNormalLineColor(color: Int) {
        this.mNeutralHeartRateColor = color
        invalidate()
    }

    fun setHeartRateLowLineColor(color: Int) {
        this.mLowHeartRateColor = color
        mCruvePaint.color = mLowHeartRateColor
        invalidate()
    }

    fun isAbsoluteTime(flag: Boolean, startTime: Long?) {
        this.mIsAbsoluteTime = flag
        this.mStartTime = startTime
        invalidate()
    }

}