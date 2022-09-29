package cn.entertech.uicomponentsdk.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.report.ReportAverageChartCard
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.TimeUtils
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import java.text.DecimalFormat
import kotlin.math.ceil

class AverageBarChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {
    private var mLevels: List<ReportAverageChartCard.Level>? = null
    private var mShowLevelOnly: Boolean = false
    private var mIsAverageInt: Boolean = false
    private var isValueFloat: Boolean = false
    private lateinit var mAverageValueTextPaint: Paint
    private var mUnit: String? = ""
    private var mIsShowUnit: Boolean = false
    private lateinit var mAverageTextPaint: Paint
    private var barWidth: Float = 0f
    private var scaleHeight: Float = 0f
    private lateinit var mAverageLinePaint: Paint
    private var mValues = listOf<Float>()
    private var mTransferValues = ArrayList<Int>()
    private lateinit var mBarPaint: Paint
    private var legendTextWidth: Int = 0
    private lateinit var mLegendTextPaint: Paint
    private var mAverageLineWidth: Float = ScreenUtil.dip2px(context, 4f).toFloat()
    private var mBarHighLightColor: Int = Color.parseColor("#FF6682")
    private var mBarValueBgColor: Int = Color.parseColor("#FFB2C0")
    private var mBarColor: Int = Color.parseColor("#FFC56F")
    private var mPrimaryTextColor: Int = Color.BLACK
    private var mSecondTextColor: Int = Color.parseColor("#666666")
    private var mAverageLineColor: Int = Color.parseColor("#FB9C98")
    private var mBackground: Int? = Color.WHITE

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.AverageBarChart)
        mBackground = typeArray.getColor(R.styleable.AverageBarChart_abc_background, Color.WHITE)
        mBarHighLightColor = typeArray.getColor(
            R.styleable.AverageBarChart_abc_barHighLightColor,
            mBarHighLightColor
        )
        mBarColor = typeArray.getColor(R.styleable.AverageBarChart_abc_barColor, mBarColor)
        mPrimaryTextColor =
            typeArray.getColor(R.styleable.AverageBarChart_abc_primaryTextColor, mPrimaryTextColor)
        mSecondTextColor =
            typeArray.getColor(R.styleable.AverageBarChart_abc_secondTextColor, mSecondTextColor)
        mAverageLineColor =
            typeArray.getColor(R.styleable.AverageBarChart_abc_averageLineColor, mAverageLineColor)
        mAverageLineWidth = typeArray.getDimension(
            R.styleable.AverageBarChart_abc_averageLineWidth,
            mAverageLineWidth
        )
        mBarValueBgColor =
            typeArray.getColor(R.styleable.AverageBarChart_abc_barValueBgColor, mBarValueBgColor)
        mIsShowUnit = typeArray.getBoolean(R.styleable.AverageBarChart_abc_isShowUnit, false)
        mUnit = typeArray.getString(R.styleable.AverageBarChart_abc_unit)
        typeArray.recycle()

        setValues(mValues)
        initPaint()
    }

    private fun initPaint() {
        this.setLayerType(LAYER_TYPE_SOFTWARE, null)
        mLegendTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLegendTextPaint.color = mSecondTextColor
        mLegendTextPaint.textSize = ScreenUtil.dip2px(context, 12f).toFloat()

        mAverageTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mAverageValueTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mAverageValueTextPaint.textSize = ScreenUtil.dip2px(context, 24f).toFloat()
        mAverageValueTextPaint.color = mPrimaryTextColor
        mBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBarPaint.color = mBarColor
        mBarPaint.style = Paint.Style.FILL
        mBarPaint.pathEffect = CornerPathEffect(15f)
        mBarPaint.textSize = ScreenUtil.dip2px(context, 10f).toFloat()
        mBarPaint.textAlign = Paint.Align.CENTER

        mAverageLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mAverageLinePaint.color = mAverageLineColor
        mAverageLinePaint.strokeWidth = mAverageLineWidth
        mAverageLinePaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        onDrawBackground(canvas)
        onDrawLegendText(canvas)
        onDrawBars(canvas)
    }


    private fun onDrawBackground(canvas: Canvas?) {
        canvas?.drawColor(mBackground!!)
    }

    private fun onDrawLegendText(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(0f, height.toFloat())
        var legend = context.getString(R.string.last_7_time)
        var textBound = Rect()
        mLegendTextPaint.getTextBounds(legend, 0, legend.length, textBound)
        legendTextWidth = textBound.width()
        canvas?.drawText(legend, 0f, -mLegendTextPaint.fontMetrics.descent, mLegendTextPaint)
        canvas?.restore()
    }

    private fun onDrawBars(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(0f, height.toFloat())
        barWidth = (width - legendTextWidth) / 15f
        mBarPaint.strokeWidth = barWidth
        scaleHeight = (height - barWidth - ScreenUtil.dip2px(context, 4f)) / 128f
        for (i in mTransferValues.indices) {
            if (i == mTransferValues.size - 1) {
                mBarPaint.color = mBarHighLightColor
            } else {
                mBarPaint.color = mBarColor
            }
            var barHeight = mTransferValues[i] * scaleHeight
            var barRect = Rect(
                (legendTextWidth + barWidth * (2 * i + 1.5)).toInt(),
                -barHeight.toInt(),
                (legendTextWidth + (2 * i + 2.5) * barWidth).toInt(),
                0
            )
            canvas?.drawRect(barRect, mBarPaint)


            if (i == mTransferValues.size - 1) {
                onDrawAverageLine(canvas)
                onDrawValue(canvas, i)
            }
        }
        canvas?.restore()
    }

    private fun onDrawValue(canvas: Canvas?, i: Int) {
        var barHeight = mTransferValues[i] * scaleHeight
        var valueRect = Rect(
            (legendTextWidth + barWidth * (2 * i + 1.5)).toInt(),
            -barHeight.toInt() - ScreenUtil.dip2px(context, 4f),
            (legendTextWidth + (2 * i + 2.5) * barWidth).toInt(),
            (-barHeight.toInt() - ScreenUtil.dip2px(context, 4f) - barWidth).toInt()
        )
        mBarPaint.color = mBarValueBgColor
        canvas?.drawRect(valueRect, mBarPaint)
        mBarPaint.color = mPrimaryTextColor
        var lastValue: Number =
            if (isValueFloat) {
                var decimalFormat = DecimalFormat(".0")
                var average = decimalFormat.format(mValues[i])
                java.lang.Float.parseFloat(average)
            } else {
                mValues[i].toInt()
            }
        var lastValueTextBound = Rect()
        mBarPaint.getTextBounds(
            "$lastValue",
            0,
            "$lastValue".length - 1,
            lastValueTextBound
        )
        if (lastValueTextBound.width() >= 30) {
            mBarPaint.textSize = ScreenUtil.dip2px(context, 8f).toFloat()
        } else {
            mBarPaint.textSize = ScreenUtil.dip2px(context, 10f).toFloat()
        }
        var lastValueDescent = Math.abs(mBarPaint.fontMetrics.descent)
        var offset =
            (Math.abs(mBarPaint.fontMetrics.ascent) + Math.abs(mBarPaint.fontMetrics.descent)) / 2f - lastValueDescent
        var offsetDescent = (-barHeight - ScreenUtil.dip2px(
            context,
            4f
        ) - barWidth * 0.5 + offset).toFloat()
        canvas?.drawText(
            "$lastValue",
            (legendTextWidth + barWidth * (2 * i + 2f)),
            offsetDescent,
            mBarPaint
        )
    }

    fun setValues(values: List<Float>) {
        if (values.isEmpty()) {
            return
        }
        mValues = values
        transferValues(values)
        invalidate()
    }

    fun setIsValueFloat(isValueFloat: Boolean) {
        this.isValueFloat = isValueFloat
        invalidate()
    }

    private fun transferValues(values: List<Float>) {
        mTransferValues.clear()
        var max = values.maxOrNull()
        var min = values.minOrNull()
        if (max == min) {
            mTransferValues.addAll(values.map { 64 })
        } else {
            for (value in values) {
                if (value == min) {
                    mTransferValues.add(28)
                } else if (value == max) {
                    mTransferValues.add(128)
                } else {
                    var transferValue = 28 + ((value - min!!) * 1f / (max!! - min) * 100).toInt()
                    mTransferValues.add(transferValue)
                }
            }
        }
    }

    private fun onDrawAverageLine(canvas: Canvas?) {
        var transferAverage = mTransferValues.average().toFloat()
        var average = mValues.average()
        if (transferAverage < 46) {
            transferAverage = 46f
        }
        if (transferAverage >= 128) {
            transferAverage = 114f
        }
        canvas?.drawLine(
            0f,
            -transferAverage * scaleHeight,
            width.toFloat(),
            -transferAverage * scaleHeight,
            mAverageLinePaint
        )

        mAverageTextPaint.color = mSecondTextColor
        mAverageTextPaint.textSize = ScreenUtil.dip2px(context, 16f).toFloat()
        canvas?.drawText(
            "${context.getString(R.string.sdk_report_average_2)}",
            0f,
            -transferAverage * scaleHeight - ScreenUtil.dip2px(context, 12f),
            mAverageTextPaint
        )

        var averageRect = Rect()
        var averageFormatString = String.format("%.1f", average)
        if (mIsAverageInt) {
            averageFormatString = "${ceil(average).toInt()}"
        }
        if (mUnit != null && mUnit == "second") {
            averageFormatString = TimeUtils.second2FormatString(context, average.toInt())
        }
        mAverageValueTextPaint.getTextBounds(
            "$averageFormatString",
            0,
            "$averageFormatString".length,
            averageRect
        )
        var averageValueTextHeight = averageRect.height()
        var averageValueTextWidth = averageRect.width()
        if (mShowLevelOnly) {
            val levelText = calLevel(average)
            //drawBigLevel
            canvas?.drawText(
                levelText,
                0f,
                -transferAverage * scaleHeight + averageValueTextHeight + ScreenUtil.dip2px(
                    context,
                    10f
                ),
                mAverageValueTextPaint
            )
        } else {
            canvas?.drawText(
                averageFormatString,
                0f,
                -transferAverage * scaleHeight + averageValueTextHeight + ScreenUtil.dip2px(
                    context,
                    10f
                ),
                mAverageValueTextPaint
            )
            if (mUnit != null && mUnit != "" && mUnit != "second") {
                canvas?.drawText(
                    mUnit!!,
                    averageValueTextWidth + 8f,
                    -transferAverage * scaleHeight + averageValueTextHeight + ScreenUtil.dip2px(
                        context,
                        10f
                    ),
                    mAverageTextPaint
                )
            } else {
                val levelText = calLevel(average)
                canvas?.drawText(
                    "(${levelText})",
                    averageValueTextWidth + 8f,
                    -transferAverage * scaleHeight + averageValueTextHeight + ScreenUtil.dip2px(
                        context,
                        10f
                    ),
                    mAverageTextPaint
                )
            }
        }
    }

    fun calLevel(average: Double): String {
        var levelText = ""
        var rate = average / 100f
        if (mLevels.isNullOrEmpty()) {
            levelText = ""
        } else {
            var sum = 0f
            for (i in mLevels!!.indices) {
                sum += mLevels!![i].percentage
                if (i == 0 && rate < sum) {
                    levelText = mLevels!![i].levelText
                } else {
                    if (rate < sum && rate >= (sum - mLevels!![i].percentage)) {
                        levelText = mLevels!![i].levelText
                    }
                }
            }
        }
        return levelText
    }


    fun setBarColor(color: Int) {
        this.mBarColor = color
        invalidate()
    }

    fun setBarHighLightColor(color: Int) {
        this.mBarHighLightColor = color
        invalidate()
    }

    fun setBarValueBgColor(color: Int) {
        this.mBarValueBgColor = color
        invalidate()
    }

    fun setUnit(unit: String?) {
        this.mUnit = unit
        invalidate()
    }

    fun setBgColor(bgColor: Int?) {
        this.mBackground = bgColor
        invalidate()
    }

    fun setPrimaryTextColor(color: Int) {
        this.mPrimaryTextColor = color
        initPaint()
        invalidate()
    }

    fun setSecondTextColor(color: Int) {
        this.mSecondTextColor = color
        initPaint()
        invalidate()
    }

    fun setAverageInt(flag: Boolean) {
        this.mIsAverageInt = flag
        invalidate()
    }

    fun setShowLevelOnly(showLevelOnly: Boolean) {
        this.mShowLevelOnly = showLevelOnly
        invalidate()
    }

    fun setLevels(levels: List<ReportAverageChartCard.Level>?) {
        this.mLevels = levels
        invalidate()
    }
}