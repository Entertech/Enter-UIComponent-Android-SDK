package cn.entertech.realtimedatasdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.realtimedatasdk.R
import cn.entertech.realtimedatasdk.utils.ScreenUtil
import java.util.*

class HeartRateVariabilityChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

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
    private var mMaxValue: Float = 150f
    var mCurveHeight: Float = 10f

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
            typeArray.getColor(R.styleable.HeartRateChart_hrc_highHeartRateColor, mHightHeartRateColor)
        mBackgroundColor = typeArray.getColor(R.styleable.HeartRateChart_hrc_backgroundColor, mBackgroundColor)

        initPaint()
    }

    var firstEffectiveValueIndex = 0
    var firstEffectiveValue = 0f
    fun setValues(values: List<Double>) {
        this.mHeartRateValues = values
        for (i in 0 until mHeartRateValues.size) {
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
        noDataPaint.strokeWidth = mCurveLineWidth
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
        var mDstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var mDstCanvas = Canvas(mDstBitmap)
        mDstCanvas?.translate(mLeftBarWidth / 2, mCurveHeight)
        var scale = mCurveHeight / mMaxValue
        var offset = width * 1f / mHeartRateValues.size
        var path = Path()
        path.moveTo(firstEffectiveValueIndex * offset, -firstEffectiveValue * scale)
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
            "${(mMaxValue * 2 / 3).toInt()}",
            mLeftBarTextPadding,
            mCurveHeight / 3f + Math.abs(mBarTextPaint.fontMetrics.ascent),
            mBarTextPaint
        )
        canvas?.drawText(
            "${(mMaxValue / 3).toInt()}",
            mLeftBarTextPadding,
            mCurveHeight * 2 / 3f + Math.abs(mBarTextPaint.fontMetrics.ascent),
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
        }
        for (i in 0 until mTimeStampLsit.size) {
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


    fun setMaxValue(max: Float) {
        this.mMaxValue = max
        invalidate()
    }
}