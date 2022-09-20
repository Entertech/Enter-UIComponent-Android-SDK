package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.dp

class ReportJournalCoherenceLineView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attributeSet, defStyleAttr) {

    private var flags: MutableList<Int>? = null
    private var lineBgColor: Int = Color.parseColor("#DDE1EB")
    private var limitAboveColor: Int = Color.parseColor("#CC9E59")
    private var limitBottomColor: Int = Color.parseColor("#4B5DCC")
    private var lineWidth: Float = LINE_WIDTH
    private var gridColor: Int = Color.parseColor("#DDE1EB")
    private var lineColor: Int = Color.parseColor("#5FC695")
    private lateinit var linePath: Path
    private lateinit var linePaint: Paint
    private var mData: MutableList<Double>? = null
    private lateinit var leftBarPaint: Paint
    private lateinit var limitGridLinePath: Path
    private lateinit var scaleLinePath: Path
    private lateinit var scaleLinePaint: Paint
    private var gridLineYPadding: Float = 0.0f
    private lateinit var gridLinePath: Path
    private lateinit var gridLinePaint: Paint

    companion object {
        val SCALE_LINE_WIDTH by lazy { 1f.dp() }
        val SCALE_LINE_HEIGHT by lazy { 5f.dp() }
        val GRID_LINE_WIDTH by lazy { 1.5f.dp() }
        val GRID_LINE_COUNT = 23
        val LEFT_BAR_WIDTH by lazy { 4f.dp() }
        val LINE_VALUE_MAX = 100.0
        val LINE_VALUE_MIN = 0.0
        val LINE_WIDTH by lazy { 1.5f.dp() }
    }

    init {
        val typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportJournalCoherenceLineView
        )
        gridColor = typeArray.getColor(
            R.styleable.ReportJournalCoherenceLineView_rgclv_gridColor,
            gridColor
        )
        lineColor = typeArray.getColor(
            R.styleable.ReportJournalCoherenceLineView_rgclv_lineColor,
            lineColor
        )
        lineBgColor = typeArray.getColor(
            R.styleable.ReportJournalCoherenceLineView_rgclv_lineBgColor,
            lineBgColor
        )
        lineWidth = typeArray.getDimension(
            R.styleable.ReportJournalCoherenceLineView_rgclv_lineWidth,
            lineWidth
        )
        initPaint()
    }

    fun initPaint() {
        gridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridLinePaint.strokeWidth = 4f.dp()
        gridLinePaint.color = gridColor
        val pointPath = Path()
        pointPath.addCircle(0f, 0f, GRID_LINE_WIDTH / 2, Path.Direction.CCW)
        gridLinePaint.pathEffect =
            PathDashPathEffect(pointPath, 2.5f.dp(), 0f, PathDashPathEffect.Style.ROTATE)
        gridLinePath = Path()

        limitGridLinePath = Path()

        scaleLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scaleLinePaint.color = gridColor
        scaleLinePaint.strokeWidth = 1f.dp()
        scaleLinePaint.style = Paint.Style.STROKE
        scaleLinePath = Path()

        leftBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        leftBarPaint.strokeWidth = LEFT_BAR_WIDTH
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        leftBarPaint.color = Color.parseColor("#4B5DCC")

        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.color = lineColor
        val pathEffect = CornerPathEffect(25f)
        linePaint.pathEffect = pathEffect
        linePaint.strokeWidth = 1.5f.dp()
        linePaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        onDrawGridLine(canvas)
        onDrawScaleLine(canvas)
        if (!mData.isNullOrEmpty() && mData!!.size > 2) {
            onDrawBgLine(canvas)
            onDrawFlagLine(canvas)
        }
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
        gridLinePaint.color = gridColor
        canvas.drawPath(gridLinePath, gridLinePaint)
    }

    private fun onDrawScaleLine(canvas: Canvas) {
        val offset = width / 2
        for (i in 0..2) {
            scaleLinePath.moveTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_WIDTH)
            scaleLinePath.lineTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_HEIGHT)
        }
        scaleLinePath.moveTo(0F, height - SCALE_LINE_WIDTH / 2)
        scaleLinePath.lineTo(width.toFloat(), height - SCALE_LINE_WIDTH / 2)
        canvas.drawPath(scaleLinePath, scaleLinePaint)
    }

    private fun onDrawBgLine(canvas: Canvas) {
        onDrawLine(canvas, lineBgColor, mData!!.mapIndexed { index, d -> index })
    }

    private fun onDrawFlagLine(canvas: Canvas) {
        if (flags.isNullOrEmpty()) {
            return
        }
        val flagLines = splitFlagData(flags!!)
        for (line in flagLines) {
            onDrawLine(canvas, lineColor, line)
        }
    }

    fun splitFlagData(
        flags: MutableList<Int>
    ): ArrayList<ArrayList<Int>> {
        var lineDataList = ArrayList<ArrayList<Int>>()
        var tempList = ArrayList<Int>()
        for (i in flags.indices) {
            if (flags[i] == 1) {
                if (i == 0 || flags[i - 1] == 0) {
                    tempList = ArrayList()
                    lineDataList.add(tempList)
                    tempList.add(i)
                } else {
                    tempList.add(i)
                }
            } else {
                continue
            }
        }
        return lineDataList
    }


    private fun onDrawLine(canvas: Canvas, lineColor: Int, indexs: List<Int>?) {
        if (indexs.isNullOrEmpty() || indexs.size < 2) {
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
        val linePath = Path()
        for (i in indexs.indices) {
            val curX = dataOffset * indexs[i] + LEFT_BAR_WIDTH
            val curY = (mData!![indexs[i]] - dataMin) * dataScale + gridLineYPadding
            if (i == 0) {
                linePath.moveTo(curX, curY.toFloat())
            } else {
                linePath.lineTo(curX, curY.toFloat())
            }
        }
        linePaint.color = lineColor
        canvas.drawPath(linePath, linePaint)
    }

    fun setData(data: MutableList<Double>, flags: MutableList<Int>) {
        this.flags = flags
        this.mData = data
        invalidate()
    }
}