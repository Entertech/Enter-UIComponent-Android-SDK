package cn.entertech.uicomponentsdk.report

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.dp

class ReportJournalFlowLineView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attributeSet, defStyleAttr) {

    private var limitAboveColor: Int = Color.parseColor("#CC9E59")
    private var limitBottomColor: Int = Color.parseColor("#4B5DCC")
    private var lineWidth: Float = LINE_WIDTH
    private var activeColor: Int = Color.parseColor("#FFC56F")
    private var neutralColor: Int = Color.parseColor("#99A7FF")
    private var flowColor: Int = Color.parseColor("#9661FF")
    private var gridColor: Int = Color.parseColor("#DDE1EB")
    private var lineColor: Int = Color.parseColor("#7684DA")
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
//    var curLineCurveIndex: Int = 0
//        set(value) {
//            field = value
//            invalidate()
//        }

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
            R.styleable.ReportJournalFlowLineView
        )
        gridColor =
            typeArray.getColor(R.styleable.ReportJournalFlowLineView_rflv_gridColor, gridColor)
        activeColor =
            typeArray.getColor(R.styleable.ReportJournalFlowLineView_rflv_activeColor, activeColor)
        neutralColor =
            typeArray.getColor(
                R.styleable.ReportJournalFlowLineView_rflv_neutralColor,
                neutralColor
            )
        flowColor =
            typeArray.getColor(R.styleable.ReportJournalFlowLineView_rflv_flowColor, flowColor)
        lineColor =
            typeArray.getColor(R.styleable.ReportJournalFlowLineView_rflv_lineColor, lineColor)
        limitAboveColor =
            typeArray.getColor(
                R.styleable.ReportJournalFlowLineView_rflv_limitAboveColor,
                limitAboveColor
            )
        limitBottomColor = typeArray.getColor(
            R.styleable.ReportJournalFlowLineView_rflv_limitBottomColor,
            limitBottomColor
        )
        lineWidth =
            typeArray.getDimension(R.styleable.ReportJournalFlowLineView_rflv_lineWidth, lineWidth)
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
        gridLineYPadding = 5f.dp()

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
        linePath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        onDrawGridLine(canvas)
        onDrawScaleLine(canvas)
        onDrawLeftYBar(canvas)
        onDrawLine(canvas)
    }
    var limitBottomY :Float = 0f
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
                        limitAboveColor
                    } else {
                        limitBottomY = gridLineYPadding + GRID_LINE_WIDTH / 2 + i * yOffset
                        limitBottomColor
                    }
                gridLinePaint.color = lineColor
                canvas.drawPath(limitGridLinePath, gridLinePaint)
            }
        }
    }

    private fun onDrawScaleLine(canvas: Canvas) {
        val offset = (width - 2 * SCALE_LINE_WIDTH) / 2
        for (i in 0..3) {
            scaleLinePath.moveTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_WIDTH)
            scaleLinePath.lineTo(SCALE_LINE_WIDTH / 2 + i * offset, height - SCALE_LINE_HEIGHT)
        }
        scaleLinePath.moveTo(0F, height - SCALE_LINE_WIDTH / 2)
        scaleLinePath.lineTo(width.toFloat(), height - SCALE_LINE_WIDTH / 2)
        canvas.drawPath(scaleLinePath, scaleLinePaint)
    }

    private fun onDrawLeftYBar(canvas: Canvas) {
        //top
        leftBarPaint.color = activeColor
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2,
            LEFT_BAR_WIDTH,
            LEFT_BAR_WIDTH / 2,
            height.toFloat() / 2,
            leftBarPaint
        )

        //bottom
        leftBarPaint.color = flowColor
        leftBarPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2,
            height.toFloat() / 2,
            LEFT_BAR_WIDTH / 2,
            height.toFloat() - LEFT_BAR_WIDTH / 2,
            leftBarPaint
        )

        //mid
        leftBarPaint.color = neutralColor
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
        val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), linePaint)
        val desBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val desCanvas = Canvas(desBitmap)
        val srcBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val srcCanvas = Canvas(srcBitmap)
        if (mData.isNullOrEmpty() || mData!!.size < 2) {
            return
        }
        var dataMax = mData!!.maxOrNull()
        var dataMin = mData!!.minOrNull()
        if (dataMax == dataMin) {
            dataMax = LINE_VALUE_MAX
            dataMin = LINE_VALUE_MIN
        }
        val dataScale = (height - 2 * gridLineYPadding) / (dataMax!! - dataMin!!)
        val dataOffset = (width - LEFT_BAR_WIDTH) / (mData!!.size - 1)
        for (i in mData!!.indices) {
            val curX = dataOffset * i + LEFT_BAR_WIDTH
            val curY = (mData!![i] - dataMin) * dataScale + gridLineYPadding
            if (i == 0) {
                linePath.moveTo(curX, curY.toFloat())
            } else {
                linePath.lineTo(curX, curY.toFloat())
            }
        }
        linePaint.color = lineColor
        desCanvas.drawPath(linePath, linePaint)
        canvas.drawBitmap(desBitmap,0f,0f,linePaint)
        linePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        linePaint.color = flowColor
        linePaint.style = Paint.Style.FILL
        canvas.drawRect(RectF(0f,limitBottomY,width.toFloat(),height.toFloat()),linePaint)
//        canvas.drawBitmap(srcBitmap,0f,0f,linePaint)
        linePaint.xfermode = null
        canvas.restoreToCount(sc)
    }

    fun setData(data: MutableList<Double>) {
        this.mData = data
    }

//    fun startLineDrawAnimation() {
//        if (mData != null) {
//            val objectAnimator = ObjectAnimator.ofInt(this, "curLineCurveIndex", 0, mData!!.size)
//            objectAnimator.duration = 1000
//            objectAnimator.interpolator = DecelerateInterpolator()
//            objectAnimator.start()
//        }
//    }
//
//    override fun onVisibilityChanged(changedView: View, visibility: Int) {
//        super.onVisibilityChanged(changedView, visibility)
////        startLineDrawAnimation()
//    }
}