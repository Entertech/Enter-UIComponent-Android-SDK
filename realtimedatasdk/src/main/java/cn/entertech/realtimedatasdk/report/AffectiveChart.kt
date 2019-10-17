package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import android.graphics.Shader
import android.graphics.LinearGradient
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil
import cn.entertech.realtimedatasdk.utils.getChartAbsoluteTime

class AffectiveChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private var mIsAbsoluteTime: Boolean = false
    private var mStartTime: Long? = null
    lateinit var mCruvePaint: Paint
    lateinit var mBarPaint: Paint
    lateinit var mBarTextPaint: Paint
    lateinit var noDataPaint: Paint
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
    var mHightHeartRateColor: Int = Color.parseColor("#ff6682")
    var mLeftBarTextSize: Float = 50f
    var mLeftBarTextPadding: Float = 10f

    var mBackgroundColor: Int = Color.parseColor("#ffffff")
    private var mMaxValue: Float = 100f
    var mCurveHeight: Float = 10f
    var mColors: IntArray =
        intArrayOf(Color.parseColor("#c7ffe4"), Color.parseColor("#5fc695"), Color.parseColor("#52a27c"))


    var DATA_SAMPLE_COUNT = 4
    var TIME_OF_TWO_POINT = 800f * DATA_SAMPLE_COUNT

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
        mHightHeartRateColor =
            typeArray.getColor(
                R.styleable.HeartRateChart_hrc_highHeartRateColor,
                mHightHeartRateColor
            )

        mBackgroundColor = typeArray.getColor(R.styleable.HeartRateChart_hrc_backgroundColor, mBackgroundColor)
        typeArray?.recycle()
        initPaint()
    }

    var firstEffectiveValueIndex = 0
    var firstEffectiveValue = 0f
    fun setValues(values: List<Double>) {
        this.mHeartRateValues = sampleValues(values)
        for (i in 0 until mHeartRateValues.size) {
            if (mHeartRateValues[i] != 0.0) {
                firstEffectiveValueIndex = i
                firstEffectiveValue = mHeartRateValues[i].toFloat()
                break
            }
        }
    }

    fun sampleValues(values: List<Double>): List<Double> {
        var newValues = ArrayList<Double>()
        for (i in 0 until values.size) {
            if (i % DATA_SAMPLE_COUNT == 0) {
                newValues.add(values[i])
            }
        }
        return newValues
    }

    fun setSample(sample: Int) {
        this.DATA_SAMPLE_COUNT = sample
    }


    fun initPaint() {
        mCruvePaint = Paint()
        mCruvePaint.isDither = true
        mCruvePaint.style = Paint.Style.FILL
        mCruvePaint.isAntiAlias = true
        mCruvePaint.strokeWidth = mCurveLineWidth
        val pathEffect = CornerPathEffect(15f)
        mCruvePaint.pathEffect = pathEffect
        mCruvePaint.color = mHightHeartRateColor
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

        noDataPaint = Paint()
        noDataPaint.color = Color.parseColor("#E9EBF1")
        noDataPaint.style = Paint.Style.FILL
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

    fun setColors(intArray: IntArray) {
        this.mColors = intArray
        invalidate()
    }

    fun drawBackground(canvas: Canvas?) {
        canvas?.drawColor(mBackgroundColor)
    }


    fun drawCurve(canvas: Canvas?) {
        mCruvePaint.shader = LinearGradient(0f, 0f, 0f, mCurveHeight, mColors, null, Shader.TileMode.MIRROR)
        canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mCruvePaint)
        canvas?.save()
        var mDstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var mDstCanvas = Canvas(mDstBitmap)
        mDstCanvas?.translate(mLeftBarWidth / 2, mCurveHeight)
        var scale = mCurveHeight / mMaxValue
        var offset = width * 1f / mHeartRateValues.size
        var path = Path()
        path.moveTo(0f, 0f)
        var lastValue = 0.0
        noDataPaint.strokeWidth = offset
        for (i in 0 until mHeartRateValues.size) {
            path.lineTo(offset * i, -mHeartRateValues[i].toFloat() * scale)
            if (i > firstEffectiveValueIndex) {
                if (mHeartRateValues[i] != 0.0) {
                    lastValue = mHeartRateValues[i]
                    continue
                } else {
                    mDstCanvas?.drawLine(
                        i * offset + offset / 2,
                        0f,
                        i * offset + offset / 2,
                        -lastValue.toFloat() * scale,
                        noDataPaint
                    )
                }
            }
        }
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(0f, 0f)
        path.close()
        mDstCanvas?.drawPath(path, mCruvePaint)
        for (i in 0 until firstEffectiveValueIndex) {
            mDstCanvas?.drawLine(
                i * offset + offset / 2,
                0f,
                i * offset + offset / 2,
                -firstEffectiveValue * scale,
                noDataPaint
            )
        }
        canvas?.drawBitmap(mDstBitmap, 0f, 0f, mCruvePaint)
        canvas?.restore()
        canvas?.restoreToCount(canvas.saveCount)
    }

    fun drawLeftBar(canvas: Canvas?) {
        canvas?.drawText(
            "${(mMaxValue).toInt()}",
            mLeftBarTextPadding,
            Math.abs(mBarTextPaint.fontMetrics.ascent),
            mBarTextPaint
        )
        canvas?.drawText(
            "${(mMaxValue * 3 / 4).toInt()}",
            mLeftBarTextPadding,
            mCurveHeight / 4f + Math.abs(mBarTextPaint.fontMetrics.ascent),
            mBarTextPaint
        )
        canvas?.drawText(
            "${(mMaxValue / 2).toInt()}",
            mLeftBarTextPadding,
            mCurveHeight / 2f + Math.abs(mBarTextPaint.fontMetrics.ascent),
            mBarTextPaint
        )
        canvas?.drawText(
            "${(mMaxValue / 4).toInt()}",
            mLeftBarTextPadding,
            mCurveHeight / 4 * 3f + Math.abs(mBarTextPaint.fontMetrics.ascent),
            mBarTextPaint
        )
    }

    fun drawGridLineAndTimestamp(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(mLeftBarWidth / 2, 0f)
        if (mHeartRateValues != null && mHeartRateValues.size > 0) {
            mTimeStampLsit.clear()
            var lineOffset = width * 1f / mHeartRateValues.size
            var totalMin = mHeartRateValues.size * TIME_OF_TWO_POINT / 1000f / 60f
            var minOffset = (totalMin / timestampMaxCount).toInt() + 1
            timestampOffset = 1000 * 60f / TIME_OF_TWO_POINT * lineOffset * minOffset
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
            if (i == 0) {
                mTimestampPaint.textAlign = Paint.Align.LEFT
            } else if (i == mTimeStampLsit.size - 1) {
                mTimestampPaint.textAlign = Paint.Align.RIGHT
            } else {
                mTimestampPaint.textAlign = Paint.Align.CENTER
            }
            canvas?.drawLine(
                timestampOffset * i,
                0f,
                timestampOffset * i,
                mCurveHeight,
                mGridPait
            )
            canvas?.drawText(
                mTimeStampLsit[i],
                timestampOffset * i,
                mCurveHeight + Math.abs(mTimestampPaint.fontMetrics.ascent),
                mTimestampPaint
            )
        }

        canvas?.restore()
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

    fun setMaxValue(max: Float) {
        this.mMaxValue = max
        invalidate()
    }

    fun isAbsoluteTime(flag: Boolean, startTime: Long?) {
        this.mIsAbsoluteTime = flag
        this.mStartTime = startTime
        invalidate()
    }
}