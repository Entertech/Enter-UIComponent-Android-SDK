package cn.entertech.uicomponentsdk.utils

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {
    private var mRadius = 5f
    fun setRadius(radius: Float) {
        this.mRadius = radius
    }

    private val mBarShadowRectBuffer = RectF()
    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.setColor(dataSet.barBorderColor)
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.barBorderWidth))

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX: Float = mAnimator.getPhaseX()
        val phaseY: Float = mAnimator.getPhaseY()

        // draw the bar shadow before the values

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.barShadowColor)
            val barData: BarData = mChart.getBarData()
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = Math.min(
                Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt(),
                dataSet.entryCount
            )
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf
                trans.rectValueToPixel(mBarShadowRectBuffer)
                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break
                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                if (mRadius > 0) {
                    c.drawRoundRect(mBarShadowRectBuffer, mRadius, mRadius, mShadowPaint)
                } else {
                    c.drawRect(mBarShadowRectBuffer, mShadowPaint)
                }
                i++
            }
        }

        // initialize the buffer

        // initialize the buffer
        val buffer: BarBuffer = mBarBuffers.get(index)
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.getBarData().getBarWidth())

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.color)
        }


        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4))
            }
            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                mRenderPaint.setShader(
                    LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.MIRROR
                    )
                )
            }
            if (dataSet.gradientColors != null) {
                mRenderPaint.setShader(
                    LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.MIRROR
                    )
                )
            }
            if (mRadius > 0) {
                c.drawRoundRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRadius,mRadius,mRenderPaint
                )
            } else {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint
                )
            }
            if (drawBorder) {
                if (mRadius>0){
                    c.drawRoundRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3],mRadius,mRadius, mBarBorderPaint
                    )
                }else{
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint
                    )
                }
            }
            j += 4
        }


    }

}