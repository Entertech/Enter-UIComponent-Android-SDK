package cn.entertech.uicomponentsdk.realtime;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

public class RealtimeAnimLineChartView extends View {
    private Context mContext;
    private float mLineWidth;
    private float mRightPadding;
    private float mLeftPadding;
    private int mGridLineCount = 4;
    private int mGridLineColor = Color.parseColor("#383838");
    private int mAxisColor = Color.parseColor("#383838");
    private String mLineColor = "#ff6682";
    private int mBgColor = Color.parseColor("#f2f4fb");
    private Paint mCurvePaint;
    ArrayList<Double> mSourceData = new ArrayList<>();
    ArrayList<Double> realData = new ArrayList<>();
    List<Double> sampleData = new ArrayList<>();
    ArrayList<Double> screenData = new ArrayList<>();

    List<ArrayList<Double>> mSourceDataList = new ArrayList<>();
    List<ArrayList<Double>> mRealDataList = new ArrayList<>();
    List<ArrayList<Double>> mScreenDataList = new ArrayList<>();
    List<Paint> mLinePaintList = new ArrayList<>();
    List<Path> mLinePathList = new ArrayList<>();
    public static int SCREEN_POINT_COUNT = 100;
    public int mBuffer = 2;
    private Paint mAxisPaint;
    private Paint mGridLinePaint;
    private Paint mBgPaint;
    private boolean isShowSampleData = false;
    private int mMaxValue = -1;
    private Paint mYAxisLabelPaint;
    private int mYAxisMargin;
    private int mRefreshTime = 600;
    private int mScreenPointCount = SCREEN_POINT_COUNT;
    private boolean mIsDrawXAxis = true;
    private int mWidth = 0;
    private Timer timer = new Timer();
    String[] lineColors;
    int lineCount;

    public RealtimeAnimLineChartView(Context context) {
        this(context, null);
    }

    public RealtimeAnimLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RealtimeAnimLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RealtimeAnimLineChartView);
        if (typedArray != null) {
            mBgColor = typedArray.getColor(R.styleable.RealtimeAnimLineChartView_ralcv_bgColor, mBgColor);
            mAxisColor = typedArray.getColor(R.styleable.RealtimeAnimLineChartView_ralcv_yAxisColor, mAxisColor);
            mGridLineColor = typedArray.getColor(R.styleable.RealtimeAnimLineChartView_ralcv_gridLineColor, mGridLineColor);
            mGridLineCount = typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_gridLineCount, mGridLineCount);
            mLeftPadding = typedArray.getDimension(R.styleable.RealtimeAnimLineChartView_ralcv_leftPadding, ScreenUtil.dip2px(context, 5));
            mRightPadding = typedArray.getDimension(R.styleable.RealtimeAnimLineChartView_ralcv_rightPadding, ScreenUtil.dip2px(context, 5));
            mLineWidth = typedArray.getDimension(R.styleable.RealtimeAnimLineChartView_ralcv_lineWidth, 3);
            mMaxValue = typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_maxValue, mMaxValue);
            mBuffer = typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_buffer, mBuffer);
            mRefreshTime = typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_refreshTime, mRefreshTime);
            mScreenPointCount = typedArray.getInteger(R.styleable.RealtimeAnimLineChartView_ralcv_screenPointCount, mScreenPointCount);
        }
        initPaint();
    }

    public void init() {
        initList();
    }

    private void initList() {
        lineColors = mLineColor.split(",");
        lineCount = lineColors.length;
        mSourceDataList.clear();
        mRealDataList.clear();
        mScreenDataList.clear();
        mLinePaintList.clear();
        mLinePathList.clear();
        for (int i = 0; i < lineCount; i++) {
            ArrayList<Double> mSourceData = new ArrayList<>();
            ArrayList<Double> realData = new ArrayList<>();
            ArrayList<Double> screenData = new ArrayList<>();
            mSourceDataList.add(mSourceData);
            mRealDataList.add(realData);
            mScreenDataList.add(screenData);
            Paint paint = createLinePaint();
            paint.setColor(Color.parseColor(lineColors[i]));
            mLinePaintList.add(paint);
            Path path = new Path();
            mLinePathList.add(path);
        }
    }

    private Paint createLinePaint() {
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mLineWidth);
        CornerPathEffect pathEffect = new CornerPathEffect(25);
        paint.setPathEffect(pathEffect);
        return paint;
    }

    private void initPaint() {
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        mAxisPaint = new Paint();
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setStrokeWidth(1f);

        mGridLinePaint = new Paint();
        mGridLinePaint.setStyle(Paint.Style.STROKE);
        mGridLinePaint.setColor(mGridLineColor);
        mGridLinePaint.setStrokeWidth(3);
//        initData();

        mYAxisLabelPaint = new Paint();
        mYAxisLabelPaint.setColor(Color.parseColor("#9AA1A9"));
        mYAxisLabelPaint.setTextSize(ScreenUtil.dip2px(mContext, 12));
        mYAxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public synchronized void setData(int lineIndex, List<Double> data) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            setData(lineIndex, data.get(i));
        }
    }


    public synchronized void setData(int lineIndex, double data) {
        List<Double> sourceData = mSourceDataList.get(lineIndex);
        sourceData.add(data);
        if (sourceData.size() > mBuffer) {
            for (int i = 0; i < sourceData.size() - mBuffer; i++) {
                sourceData.remove(0);
            }
        }
        boolean isDataNotInit = false;
        for (int i = 0; i < mSourceDataList.size(); i++) {
            if (mSourceDataList.get(i).size() == 0){
                isDataNotInit = true;
                break;
            }
        }
        if (!isDataNotInit && !isTimerStart) {
            startTimer();
        }
    }

    private boolean isTimerStart;

    private void startTimer() {
        isTimerStart = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                invalidate();
            }
        }, 0, mRefreshTime);
    }

    private void initData() {
        for (int i = 0; i < mScreenPointCount + 2; i++) {
            realData.add(0.0);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawBg(canvas);
        if (isShowSampleData) {
            onDrawSampleData(canvas);
        } else {
            if (isTimerStart) {
                onDrawHrv(canvas);
            }
        }
        onDrawRectCover(canvas);
        if (canPlayAnim()) {
            startAnim();
        }
    }

    public boolean canPlayAnim() {
        boolean isSourceDataAvailable = false;
        for (int i = 0; i < mSourceDataList.size(); i++) {
            if (mSourceDataList.get(i).size() != 0 && Collections.max(mSourceDataList.get(i)) != 0){
                isSourceDataAvailable = true;
                break;
            }
        }
        return !isAnim && isSourceDataAvailable && mScreenDataList.size() != 0  && mScreenDataList.get(0).size() > mScreenPointCount + 1;
    }

    private float axisOffset = 0f;

    public float getAxisOffset() {
        return axisOffset;
    }

    public void setAxisOffset(float axisOffset) {
        this.axisOffset = axisOffset;
    }

    private boolean isAnim = false;


    public void onDrawBg(Canvas canvas) {
        mYAxisMargin = ScreenUtil.dip2px(mContext, 0);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
//        canvas.drawColor(mBgColor,PorterDuff.Mode.CLEAR);
        float lineOffset = (getWidth() - (mLeftPadding + mRightPadding) - mYAxisMargin) / 4;
        if (mIsDrawXAxis) {
            canvas.drawLine(mLeftPadding + mYAxisMargin, getHeight() - 0.5f, getWidth() - mRightPadding, getHeight() - 0.5f, mAxisPaint);
        }
        for (int i = 0; i < (mGridLineCount + 1); i++) {
            if (i == 0) {
                canvas.drawLine(mLeftPadding + mYAxisMargin, 0, mLeftPadding + mYAxisMargin, getHeight(), mAxisPaint);
            } else {
                //绘制长度为4的实线后再绘制长度为4的空白区域，0位间隔
                mGridLinePaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
                canvas.drawLine(lineOffset * i, 0, lineOffset * i, getHeight(), mGridLinePaint);
            }
        }
//        canvas.drawText("0", mYAxisMargin, getHeight() - 10, mYAxisLabelPaint);
//        canvas.drawText("50", mYAxisMargin, 30, mYAxisLabelPaint);
    }

    public void onDrawRectCover(Canvas canvas) {
        canvas.drawRect(0, 0, mLeftPadding + mYAxisMargin, getHeight(), mBgPaint);
    }

    Path path = new Path();

    public void onDrawHrv(Canvas canvas) {
        canvas.save();
        if (!isAnim) {
            mScreenDataList.clear();
            for (int i = 0; i < mSourceDataList.size(); i++) {
                mScreenDataList.add(dealData(mSourceDataList.get(i), mRealDataList.get(i)));
            }
        }

        float pointOffset = getWidth() * 1f / mScreenPointCount;
        //获得canvas对象
        canvas.translate(mLeftPadding + mYAxisMargin - axisOffset, getHeight());
        for (int i = 0; i < mScreenDataList.size(); i++) {
            mLinePathList.get(i).reset();
//        Log.d("####", "draw data is " + drawData.toString());
            for (int j = 0; j < mScreenDataList.get(i).size(); j++) {
                if (j == 0)
                    mLinePathList.get(i).moveTo(j * pointOffset, (float) (-(mScreenDataList.get(i).get(j))));
                mLinePathList.get(i).lineTo(j * pointOffset, (float) (-(mScreenDataList.get(i).get(j))));
            }
            canvas.drawPath(mLinePathList.get(i), mLinePaintList.get(i));
        }
        canvas.restore();
    }

    public void onDrawSampleData(Canvas canvas) {
        float pointOffset = getWidth() * 1f / (sampleData.size() - 1);
        //获得canvas对象
        canvas.translate(mLeftPadding + mYAxisMargin, getHeight());
        float time = (getHeight() / mMaxValue * 1f);
        Path path = new Path();
        for (int i = 0; i < sampleData.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(sampleData.get(i) * time)));
            path.lineTo(i * pointOffset, (float) (-(sampleData.get(i) * time)));

        }
        canvas.drawPath(path, mCurvePaint);
    }

    public ArrayList<Double> dealData(List<Double> mSourceData, List<Double> realData) {
        if (mSourceData.size() == 0) {
//            realData.add(0.0);
//            realData.remove(0);
        } else {
            if (mSourceData.get(0) == 0) {
                mSourceData.remove(0);
            } else {
                realData.add((mSourceData.get(0)));
                mSourceData.remove(0);
                if (realData.size() > mScreenPointCount + 2) {
                    realData.remove(0);
                }
            }
        }
        ArrayList<Double> screenData = new ArrayList<>();
        if (realData.isEmpty()){
            return screenData;
        }
        int mMaxValue = Collections.max(realData).intValue() + 1;
        int mMinValue = Collections.min(realData).intValue() - 1;
        if (mMinValue < 0) {
            mMinValue = 0;
        }
        float times = (getHeight()) / (mMaxValue - mMinValue) * 1.0f;
        if (times != 0) {
            for (int i = 0; i < realData.size(); i++) {
                if (realData.get(i) != 0) {
                    screenData.add((realData.get(i) - mMinValue) * times);
                } else {
                    if (i - 1 >= 0) {
                        screenData.add((realData.get(i - 1) - mMinValue) * times);
                    } else {
                        screenData.add(0.0);
                    }
                }
            }
        } else {
            for (int i = 0; i < realData.size(); i++) {
                screenData.add(0.0);
            }
        }
        return screenData;
    }

    public void startAnim() {
        float pointWidthOffset = mWidth / mScreenPointCount;
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(this, "axisOffset", 0, pointWidthOffset);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                axisOffset = 0;
                isAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.setDuration(mRefreshTime);
        valueAnimator.start();
    }

    public void setSampleData(List<Double> sampleData) {
        this.sampleData = sampleData;
        this.isShowSampleData = true;
        invalidate();
    }

    //
    public void setLineColor(String color) {
        this.mLineColor = color;
        invalidate();
    }

    public void setLineWidth(float lineWidth) {
//        this.mLineWidth = lineWidth;
//        mCurvePaint.setStrokeWidth(mLineWidth);
//        invalidate();
    }

    public void setGridLineColor(int gridLineColor) {
        this.mGridLineColor = gridLineColor;
        mGridLinePaint.setColor(mGridLineColor);
        invalidate();
    }

    public void setAxisColor(int color) {
        this.mAxisColor = color;
        mAxisPaint.setColor(mAxisColor);
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.mMaxValue = maxValue;
        invalidate();
    }

    public void setRefreshTime(int refreshTime) {
        this.mRefreshTime = refreshTime;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        this.mBgColor = color;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public void isDrawXAxis(boolean flag) {
        this.mIsDrawXAxis = flag;
        invalidate();
    }

    public void setBuffer(int buffer) {
        this.mBuffer = buffer;
    }

    public void setScreenPointCount(int pointCount) {
        this.mScreenPointCount = pointCount;
        invalidate();
    }


    public void hideSampleData() {
        this.isShowSampleData = false;
    }
}
