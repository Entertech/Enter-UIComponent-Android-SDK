package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.utils.dp

class ReportFlowLineView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attributeSet, defStyleAttr) {

    private var linePath: Path
    private var linePaint: Paint
    private var mData: MutableList<Double>? = null
    private var leftBarPaint: Paint
    private var limitGridLinePath: Path
    private var scaleLinePath: Path
    private var scaleLinePaint: Paint
    private var gridLineYPadding: Float
    private var gridLinePath: Path
    private var gridLinePaint: Paint

    companion object {
        val SCALE_LINE_WIDTH by lazy { 1f.dp() }
        val SCALE_LINE_HEIGHT by lazy { 5f.dp() }
        val GRID_LINE_WIDTH by lazy { 1.5f.dp() }
        val GRID_LINE_COUNT = 23
        val LEFT_BAR_WIDTH by lazy { 4f.dp() }
        val LINE_VALUE_MAX = 100.0
        val LINE_VALUE_MIN = 0.0
    }

    init {
        gridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridLinePaint.strokeWidth = 4f.dp()
        gridLinePaint.color = Color.parseColor("#DDE1EB")
        val pointPath = Path()
        pointPath.addCircle(0f, 0f, GRID_LINE_WIDTH / 2, Path.Direction.CCW)
        gridLinePaint.pathEffect =
            PathDashPathEffect(pointPath, 2.5f.dp(), 0f, PathDashPathEffect.Style.ROTATE)
        gridLinePath = Path()
        gridLineYPadding = 5f.dp()

        limitGridLinePath = Path()

        scaleLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scaleLinePaint.color = Color.parseColor("#DDE1EB")
        scaleLinePaint.strokeWidth = 1f.dp()
        scaleLinePaint.style = Paint.Style.STROKE
        scaleLinePath = Path()

        leftBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        leftBarPaint.strokeWidth = LEFT_BAR_WIDTH
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        leftBarPaint.color = Color.parseColor("#4B5DCC")

        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.color = Color.parseColor("#7684DA")
        val pathEffect = CornerPathEffect(25f)
        linePaint.pathEffect = pathEffect
        linePaint.strokeWidth = 1.5f.dp()
        linePaint.style = Paint.Style.STROKE
        linePath = Path()
        mData = ArrayList<Double>()
        for (i in 0..100) {
            val randomValue = java.util.Random().nextDouble() * 100
            mData!!.add(randomValue)
        }

    }

    override fun onDraw(canvas: Canvas) {
        onDrawGridLine(canvas)
        onDrawScaleLine(canvas)
        onDrawLeftYBar(canvas)
        onDrawLine(canvas)
    }

    private fun onDrawGridLine(canvas: Canvas) {
        val yOffset = (height - 2 * gridLineYPadding) / (GRID_LINE_COUNT - 1)
        for (i in 0 until GRID_LINE_COUNT) {
            gridLinePath.moveTo(
                LEFT_BAR_WIDTH,
                GRID_LINE_WIDTH / 2 + gridLineYPadding + i * yOffset
            )
            gridLinePath.lineTo(
                width.toFloat(),
                GRID_LINE_WIDTH / 2 + gridLineYPadding + i * yOffset
            )
        }
        gridLinePaint.color = Color.parseColor("#DDE1EB")
        canvas.drawPath(gridLinePath, gridLinePaint)
        for (i in 0 until GRID_LINE_COUNT) {
            if (i == 7 || i == 15) {
                limitGridLinePath.reset()
                limitGridLinePath.moveTo(
                    LEFT_BAR_WIDTH,
                    gridLineYPadding + GRID_LINE_WIDTH / 2 + i * yOffset
                )
                limitGridLinePath.lineTo(
                    width.toFloat(),
                    gridLineYPadding + GRID_LINE_WIDTH / 2 + i * yOffset
                )
                var lineColor =
                    if (i == 7) {
                        Color.parseColor("#CC9E59")
                    } else {
                        Color.parseColor("#4B5DCC")
                    }
                gridLinePaint.color = lineColor
                canvas.drawPath(limitGridLinePath, gridLinePaint)
            }
        }
    }

    private fun onDrawScaleLine(canvas: Canvas) {
        val offset = width / 8
        for (i in 0..8) {
            scaleLinePath.moveTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_WIDTH)
            scaleLinePath.lineTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_HEIGHT)
        }
        scaleLinePath.moveTo(0F, height - SCALE_LINE_WIDTH / 2)
        scaleLinePath.lineTo(width.toFloat(), height - SCALE_LINE_WIDTH / 2)
        canvas.drawPath(scaleLinePath, scaleLinePaint)
    }

    private fun onDrawLeftYBar(canvas: Canvas) {
        //top
        leftBarPaint.color = Color.parseColor("#FFC56F")
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2,
            LEFT_BAR_WIDTH,
            LEFT_BAR_WIDTH / 2,
            height.toFloat() / 2,
            leftBarPaint
        )

        //bottom
        leftBarPaint.color = Color.parseColor("#4B5DCC")
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2,
            height.toFloat() / 2,
            LEFT_BAR_WIDTH / 2,
            height.toFloat() - LEFT_BAR_WIDTH / 2,
            leftBarPaint
        )

        //mid
        leftBarPaint.color = Color.parseColor("#99A7FF")
        leftBarPaint.strokeCap = Paint.Cap.SQUARE
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2,
            height.toFloat() / 3,
            LEFT_BAR_WIDTH / 2,
            height.toFloat() * 2f / 3f,
            leftBarPaint
        )
    }

    private fun onDrawLine(canvas: Canvas) {
        if (mData.isNullOrEmpty() || mData!!.size < 2) {
            return
        }
        var dataMax = mData!!.max()
        var dataMin = mData!!.min()
        if (dataMax == dataMin) {
            dataMax = LINE_VALUE_MAX
            dataMin = LINE_VALUE_MIN
        }
        val dataScale = (height - 2 * gridLineYPadding) / (dataMax!! - dataMin!!)
        val dataOffset = (width - LEFT_BAR_WIDTH) / (mData!!.size - 1)
        for (i in mData!!.indices) {
            val curX = dataOffset * i + LEFT_BAR_WIDTH
            val curY = (mData!![i] - dataMin) * dataScale
            if (i == 0) {
                linePath.moveTo(curX, curY.toFloat())
            } else {
                linePath.lineTo(curX, curY.toFloat())
            }
        }
        canvas.drawPath(linePath, linePaint)
    }

    fun setData(data: MutableList<Double>) {
        this.mData = data
        invalidate()
    }
}