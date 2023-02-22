package cn.entertech.uicomponentsdk.realtime

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import cn.entertech.uicomponentsdk.utils.dp
import java.util.*

class RealtimeAnimFlowChartView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    mContext, attrs, defStyleAttr
) {

    companion object {
        var SCREEN_POINT_COUNT = 100
        val LEFT_BAR_WIDTH by lazy { 4f.dp() }
    }
    private var leftBarPaint: Paint? = null
    private var lineColors: Array<String>? = null
    private var mLineWidth = 0f
    private var mRightPadding = 0f
    private var mLeftPadding = 0f
    private var mGridLineCount = 4
    private var mGridLineColor = Color.parseColor("#383838")
    private var mAxisColor = Color.parseColor("#383838")
    private var activeColor: Int = Color.parseColor("#FFC56F")
    private var neutralColor: Int = Color.parseColor("#99A7FF")
    private var flowColor: Int = Color.parseColor("#9661FF")
    private var mLineColor:String? = "#ff6682"
    private var mBgColor = Color.parseColor("#f2f4fb")
    private val mCurvePaint: Paint? = null
    var mSourceData = ArrayList<Double>()
    var realData = ArrayList<Double>()
    var sampleData: List<Double> = ArrayList()
    var screenData = ArrayList<Double>()
    var mSourceDataList: MutableList<ArrayList<Double>> = ArrayList()
    var mRealDataList: MutableList<ArrayList<Double>> = ArrayList()
    var mScreenDataList: MutableList<ArrayList<Double>> = ArrayList()
    var mScreenSampleDataList: List<List<Int>> = ArrayList()
    var mLinePaintList: MutableList<Paint>? = ArrayList()
    var mLinePathList: MutableList<Path> = ArrayList()
    var mBuffer = 2
    private var mAxisPaint: Paint? = null
    private var mGridLinePaint: Paint? = null
    private var mBgPaint: Paint? = null
    private var isShowSampleData = false
    private var mMaxValue = -1
    private var mYAxisLabelPaint: Paint? = null
    private var mYAxisMargin = 0
    private var mRefreshTime = 600
    private var mScreenPointCount = SCREEN_POINT_COUNT
    private var mIsDrawXAxis = true
    private var mWidth = 0
    private val timer = Timer()
    var lineCount = 0
    private var mValueLabelBgPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var rightOffset = 0
    var isDrawValueText = false
    private var showLineIndexs: List<Int>? = null
    private var isDestroy = false
    private var mPointBgPaint: Paint? = null
    private var mBgPointColor = Color.parseColor("#11152E")
    private var mTextRectBg = Color.parseColor("#ffffff")
    private var mOnDrawLastValueListener: OnDrawLastValueListener? = null
    private var mVerticalPadding = 1
    private var linePointRadius = 0
    fun init() {
        initList()
        rightOffset = ScreenUtil.dip2px(mContext, 7f)
        linePointRadius = ScreenUtil.dip2px(mContext, 7f)
    }

    private fun initList() {
        lineColors = mLineColor?.split(",")?.toTypedArray()
        lineCount = lineColors?.size ?: 0
        mSourceDataList.clear()
        mRealDataList.clear()
        mScreenDataList.clear()
        mLinePaintList!!.clear()
        mLinePathList.clear()
        for (i in 0 until lineCount) {
            val mSourceData = ArrayList<Double>()
            val realData = ArrayList<Double>()
            val screenData = ArrayList<Double>()
            mSourceDataList.add(mSourceData)
            mRealDataList.add(realData)
            mScreenDataList.add(screenData)
            val paint = createLinePaint()
            paint.color = Color.parseColor(lineColors!![i])
            mLinePaintList!!.add(paint)
            val path = Path()
            mLinePathList.add(path)
        }
    }

    private fun createLinePaint(): Paint {
        val paint = Paint()
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = mLineWidth
        val pathEffect = CornerPathEffect(25f)
        paint.pathEffect = pathEffect
        return paint
    }

    private fun initPaint() {
        isFocusable = true
        keepScreenOn = true
        isFocusableInTouchMode = true
        mBgPaint = Paint()
        mBgPaint!!.color = mBgColor
        mAxisPaint = Paint()
        mAxisPaint!!.style = Paint.Style.STROKE
        mAxisPaint!!.color = mAxisColor
        mAxisPaint!!.strokeWidth = 1f
        mGridLinePaint = Paint()
        mGridLinePaint!!.style = Paint.Style.STROKE
        mGridLinePaint!!.color = mGridLineColor
        mGridLinePaint!!.strokeWidth = 3f
        //        initData();
        mYAxisLabelPaint = Paint()
        mYAxisLabelPaint!!.color = Color.parseColor("#9AA1A9")
        mYAxisLabelPaint!!.textSize = ScreenUtil.dip2px(mContext, 12f).toFloat()
        mYAxisLabelPaint!!.textAlign = Paint.Align.RIGHT
        mValueLabelBgPaint = Paint()
        mValueLabelBgPaint!!.color = mTextRectBg
        mValueLabelBgPaint!!.alpha = (0.8 * 255).toInt()
        mValueLabelBgPaint!!.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = ScreenUtil.dip2px(mContext, 12f).toFloat()
        mPointBgPaint = Paint()
        mPointBgPaint!!.color = mBgPointColor
        mPointBgPaint!!.strokeWidth = ScreenUtil.dip2px(mContext, 2f).toFloat()
        mPointBgPaint!!.isAntiAlias = true
        mPointBgPaint!!.style = Paint.Style.STROKE
        mPointBgPaint!!.strokeCap = Paint.Cap.ROUND
        mPointBgPaint!!.alpha = (0.2f * 255).toInt()
        mPointBgPaint!!.pathEffect =
            DashPathEffect(floatArrayOf(0.02f, ScreenUtil.dip2px(mContext, 4f).toFloat()), 0f)

        leftBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        leftBarPaint!!.strokeWidth = LEFT_BAR_WIDTH
        leftBarPaint!!.style = Paint.Style.FILL
        leftBarPaint!!.strokeCap = Paint.Cap.ROUND
        leftBarPaint!!.color = Color.parseColor("#4B5DCC")
    }

    @Synchronized
    fun setData(lineIndex: Int, data: List<Double>?) {
        if (data == null) {
            return
        }
        for (i in data.indices) {
            setData(lineIndex, data[i])
        }
    }

    @Synchronized
    fun setData(lineIndex: Int, data: Double) {
        val sourceData: MutableList<Double> = mSourceDataList[lineIndex]
        sourceData.add(data)
        if (sourceData.size > mBuffer) {
            for (i in 0 until sourceData.size - mBuffer) {
                sourceData.removeAt(0)
            }
        }
        var isDataNotInit = false
        for (i in mSourceDataList.indices) {
            if (mSourceDataList[i].size == 0) {
                isDataNotInit = true
                break
            }
        }
        if (!isDataNotInit && !isTimerStart) {
            startTimer()
        }
    }

    private var isTimerStart = false
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    this.removeMessages(0)
                    if (!isDestroy) {
                        invalidate()
                        sendEmptyMessageDelayed(0, mRefreshTime.toLong())
                    }
                }
                1 -> {}
                else -> {}
            }
        }
    }

    private fun startTimer() {
        isTimerStart = true
        initRealData()
        handler.sendEmptyMessageDelayed(0, mRefreshTime.toLong())
    }

    private fun initData() {
        for (i in 0 until mScreenPointCount + 2) {
            realData.add(0.0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width
        //        initSampleData();
    }

    override fun onDraw(canvas: Canvas) {
        onDrawBg(canvas)
        onDrawBgBitmap(canvas)
        if (isShowSampleData) {
            onDrawSampleData(canvas)
        } else {
            if (isTimerStart) {
                onDrawHrv(canvas)
                onDrawLeftRectCover(canvas)
                onDrawRightRectCover(canvas)
                onDrawLastPoint(canvas)
            }
        }
        onDrawLeftYBar(canvas)
        if (canPlayAnim() && !isShowSampleData) {
            startAnim()
        }
    }

    private fun onDrawLeftYBar(canvas: Canvas) {
        //top
        leftBarPaint!!.color = activeColor
        leftBarPaint!!.strokeCap = Paint.Cap.ROUND
        var barHeight = height.toFloat() - linePointRadius
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            LEFT_BAR_WIDTH,
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            barHeight / 2,
            leftBarPaint!!
        )

        //bottom
        leftBarPaint!!.color = flowColor
        leftBarPaint!!.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            barHeight / 2,
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            barHeight - LEFT_BAR_WIDTH / 2,
            leftBarPaint!!
        )

        //mid
        leftBarPaint!!.color = neutralColor
        leftBarPaint!!.strokeCap = Paint.Cap.SQUARE
        canvas.drawLine(
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            barHeight / 3,
            LEFT_BAR_WIDTH / 2+mLeftPadding + mYAxisMargin,
            barHeight * 2f / 3f,
            leftBarPaint!!
        )
    }

    fun canPlayAnim(): Boolean {
        var isSourceDataAvailable = false
        for (i in mSourceDataList.indices) {
            if (mSourceDataList[i].size != 0 && Collections.max(
                    mSourceDataList[i]
                ) != 0.0
            ) {
                isSourceDataAvailable = true
                break
            }
        }
        return !isAnim && mScreenDataList.size != 0 && mScreenDataList[0].size > mScreenPointCount + 1
    }

    var axisOffset = 0f
    private var isAnim = false
    fun onDrawBg(canvas: Canvas) {
        mYAxisMargin = ScreenUtil.dip2px(mContext, 0f)
        canvas.drawRect(
            0f,
            0f,
            (width - rightOffset).toFloat(),
            (height - linePointRadius).toFloat(),
            mBgPaint!!
        )
        val currentX = ScreenUtil.dip2px(mContext, 2f).toFloat()
        var currentY = ScreenUtil.dip2px(mContext, 2f).toFloat()
        val gridLineCount = height * 1f / (7 + 4f)
        val limitAboveLineIndex = (gridLineCount/3f).toInt()
        val limitBottomLineIndex = (gridLineCount/3f*2).toInt()
        var lineIndex = 0
        while (currentY < height - linePointRadius) {
            lineIndex++
            if (lineIndex == limitAboveLineIndex){
                mPointBgPaint!!.color = Color.parseColor("#FFC56F")
            }else if (lineIndex == limitBottomLineIndex){
                mPointBgPaint!!.color = Color.parseColor("#8B7AF3")
            }else{
                mPointBgPaint!!.color = mGridLineColor
            }
            canvas.drawLine(currentX, currentY, width.toFloat(), currentY, mPointBgPaint!!)
            currentY = currentY + ScreenUtil.dip2px(mContext, 4f)
        }

    }

    fun onDrawBgBitmap(canvas: Canvas?) {
//        Rect src = new Rect(0, 0, mBgBitmap.getWidth(), mBgBitmap.getHeight());
//        Rect dst = new Rect(0, 0, (int) (getWidth() - rightOffset), getHeight());
//        canvas.drawBitmap(mBgBitmap, src, dst, mBgPaint);
    }

    fun onDrawRectCover(canvas: Canvas, rectF: RectF?) {
        canvas.drawRect(rectF!!, mBgPaint!!)
    }

    fun onDrawLeftRectCover(canvas: Canvas) {
        val rectF = RectF(0f, 0f, mLeftPadding + mYAxisMargin, height.toFloat())
        onDrawRectCover(canvas, rectF)
    }

    fun onDrawRightRectCover(canvas: Canvas) {
        val rectF = RectF((width - rightOffset).toFloat(), 0f, width.toFloat(), height.toFloat())
        onDrawRectCover(canvas, rectF)
    }

    var path = Path()
    fun onDrawHrv(canvas: Canvas) {
        canvas.save()
        if (!isAnim) {
//            if (!mRealDataList[0].isEmpty()) {
//                realDataMinValue = mRealDataList[0][0].toInt()
//                realDataMaxValue = mRealDataList[0][0].toInt()
//            }
//            for (i in mSourceDataList.indices) {
//                if (!mRealDataList[i].isEmpty()) {
//                    if (Collections.max(mRealDataList[i]).toInt() >= realDataMaxValue) {
//                        realDataMaxValue =
//                            Collections.max(mRealDataList[i]).toInt() + mVerticalPadding
//                    }
//                    if (Collections.min(mRealDataList[i]).toInt() <= realDataMinValue) {
//                        realDataMinValue =
//                            Collections.min(mRealDataList[i]).toInt() - mVerticalPadding
//                    }
//                    if (realDataMinValue < 0) {
//                        realDataMinValue = 0
//                    }
//                }
//            }
            mScreenDataList.clear()
            for (i in mSourceDataList.indices) {
                mScreenDataList.add(dealData(mSourceDataList[i], mRealDataList[i]))
            }
        }
        val pointOffset = (width - rightOffset) * 1f / mScreenPointCount
        canvas.translate(
            mLeftPadding + mYAxisMargin - axisOffset,
            0f
        )
        for (i in mScreenDataList.indices) {
            if (mOnDrawLastValueListener != null && mScreenDataList[i].size < mScreenPointCount) {
                mOnDrawLastValueListener!!.onLastValueDraw(
                    i,
                    mRealDataList[i][mRealDataList[i].size - 1].toInt()
                )
            }
            if (showLineIndexs != null && !showLineIndexs!!.contains(i)) {
                continue
            }
            mLinePathList[i].reset()
            //        Log.d("####", "draw data is " + drawData.toString());
            for (j in mScreenDataList[i].indices) {
                if (j == 0) mLinePathList[i].moveTo(
                    j * pointOffset,
                    (mScreenDataList[i][j]).toFloat()
                )
                mLinePathList[i].lineTo(j * pointOffset, (mScreenDataList[i][j]).toFloat())
            }
            canvas.drawPath(mLinePathList[i], mLinePaintList!![i])
        }
        canvas.restore()
    }

    fun onDrawLastPoint(canvas: Canvas) {
        canvas.save()
        canvas.translate(0f,0f)
        for (i in mScreenDataList.indices) {
            if (showLineIndexs != null && !showLineIndexs!!.contains(i)) {
                continue
            }
            val lastPointX = (width - rightOffset).toFloat()
            if (realtimeLastPointYMap.isEmpty()) {
                return
            }
            val lastPointY = -realtimeLastPointYMap[i]!!
            canvas.drawCircle(
                lastPointX,
                lastPointY,
                linePointRadius.toFloat(),
                mValueLabelBgPaint!!
            )
            mLinePaintList!![i].style = Paint.Style.FILL
            canvas.drawCircle(
                lastPointX,
                lastPointY,
                ScreenUtil.dip2px(mContext, 3f).toFloat(),
                mLinePaintList!![i]
            )
            mLinePaintList!![i].style = Paint.Style.STROKE
            if (isDrawValueText) {
                val rectWidth = ScreenUtil.dip2px(mContext, 40f)
                val rectHeight = ScreenUtil.dip2px(mContext, 18f)
                val rectTop = (lastPointY - rectHeight / 2f).toInt()
                val left = lastPointX.toInt() - ScreenUtil.dip2px(mContext, 7f) - ScreenUtil.dip2px(
                    mContext,
                    3f
                ) - rectWidth
                val valueTextRect = RectF(
                    left.toFloat(),
                    rectTop.toFloat(),
                    (left + rectWidth).toFloat(),
                    (rectTop + rectHeight).toFloat()
                )
                val rectRadius = ScreenUtil.dip2px(mContext, 4f).toFloat()
                canvas.drawRoundRect(valueTextRect, rectRadius, rectRadius, mValueLabelBgPaint!!)
                drawText(
                    canvas,
                    valueTextRect,
                    mRealDataList[i][mRealDataList[i].size - 1].toInt().toString() + "",
                    i
                )
                if (mOnDrawLastValueListener != null) {
                    mOnDrawLastValueListener!!.onLastValueDraw(
                        i,
                        mRealDataList[i][mRealDataList[i].size - 1].toInt()
                    )
                }
            }
        }
        canvas.restore()
    }

    fun drawText(canvas: Canvas, textBg: RectF, text: String?, lineIndex: Int) {
        if (lineColors != null) {
            mTextPaint!!.color = Color.parseColor(lineColors!![lineIndex])
            val fontMetrics = mTextPaint!!.fontMetrics
            val top = fontMetrics.top
            val bottom = fontMetrics.bottom
            val baseLineY = (textBg.centerY() - top / 2 - bottom / 2).toInt()
            canvas.drawText(text!!, textBg.centerX(), baseLineY.toFloat(), mTextPaint!!)
        }
    }

    fun onDrawSampleData(canvas: Canvas) {
        canvas.save()
        val pointOffset = (width - rightOffset) * 1f / mScreenPointCount
        canvas.translate(mLeftPadding + mYAxisMargin - axisOffset, height.toFloat())
        var time = 1f
        if (mMaxValue > 0) {
            time = height * 1f / mMaxValue
        }
        for (i in mScreenSampleDataList.indices) {
            if (showLineIndexs != null && !showLineIndexs!!.contains(i)) {
                continue
            }
            mLinePathList[i].reset()
            //        Log.d("####", "draw data is " + drawData.toString());
            for (j in mScreenSampleDataList[i].indices) {
                if (j == 0) mLinePathList[i].moveTo(
                    j * pointOffset,
                    (-mScreenSampleDataList[i][j] * time)
                )
                mLinePathList[i].lineTo(j * pointOffset, (-mScreenSampleDataList[i][j] * time))
            }
            canvas.drawPath(mLinePathList[i], mLinePaintList!![i])
        }
        canvas.restore()
    }

    var realDataMaxValue = 100
    var realDataMinValue = 0
    fun dealData(mSourceData: ArrayList<Double>, realData: MutableList<Double>): ArrayList<Double> {
        if (mSourceData.size == 0) {
//            realData.add(0.0);
//            realData.remove(0);
        } else {
            if (mSourceData[0] == 0.0) {
                if (realData.size == 0) {
                    realData.add(mSourceData[0])
                    mSourceData.removeAt(0)
                } else {
                    realData.add(realData[realData.size - 1])
                }
            } else {
                realData.add(mSourceData[0])
                mSourceData.removeAt(0)
            }
            if (realData.size > mScreenPointCount + 2) {
                realData.removeAt(0)
            }
        }
        val screenData = ArrayList<Double>()
        if (realData.isEmpty()) {
            return screenData
        }
        val times = (height - linePointRadius) / (realDataMaxValue - realDataMinValue) * 1.0f
        if (times != 0f) {
            for (i in realData.indices) {
                if (realData[i] != 0.0) {
                    screenData.add((realData[i] - realDataMinValue) * times)
                } else {
                    if (i - 1 >= 0) {
                        screenData.add((realData[i - 1] - realDataMinValue) * times)
                    } else {
                        screenData.add(0.0)
                    }
                }
            }
        } else {
            for (i in realData.indices) {
                screenData.add(0.0)
            }
        }
        return screenData
    }

    fun initRealData() {
        for (i in mRealDataList.indices) {
            for (j in 0 until mScreenPointCount) {
                mRealDataList[i].add(0.0)
            }
        }
    }

    var lastPointY = 0f
    private val realtimeLastPointYMap = HashMap<Int, Float>()
    private val animators: MutableList<Animator> = ArrayList()

    init {
        val typedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.RealtimeAnimLineChartView)
        if (typedArray != null) {
            mBgColor =
                typedArray.getColor(R.styleable.RealtimeAnimLineChartView_ralcv_bgColor, mBgColor)
            mAxisColor = typedArray.getColor(
                R.styleable.RealtimeAnimLineChartView_ralcv_yAxisColor,
                mAxisColor
            )
            mGridLineColor = typedArray.getColor(
                R.styleable.RealtimeAnimLineChartView_ralcv_gridLineColor,
                mGridLineColor
            )
            mGridLineCount = typedArray.getInteger(
                R.styleable.RealtimeAnimLineChartView_ralcv_gridLineCount,
                mGridLineCount
            )
            mLeftPadding = typedArray.getDimension(
                R.styleable.RealtimeAnimLineChartView_ralcv_leftPadding, ScreenUtil.dip2px(
                    mContext, 5f
                ).toFloat()
            )
            mRightPadding = typedArray.getDimension(
                R.styleable.RealtimeAnimLineChartView_ralcv_rightPadding, ScreenUtil.dip2px(
                    mContext, 5f
                ).toFloat()
            )
            mLineWidth =
                typedArray.getDimension(R.styleable.RealtimeAnimLineChartView_ralcv_lineWidth, 3f)
            mMaxValue = typedArray.getInteger(
                R.styleable.RealtimeAnimLineChartView_ralcv_maxValue,
                mMaxValue
            )
            mBuffer =
                typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_buffer, mBuffer)
            mRefreshTime = typedArray.getInteger(
                R.styleable.RealtimeAnimLineChartView_ralcv_refreshTime,
                mRefreshTime
            )
            mScreenPointCount = typedArray.getInteger(
                R.styleable.RealtimeAnimLineChartView_ralcv_screenPointCount,
                mScreenPointCount
            )
        }
        initPaint()
    }

    fun startAnim() {
        val pointWidthOffset = (mWidth / mScreenPointCount).toFloat()
        val valueAnimator = ObjectAnimator.ofFloat(this, "axisOffset", 0f, pointWidthOffset)
        valueAnimator.addUpdateListener { invalidate() }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isAnim = true
            }

            override fun onAnimationEnd(animation: Animator) {
                axisOffset = 0f
                isAnim = false
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animators.clear()
        animators.add(valueAnimator)
        var lastPointIndex = 0
        if (mScreenDataList[0].size >= mScreenPointCount) {
            for (i in mScreenDataList.indices) {
                lastPointIndex = mScreenDataList[i].size - 2
                val lastPointYAnimator = ObjectAnimator.ofFloat(
                    this,
                    "lastPointY",
                    -mScreenDataList[i][lastPointIndex].toFloat(),
                    -mScreenDataList[i][lastPointIndex + 1].toFloat()
                )
                lastPointYAnimator.addUpdateListener { animation ->
                    val lastPointY = animation.animatedValue as Float
                    realtimeLastPointYMap[i] = lastPointY
                }
                animators.add(lastPointYAnimator)
            }
        }
        val animationSet = AnimatorSet()
        animationSet.playTogether(animators)
        animationSet.duration = mRefreshTime.toLong()
        animationSet.interpolator = LinearInterpolator()
        animationSet.start()
    }

    fun showSampleData(sampleData: List<List<Int>>) {
        mScreenSampleDataList = sampleData
        isShowSampleData = true
        invalidate()
    }

    //    public void initSampleData() {
    //        mScreenSampleDataList.clear();
    //        for (int i = 0; i < lineCount; i++) {
    //            ArrayList<Double> data = new ArrayList<Double>();
    //            for (int j = 0; j < mScreenPointCount; j++) {
    //                data.add(new Random().nextDouble() * getHeight() - 10);
    //            }
    //            mScreenSampleDataList.add(data);
    //        }
    //    }
    //
    fun setLineColor(color: String?) {
        mLineColor = color
        invalidate()
    }

    fun setLineWidth(lineWidth: Float) {
        mLineWidth = lineWidth
        if (mLinePaintList != null) {
            for (i in mLinePaintList!!.indices) {
                mLinePaintList!![i].strokeWidth = mLineWidth
            }
        }
        invalidate()
    }

    fun setGridLineColor(gridLineColor: Int) {
        mGridLineColor = gridLineColor
        mGridLinePaint!!.color = mGridLineColor
        invalidate()
    }

    fun setAxisColor(color: Int) {
        mAxisColor = color
        mAxisPaint!!.color = mAxisColor
        invalidate()
    }

    fun setMaxValue(maxValue: Int) {
        mMaxValue = maxValue
        invalidate()
    }

    fun setRefreshTime(refreshTime: Int) {
        mRefreshTime = refreshTime
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        mBgColor = color
        mBgPaint!!.color = mBgColor
        invalidate()
    }

    fun isDrawXAxis(flag: Boolean) {
        mIsDrawXAxis = flag
        invalidate()
    }

    fun setBuffer(buffer: Int) {
        mBuffer = buffer
    }

    fun setScreenPointCount(pointCount: Int) {
        mScreenPointCount = pointCount
        invalidate()
    }

    fun hideSampleData() {
        isShowSampleData = false
    }

    fun setLineShowIndexs(indexs: List<Int>?) {
        showLineIndexs = indexs
    }

    interface OnDrawLastValueListener {
        fun onLastValueDraw(index: Int, value: Int)
    }

    fun setOnDrawLastValueListener(lastValueListener: OnDrawLastValueListener?) {
        mOnDrawLastValueListener = lastValueListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDestroy = false
        handler.removeCallbacksAndMessages(null)
    }

    fun setBgPointColor(color: Int) {
        mBgPointColor = color
        mPointBgPaint!!.color = mBgPointColor
        mPointBgPaint!!.alpha = (0.2f * 255).toInt()
        invalidate()
    }

    fun setTextRectBgColor(color: Int) {
        mTextRectBg = color
        mValueLabelBgPaint!!.color = mTextRectBg
        mValueLabelBgPaint!!.alpha = (0.8 * 255).toInt()
        invalidate()
    }

    fun setVerticalPadding(padding: Int) {
        mVerticalPadding = padding
        invalidate()
    }

    fun setActiveColor(color:Int){
        this.activeColor = color
        invalidate()
    }

    fun setNeutralColor(color:Int){
        this.neutralColor = color
        invalidate()
    }

    fun setFlowColor(color:Int){
        this.flowColor = color
        invalidate()
    }

}