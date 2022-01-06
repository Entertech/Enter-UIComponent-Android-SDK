package cn.entertech.uicomponentsdk.realtime;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

public class BreathCoherenceSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Context mContext;
    private float mLineWidth;
    private float mRightPadding;
    private float mLeftPadding;
    private int mGridLineCount = 4;
    private int mGridLineColor = Color.parseColor("#383838");
    private int mAxisColor = Color.parseColor("#383838");
    private int mLineColor = Color.parseColor("#ff6682");
    private int mBgColor = Color.parseColor("#f2f4fb");
    private Paint mCruvePaint;
    private List<Double> mSourceData = new ArrayList<>();
    List<Double> realData = new ArrayList<>();
    List<Double> sampleData = new ArrayList<>();
    List<Double> screenData = new ArrayList<>();
    private boolean isViewActivity;
    private SurfaceHolder mSurfaceHolder;
    public int screenDataSize = 100;
    public int mBuffer = 2;
    public int mListBuffer = 2;
    private Paint mAxisPaint;
    private Paint mGridLinePaint;
    private Paint mBgPaint;
    private boolean isShowSampleData = false;
    private int mMaxValue = -1;
    private int mMinValue = -1;
    private Paint mYAxisLabelPaint;
    private int mYAxisMargin;
    private int mRefreshTime = 200;
    private int offset = 10;
    private boolean mIsDrawXAxis = true;
    private int sample = 1;

    public BreathCoherenceSurfaceView(Context context) {
        this(context, null);
    }

    public BreathCoherenceSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreathCoherenceSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BreathCoherenceSurfaceView);
        if (typedArray != null) {
            mLineColor = typedArray.getColor(R.styleable.BreathCoherenceSurfaceView_hrvsf_lineColor, mLineColor);
            mBgColor = typedArray.getColor(R.styleable.BreathCoherenceSurfaceView_hrvsf_bgColor, mBgColor);
            mAxisColor = typedArray.getColor(R.styleable.BreathCoherenceSurfaceView_hrvsf_yAxisColor, mAxisColor);
            mGridLineColor = typedArray.getColor(R.styleable.BreathCoherenceSurfaceView_hrvsf_gridLineColor, mGridLineColor);
            mGridLineCount = typedArray.getInteger(R.styleable.BreathCoherenceSurfaceView_hrvsf_gridLineCount, mGridLineCount);
            mLeftPadding = typedArray.getDimension(R.styleable.BreathCoherenceSurfaceView_hrvsf_leftPadding, ScreenUtil.dip2px(context, 5));
            mRightPadding = typedArray.getDimension(R.styleable.BreathCoherenceSurfaceView_hrvsf_rightPadding, ScreenUtil.dip2px(context, 5));
            mLineWidth = typedArray.getDimension(R.styleable.BreathCoherenceSurfaceView_hrvsf_lineWidth, 3);
            mMaxValue = typedArray.getInteger(R.styleable.BreathCoherenceSurfaceView_hrvsf_maxValue, mMaxValue);
            mBuffer = typedArray.getInteger(R.styleable.BreathCoherenceSurfaceView_hrvsf_buffer, mBuffer);
            mRefreshTime = typedArray.getInteger(R.styleable.BreathCoherenceSurfaceView_hrvsf_refreshTime, mRefreshTime);
        }
        initPaint();
    }

    private void initPaint() {

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mCruvePaint = new Paint();
        mCruvePaint.setDither(true);
        mCruvePaint.setStyle(Paint.Style.STROKE);
        mCruvePaint.setAntiAlias(true);
        mCruvePaint.setStrokeWidth(mLineWidth);
        CornerPathEffect pathEffect = new CornerPathEffect(25);
        mCruvePaint.setPathEffect(pathEffect);
        mCruvePaint.setColor(mLineColor);
        mAxisPaint = new Paint();
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setStrokeWidth(1f);

        mGridLinePaint = new Paint();
        mGridLinePaint.setStyle(Paint.Style.STROKE);
        mGridLinePaint.setColor(mGridLineColor);
        mGridLinePaint.setStrokeWidth(3);
//        initData();
        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);

        mYAxisLabelPaint = new Paint();
        mYAxisLabelPaint.setColor(Color.parseColor("#9AA1A9"));
        mYAxisLabelPaint.setTextSize(ScreenUtil.dip2px(mContext, 12));
        mYAxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
//        this.setZOrderOnTop(true);
////        this.setZOrderMediaOverlay(true);
//        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public synchronized void setData(List<Double> data) {
        if (data == null) {
            return;
        }
        this.mSourceData.addAll(sampleData(data));
        if (mSourceData.size() > mBuffer) {
            for (int i = 0; i < mSourceData.size() - mBuffer; i++) {
                mSourceData.remove(0);
            }
        }
    }

    public List<Double> sampleData(List<Double> source){
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            if (i % sample == 0){
                result.add(source.get(i));
            }
        }
        return result;
    }


    public synchronized void setData(double data) {
        this.mSourceData.add(data);
        if (mSourceData.size() > mBuffer) {
            for (int i = 0; i < mSourceData.size() - mBuffer; i++) {
                mSourceData.remove(0);
            }
        }
    }


    private void initData() {
        for (int i = 0; i < screenDataSize; i++) {
            realData.add(0.0);
        }
    }

    public void setBuffer(int buffer) {
        this.mBuffer = buffer;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isViewActivity = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isViewActivity = false;
    }

    @Override
    public void run() {
        while (isViewActivity) {
            draw();
            try {
                Thread.sleep(mRefreshTime * sample);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


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

    Path path = new Path();

    public void onDrawHrv(Canvas canvas) {
        float pointOffset = getWidth() * 1f / screenDataSize;
        dealData();
        canvas.translate(mLeftPadding + mYAxisMargin, getHeight());
        path.reset();
//        Log.d("####", "draw data is " + drawData.toString());
        for (int i = 0; i < screenData.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(screenData.get(i))));
            path.lineTo(i * pointOffset, (float) (-(screenData.get(i))));
        }
        canvas.drawPath(path, mCruvePaint);

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
        canvas.drawPath(path, mCruvePaint);
    }

    private synchronized void draw() {
        Canvas mCanvas = null;
        try {
            mCanvas = mSurfaceHolder.lockCanvas(null);
//            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            onDrawBg(mCanvas);
            if (isShowSampleData) {
                onDrawSampleData(mCanvas);
            } else {
                onDrawHrv(mCanvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void dealData() {
        if (mSourceData.size() == 0) {
//            realData.add(0.0);
//            realData.remove(0);
        } else {
            realData.add((mSourceData.get(0)));
            mSourceData.remove(0);
            if (realData.size()> screenDataSize){
                realData.remove(0);
            }
        }
        List<Double> tempList = new ArrayList<>();
        int mMaxValue = Collections.max(realData).intValue() + 1;
        int mMinValue = Collections.min(realData).intValue() - 1;
        float times = (getHeight()) / ((mMaxValue - mMinValue) * 1.0f);
        screenData.clear();
        if (times != 0) {
            for (int i = 0; i < realData.size(); i++) {
                if (realData.get(i) != 0) {
                    screenData.add((realData.get(i) - mMinValue) * times);
                } else {
                    if (i - 1 >= 0) {
                        screenData.add((realData.get(i - 1) - mMinValue) * times);
                    }else{
                        screenData.add(0.0);
                    }
                }
            }
        } else {
            for (int i = 0; i < realData.size(); i++) {
                screenData.add(0.0);
            }
        }
    }

    public void setSampleData(List<Double> sampleData) {
        this.sampleData = sampleData;
        this.isShowSampleData = true;
        invalidate();
    }

    public void setLineColor(int color) {
        this.mLineColor = color;
        mCruvePaint.setColor(mLineColor);
        invalidate();
    }

    public void setLineWidth(float lineWidth) {
        this.mLineWidth = lineWidth;
        mCruvePaint.setStrokeWidth(mLineWidth);
        invalidate();
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

    public void setScreenDataSize(int size){
        this.screenDataSize = size;
        invalidate();
    }

    public void setSample(int sample){
        this.sample = sample;
    }

    public void hideSampleData() {
        this.isShowSampleData = false;
    }
}
