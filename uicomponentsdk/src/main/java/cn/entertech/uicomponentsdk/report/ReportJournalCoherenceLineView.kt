package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.curveByQuality
import cn.entertech.uicomponentsdk.utils.dp
import cn.entertech.uicomponentsdk.utils.formatData
import cn.entertech.uicomponentsdk.utils.sampleData
import kotlin.math.ceil


class ReportJournalCoherenceLineView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attributeSet, defStyleAttr) {

    private var qualityBadColor: Int = Color.parseColor("#DDE1EB")
    private var qualityFlags: List<Double>? = null
    private var coherenceFlags: ArrayList<Int>? = null
    private var lineBgColor: Int = Color.parseColor("#ff0000")
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
        val SAMPLE_MIN_DATA_SIZE = 2000
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
        qualityBadColor = typeArray.getColor(R.styleable.ReportJournalCoherenceLineView_rgclv_qualityBadColor,qualityBadColor)

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
//        onDrawGridLine(canvas)
        onDrawGripBitmap(canvas)
        onDrawScaleLine(canvas)
        if (!mData.isNullOrEmpty() && mData!!.size > 2) {
            if (!drawByQuality){
                onDrawBgLine(canvas,lineBgColor)
                onDrawQualityAndCoherenceLine(canvas,lineColor)
            }else{
                onDrawBgLine(canvas,qualityBadColor)
                onDrawQualityLine(canvas,lineBgColor)
                onDrawQualityAndCoherenceLine(canvas,lineColor)
            }
        }
    }

    //    private fun scale(bitmap:Bitmap,rate:Float):Bitmap {
//        val matrix = Matrix();
//        matrix.postScale(rate,rate)
//        val bmpRet = Bitmap.createBitmap(bitmap.width * rate.toInt(), bitmap.height * rate.toInt(), Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bmpRet)
//        val paint = Paint()
//        canvas.drawBitmap(bitmap, matrix, paint)
//        return bmpRet
//    }
    fun onDrawGripBitmap(canvas: Canvas) {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.pic_chart_grid_bg)
        val widthRate = width * 1f / bitmap.width
        val heightRate = height * 1f / bitmap.height
        var horizontalRepeatCount = if (widthRate <= 1f) {
            1
        } else {
            ceil(widthRate).toInt()
        }
        var verticalRepeatCount = if (heightRate <= 1f) {
            1
        } else {
            ceil(heightRate).toInt()
        }
        canvas.save()
        for (i in 0 until verticalRepeatCount) {
            drawBitmapHorizontalRepeat(canvas, bitmap, horizontalRepeatCount)
            canvas.translate(0f, height.toFloat())
        }
        canvas.restore()
    }

    fun drawBitmapHorizontalRepeat(canvas: Canvas, bitmap: Bitmap, repeatCount: Int) {
        canvas.save()
        for (i in 0 until repeatCount) {
            val rectBitmap = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val filter: ColorFilter = PorterDuffColorFilter(
                gridColor,
                PorterDuff.Mode.SRC_IN
            )
            gridLinePaint.colorFilter = filter
            canvas.scale(2f, 2f)
            canvas.drawBitmap(bitmap, rectBitmap, rectF, gridLinePaint)
            canvas.translate(width.toFloat(), 0f)
        }
        canvas.restore()
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

    private fun onDrawBgLine(canvas: Canvas,color: Int) {
        onDrawLine(canvas, color, mData!!.mapIndexed { index, d -> index })
    }

    private fun onDrawQualityLine(canvas: Canvas,color: Int) {
        if (qualityFlags.isNullOrEmpty()) {
            return
        }
        val flagLines = splitQualityData(qualityFlags!!)
        for (line in flagLines) {
            onDrawLine(canvas, color, line)
        }
    }

    private fun onDrawQualityAndCoherenceLine(canvas: Canvas,color:Int) {
        if (coherenceFlags.isNullOrEmpty()) {
            return
        }
        val flagLines = splitQualityAndCoherenceData(qualityFlags!!,coherenceFlags!!)
        for (line in flagLines) {
            onDrawLine(canvas, color, line)
        }
    }


    fun splitQualityData(
        qualityRec: List<Double>
    ): ArrayList<ArrayList<Int>> {
        var lineDataList = ArrayList<ArrayList<Int>>()
        var tempList = ArrayList<Int>()
        for (i in qualityRec.indices) {
            if (qualityRec[i] == 1.0) {
                if (i == 0 || qualityRec[i - 1] == 0.0) {
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

    fun splitQualityAndCoherenceData(
        qualityRec: List<Double>,
        flags: MutableList<Int>
    ): ArrayList<ArrayList<Int>> {
        var lineDataList = ArrayList<ArrayList<Int>>()
        var tempList = ArrayList<Int>()
        for (i in flags.indices) {
            if (flags[i] == 1 && qualityRec[i] == 1.0) {
                if (i == 0 || flags[i - 1] == 0 || qualityRec[i-1] == 0.0) {
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
        var dataMax = mData!!.maxOrNull() ?: 100.0
        var dataMin = mData!!.minOrNull() ?: 0.0
        if (dataMax == dataMin) {
            dataMax = LINE_VALUE_MAX
            dataMin = LINE_VALUE_MIN
        }
        dataMax += (dataMax - dataMin) / 8.0
        dataMin -= (dataMax - dataMin) / 8.0
        if (dataMin < 0) {
            dataMin = 0.0
        }
        val dataScale = (height - 2 * gridLineYPadding) / (dataMax - dataMin)
        val dataOffset = (width - LEFT_BAR_WIDTH) / (mData!!.size - 1)
        val linePath = Path()
        for (i in indexs.indices) {
            if (indexs[i] >= mData!!.size || indexs[i] < 0) {
                continue
            }
            val curX = dataOffset * indexs[i] + LEFT_BAR_WIDTH
            val curY = (mData!![indexs[i]] - dataMin) * dataScale + gridLineYPadding
            if (i == 0) {
                linePath.moveTo(curX, -curY.toFloat())
            } else {
                linePath.lineTo(curX, -curY.toFloat())
            }
        }
        linePaint.color = lineColor

        canvas.save()
        canvas.translate(0f, height.toFloat())
        canvas.drawPath(linePath, linePaint)
        canvas.restore()
    }
    var drawByQuality = false
    fun setData(data: ArrayList<Double>, flags: List<Int>,qualityRec: List<Double>?) {
        val sample = if (data.size > SAMPLE_MIN_DATA_SIZE) {
            data.size / SAMPLE_MIN_DATA_SIZE
        } else {
            1
        }
        this.coherenceFlags = sampleData(flags.map { it.toDouble() }, sample).map { it.toInt() } as ArrayList
        this.mData = sampleData(formatData(data),sample)
        if (mData != null){
            this.qualityFlags = if (qualityRec == null){
                drawByQuality = false
                mData!!.map { 1.0 }
            }else{
                drawByQuality = true
                val qualitySampleRec = sampleData(qualityRec,sample)
                curveByQuality(qualitySampleRec)
            }
        }
        invalidate()
    }
}