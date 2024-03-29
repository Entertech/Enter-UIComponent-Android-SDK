package cn.entertech.uicomponentsdk.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.Math.abs

class CustomYAxisRenderer(
    viewPortHandler: ViewPortHandler?,
    yAxis: YAxis?,
    trans: Transformer?
) : YAxisRenderer(viewPortHandler, yAxis, trans) {
    private var mLimitLineLabelBgPaint: Paint
    var mLimitLabelBgColor = Color.parseColor("#ffffff")

    init {
        mLimitLineLabelBgPaint = Paint()
        mLimitLineLabelBgPaint.color = getOpacityColor(mLimitLabelBgColor, 0.8F)
        mLimitLineLabelBgPaint.style = Paint.Style.FILL
        mLimitLineLabelBgPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun renderLimitLines(c: Canvas) {
        val limitLines = mYAxis.limitLines
        if (limitLines == null || limitLines.size <= 0) return
        val pts = mRenderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f
        val limitLinePath = mRenderLimitLines
        limitLinePath.reset()
        for (i in limitLines.indices) {
            val l = limitLines[i]
            if (!l.isEnabled) continue
            val clipRestoreCount = c.save()
//            mLimitLineClippingRect.set(mViewPortHandler.contentRect)
            mLimitLineClippingRect.top = mViewPortHandler.contentTop()
            mLimitLineClippingRect.left =
                mViewPortHandler.contentLeft() - Utils.convertDpToPixel(30f)
            mLimitLineClippingRect.right = mViewPortHandler.contentRight()
            mLimitLineClippingRect.bottom =
                mViewPortHandler.contentBottom() + Utils.convertDpToPixel(10f)
            mLimitLineClippingRect.inset(0f, -l.lineWidth)
            c.clipRect(mLimitLineClippingRect)
            mLimitLinePaint.style = Paint.Style.STROKE
            mLimitLinePaint.color = l.lineColor
            mLimitLinePaint.strokeWidth = l.lineWidth
            mLimitLinePaint.pathEffect = l.dashPathEffect
            pts[1] = l.limit
            mTrans.pointValuesToPixel(pts)
            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1])
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1])
            c.drawPath(limitLinePath, mLimitLinePaint)
            limitLinePath.reset()
            // c.drawLines(pts, mLimitLinePaint);
            val label = l.label

            // if drawing the limit-value label is enabled
            if (label != null && label != "") {
                mLimitLinePaint.style = l.textStyle
                mLimitLinePaint.pathEffect = null
                mLimitLinePaint.color = l.textColor
                mLimitLinePaint.typeface = l.typeface
                mLimitLinePaint.strokeWidth = 0.5f
                mLimitLinePaint.textSize = l.textSize
                val labelLineHeight =
                    Utils.calcTextHeight(mLimitLinePaint, label)
                        .toFloat()
                val xOffset =
                    Utils.convertDpToPixel(4f) + l.xOffset
                val yOffset = l.lineWidth + labelLineHeight + l.yOffset
                val position = l.labelPosition
                if (position == LimitLabelPosition.RIGHT_TOP) {
                    var yBaseLine = pts[1] - yOffset + labelLineHeight
                    var top = abs(mLimitLinePaint.fontMetrics.top)
                    var bottom = abs(mLimitLinePaint.fontMetrics.bottom)
                    var heightCenter = (yBaseLine - top + bottom + yBaseLine) / 2
                    if (yBaseLine - top < mViewPortHandler.contentTop()) {
                        heightCenter += mViewPortHandler.contentTop() - (yBaseLine - top)
                    }
                    if (yBaseLine + bottom > mViewPortHandler.contentBottom()) {
                        heightCenter -= (yBaseLine + bottom - mViewPortHandler.contentBottom())
                    }
                    yBaseLine = (heightCenter * 2 + top - bottom) / 2
                    var textWidth = Utils.calcTextWidth(mLimitLinePaint, label)
                    mLimitLineLabelBgPaint.strokeWidth = labelLineHeight + 22
                    mLimitLineLabelBgPaint.color = getOpacityColor(mLimitLabelBgColor, 0.6F)
                    c.drawLine(
                        mViewPortHandler.contentRight() - xOffset,
                        heightCenter,
                        mViewPortHandler.contentRight() - xOffset - textWidth,
                        heightCenter,
                        mLimitLineLabelBgPaint
                    )
                    mLimitLinePaint.textAlign = Align.RIGHT
                    c.drawText(
                        label,
                        mViewPortHandler.contentRight() - xOffset,
                        yBaseLine, mLimitLinePaint
                    )
                } else if (position == LimitLabelPosition.RIGHT_BOTTOM) {
                    mLimitLinePaint.textAlign = Align.RIGHT
                    c.drawText(
                        label,
                        mViewPortHandler.contentRight() - xOffset,
                        pts[1] + yOffset, mLimitLinePaint
                    )
                } else if (position == LimitLabelPosition.LEFT_TOP) {
                    mLimitLinePaint.textAlign = Align.CENTER
                    c.drawText(
                        label,
                        mViewPortHandler.contentLeft() + xOffset,
                        pts[1] - yOffset + labelLineHeight, mLimitLinePaint
                    )
                } else {
                    mLimitLinePaint.textAlign = Align.LEFT
                    c.drawText(
                        label,
                        mViewPortHandler.offsetLeft() + xOffset,
                        pts[1] + yOffset, mLimitLinePaint
                    )
                }
            }
            c.restoreToCount(clipRestoreCount)
        }
    }

//    override fun drawYLabels(
//        c: Canvas,
//        fixedPosition: Float,
//        positions: FloatArray,
//        offset: Float
//    ) {
//        val from = if (mYAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
//        val to =
//            if (mYAxis.isDrawTopYLabelEntryEnabled) mYAxis.mEntryCount else mYAxis.mEntryCount - 1
//
//        // draw
//        for (i in from until to) {
//            val text = mYAxis.getFormattedLabel(i)
//            c.drawText(text, fixedPosition, positions[i * 2 + 1] - offset*1.5f, mAxisLabelPaint)
//        }
//    }


    override fun renderGridLines(c: Canvas) {
        if (!mYAxis.isEnabled) return
        if (mYAxis.isDrawGridLinesEnabled) {
            val clipRestoreCount = c.save()
            c.clipRect(gridClippingRect)
            val positions = transformedPositions
            mGridPaint.color = mYAxis.gridColor
            mGridPaint.strokeWidth = mYAxis.gridLineWidth
            mGridPaint.pathEffect = mYAxis.gridDashPathEffect
            val gridLinePath = mRenderGridLinesPath
            gridLinePath.reset()

            // draw the grid
            var i = 0
            while (i < positions.size) {
                if (i != 0) {
                    // draw a path because lines don't support dashing on lower android versions
                    c.drawPath(linePath(gridLinePath, i, positions), mGridPaint)
                    gridLinePath.reset()
                }
                i += 2
            }
            c.restoreToCount(clipRestoreCount)
        }
        if (mYAxis.isDrawZeroLineEnabled) {
            drawZeroLine(c)
        }
    }


    override fun computeAxisValues(min: Float, max: Float) {
    }

}